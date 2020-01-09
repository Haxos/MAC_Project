package ch.heigvd.mac.hungryme.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import ch.heigvd.mac.hungryme.mongodb.parsers.JSONParser;
import ch.heigvd.mac.hungryme.mongodb.parsers.MongoDBParser;
import ch.heigvd.mac.hungryme.mongodb.parsers.ParserBuilder;

import java.util.List;

public class MongoDBController {
    private String CREDENTIAL_USERNAME;
    private String CREDENTIAL_PASSWORD;
    private String DATABASE_NAME;
    private String DATABASE_AUTH;
    private String COLLECTION_NAME;
    private String HOST_SITE;
    private int HOST_PORT;

    public MongoDBController(
            String host_site,
            int host_port,
            String credential_username,
            String credential_password,
            String database_name,
            String database_auth,
            String collection_name
    )
    {
        this.HOST_SITE = host_site;
        this.HOST_PORT = host_port;
        this.CREDENTIAL_USERNAME = credential_username;
        this.CREDENTIAL_PASSWORD = credential_password;
        this.DATABASE_NAME = database_name;
        this.DATABASE_AUTH = database_auth;
        this.COLLECTION_NAME = collection_name;
    }

    public void addData(String pathData) {
        JSONParser jsonParser = ParserBuilder.JSONInputBuilder(pathData);
        try {
            System.out.println("Parsing JSON");
            jsonParser.parse();

            System.out.println("Connection to MongoDB");

            MongoClient mongoClient = MongoClients.create("mongodb://" + CREDENTIAL_USERNAME + ":" + CREDENTIAL_PASSWORD
                    + "@" + HOST_SITE + ":" + HOST_PORT + "/?authSource=" + DATABASE_AUTH);

            MongoCollection<Document> collection = mongoClient
                    .getDatabase(DATABASE_NAME)
                    .getCollection(COLLECTION_NAME);

            System.out.println("Pushing recipes to MongoDB");
            MongoDBParser mongoDBParser = ParserBuilder.MongoDBOutputBuilder();
            mongoDBParser.setRecipes(jsonParser.getRecipes());
            mongoDBParser.compose();
            collection.insertMany((List<Document>) mongoDBParser.getDocuments());

            mongoClient.close();
            System.out.println("Operation successful");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MongoDBController mongoDBController = new MongoDBController(
                "192.168.99.101",
                27017,
                "admin",
                "pass",
                "hungry-me",
                "admin",
                "recipes"
        );
        mongoDBController.addData("resources/db-recipes.json");
    }
}
