package straw.polito.it.straw.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.utils.DatabaseUtils;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    SharedPreferences mShared;
    EditText user_name_editText;
    EditText pwd_editText;
    Button log_in_button;
    Button create_man_button;
    Button create_user_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mShared= PreferenceManager.getDefaultSharedPreferences(this);
        initialize();
        setListeners();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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

                //Intent intent = new Intent(getApplicationContext(), CreateUserAccountActivity.class);
                //Intent intent = new Intent(getApplicationContext(), AdvancedSearchActivity.class);
                Intent intent = new Intent(getApplicationContext(),SearchActivity.class);
                //Intent intent = new Intent(getApplicationContext(), CreateUserAccountActivity.class);
                //Intent intent = new Intent(getApplicationContext(), AdvancedSearchActivity.class);

                startActivity(intent);
            }
        });

    }

    private int log_in(String u, String p) {
        if(mShared.contains("Manager")){
            try {
                JSONObject jo=new JSONObject(mShared.getString("Manager","Error"));
                if(u.equals(jo.get("email")) && p.equals(jo.get("pwd"))){
                    return 1;
                }
            } catch (JSONException e) {
                Log.v(TAG,"Error retreiving manager");
            }

        }
        if(mShared.contains("User")){
            try {
                JSONObject jo=new JSONObject(mShared.getString("User","Error"));
                if(u.equals(jo.get("email")) && p.equals(jo.get("pwd"))){
                    return 2;
                }
            } catch (JSONException e) {
                Log.v(TAG,"Error retreiving user");
            }

        }

        return 0;
    }

    private void initialize() {

        user_name_editText=(EditText)findViewById(R.id.user_name_editText);
        pwd_editText=(EditText)findViewById(R.id.pwd_editText);
        log_in_button=(Button)findViewById(R.id.log_in_button);
        create_man_button=(Button)findViewById(R.id.c_man_button);
        create_user_button=(Button)findViewById(R.id.c_user_button);
    }





}
