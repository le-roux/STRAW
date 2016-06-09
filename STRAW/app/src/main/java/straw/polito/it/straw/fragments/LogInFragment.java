package straw.polito.it.straw.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.activities.DisplayInvitationActivity;
import straw.polito.it.straw.utils.DatabaseUtils;

/**
 * Created by Sylvain on 02/06/2016.
 */
public class LogInFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.log_in_fragment, null);

        final String restaurantName = ((DisplayInvitationActivity)getActivity()).getRestaurantName();

        final EditText emailEdit = (EditText)view.findViewById(R.id.email_editText);
        final EditText passwordEdit = (EditText)view.findViewById(R.id.password_editText);

        builder.setView(view)
            .setPositiveButton(R.string.log_in, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInt, int which) {
                    //Retrieve the values
                    String email = emailEdit.getText().toString();
                    String password = passwordEdit.getText().toString();

                    StrawApplication application = (StrawApplication)getActivity().getApplication();
                    DatabaseUtils databaseUtils = application.getDatabaseUtils();

                    //Prepare the progress bar
                    ProgressDialog dialog = new ProgressDialog(getActivity());
                    dialog.setMessage(getActivity().getString(R.string.LoggingIn));
                    dialog.setIndeterminate(true);
                    dialog.setCancelable(false);
                    dialog.show();

                    //Perform the log in
                    databaseUtils.logInAndReserve(email, password, restaurantName, dialog);
                }
            })
            .setNegativeButton(R.string.Cancel, null);
        return builder.create();
    }
}
