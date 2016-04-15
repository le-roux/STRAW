package straw.polito.it.straw.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import straw.polito.it.straw.R;
import straw.polito.it.straw.utils.Logger;

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
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            setContentView(R.layout.activity_home);
        }else{
            setContentView(R.layout.activity_home_landscape);
        }
        mShared= PreferenceManager.getDefaultSharedPreferences(this);
        initialize();

        log_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String u = user_name_editText.getText().toString();
                String p = pwd_editText.getText().toString();

                if (log_in(u, p)) {
                    Log.v(TAG, "User log in successfull");
                    Intent i=new Intent(getBaseContext(),ProfileActivity.class);
                    startActivity(i);
                }else{
                    Log.v(TAG, "User log in ERROR");
                }
            }
        });

        create_man_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(getBaseContext(),CreateManagerAccountActivity.class);
                startActivity(i);
            }
        });

        this.create_user_button = (Button)findViewById(R.id.c_user_button);
        this.create_user_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.d("Launch menu");
                Intent intent = new Intent(getApplicationContext(), DisplayReservationsActivity.class);
                Logger.d("Intent created");
                startActivity(intent);
            }
        });

    }

    private boolean log_in(String u, String p) {
        String ss=mShared.getString("Manager","Error");
        if(!ss.equals("Error")){
            try {
                JSONObject jo=new JSONObject(ss);
                if(u.equals(jo.get("email")) && p.equals(jo.get("pwd"))){
                    return true;
                }
            } catch (JSONException e) {
                Log.v(TAG,"Error retreiving manager");
            }

        }

        return false;
    }

    private void initialize() {

        user_name_editText=(EditText)findViewById(R.id.user_name_editText);
        pwd_editText=(EditText)findViewById(R.id.pwd_editText);
        log_in_button=(Button)findViewById(R.id.log_in_button);
        create_man_button=(Button)findViewById(R.id.c_man_button);
    }





}
