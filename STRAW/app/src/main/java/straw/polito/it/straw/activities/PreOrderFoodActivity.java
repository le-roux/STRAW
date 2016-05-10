package straw.polito.it.straw.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import straw.polito.it.straw.PriceContainer;
import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.adapter.FoodExpandableAdapter;
import straw.polito.it.straw.adapter.FoodExpandableAdapterRemove;
import straw.polito.it.straw.data.Food;
import straw.polito.it.straw.data.Menu;
import straw.polito.it.straw.data.Reservation;
import straw.polito.it.straw.utils.DatabaseUtils;
import straw.polito.it.straw.utils.PriceDisplay;

public class PreOrderFoodActivity extends AppCompatActivity implements PriceContainer{

    private Button addPlateButton;
    private Button addDrinkButton;
    private Button confirmButton;
    private PriceDisplay price;
    private ExpandableListView listView;
    private ArrayList<Food>[] command;
    private ArrayList<Food>[] menu = null;
    private Reservation reservation;

    public static final int ADD_PLATE_REQUEST_CODE = 1;
    public static final int ADD_DRINK_REQUEST_CODE = 2;

    public static final String POSITIONS = "Positions";
    public static final String COMMAND = "Command";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_order_food);
        Intent intent = getIntent();
        if(intent!=null) {
            if(intent.getStringExtra(Reservation.RESERVATION)!=null) {
                this.reservation = Reservation.create(intent.getStringExtra(Reservation.RESERVATION));
            }
        }

        this.addPlateButton = (Button)findViewById(R.id.add_plate_button);
        this.addDrinkButton = (Button)findViewById(R.id.add_drink_button);
        this.confirmButton = (Button)findViewById(R.id.confirm_button);
        this.price = (PriceDisplay) findViewById(R.id.Price);
        this.listView = (ExpandableListView)findViewById(R.id.list_item);

        if (this.menu == null) {
            /**
             * The menu can't have been retrieved from the remote database
             */
            JSONArray data = Menu.getMenuFromSharedPreferences(this.getApplicationContext());
            this.menu = new ArrayList[2];
            this.menu[Menu.PLATES] = new ArrayList<>();
            this.menu[Menu.DRINKS] = new ArrayList<>();
            Menu.restoreMenu(data, this.menu);
        }

        this.command = new ArrayList[2];
        this.command[Menu.PLATES] = new ArrayList();
        this.command[Menu.DRINKS] = new ArrayList();

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
                Intent intent = new Intent(getApplicationContext(), AddDrinkActivity.class);
                startActivityForResult(intent, ADD_DRINK_REQUEST_CODE);
            }
        });

        //Set a listener to confirm the command and launch the confirmation activity
        this.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reservation.setPlates(command[Menu.PLATES]);
                reservation.setDrinks(command[Menu.DRINKS]);
                Intent intent = new Intent(getApplicationContext(), ConfirmReservationActivity.class);
                intent.putExtra(Reservation.RESERVATION, reservation.toString());
                startActivity(intent);
            }
        });

        FoodExpandableAdapter adapter = new FoodExpandableAdapterRemove(getApplicationContext(), this.command[Menu.PLATES], this.command[Menu.DRINKS]);
        this.listView.setAdapter(adapter);

        DatabaseUtils databaseUtils = ((StrawApplication)getApplication()).getDatabaseUtils();
        databaseUtils.retrieveMenu(this.reservation.getRestaurant(), adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            int type = -1;
            switch (requestCode) {
                case (ADD_PLATE_REQUEST_CODE):
                    type = 0;
                    break;
                case (ADD_DRINK_REQUEST_CODE):
                    type = 1;
                    break;
            }
            if (type != -1) {
                ArrayList<Integer> result = data.getIntegerArrayListExtra(POSITIONS);
                for (Integer i : result) {
                    this.command[type].add(this.menu[type].get(i));
                }
                ((FoodExpandableAdapter)this.listView.getExpandableListAdapter()).notifyDataSetChanged();
            }
        }
        updatePrice();
    }

    @Override
    public PriceDisplay getPriceDisplay() {
        return this.price;
    }

    @Override
    public void updatePrice() {
        double price = 0;
        for (int i = 0; i < 2; i++) {
            for (Food food : this.command[i]) {
                price += food.getPrice();
            }
        }
        this.price.setPrice(price);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        JSONArray commandJson = Menu.saveMenu(this.command);
        outState.putString(COMMAND, commandJson.toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        JSONArray commandJson = null;
        try {
            commandJson = new JSONArray(savedInstanceState.getString(COMMAND));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Menu.restoreMenu(commandJson, this.command);
    }
}
