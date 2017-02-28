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
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    private TextView     bmrTotal;
    private TextView     bmrFillOut;
    private TextView     bmrDescription;
    private RadioButton  maleRadio;
    private RadioButton  femaleRadio;
    private EditText     currentWeightET;
    private EditText     currentHeightFootET;
    private EditText     currentHeightInchesET;
    private Button       birthday;
    private Spinner      activityFactor;
    private ImageButton  dropdownArrow;
    private ArrayAdapter activityFactorAdapter;

    private SharedPreferences settings;

    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bmrTotal                = (TextView)findViewById(R.id.bmr_total);
        bmrFillOut              = (TextView)findViewById(R.id.bmr_fill_out);
        bmrDescription          = (TextView)findViewById(R.id.bmr_description);
        maleRadio               = (RadioButton) findViewById(R.id.male_radio);
        femaleRadio             = (RadioButton)findViewById(R.id.female_radio);
        currentWeightET         = (EditText)findViewById(R.id.current_weight_et);
        currentHeightFootET     = (EditText)findViewById(R.id.current_height_foot_et);
        currentHeightInchesET   = (EditText)findViewById(R.id.current_height_inches_et);
        birthday                = (Button)findViewById(R.id.birthday);
        activityFactor          = (Spinner)findViewById(R.id.activity_factor);
        dropdownArrow           = (ImageButton)findViewById(R.id.dropdown_arrow);
        settings                = getSharedPreferences("LifeGoals", 0);

        DateFormat df           = new SimpleDateFormat("MM/dd/yy");
        birthday.setText(df.format(new Date()));

        final DatePickerDialog.OnDateSetListener eventDateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateBirthday();
            }
        };

        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(SettingsActivity.this, eventDateListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        ArrayList<String> afStrings = new ArrayList<String>(){{
            this.add("Sedentary");
            this.add("Lightly Active");
            this.add("Moderately Active");
            this.add("Very Active");
            this.add("Extremely Active");
        }};
        activityFactorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, afStrings);
        activityFactorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityFactor.setAdapter(activityFactorAdapter);
        dropdownArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityFactor.performClick();
            }
        });

        maleRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    settings.edit().putString("human_sex", "Male").commit();
                }
                updateBMR();
            }
        });
        femaleRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    settings.edit().putString("human_sex", "Female").commit();
                }
                updateBMR();
            }
        });
        currentWeightET.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                settings.edit().putInt("human_weight", Integer.parseInt(currentWeightET.getText().toString())).commit();
                updateBMR();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        currentHeightInchesET.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                settings.edit().putInt("human_height_inches", Integer.parseInt(currentHeightInchesET.getText().toString())).commit();
                updateBMR();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        currentHeightFootET.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                settings.edit().putInt("human_height_feet", Integer.parseInt(currentHeightFootET.getText().toString())).commit();
                updateBMR();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        activityFactor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                settings.edit().putString("human_activity_factor", activityFactor.getSelectedItem().toString()).commit();
                updateBMR();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        loadForm();
    }

    private void loadForm() {
        String sex = settings.getString("human_sex", "");
        if (sex.equals("Male")) {
            maleRadio.setChecked(true);
        }
        if (sex.equals("Female")) {
            femaleRadio.setChecked(true);
        }

        int weight = settings.getInt("human_weight", -1);
        if (weight != -1) {
            currentWeightET.setText(Integer.toString(weight));
        }

        int feet = settings.getInt("human_height_feet", -1);
        if (feet != -1) {
            currentHeightFootET.setText(Integer.toString(feet));
        }

        int inches = settings.getInt("human_height_inches", -1);
        if (inches != -1) {
            currentHeightInchesET.setText(Integer.toString(inches));
        }

        String date = settings.getString("human_birthday", "");
        if (!date.equals("")) {
            birthday.setText(date);
        }

        String af = settings.getString("human_activity_factor", "");
        if (!af.equals("")) {
            int afPosition = activityFactorAdapter.getPosition(af);
            activityFactor.setSelection(afPosition);
        }

        updateBMR();
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

    private void updateBMR() {
        // Reset the top messages
        bmrTotal.setText("N/A");
        bmrFillOut.setVisibility(View.VISIBLE);
        bmrDescription.setVisibility(View.GONE);

        int bmr = ((LifeGoalsApplication)getApplication()).mainActivity.getBMR();

        if (bmr == -1) {return;}

        bmrTotal.setText(Integer.toString(bmr));
        bmrFillOut.setVisibility(View.GONE);
        bmrDescription.setVisibility(View.VISIBLE);
    }

    private void updateBirthday() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        String date = sdf.format(myCalendar.getTime());
        birthday.setText(date);
        settings.edit().putString("human_birthday", date).commit();
        updateBMR();
    }
}
