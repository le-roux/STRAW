package straw.polito.it.straw.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import straw.polito.it.straw.R;
import straw.polito.it.straw.activities.BookTableActivity;
import straw.polito.it.straw.data.Manager;

/**
 * Created by Sylvain on 15/04/2016.
 */
public class NumberPickerFragment extends DialogFragment {

    private BookTableActivity activity;
    private int numberPeopleMax = 2; //To change
    private NumberPicker numberPicker;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.activity = (BookTableActivity)getActivity();
        Bundle args = getArguments();
        this.numberPeopleMax = args.getInt(Manager.SEATS_AVAILABLE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setPositiveButton(R.string.Confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                activity.setNumberPeople(numberPicker.getValue());
            }
        });

        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                //Do nothing
            }
        });

        LayoutInflater inflater = this.activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.number_picker, null);
        numberPicker = (NumberPicker)view.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(numberPeopleMax);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        return dialog;
    }
}
