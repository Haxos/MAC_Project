package ch.heigvd.mac.hungryme.models;

import ch.heigvd.mac.hungryme.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Recipe {
    private String _id;
    private String _name;
    private String _source = ""; //link
    private int _preptime = 0;
    private int _waittime = 0;
    private int _cooktime = 0;
    private int _servings = 0;
    private String _comments = "";
    private int _calories = 0;
    private int _fat = 0;
    private int _satfat = 0;
    private int _carbs = 0;
    private int _fiber = 0;
    private int _sugar = 0;
    private int _protein = 0;
    private String _instructions = "";
    private Collection<Ingredient> _ingredients = new ArrayList<>();
    private Collection<String> _tags = new ArrayList<>();

    public Recipe(String id, String name) {
        this._id = id;
        this._name = name;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        this._name = name;
    }

    public String getSource() {
        return _source;
    }

    public void setSource(String source) {
        this._source = source;
    }

    public int getPrepTime() {
        return _preptime;
    }

    public void setPrepTime(int preptime) {
        this._preptime = preptime;
    }

    public int getWaitTime() {
        return _waittime;
    }

    public void setWaitTime(int waittime) {
        this._waittime = waittime;
    }

    public int getCookTime() {
        return _cooktime;
    }

    public void setCookTime(int cooktime) {
        this._cooktime = cooktime;
    }

    public int getServings() {
        return _servings;
    }

    public void setServings(int servings) {
        this._servings = servings;
    }

    public String getComments() {
        return _comments;
    }

    public void setComments(String comments) {
        this._comments = comments;
    }

    public int getCalories() {
        return _calories;
    }

    public void setCalories(int calories) {
        this._calories = calories;
    }

    public int getFat() {
        return _fat;
    }

    public void setFat(int fat) {
        this._fat = fat;
    }

    public int getSatFat() {
        return _satfat;
    }

    public void setSatFat(int satfat) {
        this._satfat = satfat;
    }

    public int getCarbs() {
        return _carbs;
    }

    public void setCarbs(int carbs) {
        this._carbs = carbs;
    }

    public int getFiber() {
        return _fiber;
    }

    public void setFiber(int fiber) {
        this._fiber = fiber;
    }

    public int getSugar() {
        return _sugar;
    }

    public void setSugar(int sugar) {
        this._sugar = sugar;
    }

    public int getProtein() {
        return _protein;
    }

    public void setProtein(int protein) {
        this._protein = protein;
    }

    public String getInstructions() {
        return _instructions;
    }

    public void setInstructions(String instructions) {
        this._instructions = instructions;
    }

    public Collection<Ingredient> getIngredients() {
        return _ingredients;
    }

    public Collection<String> getTags() {
        return _tags;
    }

    public void addIngredient(Ingredient ingredient) {
        this._ingredients.add(ingredient);
    }

    public void removeIngredient(Ingredient ingredient) {
        this._ingredients.remove(ingredient);
    }

    public void addTag(String tag) {
        this._tags.add(tag);
    }

    public void removeTag(String tag) {
        this._tags.remove(tag);
    }

}
