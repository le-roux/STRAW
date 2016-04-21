package straw.polito.it.straw.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.data.Drink;
import straw.polito.it.straw.data.Food;
import straw.polito.it.straw.data.Plate;
import straw.polito.it.straw.utils.ImageManager;
import straw.polito.it.straw.utils.Logger;

/**
 * Created by Sylvain on 01/04/2016.
 */
public abstract class FoodAdapter extends BaseAdapter implements ExpandableListAdapter {

    /**
     * List of the food elements managed by the Adapter
     */
    protected ArrayList<Food> platesList;
    protected ArrayList<Food> drinksList;
    protected ArrayList<Food>[] groups;
    protected Context context;

    public FoodAdapter(Context context) {
        this.platesList = new ArrayList<>();
        this.drinksList = new ArrayList<>();
        this.groups = new ArrayList[]{platesList, drinksList};
        this.context = context;
    }

    public FoodAdapter(Context context,ArrayList<Food> platesList, ArrayList<Food> drinksList) {
        this.platesList = platesList;
        this.drinksList = drinksList;
        this.groups = new ArrayList[]{platesList, drinksList};
        this.context = context;
    }

    @Override
    public int getCount() {
        return (this.platesList.size() + this.drinksList.size());
    }

    @Override
    public Object getItem(int position) {
        return null;
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
            setSpecificElement(convertView, position, position);
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
        if (position < this.platesList.size()) {
            String uri = this.platesList.get(position).getImageURI();
            if(uri != null)
                ImageManager.setImage(this.context, imageView, Uri.parse(uri));
            title.setText(this.platesList.get(position).getName());
            description.setText(this.platesList.get(position).getDescription());
            price.setText(String.valueOf(this.platesList.get(position).getPrice()) + " €");
        }
        return convertView;
    }


    @Override
    public int getGroupCount() {
        return 2;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (groupPosition < 2)
            return groups[groupPosition].size();
        else {
            Logger.d("group position " + groupPosition);
            return 0;
        }

    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups[groupPosition].get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new TextView(context);
        }
        if (groupPosition == 0)
            ((TextView)convertView).setText(R.string.Plates);
        else
            ((TextView)convertView).setText(R.string.Drinks);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = getConvertView(layoutInflater);
            setSpecificElement(convertView, groupPosition, childPosition);
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
        if (groupPosition < 2) {
            if (childPosition < this.groups[groupPosition].size()) {
                String uri = this.groups[groupPosition].get(childPosition).getImageURI();
                if (uri != null)
                    ImageManager.setImage(this.context, imageView, Uri.parse(uri));
                title.setText(this.groups[groupPosition].get(childPosition).getName());
                description.setText(this.groups[groupPosition].get(childPosition).getDescription());
                price.setText(String.valueOf(this.groups[groupPosition].get(childPosition).getPrice()) + " €");
            }
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

    }

    @Override
    public void onGroupCollapsed(int groupPosition) {

    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return 0;
    }

    protected abstract void setSpecificElement(View convertView, int groupPosition, int childPosition);
    protected abstract View getConvertView(LayoutInflater layoutInflater);
}
