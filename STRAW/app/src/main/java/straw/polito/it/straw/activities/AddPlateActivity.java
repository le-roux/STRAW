package straw.polito.it.straw.activities;

import android.content.Intent;
import android.support.v7.widget.Toolbar;

import org.json.JSONArray;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.data.Food;
import straw.polito.it.straw.data.Menu;

public class AddPlateActivity extends AddFoodActivity {

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_add_food);
    }

    @Override
    protected ArrayList<Food> getMenu() {
        JSONArray jsonArray = Menu.getMenuFromSharedPreferences(this.getApplicationContext());
        ArrayList<Food> plates = Menu.getPlates(jsonArray);
        return plates;
    }

    @Override
    protected void setResult(Intent result) {
        setResult(PreOrderFoodActivity.ADD_PLATE_REQUEST_CODE, result);
    }

    @Override
    protected void setTitle(Toolbar toolbar) {
        toolbar.setTitle(getString(R.string.Add_Plate));
    }
}
