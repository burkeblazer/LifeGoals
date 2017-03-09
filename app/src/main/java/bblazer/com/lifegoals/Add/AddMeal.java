package bblazer.com.lifegoals.Add;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Stack;

import bblazer.com.lifegoals.Utility.CaptureActivityPortrait;
import bblazer.com.lifegoals.LifeGoalsApplication;
import bblazer.com.lifegoals.Manager.MealManager;
import bblazer.com.lifegoals.Objects.Food.Ingredient.Ingredient;
import bblazer.com.lifegoals.Objects.Food.Ingredient.IngredientListAdapter;
import bblazer.com.lifegoals.Objects.Food.Meal.Meal;
import bblazer.com.lifegoals.R;
import bblazer.com.lifegoals.Utility.SuggestionFetcher;

public class AddMeal extends AppCompatActivity {
    private ImageButton  barCodeScanner;
    private ListView     ingredientListView;
    public static Meal   editMeal;
    private Meal         meal;
    private PieChart     pieChart;
    private TextView     mealName;
    private Spinner      mealType;
    private ImageButton  dropdownArrow;
    private ArrayAdapter mealTypeAdapter;
    private AutoCompleteTextView autocomplete;
    private ImageButton ingredientSearchName;
    public ArrayList<Ingredient> autoCompleteIngredients;
    private ImageButton ingredientSearchIngredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        if (editMeal != null) {
            getSupportActionBar().setTitle("Edit Existing Meal");
        }
        else {
            getSupportActionBar().setTitle("Add New Meal");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        barCodeScanner = (ImageButton)findViewById(R.id.ingredient_search_bar_code);
        barCodeScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intent = new IntentIntegrator(AddMeal.this);
                intent.setCameraId(0);
                intent.setOrientationLocked(false);
                intent.setCaptureActivity(CaptureActivityPortrait.class);
                intent.initiateScan();
            }
        });

        ingredientSearchIngredients = (ImageButton)findViewById(R.id.ingredient_search_ingredients);
        ingredientListView = (ListView)findViewById(R.id.ingredients_listview);
        registerForContextMenu(ingredientListView);

        pieChart = (PieChart)findViewById(R.id.pie_chart);
        mealName = (TextView)findViewById(R.id.meal_name);
        mealType = (Spinner)findViewById(R.id.meal_type);
        dropdownArrow = (ImageButton) findViewById(R.id.dropdown_arrow);
        autoCompleteIngredients = new ArrayList<Ingredient>();

        ArrayList<String> afStrings = new ArrayList<String>(){{
            this.add(MealManager.MT_BREAKFAST);
            this.add(MealManager.MT_LUNCH);
            this.add(MealManager.MT_DINNER);
            this.add(MealManager.MT_OTHER);
        }};
        mealTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, afStrings);
        mealTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealType.setAdapter(mealTypeAdapter);
        dropdownArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mealType.performClick();
            }
        });
        mealType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                meal.setMealType(mealType.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        if (this.editMeal != null) {
            meal     = editMeal;
            mealName.setText(meal.getName());
            editMeal = null;
        }
        else {
            meal             = new Meal();
            meal.mealManager = ((LifeGoalsApplication)getApplication()).mainActivity.mealManager;
            meal.setId(Long.toString(System.currentTimeMillis()));
        }

        ingredientListView.setAdapter(new IngredientListAdapter(this, meal));

        autocomplete = (AutoCompleteTextView)findViewById(R.id.autocomplete);

        ingredientSearchIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Stack<String> strings = new Stack<String>();
                autoCompleteIngredients = ((LifeGoalsApplication)getApplication()).mainActivity.mealManager.ingredients;
                for (int ct = 0; ct < autoCompleteIngredients.size(); ct++) {
                    strings.add(autoCompleteIngredients.get(ct).getName());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddMeal.this, android.R.layout.simple_list_item_1, strings);
                autocomplete.setAdapter(adapter);
                autocomplete.invalidate();
                adapter.notifyDataSetChanged();
                autocomplete.showDropDown();
            }
        });

        ingredientSearchName = (ImageButton)findViewById(R.id.ingredient_search_name);
        ingredientSearchName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SuggestionFetcher fetcher = new SuggestionFetcher(autocomplete, AddMeal.this);
                String search = autocomplete.getText().toString();
                try {
                    URL url = new URL("https://api.nutritionix.com/v1_1/search/"+search+"?results=0%3A20&cal_min=0&cal_max=50000&fields=*&appId=661a45d3&appKey=a19fd27abad16747b447cea8dcf9b41a");
                    fetcher.execute(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

        autocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                autocomplete.setText("");
                meal.addIngredient(autoCompleteIngredients.get(pos));
                autoCompleteIngredients = new ArrayList<Ingredient>();
                calculatePieChart();
                ((IngredientListAdapter)ingredientListView.getAdapter()).notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_new_meal_menu, menu);
        MenuItem addMealItem = menu.findItem(R.id.add_meal);
        if (editMeal != null) {
            addMealItem.setTitle("Update Meal");
        } else {
            addMealItem.setTitle("Add Meal");
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.ingredients_listview) {
            menu.add("Delete");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String menuItemString = (String)item.getTitle();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        Ingredient selectedIngredient = meal.getIngredients().get(info.position);

        if (menuItemString.equals("Delete")) {
            meal.deleteIngredient(selectedIngredient.getId());
            ((IngredientListAdapter)ingredientListView.getAdapter()).notifyDataSetChanged();
            calculatePieChart();
        }
        else {
            return false;
        }

        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            loadUPC(scanResult.getContents().toString());
        }
    }

    private void calculatePieChart() {
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        yVals1.add(new Entry(meal.getFatPercent(),     0));
        yVals1.add(new Entry(meal.getCarbsPercent(),   1));
        yVals1.add(new Entry(meal.getProteinPercent(), 2));

        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("Fat");
        xVals.add("Carbs");
        xVals.add("Protein");

        // create pie data set
        PieDataSet dataSet = new PieDataSet(yVals1, "Macro Ratio");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

        // add many colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        // instantiate pie data object now
        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.GRAY);

        pieChart.setData(data);

        // undo all highlights
        pieChart.highlightValues(null);

        pieChart.setDescription("");

        // update pie chart
        pieChart.invalidate();
        pieChart.getLegend().setEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.add_meal:
                meal.setName(mealName.getText().toString());
                if (editMeal != null) {
                    ((LifeGoalsApplication)getApplication()).mainActivity.mealManager.editMeal(meal, meal.getId(), false);
                }
                else {
                    ((LifeGoalsApplication)getApplication()).mainActivity.mealManager.addMeal(meal, false);
                }

                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadUPC(final String upc) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
            String jsonInfo = ((LifeGoalsApplication)getApplication()).mainActivity.getFoodFromUPC(upc);
                try {
                    JSONObject jObj = new JSONObject(jsonInfo);
                    meal.addIngredient(jObj);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            calculatePieChart();
                            ((IngredientListAdapter)ingredientListView.getAdapter()).notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}
