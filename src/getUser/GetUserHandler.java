package getUser;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import models.ImageFormat;
import models.User;

import java.util.ArrayList;
import java.util.Arrays;

public class GetUserHandler implements RequestHandler<GetUserRequest, GetUserResponse> {

    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_EAST_1)
            .build();
    DynamoDB dynamoDB = new DynamoDB(client);

    public GetUserResponse handleRequest(GetUserRequest request, Context context) {

        GetUserResponse response = new GetUserResponse();
        User responseUser;
        try {
            responseUser = getUser(request.username);
            if (request.signedInUser != null) {
                response.followed = userFollowed(request.username, request.signedInUser);
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        finally {
        }
        response.user = responseUser;
        response.success = true;
        response.message = "Success! Returning user.";
        return response;
    }
    public boolean userFollowed(String username, String follower) {
//        Table followsTable = dynamoDB.getTable("UserFollows");
//        Item followData = followsTable.getItem("user", username, "follower", follower);
//        if (followData == null) {
//            return false;
//        }
        return true;
    }

    public User getUser(String username) {

        Table userTable = dynamoDB.getTable("Users");
        Item userData = userTable.getItem("Username", username);
        String image = ImageFormat.keyToBase64(username);
        User u = new User(userData.getString("firstName"), userData.getString("lastName"), userData.getString("Username"),
                userData.getString("password"));
        u.profilePic = image;
        return u;

    }
}
