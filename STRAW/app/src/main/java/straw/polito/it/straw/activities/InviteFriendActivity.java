package straw.polito.it.straw.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.adapter.UserAdapter;
import straw.polito.it.straw.data.Reservation;
import straw.polito.it.straw.data.User;
import straw.polito.it.straw.utils.DateDisplay;
import straw.polito.it.straw.utils.TimerDisplay;

public class InviteFriendActivity extends AppCompatActivity {

    private TextView restaurantName;
    private DateDisplay calendar;
    private TimerDisplay time;
    private ListView friendsList;
    private Button sendInvitationsButton;
    private Reservation reservation;
    private User user;
    private SharedPreferences sharedPreferences;
    private SmsManager smsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);

        Intent intent = getIntent();
        this.reservation = Reservation.create(intent.getStringExtra(Reservation.RESERVATION));

        this.smsManager = SmsManager.getDefault();

        this.sharedPreferences  = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.user = new User(this.sharedPreferences.getString("User", "Error"));

        this.restaurantName = (TextView)findViewById(R.id.RestaurantName);
        this.calendar = (DateDisplay)findViewById(R.id.Date);
        this.time = (TimerDisplay)findViewById(R.id.Time);
        this.friendsList = (ListView)findViewById(R.id.list_friends);
        this.sendInvitationsButton = (Button)findViewById(R.id.SendInvitationButton);

        this.friendsList.setAdapter(new UserAdapter(this.getApplicationContext(), this.user.getFriends()));

        this.sendInvitationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserAdapter adapter = (UserAdapter)friendsList.getAdapter();
                ArrayList<CheckBox> checkBoxes = adapter.getCheckboxes();
                String message = getInvitationMessage();
                int count = 0;
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (checkBoxes.get(i).isChecked()) {
                        count++;
                        String phoneNumber = ((User)adapter.getItem(i)).getPhoneNumber();
                        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                    }
                }
                //For test purpose
                smsManager.sendTextMessage("+33683781744", null, message, null, null);

                StringBuilder builder = new StringBuilder();
                builder.append(count)
                        .append(' ')
                        .append(getResources().getString(R.string.InvitationsSent));
                Toast toast = Toast.makeText(getApplicationContext(), builder.toString(), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private String getInvitationMessage() {
        StringBuilder builder = new StringBuilder();
        Resources resources = getResources();
        builder.append(user.getEmail())
                .append(' ')
                .append(resources.getString(R.string.InvitationMessage))
                .append(' ')
                .append(reservation.getRestaurant().getRes_name())
                .append(' ')
                .append(resources.getString(R.string.on))
                .append(' ')
                .append(DateDisplay.getDate(reservation.getDay(), reservation.getMonth(), reservation.getYear()))
                .append(' ')
                .append(resources.getString(R.string.at))
                .append(' ')
                .append(TimerDisplay.getTime(reservation.getHourOfDay(), reservation.getMinutes(), false));
        return builder.toString();
    }
}
