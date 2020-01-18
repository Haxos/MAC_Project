package ch.heigvd.mac.hungryme.interfaces;

import ch.heigvd.mac.hungryme.models.Ingredient;
import ch.heigvd.mac.hungryme.models.Recipe;
import ch.heigvd.mac.hungryme.models.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public interface GraphDatabase {
    void addRecipe(Recipe recipe);
    void addIngredient(Ingredient ingredient);
    void addTag(String tag);
    void addUser(User user);
    LinkedList<ArrayList<String>> getRecipes(Collection<String> ingredients, Collection<String> tags );

    void likeRecipe(String recipeId, String userId);
    void unLikeRecipe(String recipeId, String userId);
    boolean isRecipeLiked(String recipeId, String userId);

    void dislikeRecipe(String recipeId, String userId);
    void unDislikeRecipe(String recipeId, String userId);
    boolean isRecipeDisliked(String recipeId, String userId);

    void favoriteRecipe(String recipeId, String userId);
    void unFavoriteRecipe(String recipeId, String userId);
    boolean isRecipeFavorite(String recipeId, String userId);

    LinkedList<ArrayList<String>> getUserFavoriteRecipes(String userId );

    boolean isUserLinkedToRecipe(String recipeId, String userId);

    void lookedAtRecipe(String recipeId, String userId);

    LinkedList<ArrayList<String>> getMostAppreciatedRecipes();
}
