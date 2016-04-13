package straw.polito.it.straw.straw.polito.it.straw.utils;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import straw.polito.it.straw.R;
import straw.polito.it.straw.adapter.ReservationAdapter;
import straw.polito.it.straw.data.Reservation;

/**
 * Created by sylvain on 12/04/2016.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{
    private Reservation reservation;
    private ReservationAdapter reservationAdapter;

    public TimePickerFragment() {
        this.reservation = null;
    }

    public TimePickerFragment(Reservation reservation, ReservationAdapter reservationAdapter) {
        super();
        this.reservation = reservation;
        this.reservationAdapter = reservationAdapter;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Use the current time as default value
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minutes, DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.reservation.setTime(hourOfDay, minute);
        //this.reservationAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), getActivity().getString(R.string.HourChangedToast),
                Toast.LENGTH_LONG).show();
    }
}
