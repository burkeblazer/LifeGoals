package bblazer.com.lifegoals;

import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import bblazer.com.lifegoals.Objects.Event.Event;
import bblazer.com.lifegoals.Objects.Event.EventListAdapter;

/**
 * Created by bblazer on 4/12/16.
 */
public class EventFragment extends Fragment implements EventManagerListener, GoalManagerListener {
    public ListView        eventListView;
    private RelativeLayout emptyView;
    private ImageView      appIcon;
    public MainActivity    mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventListView = (ListView)view.findViewById(R.id.event_listview);
        emptyView     = (RelativeLayout)view.findViewById(R.id.empty_view);
        appIcon       = (ImageView)view.findViewById(R.id.app_icon);

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);  //0 means grayscale
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        appIcon.setColorFilter(cf);
        appIcon.setAlpha(128);
        mainActivity = ((LifeGoalsApplication)getActivity().getApplication()).mainActivity;
        mainActivity.goalManager.addListener(this);
        mainActivity.eventManager.addListener(this);

        eventListView.setAdapter(new EventListAdapter(mainActivity, mainActivity.eventManager));
        registerForContextMenu(eventListView);

        this.toggleGoalView();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.event_listview) {
            menu.add("Delete Event");
            menu.add("Edit Event");
        }
    }

    public void toggleGoalView() {
        if (mainActivity.eventManager.events.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            eventListView.setVisibility(View.GONE);
        }
        else {
            emptyView.setVisibility(View.GONE);
            eventListView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String menuItemString = (String)item.getTitle();
        if (menuItemString.equals("Delete Event")) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
            Event selectedEvent = mainActivity.eventManager.events.get(info.position);
            mainActivity.eventManager.deleteEvent(selectedEvent);
        }
        else if (menuItemString.equals("Edit Event")) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
            Event selectedEvent = mainActivity.eventManager.events.get(info.position);
            Intent intent = new Intent(mainActivity, AddEvent.class);
            AddEvent.editEvent = selectedEvent;
            startActivity(intent);
        }
        else {
            return false;
        }

        return true;
    }

    @Override
    public void eventsUpdated() {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toggleGoalView();
                ((EventListAdapter)eventListView.getAdapter()).notifyDataSetChanged();
            }
        });
    }

    @Override
    public void goalsUpdated(int numUpdated, int numDeleted, int numAdded) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toggleGoalView();
                ((EventListAdapter)eventListView.getAdapter()).notifyDataSetChanged();
            }
        });
    }
}