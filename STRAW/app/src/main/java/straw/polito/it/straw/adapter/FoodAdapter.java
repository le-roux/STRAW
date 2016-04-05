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
import straw.polito.it.straw.data.Food;
import straw.polito.it.straw.straw.polito.it.straw.utils.Logger;

/**
 * Created by Sylvain on 01/04/2016.
 */
public class FoodAdapter extends BaseAdapter {

    private ArrayList<Food> goods;
    private Context context;

    public FoodAdapter(Context context) {
        this.goods = new ArrayList<Food>();
        this.context = context;
    }

    public FoodAdapter(Context context, ArrayList<Food> goods) {
        this.goods = goods;
        this.context = context;
    }

    public void add(Food food) {
        this.goods.add(food);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_design, null);
        }

        ImageView imageView = (ImageView)convertView.findViewById(R.id.PlateImage);
        TextView title = (TextView)convertView.findViewById(R.id.PlateName);
        TextView description = (TextView)convertView.findViewById(R.id.PlateDescription);
        TextView price = (TextView)convertView.findViewById(R.id.PlatePrice);

        if (position < this.goods.size()) {
            String uri = this.goods.get(position).getImageURI();
            if(uri != null)
                imageView.setImageURI(Uri.parse(uri));
            title.setText(this.goods.get(position).getName());
            description.setText(this.goods.get(position).getDescription());
            price.setText(String.valueOf(this.goods.get(position).getPrice()) + " €");
        }
        return convertView;
    }
}
