package straw.polito.it.straw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.data.Review;

/**
 * Created by Andres Camilo Jimenez on 03/05/2016.
 */
public class ReviewAdapter extends ArrayAdapter<Review> {

    private Context context;
    private ArrayList<Review> reviews;

    public ReviewAdapter(Context context, ArrayList<Review> r) {
        super(context, R.layout.review_row,r);
        this.context=context;
        this.reviews=r;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.review_row, parent, false);

        RatingBar rate = (RatingBar) rowView.findViewById(R.id.rate);
        TextView desc = (TextView) rowView.findViewById(R.id.desc);

        if(reviews.size()>0) {
            desc.setText(reviews.get(position).getDescription());
            rate.setRating(reviews.get(position).getRate());
        }
        return rowView;
    }
}
