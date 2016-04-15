package straw.polito.it.straw.activities;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import straw.polito.it.straw.BookTableInterface;
import straw.polito.it.straw.DateContainer;
import straw.polito.it.straw.DateDisplayer;
import straw.polito.it.straw.R;
import straw.polito.it.straw.adapter.ReservationAdapter;
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.data.TimerDisplay;
import straw.polito.it.straw.utils.DatePickerFragment;
import straw.polito.it.straw.utils.NumberPickerFragment;
import straw.polito.it.straw.utils.TimePickerFragment;

public class BookTableActivity extends AppCompatActivity implements BookTableInterface, DateContainer{

    private TextView numberPeopleNumber;
    private Button calendarButton;
    private DateDisplayer calendar;
    private Button clockButton;
    private TimerDisplay clock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_table);

        this.numberPeopleNumber = (TextView)findViewById(R.id.number_people_number);
        this.calendarButton = (Button)findViewById(R.id.calendar);
        this.calendar = (DateDisplayer)findViewById(R.id.Date);
        this.clockButton = (Button)findViewById(R.id.clock);
        this.clock = (TimerDisplay)findViewById(R.id.Time);

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
}
