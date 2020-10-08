package postStatus;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import models.ImageFormat;
import models.User;

import java.math.BigInteger;

public class Dao {
    PostStatusQueueDAO dao = new PostStatusQueueDAO();
    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_EAST_1)
            .build();
    DynamoDB dynamoDB = new DynamoDB(client);
    public boolean validToken(String token, String username) {
        Table authTable = dynamoDB.getTable("Authentication");
        Item authItem = authTable.getItem("authToken", token, "username", username);
        if (authItem == null) {
            return false;
        }
        return true;
    }
    public User getUser(String username) {
        Table followsTable = dynamoDB.getTable("Users");
        Item userData = followsTable.getItem("Username", username);
        if (userData == null) return null;
        User u = new User(userData.getString("firstName"), userData.getString("lastName"), userData.getString("Username"),
                userData.getString("password"));
        u.profilePic = ImageFormat.keyToBase64(userData.getString("Username"));
        return u;
    }

    public void createPost(String message, User user, BigInteger timestamp, String textAttachment, String imageAttachment) {
        dao.createPost(message, user, timestamp, textAttachment, imageAttachment);
    }
}
