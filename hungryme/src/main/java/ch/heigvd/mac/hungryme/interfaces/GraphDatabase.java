package ch.heigvd.mac.hungryme.interfaces;

import ch.heigvd.mac.hungryme.models.Ingredient;
import ch.heigvd.mac.hungryme.models.Recipe;

public interface GraphDatabase {
    void addRecipe(Recipe recipe);
    void addIngredient(Ingredient ingredient);
    void addTag(String tag);
}
