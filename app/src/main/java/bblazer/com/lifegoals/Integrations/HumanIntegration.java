package bblazer.com.lifegoals.Integrations;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import bblazer.com.lifegoals.Activity.MainActivity;
import bblazer.com.lifegoals.Objects.ActivityType.HumanActivity;

/**
 * Created by bblazer on 4/17/16.
 */
public class HumanIntegration{
    String accessToken;
    String baseURL = "https://api.humanapi.co/v1/human/";
    MainActivity mainActivity;

    public HumanIntegration(String accessToken, MainActivity mainActivity) {
        this.accessToken  = accessToken;
        this.mainActivity = mainActivity;
    }

    private String getResult(String URL){
        StringBuilder sb= new StringBuilder();

        try {
            java.net.URL url = new URL(URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization","Bearer "+accessToken);

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode() + " - " + conn.getResponseMessage());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }

            conn.disconnect();

        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }

        return sb.toString();
    }

    public void getActivities() {
        SharedPreferences settings = mainActivity.getSharedPreferences("LifeGoals", 0);
        final String lastUpdated   = settings.getString("human_api_activities_last_updated", "");

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                final List<HumanActivity> activities;
                if (lastUpdated.equals("")) {
                    activities = getAllActivities();
                }
                else {
                    activities = getActivitiesSince(lastUpdated);
                }
                DateFormat df              = new SimpleDateFormat("yyyyMMd'T'Hmss", Locale.US);
                SharedPreferences settings = mainActivity.getSharedPreferences("LifeGoals", 0);
                settings.edit().putString("human_api_activities_last_updated", df.format(new Date())).apply();

                // Simulate a button click on the close to close the window
                mainActivity.eventManager.addHumanActivities(activities);
            }
        });

        thread.start();
    }

    private List<HumanActivity> getActivitiesSince(String lastUpdated) {
        String URL                           = baseURL+"activities?updated_since="+lastUpdated;
        String result                        = getResult(URL);
        Gson gson                            = new Gson();
        HumanActivity[] activitiesArray      = gson.fromJson(result,HumanActivity[].class);
        List<HumanActivity>currentActivities = Arrays.asList(activitiesArray);

        return currentActivities;
    }

    private List<HumanActivity> getAllActivities() {
        String URL                           = baseURL+"activities";
        String result                        = getResult(URL);
        Gson gson                            = new Gson();
        HumanActivity[] activitiesArray      = gson.fromJson(result ,HumanActivity[].class);
        List<HumanActivity>currentActivities = Arrays.asList(activitiesArray);

        return currentActivities;
    }
}
