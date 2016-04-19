package straw.polito.it.straw.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.adapter.FoodAdapter;
import straw.polito.it.straw.data.Food;
import straw.polito.it.straw.utils.PriceDisplay;

public class PreOrderFoodActivity extends AppCompatActivity {

    private Button addPlateButton;
    private Button addDrinkButton;
    private Button confirmButton;
    private PriceDisplay price;
    private ListView listView;
    private ArrayList<Food> command;

    public static final int ADD_PLATE_REQUEST_CODE = 1;
    public static final int ADD_DRINK_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_order_food);

        this.addPlateButton = (Button)findViewById(R.id.add_plate_button);
        this.addDrinkButton = (Button)findViewById(R.id.add_drink_button);
        this.confirmButton = (Button)findViewById(R.id.confirm_button);
        this.price = (PriceDisplay) findViewById(R.id.Price);
        this.listView = (ListView)findViewById(R.id.list_item);

        this.command = new ArrayList<>();

        //Set a listener to launch the AddPlate activity
        this.addPlateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddPlateActivity.class);
                startActivityForResult(intent, ADD_PLATE_REQUEST_CODE);
            }
        });

        //Set a listener to launch the AddDrink activity
        this.addDrinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TO DO
            }
        });

        //Set a listener to confirm the command and launch the confirmation activity
        this.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TO DO
            }
        });

        //this.listView.setAdapter(new FoodAdapter(getApplicationContext()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case (ADD_PLATE_REQUEST_CODE):
                    break;
                case (ADD_DRINK_REQUEST_CODE):
                    break;
            }
        }
        updatePrice();
    }

    public void updatePrice() {
        double price = 0;
        for (Food food : this.command) {
            price += food.getPrice();
        }
        this.price.setPrice(price);
    }
}
