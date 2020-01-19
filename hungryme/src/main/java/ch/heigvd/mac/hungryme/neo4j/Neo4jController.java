package ch.heigvd.mac.hungryme.neo4j;

import ch.heigvd.mac.hungryme.models.Ingredient;
import ch.heigvd.mac.hungryme.models.Recipe;
import ch.heigvd.mac.hungryme.models.User;
import org.neo4j.driver.v1.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
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

    public  LinkedList<ArrayList<String>> getRecipes(Collection<String> ingredients, Collection<String> tags, String userId, int speedLevel ){
        String ingredientsNames = "";
        String tagNames = "";
        String query = "";
        String speed = "";

        switch (speedLevel){
            case 1 :
                speed = "MATCH (u:User{id:\""+ userId +"\"})\n" +
                    "WHERE (r.cooktime + r.preptime + r.waittime) > 0 AND (r.cooktime + r.preptime + r.waittime) < u.timeFast * 60\n";
                break;
            case 2 :
                speed = "MATCH (u:User{id:\""+ userId +"\"})\n" +
                        "WHERE (r.cooktime + r.preptime + r.waittime) > 0 AND (r.cooktime + r.preptime + r.waittime) < u.timeVeryFast * 60\n";
                break;
            default:
                break;
        }

        LinkedList<ArrayList<String>> result = new LinkedList<ArrayList<String>>();

        if(!ingredients.isEmpty()){
            ingredientsNames= "UNWIND " + CollectionToString(ingredients) + " AS ingredientName\n";
        }

        if(!tags.isEmpty()){
            tagNames = "UNWIND " + CollectionToString(tags) + " AS tagName\n";
        }
/*
UNWIND ["clove garlic", "milk", "salt"] AS ingredientName
UNWIND ["pie", "breakfast", "fruit"] AS tagName
MATCH (r:Recipe)-->(i:Ingredient)
WHERE i.name CONTAINS  ingredientName
MATCH (r:Recipe)-->(t:Tag{name: tagName})
WHERE t.name CONTAINS  tagName
RETURN r.id, r.name, collect(i.name) AS otherIngredients, collect(t.name) AS TagName
ORDER BY size(collect(i.name) + collect(t.name)) DESC*/
        if(!ingredients.isEmpty() && !tags.isEmpty()){ // we have ingredients and tags !
            query = query.concat(ingredientsNames);
            query = query.concat(tagNames);
            query = query.concat("MATCH (r:Recipe)-->(i:Ingredient)\n" +
                    "        WHERE i.name CONTAINS  ingredientName\n" +
                    "        MATCH (r:Recipe)-->(t:Tag)\n" +
                    "        WHERE t.name CONTAINS tagName\n" +
                            speed +
                    "        RETURN r.id, r.name, collect(i.name) AS otherIngredients, collect(t.name) AS TagName\n" +
                    "        ORDER BY size(collect(i.name) + collect(t.name)) DESC");
        }else if(!ingredients.isEmpty() && tags.isEmpty()){ // we only have ingredients
            query = query.concat(ingredientsNames);
            query = query.concat("MATCH (r:Recipe)-->(i:Ingredient)\n" +
                    "        WHERE i.name CONTAINS  ingredientName\n" +
                            speed +
                    "        RETURN r.id, r.name, collect(i.name) AS otherIngredients\n" +
                    "        ORDER BY size(collect(i.name)) DESC");
        }else if(ingredients.isEmpty() && !tags.isEmpty()){ // we only have tags !
            query = query.concat(tagNames);
            query = query.concat(
                    "        MATCH (r:Recipe)-->(t:Tag)\n" +
                    "        WHERE t.name CONTAINS tagName\n" +
                            speed +
                    "        RETURN r.id, r.name, collect(t.name) AS TagName\n" +
                    "        ORDER BY size(collect(t.name)) DESC");
        }

        if(query.length() != 0) {
            StatementResult queryResult = executeQery(query);
            while (queryResult.hasNext()) {
                Record currentVal = queryResult.next();
                ArrayList<String> recipeInfo = new ArrayList<>();
                recipeInfo.add(currentVal.get(0).asString());
                recipeInfo.add(currentVal.get(1).asString());
                result.push(recipeInfo);
            }
        }
        return result;
    }

    public LinkedList<ArrayList<String>> getMostAppreciatedRecipes(){
        String query = "MATCH (a:User)-[l:looked]->(b:Recipe)\n" +
                "WHERE l.liked = true OR l.favorite = true\n" +
                "RETURN b.id, b.name, COLLECT(a) as users\n" +
                "ORDER BY SIZE(users) DESC";

        LinkedList<ArrayList<String>> result = new LinkedList<ArrayList<String>>();

        StatementResult queryResult = executeQery(query);
        while (queryResult.hasNext()) {
            Record currentVal = queryResult.next();
            ArrayList<String> recipeInfo = new ArrayList<>();
            recipeInfo.add(currentVal.get(0).asString());
            recipeInfo.add(currentVal.get(1).asString());
            result.push(recipeInfo);
        }
        return result;
    }

    public LinkedList<ArrayList<String>> getMostUnseenAppreciatedRecipes(String userId){
        String query = "MATCH (a:User)-[l:looked]->(b:Recipe)\n" +
                "WHERE l.liked = true OR l.favorite = true\n" +
                "AND NOT (:User{id:\"" + userId + "\"})-->(b)\n" +
                "RETURN b.id, b.name, COLLECT(a) as users\n" +
                "ORDER BY SIZE(users) DESC";

        LinkedList<ArrayList<String>> result = new LinkedList<ArrayList<String>>();

        StatementResult queryResult = executeQery(query);
        while (queryResult.hasNext()) {
            Record currentVal = queryResult.next();
            ArrayList<String> recipeInfo = new ArrayList<>();
            recipeInfo.add(currentVal.get(0).asString());
            recipeInfo.add(currentVal.get(1).asString());
            result.push(recipeInfo);
        }
        return result;
    }

    public LinkedList<ArrayList<String>> getUserFavoriteRecipes(String userId ){
        String query = "MATCH (a:User{id:\""+ userId +"\"})-[l:looked{favorite:true}]->(b:Recipe)\n" +
                "RETURN b.id, b.name";

        StatementResult queryResult = executeQery(query);

        LinkedList<ArrayList<String>> result = new LinkedList<ArrayList<String>>();

        while (queryResult.hasNext()) {
            Record currentVal = queryResult.next();
            ArrayList<String> recipeInfo = new ArrayList<>();
            recipeInfo.add(currentVal.get(0).asString());
            recipeInfo.add(currentVal.get(1).asString());
            result.push(recipeInfo);
        }
        return result;
    }

    public LinkedList<ArrayList<String>> getUserMostAppreciatedRecipes(String userId ){
        String query = "MATCH (a:User{id:\""+ userId +"\"})-[l:looked]->(b:Recipe)\n" +
                "WHERE l.liked = true OR l.favorite = true\n" +
                "RETURN b.id, b.name";

        StatementResult queryResult = executeQery(query);

        LinkedList<ArrayList<String>> result = new LinkedList<ArrayList<String>>();

        while (queryResult.hasNext()) {
            Record currentVal = queryResult.next();
            ArrayList<String> recipeInfo = new ArrayList<>();
            recipeInfo.add(currentVal.get(0).asString());
            recipeInfo.add(currentVal.get(1).asString());
            result.push(recipeInfo);
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

    public void lookedAtRecipe(String recipeId, String userId){

        if(isUserLinkedToRecipe(recipeId, userId))
            return;

        String query = "MATCH (n:User), (m:Recipe)\n" +
                "WHERE n.id = \""+userId+"\" AND m.id = \""+recipeId+"\"\n" +
                "MERGE (n)-[l:looked]->(m)\n" +
                "SET l.liked = false\n" +
                "SET l.disliked = false\n" +
                "SET l.favorite = false\n";

        try (Session session = this._driver.session()) {
            session.writeTransaction(transaction -> {
                transaction.run(query);
                return "Relation looked between User: {id: " + userId + "} " +
                        "and Recipe: {id: " + recipeId + "} added successfully";
            });
        }
    }

    public boolean isUserLinkedToRecipe(String recipeId, String userId) {
        String query = "MATCH (u:User { id: \""+ userId +"\" })-->(r:Recipe { id: \""+ recipeId +"\" })\n" +
                "RETURN u,r";

        StatementResult queryResult = executeQery(query);
        return queryResult.hasNext();
    }

    private void relationShipUpadte(String recipeId, String userId, String relation, String value){
        String query ="MATCH (n:User{id:\""+ userId +"\"})-[l:looked]->(m:Recipe{id:\""+ recipeId +"\"})\n" +
                "SET l."+relation+" = "+value+"";
        try (Session session = this._driver.session()) {
            session.writeTransaction(transaction -> {
                transaction.run(query);
                return "Relation "+relation+" between User: {id: " + userId + "} " +
                        "and Recipe: {id: " + recipeId + "} successfully updated with value "+ value;
            });
        }
    }

    private boolean statusOfRelationValue(String recipeId, String userId, String relation){
        if(!isUserLinkedToRecipe(recipeId, userId))
            return false;

        String query ="MATCH (n:User{id:\""+userId+"\"})-[l:looked]->(m:Recipe{id:\""+recipeId+"\"})\n" +
                "RETURN l."+relation;
        StatementResult queryResult = executeQery(query);
        if(queryResult.hasNext()){
            return queryResult.next().get(0).asBoolean();
        }else{
            return false;
        }
    }

    public void likeRecipe(String recipeId, String userId) {
        relationShipUpadte(recipeId, userId, "liked", "true");
    }

    public void unLikeRecipe(String recipeId, String userId) {
        relationShipUpadte(recipeId, userId, "liked", "false");
    }

    public boolean isRecipeLiked(String recipeId, String userId) {
        return statusOfRelationValue(recipeId, userId, "liked");
    }

    public void dislikeRecipe(String recipeId, String userId) {
        relationShipUpadte(recipeId, userId, "disliked", "true");
    }

    public void unDislikeRecipe(String recipeId, String userId) {
        relationShipUpadte(recipeId, userId, "disliked", "false");
    }

    public boolean isRecipeDisliked(String recipeId, String userId) {
        return statusOfRelationValue(recipeId, userId, "disliked");
    }

    public void favoriteRecipe(String recipeId, String userId) {
        relationShipUpadte(recipeId, userId, "favorite", "true");
    }

    public void unFavoriteRecipe(String recipeId, String userId) {
        relationShipUpadte(recipeId, userId, "favorite", "false");
    }

    public boolean isRecipeFavorite(String recipeId, String userId) {
        return statusOfRelationValue(recipeId, userId, "favorite");
    }

    public LinkedList<ArrayList<String>> getNewRecipesBasedOnUserLikes(String userId){
        String query ="MATCH (a:User{id:\"" + userId + "\"})-[l:looked]->(b:Recipe)\n" +
                "WHERE l.liked = true OR l.favorite = true\n" +
                "MATCH (b)-->(i:Ingredient)\n" +
                "MATCH (r:Recipe)-->(i)\n" +
                "WHERE NOT (a)-->(r)\n" +
                "RETURN r.id, r.name, collect(i.name) AS Ingredients\n" +
                "ORDER BY size(collect(i.name)) DESC\n" +
                "LIMIT 50";

        LinkedList<ArrayList<String>> result = new LinkedList<ArrayList<String>>();

        StatementResult queryResult = executeQery(query);
        while (queryResult.hasNext()) {
            Record currentVal = queryResult.next();
            ArrayList<String> recipeInfo = new ArrayList<>();
            recipeInfo.add(currentVal.get(0).asString());
            recipeInfo.add(currentVal.get(1).asString());
            result.push(recipeInfo);
        }
        return result;
    }
}