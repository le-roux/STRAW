package straw.polito.it.straw.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.widget.Adapter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import straw.polito.it.straw.AdapterFragment;
import straw.polito.it.straw.activities.SearchDetailActivity;
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
    private Activity activity;

    private RestaurantMapFragment(Activity activity) {
        this.activity = activity;
    }

    public static RestaurantMapFragment createInstance(double latitude, double longitude, Activity activity) {
        RestaurantMapFragment fragment = new RestaurantMapFragment(activity);
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
        this.mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String title = marker.getTitle();
                String[] tokens = title.split(" - ");
                int index = Integer.decode(tokens[0]);
                Intent i = new Intent(activity, SearchDetailActivity.class);
                i.putExtra(SearchDetailActivity.RESTAURANT, ((Manager)adapter.getItem(index - 1)).toJSONObject());
                activity.startActivity(i);
            }
        });
        this.notifyDataSetChanged();
        this.resetView();
    }

    @Override
    public void notifyDataSetChanged() {
        if (this.mMap != null && this.adapter != null) {
            this.mMap.clear();
            ArrayList<Manager> restaurantsList = this.adapter.getList();
            int i = 0;
            for (Manager manager : restaurantsList) {
                i++;
                LatLng position = new LatLng(manager.getLatitude(), manager.getLongitude());
                StringBuilder builder = new StringBuilder();
                builder.append(i)
                        .append(" - ")
                        .append(manager.getRes_name());
                MarkerOptions options = new MarkerOptions().position(position).title(builder.toString());
                switch (manager.getType()) {
                    case (Manager.RESTAURANT):  {
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        break;
                    }
                    case (Manager.CANTEEN) : {
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                        break;
                    }
                    case (Manager.BAR) : {
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        break;
                    }
                    case (Manager.TAKEAWAY) : {
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        break;
                    }
                }
                this.mMap.addMarker(options);
            }
        }
    }

    /**
     * Recenter the view on the search location, with a default zoom
     */
    private void resetView() {
        if (this.mMap != null && this.userPosition != null) {
            this.mMap.moveCamera(CameraUpdateFactory.newLatLng(this.userPosition));
            this.mMap.moveCamera(CameraUpdateFactory.zoomTo(7f));
        }
    }
}
