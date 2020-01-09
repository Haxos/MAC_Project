package ch.heigvd.mac.hungryme.mongodb.parsers;

import ch.heigvd.mac.hungryme.models.Ingredient;
import ch.heigvd.mac.hungryme.models.Recipe;
import ch.heigvd.mac.hungryme.models.Unit;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ParserBuilder {
    public static JSONParser JSONInputBuilder(final String path) {
        return new JSONParser() {
            private Collection<Recipe> _recipes;
            private final String _path = path;

            public Collection<Recipe> getRecipes() {
                return this._recipes;
            }

            public void parse() throws IOException {
                this._recipes = new ArrayList<>();

                byte[] encoded = Files.readAllBytes(Paths.get(this._path));
                String content = new String(encoded);
                JSONObject collection = new JSONObject(content);
                Integer newId = 0;
                for (String key : collection.keySet()) {
                    JSONObject element = (JSONObject) collection.get(key);
                    ++newId;

                    Recipe recipe = new Recipe(newId.toString(), element.get("name").toString());
                    recipe.setSource(element.get("source").toString());
                    recipe.setPrepTime(Integer.parseInt(element.get("preptime").toString()));
                    recipe.setWaitTime(Integer.parseInt(element.get("waittime").toString()));
                    recipe.setCookTime(Integer.parseInt(element.get("cooktime").toString()));
                    recipe.setServings(Integer.parseInt(element.get("servings").toString()));
                    recipe.setComments(element.get("comments").toString());
                    recipe.setCalories(Integer.parseInt(element.get("calories").toString()));
                    recipe.setFat(Integer.parseInt(element.get("fat").toString()));
                    recipe.setSatFat(Integer.parseInt(element.get("satfat").toString()));
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

                    this._recipes.add(recipe);
                }
            }
        };
    }

    public static MongoDBParser MongoDBOutputBuilder() {
        return new MongoDBParser() {
            private Collection<Recipe> _recipes;
            private Collection<Document> _documents;

            public Collection<Document> getDocuments() {
                return this._documents;
            }

            public void setRecipes(Collection<Recipe> recipes) {
                this._recipes = recipes;
            }

            public void compose() throws NullPointerException {
                this._documents = new ArrayList<>();
                for (Recipe recipe: this._recipes) {
                    Collection<Document> ingredientsCollection = new ArrayList<>();

                    for(Ingredient ingredient: recipe.getIngredients()) {
                        Document ingredientDocument = new Document()
                                            .append("unit", ingredient.getUnit().toString())
                                            .append("quantity", ingredient.getQuantity())
                                            .append("name", ingredient.getName());
                        ingredientsCollection.add(ingredientDocument);
                    }

                    Document document = new Document()
                            .append("name", recipe.getName())
                            .append("source", recipe.getSource())
                            .append("preptime", recipe.getPrepTime())
                            .append("waittime", recipe.getWaitTime())
                            .append("cooktime", recipe.getCookTime())
                            .append("servings", recipe.getServings())
                            .append("comments", recipe.getComments())
                            .append("calories", recipe.getCalories())
                            .append("fat", recipe.getFat())
                            .append("satfat", recipe.getSatFat())
                            .append("carbs", recipe.getCarbs())
                            .append("fiber", recipe.getFiber())
                            .append("sugar", recipe.getSugar())
                            .append("protein", recipe.getProtein())
                            .append("instructions", recipe.getInstructions())
                            .append("ingredients", ingredientsCollection)
                            .append("tags", recipe.getTags());
                    this._documents.add(document);
                }
            }
        };
    }
}
