package bblazer.com.lifegoals;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import bblazer.com.lifegoals.Integrations.HumanIntegration;
import bblazer.com.lifegoals.Integrations.StravaIntegration;


public class MainActivity extends AppCompatActivity implements GoalManagerListener {
    static final String LOG_TAG = "MainActivity";

    class FragmentItem {
        String   fragmentName;
        Fragment fragment;

        public FragmentItem(String fragmentName, Fragment fragment) {
            this.fragmentName = fragmentName;
            this.fragment     = fragment;
        }

        public String getFragmentName() {
            return fragmentName;
        }

        public void setFragmentName(String fragmentName) {
            this.fragmentName = fragmentName;
        }

        public Fragment getFragment() {
            return fragment;
        }

        public void setFragment(Fragment fragment) {
            this.fragment = fragment;
        }
    }

    ViewPager               mViewPager;
    public GoalManager      goalManager;
    public EventManager     eventManager;
    public MealManager      mealManager;
    public WeightManager    weightManager;
    public String           stravaUserToken;
    public String           humanAPIUserID;
    public String           humanAPIUserToken;
    public String           humanAPIAcessToken;
    StravaIntegration       stravaIntegration;
    ArrayList<FragmentItem> tabFragments = new ArrayList<FragmentItem>();
    OverviewFragment        overviewFragment;
    GoalFragment            goalsFragment;
    EventFragment           eventsFragment;
    FoodFragment            foodsFragment;
    FoodOverview            foodOverviewFragment;
    FloatingActionButton    addEventButton;
    FloatingActionButton    addGoalButton;
    FloatingActionButton    addFoodButton;
    FloatingActionButton    timeToEat;
    FloatingActionButton    recordMeal;
    HumanIntegration        humanIntegration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Specify that tabs should be displayed in the action bar.
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Make main activity available on the application
        ((LifeGoalsApplication)this.getApplication()).mainActivity = this;

        // Grab integrations
        SharedPreferences settings = getSharedPreferences("LifeGoals", 0);
        this.stravaUserToken       = settings.getString("strava_user_token", "");
        stravaIntegration          = new StravaIntegration(this);

        this.humanAPIUserID        = settings.getString("human_api_user_id",      "");
        this.humanAPIAcessToken    = settings.getString("human_api_access_token", "");
        this.humanAPIUserToken     = settings.getString("human_api_user_token",   "");
        if (this.humanAPIUserID.equals("")) {
            this.humanAPIUserID = Long.toString(System.currentTimeMillis());
            settings.edit().putString("human_api_user_id", this.humanAPIUserID).commit();
        }

        addEventButton = (FloatingActionButton)findViewById(R.id.add_event_button);
        addGoalButton  = (FloatingActionButton)findViewById(R.id.add_goal_button);
        timeToEat      = (FloatingActionButton)findViewById(R.id.time_to_eat);
        addFoodButton  = (FloatingActionButton)findViewById(R.id.add_food_button);
        recordMeal     = (FloatingActionButton)findViewById(R.id.add_meal_button);

        recordMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, RecordMeal.class);
            startActivity(intent);
            }
        });

        addGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, AddNewGoal.class);
            startActivity(intent);
            }
        });

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, AddEvent.class);
            startActivity(intent);
            }
        });

        timeToEat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, TimeToEatActivity.class);
            startActivity(intent);
            }
        });

        addFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, AddFood.class);
            startActivity(intent);
            }
        });

        eventManager  = new EventManager(this);
        goalManager   = new GoalManager(this);
        mealManager   = new MealManager(this);
        weightManager = new WeightManager(this);
        goalManager.addListener(this);
        eventManager.addListener(goalManager);

        // Grab the pager
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        // Create the adapter
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Set the adapter
        mViewPager.setAdapter(adapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
            // When swiping between pages, select the corresponding tab.
            getSupportActionBar().setSelectedNavigationItem(position);
            }
        });

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event
            }
        };

        // Create the sticky fragments
        overviewFragment                  = new OverviewFragment();
        goalsFragment                     = new GoalFragment();
        eventsFragment                    = new EventFragment();
        foodsFragment                     = new FoodFragment();

        tabFragments.add(new FragmentItem("Overview",      overviewFragment));
        tabFragments.add(new FragmentItem("Goals",         goalsFragment));
        tabFragments.add(new FragmentItem("Events",        eventsFragment));
        tabFragments.add(new FragmentItem("Foods",         foodsFragment));

        // Add 3 tabs, specifying the tab's text and TabListener
        for (int i = 0; i < tabFragments.size(); i++) {
            adapter.addFragment(tabFragments.get(i).getFragment(), tabFragments.get(i).getFragmentName());
            adapter.notifyDataSetChanged();
            getSupportActionBar().addTab(
                    getSupportActionBar().newTab()
                            .setText(tabFragments.get(i).getFragmentName())
                            .setTabListener(tabListener));
        }

        if (!this.humanAPIAcessToken.equals("")) {
            humanIntegration = new HumanIntegration(humanAPIAcessToken, this);
            humanIntegration.getActivities();
        }

        if (this.stravaUserToken != null && !this.stravaUserToken.equals("")) {
            this.syncStravaActivities();
        }
    }

    @Override
    public void goalsUpdated(int numUpdated, int numDeleted, int numAdded) {

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList      = new ArrayList<>();
        private final List<String>   mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public static int HUMANAPI_AUTH = 123;
    public void onConnect() {
        Intent intent = new Intent(this, co.humanapi.connectsdk.ConnectActivity.class);
        Bundle b      = new Bundle();

        b.putString("client_user_id", humanAPIUserID);
        b.putString("client_id",      "932c49512edec3e6d2de64d8b96196cadf3427fb");
        b.putString("auth_url",       "http://localhost");

        if (!humanAPIUserToken.equals("")) {
            b.putString("public_token", humanAPIUserToken);
        }

        intent.putExtras(b);
        startActivityForResult(intent, HUMANAPI_AUTH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != HUMANAPI_AUTH) {
            return; // incorrect code
        }

        if (resultCode == RESULT_OK) {
            Log.d("hapi-home", "Authorization workflow completed");
            Bundle b = data.getExtras();
            Log.d("hapi-home", ".. public_token=" + b.getString("public_token"));
            SharedPreferences settings = getSharedPreferences("LifeGoals", 0);

            if (humanAPIAcessToken.equals("")) {
                humanAPIAcessToken = b.getString("access_token");
                settings.edit().putString("human_api_access_token", humanAPIAcessToken).commit();

                humanIntegration = new HumanIntegration(humanAPIAcessToken, MainActivity.this);

                humanIntegration.getActivities();
            }

            if (humanAPIUserToken.equals("")) {
                humanAPIUserToken = b.getString("public_token");
                settings.edit().putString("human_api_user_token", humanAPIUserToken).commit();
            }
        } else if (resultCode == RESULT_CANCELED) {
            Log.d("hapi-home", "Authorization workflow cancelled");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_integrations) {
            onConnect();
            return true;
        }

        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_meal_creator) {
            Intent intent = new Intent(MainActivity.this, MealCreator.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_ingredient_manager) {
            Intent intent = new Intent(MainActivity.this, IngredientActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_weight_loss) {
            Intent intent = new Intent(MainActivity.this, WeightLossActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void syncStravaActivities() {
        Snackbar.make(this.findViewById(android.R.id.content), "Loading data from Strava...", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        this.stravaIntegration.getStravaAcitiviesSinceLastUpdate();
    }

    public int getBMR () {
        SharedPreferences settings = getSharedPreferences("LifeGoals", 0);
        String sex = settings.getString("human_sex", "");
        boolean isMale    = false;
        boolean isFemale  = false;
        if (sex.equals("Male")) {
            isMale    = true;
        }
        if (sex.equals("Female")) {
            isFemale  = true;
        }
        if (!isMale && !isFemale) {return -1;}

        int weight = settings.getInt("human_weight", -1);
        if (weight == -1) {
            return -1;
        }

        int feet = settings.getInt("human_height_feet", -1);
        if (feet == -1) {
            return -1;
        }

        int inches = settings.getInt("human_height_inches", -1);
        if (inches == -1) {
            return -1;
        }

        String date = settings.getString("human_birthday", "");
        if (date.equals("")) {
            return -1;
        }

        DateFormat df     = new SimpleDateFormat("MM/dd/yy", Locale.US);
        int age           = 0;
        try {
            Date bDate    = df.parse(date);
            long diff     = new Date().getTime() - bDate.getTime();

            long days     = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            age           = (int)days/365;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String af = settings.getString("human_activity_factor", "");
        if (af.equals("")) {
            return -1;
        }

        float bmr    = 0;
        float height = (feet*12) + inches;
        if (isMale) {
            bmr = (float)(88.3 + (13.4 * (weight/2.2)) + (4.8 * (height/0.39)) - (5.7 * age));
        }
        else {
            bmr = (float)(447.6 + (9.2 * (weight/2.2)) + (3.1 * (height/0.39)) - (4.3 * age));
        }

        if (af.equals("Sedentary")) {
            bmr *= 1.2;
        }
        else if (af.equals("Lightly Active")) {
            bmr *= 1.375;
        }
        else if (af.equals("Moderately Active")) {
            bmr *= 1.55;
        }
        else if (af.equals("Very Active")) {
            bmr *= 1.725;
        }
        else {
            bmr *= 1.9;
        }

        int roundBMR = Math.round(bmr);

        return roundBMR;
    }


    public String getFoodFromUPC(String upc) {
        String urlStr = "https://api.nutritionix.com/v1_1/item?upc="+upc+"&appId=906641bd&appKey=016ae10e5d02ed2f1e59c67a049592e4";

        URL url;
        HttpURLConnection urlConnection = null;
        String response = "";
        try {
            url = new URL(urlStr);

            urlConnection = (HttpURLConnection) url
                    .openConnection();

            InputStream in = urlConnection.getInputStream();

            InputStreamReader isw = new InputStreamReader(in);

            BufferedReader buffer = new BufferedReader(
                    new InputStreamReader(in));
            String s = "";
            while ((s = buffer.readLine()) != null) {
                response += s;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();

                return response;
            }
        }

        return response;
    }
}
