package bblazer.com.lifegoals.Add;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import bblazer.com.lifegoals.Manager.EventManager;
import bblazer.com.lifegoals.LifeGoalsApplication;
import bblazer.com.lifegoals.Objects.ActivityType.ActivityType;
import bblazer.com.lifegoals.Objects.ActivityType.ActivityTypeArrayAdapter;
import bblazer.com.lifegoals.Objects.Event.Event;
import bblazer.com.lifegoals.Objects.UOM;
import bblazer.com.lifegoals.R;

;

/**
 * Created by bblazer on 4/8/16.
 */
public class AddEvent extends AppCompatActivity {
    private Spinner activity;
    private Button eventDate;
    private Button startTime;
    private Button endTime;
    private EditText goalEt;
    private ActivityTypeArrayAdapter activityAdapter;
    private ArrayAdapter<String> uomAdapter;
    private Spinner uom;
    private ImageView activityTypeDDImage;
    public static Event editEvent;
    private Event currentEditEvent;
    private EditText eventName;
    private EditText comments;
    private EditText calories;

    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_event);

        if (editEvent != null) {
            getSupportActionBar().setTitle("Edit Existing Event");
        }
        else {
            getSupportActionBar().setTitle("Add New Event");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        eventDate   = (Button)   findViewById(R.id.event_date);
        startTime   = (Button)   findViewById(R.id.start_time);
        endTime     = (Button)   findViewById(R.id.end_time);
        activity    = (Spinner)  findViewById(R.id.activity);
        uom         = (Spinner)  findViewById(R.id.uom);
        goalEt      = (EditText) findViewById(R.id.goal_et);
        eventName   = (EditText) findViewById(R.id.event_name);
        comments    = (EditText) findViewById(R.id.event_comments);
        activityTypeDDImage = (ImageView)findViewById(R.id.dropdown_arrow);
        calories    = (EditText) findViewById(R.id.calories);

        if (this.editEvent != null) {
            currentEditEvent = editEvent;
            editEvent        = null;
        }

        DateFormat df        = new SimpleDateFormat("MM/dd/yy");
        eventDate.setText(df.format(new Date()));
        DateFormat tf        = new SimpleDateFormat("hh:mm:ss a");
        startTime.setText(tf.format(new Date()));
        GregorianCalendar gc = new GregorianCalendar();
        gc.add(Calendar.HOUR, 1);
        endTime.setText(tf.format(gc.getTime()));

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

        final DatePickerDialog.OnDateSetListener eventDateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateEventDate();
            }
        };

        final TimePickerDialog.OnTimeSetListener startTimeListener = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.HOUR, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);
                updateStartTime();
            }
        };

        final TimePickerDialog.OnTimeSetListener endTimeListener = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.HOUR, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);
                updateEndTime();
            }
        };

        eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddEvent.this, eventDateListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new TimePickerDialog(AddEvent.this, startTimeListener, myCalendar
                        .get(Calendar.HOUR), myCalendar.get(Calendar.MINUTE),
                        false).show();
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new TimePickerDialog(AddEvent.this, endTimeListener, myCalendar
                        .get(Calendar.HOUR), myCalendar.get(Calendar.MINUTE),
                        false).show();
            }
        });

        if (this.currentEditEvent != null) {
            this.loadForm();
        }
    }

    private void loadForm() {
        eventName.setText(currentEditEvent.getEventName());
        comments.setText(currentEditEvent.getComments());

        calories.setText(Integer.toString(currentEditEvent.getCalsBurned()));

        int spinnerPosition = activityAdapter.getPosition(currentEditEvent.getActivityType());
        activity.setSelection(spinnerPosition);

        DateFormat df  = new SimpleDateFormat("MM/dd/yy");
        DateFormat tdf = new SimpleDateFormat("hh:mm:ss a");
        eventDate.setText(df.format(currentEditEvent.getStartTime()));
        startTime.setText(tdf.format(currentEditEvent.getStartTime()));
        endTime.setText(tdf.format(currentEditEvent.getEndTime()));
        comments.setText(currentEditEvent.getComments());

        goalEt.setText(Double.toString(currentEditEvent.getDistance()));

        int uomSpinnerPosition = uomAdapter.getPosition(currentEditEvent.getUom().getName());
        uom.setSelection(uomSpinnerPosition);
    }

    private void updateEventDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        eventDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateStartTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a", Locale.US);
        startTime.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateEndTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a", Locale.US);
        endTime.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_new_event, menu);
        MenuItem addGoalItem = menu.findItem(R.id.add_event);
        if (currentEditEvent != null) {
            addGoalItem.setTitle("Update Event");
        } else {
            addGoalItem.setTitle("Add Event");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.add_event:
                // Create a goal and add it to your goals and finish the app
                Event event = this.getEvent();

                boolean isValid = this.getIsValidEvent(event);
                if (!isValid) {return false;}

                if (currentEditEvent != null) {
                    event.setId(currentEditEvent.getId());
                }

                ((EventManager)((LifeGoalsApplication)getApplication()).mainActivity.eventManager).addEvent(event, false);

                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Event getEvent() {
        Event event = new Event();

        if (currentEditEvent != null) {
            event.setEventType(currentEditEvent.getEventType());
        }
        else {
            event.setEventType("Manual");
        }
        String activityTypeStr = activity.getSelectedItem().toString();
        ActivityType activityType = ActivityType.getActivityTypeByName(activityTypeStr);
        event.setActivityType(activityType);

        String uomStr = uom.getSelectedItem().toString();
        UOM uomObj = UOM.getUOMByString(uomStr);
        event.setUom(uomObj);

        event.setDistance(Double.parseDouble(goalEt.getText().toString()));

        String eventDateStr = eventDate.getText().toString();
        String startTimeStr = startTime.getText().toString();
        String endTimeStr   = endTime.getText().toString();
        DateFormat df       = new SimpleDateFormat("MM/dd/yy hh:mm:ss a", Locale.US);
        DateFormat ddf      = new SimpleDateFormat("MM/dd/yy", Locale.US);
        try {
            Date startTimeDate = df.parse(eventDateStr+" "+startTimeStr);
            Date endTimeDate   = df.parse(eventDateStr+" "+endTimeStr);
            event.setElapsedTime((int) ((endTimeDate.getTime()/60000) - (startTimeDate.getTime()/60000)));

            event.setEndTime(endTimeDate);
            event.setStartTime(startTimeDate);
            event.setActivityDate(ddf.parse(eventDateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        event.setEventName(eventName.getText().toString());
        event.setComments(comments.getText().toString());
        event.setId(Long.toString(new Date().getTime()));
        event.setCalsBurned(Integer.parseInt(calories.getText().toString()));

        return event;
    }

    private boolean getIsValidEvent (Event testEvent) {
        // Test start and end date
        if (testEvent.getStartTime().after(testEvent.getEndTime())) {
            Toast.makeText(this, "Please make sure start time is before the end time.", Toast.LENGTH_LONG).show();
            return false;
        }

        // Just make sure target and current aren't null or negative
        if (testEvent.getDistance() == null || testEvent.getDistance() < 0) {
            Toast.makeText(this, "Please make sure your target is >= 0.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}