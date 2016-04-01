package straw.polito.it.straw;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

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

        imageView.setImageURI(Uri.parse(this.goods.get(position).getImageURI()));
        title.setText(this.goods.get(position).getName());
        //TO DO : fill description field
        description.setText("");
        price.setText(String.valueOf(this.goods.get(position).getPrice()) + " €");
        return convertView;
    }
}
