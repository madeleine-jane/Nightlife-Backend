package login;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.apache.commons.codec.binary.Base64;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.Instant;

public class LoginHandler {
    public LoginResponse handleRequest(LoginRequest request, Context context) {
        String token = createToken();
        LoginResponse response = new LoginResponse();
        if (validUser(request.username, request.password)) {
            addToken(token, request.username);
            response.authToken = token;
            response.success = true;
            response.message = "Success! Logging in.";
        }
        else {
            response.authToken = null;
            response.success = false;
            response.message = "Invalid username or password.";
        }
        return response;
    }
    public boolean validUser(String givenUsername, String givenPassword) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table userTable = dynamoDB.getTable("Users");
        Item user = userTable.getItem("Username", givenUsername);
        if (user == null) {
            return false;
        }
        String encoded = user.getString("password");
        String dec = new String(Base64.decodeBase64(user.getString("password")));
        if (new String(Base64.decodeBase64(user.getString("password").getBytes())).equals(givenPassword)) {
            return true;
        }
        return false;
    }
    public void addToken(String token, String username) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table authTable = dynamoDB.getTable("Authentication");
        Index index = authTable.getIndex("username-index");
        authTable.deleteItem("authToken", token, "username", username);
        Item authItem = new Item()
                .withPrimaryKey("authToken", token, "username", username)
                .withBigInteger("timestamp", java.math.BigInteger.valueOf(Instant.now().toEpochMilli()));
        authTable.putItem(authItem);
    }
    public String createToken() {
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 10; ++i) {
            stringBuilder.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        }
        String auth = stringBuilder.toString();
        return auth;
    }
}