package bblazer.com.lifegoals.Activity;

import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import bblazer.com.lifegoals.LifeGoalsApplication;
import bblazer.com.lifegoals.Objects.Food.Ingredient.Ingredient;
import bblazer.com.lifegoals.Objects.Food.MacroRatio;
import bblazer.com.lifegoals.Objects.Food.Meal.Meal;
import bblazer.com.lifegoals.R;
import bblazer.com.lifegoals.Manager.WeightManager;

/**
 * Created by bblazer on 4/20/16.
 */
public class EatMealActivity  extends AppCompatActivity {
    public static Meal       selectedMeal;
    public static int        calsPerMeal;
    public static MacroRatio macroRatio;
    private LinearLayout     ingredientContainer;
    private TextView         calorieTarget;
    private TextView         macrioRatioType;
    private PieChart         currentRatio;
    private PieChart         weekRatio;
    private PieChart         currentProgressRatio;
    private ProgressBar      progress;
    private TextView         progressText;
    private RelativeLayout   emptyView;
    private ImageView        appIcon;
    private Button           autoTest;
    private LinearLayout     autoProgressContainer;
    private ProgressBar      autoProgress;
    private TextView         autoProgressTV;
    ArrayList<Ingredient>    ingredients;
    private WeightManager weightManager;
    private ArrayList<View>  ingredientViews = new ArrayList<View>();
    private boolean          changedET = false;
    private boolean          changedSB = false;
    private int              totalCurrentFatCals;
    private int              totalCurrentCarbsCals;
    private int              totalCurrentProteinCals;
    private int              totalCurrentCals;
    private Thread           thread;

    private int progressNum;
    private int progressMax;
    public static ArrayList<Integer> bestSizes = new ArrayList<Integer>();
    float fatPercent;
    float carbPercent;
    float proteinPercent;
    private float bestFatAccuracy     = 1000;
    private float bestCarbAccuracy    = 1000;
    private float bestProteinAccuracy = 1000;
    private float bestCalAccuracy     = 1000;
    private double[] newVals;
    private boolean valid = true;
    private ArrayList<Ingredient> nonStaticIngredients = new ArrayList<Ingredient>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eat_meal);

        weightManager           = ((LifeGoalsApplication)getApplication()).mainActivity.weightManager;
        totalCurrentFatCals     = weightManager.getCurrentFatCals();
        totalCurrentCarbsCals   = weightManager.getCurrentCarbCals();
        totalCurrentProteinCals = weightManager.getCurrentProteinCals();
        totalCurrentCals        = weightManager.getCurrentTotalCals();
        macroRatio              = weightManager.currentWeek.getCurrentMacroRatio();
        ingredients             = selectedMeal.getIngredients();
        fatPercent              = macroRatio.getFatPercent();
        carbPercent             = macroRatio.getCarbsPercent();
        proteinPercent          = macroRatio.getProteinPercent();
        emptyView               = (RelativeLayout)findViewById(R.id.empty_view);
        appIcon                 = (ImageView)findViewById(R.id.app_icon);
        ingredientContainer     = (LinearLayout)findViewById(R.id.ingredient_container);
        calorieTarget           = (TextView)findViewById(R.id.calorie_target);
        currentRatio            = (PieChart)findViewById(R.id.current_ratio);
        weekRatio               = (PieChart)findViewById(R.id.week_ratio);
        macrioRatioType         = (TextView)findViewById(R.id.macro_ratio_type);
        currentProgressRatio    = (PieChart)findViewById(R.id.current_progress_ratio);
        progress                = (ProgressBar)findViewById(R.id.progress);
        progressText            = (TextView)findViewById(R.id.progress_text);
        autoTest                = (Button)findViewById(R.id.auto_test);
        autoProgress            = (ProgressBar)findViewById(R.id.auto_progress);
        autoProgressTV          = (TextView) findViewById(R.id.auto_progress_tv);
        autoProgressContainer   = (LinearLayout)findViewById(R.id.auto_progress_container);

        autoTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (autoTest.getText().toString().equals("Cancel")) {
                    thread.interrupt();
                    autoProgressContainer.setVisibility(View.GONE);
                    autoTest.setText("Auto");
                }
                else {
                    autoTest.setText("Cancel");
                    startTestIngredients();
                }
            }
        });

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);  //0 means grayscale
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        appIcon.setColorFilter(cf);
        appIcon.setAlpha(128);

        progress.setProgress(0);
        progress.setMax(calsPerMeal);
        progressText.setText("0");

        calorieTarget.setText(Integer.toString(calsPerMeal));
        macrioRatioType.setText(macroRatio.getName()+":");

        setWeekRatio();
        setCurrentRatio();

        // Add ingredients
        addIngredients();

        // Set listeners
        setListeners();

        // Set max grams
        setMaxGrams();

        // Update seekbars
        updateSBs();
    }

    private void updateSBs() {
        for (int ct = 0; ct < ingredientViews.size(); ct++) {
            Ingredient ingredient = ingredients.get(ct);
            View currentView = ingredientViews.get(ct);
            final SeekBar sb = (SeekBar) currentView.findViewById(R.id.seekbar);
            sb.setMax(ingredient.maxTest);
        }
    }

    private void setMaxGrams() {
        for (int ct = 0; ct < ingredients.size(); ct++) {
            Ingredient ingredient = ingredients.get(ct);
            float highest      = ingredient.getHighestGramsPerCals(calsPerMeal);
            final EditText max = (EditText) ((View)ingredientViews.get(ct)).findViewById(R.id.max);
            max.setText(Integer.toString((int)highest));
        }
    }

    private void setListeners() {
        for (int ct = 0; ct < ingredientViews.size(); ct++) {
            View currentView   = ingredientViews.get(ct);
            TextView tv        = (TextView)currentView.findViewById(R.id.ingredient_name);
            final SeekBar  sb  = (SeekBar)currentView.findViewById(R.id.seekbar);
            final EditText et  = (EditText)currentView.findViewById(R.id.amount);
            final EditText max = (EditText)currentView.findViewById(R.id.max);
            final CheckBox chk = (CheckBox)currentView.findViewById(R.id.static_ingredient);

            final int finalCt = ct;
            max.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    int maxInt = 0;
                    if (!max.getText().toString().equals("")) {
                        maxInt = Integer.parseInt(max.getText().toString());
                    }
                    ingredients.get(finalCt).maxTest = maxInt;
                }
            });

            chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ingredients.get(finalCt).isStatic = chk.isChecked();
                }
            });

            tv.setText(ingredients.get(ct).getName());
            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (changedET) {changedET = false;return;}
                    changedSB = true;
                    et.setText(Integer.toString(sb.getProgress()));
                    updateIngredientsUI();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (changedSB) {changedSB = false;return;}
                    String progressAmountStr = et.getText().toString();
                    int progressAmount       = 0;
                    int ingredientMax        = Integer.parseInt(max.getText().toString());
                    if (!progressAmountStr.equals(""))  {progressAmount = Integer.parseInt(progressAmountStr);}
                    if (progressAmount > ingredientMax) {progressAmount = ingredientMax;}
                    changedET = true;
                    sb.setProgress(progressAmount);
                    updateIngredientsUI();
                }
            });

            et.setText(Integer.toString(ingredients.get(ct).getServingGrams()));
        }
    }

    private void updateIngredientsUI() {
        float totalFatCals     = totalCurrentFatCals;
        float totalCarbCals    = totalCurrentCarbsCals;
        float totalProteinCals = totalCurrentProteinCals;
        float totalCals        = totalCurrentCals;
        for (int ct = 0; ct < ingredientViews.size(); ct++) {
            EditText et = (EditText) ingredientViews.get(ct).findViewById(R.id.amount);
            final EditText max = (EditText)ingredientViews.get(ct).findViewById(R.id.max);
            String progressAmountStr = et.getText().toString();
            int ingredientMax        = Integer.parseInt(max.getText().toString());
            int progressAmount       = 0;
            if (!progressAmountStr.equals(""))  {progressAmount = Integer.parseInt(progressAmountStr);}
            if (progressAmount > ingredientMax) {progressAmount = ingredientMax;}
            totalFatCals += ingredients.get(ct).getFatCalsPerGram()*progressAmount;
            totalCarbCals += ingredients.get(ct).getCarbCalsPerGram()*progressAmount;
            totalProteinCals += ingredients.get(ct).getProteinCalsPerGram()*progressAmount;
            totalCals += ingredients.get(ct).getCalsPerGram()*progressAmount;
        }

        float fatPercent     = (totalFatCals/totalCals)*100;
        float carbPercent    = (totalCarbCals/totalCals)*100;
        float proteinPercent = (totalProteinCals/totalCals)*100;

        progress.setProgress((int)(totalCals - totalCurrentCals));
        progressText.setText(Integer.toString((int)(totalCals - totalCurrentCals)));
        updatePieChart(fatPercent, carbPercent, proteinPercent);
    }

    private void updatePieChart(float fatPercent, float carbPercent, float proteinPercent) {
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        yVals.add(new Entry(fatPercent,     0));
        yVals.add(new Entry(carbPercent,    1));
        yVals.add(new Entry(proteinPercent, 2));

        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("Fat");
        xVals.add("Carbs");
        xVals.add("Protein");

        // Create pie data set
        PieDataSet dataSet = new PieDataSet(yVals, "WeekRatio");

        // Add colors
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        dataSet.setColors(colors);

        // instantiate pie data object now
        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setHighlightEnabled(true);
        data.setValueTypeface(Typeface.DEFAULT_BOLD);
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);

        currentProgressRatio.setData(data);
        currentProgressRatio.setDescription("");
        currentProgressRatio.setHoleRadius(0f);
        currentProgressRatio.setTransparentCircleRadius(0f);

        // Undo all highlights
        currentProgressRatio.highlightValues(null);

        // Update pie chart
        currentProgressRatio.invalidate();
        currentProgressRatio.getLegend().setEnabled(false);
    }

    private void addIngredients() {
        View header = getLayoutInflater().inflate(R.layout.eat_meal_ingredient_header, null);
        ingredientContainer.addView(header);
        for (int ct = 0; ct < ingredients.size(); ct++) {
            View child = getLayoutInflater().inflate(R.layout.eat_meal_ingredient_row, null);
            ingredientViews.add(child);
            ingredientContainer.addView(child);
        }
    }

    private void setCurrentRatio() {
        MacroRatio currentMacroRatio = weightManager.getCurrentMacroRatio();
        float currentFatPercent      = currentMacroRatio.getFatPercent();
        float currentCarbPercent     = currentMacroRatio.getCarbsPercent();
        float currentProteinPercent  = currentMacroRatio.getProteinPercent();

        if (currentCarbPercent == 0.0 && currentFatPercent == 0.0 && currentProteinPercent == 0.0) {
            emptyView.setVisibility(View.VISIBLE);
            currentRatio.setVisibility(View.GONE);
            return;
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();
        yVals.add(new Entry(currentFatPercent,     0));
        yVals.add(new Entry(currentCarbPercent,    1));
        yVals.add(new Entry(currentProteinPercent, 2));

        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("Fat");
        xVals.add("Carbs");
        xVals.add("Protein");

        // Create pie data set
        PieDataSet dataSet = new PieDataSet(yVals, "WeekRatio");

        // Add colors
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        dataSet.setColors(colors);

        // instantiate pie data object now
        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setHighlightEnabled(true);
        data.setValueTypeface(Typeface.DEFAULT_BOLD);
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);

        currentRatio.setData(data);
        currentRatio.setDescription("");
        currentRatio.setHoleRadius(0f);
        currentRatio.setTransparentCircleRadius(0f);

        // Undo all highlights
        currentRatio.highlightValues(null);

        // Update pie chart
        currentRatio.invalidate();
        currentRatio.getLegend().setEnabled(false);
    }

    private void setWeekRatio() {
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        yVals.add(new Entry(fatPercent,     0));
        yVals.add(new Entry(carbPercent,    1));
        yVals.add(new Entry(proteinPercent, 2));

        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("Fat");
        xVals.add("Carbs");
        xVals.add("Protein");

        // Create pie data set
        PieDataSet dataSet = new PieDataSet(yVals, "WeekRatio");
        dataSet.setSelectionShift(0);

        // Add colors
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        dataSet.setColors(colors);

        // instantiate pie data object now
        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setHighlightEnabled(true);
        data.setValueTypeface(Typeface.DEFAULT_BOLD);
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);

        weekRatio.setData(data);

        weekRatio.setData(data);
        weekRatio.setDescription("");
        weekRatio.setHoleRadius(0f);
        weekRatio.setTransparentCircleRadius(0f);

        // Undo all highlights
        weekRatio.highlightValues(null);

        // Update pie chart
        weekRatio.invalidate();
        weekRatio.getLegend().setEnabled(false);
    }

    private void makeAtt(double min, double max, double sz, int depth, double[] vals){
        if (thread.isInterrupted()) {return;}
        //Guard against bad depth
        if (depth < 0) return;
        if (depth == 0){
            valid = testSizes(vals);
            progressNum++;
            updateProgress();
        }
        else{
            for(double i = min; i < max; i += sz){
                newVals = new double[vals.length + 1];
                for(int z = 0; z < vals.length; z++) newVals[z] = vals[z];
                if (!valid) {
                    valid = true;
                    progressNum += max/10;
                    return;
                }
                newVals[vals.length] = i;
                makeAtt(min, max, sz, depth - 1, newVals);
            }
        }
    }

    int lastPercent = 0;
    private void updateProgress() {
        double percent = ((double)progressNum/(double)progressMax)*100;
        if (percent > 100) {percent = 100;}
        if ((int)percent == lastPercent) {return;}
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                double percent = ((double)progressNum/(double)progressMax)*100;
                if (percent > 100) {percent = 100;}
                lastPercent = (int)percent;
                autoProgress.setProgress((int)percent);
                autoProgressTV.setText(Integer.toString((int)percent)+"%");
            }
        });
    }

    public void startTestIngredients() {
        autoProgressContainer.setVisibility(View.VISIBLE);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    // Grab the non-static ingredients
                    // Let's define the highest grams for each ingredient and that will be our base
                    int max              = 0;
                    nonStaticIngredients = new ArrayList<Ingredient>();
                    for (int ct = 0; ct < ingredients.size(); ct++) {
                        Ingredient ingredient = ingredients.get(ct);
                        if (ingredient.isStatic == false) {
                            nonStaticIngredients.add(ingredient);
                        }
                        if (ingredient.maxTest > max) {
                            max = ingredient.maxTest;
                        }
                    }

                    progressMax = (int) Math.pow(max / 10, nonStaticIngredients.size());
                    bestSizes   = new ArrayList<Integer>();

                    bestFatAccuracy     = 1000;
                    bestCarbAccuracy    = 1000;
                    bestProteinAccuracy = 1000;
                    bestCalAccuracy     = 1000;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        autoProgress.setMax(100);
                        autoProgress.setProgress(0);
                        autoProgressTV.setText("0%");
                        }
                    });

                    progressNum = 0;
                    newVals     = null;
                    makeAtt(10, max, 10, nonStaticIngredients.size(), new double[0]);

                    if (thread.isInterrupted()) {return;}

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        autoProgressContainer.setVisibility(View.GONE);
                        autoTest.setText("Auto");
                        }
                    });

                    if (bestSizes.size() == 0) {
                        return;
                    }

                    int skipped = 0;
                    for (int ct2 = 0; ct2 < ingredients.size(); ct2++) {
                        final Ingredient ingredient = ingredients.get(ct2);
                        if (ingredient.isStatic) {skipped++;continue;}
                        final Integer size = bestSizes.get(ct2-skipped);
                        final TextView et  = (TextView) ingredientViews.get(ct2).findViewById(R.id.amount);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                et.setText(Integer.toString(size));
                            }
                        });
                    }

                    Thread.currentThread().interrupt();
                }
            }
        });

        thread.start();
    }

    public boolean testSizes(double[] sizes) {
        // Set their test sizes
        for (int ct = 0;  ct < nonStaticIngredients.size(); ct++) {
            Ingredient ingredient = nonStaticIngredients.get(ct);
            if (ingredient.maxTest < (int)sizes[ct]) {
                ingredient.testSize = ingredient.maxTest;
            }
            else {
                ingredient.testSize = (int) sizes[ct];
            }
        }

        // Test them all together
        return testIngredientsSizes();
    }

    public boolean testIngredientsSizes() {
        // Loop through the ingredients add up all their fat cals, protein cals, carb cals, and total cals
        // See how closely those totals match the macro ratio and also the calLimit
        float totalFatCals     = totalCurrentFatCals;
        float totalCarbCals    = totalCurrentCarbsCals;
        float totalProteinCals = totalCurrentProteinCals;
        float totalCals        = totalCurrentCals;
        ArrayList<Integer> sizes = new ArrayList<Integer>();
        for (int ct = 0; ct < nonStaticIngredients.size(); ct++) {
            Ingredient ingredient = nonStaticIngredients.get(ct);
            totalFatCals          += ingredient.getFatCalsPerGram()*ingredient.testSize;
            totalCarbCals         += ingredient.getCarbCalsPerGram()*ingredient.testSize;
            totalProteinCals      += ingredient.getProteinCalsPerGram()*ingredient.testSize;
            totalCals             += ingredient.totalCalsPerGram()*ingredient.testSize;
            sizes.add(nonStaticIngredients.get(ct).testSize);
        }

        float currentFatPercent     = (totalFatCals/totalCals)*100;
        float currentCarbPercent    = (totalCarbCals/totalCals)*100;
        float currentProteinPercent = (totalProteinCals/totalCals)*100;

        // Accuracy is percent when it's 100% over 100% means it is above the macro and under 100% means it's below
        float fatAccuracy     = Math.abs(100 - (currentFatPercent/fatPercent)*100);
        float carbAccuracy    = Math.abs(100 - (currentCarbPercent/carbPercent)*100);
        float proteinAccuracy = Math.abs(100 - (currentProteinPercent/proteinPercent)*100);

        float calAccuracy     = Math.abs(100 - ((totalCals - totalCurrentCals)/calsPerMeal)*100);

        if ((totalCals - totalCurrentCals) > calsPerMeal) {
            return false;
        }

        if (fatAccuracy     < bestFatAccuracy     &&
            carbAccuracy    < bestCarbAccuracy    &&
            proteinAccuracy < bestProteinAccuracy &&
            calAccuracy     < bestCalAccuracy) {
            bestFatAccuracy     = fatAccuracy;
            bestCarbAccuracy    = carbAccuracy;
            bestProteinAccuracy = proteinAccuracy;
            bestCalAccuracy     = calAccuracy;
            bestSizes = sizes;
        }

        return true;
    }
}
