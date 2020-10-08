package getFollowers;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import models.ImageFormat;
import models.Status;
import models.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class GetFollowersHandler {
    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_EAST_1)
            .build();
    DynamoDB dynamoDB = new DynamoDB(client);

    public GetFollowersResponse handleRequest(GetFollowersRequest request, Context context) {
        GetFollowersResponse response = new GetFollowersResponse();
        response.followers = getFollowing(getFollowIds(request.username));
        response.success = true;
        response.message = "Success! Returning followers.";
        return response;
    }
    public ArrayList<String> getFollowIds(String username) {
        ArrayList<String> ids = new ArrayList<>();
        Table followTable = dynamoDB.getTable("UserFollows");
        ItemCollection<QueryOutcome> items = followTable.query("user", username);
        Iterator<Item> iter = items.iterator();
        int followCount = 30;
        while (iter.hasNext() && followCount > 0) {
            Item i = iter.next();
            System.out.println(i.toJSONPretty());
            --followCount;
            ids.add(i.getString("follower"));
        }
        return ids;
    }
    public ArrayList<User> getFollowing(ArrayList<String> ids) {
        ArrayList<User> users = new ArrayList<>();
        Table userTable = dynamoDB.getTable("Users");
        for (String id : ids) {
            Item item = userTable.getItem("Username", id);
            String image = ImageFormat.keyToBase64(id);
            User u = new User(item.getString("firstName"), item.getString("lastName"), id);
            u.profilePic = image;
            users.add(u);
        }
        return users;
    }
}
