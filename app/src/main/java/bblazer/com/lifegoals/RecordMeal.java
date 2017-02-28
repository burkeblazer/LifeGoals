package bblazer.com.lifegoals;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import bblazer.com.lifegoals.Objects.Food.FoodEvent;
import bblazer.com.lifegoals.Objects.Food.Ingredient;
import bblazer.com.lifegoals.Objects.Food.Meal;

public class RecordMeal extends AppCompatActivity {
    private Spinner     mealDropdown;
    private ArrayAdapter mealAdapter;
    private Spinner     ingredientDropdown;
    private ArrayAdapter ingredientAdapter;
    private ImageButton mealsDropdownArrow;
    private ImageButton ingredientsDropdownArrow;
    private EditText    amount;
    private Button      foodDate;
    private Calendar    myCalendar = Calendar.getInstance();
    private ArrayList<Ingredient> currentIngredients;
    private int tempPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_meal);
        getSupportActionBar().setTitle("Record Meal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mealDropdown             = (Spinner)findViewById(R.id.meal_dropdown);
        ingredientDropdown       = (Spinner)findViewById(R.id.ingredients_dropdown);
        mealsDropdownArrow       = (ImageButton)findViewById(R.id.meals_dropdown_arrow);
        ingredientsDropdownArrow = (ImageButton)findViewById(R.id.ingredients_dropdown_arrow);
        amount                   = (EditText)findViewById(R.id.amount);
        foodDate                 = (Button)findViewById(R.id.food_date);

        mealsDropdownArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mealDropdown.performClick();
            }
        });

        ingredientsDropdownArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingredientDropdown.performClick();
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
                new DatePickerDialog(RecordMeal.this, eventDateListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        MealManager mealManager  = ((LifeGoalsApplication)getApplication()).mainActivity.mealManager;

        ArrayList<String> mealStrings = new ArrayList<String>();
        final ArrayList<Meal> meals   = mealManager.getMeals();
        for (int ct = 0; ct < meals.size(); ct++) {
            mealStrings.add(meals.get(ct).getName());
        }
        mealAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mealStrings);
        mealAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealDropdown.setAdapter(mealAdapter);
        mealDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectMeal(meals.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        amount.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (currentIngredients == null || tempPosition == -1) {
                    return;
                }

                if (amount.getText().toString().equals("")) {
                    currentIngredients.get(tempPosition).tempAmount = 0;
                }
                else {
                    currentIngredients.get(tempPosition).tempAmount = Integer.parseInt(amount.getText().toString());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.record_new_meal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.record_meal:
                for (int ct = 0; ct < currentIngredients.size(); ct++) {
                    FoodEvent foodEvent = new FoodEvent();

                    foodEvent.setAmount(currentIngredients.get(ct).tempAmount);
                    foodEvent.setName(currentIngredients.get(ct).getName());
                    foodEvent.setCalories(currentIngredients.get(ct).getCals());
                    foodEvent.setCarbs(currentIngredients.get(ct).getCarbs());

                    String foodDateStr = foodDate.getText().toString();
                    DateFormat ddf     = new SimpleDateFormat("MM/dd/yy", Locale.US);
                    try {
                        foodEvent.setCreated(ddf.parse(foodDateStr));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    foodEvent.setFat(currentIngredients.get(ct).getFat());
                    foodEvent.setFiber(currentIngredients.get(ct).getFiber());
                    foodEvent.setProtein(currentIngredients.get(ct).getProtein());
                    foodEvent.setServingSize(currentIngredients.get(ct).getServingGrams());
                    foodEvent.setType("Manual");

                    ((MealManager)((LifeGoalsApplication)getApplication()).mainActivity.mealManager).addFoodEvent(foodEvent);
                }

                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateFoodDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        foodDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void selectMeal(Meal meal) {
        tempPosition = -1;
        ArrayList<String> ingredientStrings = new ArrayList<String>();
        currentIngredients   = meal.getIngredients();
        for (int ct = 0; ct < currentIngredients.size(); ct++) {
            ingredientStrings.add(currentIngredients.get(ct).getName());
        }
        ingredientAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ingredientStrings);
        ingredientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ingredientDropdown.setAdapter(ingredientAdapter);
        ingredientDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tempPosition = position;
                selectIngredient(currentIngredients.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void selectIngredient(Ingredient ingredient) {
        amount.setText(Integer.toString(ingredient.tempAmount));
    }
}
