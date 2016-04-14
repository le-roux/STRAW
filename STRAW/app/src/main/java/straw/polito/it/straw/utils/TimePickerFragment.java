package straw.polito.it.straw.utils;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.widget.Toast;

import straw.polito.it.straw.R;
import straw.polito.it.straw.activities.DisplayReservationsActivity;
import straw.polito.it.straw.adapter.ReservationAdapter;
import straw.polito.it.straw.data.Reservation;

/**
 * Created by sylvain on 12/04/2016.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{
    private Reservation reservation;
    private ReservationAdapter adapter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        int position = bundle.getInt(Reservation.RESERVATION);
        this.adapter = ((DisplayReservationsActivity)getActivity()).getAdapter();
        this.reservation = (Reservation)this.adapter.getItem(position);
        int hour = this.reservation.getHour();
        int minutes = this.reservation.getMinutes();

        return new TimePickerDialog(getActivity(), this, hour, minutes, DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.reservation.setTime(hourOfDay, minute);
        this.adapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), getActivity().getString(R.string.HourChangedToast),
                Toast.LENGTH_LONG).show();
    }
}
