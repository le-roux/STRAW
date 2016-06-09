package straw.polito.it.straw.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.UserContainer;
import straw.polito.it.straw.adapter.ReservationAdapterCustomer;
import straw.polito.it.straw.utils.DatabaseUtils;

/**
 * Created by Sylvain on 19/05/2016.
 */
public class CustomerReservationsFragment extends Fragment {

    private UserContainer container;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.container = (UserContainer)getActivity();

        View view = inflater.inflate(R.layout.fragment_customer_reservations, container, false);
        ListView listView = (ListView)view.findViewById(R.id.list_item);
        DatabaseUtils databaseUtils = ((StrawApplication)getActivity().getApplication()).getDatabaseUtils();
        ReservationAdapterCustomer adapter = new ReservationAdapterCustomer(getActivity(), this.container.getUser().getReservations(), getActivity());
        databaseUtils.retrieveCurrentCustomerReservations(adapter);
        listView.setAdapter(adapter);
        return view;
    }
}
