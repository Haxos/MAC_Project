package ch.heigvd.mac.hungryme.mongodb;

import ch.heigvd.mac.hungryme.models.Recipe;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import ch.heigvd.mac.hungryme.mongodb.parsers.JSONParser;
import ch.heigvd.mac.hungryme.mongodb.parsers.MongoDBParser;
import ch.heigvd.mac.hungryme.mongodb.parsers.ParserBuilder;
import org.bson.types.ObjectId;

import java.util.List;

public class MongoDBController {
    private final String HOST_SITE;
    private final int HOST_PORT;
    private final String CREDENTIAL_USERNAME;
    private final String CREDENTIAL_PASSWORD;
    private final String DATABASE_NAME;
    private final String DATABASE_AUTH;
    private final String COLLECTION_NAME;

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

    private MongoClient connect() {
        return MongoClients.create("mongodb://" + CREDENTIAL_USERNAME + ":" + CREDENTIAL_PASSWORD
                + "@" + HOST_SITE + ":" + HOST_PORT + "/?authSource=" + DATABASE_AUTH);
    }

    public void addData(String pathData) {
        JSONParser jsonParser = ParserBuilder.JSONInputBuilder(pathData);
        try {
            System.out.println("Parsing JSON");
            jsonParser.parse();

            System.out.println("Connection to MongoDB");

            // Connect to MongoDB
            MongoClient mongoClient = connect();

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

    public Recipe getRecipeById(String id) {
        MongoClient mongoClient = connect();
        MongoCollection<Document> collection = mongoClient
                .getDatabase(DATABASE_NAME)
                .getCollection(COLLECTION_NAME);

        Document recipeDocument = collection.find(Filters.eq("_id", new ObjectId(id))).first();

        if(recipeDocument == null) {
            return null;
        } else {
            return new Recipe(recipeDocument.getString("_id"), recipeDocument.getString("name"));
        }
    }
}
