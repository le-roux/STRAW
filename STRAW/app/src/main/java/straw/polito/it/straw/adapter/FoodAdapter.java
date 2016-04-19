package straw.polito.it.straw.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.data.Food;
import straw.polito.it.straw.utils.ImageManager;
import straw.polito.it.straw.utils.Logger;

/**
 * Created by Sylvain on 01/04/2016.
 */
public abstract class FoodAdapter extends BaseAdapter {

    /**
     * List of the food elements managed by the Adapter
     */
    protected ArrayList<Food> goods;
    protected Context context;

    public FoodAdapter(Context context) {
        this.goods = new ArrayList<Food>();
        this.context = context;
    }

    public FoodAdapter(Context context,ArrayList<Food> goods) {
        this.goods = goods;
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.goods.size();
    }

    @Override
    public Object getItem(int position) {
        return this.goods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = getConvertView(layoutInflater);
            setSpecificElement(convertView, position);
        }

        //Get the different components of an item
        ImageView imageView = (ImageView)convertView.findViewById(R.id.PlateImage);
        imageView.setFocusable(false);
        TextView title = (TextView)convertView.findViewById(R.id.PlateName);
        title.setFocusable(false);
        TextView description = (TextView) convertView.findViewById(R.id.PlateDescription);
        description.setFocusable(false);
        TextView price = (TextView)convertView.findViewById(R.id.PlatePrice);
        price.setFocusable(false);

        //Fill the View with the proper data
        if (position < this.goods.size()) {
            String uri = this.goods.get(position).getImageURI();
            if(uri != null)
                ImageManager.setImage(this.context, imageView, Uri.parse(uri));
            title.setText(this.goods.get(position).getName());
            description.setText(this.goods.get(position).getDescription());
            price.setText(String.valueOf(this.goods.get(position).getPrice()) + " €");
        }
        return convertView;
    }

    protected abstract void setSpecificElement(View convertView, int position);
    protected abstract View getConvertView(LayoutInflater layoutInflater);
}
