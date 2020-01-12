package ch.heigvd.mac.hungryme.neo4j;

import ch.heigvd.mac.hungryme.models.Ingredient;
import ch.heigvd.mac.hungryme.models.Recipe;
import ch.heigvd.mac.hungryme.models.User;
import org.neo4j.driver.v1.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import static org.neo4j.driver.v1.Values.parameters;

public class Neo4jController implements ch.heigvd.mac.hungryme.interfaces.GraphDatabase, AutoCloseable {
    private final Driver _driver;

    public Neo4jController(String uri, String username, String password) {
        this(uri, username, password, 7687);
    }

    public Neo4jController(String uri, String username, String password, int port) {
        this._driver = GraphDatabase.driver(
                "bolt://" + uri + ":" + port,
                AuthTokens.basic(username, password)
        );
    }

    @Override
    public void close() {
        this._driver.close();
    }

    public void addRecipe(Recipe recipe) {
        try (Session session = this._driver.session()) {
            String result = session.writeTransaction(transaction -> {
                // label for query and propriety if not queryable
                // add recipe
                transaction.run(
                        "MERGE (n:Recipe {" +
                                "id: $id," +
                                "name: $name," +
                                "preptime: $preptime," +
                                "waittime: $waittime," +
                                "cooktime: $cooktime" +
                                "})",
                        parameters(
                                "id", recipe.getId(),
                                "name", recipe.getName(),
                                "preptime", recipe.getPrepTime(),
                                "waittime", recipe.getWaitTime(),
                                "cooktime", recipe.getCookTime()
                        )
                );

                // add relations
                for (Ingredient ingredient : recipe.getIngredients()) {
                    addIngredient(ingredient);
                    addRecipeIngredientRelation(recipe, ingredient);
                }

                for (String tag : recipe.getTags()) {
                    addTag(tag);
                    addRecipeTagRelation(recipe, tag);
                }

                return "Recipe: {id: " + recipe.getId() + ", name:" + recipe.getName() + "} added successfully";
            });

            // Source: https://www.javatpoint.com/java-get-current-date
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            System.out.println(dtf.format(now) + ": " + result);
        }
    }

    public void addIngredient(Ingredient ingredient) {
        try (Session session = this._driver.session()) {
            session.writeTransaction(transaction -> {
                transaction.run(
                        "MERGE (n:Ingredient {name: $name})",
                        parameters("name", ingredient.getName())
                );
                return "";
            });
        }
    }

    public void addTag(String tag) {
        try (Session session = this._driver.session()) {
            session.writeTransaction(transaction -> {
                transaction.run(
                        "MERGE (n:Tag {name: $name})",
                        parameters("name", tag)
                );
                return "";
            });
        }
    }

    public void addRecipeIngredientRelation(Recipe recipe, Ingredient ingredient) {
        try (Session session = this._driver.session()) {
            session.writeTransaction(transaction -> {
                transaction.run(
                        "MERGE (n:Recipe {" +
                                "id: $id," +
                                "name: $nameRecipe," +
                                "preptime: $preptime," +
                                "waittime: $waittime," +
                                "cooktime: $cooktime" +
                                "})" +
                            "-[r:contains]->" +
                            "(m:Ingredient {name: $nameIngredient})",
                        parameters(
                                "id", recipe.getId(),
                                "nameRecipe", recipe.getName(),
                                "preptime", recipe.getPrepTime(),
                                "waittime", recipe.getWaitTime(),
                                "cooktime", recipe.getCookTime(),
                                "nameIngredient", ingredient.getName()
                        )
                );
                return "";
            });
        }
    }

    public void addRecipeTagRelation(Recipe recipe, String tag) {
        try (Session session = this._driver.session()) {
            session.writeTransaction(transaction -> {
                transaction.run(
                        "MERGE (n:Recipe {" +
                                "id: $id," +
                                "name: $nameRecipe," +
                                "preptime: $preptime," +
                                "waittime: $waittime," +
                                "cooktime: $cooktime" +
                                "})" +
                                "-[r:is]->" +
                                "(m:Tag {name: $nameTag})",
                        parameters(
                                "id", recipe.getId(),
                                "nameRecipe", recipe.getName(),
                                "preptime", recipe.getPrepTime(),
                                "waittime", recipe.getWaitTime(),
                                "cooktime", recipe.getCookTime(),
                                "nameTag", tag
                        )
                );
                return "";
            });
        }
    }

    public void addUser(User user) {
        //TODO: add user
    }

}
