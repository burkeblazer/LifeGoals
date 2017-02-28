package bblazer.com.lifegoals;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import bblazer.com.lifegoals.Objects.Food.MacroRatio;

public class WeightLossActivity extends AppCompatActivity {
    private CheckBox     active;
    private EditText     goalWeight;
    private Button       goalDate;
    private Spinner      macroWeek;
    private ImageButton  dropdownArrow;
    private ArrayAdapter macroAdapter;
    final Calendar       myCalendar = Calendar.getInstance();
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_loss);

        getSupportActionBar().setTitle("Weight Loss");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        active        = (CheckBox)findViewById(R.id.active);
        goalWeight    = (EditText)findViewById(R.id.goal_weight);
        goalDate      = (Button)findViewById(R.id.goal_date);
        macroWeek     = (Spinner)findViewById(R.id.macro_week);
        dropdownArrow = (ImageButton)findViewById(R.id.dropdown_arrow);
        settings      = getSharedPreferences("LifeGoals", 0);

        DateFormat df = new SimpleDateFormat("MM/dd/yy");
        goalDate.setText(df.format(new Date()));

        ArrayList<String> afStrings = new ArrayList<String>(){{
            this.add(MacroRatio.FASTING.getName());
            this.add(MacroRatio.HIGH_PROTEIN.getName());
            this.add(MacroRatio.HIGH_CARB.getName());
            this.add(MacroRatio.FEASTING.getName());
        }};
        macroAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, afStrings);
        macroAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        macroWeek.setAdapter(macroAdapter);
        dropdownArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                macroWeek.performClick();
            }
        });

        loadForm();

        final DatePickerDialog.OnDateSetListener eventDateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateGoalDate();
            }
        };

        goalDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(WeightLossActivity.this, eventDateListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        active.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.edit().putBoolean("weight_loss_active", isChecked).commit();
            }
        });
        goalWeight.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                settings.edit().putString("weight_loss_goal_weight", goalWeight.getText().toString()).commit();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        macroWeek.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ((LifeGoalsApplication)getApplication()).mainActivity.weightManager.updateMacroRatio(macroWeek.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    private void loadForm() {
        boolean isActive = settings.getBoolean("weight_loss_active", false);
        active.setChecked(isActive);

        goalWeight.setText(settings.getString("weight_loss_goal_weight", ""));

        String date = settings.getString("weight_loss_goal_date", "");
        if (!date.equals("")) {
            goalDate.setText(date);
        }

        String af = ((LifeGoalsApplication)getApplication()).mainActivity.weightManager.currentWeek.getCurrentMacroRatio().getName();
        if (!af.equals("")) {
            int afPosition = macroAdapter.getPosition(af);
            macroWeek.setSelection(afPosition);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateGoalDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        String date = sdf.format(myCalendar.getTime());
        goalDate.setText(date);
        settings.edit().putString("weight_loss_goal_date", date).commit();
    }
}
