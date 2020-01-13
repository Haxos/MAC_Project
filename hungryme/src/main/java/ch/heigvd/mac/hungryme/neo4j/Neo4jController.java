package ch.heigvd.mac.hungryme.neo4j;

import ch.heigvd.mac.hungryme.models.Ingredient;
import ch.heigvd.mac.hungryme.models.Recipe;
import ch.heigvd.mac.hungryme.models.User;
import org.neo4j.driver.v1.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
                transaction.run("MERGE " + formatRecipe(recipe));

                for (Ingredient ingredient : recipe.getIngredients()) {
                    addIngredient(ingredient);
                }

                for (String tag : recipe.getTags()) {
                    addTag(tag);
                }

                return "Recipe: {id: " + recipe.getId() + ", name:" + recipe.getName() + "} added successfully";
            });

            // Source: https://www.javatpoint.com/java-get-current-date
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss:SS");
            LocalDateTime now = LocalDateTime.now();

            System.out.println(dtf.format(now) + ": " + result);
        }

        // need to separate otherwise the relations aren't created
        try (Session session = this._driver.session()) {
            String result = session.writeTransaction(transaction -> {
                // add relations
                for (Ingredient ingredient : recipe.getIngredients()) {
                    addRecipeIngredientRelation(recipe, ingredient);
                }

                for (String tag : recipe.getTags()) {
                    addRecipeTagRelation(recipe, tag);
                }

                return "Recipe: {id: " + recipe.getId() + ", name:" + recipe.getName() + "} relations added successfully";
            });

            // Source: https://www.javatpoint.com/java-get-current-date
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss:SS");
            LocalDateTime now = LocalDateTime.now();

            System.out.println(dtf.format(now) + ": " + result);
        }
    }

    public void addIngredient(Ingredient ingredient) {
        try (Session session = this._driver.session()) {
            session.writeTransaction(transaction -> {
                transaction.run("MERGE " + formatIngredient(ingredient));
                return "Ingredient: {name: " + ingredient.getName() + "} added successfully";
            });
        }
    }

    public void addTag(String tag) {
        try (Session session = this._driver.session()) {
            session.writeTransaction(transaction -> {
                transaction.run("MERGE " + formatTag(tag));
                return "Tag: {name: " + tag + "} added successfully";
            });
        }
    }

    public void addRecipeIngredientRelation(Recipe recipe, Ingredient ingredient) {
        try (Session session = this._driver.session()) {
            session.writeTransaction(transaction -> {
                transaction.run(
                    "MATCH " + formatRecipe(recipe, "n") +
                            ", " + formatIngredient(ingredient, "m") +
                        " MERGE (n)-[:contains]->(m)"
                );
                return "Relation between Recipe: {id: " + recipe.getId() + ", name:" + recipe.getName() + "} " +
                        "and Ingredient: {name: " + ingredient.getName() + "} added successfully";
            });
        }
    }

    public void addRecipeTagRelation(Recipe recipe, String tag) {
        try (Session session = this._driver.session()) {
            session.writeTransaction(transaction -> {
                transaction.run(
                        "MATCH " + formatRecipe(recipe, "n") +
                                ", " + formatTag(tag, "m") +
                            " MERGE (n)-[:has]->(m)"
                );
                return "Relation between Recipe: {id: " + recipe.getId() + ", name:" + recipe.getName() + "} " +
                        "and Tag: {name: " + tag + "} added successfully";
            });
        }
    }

    public void addUser(User user) {
        //TODO: add user
        try (Session session = this._driver.session()) {
            session.writeTransaction(transaction -> {
                transaction.run("MERGE " + formatUser(user));
                return "User: {userName: " + user.getUserName() + "} added successfully";
            });
        }
    }

    private String formatRecipe(Recipe recipe) {
        return formatRecipe(recipe, "n");
    }

    private String formatRecipe(Recipe recipe, String varName) {
        return "(" + varName + ":Recipe {" +
                "id: \"" + recipe.getId() + "\"" +
                ", name: \"" + recipe.getName().replaceAll("[^a-zA-Z0-9\\-]", " ").toLowerCase().trim() + "\"" +
                ", preptime: " + recipe.getPrepTime() +
                ", waittime: " + recipe.getWaitTime() +
                ", cooktime: " + recipe.getCookTime() +
                "})";
    }

    private String formatUser(User user){
        return formatUser(user, "n");
    }

    private String formatUser(User user, String varName){
        return "(" + varName + ":User {" +
                "id: \"" + user.getId() + "\"" +
                ", userName: \"" + user.getUserName() + "\"" +
                ", firstName: \"" + user.getFirstName() + "\"" +
                ", lastName: \"" + user.getLastName() + "\"" +
                ", timeFast: " + user.getTimeFast() +
                ", timeVeryFast: " + user.getTimeVeryFast() +
                "})";
    }

    private String formatIngredient(Ingredient ingredient) {
        return formatIngredient(ingredient, "n");
    }

    private String formatIngredient(Ingredient ingredient, String varName) {
        return "(" + varName + ":Ingredient {" +
                "name: \"" + ingredient.getName().replaceAll("[^a-zA-Z0-9\\-]", " ").toLowerCase().trim() + "\"" +
                "})";
    }

    private String formatTag(String tag) {
        return formatTag(tag, "n");
    }

    private String formatTag(String tag, String varName) {
        return "(" + varName + ":Tag {" +
                "name: \"" + tag.replaceAll("[^a-zA-Z0-9\\-]", " ").toLowerCase().trim() + "\"" +
                "})";
    }
}
