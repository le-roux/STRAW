package straw.polito.it.straw.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import straw.polito.it.straw.R;

/**
 * Created by Sylvain on 08/06/2016.
 */
public class InfoWindow implements GoogleMap.InfoWindowAdapter {

    private static Context context;

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.info_window, null);
        TextView title = (TextView)view.findViewById(R.id.title);
        title.setText(marker.getTitle());
        float rate = Float.parseFloat(marker.getSnippet());
        RatingBar ratingBar = (RatingBar)view.findViewById(R.id.ratingBar);
        ratingBar.setRating(rate);
        return view;
    }

    public static void setContext(Context newContext) {
        context = newContext;
    }
}
