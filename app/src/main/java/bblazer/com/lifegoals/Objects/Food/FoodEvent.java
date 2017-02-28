package bblazer.com.lifegoals.Objects.Food;

import java.util.Date;

/**
 * Created by bblazer on 4/17/16.
 */
public class FoodEvent {
    private String type;
    private String id;
    private String name;
    private Date   created;
    private int    calories;
    private float  carbs;
    private float  fat;
    private float  protein;
    private float  fiber;
    private int  servingSize;
    private int  amount;

    public FoodEvent() {
        this.id = Long.toString(System.currentTimeMillis());
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

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public float getCarbs() {
        return carbs;
    }

    public void setCarbs(float carbs) {
        this.carbs = carbs;
    }

    public float getFat() {
        return fat;
    }

    public void setFat(float fat) {
        this.fat = fat;
    }

    public float getProtein() {
        return protein;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    public float getFiber() {
        return fiber;
    }

    public void setFiber(float fiber) {
        this.fiber = fiber;
    }

    public String getType() {
        return type;
    }

    public int getServingSize() {
        return servingSize;
    }

    public void setServingSize(int servingSize) {
        this.servingSize = servingSize;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getFatCalsPerGram () {
        return (fat*9)/servingSize;
    }

    public float getProteinCalsPerGram () {
        return (protein*4)/servingSize;
    }

    public float getCarbCaloriesPerGram () {
        float proteinCals = this.protein*4;
        float fatCals     = this.fat*9;
        return (calories - (proteinCals + fatCals))/servingSize;
    }

    public float getFatCals() {
        return getFatCalsPerGram()*amount;
    }

    public float getCarbCals() {
        return getCarbCaloriesPerGram()*amount;
    }

    public float getProteinCals() {
        return getProteinCalsPerGram()*amount;
    }

    public float getCalsEaten() {
        return getFatCals()+getCarbCals()+getProteinCals();
    }
}
