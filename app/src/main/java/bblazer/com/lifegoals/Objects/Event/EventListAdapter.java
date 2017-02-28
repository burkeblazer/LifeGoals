package bblazer.com.lifegoals.Objects.Event;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import bblazer.com.lifegoals.EventManager;
import bblazer.com.lifegoals.Objects.Goal.Goal;
import bblazer.com.lifegoals.R;
import bblazer.com.lifegoals.ViewGoalActivity;

/**
 * Created by bblazer on 4/9/16.
 */
public class EventListAdapter extends BaseAdapter {
    private Context context;
    private Goal viewGoal;
    private LayoutInflater mInflater;
    private EventManager eventManager = null;

    public EventListAdapter(ViewGoalActivity viewGoalActivity, Goal viewGoal) {
        mInflater     = (LayoutInflater)viewGoalActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.viewGoal = viewGoal;
        context       = viewGoalActivity;
    }

    public EventListAdapter(Context viewGoalActivity, EventManager eventManager) {
        mInflater         = (LayoutInflater)viewGoalActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.eventManager = eventManager;
        context           = viewGoalActivity;
    }

    @Override
    public int getCount() {
        if (eventManager == null) {
            return viewGoal.getEvents().size();
        }
        else {
            return eventManager.getEvents().size();
        }
    }

    @Override
    public Event getItem(int position) {
        if (eventManager == null) {
            return viewGoal.getEvents().get(position);
        }
        else {
            return eventManager.getEvents().get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class Holder
    {
        ImageView goalImage;
        TextView  eventName;
        TextView  startEnd;
        TextView  eventDate;
        TextView  timeEllapsed;
        TextView  distance;
        TextView  distanceRemain;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder             = null;
        Event event               = getItem(position);
        if (convertView == null) {
            holder                = new Holder();
            convertView           = mInflater.inflate(R.layout.event_row, null);
            holder.goalImage      = (ImageView)convertView.findViewById(R.id.goal_image);
            holder.eventName      = (TextView)convertView.findViewById(R.id.event_name);
            holder.startEnd       = (TextView)convertView.findViewById(R.id.start_end);
            holder.eventDate      = (TextView)convertView.findViewById(R.id.event_row_date);
            holder.timeEllapsed   = (TextView)convertView.findViewById(R.id.time_ellapsed);
            holder.distance       = (TextView)convertView.findViewById(R.id.distance);
            holder.distanceRemain = (TextView)convertView.findViewById(R.id.distance_remain);
            convertView.setTag(holder);
        } else {
            holder = (Holder)convertView.getTag();
        }

        // Set icon
        if (event.getEventType().toLowerCase().equals("strava")) {
            holder.goalImage.setBackgroundResource(R.drawable.strava_event);
        }
        else {
            holder.goalImage.setBackgroundResource(R.drawable.manual_event);
        }

        DateFormat df  = new SimpleDateFormat("hh:mm:ss a", Locale.US);
        DateFormat ddf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        holder.eventName.setText(event.getEventName());
        holder.startEnd.setText(df.format(event.getStartTime())+ " - "+df.format(event.getEndTime()));
        holder.eventDate.setText(ddf.format(event.getEndTime()));
        int minuteRd  = Math.round(event.getElapsedTime());
        holder.timeEllapsed.setText(minuteRd+" mins");
        holder.distance.setText(event.getDistance()+" "+event.getUom().getName().toLowerCase());
        if (viewGoal != null) {
            holder.distanceRemain.setText(viewGoal.calculateDistanceRemain(event) + " remain");
        }
        else {
            holder.distanceRemain.setVisibility(View.GONE);
        }

        return convertView;
    }
}
