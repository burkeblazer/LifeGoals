package bblazer.com.lifegoals.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import bblazer.com.lifegoals.Add.AddMeal;
import bblazer.com.lifegoals.LifeGoalsApplication;
import bblazer.com.lifegoals.Manager.GoalManager;
import bblazer.com.lifegoals.Manager.MealManager;
import bblazer.com.lifegoals.Objects.Food.Meal.Meal;
import bblazer.com.lifegoals.Objects.Food.Meal.MealAdapter;
import bblazer.com.lifegoals.R;
import bblazer.com.lifegoals.Manager.WeightManager;

public class TimeToEatActivity extends AppCompatActivity {
    private Spinner     mealType;
    private ImageButton dropdownArrow;
    private TextView    daysCalsRemain;
    private EditText    exerciseRemain;
    private TextView    totalRemain;
    private EditText    mealsRemain;
    private TextView    calsPerMeal;
    private Button      pickForMe;
    private Button      createNew;
    private ListView    mealListView;
    private MealAdapter mealAdapter;
    private ArrayAdapter<String> mealTypeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_to_eat);

        mealType       = (Spinner)findViewById(R.id.meal_type);
        dropdownArrow  = (ImageButton)findViewById(R.id.dropdown_arrow);
        daysCalsRemain = (TextView)findViewById(R.id.days_cals_remain);
        exerciseRemain = (EditText)findViewById(R.id.exercise_remain);
        totalRemain    = (TextView)findViewById(R.id.total_remain);
        mealsRemain    = (EditText)findViewById(R.id.meals_remain);
        calsPerMeal    = (TextView)findViewById(R.id.cals_per_meal);
        pickForMe      = (Button)findViewById(R.id.pick_for_me);
        createNew      = (Button)findViewById(R.id.create_new);
        mealListView   = (ListView)findViewById(R.id.meals_listview);
        mealAdapter    = new MealAdapter(this, mealType);

        ArrayList<String> afStrings = new ArrayList<String>(){{
            this.add(MealManager.MT_BREAKFAST);
            this.add(MealManager.MT_LUNCH);
            this.add(MealManager.MT_DINNER);
            this.add(MealManager.MT_OTHER);
            this.add("All");
        }};
        mealTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, afStrings);
        mealTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealType.setAdapter(mealTypeAdapter);
        dropdownArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mealType.performClick();
            }
        });
        mealListView.setAdapter(mealAdapter);
        mealListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent                = new Intent(TimeToEatActivity.this, EatMealActivity.class);
                EatMealActivity.selectedMeal = (Meal)mealListView.getItemAtPosition(position);
                EatMealActivity.calsPerMeal  = Integer.parseInt(calsPerMeal.getText().toString());
                startActivity(intent);
            }
        });

        // Set textviews
        setTextViews();

        exerciseRemain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateTotalRemain();
                updateTotalTotalRemain();
            }
        });

        mealsRemain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateTotalTotalRemain();
            }
        });

        pickForMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = mealAdapter.getCount();
                if (count == 0) {return;}
                Random r      = new Random();
                int postition = r.nextInt(count);

                mealListView.performItemClick(
                        mealListView.getAdapter().getView(postition, null, null),
                        postition,
                        mealListView.getAdapter().getItemId(postition));
            }
        });

        createNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimeToEatActivity.this, AddMeal.class);
                startActivityForResult(intent, ADD_MEAL_INT);
            }
        });
    }

    private static final int ADD_MEAL_INT = 4321;@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mealAdapter.notifyDataSetChanged();
    }

    private void setTextViews() {
        WeightManager weightManager = ((LifeGoalsApplication)getApplication()).mainActivity.weightManager;
        GoalManager goalManager   = ((LifeGoalsApplication)getApplication()).mainActivity.goalManager;
        daysCalsRemain.setText(Integer.toString(weightManager.getCalsRemain()));

        exerciseRemain.setText(Integer.toString(goalManager.getCalsRemainingToday()));

        updateTotalRemain();

        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int mealsRemainInt = 3;
        if (hourOfDay >= 12 && hourOfDay <= 16) {
            mealsRemainInt = 2;
        }
        else if (hourOfDay >= 17) {
            mealsRemainInt = 1;
        }
        mealsRemain.setText(Integer.toString(mealsRemainInt));

        updateTotalTotalRemain();
    }

    private void updateTotalTotalRemain() {
        String mealsRemainStr      = mealsRemain.getText().toString();
        int    mealsRemainInt      = 1;
        if (!mealsRemainStr.equals("")) {
            mealsRemainInt         = Integer.parseInt(mealsRemainStr);
        }
        if (mealsRemainInt == 0) {mealsRemainInt = 1;}

        String totalRemainStr      = totalRemain.getText().toString();
        int    totalRemainInt      = 0;
        if (!totalRemainStr.equals("")) {
            totalRemainInt         = Integer.parseInt(totalRemainStr);
        }

        int totalTotalRemainInt = totalRemainInt/mealsRemainInt;
        calsPerMeal.setText(Integer.toString(totalTotalRemainInt));
    }

    private void updateTotalRemain() {
        int daysCalsRemainInt = 0;
        if (!daysCalsRemain.getText().toString().equals("")) {daysCalsRemainInt = Integer.parseInt(daysCalsRemain.getText().toString());}
        int dayCalsRemain  = daysCalsRemainInt;
        int exCalsRemainInt = 0;
        if (!exerciseRemain.getText().toString().equals("")) {exCalsRemainInt = Integer.parseInt(exerciseRemain.getText().toString());}
        int exCalsRemain  = exCalsRemainInt;
        int totalRemainInt = dayCalsRemain+exCalsRemain;
        totalRemain.setText(Integer.toString(totalRemainInt));
    }
}
