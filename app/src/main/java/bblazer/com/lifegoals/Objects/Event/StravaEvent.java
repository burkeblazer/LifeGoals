package bblazer.com.lifegoals.Objects.Event;

import org.jstrava.entities.activity.Activity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import bblazer.com.lifegoals.Objects.ActivityType.ActivityType;
import bblazer.com.lifegoals.Objects.UOM;

/**
 * Created by bblazer on 4/8/16.
 */
public class StravaEvent extends Event {
    Activity stravaActivity;

    public StravaEvent(Activity currentActivity) {
        DateFormat df       = new SimpleDateFormat("yyyy-MM-d'T'H:m:ss", Locale.US);
        DateFormat ddf      = new SimpleDateFormat("MM/dd/yy", Locale.US);

        this.eventName      = currentActivity.getName();
        this.id             = Integer.toString(currentActivity.getId());
        this.elapsedTime    = currentActivity.getMoving_time()/60; // Time comes in seconds so /60 to get minutes
        this.activityType   = ActivityType.convertStravaActivityType(currentActivity.getType());
        try {
            this.startTime  = df.parse(currentActivity.getStart_date_local());
            Calendar myCal  = new GregorianCalendar();
            myCal.setTime(this.startTime);
            myCal.add(Calendar.MINUTE, this.elapsedTime);
            this.endTime    = myCal.getTime();
            String activityDateStr = ddf.format(this.startTime);
            this.activityDate = ddf.parse(activityDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.eventType      = "Strava";
        this.stravaActivity = currentActivity;

        this.distance       = (double)currentActivity.getDistance()*0.000621371;
        this.uom            = new UOM("Miles", "m");
        this.comments       = "Imported from Strava";
    }
}
