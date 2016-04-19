package straw.polito.it.straw.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import straw.polito.it.straw.PriceContainer;
import straw.polito.it.straw.R;
import straw.polito.it.straw.adapter.FoodAdapterCheckbox;
import straw.polito.it.straw.data.Food;
import straw.polito.it.straw.data.Plate;
import straw.polito.it.straw.utils.Logger;
import straw.polito.it.straw.utils.PriceDisplay;

public class AddPlateActivity extends AppCompatActivity implements PriceContainer {

    private ArrayList<Food> menu;
    private ListView menu_view;
    private PriceDisplay price;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plate);

        this.menu = new ArrayList<>(); //TO DO : change it
        this.menu.add(new Plate());
        menu.get(0).setPrice(10);
        this.menu_view = (ListView)findViewById(R.id.list_item);
        this.price = (PriceDisplay)findViewById(R.id.Price);
        this.addButton = (Button)findViewById(R.id.add_button);

        this.menu_view.setAdapter(new FoodAdapterCheckbox(this, this.menu));

        this.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent result = new Intent();
                ArrayList<Integer> plates = ((FoodAdapterCheckbox)menu_view.getAdapter()).getPlates();
                Bundle bundle = new Bundle();
                bundle.putIntegerArrayList(PreOrderFoodActivity.POSITIONS, plates);
                result.putExtra(PreOrderFoodActivity.RESULT, bundle);
                setResult(PreOrderFoodActivity.ADD_PLATE_REQUEST_CODE, result);
                finish();
            }
        });

    }

    @Override
    public PriceDisplay getPriceDisplay() {
        return this.price;
    }

    @Override
    public void updatePrice() {
        ArrayList<Integer> plates = ((FoodAdapterCheckbox)menu_view.getAdapter()).getPlates();
        double price = 0;
        for (Integer position : plates) {
            price += this.menu.get(position).getPrice();
        }
        this.price.setPrice(price);
    }
}
