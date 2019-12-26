import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import parsers.JSONParser;
import parsers.MongoDBParser;
import parsers.ParserBuilder;

import java.util.Collections;
import java.util.List;

public class Main {
    private final static String CREDENTIAL_USERNAME = "admin";
    private final static char[] CREDENTIAL_PASSWORD = "pass".toCharArray();
    private final static String DATABASE_NAME = "hungry-me";
    private final static String COLLECTION_NAME = "recipes";
    private final static String HOST_SITE = "localhost";
    private final static int HOST_PORT = 27017;

    public static void main(String[] args) {
        JSONParser jsonParser = ParserBuilder.JSONInputBuilder("resources/db-recipes.json");
        try {
            System.out.println("Parsing JSON");
            jsonParser.parse();

            System.out.println("Connection to MongoDB");
            MongoCredential credential = MongoCredential.createCredential(
                    CREDENTIAL_USERNAME,
                    DATABASE_NAME,
                    CREDENTIAL_PASSWORD
            );

            MongoClientSettings settings = MongoClientSettings.builder()
                    .credential(credential)
//                    .applyToSslSettings(builder -> builder.enabled(true))
                    .applyToClusterSettings(builder ->
                            builder.hosts(Collections.singletonList(new ServerAddress(HOST_SITE, HOST_PORT)))
                    ).build();

            MongoClient mongoClient = MongoClients.create(settings);
//            MongoClient mongoClient = MongoClients.create("mongodb://" + HOST_SITE + ":" + HOST_PORT);
            //FIXME: if collection doesn't exits, creates it
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
}
