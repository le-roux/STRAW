package straw.polito.it.straw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.ArrayList;

import straw.polito.it.straw.PriceContainer;
import straw.polito.it.straw.R;
import straw.polito.it.straw.data.Food;

/**
 * Created by Sylvain on 19/04/2016.
 */
public class FoodAdapterCheckbox extends FoodAdapter {

    private ArrayList<CheckBox> checkBoxListPLates;
    private ArrayList<CheckBox> checkBoxListDrinks;
    private ArrayList<CheckBox> checkBoxLists[];

    public FoodAdapterCheckbox(Context context) {
        super(context);
        this.checkBoxListPLates = new ArrayList<>();
        this.checkBoxListDrinks = new ArrayList<>();
        this.checkBoxLists = new ArrayList[] {this.checkBoxListPLates, this.checkBoxListDrinks};

    }

    public FoodAdapterCheckbox(Context context, ArrayList<Food> platesList, ArrayList<Food> drinksList) {
        super(context, platesList, drinksList);
        this.checkBoxListPLates = new ArrayList<>();
        this.checkBoxListDrinks = new ArrayList<>();
        this.checkBoxLists = new ArrayList[] {this.checkBoxListPLates, this.checkBoxListDrinks};
    }
    @Override
    protected void setSpecificElement(View convertView, int groupPosition, int childPosition) {
        CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.checkbox);
        if (childPosition < this.checkBoxLists[groupPosition].size())
            this.checkBoxLists.set(groupPosition * this.groups[0].size() + childPosition, checkBox);
        else if (childPosition == this.checkBoxList.size())
            this.checkBoxList.add(checkBox);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PriceContainer)context).updatePrice();
            }
        });
    }

    @Override
    protected View getConvertView(LayoutInflater layoutInflater) {
        return layoutInflater.inflate(R.layout.menu_item, null);
    }

    /**
     *
     * @return An ArrayList containing the position of the plates selected
     */
    public ArrayList<Integer> getPlates() {
        ArrayList<Integer> platesSelected = new ArrayList<>();
        for (int i = 0; i < this.checkBoxList.size(); i++) {
            if (this.checkBoxList.get(i).isChecked())
                platesSelected.add(i);
        }
        return platesSelected;
    }
}
