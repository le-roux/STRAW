package straw.polito.it.straw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.data.Food;
import straw.polito.it.straw.utils.DatabaseUtils;

/**
 * Created by Sylvain on 19/04/2016.
 */
public class FoodExpandableAdapterRemove extends FoodExpandableAdapter {

    private boolean syncWithDatabase = false;
    private StrawApplication application;

    public FoodExpandableAdapterRemove(Context context) {
        super(context);
    }

    public FoodExpandableAdapterRemove(Context context, ArrayList<Food> platesList) {
        super(context, platesList, new ArrayList<Food>());
    }

    public FoodExpandableAdapterRemove(Context context, ArrayList<Food> platesList, ArrayList<Food> drinksList) {
        super(context, platesList, drinksList);
    }

    public void setSyncWithDatabase(boolean syncWithDatabase, StrawApplication application) {
        this.syncWithDatabase = syncWithDatabase;
        this.application = application;
    }

    @Override
    protected void setSpecificElement(View convertView, final int groupPosition, final int childPosition) {
        //Listener of the 'remove' button of each item
        Button remove_button = (Button) convertView.findViewById(R.id.RemoveButton);

        remove_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (syncWithDatabase) {
                    String restaurantName = application.getSharedPreferencesHandler().getCurrentManager().getRes_name();
                    String foodName = groups[groupPosition].get(childPosition).getName();
                    application.getDatabaseUtils().removeFood(restaurantName, groupPosition, foodName);
                }
                groups[groupPosition].remove(childPosition);
                FoodExpandableAdapterRemove.this.notifyDataSetChanged();
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
