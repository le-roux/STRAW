package straw.polito.it.straw.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import straw.polito.it.straw.R;
import straw.polito.it.straw.utils.DateDisplay;
import straw.polito.it.straw.utils.TimerDisplay;

public class InviteFriendActivity extends AppCompatActivity {

    private TextView restaurantName;
    private DateDisplay calendar;
    private TimerDisplay time;
    private Button sendInvitationsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);

        this.restaurantName = (TextView)findViewById(R.id.RestaurantName);
        this.calendar = (DateDisplay)findViewById(R.id.Date);
        this.time = (TimerDisplay)findViewById(R.id.Time);
        this.sendInvitationsButton = (Button)findViewById(R.id.SendInvitationButton);

        this.sendInvitationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.InvitationsSent, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}
