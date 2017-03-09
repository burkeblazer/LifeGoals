package bblazer.com.lifegoals.Manager;

import bblazer.com.lifegoals.Objects.ActivityType.ActivityType;
import bblazer.com.lifegoals.Objects.Event.Event;
import bblazer.com.lifegoals.Objects.UOM;

public class DailyActivityType {
    ActivityType activityType;
    double       todayRemains;
    double       todayNeeded;
    public int          percentComplete;
    public UOM uom;
    Event mostRecent;

    public DailyActivityType(ActivityType activityType, double todayRemains, double todayNeeded, UOM uom, Event mostRecent) {
        this.activityType    = activityType;
        this.todayRemains    = todayRemains;
        this.todayNeeded     = todayNeeded;
        this.uom             = uom;
        this.mostRecent      = mostRecent;
        int percent          = (int) Math.round(((todayNeeded - todayRemains) / todayNeeded) * 100);
        int value            = Math.min(100, percent);
        if (todayNeeded == 0 && todayRemains == 0) {this.percentComplete = 100;}
        else {this.percentComplete = value;}
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public double getTodayRemains() {
        return todayRemains;
    }

    public void setTodayRemains(double todayRemains) {
        this.todayRemains    = todayRemains;
        int percent          = (int) Math.round(((todayNeeded - todayRemains) / todayNeeded) * 100);
        int value            = Math.min(100, percent);
        if (todayNeeded == 0 && todayRemains == 0) {this.percentComplete = 100;}
        else {this.percentComplete = value;}
    }

    public double getTodayNeeded() {
        return todayNeeded;
    }

    public void setTodayNeeded(double todayNeeded) {
        this.todayNeeded     = todayNeeded;
        int percent          = (int) Math.round(((todayNeeded - todayRemains) / todayNeeded) * 100);
        int value            = Math.min(100, percent);
        if (todayNeeded == 0 && todayRemains == 0) {this.percentComplete = 100;}
        else {this.percentComplete = value;}
    }

    public int getPercentComplete() {
        return percentComplete;
    }

    public void setPercentComplete(int percentComplete) {
        this.percentComplete = percentComplete;
    }
}
