package straw.polito.it.straw.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import straw.polito.it.straw.R;
import straw.polito.it.straw.data.User;
import straw.polito.it.straw.utils.ImageManager;

public class ProfileUserActivity extends AppCompatActivity {

    ImageView photo;
    TextView email;
    TextView user_info;
    TextView diet;
    TextView pref_t;

    TextView res_h;
    TextView rev_h;
    TextView friends;

    Button edit_profile;
    private String TAG = "ProfileUserActivity";
    private SharedPreferences mShared;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        mShared = PreferenceManager.getDefaultSharedPreferences(this);

        initialize();

        String ss = mShared.getString("User", "Error");
        user=new User(ss);
        loadPrevInfo(user);

        setListeners();
    }

    private void setListeners() {
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), CreateUserAccountActivity.class);
                i.putExtra("user", user.toString());
                startActivity(i);
            }
        });

        res_h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO RESERVATION HISTORY
            }
        });

        rev_h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO REVIEW HISTORY
            }
        });
        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //TODO FRIEND LIST
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        user=new User(mShared.getString("User","Error"));
        loadPrevInfo(user);
    }

    private void initialize() {
        photo=(ImageView)findViewById(R.id.photo_imageView);
        email=(TextView)findViewById(R.id.email_textView);
        user_info=(TextView)findViewById(R.id.user_textView);
        diet=(TextView)findViewById(R.id.diet_textView);
        pref_t=(TextView)findViewById(R.id.pref_t_textView);
        edit_profile=(Button)findViewById(R.id.edit_button);
        res_h=(TextView)findViewById(R.id.reservation_history);
        rev_h=(TextView)findViewById(R.id.review_history);
        friends=(TextView)findViewById(R.id.list_friends);
    }
    private void loadPrevInfo(User user) {
        ImageManager.setImage(this, photo, user.getImage());

        email.setText(getString(R.string.email) + ": " + user.getEmail());
        user_info.setText(getString(R.string.u_t) + ": " + user.getType()+" , "+user.getUniversity());
        diet.setText(getString(R.string.u_d) + ": " + user.getDiet());
        pref_t.setText(getString(R.string.p_t) + ": " + user.getPref_time());

    }
}
