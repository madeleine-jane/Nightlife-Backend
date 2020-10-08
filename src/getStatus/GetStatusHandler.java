package getStatus;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import models.ImageFormat;
import models.Status;
import models.User;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GetStatusHandler {
    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_EAST_1)
            .build();
    DynamoDB dynamoDB = new DynamoDB(client);
    public GetStatusResponse handleRequest(GetStatusRequest request, Context context) {
        GetStatusResponse response = new GetStatusResponse();
        try {
            response.status = getStatus(request.timestamp, request.username);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        finally {
        }
        response.success = true;
        response.message = "Success!";
        return response;
    }

    public Status getStatus(BigInteger timestamp, String username) {

        Table statusTable = dynamoDB.getTable("Story");
        Item statusData = statusTable.getItem("authorID", username, "timestamp", timestamp);
        statusData.getString("authorID");
        String image = ImageFormat.keyToBase64(statusData.getString("authorID"));
        User u = new User(statusData.getString("authorFirstName"),
                statusData.getString("authorLastName"),
                statusData.getString("authorID"));
        u.profilePic = image;
        statusData.getString("message");
        System.out.println(u);
        statusData.getBigInteger("timestamp");
        Status s = new Status(statusData.getString("message"), u, statusData.getBigInteger("timestamp"));
        String imageAttachment = ImageFormat.keyToBase64(u.username + "-" + s.timestamp + "-image");
        String textAttachment = statusData.getString("textAttachment");
        if (textAttachment == null  || textAttachment.equals("null")) {
            textAttachment = "";
        }
        s.imageAttachment = imageAttachment;
        s.textAttachment = textAttachment;
        return s;
    }
}
