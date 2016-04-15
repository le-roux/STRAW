package straw.polito.it.straw.activities;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import straw.polito.it.straw.BookTableInterface;
import straw.polito.it.straw.R;
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.utils.NumberPickerFragment;

public class BookTableActivity extends AppCompatActivity implements BookTableInterface{

    private TextView numberPeopleNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_table);

        this.numberPeopleNumber = (TextView)findViewById(R.id.number_people_number);

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


    }

    @Override
    public void setNumberPeople(int numberPeople) {
        this.numberPeopleNumber.setText(String.valueOf(numberPeople));
    }
}
