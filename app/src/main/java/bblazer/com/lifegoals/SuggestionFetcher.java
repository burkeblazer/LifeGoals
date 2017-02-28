package bblazer.com.lifegoals;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Stack;

import bblazer.com.lifegoals.Objects.Food.Ingredient;

/**
 * Created by bblazer on 4/24/16.
 */
public class SuggestionFetcher extends AsyncTask<URL, Void, Stack<String>> {
    // suggestion titles will be saved here
    private Stack<String> suggestions;

    // this is the auto complete text view we will be handling
    private AutoCompleteTextView atv;
    // and it's adapter
    private ArrayAdapter<String> adapter;
    private AddFood addFood;
    private AddMeal addMeal;
    private AddIngredient addIngredient;
    // context of the activity or fragment

    private static final String TAG = "Suggestion Fetcher";

    public SuggestionFetcher(AutoCompleteTextView atv, AddFood addFood) {
        this.atv         = atv;
        this.suggestions = new Stack<String>();
        this.atv         = atv;
        this.addFood     = addFood;
    }

    public SuggestionFetcher(AutoCompleteTextView atv, AddMeal addMeal) {
        this.atv         = atv;
        this.suggestions = new Stack<String>();
        this.atv         = atv;
        this.addMeal     = addMeal;
    }

    public SuggestionFetcher(AutoCompleteTextView atv, AddIngredient addIngredient) {
        this.atv         = atv;
        this.suggestions = new Stack<String>();
        this.atv         = atv;
        this.addIngredient     = addIngredient;
    }

    @Override
    protected Stack<String> doInBackground(URL... params) {
        if (addFood != null) {
            addFood.autoCompleteIngredients = new ArrayList<Ingredient>();
        }
        if (addMeal != null) {
            addMeal.autoCompleteIngredients = new ArrayList<Ingredient>();
        }
        if (addIngredient != null) {
            addIngredient.autoCompleteIngredients = new ArrayList<Ingredient>();
        }
        URL url = params[0];
        HttpURLConnection urlConnection = null;
        String response = "";
        try {
            urlConnection = (HttpURLConnection) url
                    .openConnection();

            InputStream in = urlConnection.getInputStream();

            InputStreamReader isw = new InputStreamReader(in);

            BufferedReader buffer = new BufferedReader(
                    new InputStreamReader(in));
            String s = "";
            while ((s = buffer.readLine()) != null) {
                response += s;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
                setTitlesFromResponse(response);
            }
        }

        return this.suggestions;
    }

    private void setTitlesFromResponse(String response) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            JSONArray resultsArray = jsonObject.getJSONArray("hits");
            for (int ct = 0; ct < resultsArray.length(); ct++) {
                JSONObject fieldsObj = ((JSONObject) resultsArray.get(ct)).getJSONObject("fields");
                String brand = fieldsObj.getString("brand_name");
                String name  = fieldsObj.getString("item_name");
                String title = brand+" - "+name;
                if (addFood != null) {
                    addFood.autoCompleteIngredients.add(new Ingredient(fieldsObj));
                }
                if (addMeal != null) {
                    addMeal.autoCompleteIngredients.add(new Ingredient(fieldsObj));
                }
                if (addIngredient != null) {
                    addIngredient.autoCompleteIngredients.add(new Ingredient(fieldsObj));
                }
                this.suggestions.add(title);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Stack<String> strings) {
        super.onPostExecute(strings);
        if (addFood != null) {
            this.adapter = new ArrayAdapter<String>(addFood, android.R.layout.simple_list_item_1, strings);
        }
        if (addMeal != null) {
            this.adapter = new ArrayAdapter<String>(addMeal, android.R.layout.simple_list_item_1, strings);
        }
        if (addIngredient != null) {
            this.adapter = new ArrayAdapter<String>(addIngredient, android.R.layout.simple_list_item_1, strings);
        }
        this.atv.setAdapter(this.adapter);
        this.atv.invalidate();
        ((ArrayAdapter)this.atv.getAdapter()).notifyDataSetChanged();
        this.atv.showDropDown();
    }
}
