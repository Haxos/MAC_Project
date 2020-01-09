package ch.heigvd.mac.hungryme.neo4j;

import ch.heigvd.mac.hungryme.models.Ingredient;
import ch.heigvd.mac.hungryme.models.Recipe;
import ch.heigvd.mac.hungryme.models.Tag;
import ch.heigvd.mac.hungryme.models.User;
import org.neo4j.driver.v1.Session;

public class MainController {
    private final Session session;

    public MainController(Session session) {
        this.session = session;
    }

    public void addIngredient(Ingredient ingredient) {
        //TODO: add ingredient
    }

    public void addTag(Tag tag) {
        //TODO: add tag
    }

    public void addRecipe(Recipe recipe) {
        //TODO: add recipe
    }

    public void addUser(User user) {
        //TODO: add user
    }
}
