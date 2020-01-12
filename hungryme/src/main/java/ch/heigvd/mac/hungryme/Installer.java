package ch.heigvd.mac.hungryme;

import ch.heigvd.mac.hungryme.models.Recipe;
import ch.heigvd.mac.hungryme.mongodb.MongoDBController;
import ch.heigvd.mac.hungryme.neo4j.Neo4jController;

import java.util.Collection;

public class Installer {

    public static void main(String[] args) {
        MongoDBController mongoDBController = new MongoDBController(
                Env.MONGODB_URI,
                Env.MONGODB_PORT,
                Env.MONGODB_CREDENTIAL_USERNAME,
                Env.MONGODB_CREDENTIAL_PASSWORD,
                Env.MONGODB_DATABASE_NAME,
                Env.MONGODB_DATABASE_AUTH,
                Env.MONGODB_COLLECTION_NAME
        );
        mongoDBController.addData(Env.MONGODB_DATA_PATH);

        Collection<Recipe> recipes = mongoDBController.getAllRecipes();

        Neo4jController neo4jController = new Neo4jController(
                Env.NEO4J_URI,
                Env.NEO4J_CREDENTIAL_USERNAME,
                Env.NEO4J_CREDENTIAL_PASSWORD
        );

        System.out.println("Neo4j: begin data push");

        for (Recipe recipe : recipes) {
            neo4jController.addRecipe(recipe);
        }
        System.out.println("Neo4j: data pushed");

        System.exit(0);
    }
}
