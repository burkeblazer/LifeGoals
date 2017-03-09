package bblazer.com.lifegoals.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import bblazer.com.lifegoals.Add.AddEvent;
import bblazer.com.lifegoals.Utility.CircleProgressBar;
import bblazer.com.lifegoals.LifeGoalsApplication;
import bblazer.com.lifegoals.Manager.GoalManagerListener;
import bblazer.com.lifegoals.Objects.Event.Event;
import bblazer.com.lifegoals.Objects.Event.EventListAdapter;
import bblazer.com.lifegoals.Objects.Goal.Goal;
import bblazer.com.lifegoals.Objects.Goal.GoalAdapter;
import bblazer.com.lifegoals.R;

public class ViewGoalActivity extends AppCompatActivity implements GoalManagerListener {
    public static Goal viewGoalStatic;
    private Goal viewGoal;
    private CircleProgressBar circleProgressBar;
    private LinearLayout overviewContainer;
    private TextView distanceLeft;
    private ListView eventList;
    private Button refreshIntegrations;
    private RelativeLayout emptyView;
    private ImageView appIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_goal);

        // Set the private goal from the static goal and release it
        this.viewGoal  = viewGoalStatic;
        viewGoalStatic = null;

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        View cView = getLayoutInflater().inflate(R.layout.view_goal_titlebar, null);
        ImageView topIcon  = (ImageView)cView.findViewById(R.id.title_bar_icon);
        TextView titleText = (TextView)cView.findViewById(R.id.title_bar_title_text);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (this.viewGoal.getActivityType().getFullName().equals("Biking")) {
            topIcon.setImageResource(R.drawable.bike_icon);
        }
        else if (this.viewGoal.getActivityType().getFullName().equals("Elliptical")) {
            topIcon.setImageResource(R.drawable.elliptical_icon);
        }
        else {
            topIcon.setImageResource(R.drawable.running_icon);
        }

        DateFormat df = new SimpleDateFormat("MM/dd/yy");
        titleText.setText(this.viewGoal.getGoalDistance() + " " + this.viewGoal.getUom().getName().toLowerCase() + " by " + df.format(this.viewGoal.getEndDate()));

        getSupportActionBar().setCustomView(cView);
        circleProgressBar = (CircleProgressBar) findViewById(R.id.custom_progressBar);
        distanceLeft = (TextView)findViewById(R.id.distance_left);

        setProgressBar();

        overviewContainer = (LinearLayout)findViewById(R.id.overview_container);

        // And From your main() method or any other method
        Timer timer = new Timer();
        timer.schedule(new LoadOverview(), 0, 15000);

        ((LifeGoalsApplication)getApplication()).mainActivity.goalManager.addListener(this);

        eventList = (ListView)findViewById(R.id.event_list);
        eventList.setAdapter(new EventListAdapter(this, viewGoal));
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event selectedEvent = viewGoal.getEvents().get(position);
                Intent intent = new Intent(ViewGoalActivity.this, AddEvent.class);
                AddEvent.editEvent = selectedEvent;
                startActivity(intent);
            }
        });
        registerForContextMenu(eventList);

        emptyView    = (RelativeLayout)findViewById(R.id.empty_view);
        appIcon      = (ImageView)findViewById(R.id.app_icon);
        refreshIntegrations = (Button)findViewById(R.id.refresh_integrations);
        refreshIntegrations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LifeGoalsApplication)getApplication()).mainActivity.syncStravaActivities();
            }
        });

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);  //0 means grayscale
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        appIcon.setColorFilter(cf);
        appIcon.setAlpha(128);

        this.toggleGoalView();
    }

    private void setProgressBar() {
        circleProgressBar.setAnimationDuration(2500);
        float percent = (float) ((double)this.viewGoal.getCurrentDistance() / this.viewGoal.getGoalDistance())*100;
        if (percent >= 50) {
            circleProgressBar.setProgress(0);
        }
        else {
            circleProgressBar.setProgress(100);
        }
        circleProgressBar.setProgressWithAnimation(percent);

        distanceLeft.setText(GoalAdapter.round(viewGoal.getGoalDistance() - viewGoal.getCurrentDistance(), 1)+" "+viewGoal.getUom().getName().toLowerCase()+" left");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.event_list) {
            menu.add("Delete");
            menu.add("Edit");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String menuItemString = (String)item.getTitle();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        Event selectedEvent = ViewGoalActivity.this.viewGoal.getEvents().get(info.position);

        if (menuItemString.equals("Delete")) {
            ViewGoalActivity.this.viewGoal.deleteEvent(selectedEvent);
            ((LifeGoalsApplication)getApplication()).mainActivity.goalManager.saveGoals();
        }
        else if (menuItemString.equals("Edit")) {
            Intent intent = new Intent(ViewGoalActivity.this, AddEvent.class);
            AddEvent.editEvent = selectedEvent;
            startActivity(intent);
        }
        else {
            return false;
        }

        return true;
    }

    public void toggleGoalView() {
        if (viewGoal.getEvents().size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            eventList.setVisibility(View.GONE);
        }
        else {
            emptyView.setVisibility(View.GONE);
            eventList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void goalsUpdated(int numUpdated, int numDeleted, int numAdded) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((EventListAdapter)eventList.getAdapter()).notifyDataSetChanged();
                toggleGoalView();
                setProgressBar();
            }
        });
    }

    class LoadOverview extends TimerTask {
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateInfo();
                }
            });
        }
    }

    public void updateInfo() {
        overviewContainer.removeAllViews();
        double containerHeight = overviewContainer.getHeight();
        double numAvailable    = containerHeight/convertDpToPixel(40, ViewGoalActivity.this);
        double infoCt          = Math.floor(numAvailable);
        ArrayList<String> infos = viewGoal.getInfo((int)infoCt);
        for (int ct = 0; ct < infos.size(); ct++) {
            TextView textView =  (TextView) getLayoutInflater().inflate(R.layout.customtextview, null);
            textView.setText(infos.get(ct));
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llp.setMargins(0, 0, 0, 10);
            textView.setLayoutParams(llp);
            overviewContainer.addView(textView);
        }
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
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
}
