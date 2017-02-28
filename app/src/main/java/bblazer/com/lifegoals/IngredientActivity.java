package bblazer.com.lifegoals;

import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;

import bblazer.com.lifegoals.Objects.Food.Ingredient;

public class IngredientActivity extends AppCompatActivity {
    private ListView ingredientListView;
    private ArrayAdapter ingredientAdapter;
    FloatingActionButton addIngredientButton;
    private RelativeLayout emptyView;
    private ImageView appIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);

        ingredientListView = (ListView)findViewById(R.id.ingredient_list_view);
        emptyView          = (RelativeLayout)findViewById(R.id.empty_view);
        appIcon            = (ImageView)findViewById(R.id.app_icon);

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);  //0 means grayscale
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        appIcon.setColorFilter(cf);
        appIcon.setAlpha(128);

        setIngredientList();

        registerForContextMenu(ingredientListView);
        addIngredientButton = (FloatingActionButton)findViewById(R.id.add_ingredient);

        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IngredientActivity.this, AddIngredient.class);
                startActivityForResult(intent, EDIT_INGREDIENT);
            }
        });
    }

    private void setIngredientList() {
        ArrayList<String>    ingredientStrings = new ArrayList<String>();
        ArrayList<Ingredient>ingredientArray   = ((LifeGoalsApplication)getApplication()).mainActivity.mealManager.ingredients;
        for (Ingredient ingredient: ingredientArray) {
            ingredientStrings.add(ingredient.getName());
        }
        final ArrayList<String> finalIngredientStrings = ingredientStrings;
        ingredientAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, finalIngredientStrings);
        ingredientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ingredientListView.setAdapter(ingredientAdapter);

        this.toggleGoalView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setIngredientList();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.ingredient_list_view) {
            menu.add("Edit");
            menu.add("Delete");
        }
    }

    private static final int EDIT_INGREDIENT = 4321;

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String menuItemString = (String)item.getTitle();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        Ingredient selectedIngredient = ((LifeGoalsApplication)getApplication()).mainActivity.mealManager.ingredients.get(info.position);

        if (menuItemString.equals("Delete")) {
            ((LifeGoalsApplication)getApplication()).mainActivity.mealManager.deleteIngredient(selectedIngredient);
            setIngredientList();
        }
        else if (menuItemString.equals("Edit")) {
            Intent intent = new Intent(IngredientActivity.this, AddIngredient.class);
            AddIngredient.editIngredient = selectedIngredient;
            startActivityForResult(intent, EDIT_INGREDIENT);
        }
        else {
            return false;
        }

        return true;
    }

    public void toggleGoalView() {
        if (((LifeGoalsApplication)getApplication()).mainActivity.mealManager.ingredients.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            ingredientListView.setVisibility(View.GONE);
        }
        else {
            emptyView.setVisibility(View.GONE);
            ingredientListView.setVisibility(View.VISIBLE);
        }
    }
}
