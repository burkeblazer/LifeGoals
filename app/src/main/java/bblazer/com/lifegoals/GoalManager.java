package bblazer.com.lifegoals;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import bblazer.com.lifegoals.Objects.ActivityType.ActivityType;
import bblazer.com.lifegoals.Objects.Event.Event;
import bblazer.com.lifegoals.Objects.Goal.Goal;
import bblazer.com.lifegoals.Objects.UOM;

interface GoalManagerListener {
    void goalsUpdated(int numUpdated, int numDeleted, int numAdded);
}

class DailyActivityType {
    ActivityType activityType;
    double       todayRemains;
    double       todayNeeded;
    int          percentComplete;
    UOM          uom;
    Event        mostRecent;

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

/**
 * Created by bblazer on 4/7/16.
 */
public class GoalManager implements EventManagerListener {
    public ArrayList<Goal> goals = new ArrayList<Goal>();
    public MainActivity activity;
    private int deleted          = 0;
    private int updated          = 0;
    private int added            = 0;

    private List<GoalManagerListener> listeners = new ArrayList<GoalManagerListener>();

    public void addListener(GoalManagerListener toAdd) {
        listeners.add(toAdd);
    }

    public GoalManager(MainActivity activity) {
        this.activity = activity;

        // Try to deserialize any goals we have stored
        Gson gson                 = new Gson();
        SharedPreferences  mPrefs = activity.getPreferences(Activity.MODE_PRIVATE);
        String json               = mPrefs.getString("LifeGoals", "");

        // If there are none just return
        if (json.equals("")) {return;}

        goals = gson.fromJson(json, new TypeToken<ArrayList<Goal>>(){}.getType());

        // check to see if any are persistent and active and set those guys up
        this.checkPersistent();

        // Now that the events have been loaded and the goals have been loaded we can associate everything
        this.checkForEvents();

        this.saveGoals();
    }

    public void checkPersistent() {
        for (int ct = 0; ct < goals.size(); ct++) {
            // Basically if it's a persistent goal, just set the start and end date properly
            if (goals.get(ct).getPersistent() && goals.get(ct).getActive()) {
                goals.get(ct).setPersistentData();
            }
        }
    }

    public void addGoal(Goal goal) {
        goals.add(goal);
        added++;

        if (goal.getPersistent() && goal.getActive()) {
            goal.setPersistentData();
        }

        this.checkForEvents();

        this.saveGoals();
    }

    public void editGoal(Goal goal, String id) {
        for (int ct = 0; ct < goals.size(); ct++) {
            if (goals.get(ct).getId().equals(id)) {
                goals.get(ct).setEndDate(goal.getEndDate());
                goals.get(ct).setStartDate(goal.getStartDate());
                goals.get(ct).setGoalDistance(goal.getGoalDistance());
                goals.get(ct).setCurrentDistance(goal.getCurrentDistance());
                goals.get(ct).setActivityType(goal.getActivityType());
                goals.get(ct).setActive(goal.getActive());
                goals.get(ct).setPersistent(goal.getPersistent());
                goals.get(ct).setPersistentInterval(goal.getPersistentInterval());

                if (goals.get(ct).getPersistent() && goals.get(ct).getActive()) {
                    goals.get(ct).setPersistentData();
                }

                updated++;
            }
        }

        this.checkForEvents();

        this.saveGoals();
    }

    public void deleteGoal(Goal delete) {
        int index = -1;
        for (int ct = 0; ct < goals.size(); ct++) {
            if (goals.get(ct).getId().equals(delete.getId())) {
                index = ct;
            }
        }

        if (index == -1) {return;}

        deleted++;
        goals.remove(index);

        this.saveGoals();
    }

    public void saveGoals() {
        SharedPreferences  mPrefs            = activity.getPreferences(Activity.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson                            = new Gson();
        String json                          = gson.toJson(goals);
        prefsEditor.putString("LifeGoals", json);
        prefsEditor.commit();

        for (GoalManagerListener hl : listeners)
            hl.goalsUpdated(updated, deleted, added);

        updated = 0;
        deleted = 0;
        added   = 0;
    }

    public void addEventToGoal(Goal currentGoal, Event addEvent) {
        // See if the goal activity type and activity activity type match
        if (addEvent.getActivityType().getFullName() == null || !addEvent.getActivityType().getFullName().equals(currentGoal.getActivityType().getFullName())) {
            return;
        }

        Calendar endDate = new GregorianCalendar();
        endDate.setTime(currentGoal.getEndDate());
        endDate.set(Calendar.HOUR_OF_DAY, 0);
        endDate.set(Calendar.MINUTE, 0);
        endDate.set(Calendar.SECOND, 0);
        endDate.set(Calendar.MILLISECOND, 0);
        endDate.add(Calendar.HOUR, 24);

        // If the activity's start time is within the goal's start date and end date then it's a good one
        if (addEvent.getStartTime().after(currentGoal.getStartDate()) && addEvent.getStartTime().before(endDate.getTime())) {
            currentGoal.addEvent(addEvent);
        }
    }

    public void checkForEvents() {
        // Let's go through the events and try to add what we can
        ArrayList<Event> events = activity.eventManager.getEvents();
        for (int ct = 0; ct < events.size(); ct++) {
            for (int ct2 = 0; ct2 < goals.size(); ct2++) {
                this.addEventToGoal(goals.get(ct2), events.get(ct));
            }
        }

        // TODO: Check delete from goal
        for (int ct3 = 0; ct3 < goals.size(); ct3++) {
            ArrayList<Event> goalEvents = goals.get(ct3).getEvents();
            for (int ct4 = 0; ct4 < goalEvents.size(); ct4++) {
                boolean bFound = false;
                for (int ct5 = 0; ct5 < events.size(); ct5++) {
                    if (events.get(ct5).getId().equals(goalEvents.get(ct4).getId())) {bFound = true;break;}
                }

                // If it wasn't found, remove it from the goal
                if (!bFound) {
                    goals.get(ct3).deleteEvent(goalEvents.get(ct4));
                }
            }
        }
    }

    @Override
    public void eventsUpdated() {
        this.checkForEvents();
    }

    public Goal getEmptyGoal() {
        Goal newGoal = new Goal(this);

        return newGoal;
    }

    public ArrayList<DailyActivityType> getDailyActivities() {
        Calendar today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        // Loop through your goals to see which are active today
        ArrayList<Goal> activeGoals = new ArrayList<Goal>();
        for (int ct = 0; ct < goals.size(); ct++) {
            Goal currentGoal = goals.get(ct);

            Calendar endDate = new GregorianCalendar();
            endDate.setTime(currentGoal.getEndDate());
            endDate.set(Calendar.HOUR_OF_DAY, 0);
            endDate.set(Calendar.MINUTE, 0);
            endDate.set(Calendar.SECOND, 0);
            endDate.set(Calendar.MILLISECOND, 0);
            endDate.add(Calendar.HOUR, 24);

            if (currentGoal.getPersistent() && currentGoal.getActive()) {
                activeGoals.add(currentGoal);
            }
            else if (today.getTime().equals(currentGoal.getStartDate()) || (today.getTime().after(currentGoal.getStartDate()) && today.getTime().before(endDate.getTime()))) {
                activeGoals.add(currentGoal);
            }
        }

        // Create the DailyActivityTypes from these
        ArrayList<DailyActivityType> dailyActivityTypes = new ArrayList<DailyActivityType>();
        for (int ct2 = 0; ct2 < activeGoals.size(); ct2++) {
            Goal activeGoal = activeGoals.get(ct2);
            // Loop through any existing DailyActivityTypes to see if one already exists for this type
            boolean bFound = false;
            for (int ct3 = 0; ct3 < dailyActivityTypes.size(); ct3++) {
                DailyActivityType activeDailyActivityType = dailyActivityTypes.get(ct3);
                if (activeDailyActivityType.activityType.getFullName().equals(activeGoal.getActivityType().getFullName())) {
                    bFound = true;

                    // Update it accordingly
                    if (activeGoal.getTodayRemains() > activeDailyActivityType.getTodayRemains()) {
                        activeDailyActivityType.setTodayRemains(activeGoal.getTodayRemains());
                    }

                    if (activeGoal.getTodayNeeded() > activeDailyActivityType.getTodayNeeded()) {
                        activeDailyActivityType.setTodayNeeded(activeGoal.getTodayNeeded());
                    }
                }
            }

            if (!bFound) {
                dailyActivityTypes.add(new DailyActivityType(activeGoal.getActivityType(), activeGoal.getTodayRemains(), activeGoal.getTodayNeeded(), activeGoal.getUom(), activeGoal.getMostRecentEvent()));
            }
        }

        return dailyActivityTypes;
    }

    public int getCalsRemainingToday() {
        ArrayList<DailyActivityType> dailyActivityTypes = getDailyActivities();
        int calsRemaining = 0;
        for (int ct = 0; ct < dailyActivityTypes.size(); ct++) {
            Event lastEvent = dailyActivityTypes.get(ct).mostRecent;
            if (lastEvent == null) {continue;}
            double distance = lastEvent.getDistance();
            double calsBurnedPerDistance = lastEvent.getCalsBurned()/distance;

            double remains  = dailyActivityTypes.get(ct).getTodayRemains();
            calsRemaining   += (int)remains*calsBurnedPerDistance;
        }
        return calsRemaining;
    }
}
