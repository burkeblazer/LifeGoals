package bblazer.com.lifegoals.Objects.Event;

import java.util.Date;

import bblazer.com.lifegoals.Objects.ActivityType.ActivityType;
import bblazer.com.lifegoals.Objects.UOM;

/**
 * Created by bblazer on 4/8/16.
 */
public class Event {
    String       id;
    String       eventName;
    ActivityType activityType;
    Date         activityDate;
    Date         startTime;
    Date         endTime;
    Double       distance;
    int          elapsedTime; // Minutes
    UOM          uom;
    String       eventType;
    String       comments;
    int          calsBurned;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Double getDistance() {
        Double roundDistance = this.round(distance, 1);

        return roundDistance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(int elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public UOM getUom() {
        return uom;
    }

    public void setUom(UOM uom) {
        this.uom = uom;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Date getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(Date activityDate) {
        this.activityDate = activityDate;
    }

    public int getCalsBurned() {
        return calsBurned;
    }

    public void setCalsBurned(int calsBurned) {
        this.calsBurned = calsBurned;
    }
}
