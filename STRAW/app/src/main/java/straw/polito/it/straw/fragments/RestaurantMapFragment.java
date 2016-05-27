package straw.polito.it.straw.fragments;

import android.widget.Adapter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import straw.polito.it.straw.AdapterFragment;
import straw.polito.it.straw.adapter.RestaurantListAdapter;

/**
 * Created by Sylvain on 27/05/2016.
 */
public class RestaurantMapFragment implements AdapterFragment, OnMapReadyCallback {
    private GoogleMap mMap;
    private RestaurantListAdapter adapter;
    private SupportMapFragment fragment;

    public static RestaurantMapFragment createInstance() {
        RestaurantMapFragment fragment = new RestaurantMapFragment();
        fragment.setFragment(SupportMapFragment.newInstance());
        fragment.getFragment().getMapAsync(fragment);
        return fragment;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        this.adapter = (RestaurantListAdapter)adapter;
    }

    @Override
    public Adapter getAdapter() {
        return this.adapter;
    }

    public void setMap(GoogleMap map) {
        this.mMap = map;
    }

    private void setFragment(SupportMapFragment fragment) {
        this.fragment = fragment;
    }

    public SupportMapFragment getFragment() {
        return this.fragment;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        this.mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        this.mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
