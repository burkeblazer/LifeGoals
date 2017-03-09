package bblazer.com.lifegoals.Integrations;

import android.content.SharedPreferences;

import org.jstrava.connector.JStravaV3;
import org.jstrava.entities.activity.Activity;

import java.util.List;

import bblazer.com.lifegoals.Activity.MainActivity;

/**
 * Created by bblazer on 4/8/16.
 */
public class StravaIntegration {
    private MainActivity mainActivity;

    public StravaIntegration(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void getStravaAcitiviesSinceLastUpdate() {
        SharedPreferences settings = mainActivity.getSharedPreferences("LifeGoals", 0);
        final String userToken     = settings.getString("strava_user_token", "");
        final String lastUpdated   = settings.getString("strava_last_updated", "");

        if (userToken.equals("")) {return;}

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                JStravaV3 api      = new JStravaV3(userToken);
                final List<Activity> activities;
                if (lastUpdated.equals("")) {
                    activities = api.getCurrentAthleteActivitiesAll();
                }
                else {
                    activities = api.getCurrentAthleteActivitiesAfterDate(Long.parseLong(lastUpdated));
                }
                SharedPreferences settings = mainActivity.getSharedPreferences("LifeGoals", 0);
                settings.edit().putString("strava_last_updated", Long.toString(System.currentTimeMillis() / 1000L)).apply();

                // Simulate a button click on the close to close the window
                mainActivity.eventManager.addStravaActivities(activities);
            }
        });

        thread.start();
    }
}