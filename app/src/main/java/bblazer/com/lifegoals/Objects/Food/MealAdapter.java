package bblazer.com.lifegoals.Objects.Food;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import bblazer.com.lifegoals.LifeGoalsApplication;
import bblazer.com.lifegoals.MealManager;
import bblazer.com.lifegoals.R;

/**
 * Created by bblazer on 4/20/16.
 */
public class MealAdapter extends BaseAdapter {
    private Context        context;
    private Spinner        mealType;
    private LayoutInflater mInflater;
    private MealManager    mealManager;

    public MealAdapter (Context context, Spinner mealType) {
        this.context  = context;
        this.mealType = mealType;
        mInflater     = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mealManager   = ((LifeGoalsApplication)context.getApplicationContext()).mainActivity.mealManager;
        this.mealType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    private ArrayList<Meal> getFilteredMeals() {
        if (mealType.getSelectedItem() == null) {return new ArrayList<Meal>();}
        String          mealTypeStr   = mealType.getSelectedItem().toString();

        if (mealTypeStr.equals("All")) {
            return mealManager.getMeals();
        }

        ArrayList<Meal> filteredmeals = new ArrayList<Meal>();
        for (int ct = 0; ct < mealManager.getMeals().size(); ct++) {
            if (mealManager.getMeals().get(ct).getMealType().equals(mealTypeStr)) {filteredmeals.add(mealManager.getMeals().get(ct));}
        }

        return filteredmeals;
    }

    @Override
    public int getCount() {
        return getFilteredMeals().size();
    }

    @Override
    public Meal getItem(int position) {
        return getFilteredMeals().get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(getFilteredMeals().get(position).getId());
    }

    public class Holder
    {
        TextView     mealName;
//        LinearLayout ingredientsContainer;
//        TextView     totalFat;
//        TextView     totalCarbs;
//        TextView     totalProtein;
//        TextView     totalCals;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        Meal   meal   = getItem(position);
        if (convertView == null) {
            holder                      = new Holder();
            convertView                 = mInflater.inflate(R.layout.meal_row_new, null);
            holder.mealName             = (TextView)convertView.findViewById(R.id.meal_name);
//            holder.ingredientsContainer = (LinearLayout) convertView.findViewById(R.id.ingredients_container);
//            holder.totalFat             = (TextView)convertView.findViewById(R.id.total_fat);
//            holder.totalCarbs           = (TextView)convertView.findViewById(R.id.total_carbs);
//            holder.totalProtein         = (TextView)convertView.findViewById(R.id.total_protein);
//            holder.totalCals            = (TextView)convertView.findViewById(R.id.total_cals);
            convertView.setTag(holder);
        } else {
            holder = (Holder)convertView.getTag();
        }

        holder.mealName.setText(meal.getName());

//        ArrayList<Ingredient> ingredients = meal.getIngredients();
//        int totalWeight  = 0;
//        int totalFat     = 0;
//        int totalCarbs   = 0;
//        int totalProtein = 0;
//        int totalCals    = 0;
//        for (Ingredient ingredient : ingredients) {
//            LinearLayout row = new LinearLayout(context);
//            row.setOrientation(LinearLayout.VERTICAL);
//            LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            LLParams.setMargins(0, 20, 0, 0);
//            row.setLayoutParams(LLParams);
//
//            TextView ingredientName = new TextView(context);
//            ingredientName.setText(ingredient.getName());
//            ingredientName.setGravity(Gravity.CENTER_HORIZONTAL);
//            LinearLayout.LayoutParams nameTvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
//            ingredientName.setLayoutParams(nameTvParams);
//            row.addView(ingredientName);
//
//            LinearLayout container = new LinearLayout(context);
//            container.setOrientation(LinearLayout.HORIZONTAL);
//            LinearLayout.LayoutParams containerLLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            container.setLayoutParams(containerLLParams);
//
//            LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
//
//            TextView fat = new TextView(context);
//            fat.setText("Fat: "+Integer.toString(ingredient.getFatCals()));
//            fat.setGravity(Gravity.CENTER);
//            fat.setLayoutParams(tvParams);
//            totalFat += ingredient.getFatCals();
//
//            TextView carbs = new TextView(context);
//            carbs.setText("Carbs: "+Integer.toString(ingredient.getCarbCalories()));
//            carbs.setGravity(Gravity.CENTER);
//            carbs.setLayoutParams(tvParams);
//            totalCarbs += ingredient.getCarbCalories();
//
//            TextView protein = new TextView(context);
//            protein.setText("Protein: "+Integer.toString(ingredient.getProteinCals()));
//            protein.setGravity(Gravity.CENTER);
//            protein.setLayoutParams(tvParams);
//            totalProtein += ingredient.getProteinCals();
//
//            TextView total = new TextView(context);
//            total.setText("Total: "+Integer.toString(ingredient.getCals()));
//            total.setGravity(Gravity.CENTER);
//            total.setLayoutParams(tvParams);
//            totalCals += ingredient.getCals();
//
//            container.addView(fat);
//            container.addView(carbs);
//            container.addView(protein);
//            container.addView(total);
//            row.addView(container);
//
//            holder.ingredientsContainer.addView(row);
//        }
//
//        holder.totalFat.setText("Fat: "+Integer.toString(totalFat));
//        holder.totalCarbs.setText("Carbs: "+Integer.toString(totalCarbs));
//        holder.totalProtein.setText("Protein: "+Integer.toString(totalProtein));
//        holder.totalCals.setText("Total: "+Integer.toString(totalCals));

        return convertView;
    }
}
