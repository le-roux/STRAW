package straw.polito.it.straw.activities;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.fragments.LogInFragment;
import straw.polito.it.straw.utils.Logger;

public class DisplayInvitationActivity extends AppCompatActivity {

    private String restaurantName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_invitation);

        String message = getIntent().getStringExtra(InviteFriendActivity.INVITATION);
        this.restaurantName = getIntent().getStringExtra(InviteFriendActivity.RESTAURANT);
        if (message == null)
            message = getString(R.string.ErrorNetwork);

        TextView text = (TextView)findViewById(R.id.invitation);
        Button logIn = (Button)findViewById(R.id.logInAndReserveButton);

        if (text != null)
            text.setText(message);
       if (logIn != null) {
            logIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean autoLogged;
                    autoLogged = HomeActivity.autoLogIn(PreferenceManager.getDefaultSharedPreferences(DisplayInvitationActivity.this),
                            null, (StrawApplication)getApplication());
                    if (!autoLogged) {
                        LogInFragment fragment = new LogInFragment();
                        fragment.show(getSupportFragmentManager(), "LogIn");
                    }
                }
            });
        }
    }

    public String getRestaurantName() {
        return this.restaurantName;
    }
}
