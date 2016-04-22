package straw.polito.it.straw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import java.util.ArrayList;

import straw.polito.it.straw.PriceContainer;
import straw.polito.it.straw.R;
import straw.polito.it.straw.data.Food;

/**
 * Created by Sylvain on 19/04/2016.
 */
public class FoodExpandableAdapterCheckbox extends FoodExpandableAdapter {

    private ArrayList<CheckBox> checkBoxListPLates;
    private ArrayList<CheckBox> checkBoxListDrinks;
    private ArrayList<CheckBox> checkBoxLists[];

    public FoodExpandableAdapterCheckbox(Context context) {
        super(context);
        this.checkBoxListPLates = new ArrayList<>();
        this.checkBoxListDrinks = new ArrayList<>();
        this.checkBoxLists = new ArrayList[] {this.checkBoxListPLates, this.checkBoxListDrinks};

    }

    public FoodExpandableAdapterCheckbox(Context context, ArrayList<Food> platesList, ArrayList<Food> drinksList) {
        super(context, platesList, drinksList);
        this.checkBoxListPLates = new ArrayList<>();
        this.checkBoxListDrinks = new ArrayList<>();
        this.checkBoxLists = new ArrayList[] {this.checkBoxListPLates, this.checkBoxListDrinks};
    }
    @Override
    protected void setSpecificElement(View convertView, int groupPosition, int childPosition) {
        CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.checkbox);
        if (childPosition < this.checkBoxLists[groupPosition].size())
            this.checkBoxLists[groupPosition].set(childPosition, checkBox);
        else if (childPosition == this.checkBoxLists[groupPosition].size())
            this.checkBoxLists[groupPosition].add(checkBox);
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
        for (int i = 0; i < this.checkBoxListPLates.size(); i++) {
            if (this.checkBoxListPLates.get(i).isChecked())
                platesSelected.add(i);
        }
        return platesSelected;
    }

    public ArrayList<Integer> getDrinks() {
        ArrayList<Integer> drinksSelected = new ArrayList<>();
        for (int i = 0; i < this.checkBoxListDrinks.size(); i++) {
            if (this.checkBoxListDrinks.get(i).isChecked())
                drinksSelected.add(i);
        }
        return drinksSelected;
    }
}
