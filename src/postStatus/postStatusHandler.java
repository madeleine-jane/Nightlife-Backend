package postStatus;

import Queue.RecievePostRequest;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.Gson;
import models.ImageFormat;
import models.User;

import java.math.BigInteger;
import java.time.Instant;

public class postStatusHandler {


    Dao dao = new Dao();

    public postStatusResponse handleRequest(postStatusRequest request, Context context) {
        postStatusResponse response = new postStatusResponse();

        if (dao.validToken(request.authToken, request.username)) {
            User user = dao.getUser(request.username);
            if (user == null) {
                response.success = false;
                response.message = "User not found";
            }
            else {
                String textAttachment = request.textAttachment;
                if (textAttachment.equals("")) {
                    textAttachment = "null";
                }
                BigInteger timestamp = java.math.BigInteger.valueOf(Instant.now().toEpochMilli());
                dao.createPost(request.message, user, timestamp, textAttachment, request.imageAttachment);
                response.success = true;
                response.message = "success! posted status.";
            }
        }
        else {
            response.success = false;
            response.message = "invalid auth token";
        }
        return response;
    }




}
