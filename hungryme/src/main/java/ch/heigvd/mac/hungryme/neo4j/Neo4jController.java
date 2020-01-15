package ch.heigvd.mac.hungryme.neo4j;

import ch.heigvd.mac.hungryme.models.Ingredient;
import ch.heigvd.mac.hungryme.models.Recipe;
import ch.heigvd.mac.hungryme.models.User;
import org.neo4j.driver.v1.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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

    public List<String> getRecipes(Collection<String> ingredients, Collection<String> tags ){
        String ingredientsNames = "";
        String tagNames = "";
        String query = "";

        List result = new ArrayList<String>();

        if(!ingredients.isEmpty()){
            ingredientsNames= "UNWIND " + CollectionToString(ingredients) + " AS ingredientName\n";
        }

        if(!tags.isEmpty()){
            tagNames = "UNWIND " + CollectionToString(tags) + " AS tagName\n";
        }

        if(!ingredients.isEmpty() && !tags.isEmpty()){ // we have ingredients and tags !
            query = query.concat(ingredientsNames);
            query = query.concat(tagNames);
            query = query.concat("MATCH (r:Recipe)-->(ingredient {name: ingredientName})\n" +
                    "        MATCH (r:Recipe)-->(t:Tag{name: tagName})\n" +
                    "        RETURN r.id, r.name, collect(ingredient.name) AS otherIngredients, collect(t.name) AS TagName\n" +
                    "        ORDER BY size(collect(ingredient.name) + collect(t.name)) DESC");
        }else if(!ingredients.isEmpty() && tags.isEmpty()){ // we only have ingredients
            query = query.concat(ingredientsNames);
            query = query.concat("MATCH (r:Recipe)-->(ingredient {name: ingredientName})\n" +
                    "        RETURN r.id, r.name, collect(ingredient.name) AS otherIngredients\n" +
                    "        ORDER BY size(collect(ingredient.name)) DESC");
        }else if(ingredients.isEmpty() && !tags.isEmpty()){ // we only have tags !
            query = query.concat(tagNames);
            query = query.concat(
                    "        MATCH (r:Recipe)-->(t:Tag{name: tagName})\n" +
                    "        RETURN r.id, r.name, collect(t.name) AS TagName\n" +
                    "        ORDER BY size(collect(t.name)) DESC");
        }

        /*
        UNWIND ["clove garlic", "milk", "salt"] AS ingredientName
        UNWIND ["pie", "breakfast", "fruit"] AS tagName
        MATCH (r:Recipe)-->(ingredient {name: ingredientName})
        MATCH (r:Recipe)-->(t:Tag{name: tagName})
        RETURN r.id, r.name, collect(ingredient.name) AS otherIngredients, collect(t.name) AS TagName
        ORDER BY size(collect(ingredient.name) + collect(t.name)) DESC
        */
        StatementResult queryResult = executeQery(query);

        while (queryResult.hasNext()){
            result.add(queryResult.next().get(0));
        }

        return result;
    }

    private StatementResult executeQery( String query ){
        try ( Session session = this._driver.session() ){
            return session.run( query);
        }
    }

    private String CollectionToString(Collection<String> collection){
        String collectionString = "[";
        for(String element : collection){
            collectionString = collectionString.concat("\""+element+"\", ");
        }
        collectionString = collectionString.substring(0, collectionString.length() - 2);
        collectionString = collectionString.concat("]");
        return collectionString;
    }
}
