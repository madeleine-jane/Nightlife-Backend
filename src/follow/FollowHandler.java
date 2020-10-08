package follow;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;

import java.math.BigInteger;
import java.time.Instant;

public class FollowHandler {
    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_EAST_1)
            .build();
    DynamoDB dynamoDB = new DynamoDB(client);

    public FollowResponse handleRequest(FollowRequest request, Context context) {
        FollowResponse response = new FollowResponse();
        if (validToken(request.authToken, request.follower)) {
            if (follow(request.username, request.follower)) {
                response.success = true;
                response.message = "Success! Followed user.";
                return response;
            }
            response.message = "Something went wrong";
            response.success = false;
            return response;
        }
        response.message = "invalid authtoken";
        response.success = false;
        return response;
    }

    public boolean validToken(String token, String username) {
//        Table authTable = dynamoDB.getTable("Authentication");
//        Item authItem = authTable.getItem("authToken", token, "username", username);
//        if (authItem == null) {
//            return false;
//        }
//        if (java.math.BigInteger.valueOf(Instant.now().toEpochMilli()).compareTo(authItem.getBigInteger("timestamp").add(new BigInteger("1814400000"))) > 0) {
//            authTable.deleteItem("authToken", token, "username", username);
//            return false;
//        }
        return true;
    }
    public boolean follow(String userID, String followerID) {
        Table followTable = dynamoDB.getTable("UserFollows");
        Item followItem = new Item()
                .withPrimaryKey("user", userID, "follower", followerID)
                .withLong("timestamp", Instant.now().toEpochMilli());
        followTable.putItem(followItem);
        return true;
    }
}
