package bblazer.com.lifegoals;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Stack;

import bblazer.com.lifegoals.Objects.Food.FoodEvent;
import bblazer.com.lifegoals.Objects.Food.Ingredient;

public class AddFood extends AppCompatActivity {
    public static FoodEvent editFood;
    private FoodEvent            currentEditFood;
    private AutoCompleteTextView foodName;
    private Button               foodDate;
    private EditText             cals;
    private EditText             fat;
    private EditText             protein;
    private EditText             fiber;
    private EditText             carbs;
    private EditText             servingSize;
    private EditText             amount;
    private ImageButton          searchButton;
    private ImageButton          searchBarCodeButton;
    public ArrayList<Ingredient> autoCompleteIngredients;
    private ImageButton ingredientSearchIngredients;

    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        if (editFood != null) {
            getSupportActionBar().setTitle("Edit Existing Food");
        }
        else {
            getSupportActionBar().setTitle("Add New Food");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        foodName            = (AutoCompleteTextView)findViewById(R.id.food_name);
        fat                 = (EditText)findViewById(R.id.fat);
        protein             = (EditText)findViewById(R.id.protein);
        cals                = (EditText)findViewById(R.id.calories);
        fiber               = (EditText)findViewById(R.id.fiber);
        carbs               = (EditText)findViewById(R.id.carbs);
        servingSize         = (EditText)findViewById(R.id.serving_size);
        amount              = (EditText)findViewById(R.id.amount_ate);
        foodDate            = (Button)findViewById(R.id.food_date);
        searchButton        = (ImageButton)findViewById(R.id.ingredient_search_name);
        searchBarCodeButton = (ImageButton)findViewById(R.id.ingredient_search_bar_code);
        ingredientSearchIngredients = (ImageButton)findViewById(R.id.ingredient_search_ingredients);
        autoCompleteIngredients = new ArrayList<Ingredient>();
        searchBarCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intent = new IntentIntegrator(AddFood.this);
                intent.setCameraId(0);
                intent.setOrientationLocked(false);
                intent.setCaptureActivity(CaptureActivityPortrait.class);
                intent.initiateScan();
            }
        });

        if (this.editFood != null) {
            currentEditFood = editFood;
            editFood = null;
        }

        ingredientSearchIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Stack<String> strings = new Stack<String>();
                autoCompleteIngredients = ((LifeGoalsApplication)getApplication()).mainActivity.mealManager.ingredients;
                for (int ct = 0; ct < autoCompleteIngredients.size(); ct++) {
                    strings.add(autoCompleteIngredients.get(ct).getName());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddFood.this, android.R.layout.simple_list_item_1, strings);
                foodName.setAdapter(adapter);
                foodName.invalidate();
                adapter.notifyDataSetChanged();
                foodName.showDropDown();
            }
        });

        DateFormat df        = new SimpleDateFormat("MM/dd/yy");
        foodDate.setText(df.format(new Date()));

        final DatePickerDialog.OnDateSetListener eventDateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateFoodDate();
            }
        };

        foodDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddFood.this, eventDateListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        if (this.currentEditFood != null) {
            this.loadForm();
            currentEditFood = null;
        }

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            SuggestionFetcher fetcher = new SuggestionFetcher(foodName, AddFood.this);
            String search             = foodName.getText().toString();
            try {
                URL url = new URL("https://api.nutritionix.com/v1_1/search/"+search+"?results=0%3A20&cal_min=0&cal_max=50000&fields=*&appId=661a45d3&appKey=a19fd27abad16747b447cea8dcf9b41a");
                fetcher.execute(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            }
        });

        foodName.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                foodName.setText("");
                loadIngredient(autoCompleteIngredients.get(pos));
                autoCompleteIngredients = new ArrayList<Ingredient>();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            loadUPC(scanResult.getContents().toString());
        }
    }

    private void loadUPC(final String upc) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String jsonInfo = ((LifeGoalsApplication)getApplication()).mainActivity.getFoodFromUPC(upc);
                try {
                    JSONObject jObj = new JSONObject(jsonInfo);
                    final Ingredient ingredient = new Ingredient(jObj);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadIngredient(ingredient);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private void loadIngredient(Ingredient ingredient) {
        foodName.setText(ingredient.getName());
        fat.setText(Float.toString(ingredient.getFat()));
        protein.setText(Float.toString(ingredient.getProtein()));
        carbs.setText(Float.toString(ingredient.getCarbs()));
        fiber.setText(Float.toString(ingredient.getFiber()));
        cals.setText(Integer.toString(ingredient.getCals()));
        servingSize.setText(Integer.toString(ingredient.getServingGrams()));
    }

    private void loadForm() {
        foodName.setText(currentEditFood.getName());
        fat.setText(Float.toString(currentEditFood.getFat()));
        protein.setText(Float.toString(currentEditFood.getProtein()));
        carbs.setText(Float.toString(currentEditFood.getCarbs()));
        fiber.setText(Float.toString(currentEditFood.getFiber()));
        cals.setText(Integer.toString(currentEditFood.getCalories()));
        servingSize.setText(Integer.toString(currentEditFood.getServingSize()));
        amount.setText(Integer.toString(currentEditFood.getAmount()));

        DateFormat df  = new SimpleDateFormat("MM/dd/yy");
        foodDate.setText(df.format(currentEditFood.getCreated()));
    }

    private void updateFoodDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        foodDate.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_new_food, menu);
        MenuItem addGoalItem = menu.findItem(R.id.add_food);
        if (currentEditFood != null) {
            addGoalItem.setTitle("Update Food");
        } else {
            addGoalItem.setTitle("Add Food");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.add_food:
                // Create a goal and add it to your goals and finish the app
                FoodEvent food = this.getFood();

                if (currentEditFood != null) {
                    food.setId(currentEditFood.getId());
                }

                ((MealManager)((LifeGoalsApplication)getApplication()).mainActivity.mealManager).addFoodEvent(food);

                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private FoodEvent getFood() {
        FoodEvent food = new FoodEvent();

        if (currentEditFood != null) {
            food.setType(currentEditFood.getType());
        }
        else {
            food.setType("Manual");
        }

        food.setName(foodName.getText().toString());
        food.setFiber(Float.parseFloat(fiber.getText().toString()));
        food.setProtein(Float.parseFloat(protein.getText().toString()));
        food.setFat(Float.parseFloat(fat.getText().toString()));
        food.setCarbs(Float.parseFloat(carbs.getText().toString()));
        food.setCalories(Integer.parseInt(cals.getText().toString()));
        food.setServingSize(Integer.parseInt(servingSize.getText().toString()));
        food.setAmount(Integer.parseInt(amount.getText().toString()));

        String foodDateStr = foodDate.getText().toString();
        DateFormat ddf     = new SimpleDateFormat("MM/dd/yy", Locale.US);
        try {
            food.setCreated(ddf.parse(foodDateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return food;
    }
}
