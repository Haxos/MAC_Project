package ch.heigvd.mac.hungryme.neo4j;

import ch.heigvd.mac.hungryme.models.Ingredient;
import ch.heigvd.mac.hungryme.models.Recipe;
import ch.heigvd.mac.hungryme.models.User;
import org.neo4j.driver.v1.Session;

public class Neo4jController {
    private final Session _session;

    public Neo4jController(Session session) {
        this._session = session;
    }

    public void addIngredient(Ingredient ingredient) {
        //TODO: add ingredient
    }

    public void addTag(String tag) {
        //TODO: add tag
    }

    public void addRecipe(Recipe recipe) {
        //TODO: add recipe
    }

    public void addUser(User user) {
        //TODO: add user
    }
}
