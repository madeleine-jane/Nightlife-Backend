package getFeed;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.gson.Gson;
import getStory.GetStoryHandler;
import models.ImageFormat;
import models.Status;
import models.User;

import java.awt.*;
import java.math.BigInteger;
import java.time.Instant;
import java.util.*;
import java.util.List;

public class GetFeedHandler {
    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_EAST_1)
            .build();
    DynamoDB dynamoDB = new DynamoDB(client);

    public GetFeedResponse handleRequest(GetFeedRequest request, Context context) {
        GetFeedResponse response = new GetFeedResponse();
        if (!validToken(request.authToken, request.username)) {
            response.feed = null;
            response.success = false;
            response.message = "Invalid auth token";
            return response;
        }
        response.feed = getFeed(request.username);
        response.success = true;
        response.message = "Success! Returning feed.";
        return response;
    }
    public boolean validToken(String token, String username) {
        Table authTable = dynamoDB.getTable("Authentication");
        Item authItem = authTable.getItem("authToken", token, "username", username);
        if (authItem == null) {
            return false;
        }
//        if (java.math.BigInteger.valueOf(Instant.now().toEpochMilli()).compareTo(authItem.getBigInteger("timestamp").add(new BigInteger("1814400000"))) > 0) {
//            authTable.deleteItem("authToken", token, "username", username);
//            return false;
//        }
        return true;
    }
    public ArrayList<Status> getFeed(String username) {
        ArrayList<Status> statuses = new ArrayList<>();
        Table feedTable = dynamoDB.getTable("Feed");
        ItemCollection items = feedTable.query("ownerID", username);
        Iterator<Item> iter = items.iterator();
        while (iter.hasNext()) {
            Item item = iter.next();
            String image = ImageFormat.keyToBase64(item.getString("authorID"));
            User u = new User(item.getString("authorFirstName"),
                    item.getString("authorLastName"),
                    item.getString("authorID"));
            u.profilePic = image;
            Status s = new Status(item.getString("message"), u, item.getBigInteger("timestamp"));
            String imageAttachment = ImageFormat.keyToBase64(u.username + "-" + s.timestamp + "-image");
            item.getString("textAttachment");
            String textAttachment = item.getString("textAttachment");
            if (textAttachment == null || textAttachment.equals("null")) {
                textAttachment = "";
            }
            s.imageAttachment = imageAttachment;
            s.textAttachment = textAttachment;
            statuses.add(0, s);
        }
        Collections.reverse(statuses);
        return statuses;
    }
}
