package straw.polito.it.straw.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import straw.polito.it.straw.R;
import straw.polito.it.straw.UserContainer;
import straw.polito.it.straw.data.Friend;

/**
 * Created by Sylvain on 13/05/2016.
 */
public class FriendCreationFragment extends DialogFragment{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_friend_creation, null);
        final EditText nameText = (EditText)view.findViewById(R.id.name_field);
        final EditText phoneText = (EditText)view.findViewById(R.id.phoneNumber_field);
        final EditText emailText = (EditText)view.findViewById(R.id.email_field);

        builder.setView(view)
                .setPositiveButton(getString(R.string.Create), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameText.getText().toString();
                        String phoneNumber = phoneText.getText().toString();
                        if (phoneNumber.equals(""))
                            phoneNumber = null;
                        String emailAddress = emailText.getText().toString();
                        if (emailAddress.equals(""))
                            emailAddress = null;
                        Friend friend = new Friend(name, phoneNumber, emailAddress);
                        UserContainer container = (UserContainer)getActivity();
                        container.getUser().addFriend(friend);
                    }
                })
                .setNegativeButton(getString(R.string.Cancel), null);
        return builder.create();
    }
}
