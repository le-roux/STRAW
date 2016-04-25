package straw.polito.it.straw.activities;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import straw.polito.it.straw.data.Manager;
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
                StringBuilder builder = new StringBuilder();
                Resources resources = getResources();
                builder.append(user.getEmail())
                        .append(resources.getString(R.string.InvitationMessage))
                        .append(reservation.getRestaurant().getRes_name())
                        .append(' ')
                        .append(resources.getString(R.string.on))
                        .append(' ');
                if (reservation.getDay() < 10)
                    builder.append('0');
                builder.append(reservation.getDay())
                        .append('/');
                if(reservation.getMonth() < 10)
                    builder.append('0');
                builder.append(reservation.getMonth() + 1)
                        .append('/')
                        .append(reservation.getYear());
                builder.append(' ')
                        .append(resources.getString(R.string.at))
                        .append(' ');
                if(reservation.getHourOfDay() < 10)
                    builder.append('0');
                builder.append(reservation.getHourOfDay())
                        .append(":");
                if(reservation.getMinutes() < 10)
                    builder.append(('0'));
                builder.append(String.valueOf(reservation.getMinutes()));

                int count = 0;
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (checkBoxes.get(i).isChecked()) {
                        count++;
                        String phoneNumber = ((User)adapter.getItem(i)).getPhoneNumber();
                        smsManager.sendTextMessage(phoneNumber, null, builder.toString(), null, null);
                    }
                }
                Toast toast = Toast.makeText(getApplicationContext(), String.valueOf(count) + R.string.InvitationsSent, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}
