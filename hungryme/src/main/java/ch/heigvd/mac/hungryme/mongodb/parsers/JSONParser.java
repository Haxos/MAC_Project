package ch.heigvd.mac.hungryme.mongodb.parsers;

import ch.heigvd.mac.hungryme.models.Recipe;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;

public interface JSONParser {

    Collection<Recipe> getRecipes();
    void parse() throws IOException, ParseException;
}
