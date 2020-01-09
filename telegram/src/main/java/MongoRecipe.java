import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Collection;
import java.util.Iterator;

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

        // TITLE

        String telegramRecipe = "<b><u>" + (String) recipe.get("name") + "</u></b>";
        int servings = (int) recipe.get("servings");
        if(servings != 0) telegramRecipe = telegramRecipe.concat(" - <i>("+servings+" servings)</i>");
        telegramRecipe = telegramRecipe.concat("\n");

        // COOKING TIME STUFF

        telegramRecipe = telegramRecipe.concat("<i>Time :</i>\n");
        int preptime = (int) recipe.get("preptime");
        int waittime = (int) recipe.get("waittime");
        int cooktime = (int) recipe.get("cooktime");

        if(preptime != 0) telegramRecipe = telegramRecipe.concat(" Prep : "+ Utils.formatSeconds(preptime));
        if(waittime != 0) telegramRecipe = telegramRecipe.concat(" Wait : "+ Utils.formatSeconds(waittime));
        if(cooktime != 0) telegramRecipe = telegramRecipe.concat(" Cook : "+ Utils.formatSeconds(cooktime));

        telegramRecipe = telegramRecipe.concat("\n\n");

        // INGREDIENTS STUFF

        telegramRecipe = telegramRecipe.concat("<i>Ingrdients :</i>\n");
        Collection<Document> ingredients = (Collection<Document>)recipe.get("ingredients");
        Iterator ingredient = ingredients.iterator();
        while(ingredient.hasNext()){
            Document ingr = (Document) ingredient.next();
            String ingredientUnit       = (String) ingr.get("unit");
            String ingredientQuantity   =  ingr.get("quantity").toString();
            String ingredientName       = (String) ingr.get("name");

            telegramRecipe = telegramRecipe.concat(ingredientQuantity);
            if(!ingredientUnit.equals("unit")){
                telegramRecipe = telegramRecipe.concat(" "+ingredientUnit);
            }

            telegramRecipe = telegramRecipe.concat("\t"+ingredientName+"\n");

        }
        telegramRecipe = telegramRecipe.concat("\n");

        // PREPARATION STUFF

        telegramRecipe = telegramRecipe.concat("<i>Preparation :</i>\n");

        telegramRecipe = telegramRecipe.concat(
                ((String) recipe.get("instructions"))
        );

        return telegramRecipe;
    }

}
