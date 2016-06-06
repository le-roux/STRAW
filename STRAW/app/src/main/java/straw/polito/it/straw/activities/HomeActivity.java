package straw.polito.it.straw.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

    private static final String TAG = "HomeActivity";
    private SharedPreferences mShared;
    private EditText user_name_editText;
    private EditText pwd_editText;
    private Button log_in_button;
    private Button create_man_button;
    private Button create_user_button;
    private TextView forgot;
    private CheckBox remember;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mShared= PreferenceManager.getDefaultSharedPreferences(this);
        initialize();
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        autoLogIn(mShared, remember, (StrawApplication)getApplication());
        setListeners();
    }

    private void setListeners() {
        log_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAddress = user_name_editText.getText().toString();
                String password = pwd_editText.getText().toString();
                DatabaseUtils databaseUtils = ((StrawApplication)getApplication()).getDatabaseUtils();
                ProgressDialog dialog = new ProgressDialog(HomeActivity.this, ProgressDialog.STYLE_SPINNER);
                dialog.setIndeterminate(true);
                dialog.setMessage(getResources().getString(R.string.log_in));
                dialog.setCancelable(false);
                dialog.show();
                databaseUtils.logIn(emailAddress, password, true, dialog);
            }
        });

        create_man_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(getBaseContext(),CreateManagerAccountActivity.class);
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
        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    JSONObject jo = new JSONObject();
                    try {
                        jo.put("user",user_name_editText.getText().toString());
                        jo.put("pwd",pwd_editText.getText().toString());
                        mShared.edit().putString("remember",jo.toString()).apply();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    mShared.edit().remove("remember").apply();
                }
            }
        });
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=user_name_editText.getText().toString();
                if(email!= null || !email.equals("")) {
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

        user_name_editText=(EditText)findViewById(R.id.user_name_editText);
        pwd_editText=(EditText)findViewById(R.id.pwd_editText);
        log_in_button=(Button)findViewById(R.id.log_in_button);
        create_man_button=(Button)findViewById(R.id.c_man_button);
        create_user_button=(Button)findViewById(R.id.c_user_button);
        remember = (CheckBox) findViewById(R.id.remember_checkBox);
        forgot =(TextView)findViewById(R.id.forgot);
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

    public static boolean autoLogIn(SharedPreferences mShared, CheckBox remember, StrawApplication application) {
        if(mShared.contains("remember")){
            try {
                if (remember != null)
                    remember.setChecked(true);
                JSONObject jo = new JSONObject(mShared.getString("remember","Error"));
                String emailAddress = jo.getString("user");
                String password =jo.getString("pwd");
                DatabaseUtils databaseUtils = (application.getDatabaseUtils());
                ProgressDialog dialog = new ProgressDialog(application.getBaseContext(), ProgressDialog.STYLE_SPINNER);
                dialog.setIndeterminate(true);
                dialog.setMessage(application.getResources().getString(R.string.log_in));
                dialog.setCancelable(false);
                dialog.show();
                databaseUtils.logIn(emailAddress, password, true, dialog);
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }





}
