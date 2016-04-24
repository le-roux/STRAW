package straw.polito.it.straw.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import straw.polito.it.straw.BookTableInterface;
import straw.polito.it.straw.DateContainer;
import straw.polito.it.straw.DateDisplayer;
import straw.polito.it.straw.R;
import straw.polito.it.straw.TimeContainer;
import straw.polito.it.straw.TimeDisplayer;
import straw.polito.it.straw.adapter.ReservationAdapter;
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.data.Reservation;
import straw.polito.it.straw.data.Reservation.Place;
import straw.polito.it.straw.utils.DatePickerFragment;
import straw.polito.it.straw.utils.NumberPickerFragment;
import straw.polito.it.straw.utils.TimePickerFragment;
import straw.polito.it.straw.utils.TimerDisplay;

public class BookTableActivity extends AppCompatActivity implements BookTableInterface, DateContainer, TimeContainer{

    private TextView numberPeopleNumber;
    private Button calendarButton;
    private DateDisplayer calendar;
    private Button clockButton;
    private TimerDisplay clock;
    private CheckBox insideCheckbox;
    private CheckBox outsideCheckbox;
    private Button preOrderButton;
    private Button confirmButton;

    private Reservation reservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_table);

        this.numberPeopleNumber = (TextView)findViewById(R.id.number_people_number);
        this.calendarButton = (Button)findViewById(R.id.calendar);
        this.calendar = (DateDisplayer)findViewById(R.id.Date);
        this.clockButton = (Button)findViewById(R.id.clock);
        this.clock = (TimerDisplay)findViewById(R.id.Time);
        this.insideCheckbox = (CheckBox)findViewById(R.id.insideCheckbox);
        this.outsideCheckbox = (CheckBox)findViewById(R.id.outsideCheckbox);
        this.preOrderButton = (Button)findViewById(R.id.PreOrderButton);
        this.confirmButton = (Button)findViewById(R.id.confirm_button);


        //Add a listener to launch the NumberPicker dialog to select the number of people in the reservation
        this.numberPeopleNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment fragment = new NumberPickerFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(Manager.SEATS_AVAILABLE, 10); //TO DO : Get the exact number
                fragment.setArguments(bundle);
                fragment.show(BookTableActivity.this.getFragmentManager(), "NumberPicker");
            }
        });

        this.clockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment fragment = new TimePickerFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean(ReservationAdapter.ADAPTER, false);
                fragment.setArguments(bundle);
                fragment.show(getFragmentManager(), "timePicker");
            }
        });

        //Add a listener to launch the DatePickerDialog to select the date of the reservation
        this.calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment fragment = new DatePickerFragment();
                fragment.show(getFragmentManager(), "DatePicker");
            }
        });

        this.preOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
                Intent intent = new Intent(getApplicationContext(), PreOrderFoodActivity.class);
                intent.putExtra(Reservation.RESERVATION, reservation.toString());
                startActivity(intent);
            }
        });

        this.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData();
                Intent intent = new Intent(getApplicationContext(), ConfirmReservationActivity.class);
                intent.putExtra(Reservation.RESERVATION, reservation.toString());
                startActivity(intent);
            }
        });

        //Date creation/restoration
        if(savedInstanceState == null) {
            this.reservation = new Reservation();
        } else {
            this.reservation = Reservation.create(savedInstanceState.getString(Reservation.RESERVATION));
        }
        updateDisplay();
    }

    public TimerDisplay getClock() {
        return this.clock;
    }

    @Override
    public void setNumberPeople(int numberPeople) {
        this.numberPeopleNumber.setText(String.valueOf(numberPeople));
    }

    @Override
    public DateDisplayer getDateDisplayer() {
        return this.calendar;
    }

    //Update the display according to the values stored
    public void updateDisplay() {
        this.numberPeopleNumber.setText(String.valueOf(this.reservation.getNumberPeople()));
        this.calendar.setDate(this.reservation.getYear(), this.reservation.getMonth(), this.reservation.getDay());
        this.clock.setTime(this.reservation.getHourOfDay(), this.reservation.getMinutes());
        boolean inside = false;
        boolean outside = false;
        switch(this.reservation.getPlace()) {
            case INSIDE:
                inside = true;
                break;
            case OUTSIDE:
                outside = true;
                break;
        }
        this.insideCheckbox.setChecked(inside);
        this.outsideCheckbox.setChecked(outside);
    }

    //Update the stored data according to the content of the fields
    public void updateData() {
        this.reservation.setNumberPeople(Integer.decode(String.valueOf(this.numberPeopleNumber.getText())));
        this.reservation.setTime(this.calendar.getYear(), this.calendar.getMonth(), this.calendar.getDay(), this.clock.getHourOfDay(), this.clock.getMinutes());
        if (this.insideCheckbox.isChecked()) {
            if (this.outsideCheckbox.isChecked())
                this.reservation.setPlace(Place.NO_PREFERENCE);
            else
                this.reservation.setPlace(Place.INSIDE);
        } else if (this.outsideCheckbox.isChecked()) {
            this.reservation.setPlace(Place.OUTSIDE);
        } else
            this.reservation.setPlace(Place.NO_PREFERENCE);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        updateData();
        bundle.putString(Reservation.RESERVATION, this.reservation.toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        this.reservation = Reservation.create(savedInstanceState.getString(Reservation.RESERVATION));
        updateDisplay();
    }

    @Override
    public TimeDisplayer getTimeDisplayer() {
        return this.clock;
    }
}
