package Queue;


import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.Gson;
import models.User;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecievePostsQueue implements RequestHandler<SQSEvent, Void> {
    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_EAST_1)
            .build();
    DynamoDB dynamoDB = new DynamoDB(client);

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        Gson gson = new Gson();
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            RecievePostRequest body = gson.fromJson(msg.getBody(), RecievePostRequest.class);
            addToStory(body.message, body.user, body.timestamp, body.textAttachment);
            uploadAttachments(body.imageAttachment, body.user.username, body.timestamp);
            addTags(body.message, body.user, body.timestamp, body.textAttachment);
            ArrayList<String> followers = getFollowerIds(body.user.username);
            ArrayList<String> testFollowers = new ArrayList<>();
            System.out.println("Test followers: ");
            for (int i = 0; i < followers.size(); ++i) {
                //System.out.println(followers.get(i));
                testFollowers.add(followers.get(i));
            }
            ArrayList<ArrayList<String>> followerBatches = divideInBatches(testFollowers);
            System.out.println("Batches: ");
            System.out.println(followerBatches.size());
            System.out.println(followerBatches.get(0).size());
            boolean firstQueue = true;
            for (ArrayList<String> i : followerBatches) {
                if (firstQueue) {
                    firstQueue = false;
                    sendBatch(i, body);
                }
                else {
                   firstQueue = true;
                   sendBatchSecondQueue(i, body);
                }
            }
        }

        return null;
    }

    public boolean addToStory(String message, User user, BigInteger timestamp, String textAttachment) {
        Table storyTable = dynamoDB.getTable("Story");
        Item statusItem = new Item()
                .withPrimaryKey("authorID", user.username, "timestamp", timestamp)
                .withString("authorFirstName", user.firstName)
                .withString("authorLastName", user.lastName)
                .withString("message", message)
                .withString("textAttachment", textAttachment);

        storyTable.putItem(statusItem);
        return true;
    }

    public boolean addTags(String message, User user, BigInteger timestamp, String textAttachment) {
        Table mentionsTable = dynamoDB.getTable("Mentions");
        ArrayList<String> tags = new ArrayList<>();
        String[] words = message.split(" ");
        for (String word : words) {
            if (new Character(word.charAt(0)).equals('#')) {
                tags.add(word);
            }
        }
        for (String tag : tags) {
            Item tagItem = new Item().withPrimaryKey("tag", tag, "statusID", timestamp)
                    .withString("authorFirstName", user.firstName)
                    .withString("authorLastName", user.lastName)
                    .withString("authorID", user.username)
                    .withString("message", message)
                    .withString("textAttachment", textAttachment);
            mentionsTable.putItem(tagItem);
        }
        return true;
    }

    public void uploadAttachments(String imageAttachment, String username, BigInteger timestamp) {
        if (imageAttachment != null && !imageAttachment.equals("")) {
            uploadToS3(imageAttachment, username + "-" + timestamp + "-image", true);
        }
    }

    public boolean uploadToS3(String base64, String name, boolean isImage) {
        byte[] bI = org.apache.commons.codec.binary.Base64.decodeBase64((base64.substring(base64.indexOf(",")+1)).getBytes());
        InputStream fis = new ByteArrayInputStream(bI);
        AmazonS3 s3 = new AmazonS3Client();
        s3.setRegion(Region.getRegion(Regions.US_EAST_1));
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bI.length);
        if (isImage) {
            metadata.setContentType("image/png");
        }
        else {
            metadata.setContentType("text/plain");
        }
        metadata.setCacheControl("public, max-age=31536000");
        s3.putObject("nightlife-images", name, fis, metadata);
        return true;
        //int claire = 56;
    }

    public void sendBatch(ArrayList<String> batch, RecievePostRequest msg) {
        String queueUrl = "https://sqs.us-east-1.amazonaws.com/021359287760/post_batch";
        Gson gson = new Gson();
        RecieveBatchRequest msgBody = new RecieveBatchRequest();
        msgBody.message = msg.message;
        msgBody.textAttachment = msg.textAttachment;
        msgBody.timestamp = msg.timestamp;
        msgBody.user = msg.user;
        msgBody.followers = batch;
        System.out.println("msg Batch: " + msgBody.followers.size());
//        for (String i : batch) {
//            System.out.println(i);
//        }
        SendMessageRequest sendMsgRequest = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(gson.toJson(msgBody))
                .withDelaySeconds(5);

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        SendMessageResult result = sqs.sendMessage(sendMsgRequest);
    }

    public void sendBatchSecondQueue(ArrayList<String> batch, RecievePostRequest msg) {
        String queueUrl = "https://sqs.us-east-1.amazonaws.com/021359287760/post_batch_2";
        Gson gson = new Gson();
        RecieveBatchRequest msgBody = new RecieveBatchRequest();
        msgBody.message = msg.message;
        msgBody.textAttachment = msg.textAttachment;
        msgBody.timestamp = msg.timestamp;
        msgBody.user = msg.user;
        msgBody.followers = batch;
        System.out.println("msg Batch: " + msgBody.followers.size());
        SendMessageRequest sendMsgRequest = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(gson.toJson(msgBody))
                .withDelaySeconds(5);

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        SendMessageResult result = sqs.sendMessage(sendMsgRequest);
    }

    public ArrayList<ArrayList<String>> divideInBatches(ArrayList<String> followers) {
        ArrayList<ArrayList<String>> followerBatches = new ArrayList<>();
        System.out.println("Num followers: " + followers.size());
        int batchSize = 0;
        ArrayList<String> batch = new ArrayList<>();
        for (String i : followers) {
            if (batchSize < 400) {
                batch.add(i);
                ++batchSize;
            }
            else {
                followerBatches.add(new ArrayList<>(batch));
                System.out.println("Batch size: " + batch.size());
                batch = new ArrayList<>();
                batchSize = 0;
            }
        }
        followerBatches.add(batch);
        System.out.println("Num batches: " + followerBatches.size());
        return followerBatches;
    }

    public ArrayList<String> getFollowerIds(String username) {
        ArrayList<String> ids = new ArrayList<>();
        Table followTable = dynamoDB.getTable("UserFollows");
        ItemCollection<QueryOutcome> items = followTable.query("user", username);
        Iterator<Item> iter = items.iterator();
        while (iter.hasNext()) {
            Item i = iter.next();
            ids.add(i.getString("follower"));
        }
        return ids;
    }
}
