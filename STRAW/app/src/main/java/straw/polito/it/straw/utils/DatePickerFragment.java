package straw.polito.it.straw.utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

import straw.polito.it.straw.DateContainer;
import straw.polito.it.straw.DateDisplayer;

/**
 * Created by Sylvain on 15/04/2016.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    private DateDisplayer displayer;
    private DateContainer container;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        this.container = (DateContainer)getActivity();
        this.displayer = this.container.getDateDisplayer();

        //TO DO : add validation of the data
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.displayer.setDate(year, monthOfYear, dayOfMonth);
    }
}
