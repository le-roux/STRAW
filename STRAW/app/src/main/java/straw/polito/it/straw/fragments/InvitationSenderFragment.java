package straw.polito.it.straw.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;

import straw.polito.it.straw.MessageSender;
import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.utils.DatabaseUtils;

/**
 * Created by Sylvain on 26/04/2016.
 */
public class InvitationSenderFragment extends DialogFragment {

    private MessageSender messageSender;
    private SmsManager smsManager;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.messageSender = (MessageSender)getActivity();
        this.smsManager = SmsManager.getDefault();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.SelectMessageType)
                .setPositiveButton(R.string.SMS, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] addresses = messageSender.getAddresses(false);
                        String message = messageSender.getMessage();
                        for (String address : addresses) {
                            smsManager.sendTextMessage(address, null, message, null, null);
                        }
                        messageSender.displayConfirmationToast(addresses.length);
                        DatabaseUtils databaseUtils = ((StrawApplication)getActivity().getApplication()).getDatabaseUtils();
                        for(String email:addresses){
                            databaseUtils.sendFriendNotification(email, message, messageSender.getSubject());
                        }
                    }
                })
                .setNeutralButton(R.string.Email, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] addresses = messageSender.getAddresses(true);
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setData(Uri.parse("mailto:"));
                        intent.setType("text/html");
                        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
                        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.STRAWInvitation));
                        intent.putExtra(Intent.EXTRA_TEXT, messageSender.getMessage());
                        startActivity(intent);
                        messageSender.displayConfirmationToast(addresses.length);
                        DatabaseUtils databaseUtils = ((StrawApplication)getActivity().getApplication()).getDatabaseUtils();
                        for(String email:addresses){
                            databaseUtils.sendFriendNotification(email, messageSender.getMessage(), messageSender.getSubject());
                        }
                    }
                })
                .setNegativeButton(R.string.Cancel, null);
        return builder.create();
    }
}
