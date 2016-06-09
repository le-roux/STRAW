package straw.polito.it.straw.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.services.RegistrationIntentService;
import straw.polito.it.straw.utils.DatabaseUtils;

public class HomeActivity extends AppCompatActivity {

    private SharedPreferences mShared;
    private EditText user_name_editText;
    private EditText pwd_editText;
    private Button log_in_button;
    private Button create_man_button;
    private Button create_user_button;
    private TextView forgot;
    private CheckBox remember;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    // Keys used for the auto-login part
    public static final String USER = "user";
    public static final String PASSWORD = "pwd";
    public static final String REMEMBER = "remember";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("HOME");
        setSupportActionBar(toolbar);
        mShared= PreferenceManager.getDefaultSharedPreferences(this);

        initialize();
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (this.mShared.contains(REMEMBER)) {
            this.user_name_editText.setText(this.mShared.getString(USER, ""));
            this.pwd_editText.setText(this.mShared.getString(PASSWORD, ""));
        }
        autoLogIn(this.mShared, this.remember, this, null);
        setListeners();
    }

    private void setListeners() {
        log_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the entered values
                String emailAddress = user_name_editText.getText().toString();
                String password = pwd_editText.getText().toString();
                DatabaseUtils databaseUtils = ((StrawApplication)getApplication()).getDatabaseUtils();

                // Prepare a progress dialog to show that something is happening
                ProgressDialog dialog = new ProgressDialog(HomeActivity.this, ProgressDialog.STYLE_SPINNER);
                dialog.setIndeterminate(true);
                dialog.setMessage(getResources().getString(R.string.log_in));
                dialog.setCancelable(false);
                dialog.show();
                // If the remember checkbox is ticked, save the data in the internal memory
                if(remember.isChecked()){
                    JSONObject jo = new JSONObject();
                    try {
                        jo.put(USER, user_name_editText.getText().toString());
                        jo.put(PASSWORD, pwd_editText.getText().toString());
                        mShared.edit().putString(REMEMBER, jo.toString()).apply();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    mShared.edit().remove(REMEMBER).apply();
                }

                // Perform the login and change of activity
                databaseUtils.logIn(emailAddress, password, true, dialog);
            }
        });

        create_man_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(),CreateManagerAccountActivity.class);
                startActivity(i);
            }
        });

        create_user_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateUserAccountActivity.class);
                startActivity(intent);
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = user_name_editText.getText().toString();
                if(!email.equals("")) {
                    DatabaseUtils databaseUtils = ((StrawApplication) getApplication()).getDatabaseUtils();
                    databaseUtils.sendResetRequest(user_name_editText.getText().toString());
                }else{
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.m_email), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


    private void initialize() {

        user_name_editText = (EditText)findViewById(R.id.user_name_editText);
        pwd_editText = (EditText)findViewById(R.id.pwd_editText);
        log_in_button = (Button)findViewById(R.id.log_in_button);
        create_man_button = (Button)findViewById(R.id.c_man_button);
        create_user_button = (Button)findViewById(R.id.c_user_button);
        remember = (CheckBox) findViewById(R.id.remember_checkBox);
        forgot = (TextView)findViewById(R.id.forgot);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences.getBoolean("tokenSW", false);
                if (sentToken) {
                    //user.setTokenGCM(sharedPreferences.getString("tokenGCM","Error"));
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,new IntentFilter("complete"));
    }

    public static boolean autoLogIn(SharedPreferences mShared, CheckBox remember, Activity activity, String restaurantName) {
        if(mShared.contains(REMEMBER)){
            try {
                if (remember != null)
                    remember.setChecked(true);

                // Retrieve the saved values
                JSONObject jo = new JSONObject(mShared.getString(REMEMBER,"Error"));
                String emailAddress = jo.getString(USER);
                String password =jo.getString(PASSWORD);
                StrawApplication application = (StrawApplication)activity.getApplication();
                DatabaseUtils databaseUtils = (application.getDatabaseUtils());

                // Prepare a progress dialog to show that the app is not frozen
                ProgressDialog dialog = new ProgressDialog(activity, ProgressDialog.STYLE_SPINNER);
                dialog.setIndeterminate(true);
                dialog.setMessage(application.getResources().getString(R.string.log_in));
                dialog.setCancelable(false);
                dialog.show();

                // Perform the login and change activity
                if (restaurantName != null) // Log in requested by an invitation
                    databaseUtils.logInAndReserve(emailAddress, password, restaurantName, dialog);
                else // Normal log in
                    databaseUtils.logIn(emailAddress, password, true, dialog);
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }





}
