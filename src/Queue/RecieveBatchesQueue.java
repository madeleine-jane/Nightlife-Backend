package Queue;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.model.StreamStatus;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

//recieve batches
public class RecieveBatchesQueue implements RequestHandler<SQSEvent, Void> {
    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_EAST_1)
            .build();
    DynamoDB dynamoDB = new DynamoDB(client);
    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        Gson gson = new Gson();
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            RecieveBatchRequest body = gson.fromJson(msg.getBody(), RecieveBatchRequest.class);
            System.out.println("Recieved batch size: " + body.followers.size());
//            System.out.println("Names: ");
//            for (String i : body.followers) {
//                System.out.println(i);
//            }
            int batchCounter = 0;
            ArrayList<String> batch = new ArrayList<>();
            for (String i : body.followers) {
                if (batchCounter < 25) {
                    batch.add(i);
                    ++batchCounter;
                }
                else {
                    batchCounter = 0;
                    writeBatch(batch, body);
                    batch = new ArrayList<>();
                }
            }
            writeBatch(batch, body);
        }

            return null;
    }

    public void writeBatch(ArrayList<String> batch, RecieveBatchRequest body) {
        System.out.println("Writing batch size " + batch.size());
        List<Item> items = postsToItem(batch, body);
        TableWriteItems tableWriteItems = new TableWriteItems("Feed")
                .withItemsToPut(items);
        BatchWriteItemOutcome outcome = dynamoDB.batchWriteItem(tableWriteItems);
        // Check for unprocessed keys which could happen if you exceed
        // provisioned throughput
        while (outcome.getUnprocessedItems().size() > 0) {
            outcome = dynamoDB.batchWriteItemUnprocessed(outcome.getUnprocessedItems());
        }
    }

    public List<Item> postsToItem(ArrayList<String> batch, RecieveBatchRequest body) {
        ArrayList<Item> items = new ArrayList<>();
        for (String id : batch) {
            Item statusItem = new Item()
                    .withPrimaryKey("ownerID", id,
                            "timestamp", body.timestamp)
                    .withString("authorID", body.user.username)
                    .withString("authorFirstName", body.user.firstName)
                    .withString("authorLastName", body.user.lastName)
                    .withString("message", body.message)
                    .withString("textAttachment", body.textAttachment);
            items.add(statusItem);
        }
        return items;
    }
}
