package bblazer.com.lifegoals.Objects.Food;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import bblazer.com.lifegoals.R;

/**
 * Created by bblazer on 4/18/16.
 */
public class IngredientListAdapter extends BaseAdapter {
    private Context context;
    private Meal meal;
    private LayoutInflater mInflater;

    public IngredientListAdapter (Context context, Meal meal) {
        this.context = context;
        this.meal    = meal;
        mInflater    = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return meal.getIngredients().size();
    }

    @Override
    public Ingredient getItem(int position) {
        return meal.getIngredients().get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(meal.getIngredients().get(position).getId());
    }

    public class Holder
    {
        TextView brandName;
        TextView ingredientName;
        TextView fat;
        TextView carbs;
        TextView protein;
        TextView fiber;
        TextView fatCals;
        TextView carbsCals;
        TextView proteinCals;
        TextView totalCals;
        TextView fatPercent;
        TextView carbsPercent;
        TextView proteinPercent;
        TextView servingSize;
        TextView notes;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder     holder              = null;
        Ingredient ingredient          = getItem(position);
        if (convertView == null) {
            holder                = new Holder();
            convertView           = mInflater.inflate(R.layout.ingredient_row, null);
            holder.ingredientName = (TextView)convertView.findViewById(R.id.ingredient_name);
            holder.brandName      = (TextView)convertView.findViewById(R.id.ingredient_brand);
            holder.fat            = (TextView)convertView.findViewById(R.id.fat);
            holder.carbs          = (TextView)convertView.findViewById(R.id.carbs);
            holder.protein        = (TextView)convertView.findViewById(R.id.protein);
            holder.fiber          = (TextView)convertView.findViewById(R.id.fiber);
            holder.fatCals            = (TextView)convertView.findViewById(R.id.fat_cals);
            holder.carbsCals          = (TextView)convertView.findViewById(R.id.carbs_cals);
            holder.proteinCals        = (TextView)convertView.findViewById(R.id.protein_cals);
            holder.totalCals        = (TextView)convertView.findViewById(R.id.total_cals);
            holder.fatPercent            = (TextView)convertView.findViewById(R.id.fat_percent);
            holder.carbsPercent          = (TextView)convertView.findViewById(R.id.carbs_percent);
            holder.proteinPercent        = (TextView)convertView.findViewById(R.id.protein_percent);
            holder.servingSize    = (TextView)convertView.findViewById(R.id.serving_size_grams);
            holder.notes    = (TextView)convertView.findViewById(R.id.notes);
            convertView.setTag(holder);
        } else {
            holder = (Holder)convertView.getTag();
        }

        holder.ingredientName.setText(ingredient.getName());
        holder.brandName.setText(ingredient.getBrandName());

        holder.fat.setText("Fat: "+Integer.toString(ingredient.getFat())+"g");
        holder.carbs.setText("Carbs: "+Integer.toString(ingredient.getCarbs())+"g");
        holder.protein.setText("Protein: "+Integer.toString(ingredient.getProtein())+"g");
        holder.fiber.setText("Fiber: "+Integer.toString(ingredient.getFiber())+"g");

        holder.fatCals.setText(Integer.toString(ingredient.getFatCals())+" cals");
        holder.carbsCals.setText(Integer.toString(ingredient.getCarbCalories())+" cals");
        holder.proteinCals.setText(Integer.toString(ingredient.getProteinCals())+" cals");
        holder.totalCals.setText(Integer.toString(ingredient.getCals())+" tot. cals");

        holder.fatPercent.setText(Integer.toString(ingredient.getFatPercent())+"%");
        holder.carbsPercent.setText(Integer.toString(ingredient.getCarbsPercent())+"%");
        holder.proteinPercent.setText(Integer.toString(ingredient.getProteinPercent())+"%");
        holder.servingSize.setText("Serving: "+Integer.toString(ingredient.getServingGrams())+"g");

        holder.notes.setText("Notes: "+ingredient.getNotes());

        return convertView;
    }
}