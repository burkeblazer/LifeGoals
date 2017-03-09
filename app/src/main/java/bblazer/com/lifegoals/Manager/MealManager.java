package bblazer.com.lifegoals.Manager;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import bblazer.com.lifegoals.Activity.MainActivity;
import bblazer.com.lifegoals.Objects.Food.FoodEvent;
import bblazer.com.lifegoals.Objects.Food.Ingredient.Ingredient;
import bblazer.com.lifegoals.Objects.Food.Meal.Meal;

/**
 * Created by bblazer on 4/17/16.
 */
public class MealManager {
    public ArrayList<Meal> meals = new ArrayList<Meal>();
    public ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
    public ArrayList<FoodEvent>foodEvents    = new ArrayList<FoodEvent>();
    MainActivity activity;
    public static final String MT_BREAKFAST  = "Breakfast";
    public static final String MT_LUNCH      = "Lunch";
    public static final String MT_DINNER     = "Dinner";
    public static final String MT_OTHER      = "Other";

    private List<MealManagerListener> listeners = new ArrayList<MealManagerListener>();

    public void addListener(MealManagerListener toAdd) {
        listeners.add(toAdd);
    }

    public MealManager(MainActivity activity) {
        this.activity = activity;

        loadIngredients();

        loadMeals();

        loadFoodEvents();
    }

    public void editIngredient(Ingredient ingredient, String id, boolean suppressSave) {
        // Check to see if we already have this one
        Ingredient found = null;
        for (int ct = 0; ct < ingredients.size(); ct++) {
            if (ingredients.get(ct).getApiID() != null && ingredient.getApiID() != null && ingredients.get(ct).getApiID().equals(ingredient.getApiID())) {
                found = ingredients.get(ct);
            }
            if (ingredients.get(ct).getName().equals(ingredient.getName())) {
                found = ingredients.get(ct);
            }
        }

        if (found == null) {
            ingredients.add(ingredient);
            this.saveIngredients();
        }
        else {
            ingredients.remove(found);
            ingredients.add(ingredient);
            this.saveIngredients();
        }

        if (!suppressSave) {
            this.saveMeals();
        }
    }

    public class FoodEventComparator implements Comparator<FoodEvent> {
        @Override
        public int compare(FoodEvent o1, FoodEvent o2) {
            return o2.getCreated().compareTo(o1.getCreated());
        }
    }

    public void loadFoodEvents() {
        // Try to deserialize any events we have stored
        Gson gson                = new Gson();
        SharedPreferences mPrefs = activity.getPreferences(Activity.MODE_PRIVATE);
        String json              = mPrefs.getString("LifeGoalsFoodEvents", "");

        // There's nothing ouch
        if (json.equals("")) {return;}

        // Deserialize
        foodEvents = gson.fromJson(json, new TypeToken<ArrayList<FoodEvent>>(){}.getType());
        Collections.sort(foodEvents, new FoodEventComparator());
    }

    public void loadIngredients() {
        // Try to deserialize any events we have stored
        Gson gson                = new Gson();
        SharedPreferences mPrefs = activity.getPreferences(Activity.MODE_PRIVATE);
        String json              = mPrefs.getString("LifeGoalsIngredients", "");

        // There's nothing ouch
        if (json.equals("")) {return;}

        // Deserialize
        ingredients = gson.fromJson(json, new TypeToken<ArrayList<Ingredient>>(){}.getType());
    }

    public void loadMeals() {
        // Try to deserialize any events we have stored
        Gson gson                = new Gson();
        SharedPreferences mPrefs = activity.getPreferences(Activity.MODE_PRIVATE);
        String json              = mPrefs.getString("LifeGoalsMeals", "");

        // There's nothing ouch
        if (json.equals("")) {return;}

        // Deserialize
        meals = gson.fromJson(json, new TypeToken<ArrayList<Meal>>(){}.getType());

        for (int ct = 0; ct < meals.size(); ct++) {
            meals.get(ct).mealManager = this;
        }
    }

    public void addMeal(Meal meal, boolean suppressSave) {
        int index = -1;
        for (int ct = 0; ct < meals.size(); ct++) {
            if (meals.get(ct).getId().equals(meal.getId())) {
                index = ct;
            }
        }

        if (index != -1) {this.editMeal(meal, this.meals.get(index).getId(), suppressSave);return;}

        meals.add(meal);

        if (!suppressSave) {
            this.saveMeals();
        }
    }

    public void editMeal(Meal meal, String id, boolean suppressSave) {
        for (int ct = 0; ct < meals.size(); ct++) {
            if (meals.get(ct).getId().equals(id)) {
                meals.get(ct).setName(meal.getName());
                meals.get(ct).setIngredients(meal.ingredients);
            }
        }

        if (!suppressSave) {
            this.saveMeals();
        }
    }

    public Meal getByID(String id) {
        Meal meal = null;
        for (int ct = 0; ct < meals.size(); ct++) {
            if (meals.get(ct).getId().equals(id)) {
                meal = meals.get(ct);
            }
        }

        return meal;
    }

    public void deleteMeal(Meal delete) {
        int index = -1;
        for (int ct = 0; ct < meals.size(); ct++) {
            if (meals.get(ct).getId().equals(delete.getId())) {
                index = ct;
            }
        }

        if (index == -1) {return;}

        meals.remove(index);

        this.saveMeals();
    }

    public void saveMeals() {
        SharedPreferences  mPrefs            = activity.getPreferences(Activity.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson                            = new Gson();
        String json                          = gson.toJson(meals);
        prefsEditor.putString("LifeGoalsMeals", json);
        prefsEditor.commit();

        for (MealManagerListener hl : listeners)
            hl.mealsUpdated();
    }

    public void saveIngredients() {
        SharedPreferences  mPrefs            = activity.getPreferences(Activity.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson                            = new Gson();
        String json                          = gson.toJson(ingredients);
        prefsEditor.putString("LifeGoalsIngredients", json);
        prefsEditor.commit();

        for (MealManagerListener hl : listeners)
            hl.ingredientsUpdated();
    }

    public ArrayList<Meal> getMeals() {
        return meals;
    }

    public Ingredient addIngredient(Ingredient ingredient) {
        // Check to see if we already have this one
        Ingredient found = null;
        for (int ct = 0; ct < ingredients.size(); ct++) {
            if (ingredients.get(ct).getApiID() != null && ingredient.getApiID() != null && ingredients.get(ct).getApiID().equals(ingredient.getApiID())) {
                found = ingredients.get(ct);
            }
            if (ingredients.get(ct).getName().equals(ingredient.getName())) {
                found = ingredients.get(ct);
            }
        }

        if (found != null) {
            return found;
        }
        else {
            ingredients.add(ingredient);
            this.saveIngredients();
            return ingredient;
        }
    }

    public Ingredient getIngredientByID(String s) {
        for (int ct = 0; ct < ingredients.size(); ct++) {
            if (ingredients.get(ct).getId().equals(s)) {return ingredients.get(ct);}
        }

        return null;
    }

    public void addFoodEvent(FoodEvent foodEvent) {
        // Check to see if we already have this one
        FoodEvent found = null;
        for (int ct = 0; ct < foodEvents.size(); ct++) {
            if (foodEvents.get(ct).getId().equals(foodEvent.getId())) {
                found = foodEvents.get(ct);
            }
        }

        if (found != null) {
            editFoodEvent(foodEvent, false);
        }
        else {
            foodEvents.add(foodEvent);
            this.saveFoodEvents();
        }
    }

    private void editFoodEvent(FoodEvent foodEvent, boolean suppressSave) {
        int remove = -1;
        for (int ct = 0; ct < foodEvents.size(); ct++) {
            if (foodEvents.get(ct).getId().equals(foodEvent.getId())) {
                remove = ct;
            }
        }

        if (remove == -1) {return;}
        foodEvents.remove(remove);
        foodEvents.add(foodEvent);

        if (!suppressSave) {
            this.saveFoodEvents();
        }
    }

    private void saveFoodEvents() {
        SharedPreferences  mPrefs            = activity.getPreferences(Activity.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson                            = new Gson();
        String json                          = gson.toJson(foodEvents);
        prefsEditor.putString("LifeGoalsFoodEvents", json);
        prefsEditor.commit();

        for (MealManagerListener hl : listeners)
            hl.foodEventsUpdated();
    }

    public ArrayList<FoodEvent> getFoodEvents() {
        return foodEvents;
    }

    public void deleteFoodEvent(FoodEvent selectedFood) {
        int index = -1;
        for (int ct = 0; ct < foodEvents.size(); ct++) {
            if (foodEvents.get(ct).getId().equals(selectedFood.getId())) {
                index = ct;
            }
        }

        if (index == -1) {return;}

        foodEvents.remove(index);

        this.saveFoodEvents();
    }

    public void deleteIngredient(Ingredient selectedIngredient) {
        int index = -1;
        for (int ct = 0; ct < ingredients.size(); ct++) {
            if (ingredients.get(ct).getId().equals(selectedIngredient.getId())) {
                index = ct;
            }
        }

        if (index == -1) {return;}

        ingredients.remove(index);

        this.saveIngredients();
    }

    public int getFoodCountForDay(Date created) {
        int foodCount = 0;
        for (int ct = 0; ct < foodEvents.size(); ct++) {
            if (foodEvents.get(ct).getCreated().equals(created)) {
                foodCount++;
            }
        }

        return foodCount;
    }

    public int getFatPercentForDay(Date created) {
        int totalCalsForDay = getTotalCaloriesForDay(created);
        int totalFatCals    = 0;
        for (int ct = 0; ct < foodEvents.size(); ct++) {
            if (foodEvents.get(ct).getCreated().equals(created)) {
                totalFatCals += foodEvents.get(ct).getFatCals();
            }
        }

        return (int)(((float)totalFatCals/(float)totalCalsForDay)*100);
    }

    public int getCarbsPercentForDay(Date created) {
        int totalCalsForDay = getTotalCaloriesForDay(created);
        int totalCarbCals   = 0;
        for (int ct = 0; ct < foodEvents.size(); ct++) {
            if (foodEvents.get(ct).getCreated().equals(created)) {
                totalCarbCals += foodEvents.get(ct).getCarbCals();
            }
        }

        return (int)(((float)totalCarbCals/(float)totalCalsForDay)*100);
    }

    public int getProteinPercentForDay(Date created) {
        int totalCalsForDay  = getTotalCaloriesForDay(created);
        int totalProteinCals = 0;
        for (int ct = 0; ct < foodEvents.size(); ct++) {
            if (foodEvents.get(ct).getCreated().equals(created)) {
                totalProteinCals += foodEvents.get(ct).getProteinCals();
            }
        }

        return (int)(((float)totalProteinCals/(float)totalCalsForDay)*100);
    }

    public int getTotalCaloriesForDay(Date created) {
        int totalCals = 0;
        for (int ct = 0; ct < foodEvents.size(); ct++) {
            if (foodEvents.get(ct).getCreated().equals(created)) {
                totalCals += foodEvents.get(ct).getCalsEaten();
            }
        }

        return totalCals;
    }

    public int getTotalFoodGramsForDay(Date created) {
        int foodGrams = 0;
        for (int ct = 0; ct < foodEvents.size(); ct++) {
            if (foodEvents.get(ct).getCreated().equals(created)) {
                foodGrams += foodEvents.get(ct).getAmount();
            }
        }

        return foodGrams;
    }
}