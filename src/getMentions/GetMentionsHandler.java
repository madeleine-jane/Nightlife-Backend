package getMentions;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import getFeed.GetFeedHandler;
import models.ImageFormat;
import models.Status;
import models.User;

import java.util.*;

public class GetMentionsHandler {
    public GetMentionsResponse handleRequest(GetMentionsRequest request, Context context) {
        GetMentionsResponse response = new GetMentionsResponse();

        response.statuses = getMentions(request.keyword);
        response.success = true;
        response.message = "Success! Returning mentions.";
        return response;
    }

    public ArrayList<Status> getMentions(String keyword) {
        ArrayList<Status> statuses = new ArrayList<>();
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table statusTable = dynamoDB.getTable("Mentions");
        ItemCollection i = statusTable.query("tag", keyword);
        Iterator<Item> iterator = i.iterator();
        Item item = null;
        while (iterator.hasNext()) {
            item = iterator.next();
            System.out.println(item.toJSONPretty());

            String image = ImageFormat.keyToBase64(item.getString("authorID"));
            User u = new User(item.getString("authorFirstName"),
                    item.getString("authorLastName"),
                    item.getString("authorID"));
            u.profilePic = image;
            Status s = new Status(item.getString("message"), u, item.getBigInteger("statusID"));
            String imageAttachment = ImageFormat.keyToBase64(u.username + "-" + s.timestamp + "-image");
            String textAttachment = item.getString("textAttachment");
            if (textAttachment == null  || textAttachment.equals("null")) {
                textAttachment = "";
            }
            s.imageAttachment = imageAttachment;
            s.textAttachment = textAttachment;
            statuses.add(s);
        }
        return statuses;

    }

}
