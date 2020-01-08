import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

public class MongoRecipe {
    private final static String CREDENTIAL_USERNAME = "admin";
    private final static String CREDENTIAL_PASSWORD = "pass";
    private final static String DATABASE_NAME = "hungry-me";
    private final static String DATABASE_AUTH = "admin";
    private final static String COLLECTION_NAME = "recipes";
    private final static String HOST_SITE = "localhost";
    private final static int HOST_PORT = 27017;

    private final static MongoClient MONGO_CLIENT = MongoClients.create("mongodb://" + CREDENTIAL_USERNAME + ":" + CREDENTIAL_PASSWORD
            + "@" + HOST_SITE + ":" + HOST_PORT + "/?authSource=" + DATABASE_AUTH);

    private final static MongoDatabase RECIPE_DB = (MongoDatabase) MONGO_CLIENT.getDatabase(DATABASE_NAME);
    private  final static MongoCollection<Document> RECIPE_COLLECTION = (MongoCollection<Document>) RECIPE_DB.getCollection(COLLECTION_NAME);

    public static String recipeById(String id){
        Document recipe = RECIPE_COLLECTION.find(eq("_id", new ObjectId(id))).first();
        String telegramRecipe = "<b>" + (String) recipe.get("name") + "</b>\n";
        return telegramRecipe;
    }

}
