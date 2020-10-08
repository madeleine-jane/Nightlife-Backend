package changeImage;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.Instant;

public class changeImageHandler {

    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_EAST_1)
            .build();
    DynamoDB dynamoDB = new DynamoDB(client);

    public changeImageResponse handleRequest(changeImageRequest request, Context context) {
        changeImageResponse response = new changeImageResponse();
        if (!validToken(request.authToken, request.username)) {
            response.success = false;
            response.message = "invalid authtoken";
        }
        else {
            uploadToS3(request.image, request.username);
            response.success = true;
            response.message = "Success! Posted image.";
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
    public boolean uploadToS3(String base64, String username) {
        byte[] bI = org.apache.commons.codec.binary.Base64.decodeBase64((base64.substring(base64.indexOf(",")+1)).getBytes());
        InputStream fis = new ByteArrayInputStream(bI);
        AmazonS3 s3 = new AmazonS3Client();
        s3.setRegion(Region.getRegion(Regions.US_EAST_1));
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bI.length);
        metadata.setContentType("image/png");
        metadata.setCacheControl("public, max-age=31536000");
        s3.putObject("nightlife-images", username, fis, metadata);

        return true;
    }
}
