package bblazer.com.lifegoals.Activity;

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

import bblazer.com.lifegoals.Add.AddMeal;
import bblazer.com.lifegoals.LifeGoalsApplication;
import bblazer.com.lifegoals.Objects.Food.Meal.Meal;
import bblazer.com.lifegoals.R;

public class MealCreator extends AppCompatActivity {
    private ListView mealCreatorListView;
    private ArrayAdapter mealAdapter;
    FloatingActionButton addMealButton;
    private RelativeLayout emptyView;
    private ImageView appIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_creator);

        mealCreatorListView = (ListView)findViewById(R.id.meal_list_view);
        emptyView           = (RelativeLayout)findViewById(R.id.empty_view);
        appIcon             = (ImageView)findViewById(R.id.app_icon);

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);  //0 means grayscale
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        appIcon.setColorFilter(cf);
        appIcon.setAlpha(128);

        setMealList();

        registerForContextMenu(mealCreatorListView);
        addMealButton = (FloatingActionButton)findViewById(R.id.add_meal);

        addMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MealCreator.this, AddMeal.class);
                startActivityForResult(intent, EDIT_MEAL);
            }
        });
    }

    private void setMealList() {
        ArrayList<String> mealStrings = new ArrayList<String>();
        ArrayList<Meal>   mealArray  = ((LifeGoalsApplication)getApplication()).mainActivity.mealManager.meals;
        for (Meal meal: mealArray) {
            mealStrings.add(meal.getName());
        }
        final ArrayList<String> finalMealStrings = mealStrings;
        mealAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, finalMealStrings);
        mealAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealCreatorListView.setAdapter(mealAdapter);

        this.toggleGoalView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setMealList();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.meal_list_view) {
            menu.add("Edit");
            menu.add("Delete");
        }
    }

    private static final int EDIT_MEAL = 1234;

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String menuItemString = (String)item.getTitle();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        Meal selectedMeal = ((LifeGoalsApplication)getApplication()).mainActivity.mealManager.meals.get(info.position);

        if (menuItemString.equals("Delete")) {
            ((LifeGoalsApplication)getApplication()).mainActivity.mealManager.deleteMeal(selectedMeal);
            setMealList();
        }
        else if (menuItemString.equals("Edit")) {
            Intent intent = new Intent(MealCreator.this, AddMeal.class);
            AddMeal.editMeal = selectedMeal;
            startActivityForResult(intent, EDIT_MEAL);
        }
        else {
            return false;
        }

        return true;
    }

    public void toggleGoalView() {
        if (((LifeGoalsApplication)getApplication()).mainActivity.mealManager.meals.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            mealCreatorListView.setVisibility(View.GONE);
        }
        else {
            emptyView.setVisibility(View.GONE);
            mealCreatorListView.setVisibility(View.VISIBLE);
        }
    }
}
