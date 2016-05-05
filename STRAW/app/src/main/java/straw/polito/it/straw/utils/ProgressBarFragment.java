package straw.polito.it.straw.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import straw.polito.it.straw.R;

/**
 * Created by Sylvain on 03/05/2016.
 */
public class ProgressBarFragment extends DialogFragment {

    private TextView textView;
    public static final String TEXT = "Text";


    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.progress_bar_fragment, null);
        builder.setView(view);
        textView = (TextView)view.findViewById(R.id.text);
        setText(getArguments().getString(TEXT));
        return builder.create();
    }

    public void setText(String text) {
        if (text != null)
            this.textView.setText(text);
    }

    public void setText(int resId) {
        if (isAdded())
            setText(getResources().getString(resId));
    }
}
