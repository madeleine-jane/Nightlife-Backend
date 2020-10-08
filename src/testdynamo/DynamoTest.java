package testdynamo;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import models.Status;
import models.User;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

public class DynamoTest {

    public static void main(String[] args) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .build();
        System.out.println(client.listTables());
        DynamoDB dynamoDB = new DynamoDB(client);
        uploadStatuses(client, dynamoDB);
    }

    public static void uploadStatuses(AmazonDynamoDB client, DynamoDB dynamoDB) {

//        User v = new User("Vlad", "Impaler", "vboi", "murderInnocents");
//        User w = new User("Emperor", "Kuzko", "grooveShark", "kuzkoooo");
//        User x = new User("Leroy", "Jenkins", "leerooooy", "jenkinssss");
//        User y = new User("Gordon", "Frog", "feedmeflies", "ribbit");
//        User z = new User("Rogue", "McCool", "rogue", "steal");
//        ArrayList<User> userList = new ArrayList<User>(Arrays.asList(v, w, x, y, z));
//
//        Status a = new Status("Once upon a time there was a cool #cat who did cool things <img class = 'cat-img' src = '../../res/images/sunglasses.jpg'>", userList.get(0), Instant.now().toEpochMilli());
//        Status b = new Status("It was a really cool #cat and it had sunglasses for special occasions", userList.get(1), Instant.now().toEpochMilli() + 1);
//        Status c = new Status("One day the #cat was lounging in the sun and contemplating his true greatness", userList.get(2), Instant.now().toEpochMilli() + 2);
//        Status d = new Status("But then- horror struck! The #cat spotted a @rogue climbing his balcony!", userList.get(3), Instant.now().toEpochMilli() + 3);
//        Status e = new Status("Furious, he leaped at the @rogue intruder. But he was a shade too slow.", userList.get(3), Instant.now().toEpochMilli() + 4);
//        Status f = new Status("The @rogue made off with his sunglasses and scuttled into the night.", userList.get(4), Instant.now().toEpochMilli() + 5);
//        Status g = new Status("Determined to catch this beast, he grabbed his sword and leaped after them.", userList.get(4), Instant.now().toEpochMilli() + 6);
//        ArrayList<Status> postList = new ArrayList<Status>(Arrays.asList(a, b, c, d, e, f, g));
//        User cu = new User("MJ", "Andersen", "stseraphim", "thedarkone");
//
//
//        Table followsTable = dynamoDB.getTable("Story");
//        ArrayList<Item> items = new ArrayList<>(Arrays.asList(
//                new Item().withPrimaryKey("authorID", cu.username, "timestamp", g.timestamp)
//                        .withString("authorFirstName", cu.firstName)
//                        .withString("authorLastName", cu.lastName)
//                        .withString("message", g.message)
//                        .withList("attachments", g.elements)
//
////                new Item().withPrimaryKey("ownerID", cu.username, "timestamp", b.timestamp)
////                        .withString("authorID", b.user.username)
////                        .withString("authorFirstName", b.user.firstName)
////                        .withString("authorLastName", b.user.lastName)
////                        .withString("message", b.message)
////                        .withList("attachments", b.elements),
////
////                new Item().withPrimaryKey("ownerID", cu.username, "timestamp", c.timestamp)
////                        .withString("authorID", c.user.username)
////                        .withString("authorFirstName", c.user.firstName)
////                        .withString("authorLastName", c.user.lastName)
////                        .withString("message", c.message)
////                        .withList("attachments", c.elements),
////
////                new Item().withPrimaryKey("ownerID", cu.username, "timestamp", d.timestamp)
////                        .withString("authorID", d.user.username)
////                        .withString("authorFirstName", d.user.firstName)
////                        .withString("authorLastName", d.user.lastName)
////                        .withString("message", d.message)
////                        .withList("attachments", d.elements)
//        ));
//
//        for (Item item : items) {
//            followsTable.putItem(item);
//        }

    }
}

