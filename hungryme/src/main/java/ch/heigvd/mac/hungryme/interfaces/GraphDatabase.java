package ch.heigvd.mac.hungryme.interfaces;

import ch.heigvd.mac.hungryme.models.Ingredient;
import ch.heigvd.mac.hungryme.models.Recipe;
import ch.heigvd.mac.hungryme.models.User;

import java.util.Collection;
import java.util.List;

public interface GraphDatabase {
    void addRecipe(Recipe recipe);
    void addIngredient(Ingredient ingredient);
    void addTag(String tag);
    void addUser(User user);
    List<String> getRecipes(Collection<String> ingredients, Collection<String> tags );
}
