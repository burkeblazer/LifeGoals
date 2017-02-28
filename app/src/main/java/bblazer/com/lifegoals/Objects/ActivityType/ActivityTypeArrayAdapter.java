package bblazer.com.lifegoals.Objects.ActivityType;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import bblazer.com.lifegoals.R;

public class ActivityTypeArrayAdapter extends ArrayAdapter<ActivityType> {
    ArrayList<ActivityType> activityTypes;
    LayoutInflater mInflater;

    public ActivityTypeArrayAdapter(Context context) {
        super(context, R.layout.activity_type_dropdown_row, R.id.activity_name);
        mInflater     = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        activityTypes = ActivityType.getActivityTypes();
    }

    @Override
    public int getCount() {
        return activityTypes.size();
    }

    @Override
    public ActivityType getItem(int position) {
        return activityTypes.get(position);
    }



    @Override
    public int getPosition(ActivityType item) {
        int position = 0;
        for (int ct = 0; ct < activityTypes.size(); ct++) {
            if (item.getFullName().equals(activityTypes.get(ct).getFullName())) {
                position = ct;
            }
        }
        return position;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        Holder holder             = null;
        ActivityType at           = getItem(position);
        if (convertView == null) {
            holder                = new Holder();
            convertView           = mInflater.inflate(R.layout.activity_type_dropdown_row, null);
            holder.activityName   = (TextView)convertView.findViewById(R.id.activity_name);
            holder.activityIcon   = (ImageView)convertView.findViewById(R.id.activity_icon);
            holder.activityColor  = (LinearLayout)convertView.findViewById(R.id.activity_color);
            convertView.setTag(holder);
        } else {
            holder = (Holder)convertView.getTag();
        }

        holder.activityIcon.setBackgroundResource(at.getResourceID());
        holder.activityName.setText(at.getFullName());
        holder.activityColor.setBackgroundColor(Color.parseColor(at.getColor()));

        return convertView;
    }

    public class Holder
    {
        TextView activityName;
        ImageView activityIcon;
        LinearLayout activityColor;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder             = null;
        ActivityType at           = getItem(position);
        if (convertView == null) {
            holder                = new Holder();
            convertView           = mInflater.inflate(R.layout.activity_type_dropdown_row, null);
            holder.activityName   = (TextView)convertView.findViewById(R.id.activity_name);
            holder.activityIcon   = (ImageView)convertView.findViewById(R.id.activity_icon);
            holder.activityColor  = (LinearLayout)convertView.findViewById(R.id.activity_color);
            convertView.setTag(holder);
        } else {
            holder = (Holder)convertView.getTag();
        }

        holder.activityIcon.setBackgroundResource(at.getResourceID());
        holder.activityName.setText(at.getFullName());
        holder.activityColor.setBackgroundColor(Color.parseColor(at.getColor()));

        return convertView;
    }
}
