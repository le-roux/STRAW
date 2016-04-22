package straw.polito.it.straw.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.data.Drink;
import straw.polito.it.straw.data.Food;

public class AddDrinkActivity extends AddFoodActivity {

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_add_drink);
    }

    @Override
    protected ArrayList<Food> getMenu() {
        //TO DO
        ArrayList<Food> drinks = new ArrayList<>();
        drinks.add(new Drink());
        drinks.get(0).setPrice(5);
        return drinks;
    }

    @Override
    protected void setResult(Intent result) {
        setResult(PreOrderFoodActivity.ADD_DRINK_REQUEST_CODE, result);
    }
}
