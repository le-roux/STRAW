package straw.polito.it.straw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.activities.QuickSearchActivity;
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.utils.ImageManager;

/**
 * Created by tibo on 16/04/2016.
 */
public class RestaurantListAdapter extends BaseAdapter {

    private ArrayList<Manager> list;
    private static Context context;

    public RestaurantListAdapter(Context context) {
        this.list = new ArrayList<Manager>();
        this.context = context;
    }

    public RestaurantListAdapter(Context context,ArrayList<Manager> list) {
        this.list = list;
        this.context = context;

    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.restaurant_list_design, null);
        }
        TextView name = (TextView)convertView.findViewById(R.id.RestaurantName);
        name.setFocusable(false);
        TextView price = (TextView)convertView.findViewById(R.id.RestaurantPrice);
        name.setFocusable(false);
        ImageView photo = (ImageView)convertView.findViewById(R.id.RestaurantImage);
        photo.setFocusable(false);

        RatingBar ratingBar = (RatingBar)convertView.findViewById(R.id.RestaurantReviews);
        ratingBar.setRating(this.list.get(position).getRate());
        if (position < this.list.size()) {
            name.setText(String.valueOf(this.list.get(position).getRes_name()));

            String imageString = this.list.get(position).getImage();
            if(imageString != null)
                ImageManager.setImage(this.context, photo, imageString);
            double p = this.list.get(position).getMin_price();
            if(p<=6)
                price.setText("€");
            else if(p > 6 && p <= 12)
                price.setText("€€");
            else if(p > 12 && p < 20)
                price.setText("€€€");
            else if(p >= 20)
                price.setText("€€€€");
        }
        return convertView;
    }

    public ArrayList<Manager> getList() {
        return this.list;
    }
}
