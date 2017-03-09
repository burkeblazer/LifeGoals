package bblazer.com.lifegoals.Objects.Food;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import bblazer.com.lifegoals.Manager.MealManager;
import bblazer.com.lifegoals.R;

/**
 * Created by bblazer on 4/9/16.
 */
public class FoodListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private MealManager mealManager = null;

    public FoodListAdapter(Context viewGoalActivity, MealManager mealManager) {
        mInflater        = (LayoutInflater)viewGoalActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mealManager = mealManager;
        context          = viewGoalActivity;
    }

    @Override
    public int getCount() {
        return mealManager.getFoodEvents().size();
    }

    @Override
    public FoodEvent getItem(int position) {
        return mealManager.getFoodEvents().get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class RowHolder
    {
        ImageView foodTypeImg;
        TextView  protein;
        TextView  fat;
        TextView  fiber;
        TextView  carbs;
        TextView  calories;
        TextView  date;
        TextView  foodName;
        TextView  dateText;
        TextView  foodCount;
        TextView  fatPercent;
        TextView  carbsPercent;
        TextView  proteinPercent;
        TextView  totalCalories;
        TextView  totalFoodGrams;
        LinearLayout headerContainer;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RowHolder holder      = null;
        FoodEvent foodEvent   = getItem(position);
        FoodEvent prevFE      = null;
        String prevDateString = null;
        DateFormat df         = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.US);
        if (position > 0) {
            prevFE            = getItem(position-1);
            prevDateString    = df.format(prevFE.getCreated());
        }
        String currentDateString = df.format(foodEvent.getCreated());
        if (convertView == null) {
            holder                 = new RowHolder();
            convertView            = mInflater.inflate(R.layout.food_row, null);
            holder.headerContainer = (LinearLayout)convertView.findViewById(R.id.header_container);
            holder.foodTypeImg     = (ImageView)convertView.findViewById(R.id.food_image);
            holder.dateText        = (TextView)convertView.findViewById(R.id.date_textview);
            holder.foodCount       = (TextView)convertView.findViewById(R.id.food_count);
            holder.fatPercent      = (TextView)convertView.findViewById(R.id.fat_percent);
            holder.carbsPercent    = (TextView)convertView.findViewById(R.id.carb_percent);
            holder.proteinPercent  = (TextView)convertView.findViewById(R.id.protein_percent);
            holder.totalCalories   = (TextView)convertView.findViewById(R.id.total_cals);
            holder.totalFoodGrams  = (TextView)convertView.findViewById(R.id.total_food_grams);
            holder.protein         = (TextView)convertView.findViewById(R.id.protein);
            holder.fat             = (TextView)convertView.findViewById(R.id.fat);
            holder.fiber           = (TextView)convertView.findViewById(R.id.fiber);
            holder.carbs           = (TextView)convertView.findViewById(R.id.carbs);
            holder.date            = (TextView)convertView.findViewById(R.id.food_date);
            holder.calories        = (TextView)convertView.findViewById(R.id.calories);
            holder.foodName        = (TextView)convertView.findViewById(R.id.food_name);
            convertView.setTag(holder);
        } else {
            holder = (RowHolder)convertView.getTag();
        }

        if (prevDateString == null || !prevDateString.equals(currentDateString)) {
            holder.headerContainer.setVisibility(View.VISIBLE);
            holder.dateText.setText(currentDateString);
            holder.foodCount.setText(mealManager.getFoodCountForDay(foodEvent.getCreated())+" Foods");
            holder.fatPercent.setText(mealManager.getFatPercentForDay(foodEvent.getCreated())+"% Fat");
            holder.carbsPercent.setText(mealManager.getCarbsPercentForDay(foodEvent.getCreated())+"% Carbs");
            holder.proteinPercent.setText(mealManager.getProteinPercentForDay(foodEvent.getCreated())+"% Protein");
            holder.totalCalories.setText(mealManager.getTotalCaloriesForDay(foodEvent.getCreated())+" cals");
            holder.totalFoodGrams.setText(mealManager.getTotalFoodGramsForDay(foodEvent.getCreated())+"g");
        }
        else {
            holder.headerContainer.setVisibility(View.GONE);
        }

        // Set icon
        holder.foodTypeImg.setBackgroundResource(R.drawable.manual_event);

        holder.protein.setText("Protein: "+Float.toString(foodEvent.getProtein())+"g");
        holder.fat.setText("Fat: "+Float.toString(foodEvent.getFat())+"g");
        holder.fiber.setText("Fiber: "+Float.toString(foodEvent.getFiber())+"g");
        holder.carbs.setText("Carbs: "+Float.toString(foodEvent.getCarbs())+"g");
        holder.calories.setText(Integer.toString((int)foodEvent.getCalsEaten())+" cals");
        holder.foodName.setText(foodEvent.getName());

        DateFormat ddf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        holder.date.setText(ddf.format(foodEvent.getCreated()));

        return convertView;
    }
}
