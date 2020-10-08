package getStory;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.gson.Gson;
import models.ImageFormat;
import models.Status;
import models.User;
import org.apache.http.HttpEntity;

import java.math.BigInteger;
import java.util.*;

public class GetStoryHandler {
    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_EAST_1)
            .build();
    DynamoDB dynamoDB = new DynamoDB(client);
    public GetStoryResponse handleRequest(GetStoryRequest request, Context context) {

        GetStoryResponse response = new GetStoryResponse();
        ArrayList<Status> story = new ArrayList<>();
        try {
            story = getStory(request.username);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        response.story = story;
        response.success = true;
        response.message = "Success! Returning story.";

        return response;
    }

    public ArrayList<Status> getStory(String username) {
        ArrayList<Status> statuses = new ArrayList<>();
        Table statusTable = dynamoDB.getTable("Story");
        ItemCollection i = statusTable.query("authorID", username);
        Iterator<Item> iterator = i.iterator();
        Item item = null;
        while (iterator.hasNext()) {
            item = iterator.next();
            String image = ImageFormat.keyToBase64(item.getString("authorID"));
            User u = new User(item.getString("authorFirstName"),
                    item.getString("authorLastName"),
                    item.getString("authorID"));
            u.profilePic = image;
            Status s = new Status(item.getString("message"), u, item.getBigInteger("timestamp"));
            System.out.println(u.username);
            System.out.println(s.timestamp);
            String imageAttachment = ImageFormat.keyToBase64(u.username + "-" + s.timestamp + "-image");
            System.out.println(imageAttachment);
            String textAttachment = item.getString("textAttachment");
            if (textAttachment == null  || textAttachment.equals("null")) {
                textAttachment = "";
            }
            s.imageAttachment = imageAttachment;
            s.textAttachment = textAttachment;
            statuses.add(s);
        }
        Collections.reverse(statuses);
        return statuses;
    }

}
