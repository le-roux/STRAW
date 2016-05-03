package straw.polito.it.straw.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import straw.polito.it.straw.R;
import straw.polito.it.straw.activities.CreateManagerAccountActivity;

/**
 * Created by Sylvain on 03/05/2016.
 */
public class ProgressBarFragment extends DialogFragment {

    private TextView text;
    private Activity activity;

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.progress_bar_fragment, null);
        builder.setView(view);
        text = (TextView)view.findViewById(R.id.text);
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    public void setText(String text) {
        this.text.setText(text);
    }

    public void setText(int resId) {
        if (isAdded())
            setText(getResources().getString(resId));
    }
}
