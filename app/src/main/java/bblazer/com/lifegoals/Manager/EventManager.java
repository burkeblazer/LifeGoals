package bblazer.com.lifegoals.Manager;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;

import bblazer.com.lifegoals.Activity.MainActivity;
import bblazer.com.lifegoals.Objects.ActivityType.HumanActivity;
import bblazer.com.lifegoals.Objects.Event.Event;
import bblazer.com.lifegoals.Objects.Event.HumanEvent;
import bblazer.com.lifegoals.Objects.Event.StravaEvent;

/**
 * Created by bblazer on 4/10/16.
 */
public class EventManager {
    public ArrayList<Event> events = new ArrayList<Event>();
    MainActivity activity;

    private List<EventManagerListener> listeners = new ArrayList<EventManagerListener>();

    public void addListener(EventManagerListener toAdd) {
        listeners.add(toAdd);
    }

    public EventManager(MainActivity activity) {
        this.activity = activity;

        // Try to deserialize any events we have stored
        Gson gson                = new Gson();
        SharedPreferences mPrefs = activity.getPreferences(Activity.MODE_PRIVATE);
        String json              = mPrefs.getString("LifeGoalsEvents", "");

        // There's nothing ouch
        if (json.equals("")) {return;}

        // Deserialize
        events = gson.fromJson(json, new TypeToken<ArrayList<Event>>(){}.getType());
    }

    public void addEvent(Event event, boolean suppressSave) {
        int index = -1;
        for (int ct = 0; ct < events.size(); ct++) {
            if (events.get(ct).getId().equals(event.getId())) {
                index = ct;
            }
        }

        if (index != -1) {this.editEvent(event, this.events.get(index).getId(), suppressSave);return;}

        events.add(event);

        if (!suppressSave) {
            this.saveEvents();
        }
    }

    public void editEvent(Event event, String id, boolean suppressSave) {
        for (int ct = 0; ct < events.size(); ct++) {
            if (events.get(ct).getId().equals(id)) {
                events.get(ct).setEventName(event.getEventName());
                events.get(ct).setActivityType(event.getActivityType());
                events.get(ct).setStartTime(event.getStartTime());
                events.get(ct).setEndTime(event.getEndTime());
                events.get(ct).setDistance(event.getDistance());
                events.get(ct).setElapsedTime(event.getElapsedTime());
                events.get(ct).setUom(event.getUom());
                events.get(ct).setComments(event.getComments());
            }
        }

        if (!suppressSave) {
            this.saveEvents();
        }
    }

    public Event getByID(String id) {
        Event event = null;
        for (int ct = 0; ct < events.size(); ct++) {
            if (events.get(ct).getId().equals(id)) {
                event = events.get(ct);
            }
        }

        return event;
    }

    public void deleteEvent(Event delete) {
        int index = -1;
        for (int ct = 0; ct < events.size(); ct++) {
            if (events.get(ct).getId().equals(delete.getId())) {
                index = ct;
            }
        }

        if (index == -1) {return;}

        events.remove(index);

        this.saveEvents();
    }

    public class EventManagerComparator implements Comparator<Event> {
        @Override
        public int compare(Event o1, Event o2) {
            return o2.getEndTime().compareTo(o1.getEndTime());
        }
    }

    public void saveEvents() {
        SharedPreferences  mPrefs            = activity.getPreferences(Activity.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson                            = new Gson();
        String json                          = gson.toJson(events);
        prefsEditor.putString("LifeGoalsEvents", json);
        prefsEditor.commit();

        // Sort by date
        Collections.sort(this.events, new EventManagerComparator());

        for (EventManagerListener hl : listeners)
            hl.eventsUpdated();
    }

    public void addStravaActivities(List<org.jstrava.entities.activity.Activity> activities) {
        for (int ct = 0; ct < activities.size(); ct++) {
            this.addEvent(new StravaEvent(activities.get(ct)), true);
        }

        this.saveEvents();
    }

    public void addHumanActivities(List<HumanActivity> activities) {
        for (int ct = 0; ct < activities.size(); ct++) {
            HumanEvent humanEvent = new HumanEvent(activities.get(ct));
            if (humanEvent.getActivityType() == null) {continue;}
            this.addEvent(humanEvent, true);
        }

        this.saveEvents();
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public ArrayList<Event> getTodaysEvents() {
        Calendar today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        // Loop through your events to see which are active today
        ArrayList<Event> todaysEvents = new ArrayList<Event>();
        for (int ct = 0; ct < events.size(); ct++) {
            Event currentEvent = events.get(ct);

            if (today.getTime().getTime() <= currentEvent.getEndTime().getTime()) {
                todaysEvents.add(currentEvent);
            }
        }

        return todaysEvents;
    }
}