package straw.polito.it.straw.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.utils.ImageManager;
import straw.polito.it.straw.utils.SharedPreferencesHandler;

public class ProfileManagerActivity extends AppCompatActivity {

    private ImageView photo;
    private TextView user_n;
    private TextView tel;
    private TextView r_n;
    private TextView addr;
    private TextView log_out;
    private TextView seats;
    private TextView menu_link;
    private TextView reservations_link;
    private TextView offerts_link;
    private Button edit_button;
    private SharedPreferencesHandler sharedPreferencesHandler;
    public static final String MANAGER = "manager";

    private String TAG = "ProfileManagerActivity";
    private SharedPreferences mShared;
    private Manager man;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_manager);
        sharedPreferencesHandler = ((StrawApplication)getApplication()).getSharedPreferencesHandler();
        man=sharedPreferencesHandler.getCurrentManager();
        initialize();
        loadPrevInfo(man);
        setListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();

        man=sharedPreferencesHandler.getCurrentManager();
        loadPrevInfo(man);
    }

    private void setListeners() {
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), CreateManagerAccountActivity.class);
                Log.v(TAG,man.toJSONObject());
                i.putExtra(MANAGER, man.toJSONObject());
                startActivity(i);
            }
        });

        this.menu_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateMenuActivity.class);
                startActivity(intent);
            }
        });

        this.reservations_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DisplayReservationsActivity.class);
                startActivity(intent);
            }
        });
        offerts_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getBaseContext(),OfferActivity.class);
                startActivity(i);
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
        photo=(ImageView)findViewById(R.id.photo_imageView);
        user_n=(TextView)findViewById(R.id.email_textView);
        tel=(TextView)findViewById(R.id.tel_editText);
        r_n=(TextView)findViewById(R.id.diet_editText);
        addr=(TextView)findViewById(R.id.pref_t_textView);
        seats=(TextView)findViewById(R.id.seats_editText);
        edit_button=(Button)findViewById(R.id.edit_button);
        this.menu_link = (TextView)findViewById(R.id.reservation_history);
        this.reservations_link = (TextView)findViewById(R.id.review_history);
        offerts_link=(TextView)findViewById(R.id.offers_link_textView);
        log_out=(TextView)findViewById(R.id.log_out);
    }
    private void loadPrevInfo(Manager man) {
        ImageManager.setImage(this, photo, man.getImage());

        user_n.setText(getString(R.string.email) + ": " + man.getEmail());
        tel.setText(getString(R.string.tel) + ": " + man.getTelephone());
        r_n.setText(getString(R.string.r_name) + ": " + man.getRes_name());
        addr.setText(getString(R.string.addr) + ": " + man.getAddress());
        seats.setText(getString(R.string.seats) + ": " + man.getSeats());
    }

}
