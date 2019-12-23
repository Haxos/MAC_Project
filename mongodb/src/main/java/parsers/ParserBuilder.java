package parsers;

import models.Recipe;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

            public void parse() throws IOException {
                _recipes = new ArrayList<>();
                //TODO: do parsing
                byte[] encoded = Files.readAllBytes(Paths.get(_path));
                String content = new String(encoded);
                JSONObject collection = new JSONObject(content);
                JSONObject element;
                Recipe recipe;
                int newId = 0;
                for (String key : collection.keySet()) {
                    element = (JSONObject) collection.get(key);

                    recipe = new Recipe(newId, element.get("name").toString());
                    recipe.setSource(element.get("source").toString());
                    recipe.setPreptime(Integer.parseInt(element.get("preptime").toString()));
                    recipe.setWaittime(Integer.parseInt(element.get("waittime").toString()));
                    recipe.setCooktime(Integer.parseInt(element.get("cooktime").toString()));
                    recipe.setServings(Integer.parseInt(element.get("servings").toString()));
                    recipe.setComments(element.get("comments").toString());
                    recipe.setCalories(Integer.parseInt(element.get("calories").toString()));
                    recipe.setFat(Integer.parseInt(element.get("fat").toString()));
                    recipe.setSatfat(Integer.parseInt(element.get("satfat").toString()));
                    recipe.setCarbs(Integer.parseInt(element.get("carbs").toString()));
                    recipe.setFiber(Integer.parseInt(element.get("fiber").toString()));
                    recipe.setSugar(Integer.parseInt(element.get("sugar").toString()));
                    recipe.setProtein(Integer.parseInt(element.get("protein").toString()));
                    recipe.setInstructions(element.get("instructions").toString());

                    //TODO: ingredients parse

                    //TODO: tags
                }
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
