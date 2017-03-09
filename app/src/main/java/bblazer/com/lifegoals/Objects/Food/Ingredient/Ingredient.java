package bblazer.com.lifegoals.Objects.Food.Ingredient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import bblazer.com.lifegoals.Store.Department;

/**
 * Created by bblazer on 4/18/16.
 */
public class Ingredient {
    private String id;
    private String apiID;
    private String brandName;
    private String name;
    private Date created;
    private Date expirationDate;
    private int amount;
    private Department department;
    private int  carbs;
    private int  saturatedFat;
    private int  fat;
    private int  protein;
    private int  fiber;
    private int servingGrams;
    private String ingredientStr;
    private int calories;
    public transient int testSize;
    public transient int tempAmount;
    public transient int maxTest;
    public transient boolean isStatic = false;

    public Ingredient() {
        id = Long.toString(System.currentTimeMillis());
    }

    public Ingredient(JSONObject jObj) {
        id = Long.toString(System.currentTimeMillis());
        try {
            String brandName = jObj.getString("brand_name");
            String itemName  = jObj.getString("item_name");
            this.apiID       = jObj.getString("item_id");
            this.brandName   = brandName;
            name             = itemName;
            created          = new Date();
            carbs            = jObj.getInt("nf_total_carbohydrate");
            fat              = jObj.getInt("nf_total_fat");
            protein          = jObj.getInt("nf_protein");
            fiber            = jObj.getInt("nf_dietary_fiber");
            saturatedFat     = jObj.getInt("nf_saturated_fat");
            servingGrams     = jObj.getInt("nf_serving_weight_grams");
            ingredientStr    = jObj.getString("nf_ingredient_statement");
            calories         = jObj.getInt("nf_calories");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getFatCals () {
        return this.fat*9;
    }

    public int getProteinCals () {
        return this.protein*4;
    }

    public int getCarbCalories () {
        int proteinCals = this.protein*4;
        int fatCals     = this.fat*9;
        return this.calories - (proteinCals + fatCals);
    }

    public int getFatPercent() {
        return (int)((float)getFatCals() / (float)getCals() * 100);
    }

    public int getProteinPercent() {
        return (int)((float)getProteinCals() / (float)getCals() * 100);
    }

    public int getCarbsPercent() {
        return 100 - getProteinPercent() - getFatPercent();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public int getCalories() {
        return calories;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getCarbs() {
        return carbs;
    }

    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }

    public int getFat() {
        return fat;
    }

    public void setFat(int fat) {
        this.fat = fat;
    }

    public int getProtein() {
        return protein;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public int getFiber() {
        return fiber;
    }

    public void setFiber(int fiber) {
        this.fiber = fiber;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public int getSaturatedFat() {
        return saturatedFat;
    }

    public void setSaturatedFat(int saturatedFat) {
        this.saturatedFat = saturatedFat;
    }

    public int getServingGrams() {
        return servingGrams;
    }

    public void setServingGrams(int servingGrams) {
        this.servingGrams = servingGrams;
    }

    public String getIngredientStr() {
        return ingredientStr;
    }

    public void setIngredientStr(String ingredientStr) {
        this.ingredientStr = ingredientStr;
    }

    public String getNotes() {
        String notes = "";
        if (saturatedFat > fat*.4) {
            notes += "High in Saturated Fat.";
        }

        return notes;
    }

    public String getApiID() {
        return apiID;
    }

    public void setApiID(String apiID) {
        this.apiID = apiID;
    }

    public int getCals() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public float getFatCalsPerGram() {
        return (float)((float)getFatCals()/(float)getServingGrams());
    }

    public float getCarbCalsPerGram() {
        return (float)((float)getCarbCalories()/(float)getServingGrams());
    }

    public float getProteinCalsPerGram() {
        return (float)((float)getProteinCals()/(float)getServingGrams());
    }

    public float totalCalsPerGram() {
        return getFatCalsPerGram()+getCarbCalsPerGram()+getProteinCalsPerGram();
    }

    public float getHighestGramsPerCals(int maxCals) {
        float calsPerGram = totalCalsPerGram();
        return maxCals/calsPerGram;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    public float getCalsPerGram() {
        return getFatCalsPerGram()+getCarbCalsPerGram()+getProteinCalsPerGram();
    }
}

