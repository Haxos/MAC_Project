package parsers;

import models.Ingredient;
import models.Recipe;
import models.Unit;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
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

                byte[] encoded = Files.readAllBytes(Paths.get(_path));
                String content = new String(encoded);
                JSONObject collection = new JSONObject(content);
                int newId = 0;
                for (String key : collection.keySet()) {
                    JSONObject element = (JSONObject) collection.get(key);
                    ++newId;

                    Recipe recipe = new Recipe(newId, element.get("name").toString());
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

                    JSONArray ingredientsJSONArray = element.getJSONArray("ingredients");
                    for (Object ingredientObject : ingredientsJSONArray) {
                        String[] ingredientParsed = ingredientObject.toString().split(" ");
                        Ingredient ingredient;
                        try {
                            double quantity = Double.parseDouble(ingredientParsed[0]);
                            int startPos = 2;
                            Unit unit = Unit.getUnitFromString(ingredientParsed[1]);

                            if(unit == Unit.NONE) {
                                startPos = 1;
                            }

                            ingredient = new Ingredient(
                                    unit,
                                    quantity,
                                    String.join(" ", Arrays.copyOfRange(ingredientParsed, startPos, ingredientParsed.length))
                            );
                        } catch (Exception ignored) {
                            ingredient = new Ingredient(ingredientObject.toString());
                        }
                        recipe.addIngredient(ingredient);
                    }

                    JSONArray tagsJSONArray = element.getJSONArray("tags");
                    for (Object tagObject : tagsJSONArray) {
                        recipe.addTag(tagObject.toString());
                    }
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
