package straw.polito.it.straw.activities;

import android.content.Intent;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.data.Food;
import straw.polito.it.straw.data.Plate;

public class AddPlateActivity extends AddFoodActivity {

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_add_plate);
    }

    @Override
    protected ArrayList<Food> getMenu() {
        //TO DO
        ArrayList<Food> plates = new ArrayList<>();
        plates.add(new Plate());
        plates.get(0).setPrice(10);
        return plates;
    }

    @Override
    protected void setResult(Intent result) {
        setResult(PreOrderFoodActivity.ADD_PLATE_REQUEST_CODE, result);
    }
}
