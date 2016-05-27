package straw.polito.it.straw.fragments;

import android.widget.Adapter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import straw.polito.it.straw.AdapterFragment;
import straw.polito.it.straw.adapter.RestaurantListAdapter;
import straw.polito.it.straw.data.Manager;

/**
 * Created by Sylvain on 27/05/2016.
 */
public class RestaurantMapFragment implements AdapterFragment, OnMapReadyCallback {
    private GoogleMap mMap;
    private RestaurantListAdapter adapter;
    private SupportMapFragment fragment;
    private LatLng userPosition;

    private RestaurantMapFragment() {
        //Do nothing
    }

    public static RestaurantMapFragment createInstance(double latitude, double longitude) {
        RestaurantMapFragment fragment = new RestaurantMapFragment();
        fragment.setUserPosition(new LatLng(latitude, longitude));
        fragment.setFragment(SupportMapFragment.newInstance());
        fragment.getFragment().getMapAsync(fragment);
        return fragment;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        this.adapter = (RestaurantListAdapter)adapter;
        this.notifyDataSetChanged();
        this.resetView();
    }

    @Override
    public Adapter getAdapter() {
        return this.adapter;
    }

    private void setFragment(SupportMapFragment fragment) {
        this.fragment = fragment;
    }

    public SupportMapFragment getFragment() {
        return this.fragment;
    }

    private void setUserPosition(LatLng userPosition) {
        this.userPosition = userPosition;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        this.notifyDataSetChanged();
        this.resetView();
    }

    @Override
    public void notifyDataSetChanged() {
        if (this.mMap != null && this.adapter != null) {
            this.mMap.clear();
            ArrayList<Manager> restaurantsList = this.adapter.getList();
            for (Manager manager : restaurantsList) {
                LatLng position = new LatLng(manager.getLatitude(), manager.getLongitude());
                this.mMap.addMarker(new MarkerOptions().position(position).title(manager.getRes_name()));
            }
        }
    }

    /**
     * Recenter the view on the search location, with a default zoom
     */
    private void resetView() {
        if (this.mMap != null && this.userPosition != null) {
            this.mMap.moveCamera(CameraUpdateFactory.newLatLng(this.userPosition));
            this.mMap.moveCamera(CameraUpdateFactory.zoomTo(15f));
        }
    }
}
