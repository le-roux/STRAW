package straw.polito.it.straw.adapter;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.data.Review;
import straw.polito.it.straw.utils.Logger;

/**
 * Created by Andres Camilo Jimenez on 03/05/2016.
 */
public class ReviewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Review> reviews;
    private boolean sw;

    public ReviewAdapter(Context context, ArrayList<Review> r, boolean sw) {
        this.context = context;
        this.reviews = r;
        this.sw = sw;
    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public Object getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.review_row, null);

        RatingBar rate = (RatingBar) convertView.findViewById(R.id.rate);
        TextView desc = (TextView) convertView.findViewById(R.id.desc);
        TextView res = (TextView) convertView.findViewById(R.id.res_name);
        if(sw) {
            res.setText(reviews.get(position).getRestaurant());
        }
        desc.setText(reviews.get(position).getDescription());
        rate.setRating(reviews.get(position).getRate());
        return convertView;
    }

    public ArrayList<Review> getReviewList() {
        return this.reviews;
    }
}
