package postStatus;

import Queue.RecievePostRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.Gson;
import models.User;

import java.math.BigInteger;

public class PostStatusQueueDAO {
    public void createPost(String message, User user, BigInteger timestamp, String textAttachment, String imageAttachment) {
        String queueUrl = "https://sqs.us-east-1.amazonaws.com/021359287760/post_queue";
        Gson gson = new Gson();
        RecievePostRequest msgBody = new RecievePostRequest();
        msgBody.message = message;
        msgBody.textAttachment = textAttachment;
        msgBody.timestamp = timestamp;
        msgBody.user = user;
        msgBody.imageAttachment = imageAttachment;
        SendMessageRequest sendMsgRequest = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(gson.toJson(msgBody))
                .withDelaySeconds(5);

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        SendMessageResult result = sqs.sendMessage(sendMsgRequest);
        System.out.println("SQS RESULT: ");
        System.out.println(result.toString());
    }
}
