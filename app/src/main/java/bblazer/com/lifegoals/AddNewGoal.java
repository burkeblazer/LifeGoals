package bblazer.com.lifegoals;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import bblazer.com.lifegoals.Objects.ActivityType.ActivityType;
import bblazer.com.lifegoals.Objects.ActivityType.ActivityTypeArrayAdapter;
import bblazer.com.lifegoals.Objects.Goal.Goal;
import bblazer.com.lifegoals.Objects.UOM;

public class AddNewGoal extends AppCompatActivity {

    private Spinner activity;
    private Button startDate;
    private Button endDate;
    private EditText distance_et;
    private Spinner uom;
    public static Goal editGoal;
    private ActivityTypeArrayAdapter activityAdapter;
    private ArrayAdapter<String> uomAdapter;
    private ArrayAdapter<String> persistentAdapter;
    private Goal currentEditGoal;
    private EditText goalName;
    private ImageView activityTypeDDImage;
    private ImageView ddUOMArrow;
    private CheckBox persistent;
    private Spinner persistentInterval;
    private ImageView persistentArrow;
    private TextView iot;
    private EditText increaseOverTime;
    private TextView increaseOverTimetv;
    private RelativeLayout startEndContainer;
    private CheckBox active;
    private RelativeLayout activeContainer;

    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_goal);

        if (editGoal != null) {
            getSupportActionBar().setTitle("Edit Existing Goal");
        }
        else {
            getSupportActionBar().setTitle("Add New Goal");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startDate           = (Button)        findViewById(R.id.start_date);
        endDate             = (Button)        findViewById(R.id.end_date);
        activity            = (Spinner)       findViewById(R.id.activity);
        uom                 = (Spinner)       findViewById(R.id.uom);
        distance_et         = (EditText)      findViewById(R.id.distance_et);
        goalName            = (EditText)      findViewById(R.id.goal_name);
        activityTypeDDImage = (ImageView)     findViewById(R.id.dropdown_arrow);
        ddUOMArrow          = (ImageView)     findViewById(R.id.dropdown_uom_arrow);
        persistent          = (CheckBox)      findViewById(R.id.presistent);
        persistentInterval  = (Spinner)       findViewById(R.id.persistent_interval);
        persistentArrow     = (ImageView)     findViewById(R.id.dropdown_persistent_arrow);
        iot                 = (TextView)      findViewById(R.id.iot_uom);
        increaseOverTime    = (EditText)      findViewById(R.id.increase_over_time);
        increaseOverTimetv  = (TextView)      findViewById(R.id.increase_over_time_tv);
        startEndContainer   = (RelativeLayout)findViewById(R.id.start_end_container);
        active              = (CheckBox)      findViewById(R.id.active);
        activeContainer     = (RelativeLayout)findViewById(R.id.active_container);

        persistent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                startEndContainer.setVisibility((isChecked) ? View.GONE    : View.VISIBLE);
                activeContainer.setVisibility((isChecked)   ? View.VISIBLE : View.GONE);
            }
        });

        DateFormat df = new SimpleDateFormat("MM/dd/yy");
        startDate.setText(df.format(new Date()));
        GregorianCalendar gc = new GregorianCalendar();
        gc.add(Calendar.DATE, 1);
        endDate.setText(df.format(gc.getTime()));

        activityAdapter = new ActivityTypeArrayAdapter(this);
        activity.setAdapter(activityAdapter);
        activityTypeDDImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.performClick();
            }
        });

        ArrayList<String> uomStrings = new ArrayList<String>();
        ArrayList<UOM>    uomArray   = UOM.getUOMs();
        for (UOM uom: uomArray) {
            uomStrings.add(uom.getName());
        }
        final ArrayList<String> finalUomStrings = uomStrings;
        uomAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, uomStrings);
        uomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        uom.setAdapter(uomAdapter);
        uom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String uomStr = finalUomStrings.get(position);
                iot.setText(uomStr);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ddUOMArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uom.performClick();
            }
        });

        persistentAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>(){{this.add("Daily");this.add("Weekly");this.add("Monthly");this.add("Yearly");}});
        persistentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        persistentInterval.setAdapter(persistentAdapter);
        persistentArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                persistentInterval.performClick();
            }
        });
        persistentInterval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                increaseOverTimetv.setText(persistentInterval.getSelectedItem().toString()+" increase:");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateStartLabel();
            }
        };

        final DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateEndLabel();
            }
        };

        startDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddNewGoal.this, startDateListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddNewGoal.this, endDateListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        if (this.editGoal != null) {
            currentEditGoal = editGoal;
            editGoal = null;
            this.loadForm();
        }
    }

    private void loadForm() {
        int spinnerPosition = activityAdapter.getPosition(currentEditGoal.getActivityType());
        activity.setSelection(spinnerPosition);

        DateFormat df = new SimpleDateFormat("MM/dd/yy");
        startDate.setText(df.format(currentEditGoal.getStartDate()));
        endDate.setText(df.format(currentEditGoal.getEndDate()));

        String distanceET = Integer.toString((int)currentEditGoal.getGoalDistance().intValue());
        distance_et.setText(distanceET);

        int uomSpinnerPosition = uomAdapter.getPosition(currentEditGoal.getUom().getName());
        uom.setSelection(uomSpinnerPosition);

        goalName.setText(currentEditGoal.getName());
        active.setChecked(currentEditGoal.getActive());

        persistent.setChecked(currentEditGoal.getPersistent());
        int intervalPosition = persistentAdapter.getPosition(currentEditGoal.getPersistentInterval());
        persistentInterval.setSelection(intervalPosition);
        increaseOverTime.setText(Double.toString(currentEditGoal.getIncreaseOverTime()));
    }

    private void updateStartLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        startDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateEndLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        endDate.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_new_goal_menu, menu);
        MenuItem addGoalItem = menu.findItem(R.id.add_goal);
        if (currentEditGoal != null) {
            addGoalItem.setTitle("Update Goal");
        } else {
            addGoalItem.setTitle("Add Goal");
        }
        return true;
    }

    private Goal getGoal() {
        Goal lifeGoal = ((LifeGoalsApplication)getApplication()).mainActivity.goalManager.getEmptyGoal();

        String activityTypeStr = activity.getSelectedItem().toString();
        ActivityType activityType = ActivityType.getActivityTypeByName(activityTypeStr);
        lifeGoal.setActivityType(activityType);

        String uomStr = uom.getSelectedItem().toString();
        UOM uomObj    = UOM.getUOMByString(uomStr);
        lifeGoal.setUom(uomObj);
        lifeGoal.setName(goalName.getText().toString());
        lifeGoal.setActive(active.isChecked());

        String goalStr    = (distance_et.getText().toString() != null && !distance_et.getText().toString().equals("")) ? distance_et.getText().toString() : "0";
        lifeGoal.setGoalDistance(Double.parseDouble(goalStr));
        DateFormat format = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);
        try {
            lifeGoal.setStartDate(format.parse(startDate.getText().toString()));
            lifeGoal.setEndDate(format.parse(endDate.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        lifeGoal.setPersistent(persistent.isChecked());
        lifeGoal.setPersistentInterval(persistentInterval.getSelectedItem().toString());
        lifeGoal.setIncreaseOverTime(Double.parseDouble(increaseOverTime.getText().toString()));

        return lifeGoal;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.add_goal:
                // Create a goal and add it to your goals and finish the app
                Goal goal = this.getGoal();

                boolean isValid = this.getIsValidGoal(goal);
                if (!isValid) {return false;}

                if (currentEditGoal != null) {
                    ((LifeGoalsApplication)getApplication()).mainActivity.goalManager.editGoal(goal, currentEditGoal.getId());
                }
                else {
                    ((LifeGoalsApplication)getApplication()).mainActivity.goalManager.addGoal(goal);
                }

                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean getIsValidGoal (Goal testGoal) {
        // Test start and end date
        if (testGoal.getStartDate().after(testGoal.getEndDate())) {
            Toast.makeText(this, "Please make sure start date is before the end date.", Toast.LENGTH_LONG).show();
            return false;
        }

        // Just make sure target and current aren't null or negative
        if (testGoal.getGoalDistance() == null || testGoal.getGoalDistance() < 0) {
            Toast.makeText(this, "Please make sure your target is >= 0.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
