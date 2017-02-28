package bblazer.com.lifegoals.Objects.ActivityType;

import java.util.ArrayList;

import bblazer.com.lifegoals.R;

;

/**
 * Created by bblazer on 4/6/16.
 */
public class ActivityType {
    String fullName;
    String shortName;
    String pastName;
    int resourceID;
    String color;

    private static final ActivityType BIKE       = new ActivityType("Biking",     "Ride", "Rode", R.drawable.bike_icon,       "#F26140");
    private static final ActivityType RUN        = new ActivityType("Running",    "Run",  "Ran",  R.drawable.running_icon,    "#59D3B6");
    private static final ActivityType ELLIPTICAL = new ActivityType("Elliptical", "Ride", "Rode", R.drawable.elliptical_icon, "#071EA6");

    public ActivityType() {

    }

    @Override
    public String toString() {
        return this.getFullName();
    }

    public ActivityType(String fullName, String shortName, String pastName, int resourceID, String color) {
        this.fullName  = fullName;
        this.shortName  = shortName;
        this.pastName   = pastName;
        this.color      = color;
        this.resourceID = resourceID;
    }

    public static ArrayList<ActivityType> getActivityTypes() {
        ArrayList<ActivityType> activityTypes = new ArrayList<ActivityType>(){{
            this.add(BIKE);
            this.add(RUN);
            this.add(ELLIPTICAL);
        }};

        return activityTypes;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public static ActivityType getActivityTypeByName (String name) {
        if (name.toLowerCase().equals("biking") || name.toLowerCase().equals("bike") || name.toLowerCase().equals("ride")) {
            return BIKE;
        }
        else if (name.toLowerCase().equals("running") || name.toLowerCase().equals("run")) {
            return RUN;
        }
        else if (name.toLowerCase().equals("elliptical")) {
            return ELLIPTICAL;
        }

        return null;
    }

    public static ActivityType convertStravaActivityType(String type) {
        // ride, run, swim, workout, hike, walk, nordicski, alpineski, backcountryski, iceskate, inlineskate, kitesurf, rollerski, windsurf, workout, snowboard, snowshoe, ebikeride, virtualride
        if (type.toLowerCase().equals("ride")) {
            return BIKE;
        }
        else if (type.toLowerCase().equals("run")) {
            return RUN;
        }

        return new ActivityType();
    }

    public String getPastName() {
        return pastName;
    }

    public void setPastName(String pastName) {
        this.pastName = pastName;
    }

    public int getResourceID() {
        return resourceID;
    }

    public void setResourceID(int resourceID) {
        this.resourceID = resourceID;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public static ActivityType convertHumanActivityType(String type) {
        // walking, running, cycling, unknown, bike, run
        if (type.toLowerCase().equals("ride") || type.toLowerCase().equals("bike") || type.toLowerCase().equals("cycling")) {
            return BIKE;
        }
        else if (type.toLowerCase().equals("run") || type.toLowerCase().equals("running")) {
            return RUN;
        }

        return null;
    }
}
