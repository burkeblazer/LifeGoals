package bblazer.com.lifegoals.Integrations;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jstrava.authenticator.AuthResponse;
import org.jstrava.authenticator.StravaAuthenticator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import bblazer.com.lifegoals.LifeGoalsApplication;
import bblazer.com.lifegoals.R;

public class ModifyIntegrationsActivity extends AppCompatActivity {
    private RelativeLayout stravaConnectedContainer;
    private LinearLayout   connectStravaContainer;
    private TextView       stravaConnectedText;
    private ImageButton    stravaRefreshButton;
    private ImageButton    stravaConnectButton;
    private ImageButton    stravaDeleteIntegration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_integrations);

        getSupportActionBar().setTitle("Manage Integrations");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        stravaConnectedContainer = (RelativeLayout)findViewById(R.id.strava_connected_container);
        connectStravaContainer   = (LinearLayout)findViewById(R.id.connect_strava_container);
        stravaConnectedText      = (TextView)findViewById(R.id.strava_connected_text);
        stravaRefreshButton      = (ImageButton)findViewById(R.id.strava_refresh_button);
        stravaConnectButton      = (ImageButton)findViewById(R.id.connect_with_strava);
        stravaDeleteIntegration  = (ImageButton)findViewById(R.id.strava_delete_integration);

        // See if we have a user_token or not
        if (((LifeGoalsApplication)this.getApplication()).mainActivity.stravaUserToken.equals("")) {
            stravaConnectedContainer.setVisibility(View.GONE);
            connectStravaContainer.setVisibility(View.VISIBLE);
        }
        else {
            stravaConnectedContainer.setVisibility(View.VISIBLE);
            connectStravaContainer.setVisibility(View.GONE);

            SharedPreferences settings = getSharedPreferences("LifeGoals",          0);
            String stravaConnectedDate = settings.getString("strava_connected_date", "");
            stravaConnectedText.setText("Connected on "+stravaConnectedDate);
        }

        stravaDeleteIntegration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences("LifeGoals", 0);
                settings.edit().putString("strava_user_token", "").apply();
                settings.edit().putString("strava_connected_date", "").apply();
                settings.edit().commit();

                stravaConnectedContainer.setVisibility(View.GONE);
                connectStravaContainer.setVisibility(View.VISIBLE);
            }
        });

        stravaConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ModifyIntegrationsActivity.this);

                final WebView wv = new WebView(ModifyIntegrationsActivity.this);

                LinearLayout wrapper = new LinearLayout(ModifyIntegrationsActivity.this);
                EditText keyboardHack = new EditText(ModifyIntegrationsActivity.this);

                keyboardHack.setVisibility(View.GONE);
                wv.loadUrl("https://www.strava.com/oauth/authorize?client_id=10939&response_type=code&redirect_uri=http://localhost&approval_prompt=force");
                wv.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(final WebView view, String url) {
                        if (url.contains("code=")) {
                            final String authenticationToken = url.replace("http://localhost/?state=&code=", "");
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    // Got the code now get a token from it
                                    StravaAuthenticator auth = new StravaAuthenticator(10939, "localhost", "6266fa80a8a744a157990b420ec4dff94549cae9");
                                    AuthResponse response = auth.getToken(authenticationToken);
                                    String userToken = response.access_token;

                                    // Store it in our settings
                                    SharedPreferences settings = getSharedPreferences("LifeGoals", 0);
                                    settings.edit().putString("strava_user_token", userToken).apply();
                                    DateFormat df = new SimpleDateFormat("MM/dd/yy", Locale.US);
                                    settings.edit().putString("strava_connected_date", df.format(new Date())).apply();
                                    settings.edit().commit();
                                    ((LifeGoalsApplication) getApplication()).mainActivity.stravaUserToken = userToken;

                                    // Simulate a button click on the close to close the window
                                    ModifyIntegrationsActivity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            LinearLayout container = (LinearLayout) view.getParent().getParent().getParent().getParent();
                                            ButtonBarLayout barLayout = (ButtonBarLayout) container.getChildAt(3);
                                            barLayout.getChildAt(2).performClick();
                                        }
                                    });
                                }
                            });

                            thread.start();
                            wv.stopLoading();
                            return true;
                        }

                        view.loadUrl(url);

                        return true;
                    }
                });

                wrapper.setOrientation(LinearLayout.VERTICAL);
                wrapper.addView(wv, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                wrapper.addView(keyboardHack, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                alert.setView(wrapper);
                alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        successfulStravaIntegration();
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = alert.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
                wv.requestFocus(View.FOCUS_DOWN);
            }
        });

        stravaRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(ModifyIntegrationsActivity.this.findViewById(android.R.id.content), "Loading data from Strava...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                SharedPreferences settings = getSharedPreferences("LifeGoals", 0);
                settings.edit().putString("strava_last_updated", "").apply();
                ((LifeGoalsApplication)getApplication()).mainActivity.syncStravaActivities();
            }
        });
    }

    public void successfulStravaIntegration() {
        if (((LifeGoalsApplication)this.getApplication()).mainActivity.stravaUserToken.equals("")) {
            stravaConnectedContainer.setVisibility(View.GONE);
            connectStravaContainer.setVisibility(View.VISIBLE);
        }
        else {
            stravaConnectedContainer.setVisibility(View.VISIBLE);
            connectStravaContainer.setVisibility(View.GONE);

            SharedPreferences settings = getSharedPreferences("LifeGoals",          0);
            String stravaConnectedDate = settings.getString("strava_connected_date", "");
            stravaConnectedText.setText("Connected on "+stravaConnectedDate);
        }

        Toast.makeText(ModifyIntegrationsActivity.this, "Successfully integrated Strava.", Toast.LENGTH_SHORT).show();
        Snackbar.make(ModifyIntegrationsActivity.this.findViewById(android.R.id.content), "Loading data from Strava...", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        ((LifeGoalsApplication)this.getApplication()).mainActivity.syncStravaActivities();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
