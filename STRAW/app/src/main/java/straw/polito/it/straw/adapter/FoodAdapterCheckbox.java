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
public class FoodAdapterCheckbox extends FoodAdapter {

    private ArrayList<CheckBox> checkBoxList;

    public FoodAdapterCheckbox(Context context) {
        super(context);
        this.checkBoxList = new ArrayList<>();
    }

    public FoodAdapterCheckbox(Context context, ArrayList<Food> menu) {
        super(context, menu);
        this.checkBoxList = new ArrayList<>();
    }
    @Override
    protected void setSpecificElement(View convertView, final int position) {
        final CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.checkbox);
        if (position < this.checkBoxList.size())
            this.checkBoxList.set(position, checkBox);
        else if (position == this.checkBoxList.size())
            this.checkBoxList.add(checkBox);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBoxList.get(position).setChecked(checkBox.isChecked());
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
