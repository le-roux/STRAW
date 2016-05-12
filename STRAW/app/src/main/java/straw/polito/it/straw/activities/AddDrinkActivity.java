package straw.polito.it.straw.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.data.Drink;
import straw.polito.it.straw.data.Food;
import straw.polito.it.straw.data.Menu;

public class AddDrinkActivity extends AddFoodActivity {

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_add_food);
        TextView title = (TextView)findViewById(R.id.title);
        title.setText(R.string.Add_drink);
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
}
