package bblazer.com.lifegoals.Objects.Goal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import bblazer.com.lifegoals.GoalManager;
import bblazer.com.lifegoals.Objects.Event.Event;
import bblazer.com.lifegoals.R;

public class GoalAdapter extends BaseAdapter {
    private Context context;
    private GoalManager goalManager;
    private LayoutInflater mInflater;

    public GoalAdapter (Context context, GoalManager goalManager) {
        this.context     = context;
        this.goalManager = goalManager;
        mInflater        = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return goalManager.goals.size();
    }

    @Override
    public Goal getItem(int position) {
        return goalManager.goals.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(goalManager.goals.get(position).getId());
    }

    public class Holder
    {
        ImageView   goalImage;
        TextView    rideToday;
        TextView    daysRemaining;
        TextView    percentComplete;
        TextView    lastUpdated;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder                  = null;
        Goal goal                      = getItem(position);
        if (convertView == null) {
            holder                     = new Holder();
            convertView                = mInflater.inflate(R.layout.goal_row, null);
            holder.goalImage           = (ImageView)convertView.findViewById(R.id.goal_image);
            holder.rideToday           = (TextView)convertView.findViewById(R.id.ride_today);
            holder.daysRemaining       = (TextView)convertView.findViewById(R.id.days_remaining);
            holder.percentComplete     = (TextView)convertView.findViewById(R.id.percent_complete);
            holder.lastUpdated         = (TextView)convertView.findViewById(R.id.last_updated);
            convertView.setTag(holder);
        } else {
            holder = (Holder)convertView.getTag();
        }

        // Set icon
        if (goal.getActivityType().getFullName().equals("Biking")) {
            holder.goalImage.setBackgroundResource(R.drawable.bike_icon);
        }
        else if (goal.getActivityType().getFullName().equals("Running")) {
            holder.goalImage.setBackgroundResource(R.drawable.running_icon);
        }
        else if (goal.getActivityType().getFullName().equals("Elliptical")) {
            holder.goalImage.setBackgroundResource(R.drawable.elliptical_icon);
        }

        // Get today at midnight
        Calendar today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar endDate = new GregorianCalendar();
        endDate.setTime(goal.getEndDate());
        endDate.set(Calendar.HOUR_OF_DAY, 0);
        endDate.set(Calendar.MINUTE, 0);
        endDate.set(Calendar.SECOND, 0);
        endDate.set(Calendar.MILLISECOND, 0);
        endDate.add(Calendar.HOUR, 24);

        // Calculate the days remaining
        long diff;
        if (today.getTime().after(goal.getStartDate())) {
            diff            = endDate.getTime().getTime() - today.getTime().getTime();
        }
        else {
            diff            = endDate.getTime().getTime() - goal.getStartDate().getTime();
        }

        long days           = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        double rideTodayDbl = goal.getGoalDistance() - goal.getCurrentDistance();
        double completion   = (goal.getCurrentDistance() / goal.getGoalDistance()) * 100;
        double rideTodayAvg = 0;
        if (days == 0) {
            rideTodayAvg = GoalAdapter.round(rideTodayDbl, 1);
        }
        else {
            rideTodayAvg = GoalAdapter.round(rideTodayDbl / days, 1);
        }
        holder.percentComplete.setText((int) completion + "% complete");

        holder.daysRemaining.setVisibility(View.VISIBLE);
        holder.lastUpdated.setVisibility(View.VISIBLE);
        holder.percentComplete.setVisibility(View.VISIBLE);

        // Goal has yet to start (start date is after today)
        if (today.getTime().before(goal.getStartDate())) {
            long startDiff   = goal.getStartDate().getTime() - today.getTime().getTime();
            long daysRemainL = TimeUnit.DAYS.convert(startDiff, TimeUnit.MILLISECONDS);
            if (daysRemainL == 1) {
                holder.lastUpdated.setText("Prepare yourself, you start tomorrow!");
            }
            else {
                holder.lastUpdated.setText("Prepare, you start in " + daysRemainL + " days");
            }
            holder.rideToday.setText("You'll have to "+goal.getActivityType().getShortName().toLowerCase()+" "+rideTodayAvg+" "+goal.getUom().getName().toLowerCase()+" daily");
            holder.daysRemaining.setText(days+" days remain");
        }
        else if (days >= 1) { // Goal is in session
            Event mostRecent    = goal.getMostRecentEvent();
            if (mostRecent == null) {
                holder.lastUpdated.setText("Today's the day, get it going!");
                holder.rideToday.setText(goal.getActivityType().getShortName()+" "+rideTodayAvg +" "+goal.getUom().getName().toLowerCase()+" today, "+this.getMotivationalWords());
            }
            else {
                long lastDiff  = today.getTime().getTime() - mostRecent.getActivityDate().getTime();
                long lastDiffL = TimeUnit.DAYS.convert(lastDiff, TimeUnit.MILLISECONDS);
                if (lastDiffL <= 0) {
                    double distanceToday      = goal.getDistanceToday();
                    double roundDistanceToday = GoalAdapter.round(distanceToday, 1);
                    holder.lastUpdated.setText(goal.getActivityType().getPastName()+" "+roundDistanceToday+" "+mostRecent.getUom().getName().toLowerCase()+" today, "+this.getPraisingWords());

                    // Rode it good today
                    if (distanceToday >= rideTodayAvg) {
                        holder.rideToday.setText("Completed today, "+this.getPraisingWords());
                    }
                    else { // Didnt ride it good
                        double difference = GoalAdapter.round(rideTodayAvg - distanceToday, 1);
                        holder.rideToday.setText(goal.getActivityType().getShortName()+" "+difference+" more "+goal.getUom().getName().toLowerCase()+" today, "+this.getMotivationalWords());
                    }
                }
                else if (lastDiffL <= 1) {
                    holder.lastUpdated.setText(goal.getActivityType().getPastName()+" "+mostRecent.getDistance()+" "+mostRecent.getUom().getName().toLowerCase() + " last time, "+this.getPraisingWords());
                    holder.rideToday.setText(goal.getActivityType().getShortName()+" "+rideTodayAvg +" "+goal.getUom().getName().toLowerCase()+" today, "+this.getMotivationalWords());
                }
                else {
                    holder.lastUpdated.setText(goal.getActivityType().getPastName()+" "+mostRecent.getDistance()+" "+mostRecent.getUom().getName().toLowerCase() + "  last time, "+this.getPraisingWords());
                    holder.rideToday.setText(goal.getActivityType().getShortName()+" "+rideTodayAvg +" "+goal.getUom().getName().toLowerCase()+" today, "+this.getMotivationalWords());
                }
            }

            if (days == 1) {
                holder.daysRemaining.setText(days+" day remains");
            }
            else {
                holder.daysRemaining.setText(days+" days remain");
            }
        }
        else { // It's over
            if (goal.getCurrentDistance() < goal.getGoalDistance()) { // Didn't make it
                holder.rideToday.setText("Better luck next time!");
                holder.percentComplete.setVisibility(View.GONE);
            }
            else {
                holder.rideToday.setText("Congratulations you did it!");
            }

            holder.daysRemaining.setVisibility(View.GONE);
            holder.lastUpdated.setVisibility(View.GONE);
        }

        return convertView;
    }

    private String getPraisingWords() {
        String[] words = new String[]{"noice!","way to go!","my hero!"};

        Random rand   = new Random();
        int range     = words.length - 1;
        int randomNum = rand.nextInt(range);

        return words[randomNum];
    }

    private String getMotivationalWords() {
        String[] words = new String[]{"let's go!","you got this!","get some!"};

        Random rand   = new Random();
        int range     = words.length - 1;
        int randomNum = rand.nextInt(range);

        return words[randomNum];
    }

    public static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}