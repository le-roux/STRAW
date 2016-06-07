package straw.polito.it.straw.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.adapter.FoodExpandableAdapterRemove;
import straw.polito.it.straw.data.Food;
import straw.polito.it.straw.data.Reservation;
import straw.polito.it.straw.utils.DatabaseUtils;
import straw.polito.it.straw.utils.DateDisplay;
import straw.polito.it.straw.utils.Logger;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("CONFIRM  RESERVATION");
        setSupportActionBar(toolbar);
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

        StringBuilder builder = new StringBuilder();
        builder.append(this.reservation.getNumberPeople())
                .append(' ')
                .append(this.resources.getString(R.string.Persons));
        this.numberPeople.setText(builder.toString());

        if (this.reservation.getPlace().equals(Reservation.Place.OUTSIDE))
            this.place.setText(this.resources.getString(R.string.Outside));
        else if (this.reservation.getPlace().equals(Reservation.Place.INSIDE))
            this.place.setText(this.resources.getString(R.string.Inside));
        else
            this.place.setText(this.getResources().getString(R.string.NoPreference));
        this.date.setDate(this.reservation.getYear(), this.reservation.getMonth(), this.reservation.getDay());
        this.time.setTime(this.reservation.getHourOfDay(), this.reservation.getMinutes());
        this.list_item.setAdapter(new FoodExpandableAdapterRemove(getApplicationContext(), this.reservation.getPlates(), this.reservation.getDrinks()));
        double price = 0;
        for (Food plate : this.reservation.getPlates())
            price += plate.getPrice();
        for (Food drink : this.reservation.getDrinks())
            price += drink.getPrice();
        this.price.setPrice(price);

        this.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 DatabaseUtils databaseUtils = ((StrawApplication)getApplication()).getDatabaseUtils();
                 databaseUtils.saveReservation(reservation);
             }
        });
    }
}
