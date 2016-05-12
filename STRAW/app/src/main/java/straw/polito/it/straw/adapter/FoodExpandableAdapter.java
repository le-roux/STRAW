package straw.polito.it.straw.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListAdapter;
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
public class FoodExpandableAdapter extends BaseExpandableListAdapter implements ExpandableListAdapter {

    /**
     * List of the food elements managed by the Adapter
     */
    protected ArrayList<Food> platesList;
    protected ArrayList<Food> drinksList;
    protected ArrayList<Food>[] groups;
    protected Context context;

    public FoodExpandableAdapter(Context context) {
        this.platesList = new ArrayList<>();
        this.drinksList = new ArrayList<>();
        this.groups = new ArrayList[]{platesList, drinksList};
        this.context = context;
    }

    public FoodExpandableAdapter(Context context, ArrayList<Food> platesList, ArrayList<Food> drinksList) {
        this.platesList = platesList;
        this.drinksList = drinksList;
        this.groups = new ArrayList[]{platesList, drinksList};
        this.context = context;
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
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.group_item, null);
            if (groupPosition == 1)
                ((TextView)convertView.findViewById(R.id.text)).setText(R.string.Drinks);
        }
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
                String image = this.groups[groupPosition].get(childPosition).getImage();
                if (image != null) {
                    ImageManager.setImage(this.context, imageView, image);
                }
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

    @Override
    public boolean hasStableIds() {
        return false;
    }

    protected void setSpecificElement(View convertView, int groupPosition, int childPosition) {
        CheckBox checkbox = (CheckBox)convertView.findViewById(R.id.checkbox);
        checkbox.setVisibility(View.INVISIBLE);
    }
    protected View getConvertView(LayoutInflater layoutInflater) {
        return layoutInflater.inflate(R.layout.menu_item, null);
    }
}
