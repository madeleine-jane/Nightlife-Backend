package logout;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;

public class LogoutHandler {
    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_EAST_1)
            .build();
    DynamoDB dynamoDB = new DynamoDB(client);
    public LogoutResponse handleRequest(LogoutRequest request, Context context) {
        LogoutResponse response = new LogoutResponse();
        System.out.println(request.username);
        System.out.println(request.authToken);
        if (logout(request.authToken, request.username)) {
            response.success = true;
            response.message = "Success! Logging out.";
        }
        else {
            response.success = false;
            response.message = "Something went wrong!";
        }
        return response;
    }
    public boolean logout(String authToken, String username) {
        Table authTable = dynamoDB.getTable("Authentication");
        authTable.deleteItem("authToken", authToken, "username", username);
        return true;
    }

}