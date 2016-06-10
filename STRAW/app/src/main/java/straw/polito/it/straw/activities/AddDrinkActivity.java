package straw.polito.it.straw.activities;

import android.content.Intent;
import android.support.v7.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.data.Food;
import straw.polito.it.straw.data.Menu;

public class AddDrinkActivity extends AddFoodActivity {

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_add_food);
    }

    @Override
    protected ArrayList<Food> getMenu() {
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(getIntent().getStringExtra(Menu.MENU));
        } catch (JSONException e) {
            return new ArrayList<>();
        }
        ArrayList<Food> drinks = Menu.getDrinks(jsonArray);
        return drinks;
    }

    @Override
    protected void setResult(Intent result) {
        setResult(PreOrderFoodActivity.ADD_DRINK_REQUEST_CODE, result);
    }

    @Override
    protected void setTitle(Toolbar toolbar) {
        toolbar.setTitle(getString(R.string.Add_Drink));
    }
}
