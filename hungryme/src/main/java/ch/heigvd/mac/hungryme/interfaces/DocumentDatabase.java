package ch.heigvd.mac.hungryme.interfaces;

import ch.heigvd.mac.hungryme.models.Recipe;

import java.util.Collection;

public interface DocumentDatabase {
    void addData(String pathData);
    Recipe getRecipeById(String id);
    Collection<Recipe> getAllRecipes();
}
