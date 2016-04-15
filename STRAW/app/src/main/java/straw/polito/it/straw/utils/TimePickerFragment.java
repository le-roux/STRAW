package straw.polito.it.straw.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.widget.Toast;

import straw.polito.it.straw.R;
import straw.polito.it.straw.Timer;
import straw.polito.it.straw.activities.BookTableActivity;
import straw.polito.it.straw.activities.DisplayReservationsActivity;
import straw.polito.it.straw.adapter.ReservationAdapter;
import straw.polito.it.straw.data.Reservation;

/**
 * Created by sylvain on 12/04/2016.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{
    private Timer timer;
    private Activity activity;
    private ReservationAdapter adapter;
    private boolean notifyAdapter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        int position = bundle.getInt(Reservation.RESERVATION);
        this.activity = getActivity();
        this.notifyAdapter = bundle.getBoolean(ReservationAdapter.ADAPTER);
        if (this.notifyAdapter) {
            this.adapter = ((DisplayReservationsActivity) this.activity).getAdapter();
            this.timer = (Timer) this.adapter.getItem(position);
        } else {
            this.timer = ((BookTableActivity)this.activity).getClock();
        }
        int hour = this.timer.getHourOfDay();
        int minutes = this.timer.getMinutes();

        return new TimePickerDialog(this.activity, this, hour, minutes,
                DateFormat.is24HourFormat(this.activity));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.timer.setIs24HFormat(DateFormat.is24HourFormat(this.activity));
        this.timer.setTime(hourOfDay, minute);
        if (this.notifyAdapter) {
            this.adapter.notifyDataSetChanged();
            Toast.makeText(getActivity(), getActivity().getString(R.string.HourChangedToast),
                    Toast.LENGTH_LONG).show();
        }
    }
}