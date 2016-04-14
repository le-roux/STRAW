package straw.polito.it.straw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.data.Food;

/**
 * Created by Andres Camilo Jimenez on 13/04/2016.
 */
public class OfferAdapter extends BaseAdapter {

    private ArrayList<Food> offer;
    private Context context;
    private boolean sw;

    public OfferAdapter(Context context,ArrayList<Food>offer,boolean sw) {
        this.offer = offer;
        this.context = context;
        this.sw=sw;
    }

    @Override
    public int getCount() {
        return this.offer.size();
    }

    @Override
    public Object getItem(int position) {
        return this.offer.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.offer_list_design, null);

            //Listener of the 'remove' button of each item
            ImageView remove_button = (ImageView) convertView.findViewById(R.id.imageView);
            if(sw) {
                remove_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        offer.remove(position);
                        OfferAdapter.this.notifyDataSetChanged();
                    }
                });
                //Necessary in order that the item can react (onItemClicked) when clicked somewhere
                // else than on the button
                remove_button.setFocusable(false);
            }else{
                remove_button.setVisibility(View.INVISIBLE);
            }

        }

        //Get the different components of an item
        TextView title = (TextView)convertView.findViewById(R.id.PlateName);
        title.setFocusable(false);
        TextView price = (TextView)convertView.findViewById(R.id.PlatePrice);
        price.setFocusable(false);

        //Fill the View with the proper data
        if (position < this.offer.size()) {
            String uri = this.offer.get(position).getImageURI();
            title.setText(this.offer.get(position).getName());
            price.setText(String.valueOf(this.offer.get(position).getPrice()) + " €");
        }
        return convertView;
    }
}