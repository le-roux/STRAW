package straw.polito.it.straw.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.UserContainer;
import straw.polito.it.straw.data.User;
import straw.polito.it.straw.fragments.AddFriendsFragment;
import straw.polito.it.straw.fragments.CustomerReservationsFragment;
import straw.polito.it.straw.fragments.CustomerReviewFragment;
import straw.polito.it.straw.utils.ImageManager;
import straw.polito.it.straw.utils.SharedPreferencesHandler;
import straw.polito.it.straw.utils.TimerDisplay;

public class ProfileUserActivity extends AppCompatActivity implements UserContainer{

    private ImageView photo;
    private TextView email;
    private TextView user_info;
    private TextView diet;
    private TextView pref_t;
    private TextView log_out;

    private TextView res_h;
    private TextView rev_h;
    private TextView friends;

    private Button edit_profile;
    private SharedPreferencesHandler sharedPreferencesHandler;
    private FragmentManager fragmentManager;
    private User user;

    public static final String USER = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("PROFILE  USER");
        setSupportActionBar(toolbar);
        sharedPreferencesHandler = ((StrawApplication)getApplication()).getSharedPreferencesHandler();
        this.fragmentManager = getFragmentManager();

        initialize();

        this.user = sharedPreferencesHandler.getCurrentUser();
        loadPrevInfo(user);

        setListeners();
    }

    private void setListeners() {
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), CreateUserAccountActivity.class);
                i.putExtra(USER, user.toString());
                startActivity(i);
            }
        });

        res_h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.mainLayout, new CustomerReservationsFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        rev_h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.mainLayout, new CustomerReviewFragment());
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Display the fragment that allows to manage the list of friends
                 */
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.mainLayout, new AddFriendsFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferencesHandler.removeMemory();
                Intent intent  = new Intent(getBaseContext(),HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initialize() {
        photo = (ImageView)findViewById(R.id.photo_imageView);
        email = (TextView)findViewById(R.id.email_textView);
        user_info = (TextView)findViewById(R.id.user_textView);
        diet = (TextView)findViewById(R.id.diet_textView);
        pref_t = (TextView)findViewById(R.id.pref_t_textView);
        edit_profile = (Button)findViewById(R.id.edit_button);
        res_h = (TextView)findViewById(R.id.reservation_history);
        rev_h = (TextView)findViewById(R.id.review_history);
        friends = (TextView)findViewById(R.id.list_friends);
        log_out = (TextView)findViewById(R.id.log_out);
    }
    private void loadPrevInfo(User user) {
        ImageManager.setImage(this, photo, user.getImage());

        String mail = getString(R.string.email) + " : " + user.getEmail();
        email.setText(mail);

        String info = getString(R.string.u_t)
                + " : "
                + user.getType()
                + " , "
                + user.getUniversity();
        user_info.setText(info);

        String dietText = getString(R.string.u_d)
                + " : "
                + user.getDiet();
        diet.setText(dietText);

        String pref =  getString(R.string.p_t)
                + " : "
                + TimerDisplay.getTime(this.user.getPrefTimeHour(), this.user.getPrefTimeMinutes(), DateFormat.is24HourFormat(this));
        pref_t.setText(pref);
    }

    @Override
    public User getUser() {
        return this.user;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        this.sharedPreferencesHandler.storeCurrentUser(this.user.toString());
    }
}
