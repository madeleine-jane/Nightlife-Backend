package unfollow;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import models.Status;
import models.User;

import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;

public class UnfollowHandler {
    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_EAST_1)
            .build();
    DynamoDB dynamoDB = new DynamoDB(client);

    public UnfollowResponse handleRequest(UnfollowRequest request, Context context) {
        UnfollowResponse response = new UnfollowResponse();
        if (validToken(request.authToken, request.follower)) {
            unfollow(request.username, request.follower);
            removeFeedPosts(request.username, request.follower);
            response.success = true;
            response.message = "Success! Unfollowed.";
        }
        else {
            response.success = false;
            response.message = "invalid authtoken";
            return response;
        }
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
    public boolean unfollow(String username, String follower) {
        Table followTable = dynamoDB.getTable("UserFollows");
        followTable.deleteItem("user", username, "follower", follower);
        return true;
    }
    public boolean removeFeedPosts(String username, String follower) {
        Table feedTable = dynamoDB.getTable("Feed");
        ItemCollection feedPosts = feedTable.query("ownerID", follower);
        Iterator<Item> iterator = feedPosts.iterator();
        ArrayList<BigInteger> removedPostsTimestamps = new ArrayList<>();
        Item item = null;
        while (iterator.hasNext()) {
            item = iterator.next();
            System.out.println(item.toJSONPretty());
            if (item.getString("authorID").equals(username)) {
                removedPostsTimestamps.add(item.getBigInteger("timestamp"));
            }
        }
        for (BigInteger t : removedPostsTimestamps) {
            feedTable.deleteItem("ownerID", follower, "timestamp", t);
        }
        return true;
    }
}
