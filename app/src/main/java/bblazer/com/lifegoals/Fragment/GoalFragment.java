package bblazer.com.lifegoals.Fragment;

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

import bblazer.com.lifegoals.Activity.MainActivity;
import bblazer.com.lifegoals.Activity.ViewGoalActivity;
import bblazer.com.lifegoals.Add.AddNewGoal;
import bblazer.com.lifegoals.LifeGoalsApplication;
import bblazer.com.lifegoals.Manager.EventManagerListener;
import bblazer.com.lifegoals.Manager.GoalManagerListener;
import bblazer.com.lifegoals.Objects.Goal.Goal;
import bblazer.com.lifegoals.Objects.Goal.GoalAdapter;
import bblazer.com.lifegoals.R;

/**
 * Created by bblazer on 4/12/16.
 */
public class GoalFragment extends Fragment implements EventManagerListener, GoalManagerListener {
    public  ListView       goalListView;
    private RelativeLayout emptyView;
    private ImageView      appIcon;
    public MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.goal_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        goalListView = (ListView)view.findViewById(R.id.goal_listview);
        emptyView    = (RelativeLayout)view.findViewById(R.id.empty_view);
        appIcon      = (ImageView)view.findViewById(R.id.app_icon);

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);  //0 means grayscale
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        appIcon.setColorFilter(cf);
        appIcon.setAlpha(128);
        mainActivity = ((LifeGoalsApplication)getActivity().getApplication()).mainActivity;
        mainActivity.goalManager.addListener(this);
        mainActivity.eventManager.addListener(this);

        goalListView.setAdapter(new GoalAdapter(mainActivity, mainActivity.goalManager));
        goalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Goal selectedGoal = mainActivity.goalManager.goals.get(position);
                Intent intent = new Intent(mainActivity, ViewGoalActivity.class);
                ViewGoalActivity.viewGoalStatic = selectedGoal;
                startActivity(intent);
            }
        });
        registerForContextMenu(goalListView);

        this.toggleGoalView();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.goal_listview) {
            menu.add("Delete Goal");
            menu.add("Edit Goal");
        }
    }

    public void toggleGoalView() {
        if (mainActivity.goalManager.goals.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            goalListView.setVisibility(View.GONE);
        }
        else {
            emptyView.setVisibility(View.GONE);
            goalListView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String menuItemString = (String)item.getTitle();

        if (menuItemString.equals("Delete Goal")) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
            Goal selectedGoal = mainActivity.goalManager.goals.get(info.position);
            mainActivity.goalManager.deleteGoal(selectedGoal);
        }
        else if (menuItemString.equals("Edit Goal")) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
            Goal selectedGoal = mainActivity.goalManager.goals.get(info.position);
            Intent intent = new Intent(mainActivity, AddNewGoal.class);
            AddNewGoal.editGoal = selectedGoal;
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
                ((GoalAdapter)goalListView.getAdapter()).notifyDataSetChanged();
                toggleGoalView();
            }
        });
    }

    @Override
    public void goalsUpdated(int numUpdated, int numDeleted, int numAdded) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((GoalAdapter)goalListView.getAdapter()).notifyDataSetChanged();
                toggleGoalView();
            }
        });
    }
}
