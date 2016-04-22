package straw.polito.it.straw.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import straw.polito.it.straw.R;
import straw.polito.it.straw.data.Reservation;
import straw.polito.it.straw.utils.DateDisplay;
import straw.polito.it.straw.utils.PriceDisplay;
import straw.polito.it.straw.utils.TimerDisplay;

public class ConfirmReservationActivity extends AppCompatActivity {

    private TextView numberPeople;
    private TextView place;
    private DateDisplay date;
    private TimerDisplay time;
    private ExpandableListView list_item;
    private PriceDisplay price;
    private Button confirmButton;

    private Reservation reservation;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_reservation);
        Intent intent = getIntent();
        this.resources = getResources();
        this.reservation = Reservation.create(intent.getStringExtra(Reservation.RESERVATION));

        this.numberPeople = (TextView)findViewById(R.id.number_people);
        this.place = (TextView)findViewById(R.id.place);
        this.date = (DateDisplay)findViewById(R.id.Date);
        this.time = (TimerDisplay)findViewById(R.id.Time);
        this.list_item = (ExpandableListView)findViewById(R.id.list_item);
        this.price = (PriceDisplay)findViewById(R.id.Price);
        this.confirmButton = (Button)findViewById(R.id.confirm_button);

        this.numberPeople.setText(this.reservation.getNumberPeople() + this.resources.getString(R.string.Persons));
        if (this.reservation.getPlace().equals(Reservation.Place.OUTSIDE))
            this.place.setText(this.resources.getString(R.string.Outside));
        else
            this.place.setText(this.resources.getString(R.string.Inside));
        this.date.setDate(this.reservation.getYear(), this.reservation.getMonth(), this.reservation.getDay());
        this.time.setTime(this.reservation.getHourOfDay(), this.reservation.getMinutes());
        // To be continued

    }
}
