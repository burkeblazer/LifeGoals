package bblazer.com.lifegoals.Objects.Event;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import bblazer.com.lifegoals.Objects.ActivityType.ActivityType;
import bblazer.com.lifegoals.Objects.ActivityType.HumanActivity;
import bblazer.com.lifegoals.Objects.UOM;

/**
 * Created by bblazer on 4/17/16.
 */
public class HumanEvent extends Event {
    HumanActivity humanActivity;
    String humanActivityID;

    public HumanEvent(HumanActivity currentActivity) {
        DateFormat df       = new SimpleDateFormat("yyyy-MM-d'T'H:m:ss", Locale.US);
        DateFormat ddf      = new SimpleDateFormat("MM/dd/yy", Locale.US);

        this.id             = currentActivity.getId();
        this.humanActivityID = currentActivity.getId();
        this.elapsedTime    = currentActivity.getDuration()/60; // Time comes in seconds so /60 to get minutes
        try {
            this.startTime  = df.parse(currentActivity.getStartTime());
            Calendar myCal  = new GregorianCalendar();
            myCal.setTime(this.startTime);
            myCal.add(Calendar.MINUTE, this.elapsedTime);
            this.endTime    = myCal.getTime();
            String activityDateStr = ddf.format(this.startTime);
            this.activityDate = ddf.parse(activityDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.eventType      = currentActivity.getSource();
        this.humanActivity  = currentActivity;

        this.distance       = (double)currentActivity.getDistance()*0.000621371;
        this.uom            = new UOM("Miles", "m");

        this.comments       = "Imported from "+currentActivity.getSource();

        String timeOfDayStr = "";
        Calendar c    = Calendar.getInstance();
        c.setTime(this.startTime);
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay >= 0 && timeOfDay < 12){
            timeOfDayStr = "Morning";
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            timeOfDayStr = "Afternoon";
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            timeOfDayStr = "Evening";
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            timeOfDayStr = "Night";
        }

        this.eventName      = timeOfDayStr+" "+currentActivity.getType();
        this.activityType   = ActivityType.convertHumanActivityType(currentActivity.getType());
        this.calsBurned     = (int)currentActivity.getCalories();
    }
}