package ch.heigvd.mac.hungryme.mongodb.parsers;

import models.Recipe;
import org.bson.Document;

import java.io.IOException;
import java.util.Collection;

public interface MongoDBParser {

    Collection<Document> getDocuments();
    void setRecipes(Collection<Recipe> recipes);
    void compose() throws NullPointerException, IOException;
}
