package parsers;

import models.Recipe;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;

public class ParserBuilder {
    public static JSONParser JSONInputBuilder(final String path) {
        return new JSONParser() {
            private Collection<Recipe> _recipes;
            private final String _path = path;

            public Collection<Recipe> getRecipes() {
                return _recipes;
            }

            public void parse() throws IOException, ParseException {
                _recipes = new ArrayList<Recipe>();
                //TODO: do parsing
            }
        };
    }

    public static MongoDBParser MongoDBOutputBuilder(final String path) {
        return new MongoDBParser() {
            private Collection<Recipe> _recipes;
            private final String _path = path;

            public void setRecipes(Collection<Recipe> recipes) {
                this._recipes = recipes;
            }

            public void compose() throws NullPointerException, IOException {
                //TODO: do composing
            }
        };
    }
}
