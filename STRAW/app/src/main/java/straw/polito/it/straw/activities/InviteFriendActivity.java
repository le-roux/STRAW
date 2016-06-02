package straw.polito.it.straw.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import straw.polito.it.straw.MessageSender;
import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.UserContainer;
import straw.polito.it.straw.adapter.FriendAdapter;
import straw.polito.it.straw.data.Friend;
import straw.polito.it.straw.data.Reservation;
import straw.polito.it.straw.data.User;
import straw.polito.it.straw.fragments.FriendCreationFragment;
import straw.polito.it.straw.utils.DateDisplay;
import straw.polito.it.straw.fragments.InvitationSenderFragment;
import straw.polito.it.straw.utils.SharedPreferencesHandler;
import straw.polito.it.straw.utils.TimerDisplay;

public class InviteFriendActivity extends AppCompatActivity implements MessageSender, UserContainer{

    private TextView restaurantName;
    private DateDisplay calendar;
    private TimerDisplay time;
    private ListView friendsList;
    private Button sendInvitationsButton;
    private Reservation reservation;
    private User user;
    private FriendAdapter friendAdapter;
    private Button addButton;
    private ArrayList<String> addresses;
    private int singleAddress;
    private static final int INVALID = -1;

    public static final String INVITATION = "Invitation";
    public static final String RESTAURANT = "Restaurant";
    private static final String ADDRESSES = "addresses";
    private static final String USER = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);

        Intent intent = getIntent();
        this.reservation = Reservation.create(intent.getStringExtra(Reservation.RESERVATION));

        StrawApplication application = (StrawApplication)getApplication();
        SharedPreferencesHandler sharedPreferencesHandler = application.getSharedPreferencesHandler();
        if (savedInstanceState == null)
            this.user = sharedPreferencesHandler.getCurrentUser();
        else
            this.user = new User(savedInstanceState.getString(USER));
        this.singleAddress = INVALID;

        this.restaurantName = (TextView)findViewById(R.id.RestaurantName);
        this.calendar = (DateDisplay)findViewById(R.id.Date);
        this.time = (TimerDisplay)findViewById(R.id.Time);
        this.friendsList = (ListView)findViewById(R.id.list_friends);
        this.sendInvitationsButton = (Button)findViewById(R.id.SendInvitationButton);
        this.addButton = (Button)findViewById(R.id.add_button);

        this.restaurantName.setText(this.reservation.getRestaurant());
        this.calendar.setDate(this.reservation.getYear(), this.reservation.getMonth(), this.reservation.getDay());
        this.time.setTime(this.reservation.getHourOfDay(), this.reservation.getMinutes());

        if (savedInstanceState == null)
            this.addresses = new ArrayList<>();
        else
            this.addresses = savedInstanceState.getStringArrayList(ADDRESSES);

        this.friendAdapter = new FriendAdapter(this.getApplicationContext(), this.user.getFriends());
        this.friendsList.setAdapter(this.friendAdapter);
        this.friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                singleAddress = position;
                DialogFragment fragment = new InvitationSenderFragment();
                fragment.show(getFragmentManager(), "messagePicker");
            }
        });
        this.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = new FriendCreationFragment();
                dialog.show(getFragmentManager(), "FriendCreation");
            }
        });

        this.sendInvitationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment fragment = new InvitationSenderFragment();
                fragment.show(getFragmentManager(), "messagePicker");
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
                .append(reservation.getRestaurant())
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

    @Override
    public String[] getAddresses(boolean email) {
        if (this.singleAddress == INVALID) {
            ArrayList<String> addresses = new ArrayList<>();
            for (int i = 0; i < friendAdapter.getCount(); i++) {
                if (friendAdapter.getCheckboxes().get(i).isChecked()) {
                    if (email)
                        addresses.add(((Friend) friendAdapter.getItem(i)).getEmailAddress());
                    else
                        addresses.add(((Friend) friendAdapter.getItem(i)).getPhoneNumber());
                }
            }
            String[] result = new String[addresses.size()];
            return addresses.toArray(result);
        } else {
            String[] result = new String[1];
            if (email)
                result[0] = ((Friend) this.friendAdapter.getItem(this.singleAddress)).getEmailAddress();
            else
                result[0] = ((Friend) this.friendAdapter.getItem(this.singleAddress)).getPhoneNumber();
            singleAddress = INVALID;
            return result;
        }
    }

    @Override
    public String getMessage() {
        return getInvitationMessage();
    }

    @Override
    public String getSubject() {
        return reservation.getRestaurant();
    }

    @Override
    public void displayConfirmationToast(int count) {
        StringBuilder builder = new StringBuilder();
        builder.append(count)
                .append(' ')
                .append(getResources().getString(R.string.InvitationsSent));
        Toast toast = Toast.makeText(getApplicationContext(), builder.toString(), Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(USER, this.user.toString());
        outState.putStringArrayList(ADDRESSES, addresses);
    }

    @Override
    public User getUser() {
        return this.user;
    }
}
