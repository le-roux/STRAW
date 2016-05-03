package straw.polito.it.straw.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.activities.QuickSearchActivity;
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.utils.ImageManager;
import straw.polito.it.straw.utils.Logger;

/**
 * Created by tibo on 16/04/2016.
 */
public class RestaurantListAdapter extends BaseAdapter {

    private ArrayList<Manager> list;
    private static Context context;
    private QuickSearchActivity parentActivity;

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

        //RatingBar rb_reviews = (RatingBar)convertView.findViewById(R.id.RestaurantReviews);
        if (position < this.list.size()) {
            name.setText(String.valueOf(this.list.get(position).getRes_name()));

            String uri = this.list.get(position).getImageURI();
            Logger.d("uri = " + uri);
            if(uri != null)
                ImageManager.setImage(this.context, photo, Uri.parse(uri));
            //name.setText("coucou");
        }

        return convertView;
    }
}
