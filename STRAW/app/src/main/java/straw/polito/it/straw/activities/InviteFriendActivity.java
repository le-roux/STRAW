package straw.polito.it.straw.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import straw.polito.it.straw.MessageSender;
import straw.polito.it.straw.R;
import straw.polito.it.straw.adapter.UserAdapter;
import straw.polito.it.straw.data.Reservation;
import straw.polito.it.straw.data.User;
import straw.polito.it.straw.utils.DateDisplay;
import straw.polito.it.straw.utils.InvitationSenderFragment;
import straw.polito.it.straw.utils.TimerDisplay;

public class InviteFriendActivity extends AppCompatActivity implements MessageSender{

    private TextView restaurantName;
    private DateDisplay calendar;
    private TimerDisplay time;
    private ListView friendsList;
    private Button sendInvitationsButton;
    private Reservation reservation;
    private User user;
    private SharedPreferences sharedPreferences;
    private UserAdapter userAdapter;
    private Button addButton;
    private EditText addressInput;
    private ArrayList<String> addresses;

    private static final String ADDRESSES = "addresses";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);

        Intent intent = getIntent();
        this.reservation = Reservation.create(intent.getStringExtra(Reservation.RESERVATION));

        this.sharedPreferences  = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.user = new User(this.sharedPreferences.getString("User", "Error"));

        this.restaurantName = (TextView)findViewById(R.id.RestaurantName);
        this.calendar = (DateDisplay)findViewById(R.id.Date);
        this.time = (TimerDisplay)findViewById(R.id.Time);
        this.friendsList = (ListView)findViewById(R.id.list_friends);
        this.sendInvitationsButton = (Button)findViewById(R.id.SendInvitationButton);
        this.addButton = (Button)findViewById(R.id.add_button);
        this.addressInput = (EditText)findViewById(R.id.new_friend);
        this.addressInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        this.restaurantName.setText(this.reservation.getRestaurant());
        this.calendar.setDate(this.reservation.getYear(), this.reservation.getMonth(), this.reservation.getDay());
        this.time.setTime(this.reservation.getHourOfDay(), this.reservation.getMinutes());

        if (savedInstanceState == null)
            this.addresses = new ArrayList<>();
        else
            this.addresses = savedInstanceState.getStringArrayList(ADDRESSES);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.addresses);
        this.friendsList.setAdapter(adapter);
        this.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = addressInput.getText().toString();
                if (!address.equals("")) {
                    addresses.add(address);
                    addressInput.setText("");
                    adapter.notifyDataSetChanged();
                }
            }
        });

        //this.userAdapter = new UserAdapter(this.getApplicationContext(), this.user.getFriends());
        //this.friendsList.setAdapter(this.userAdapter);

        this.sendInvitationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DialogFragment fragment = new InvitationSenderFragment();
                //fragment.show(getFragmentManager(), "messagePicker");
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse("mailto:"));
                intent.setType("text/html");
                String[] dest = new String[addresses.size()];
                intent.putExtra(Intent.EXTRA_EMAIL, addresses.toArray(dest));
                intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.STRAWInvitation));
                intent.putExtra(Intent.EXTRA_TEXT, getMessage());
                startActivity(intent);
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
    public String[] getAddresses() {
        ArrayList<String> addresses = new ArrayList<>();
        for (int i = 0; i < userAdapter.getCount(); i++) {
            if (userAdapter.getCheckboxes().get(i).isChecked()) {
                addresses.add(((User)userAdapter.getItem(i)).getEmail());
            }
        }
        String[] result = new String[addresses.size()];
        return addresses.toArray(result);
    }

    @Override
    public String getMessage() {
        return getInvitationMessage();
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
        outState.putStringArrayList(ADDRESSES, addresses);
    }
}
