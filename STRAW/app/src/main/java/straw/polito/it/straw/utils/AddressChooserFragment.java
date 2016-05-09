package straw.polito.it.straw.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import straw.polito.it.straw.AddressContainer;
import straw.polito.it.straw.R;

/**
 * Created by Sylvain on 09/05/2016.
 */
public class AddressChooserFragment extends DialogFragment {

    public static final String ADDRESSES = "addresses";
    private AddressContainer addressContainer;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.addressContainer = (AddressContainer)getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle args = getArguments();
        final String[] addresses = args.getStringArray(ADDRESSES);
        ListView listView = new ListView(getActivity());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Logger.d("item clicked");
                if (addressContainer != null) {
                    addressContainer.setAddressNumber(position);
                    AddressChooserFragment.this.dismiss();
                }
            }
        });
        listView.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.text_view, addresses));
        builder.setView(listView);
        return builder.create();
    }
}
