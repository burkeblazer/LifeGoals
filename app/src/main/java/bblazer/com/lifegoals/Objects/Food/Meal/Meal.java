package bblazer.com.lifegoals.Objects.Food.Meal;

import org.json.JSONObject;

import java.util.ArrayList;

import bblazer.com.lifegoals.Manager.MealManager;
import bblazer.com.lifegoals.Objects.Food.Ingredient.Ingredient;

/**
 * Created by bblazer on 4/18/16.
 */
public class Meal {
    private String id;
    private String name;
    public ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
    private String mealType;
    public transient MealManager mealManager;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public void addIngredient(JSONObject jObj) {
        Ingredient ingredient = new Ingredient(jObj);

        // Add this ingredient to our master list
        Ingredient confirmedIngredient = mealManager.addIngredient(ingredient);

        ingredients.add(confirmedIngredient);
    }

    public void addIngredient(Ingredient ingredient) {
        // Add this ingredient to our master list
        Ingredient confirmedIngredient = mealManager.addIngredient(ingredient);

        ingredients.add(confirmedIngredient);
    }

    public float getFatPercent() {
        // Calculate all of the ingredients fat cals and get their total cals
        int totalCals = 0;
        int fatCals   = 0;
        for (int ct = 0; ct < ingredients.size(); ct++) {
            Ingredient ingredient = ingredients.get(ct);
            totalCals = ingredient.getCals();
            fatCals   = ingredient.getFatCals();
        }

        return ((float)fatCals/(float)totalCals) * 100;
    }

    public float getProteinPercent() {
        // Calculate all of the ingredients protein cals and get their total cals
        int totalCals   = 0;
        int proteinCals = 0;
        for (int ct = 0; ct < ingredients.size(); ct++) {
            Ingredient ingredient = ingredients.get(ct);
            totalCals   = ingredient.getCals();
            proteinCals = ingredient.getProteinCals();
        }

        return ((float)proteinCals/(float)totalCals) * 100;
    }

    public void deleteIngredient(String id) {
        for (int ct = 0; ct < ingredients.size(); ct++) {
            if (ingredients.get(ct).getId().equals(id)) {ingredients.remove(ct);return;}
        }
    }

    public float getCarbsPercent() {
        // Calculate all of the ingredients carbs cals and get their total cals
        int totalCals = 0;
        int carbsCals = 0;
        for (int ct = 0; ct < ingredients.size(); ct++) {
            Ingredient ingredient = ingredients.get(ct);
            totalCals = ingredient.getCals();
            carbsCals   = ingredient.getCarbCalories();
        }

        return ((float)carbsCals/(float)totalCals) * 100;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }
}
