package bblazer.com.lifegoals;

import android.app.Application;

import bblazer.com.lifegoals.Activity.MainActivity;

/**
 * Created by bblazer on 4/8/16.
 */
public class LifeGoalsApplication extends Application
{
    public MainActivity mainActivity;

    @Override
    public void onCreate()
    {
        super.onCreate();
    }
}