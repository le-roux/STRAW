package straw.polito.it.straw;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    SharedPreferences mShared;
    EditText user_name_editText;
    EditText pwd_editText;
    Button log_in_button;
    Button create_man_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initialize();

        log_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String u = user_name_editText.getText().toString();
                String p = pwd_editText.getText().toString();

                if (log_in(u, p)) {
                    Log.v(TAG, "User log ing successfull");
                }
            }
        });

        create_man_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(getBaseContext(),CreateAccount.class);
                startActivity(i);
            }
        });

    }

    private boolean log_in(String u, String p) {
        return false;
    }

    private void initialize() {

        user_name_editText=(EditText)findViewById(R.id.user_name_editText);
        pwd_editText=(EditText)findViewById(R.id.pwd_editText);
        log_in_button=(Button)findViewById(R.id.log_in_button);
        create_man_button=(Button)findViewById(R.id.c_man_button);
    }





}
