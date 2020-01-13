package ch.heigvd.mac.hungryme.mongodb;

import ch.heigvd.mac.hungryme.Utils;
import ch.heigvd.mac.hungryme.interfaces.DocumentDatabase;
import ch.heigvd.mac.hungryme.models.Ingredient;
import ch.heigvd.mac.hungryme.models.Recipe;
import ch.heigvd.mac.hungryme.models.Unit;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import ch.heigvd.mac.hungryme.mongodb.parsers.JSONParser;
import ch.heigvd.mac.hungryme.mongodb.parsers.MongoDBParser;
import ch.heigvd.mac.hungryme.mongodb.parsers.ParserBuilder;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MongoDBController implements DocumentDatabase {
    private final String URI;
    private final int PORT;
    private final String CREDENTIAL_USERNAME;
    private final String CREDENTIAL_PASSWORD;
    private final String DATABASE_NAME;
    private final String DATABASE_AUTH;
    private final String COLLECTION_NAME;

    public MongoDBController(
            String uri,
            int port,
            String credential_username,
            String credential_password,
            String database_name,
            String database_auth,
            String collection_name
    )
    {
        this.URI = uri;
        this.PORT = port;
        this.CREDENTIAL_USERNAME = credential_username;
        this.CREDENTIAL_PASSWORD = credential_password;
        this.DATABASE_NAME = database_name;
        this.DATABASE_AUTH = database_auth;
        this.COLLECTION_NAME = collection_name;
    }

    private MongoClient connect() {
        return MongoClients.create("mongodb://" + CREDENTIAL_USERNAME + ":" + CREDENTIAL_PASSWORD
                + "@" + URI + ":" + PORT + "/?authSource=" + DATABASE_AUTH);
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

        return documentToRecipe(collection.find(Filters.eq("_id", new ObjectId(id))).first());
    }

    @Override
    public Collection<Recipe> getAllRecipes() {
        MongoClient mongoClient = connect();
        MongoCollection<Document> collection = mongoClient
                .getDatabase(DATABASE_NAME)
                .getCollection(COLLECTION_NAME);
        Collection<Recipe> recipes = new ArrayList<>();

        for (Document document : collection.find()) {
            recipes.add(documentToRecipe(document));
        }

        return recipes;
    }


    private Recipe documentToRecipe(Document recipeDocument) {

        if(recipeDocument == null) {
            return null;
        }

        Recipe recipe = new Recipe(recipeDocument.get("_id").toString(), (String) recipeDocument.get("name"));
        recipe.setSource((String) recipeDocument.get("source"));
        recipe.setPrepTime((int) recipeDocument.get("preptime"));
        recipe.setWaitTime((int) recipeDocument.get("waittime"));
        recipe.setCookTime((int) recipeDocument.get("cooktime"));
        recipe.setServings((int) recipeDocument.get("servings"));
        recipe.setComments((String) recipeDocument.get("comments"));
        recipe.setCalories((int) recipeDocument.get("calories"));
        recipe.setFat((int) recipeDocument.get("fat"));
        recipe.setSatFat((int) recipeDocument.get("satfat"));
        recipe.setCarbs((int) recipeDocument.get("carbs"));
        recipe.setFiber((int) recipeDocument.get("fiber"));
        recipe.setSugar((int) recipeDocument.get("sugar"));
        recipe.setProtein((int) recipeDocument.get("protein"));
        recipe.setInstructions((String) recipeDocument.get("instructions"));

        for (Document ingredientDocument: (Collection<Document>)recipeDocument.get("ingredients")) {
            recipe.addIngredient(
                    new Ingredient(
                            Unit.getUnitFromString((String) ingredientDocument.get("unit")),
                            (double) ingredientDocument.get("quantity"),
                            (String) ingredientDocument.get("name")
                    )
            );
        }

        for (String tag : (Collection<String>)recipeDocument.get("tags")) {
            recipe.addTag(tag);
        }

        return recipe;
    }
}
