package bblazer.com.lifegoals;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import bblazer.com.lifegoals.Objects.Food.Ingredient;

public class AddIngredient extends AppCompatActivity {
    public static Ingredient editIngredient;
    private Ingredient currentEditIngredient;
    private AutoCompleteTextView foodName;
    private EditText cals;
    private EditText fat;
    private EditText protein;
    private EditText fiber;
    private EditText carbs;
    private EditText servingSize;
    private ImageButton searchButton;
    private ImageButton searchBarCodeButton;
    public ArrayList<Ingredient> autoCompleteIngredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        if (editIngredient != null) {
            getSupportActionBar().setTitle("Edit Existing Ingredient");
        } else {
            getSupportActionBar().setTitle("Add New Ingredient");
        }

        if (this.editIngredient != null) {
            currentEditIngredient = editIngredient;
            editIngredient = null;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        foodName = (AutoCompleteTextView) findViewById(R.id.food_name);
        fat = (EditText) findViewById(R.id.fat);
        protein = (EditText) findViewById(R.id.protein);
        cals = (EditText) findViewById(R.id.calories);
        fiber = (EditText) findViewById(R.id.fiber);
        carbs = (EditText) findViewById(R.id.carbs);
        servingSize = (EditText) findViewById(R.id.serving_size);
        searchButton = (ImageButton) findViewById(R.id.ingredient_search_name);
        searchBarCodeButton = (ImageButton) findViewById(R.id.ingredient_search_bar_code);
        autoCompleteIngredients = new ArrayList<Ingredient>();
        searchBarCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intent = new IntentIntegrator(AddIngredient.this);
                intent.setCameraId(0);
                intent.setOrientationLocked(false);
                intent.setCaptureActivity(CaptureActivityPortrait.class);
                intent.initiateScan();
            }
        });

        if (this.currentEditIngredient != null) {
            this.loadForm();
        }

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SuggestionFetcher fetcher = new SuggestionFetcher(foodName, AddIngredient.this);
                String search = foodName.getText().toString();
                try {
                    URL url = new URL("https://api.nutritionix.com/v1_1/search/" + search + "?results=0%3A20&cal_min=0&cal_max=50000&fields=*&appId=661a45d3&appKey=a19fd27abad16747b447cea8dcf9b41a");
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
                String jsonInfo = ((LifeGoalsApplication) getApplication()).mainActivity.getFoodFromUPC(upc);
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
        foodName.setText(currentEditIngredient.getName());
        fat.setText(Float.toString(currentEditIngredient.getFat()));
        protein.setText(Float.toString(currentEditIngredient.getProtein()));
        carbs.setText(Float.toString(currentEditIngredient.getCarbs()));
        fiber.setText(Float.toString(currentEditIngredient.getFiber()));
        cals.setText(Integer.toString(currentEditIngredient.getCals()));
        servingSize.setText(Integer.toString(currentEditIngredient.getServingGrams()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_new_ingredient, menu);
        MenuItem addGoalItem = menu.findItem(R.id.add_ingredient);
        if (currentEditIngredient != null) {
            addGoalItem.setTitle("Update Ingredient");
        } else {
            addGoalItem.setTitle("Add Ingredient");
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
                Ingredient ingredient = this.getIngredient();

                if (currentEditIngredient != null) {
                    ingredient.setId(currentEditIngredient.getId());
                }

                ((MealManager) ((LifeGoalsApplication) getApplication()).mainActivity.mealManager).addIngredient(ingredient);

                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Ingredient getIngredient() {
        Ingredient ingredient = new Ingredient();

        ingredient.setName(foodName.getText().toString());
        ingredient.setFiber(Integer.parseInt(fiber.getText().toString()));
        ingredient.setProtein(Integer.parseInt(protein.getText().toString()));
        ingredient.setFat(Integer.parseInt(fat.getText().toString()));
        ingredient.setCarbs(Integer.parseInt(carbs.getText().toString()));
        ingredient.setCalories(Integer.parseInt(cals.getText().toString()));
        ingredient.setServingGrams(Integer.parseInt(servingSize.getText().toString()));

        return ingredient;
    }
}