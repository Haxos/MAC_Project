package parsers;

import models.Recipe;

import java.io.IOException;
import java.util.Collection;

public interface MongoDBParser {

    void setRecipes(Collection<Recipe> recipes);
    void compose() throws NullPointerException, IOException;
}
