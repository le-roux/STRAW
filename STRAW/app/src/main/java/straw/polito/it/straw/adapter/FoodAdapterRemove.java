package straw.polito.it.straw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.data.Food;

/**
 * Created by Sylvain on 19/04/2016.
 */
public class FoodAdapterRemove extends FoodAdapter {

    public FoodAdapterRemove(Context context) {
        super(context);
    }

    public FoodAdapterRemove(Context context, ArrayList<Food> menu) {
        super(context, menu);
    }

    @Override
    protected void setSpecificElement(View convertView, final int position) {
        //Listener of the 'remove' button of each item
        Button remove_button = (Button) convertView.findViewById(R.id.RemoveButton);

        remove_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goods.remove(position);
                FoodAdapterRemove.this.notifyDataSetChanged();
            }
        });
        //Necessary in order that the item can react (onItemClicked) when clicked somewhere
        // else than on the button
        remove_button.setFocusable(false);
    }

    @Override
    protected View getConvertView(LayoutInflater layoutInflater) {
        return layoutInflater.inflate(R.layout.food_item, null);
    }
}
