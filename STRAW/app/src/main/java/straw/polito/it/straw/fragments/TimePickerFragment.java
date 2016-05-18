package straw.polito.it.straw.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.BaseAdapter;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import straw.polito.it.straw.BaseAdapterContainer;
import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.TimeContainer;
import straw.polito.it.straw.TimeDisplayer;
import straw.polito.it.straw.adapter.ReservationAdapter;
import straw.polito.it.straw.data.Reservation;
import straw.polito.it.straw.utils.DatabaseUtils;

/**
 * Created by sylvain on 12/04/2016.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{
    private TimeDisplayer timeDisplayer;
    private Activity activity;
    private BaseAdapter adapter;
    private boolean notifyAdapter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        int position = bundle.getInt(Reservation.RESERVATION);
        this.activity = getActivity();
        this.notifyAdapter = bundle.getBoolean(ReservationAdapter.ADAPTER);
        if (this.notifyAdapter) {
            this.adapter = ((BaseAdapterContainer)this.activity).getAdapter();
            this.timeDisplayer = (TimeDisplayer) this.adapter.getItem(position);
        } else {
            this.timeDisplayer = ((TimeContainer)this.activity).getTimeDisplayer();
        }
        int hour = this.timeDisplayer.getHourOfDay();
        int minutes = this.timeDisplayer.getMinutes();

        return new TimePickerDialog(this.activity, this, hour, minutes,
                DateFormat.is24HourFormat(this.activity));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.timeDisplayer.setIs24HFormat(DateFormat.is24HourFormat(this.activity));
        this.timeDisplayer.setTime(hourOfDay, minute);
        if (this.notifyAdapter) {
            if (this.adapter.getClass().equals(ReservationAdapter.class)) {
                /**
                 * Update the reservation in the database
                 */
                StrawApplication application = (StrawApplication)this.activity.getApplication();
                DatabaseUtils databaseUtils = application.getDatabaseUtils();
                Map<String, Object> map = new HashMap<>();
                map.put(Reservation.ID, ((Reservation)this.timeDisplayer).getId());
                map.put(Reservation.HOUR, ((Reservation)this.timeDisplayer).getHourOfDay());
                map.put(Reservation.MINUTES, ((Reservation)this.timeDisplayer).getMinutes());
                map.put(Reservation.STATUS, Reservation.CHANGED);
                ((ReservationAdapter)this.adapter).setIconVisible(ReservationAdapter.WAIT_ICON);
                ProgressDialog dialog = new ProgressDialog(this.activity);
                dialog.setIndeterminate(true);
                dialog.setMessage(this.activity.getString(R.string.UpdatingReservation));
                dialog.show();
                databaseUtils.updateReservation(map, dialog);
                /**
                 * Update the status of the reservation
                 */
                ((Reservation)this.timeDisplayer).setStatus(Reservation.CHANGED);
                /**
                 * Display a message indicating that everything worked fine.
                 */
                Toast.makeText(getActivity(), getActivity().getString(R.string.HourChangedToast),
                        Toast.LENGTH_LONG).show();
            }
            this.adapter.notifyDataSetChanged();
        }
    }
}