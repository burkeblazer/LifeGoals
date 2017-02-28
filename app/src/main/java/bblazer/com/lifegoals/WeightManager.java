package bblazer.com.lifegoals;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import bblazer.com.lifegoals.Objects.Event.Event;
import bblazer.com.lifegoals.Objects.Food.FoodEvent;
import bblazer.com.lifegoals.Objects.Food.MacroRatio;

/**
 * Created by bblazer on 4/20/16.
 */
public class WeightManager {
    MainActivity   activity;
    ArrayList<WeightLossWeek> weeks = new ArrayList<WeightLossWeek>();
    WeightLossWeek currentWeek;

    public WeightManager(MainActivity activity) {
        this.activity = activity;

        // Try to deserialize any events we have stored
        Gson gson                = new Gson();
        SharedPreferences mPrefs = activity.getPreferences(Activity.MODE_PRIVATE);
        String json              = mPrefs.getString("LifeGoalsWeightWeeks", "");

        // There's nothing ouch
        if (json.equals("")) {checkWeek();return;}

        // Deserialize
        weeks = gson.fromJson(json, new TypeToken<ArrayList<WeightLossWeek>>(){}.getType());

        checkWeek();
    }

    private void checkWeek() {
        // Loop through weeks and see if we have a week going for this week
        WeightLossWeek current = null;
        Calendar sDateCalendar = Calendar.getInstance();
        int week = sDateCalendar.get(Calendar.WEEK_OF_YEAR);
        int year = sDateCalendar.get(Calendar.YEAR);
        String key = Integer.toString(year)+"_"+Integer.toString(week);
        for (int ct = 0; ct < weeks.size(); ct++) {
            if (key.equals(weeks.get(ct).getWeekStr())) {
                current = weeks.get(ct);
            }
        }

        if (current != null) {currentWeek = current;return;}

        setCurrentWeek();
    }

    private void setCurrentWeek() {
        Calendar sDateCalendar = Calendar.getInstance();
        int week = sDateCalendar.get(Calendar.WEEK_OF_YEAR);
        int year = sDateCalendar.get(Calendar.YEAR);
        String key = Integer.toString(year)+"_"+Integer.toString(week);
        currentWeek = new WeightLossWeek();
        currentWeek.setWeekStr(key);

        // Set proper macro
        // Get current
        if (weeks.size() > 0) {
            WeightLossWeek last = weeks.get(weeks.size() - 1);
            if (last != null) {
                if (last.getCurrentMacroRatio().getName().equals("Fasting week")) {
                    currentWeek.setCurrentMacroRatio(MacroRatio.HIGH_PROTEIN);
                }
                if (last.getCurrentMacroRatio().getName().equals("High Protein/LowCarb")) {
                    currentWeek.setCurrentMacroRatio(MacroRatio.HIGH_CARB);
                }
                if (last.getCurrentMacroRatio().getName().equals("High Carb/Low Protein")) {
                    currentWeek.setCurrentMacroRatio(MacroRatio.FEASTING);
                }
                if (last.getCurrentMacroRatio().getName().equals("Keto/Low Carb week/Feasting week")) {
                    currentWeek.setCurrentMacroRatio(MacroRatio.FASTING);
                }
            }
            else {
                currentWeek.setCurrentMacroRatio(MacroRatio.FASTING);
            }
        }
        else {
            currentWeek.setCurrentMacroRatio(MacroRatio.FASTING);
        }

        weeks.add(currentWeek);

        SharedPreferences  mPrefs            = activity.getPreferences(Activity.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson                            = new Gson();
        String json                          = gson.toJson(weeks);
        prefsEditor.putString("LifeGoalsWeightWeeks", json);
        prefsEditor.commit();
    }

    public void updateMacroRatio(String macroRatioName) {
        if (macroRatioName.equals("Fasting week")) {
            currentWeek.setCurrentMacroRatio(MacroRatio.FASTING);
        }
        if (macroRatioName.equals("High Protein/LowCarb")) {
            currentWeek.setCurrentMacroRatio(MacroRatio.HIGH_PROTEIN);
        }
        if (macroRatioName.equals("High Carb/Low Protein")) {
            currentWeek.setCurrentMacroRatio(MacroRatio.HIGH_CARB);
        }
        if (macroRatioName.equals("Keto/Low Carb week/Feasting week")) {
            currentWeek.setCurrentMacroRatio(MacroRatio.FEASTING);
        }

        weeks.remove(weeks.size()-1);
        weeks.add(currentWeek);

        SharedPreferences  mPrefs            = activity.getPreferences(Activity.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson                            = new Gson();
        String json                          = gson.toJson(weeks);
        prefsEditor.putString("LifeGoalsWeightWeeks", json);
        prefsEditor.commit();
    }

    public int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    public int getCalsRemain() {
        SharedPreferences settings = activity.getSharedPreferences("LifeGoals", 0);

        // Get current weight
        // Get goal weight
        // Get goal end date
        int calsForOneLb   = 3500;
        int currentWt      = settings.getInt("human_weight", 0);
        int goalWt         = Integer.parseInt(settings.getString("weight_loss_goal_weight", "0"));
        String goalEndDate = settings.getString("weight_loss_goal_date", "");
        Date goalDate      = new Date();
        DateFormat df      = new SimpleDateFormat("MM/dd/yy", Locale.US);
        try {
            goalDate       = df.parse(goalEndDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int days = daysBetween(new Date(), goalDate);
        if (days == 0) {days = 1;}
        int bmr            = activity.getBMR();
        int weightDiff     = currentWt - goalWt;
        float lbsPerDay    = (float)weightDiff / (float)days;
        float calsPerDay   = lbsPerDay*3500;
        float calsMinusBMR = (float)bmr - calsPerDay;

        // Now we can also add any exercise we've done today
        ArrayList<Event> todaysEvents = activity.eventManager.getTodaysEvents();
        int calsToday                 = 0;
        for (int ct = 0; ct < todaysEvents.size(); ct++) {
            calsToday += todaysEvents.get(ct).getCalsBurned();
        }
        int calsPlusExercise = (int)calsMinusBMR + calsToday;

        // Subtract any food we've already eaten today
        Calendar today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        ArrayList<FoodEvent>foodEvents = activity.mealManager.getFoodEvents();
        for (int ct = 0; ct < foodEvents.size(); ct++) {
            FoodEvent foodEvent = foodEvents.get(ct);
            if (foodEvent.getCreated().equals(today.getTime())) {
                // If it's from today subtract from our total
                float calsPerGram = (float)foodEvent.getCalories()/(float)foodEvent.getServingSize();
                float totalCals   = (float)foodEvent.getAmount()*calsPerGram;
                calsPlusExercise -= totalCals;
            }
        }

        return calsPlusExercise;
    }

    public MacroRatio getCurrentMacroRatio() {
        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        cal.setFirstDayOfWeek(Calendar.SUNDAY);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        ArrayList<FoodEvent>foodEvents = activity.mealManager.getFoodEvents();
        float totalFatCals     = 0;
        float totalCarbCals    = 0;
        float totalProteinCals = 0;
        float totalCals        = 0;
        for (int ct = 0; ct < foodEvents.size(); ct++) {
            FoodEvent foodEvent = foodEvents.get(ct);
            if (foodEvent.getCreated().after(cal.getTime()) || foodEvent.getCreated().equals(cal.getTime())) {
                totalFatCals     += foodEvent.getFatCals();
                totalCarbCals    += foodEvent.getCarbCals();
                totalProteinCals += foodEvent.getProteinCals();
                totalCals        += foodEvent.getCalsEaten();
            }
        }

        if (totalCals == 0) {return new MacroRatio("", 0, 0, 0);}
        float fatPercent     = (totalFatCals/totalCals)*100;
        float carbPercent    = (totalCarbCals/totalCals)*100;
        float proteinPercent = (totalProteinCals/totalCals)*100;
        MacroRatio returnMacroRatio = new MacroRatio("", fatPercent, carbPercent, proteinPercent);

        return returnMacroRatio;
    }

    public int getCurrentFatCals() {
        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        cal.setFirstDayOfWeek(Calendar.SUNDAY);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        ArrayList<FoodEvent>foodEvents = activity.mealManager.getFoodEvents();
        int totalFatCals     = 0;
        for (int ct = 0; ct < foodEvents.size(); ct++) {
            FoodEvent foodEvent = foodEvents.get(ct);
            if (foodEvent.getCreated().after(cal.getTime()) || foodEvent.getCreated().equals(cal.getTime())) {
                totalFatCals     += foodEvent.getFatCals();
            }
        }

        return totalFatCals;
    }

    public int getCurrentCarbCals() {
        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        cal.setFirstDayOfWeek(Calendar.SUNDAY);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        ArrayList<FoodEvent>foodEvents = activity.mealManager.getFoodEvents();
        int totalCarbCals     = 0;
        for (int ct = 0; ct < foodEvents.size(); ct++) {
            FoodEvent foodEvent = foodEvents.get(ct);
            if (foodEvent.getCreated().after(cal.getTime()) || foodEvent.getCreated().equals(cal.getTime())) {
                totalCarbCals     += foodEvent.getCarbCals();
            }
        }

        return totalCarbCals;
    }

    public int getCurrentProteinCals() {
        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        cal.setFirstDayOfWeek(Calendar.SUNDAY);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        ArrayList<FoodEvent>foodEvents = activity.mealManager.getFoodEvents();
        int totalProteinCals     = 0;
        for (int ct = 0; ct < foodEvents.size(); ct++) {
            FoodEvent foodEvent = foodEvents.get(ct);
            if (foodEvent.getCreated().after(cal.getTime()) || foodEvent.getCreated().equals(cal.getTime())) {
                totalProteinCals     += foodEvent.getProteinCals();
            }
        }

        return totalProteinCals;
    }

    public int getCurrentTotalCals() {
        return getCurrentFatCals()+getCurrentCarbCals()+getCurrentProteinCals();
    }
}
