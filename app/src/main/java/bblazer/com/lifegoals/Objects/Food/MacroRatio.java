package bblazer.com.lifegoals.Objects.Food;

/**
 * Created by bblazer on 4/18/16.
 */
public class MacroRatio {
    private String name;
    private float fatPercent;
    private float proteinPercent;
    private float carbsPercent;

    public static final MacroRatio FASTING      = new MacroRatio("Fasting week",                     25, 45, 30);
    public static final MacroRatio HIGH_PROTEIN = new MacroRatio("High Protein/LowCarb",             35, 25, 40);
    public static final MacroRatio HIGH_CARB    = new MacroRatio("High Carb/Low Protein",            15, 60, 25);
    public static final MacroRatio FEASTING     = new MacroRatio("Keto/Low Carb week/Feasting week", 45, 10, 45);

    public MacroRatio(String name, float fatPercent, float carbsPercent, float proteinPercent) {
        this.name = name;
        this.fatPercent = fatPercent;
        this.proteinPercent = proteinPercent;
        this.carbsPercent = carbsPercent;
    }

    public float getFatPercent() {
        return fatPercent;
    }

    public void setFatPercent(float fatPercent) {
        this.fatPercent = fatPercent;
    }

    public float getProteinPercent() {
        return proteinPercent;
    }

    public void setProteinPercent(float proteinPercent) {
        this.proteinPercent = proteinPercent;
    }

    public float getCarbsPercent() {
        return carbsPercent;
    }

    public void setCarbsPercent(float carbsPercent) {
        this.carbsPercent = carbsPercent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
