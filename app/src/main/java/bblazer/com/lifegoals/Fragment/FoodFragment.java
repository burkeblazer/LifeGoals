package bblazer.com.lifegoals.Fragment;

import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import bblazer.com.lifegoals.Activity.MainActivity;
import bblazer.com.lifegoals.Add.AddFood;
import bblazer.com.lifegoals.LifeGoalsApplication;
import bblazer.com.lifegoals.Manager.MealManagerListener;
import bblazer.com.lifegoals.Objects.Food.FoodEvent;
import bblazer.com.lifegoals.Objects.Food.FoodListAdapter;
import bblazer.com.lifegoals.R;

/**
 * Created by bblazer on 4/12/16.
 */
public class FoodFragment extends Fragment implements MealManagerListener {
    public  ListView       foodListView;
    private RelativeLayout emptyView;
    private ImageView      appIcon;
    public MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.food_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        foodListView = (ListView)view.findViewById(R.id.food_listview);
        emptyView    = (RelativeLayout)view.findViewById(R.id.empty_view);
        appIcon      = (ImageView)view.findViewById(R.id.app_icon);

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);  //0 means grayscale
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        appIcon.setColorFilter(cf);
        appIcon.setAlpha(128);
        mainActivity = ((LifeGoalsApplication)getActivity().getApplication()).mainActivity;
        mainActivity.mealManager.addListener(this);

        foodListView.setAdapter(new FoodListAdapter(mainActivity, mainActivity.mealManager));
        registerForContextMenu(foodListView);

        this.toggleGoalView();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.food_listview) {
            menu.add("Delete Food");
            menu.add("Edit Food");
        }
    }

    public void toggleGoalView() {
        if (mainActivity.mealManager.foodEvents.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            foodListView.setVisibility(View.GONE);
        }
        else {
            emptyView.setVisibility(View.GONE);
            foodListView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String menuItemString = (String)item.getTitle();
        if (menuItemString.equals("Delete Food")) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
            FoodEvent selectedFood = mainActivity.mealManager.foodEvents.get(info.position);
            mainActivity.mealManager.deleteFoodEvent(selectedFood);
        }
        else if (menuItemString.equals("Edit Food")) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
            FoodEvent selectedFood = mainActivity.mealManager.foodEvents.get(info.position);
            Intent intent = new Intent(mainActivity, AddFood.class);
            AddFood.editFood = selectedFood;
            startActivity(intent);
        }
        else {
            return false;
        }

        return true;
    }

    @Override
    public void mealsUpdated() {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toggleGoalView();
                ((FoodListAdapter)foodListView.getAdapter()).notifyDataSetChanged();
            }
        });
    }

    @Override
    public void ingredientsUpdated() {

    }

    @Override
    public void foodEventsUpdated() {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toggleGoalView();
                ((FoodListAdapter)foodListView.getAdapter()).notifyDataSetChanged();
            }
        });
    }
}