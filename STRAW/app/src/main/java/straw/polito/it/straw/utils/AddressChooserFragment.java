package straw.polito.it.straw.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import straw.polito.it.straw.AddressContainer;
import straw.polito.it.straw.R;
import straw.polito.it.straw.activities.AdvancedSearchActivity;

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

    public static List<Address> showAddressChooser(Activity activity, String addressName) {
        Geocoder geocoder = new Geocoder(activity);
        List<Address> addressList;
        if (Geocoder.isPresent()) {
            try {
                addressList = geocoder.getFromLocationName(addressName, 10);
                AddressChooserFragment fragment = new AddressChooserFragment();
                Bundle args = new Bundle();
                String[] addresses = new String[addressList.size()];
                String string;
                for (int i = 0; i < addressList.size(); i++) {
                    Address address = addressList.get(i);
                    string = address.getAddressLine(0) + ' ' + address.getAddressLine(1) + ' ' + address.getAddressLine(2);
                    addresses[i] = string;
                }
                args.putStringArray(AddressChooserFragment.ADDRESSES, addresses);
                fragment.setArguments(args);
                fragment.show(activity.getFragmentManager(), "Address chooser");
                return addressList;
            } catch (IOException e) {
                e.printStackTrace();
                Logger.d("error geocoder");
                Toast.makeText(activity, R.string.ErrorGeocoder, Toast.LENGTH_LONG).show();
                return null;
            }
        } else {
            Logger.d("no geocoder");
            return null;
        }
    }
}
