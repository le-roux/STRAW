package straw.polito.it.straw.fragments;

import android.widget.Adapter;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import straw.polito.it.straw.AdapterFragment;

/**
 * Created by Sylvain on 23/05/2016.
 */
public class RestaurantMapFragment extends MapFragment implements AdapterFragment{
    private Adapter adapter;
    private MapFragment fragment;

    public RestaurantMapFragment() {
        this.fragment = MapFragment.newInstance();
        this.fragment.getMapAsync((OnMapReadyCallback)this.getActivity());
    }

    public MapFragment getFragment() {
        return this.fragment;
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
    }

    public Adapter getAdapter() {
        return this.adapter;
    }
}
