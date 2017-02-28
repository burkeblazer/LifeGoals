package bblazer.com.lifegoals.Objects;

import org.jstrava.entities.activity.Activity;

import java.util.ArrayList;

/**
 * Created by bblazer on 4/6/16.
 */
public class UOM {
    private String name;
    private String abbr;

    public UOM(String name, String abbr) {
        this.name = name;
        this.abbr = abbr;
    }

    public static ArrayList<UOM> getUOMs() {
        ArrayList<UOM> uoms = new ArrayList<UOM>(){{this.add(new UOM("Miles", "m"));this.add(new UOM("Hours", "hr"));}};

        return uoms;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public static UOM getUOMByString (String name) {
        if (name.toLowerCase().equals("miles") || name.toLowerCase().equals("mile") || name.toLowerCase().equals("m")) {
            return new UOM("Miles", "m");
        }
        else if (name.toLowerCase().equals("hours") || name.toLowerCase().equals("hour") || name.toLowerCase().equals("hr")) {
            return new UOM("Hours", "hr");
        }

        return null;
    }

    public Double getDistanceFromStravaEvent(Activity currentActivity) {
        if (this.name.equals("Miles")) {
            // Strava distances are in meters so convert to miles
            return (double)currentActivity.getDistance()*0.000621371;
        }
        else if (this.name.equals("Hours")) {
            // Strava moving_time is in seconds so convert to hours
            return (double)currentActivity.getMoving_time()/60;
        }

        return 0.0;
    }

    public UOM getUOMFromStravaEvent(Activity currentActivity) {
        if (this.name.equals("Miles")) {
            return new UOM("Miles", "m");
        }
        else if (this.name.equals("Hours")) {
            return new UOM("Hours", "hr");
        }

        return null;
    }
}
