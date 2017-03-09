package bblazer.com.lifegoals.Fragment;

import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.DecoDrawEffect;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import bblazer.com.lifegoals.Activity.MainActivity;
import bblazer.com.lifegoals.LifeGoalsApplication;
import bblazer.com.lifegoals.Manager.DailyActivityType;
import bblazer.com.lifegoals.Manager.EventManagerListener;
import bblazer.com.lifegoals.Manager.GoalManagerListener;
import bblazer.com.lifegoals.R;

/**
 * Created by bblazer on 4/12/16.a
 */
public class OverviewFragment extends Fragment implements EventManagerListener, GoalManagerListener {
    public MainActivity mainActivity;
    DecoView              decoView;
    ArrayList<SeriesItem> seriesItems   = new ArrayList<SeriesItem>();
    ArrayList<Integer>    seriesIndexes = new ArrayList<Integer>();
    ArrayList<View>       seriesViews   = new ArrayList<View>();
    int                   totalProgressIndex;
    RelativeLayout        activityViewContainer;
    LayoutInflater        inflater;
    RelativeLayout        mainLayout;
    View                  totalProgressActivityView;
    RelativeLayout        finishedView;
    RelativeLayout        emptyView;
    ImageView             emptyAppIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflater = inflater;

        return inflater.inflate(R.layout.overview_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity = ((LifeGoalsApplication)getActivity().getApplication()).mainActivity;
        mainActivity.eventManager.addListener(this);
        mainActivity.goalManager.addListener(this);

        decoView              = (DecoView)view.findViewById(R.id.decoview);
        activityViewContainer = (RelativeLayout)view.findViewById(R.id.activity_view_container);
        finishedView          = (RelativeLayout)view.findViewById(R.id.finished_view);
        emptyView             = (RelativeLayout)view.findViewById(R.id.empty_view);
        seriesItems           = new ArrayList<SeriesItem>();
        seriesIndexes         = new ArrayList<Integer>();
        seriesViews           = new ArrayList<View>();
        totalProgressIndex    = -1;
        mainLayout            = (RelativeLayout) view;
        emptyAppIcon          = (ImageView)view.findViewById(R.id.empty_app_icon);

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);  //0 means grayscale
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        emptyAppIcon.setColorFilter(cf);
        emptyAppIcon.setAlpha(128);

        decoView.addSeries(new SeriesItem.Builder(Color.GRAY)
                .setRange(0, 100, 100)
                .setLineWidth(34f)
                .build());

        this.refreshDailyActivities();
    }

    public void refreshDailyActivities() {
        // Set everything to null
        seriesItems           = new ArrayList<SeriesItem>();
        seriesIndexes         = new ArrayList<Integer>();
        seriesViews           = new ArrayList<View>();
        totalProgressIndex    = -1;
        decoView.deleteAll();

        activityViewContainer.removeAllViews();

        this.setDailyActivities();
    }

    public class DailyActivityTypeComparator implements Comparator<DailyActivityType> {
        @Override
        public int compare(DailyActivityType lhs, DailyActivityType rhs) {
            return rhs.percentComplete - lhs.percentComplete;
        }
    }

    public void setDailyActivities() {
        // Get today's overview
        ArrayList<DailyActivityType> dailyActivities = mainActivity.goalManager.getDailyActivities();

        if (dailyActivities.size() == 0) {this.showEmptyView();}
        else {this.hideEmptyView();}

        // Show gray background bar
        SeriesItem grayItem = new SeriesItem.Builder(Color.GRAY)
                .setRange(0, 100, 100)
                .setLineWidth(34f)
                .build();
        decoView.addSeries(grayItem);

        // Sort the daily activities by percent complete desc
        Collections.sort(dailyActivities, new DailyActivityTypeComparator());

        int delay        = 1000;
        int totalPercent = 0;
        // Loop through each of the sorted activities and add them to the view
        for (int ct = 0; ct < dailyActivities.size(); ct++) {
            SeriesItem seriesItem  = null;
            int        seriesIndex = 0;
            View dailyActivityView = null;

            seriesItem = new SeriesItem.Builder(Color.parseColor(dailyActivities.get(ct).getActivityType().getColor()))
                    .setRange(0, 100, 0)
                    .setLineWidth(34f)
                    .build();

            seriesItems.add(seriesItem);
            seriesIndex = decoView.addSeries(seriesItem);
            seriesIndexes.add(seriesIndex);

            // Add the activity view to the center
            dailyActivityView = inflater.inflate(R.layout.daily_activity_view, activityViewContainer, false);
            dailyActivityView.setVisibility(View.INVISIBLE);
            ImageView icon = (ImageView)dailyActivityView.findViewById(R.id.daily_activity_view_image);
            final TextView text  = (TextView)dailyActivityView.findViewById(R.id.daily_activity_view_text);
            text.setText(dailyActivities.get(ct).getPercentComplete()+"%");
            if (dailyActivities.get(ct).getActivityType().getFullName().equals("Biking")) {
                icon.setImageResource(R.drawable.bike_icon);
            }
            else if (dailyActivities.get(ct).getActivityType().getFullName().equals("Elliptical")) {
                icon.setImageResource(R.drawable.elliptical_icon);
            }
            else {
                icon.setImageResource(R.drawable.running_icon);
            }

            activityViewContainer.addView(dailyActivityView);
            seriesViews.add(dailyActivityView);

            final SeriesItem finalSeriesItem = seriesItem;
            seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
                @Override
                public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                    float percentFilled = ((currentPosition - finalSeriesItem.getMinValue()) / (finalSeriesItem.getMaxValue() - finalSeriesItem.getMinValue()));
                    text.setText(String.format("%.0f%%", percentFilled * 100f));
                }

                @Override
                public void onSeriesItemDisplayProgress(float percentComplete) {

                }
            });

            int currentPercent = 0;
            currentPercent = dailyActivities.get(ct).getPercentComplete();

            totalPercent += currentPercent;

            if (currentPercent == 0) {
                decoView.addEvent(new DecoEvent.Builder(100).setDelay(delay).setIndex(seriesIndex).setDuration(2500).setListener(new DecoEvent.ExecuteEventListener() {
                    @Override
                    public void onEventStart(DecoEvent decoEvent) {
                        Animation in = AnimationUtils.makeInAnimation(mainActivity, true);

                        View currentView = seriesViews.get(decoEvent.getIndexPosition() - 1);

                        currentView.startAnimation(in);
                        currentView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onEventEnd(DecoEvent decoEvent) {

                    }
                }).build());

                decoView.addEvent(new DecoEvent.Builder(0).setIndex(seriesIndex).setDelay(delay+2500).setDuration(2500).setListener(new DecoEvent.ExecuteEventListener() {
                    @Override
                    public void onEventStart(DecoEvent decoEvent) {

                    }

                    @Override
                    public void onEventEnd(DecoEvent decoEvent) {
                        Animation out = AnimationUtils.makeOutAnimation(mainActivity, true);

                        View currentView = seriesViews.get(decoEvent.getIndexPosition() - 1);

                        currentView.startAnimation(out);
                        currentView.setVisibility(View.INVISIBLE);
                    }
                }).build());
            }
            else {
                decoView.addEvent(new DecoEvent.Builder(dailyActivities.get(ct).getPercentComplete()).setDelay(delay).setIndex(seriesIndex).setDuration(5000).setListener(new DecoEvent.ExecuteEventListener() {
                    @Override
                    public void onEventStart(DecoEvent decoEvent) {
                        Animation in = AnimationUtils.makeInAnimation(mainActivity, true);

                        View currentView = seriesViews.get(decoEvent.getIndexPosition() - 1);

                        currentView.startAnimation(in);
                        currentView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onEventEnd(DecoEvent decoEvent) {
                        Animation out = AnimationUtils.makeOutAnimation(mainActivity, true);

                        View currentView = seriesViews.get(decoEvent.getIndexPosition() - 1);

                        currentView.startAnimation(out);
                        currentView.setVisibility(View.INVISIBLE);
                    }
                }).build());
            }

            delay += 6000;
        }

        if (dailyActivities.size() == 0) {return;}

        this.setTotalProgress(delay, ((totalPercent / dailyActivities.size())));
    }

    public void setTotalProgress(int delay, final int value) {
        final SeriesItem seriesItem = new SeriesItem.Builder(Color.argb(255, 255, 215, 0))
            .setRange(0, 100, 0)
            .setLineWidth(34f)
            .setInset(new PointF(34f, 34f))
            .build();

        totalProgressIndex = decoView.addSeries(seriesItem);

        // Add the activity view to the center
        totalProgressActivityView = inflater.inflate(R.layout.daily_activity_view, activityViewContainer, false);
        totalProgressActivityView.setVisibility(View.INVISIBLE);
        ImageView icon = (ImageView)totalProgressActivityView.findViewById(R.id.daily_activity_view_image);
        final TextView text  = (TextView)totalProgressActivityView.findViewById(R.id.daily_activity_view_text);
        text.setText(value+"%");
        icon.setImageResource(R.drawable.app_icon);

        activityViewContainer.addView(totalProgressActivityView);

        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                float percentFilled = ((currentPosition - seriesItem.getMinValue()) / (seriesItem.getMaxValue() - seriesItem.getMinValue()));
                text.setText(String.format("%.0f%%", percentFilled * 100f));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        if (value == 0) {
            decoView.addEvent(new DecoEvent.Builder(100).setDelay(delay).setIndex(totalProgressIndex).setDuration(2500).setListener(new DecoEvent.ExecuteEventListener() {
                @Override
                public void onEventStart(DecoEvent decoEvent) {
                    Animation in = AnimationUtils.makeInAnimation(mainActivity, true);

                    totalProgressActivityView.startAnimation(in);
                    totalProgressActivityView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEventEnd(DecoEvent decoEvent) {

                }
            }).build());

            decoView.addEvent(new DecoEvent.Builder(0).setIndex(totalProgressIndex).setDelay(delay+2500).setDuration(2500).setListener(new DecoEvent.ExecuteEventListener() {
                @Override
                public void onEventStart(DecoEvent decoEvent) {

                }

                @Override
                public void onEventEnd(DecoEvent decoEvent) {
                    Animation out = AnimationUtils.makeOutAnimation(mainActivity, true);

                    totalProgressActivityView.startAnimation(out);
                    totalProgressActivityView.setVisibility(View.INVISIBLE);

                    showSlideViews();
                }
            }).build());
        }
        else {
            decoView.addEvent(new DecoEvent.Builder(value).setDelay(delay).setIndex(totalProgressIndex).setDuration(5000).setListener(new DecoEvent.ExecuteEventListener() {
                @Override
                public void onEventStart(DecoEvent decoEvent) {
                    Animation in = AnimationUtils.makeInAnimation(mainActivity, true);

                    totalProgressActivityView.startAnimation(in);
                    totalProgressActivityView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEventEnd(DecoEvent decoEvent) {
                    Animation out = AnimationUtils.makeOutAnimation(mainActivity, true);

                    totalProgressActivityView.startAnimation(out);
                    totalProgressActivityView.setVisibility(View.INVISIBLE);

                    if (value == 100) {
                        decoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_EXPLODE)
                                .setIndex(totalProgressIndex)
                                .setDuration(3000)
                                .setDisplayText("CONGRATULATIONS!")
                                .setListener(new DecoEvent.ExecuteEventListener() {
                                    @Override
                                    public void onEventStart(DecoEvent decoEvent) {

                                    }

                                    @Override
                                    public void onEventEnd(DecoEvent decoEvent) {
                                        showFinishedView();
                                    }
                                })
                                .build());
                    } else {
                        showSlideViews();
                    }
                }
            }).build());
        }
    }

    public void showSlideViews() {
        ArrayList<DailyActivityType> dailyActivities = mainActivity.goalManager.getDailyActivities();

        for (int ct = 0; ct < dailyActivities.size(); ct++) {
            View dailyActivityView = null;
            for (int ct2 = 0; ct2 < seriesItems.size(); ct2++) {
                if (seriesItems.get(ct2).getColor() == Color.parseColor(dailyActivities.get(ct).getActivityType().getColor())) {
                    dailyActivityView = seriesViews.get(ct2);
                }
            }

            String atName = dailyActivities.get(ct).getActivityType().getShortName();
            double remain = dailyActivities.get(ct).getTodayRemains();
            String uom    = dailyActivities.get(ct).uom.getName().toLowerCase();
            TextView text  = (TextView)dailyActivityView.findViewById(R.id.daily_activity_view_text);
            if (remain == 0) {
                text.setTextColor(Color.argb(255, 0, 270, 0));
                text.setText("Completed!");
            }
            else {
                text.setTextSize(20);
                text.setText(atName + " " + remain + " more " + uom + " today");
            }
        }

        animateView(0);
    }

    public void animateView(int index) {
        if (index+1 > seriesViews.size()) {return;}
        final View currentView = seriesViews.get(index);
        if (index == seriesViews.size()-1) {index = 0;}
        else {index++;}
        final int nextIndex = index;
        Animation in = AnimationUtils.makeInAnimation(mainActivity, true);
        in.setDuration(1000);
        in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation out = AnimationUtils.makeOutAnimation(mainActivity, true);
                out.setDuration(1000);
                out.setStartOffset(3000);

                out.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        animateView(nextIndex);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                currentView.startAnimation(out);
                currentView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        currentView.startAnimation(in);
        currentView.setVisibility(View.VISIBLE);
    }

    public void showFinishedView() {
        decoView.setVisibility(View.GONE);

        Animation in = AnimationUtils.makeInAnimation(mainActivity, true);
        finishedView.startAnimation(in);
        finishedView.setVisibility(View.VISIBLE);
    }

    public void showEmptyView() {
        decoView.setVisibility(View.GONE);

        Animation in = AnimationUtils.makeInAnimation(mainActivity, true);
        emptyView.startAnimation(in);
        emptyView.setVisibility(View.VISIBLE);
    }

    public void hideEmptyView() {
        decoView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }

    @Override
    public void goalsUpdated(int numUpdated, int numDeleted, int numAdded) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshDailyActivities();
            }
        });
    }

    @Override
    public void eventsUpdated() {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshDailyActivities();
            }
        });
    }
}
