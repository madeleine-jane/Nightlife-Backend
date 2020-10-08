package signup;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import models.Image;
import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.SecureRandom;
import java.time.Instant;

public class SignupHandler {
    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_EAST_1)
            .build();
    DynamoDB dynamoDB = new DynamoDB(client);

    public SignupResponse handleRequest(SignupRequest request, Context context) {
        SignupResponse response = new SignupResponse();
        if (userExists(request.username, request.password)) {
            response.authToken = null;
            response.success = false;
            response.message = "User already exists";
            return response;
        }
        if (createUser(request.username, request.password, request.firstName, request.lastName, Image.s)) {
            //response.authToken = authenticate(request.username);
            response.success = true;
            response.message = "Success! You have registered. Signing in.";
        }
        return response;

    }
    public boolean userExists(String givenUsername, String givenPassword) {
//        Table userTable = dynamoDB.getTable("Users");
//        Item user = userTable.getItem("Username", givenUsername);
//        if (user == null) {
//            return false;
//        }
        return false;
    }
    public boolean createUser(String username, String password, String firstName, String lastName, String image) {
        uploadToS3(image, username);
        Table userTable = dynamoDB.getTable("Users");
        Item newUser = new Item()
                .withPrimaryKey("Username", username)
                .withString("firstName", firstName)
                .withString("lastName", lastName)
                .withString("password", new String(Base64.encodeBase64String(password.getBytes())));
        userTable.putItem(newUser);
        return true;
    }
    public boolean uploadToS3(String base64, String username) {
        byte[] bI = Base64.decodeBase64((base64).getBytes());
        InputStream fis = new ByteArrayInputStream(bI);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .build();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bI.length);
        metadata.setContentType("image/png");
        metadata.setCacheControl("public, max-age=31536000");
        s3Client.putObject("nightlife-images", username, fis, metadata);
        return true;
    }
    public String authenticate(String username) {
        String token = createToken();
        Table authTable = dynamoDB.getTable("Authentication");
        Item authItem = new Item().withPrimaryKey("authToken", token, "username", username)
                .withBigInteger("timestamp", java.math.BigInteger.valueOf(Instant.now().toEpochMilli()));
        ;
        authTable.putItem(authItem);
        return token;
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
