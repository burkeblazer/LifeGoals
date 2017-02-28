package bblazer.com.lifegoals.Objects.Goal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import bblazer.com.lifegoals.GoalManager;
import bblazer.com.lifegoals.Objects.ActivityType.ActivityType;
import bblazer.com.lifegoals.Objects.Event.Event;
import bblazer.com.lifegoals.Objects.UOM;

class PersistentEvent {
    private Date startDate;
    private Date endDate;

    public PersistentEvent(String interval) {
        Calendar today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        if (interval.toLowerCase().equals("daily")) {
            this.startDate = today.getTime();
            this.endDate   = today.getTime();
        }
        else if (interval.toLowerCase().equals("weekly")) {
            today.add(Calendar.DAY_OF_WEEK, -(today.get(Calendar.DAY_OF_WEEK)-1));
            this.startDate = today.getTime();
            today.add(Calendar.DAY_OF_MONTH, 7);
            this.endDate   = today.getTime();
        }
        else if (interval.toLowerCase().equals("monthly")) {
            today.set(Calendar.DAY_OF_MONTH, 1);
            this.startDate = today.getTime();
            today.set(Calendar.DAY_OF_MONTH, today.getActualMaximum(Calendar.DAY_OF_MONTH));
            this.endDate   = today.getTime();
        }
        else if (interval.toLowerCase().equals("yearly")) {
            today.set(Calendar.DAY_OF_YEAR, 1);
            this.startDate = today.getTime();
            today.set(Calendar.DAY_OF_YEAR, today.getActualMaximum(Calendar.DAY_OF_YEAR));
            this.endDate   = today.getTime();
        }
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}

/**
 * Created by bblazer on 4/6/16.
 */
public class Goal {
    private Boolean                     isActive;
    private Boolean                     isPersistent;
    private String                      persistentInterval;
    private Double                      increaseOverTime;
    private String                      name;
    private String                      id;
    private ActivityType                activityType;
    private Date                        startDate;
    private Date                        endDate;
    private Double                      goalDistance;
    private UOM                         uom;
    private ArrayList<PersistentEvent>  persistentEvents = new ArrayList<PersistentEvent>();
    private transient Double            currentDistance = 0.0;
    private transient ArrayList<Event>  events = new ArrayList<Event>();

    // Constructors
    public Goal() {
        this.id = Long.toString(System.currentTimeMillis());
    }

    public Goal(GoalManager goalManager) {
        this.id          = Long.toString(System.currentTimeMillis());
    }

    // Getters Setters
    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Double getCurrentDistance() {
        return this.round(currentDistance, 1);
    }

    public void setCurrentDistance(Double currentDistance) {
        this.currentDistance = currentDistance;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public Double getGoalDistance() {
        return goalDistance;
    }

    public void setGoalDistance(Double goalDistance) {
        this.goalDistance = goalDistance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UOM getUom() {
        return uom;
    }

    public void setUom(UOM uom) {
        this.uom = uom;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getPersistent() {
        return isPersistent;
    }

    public void setPersistent(Boolean persistent) {
        isPersistent = persistent;
    }

    public String getPersistentInterval() {
        return persistentInterval;
    }

    public void setPersistentInterval(String persistentInterval) {
        this.persistentInterval = persistentInterval;
    }

    public Double getIncreaseOverTime() {
        return increaseOverTime;
    }

    public void setIncreaseOverTime(Double increaseOverTime) {
        this.increaseOverTime = increaseOverTime;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    // Info functions
    public ArrayList<String> getInfo(int amount) {
        ArrayList<String> infos    = new ArrayList<String>();
        String startInfo           = this.startInfo();
        String distanceSoFar       = this.distanceSoFarInfo();
        String lastRodeInfo        = this.lastRodeInfo();
        String getEarliest         = this.getEarliest();
        String getLatest           = this.getLatest();
        String getLongest          = this.getLongest();
        String getShortest         = this.getShortest();
        String getLongestDistance  = this.getLongestDistance();
        String getShortestDistance = this.getShortestDistance();
        String lastDistance        = this.lastDistance();
        String getFavoriteDOW      = this.getFavoriteDOW();
        String daysLeftInfo        = this.daysLeftInfo();
        String getAvgRide          = this.getAvgRide();
        if (startInfo           != null) {infos.add(startInfo);          }
        if (distanceSoFar       != null) {infos.add(distanceSoFar);      }
        if (lastRodeInfo        != null) {infos.add(lastRodeInfo);       }
        if (getEarliest         != null) {infos.add(getEarliest);        }
        if (getLatest           != null) {infos.add(getLatest);          }
        if (getLongest          != null) {infos.add(getLongest);         }
        if (getShortest         != null) {infos.add(getShortest);        }
        if (getLongestDistance  != null) {infos.add(getLongestDistance); }
        if (getShortestDistance != null) {infos.add(getShortestDistance);}
        if (lastDistance        != null) {infos.add(lastDistance);       }
        if (getFavoriteDOW      != null) {infos.add(getFavoriteDOW);     }
        if (daysLeftInfo        != null) {infos.add(daysLeftInfo);       }
        if (getAvgRide          != null) {infos.add(getAvgRide);         }

        ArrayList<String> selectedInfo = new ArrayList<String>();
        Random rand                    = new Random();
        for (int ct = 0; ct < amount; ct++) {
            if (infos.size() == 0) {return selectedInfo;}

            int index = rand.nextInt(infos.size());
            String item = infos.get(index);
            selectedInfo.add(item);
            infos.remove(index);
        }

        return selectedInfo;
    }

    public String startInfo() {
        Calendar today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        // Is going to start
        if (today.getTime().before(this.getStartDate())) {
            long willStartDiff  = this.getStartDate().getTime() - today.getTime().getTime();
            long willStartDiffL = TimeUnit.DAYS.convert(willStartDiff, TimeUnit.MILLISECONDS);
            if (willStartDiffL == 0) {
                return "You start today!";
            }
            else if (willStartDiffL == 1) {
                return "You start tomorrow!";
            }
            else {
                return "You start in " + willStartDiffL + " days";
            }
        }
        else { // Has begun
            long startDiff  = today.getTime().getTime() - this.getStartDate().getTime();
            long startDiffL = TimeUnit.DAYS.convert(startDiff, TimeUnit.MILLISECONDS);
            if (startDiffL == 0) {
                return "You started today";
            }
            else if (startDiffL == 1) {
                return "You started yesterday";
            }
            else {
                return  "You started " + startDiffL + " days ago";
            }
        }
    }

    public String distanceSoFarInfo() {
        return this.getActivityType().getPastName()+" "+this.getCurrentDistance()+" "+this.getUom().getName().toLowerCase()+" so far";
    }

    public String lastRodeInfo() {
        Event mostRecent = this.getMostRecentEvent();
        if (mostRecent == null) {return null;}

        Calendar today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        long startDiff  = today.getTime().getTime() - mostRecent.getStartTime().getTime();
        long startDiffL = TimeUnit.DAYS.convert(startDiff, TimeUnit.MILLISECONDS);
        if (startDiffL <= 0) {
            return "Last "+this.getActivityType().getPastName().toLowerCase()+" today";
        }
        else if (startDiffL <= 1) {
            return "Last "+this.getActivityType().getPastName().toLowerCase()+" yesterday";
        }
        else {
            return  "You "+this.getActivityType().getPastName().toLowerCase()+ " " + startDiffL + " days ago";
        }
    }

    public String getEarliest() {
        if (this.events.size() == 0) {return null;}

        Event earliest = this.events.get(0);
        for (int ct = 0; ct < this.events.size(); ct++) {
            if (this.events.get(ct).getStartTime().before(earliest.getStartTime())) {
                earliest = this.events.get(ct);
            }
        }

        DateFormat df = new SimpleDateFormat("hh:mm:ss a", Locale.US);

        return "Earliest "+this.getActivityType().getShortName().toLowerCase()+" was "+df.format(earliest.getStartTime());
    }

    public String getLatest() {
        if (this.events.size() == 0) {return null;}

        Event latest = this.events.get(0);
        for (int ct = 0; ct < this.events.size(); ct++) {
            if (this.events.get(ct).getStartTime().after(latest.getStartTime())) {
                latest = this.events.get(ct);
            }
        }

        DateFormat df = new SimpleDateFormat("hh:mm:ss a", Locale.US);

        return "Latest "+this.getActivityType().getShortName().toLowerCase()+" was "+df.format(latest.getStartTime());
    }

    public String getLongest() {
        if (this.events.size() == 0) {return null;}

        Event longest = this.events.get(0);
        for (int ct = 0; ct < this.events.size(); ct++) {
            if (this.events.get(ct).getElapsedTime() > longest.getElapsedTime()) {
                longest = this.events.get(ct);
            }
        }
        return "Longest "+this.getActivityType().getShortName().toLowerCase()+" was "+longest.getElapsedTime()+" minutes";
    }

    public String getShortest() {
        if (this.events.size() == 0) {return null;}

        Event shortest = this.events.get(0);
        for (int ct = 0; ct < this.events.size(); ct++) {
            if (this.events.get(ct).getElapsedTime() < shortest.getElapsedTime()) {
                shortest = this.events.get(ct);
            }
        }
        return "Shortest "+this.getActivityType().getShortName().toLowerCase()+" was "+shortest.getElapsedTime()+" minutes";
    }

    public String getLongestDistance() {
        if (this.events.size() == 0) {return null;}

        Event longest = this.events.get(0);
        for (int ct = 0; ct < this.events.size(); ct++) {
            if (this.events.get(ct).getDistance() > longest.getDistance()) {
                longest = this.events.get(ct);
            }
        }
        return "Longest "+this.getActivityType().getShortName().toLowerCase()+" was "+longest.getDistance()+" "+longest.getUom().getName().toLowerCase();
    }

    public String getShortestDistance() {
        if (this.events.size() == 0) {return null;}

        Event shortest = this.events.get(0);
        for (int ct = 0; ct < this.events.size(); ct++) {
            if (this.events.get(ct).getDistance() < shortest.getDistance()) {
                shortest = this.events.get(ct);
            }
        }
        return "Shortest "+this.getActivityType().getShortName().toLowerCase()+" was "+shortest.getDistance()+" "+shortest.getUom().getName().toLowerCase();
    }

    public String getFavoriteDOW() {
        if (this.events.size() == 0) {return null;}

        int mon   = 0;
        int tues  = 0;
        int wed   = 0;
        int thurs = 0;
        int fri   = 0;
        int sat   = 0;
        int sun   = 0;
        DateFormat df = new SimpleDateFormat("EEEE", Locale.US);
        for (int ct = 0; ct < this.events.size(); ct++) {
            String dow = df.format(this.events.get(ct).getStartTime());
            if (dow.equals("Monday")) {mon++;}
            else if (dow.equals("Tuesday")) {tues++;}
            else if (dow.equals("Wednesday")) {wed++;}
            else if (dow.equals("Thursday")) {thurs++;}
            else if (dow.equals("Friday")) {fri++;}
            else if (dow.equals("Saturday")) {sat++;}
            else if (dow.equals("Sunday")) {sun++;}
        }

        int max = mon;
        if (tues > mon)  {max = tues;}
        if (wed > mon)   {max = wed;}
        if (thurs > mon) {max = thurs;}
        if (fri > mon)   {max = fri;}
        if (sat > mon)   {max = sat;}
        if (sun > mon)   {max = sun;}
        String fav = null;
        if (max == mon)   {fav = "Monday";}
        if (max == tues)  {if (fav != null) {return null;}fav = "Tuesday";}
        if (max == wed)  {if (fav != null) {return null;}fav = "Wenesday";}
        if (max == thurs)  {if (fav != null) {return null;}fav = "Thursday";}
        if (max == fri)  {if (fav != null) {return null;}fav = "Friday";}
        if (max == sat)  {if (fav != null) {return null;}fav = "Saturday";}
        if (max == sun)  {if (fav != null) {return null;}fav = "Sunday";}

        return fav+"s are your favorite";
    }

    public String lastDistance() {
        Event mostRecent = this.getMostRecentEvent();
        if (mostRecent == null) {return null;}

        return "Last "+mostRecent.getActivityType().getPastName().toLowerCase()+" "+mostRecent.getDistance()+" "+this.getUom().getName().toLowerCase();
    }

    public String daysLeftInfo() {
        long diff;
        Calendar today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        if (today.getTime().after(this.getStartDate())) {
            diff  = this.getEndDate().getTime() - today.getTime().getTime();
        }
        else {
            diff  = this.getEndDate().getTime() - this.getStartDate().getTime();
        }

        long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

        if (days == 0) {
            return "Goal ends today!";
        }
        else if (days < 0) {
            return "Goal has ended";
        }
        else if (days == 1) {
            return "Goal ends tomorrow!";
        }
        else {
            return "Goal ends in "+days+" days";
        }
    }

    public String getAvgRide() {
        Calendar today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        long diff;
        if (today.getTime().after(this.getStartDate())) {
            diff            = this.getEndDate().getTime() - today.getTime().getTime();
        }
        else {
            diff            = this.getEndDate().getTime() - this.getStartDate().getTime();
        }

        long days           = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        double rideTodayDbl = this.getGoalDistance() - this.getCurrentDistance();
        double rideTodayAvg = this.round(rideTodayDbl / days, 1);

        return "Have to "+this.getActivityType().getShortName().toLowerCase()+" "+rideTodayAvg+" "+this.getUom().getName().toLowerCase()+" a day";
    }

    public Double getNumberFromEvent(Event event) {
        if (this.uom.getName().equals("Miles")) {
            return (double)event.getDistance();
        }
        else if (this.uom.getName().equals("Hours")) {
            return (double)event.getElapsedTime();
        }

        return new Double(0);
    }

    private double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    public void deleteEvent(Event event) {
        // Remove this guy
        this.events.remove(event);

        // Sort everything
        Collections.sort(this.events, new EventComparator());

        // Update our current goal
        Double removeDistance = this.getNumberFromEvent(event);
        this.currentDistance -= removeDistance;
    }

    public double getDistanceToday() {
        double distance = 0;

        Calendar today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        ArrayList<Event> events = this.getEvents();
        for (int ct = 0; ct < events.size(); ct++) {
            if (events.get(ct).getStartTime().after(today.getTime())) {
                distance += events.get(ct).getDistance();
            }
        }

        return distance;
    }

    public void setPersistentData() {
        Calendar today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        // Check to see if we already have a persistent event going
        PersistentEvent persistentEvent = null;
        for (int ct = 0; ct < persistentEvents.size(); ct++) {
            if (today.getTime().equals(persistentEvents.get(ct).getStartDate())   ||
                    today.getTime().equals(persistentEvents.get(ct).getEndDate()) ||
                    (today.getTime().after(persistentEvents.get(ct).getStartDate()) && today.getTime().before(persistentEvents.get(ct).getEndDate()))) {
                persistentEvent = persistentEvents.get(ct);
            }
        }

        // If we didn't find it, we need to add a new event
        if (persistentEvent == null) {
            // Add it
            persistentEvent = new PersistentEvent(this.getPersistentInterval());
            persistentEvents.add(persistentEvent);

            // Set the current goal to what it should be
            this.setGoalDistance(this.getGoalDistance() + this.getIncreaseOverTime());
        }

        this.startDate = persistentEvent.getStartDate();
        this.endDate   = persistentEvent.getEndDate();
    }

    public class EventComparator implements Comparator<Event> {
        @Override
        public int compare(Event o1, Event o2) {
            Event oo2 = o2;
            Event oo1 = o1;
            return oo2.getEndTime().compareTo(oo1.getEndTime());
        }
    }

    public void addEvent(Event event) {
        // Make sure we don't already have this activity
        int bFound = -1;
        for (int ct = 0; ct < events.size(); ct++) {
            if (events.get(ct).getId().equals(event.getId())) {
                bFound = ct;
            }
        }

        //If it was found, just update everything (by removing the old one and then reading this one)
        if (bFound != -1) {
            Double removeDistance = this.getNumberFromEvent(event);
            this.currentDistance -= removeDistance;
            events.remove(bFound);
        }

        // Add this guy
        this.events.add(event);

        // Sort everything
        Collections.sort(this.events, new EventComparator());

        // Update our current goal
        Double addDistance = this.getNumberFromEvent(event);
        this.currentDistance += addDistance;
    }

    public Event getMostRecentEvent() {
        if (events.size() == 0) {return null;}

        Event mostRecent = this.events.get(0);
        for (int ct = 0; ct < events.size(); ct++) {
            if (this.events.get(ct).getStartTime().after(mostRecent.getStartTime())) {mostRecent = this.events.get(ct);}
        }

        return mostRecent;
    }

    public double calculateDistanceRemain(Event event) {
        // So basically, start at the beginning of the events and work you're way to the front subtracting event distances
        double goal = this.getGoalDistance();
        ArrayList<Event> reversed = (ArrayList<Event>) this.events.clone();
        Collections.reverse(reversed);
        for (int ct = 0; ct < reversed.size(); ct++) {
            goal -= reversed.get(ct).getDistance();
            if (reversed.get(ct).getId().equals(event.getId())) {
                return this.round(goal, 1);
            }
        }

        return 0;
    }

    public double getTodayRemains() {
        Calendar today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        double riddenToday = 0;

        // Find out how much has been ridden today
        ArrayList<Event> events = this.getEvents();
        for (int ct = 0; ct < events.size(); ct++) {
            Event current = events.get(ct);
            if (current.getActivityDate().equals(today.getTime())) {
                riddenToday += current.getDistance();
            }
        }

        return Math.max(this.getTodayNeeded() - riddenToday, 0);
    }

    public double getTodayNeeded() {
        // Get today at midnight
        Calendar today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar endDate = new GregorianCalendar();
        endDate.setTime(this.getEndDate());
        endDate.set(Calendar.HOUR_OF_DAY, 0);
        endDate.set(Calendar.MINUTE, 0);
        endDate.set(Calendar.SECOND, 0);
        endDate.set(Calendar.MILLISECOND, 0);
        endDate.add(Calendar.HOUR, 24);

        // Calculate the days remaining
        long diff;
        if (today.getTime().after(this.getStartDate())) {
            diff            = endDate.getTime().getTime() - today.getTime().getTime();
        }
        else {
            diff            = endDate.getTime().getTime() - this.getStartDate().getTime();
        }

        long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        double rideTodayDbl = this.getGoalDistance() - this.getCurrentDistance();
        double completion   = (this.getCurrentDistance() / this.getGoalDistance()) * 100;
        double rideTodayAvg = 0;
        if (days == 0) {
            rideTodayAvg = GoalAdapter.round(rideTodayDbl, 1);
        }
        else {
            rideTodayAvg = GoalAdapter.round(rideTodayDbl / days, 1);
        }

        return rideTodayAvg;
    }
}
