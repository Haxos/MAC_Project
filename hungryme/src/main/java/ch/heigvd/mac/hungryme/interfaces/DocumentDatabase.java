package ch.heigvd.mac.hungryme.interfaces;

import ch.heigvd.mac.hungryme.models.Recipe;

public interface DocumentDatabase {
    void addData(String pathData);
    Recipe getRecipeById(String id);
}
