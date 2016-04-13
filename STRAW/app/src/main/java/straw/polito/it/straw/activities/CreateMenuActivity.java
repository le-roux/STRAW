package straw.polito.it.straw.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.adapter.FoodAdapter;
import straw.polito.it.straw.data.Drink;
import straw.polito.it.straw.data.Food;
import straw.polito.it.straw.R;
import straw.polito.it.straw.data.Plate;
import straw.polito.it.straw.straw.polito.it.straw.utils.Logger;
import straw.polito.it.straw.straw.polito.it.straw.utils.ObjectSerializer;


public class CreateMenuActivity extends AppCompatActivity {

    //Request code for the intents
    private static final int EDIT_FOOD = 1;
    private static final int ADD_FOOD = 2;

    //Keys for storing and retrieving the data in bundles or sharedPreference
    public static final String ELEMENT = "it.polito.straw.Element";
    public static final String ID = "it.polito.straw.Id";
    public static final String ACTION = "it.polito.straw.Action";
    public static final String ADD_ELEMENT = "it.polito.straw.Add";
    public static final String EDIT_ELEMENT = "it.polito.straw.Edit";
    public static final String NUMBER_OF_ELEMENTS = "ElementsNb";

    private ListView food_listView;
    private ArrayList<Food> list_plate;
    private Context context;
    private Button add_plate_button;
    private Button add_drink_button;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_menu);

        this.context = this;
        this.context = getApplicationContext();
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //Listener for the "Add plate" button
        this.add_plate_button = (Button)findViewById(R.id.add_plate_button);
        this.add_plate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddPlateActivity.class);
                intent.putExtra(ACTION, ADD_ELEMENT);
                startActivityForResult(intent, ADD_FOOD);
            }
        });

        //Listener for the "Add drink" button
        this.add_drink_button = (Button)findViewById(R.id.add_drink_button);
        this.add_drink_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddDrinkActivity.class);
                intent.putExtra(ACTION, ADD_ELEMENT);
                startActivityForResult(intent, ADD_FOOD);
            }
        });

        this.list_plate = new ArrayList<Food>();
        if (savedInstanceState == null) {
            //No data temporarily stored
            //Get number of elements stored in sharedPreference
            int elementsNb = sharedPreferences.getInt(NUMBER_OF_ELEMENTS, 0);
            //Retrieve element(s) from sharedPreference
            for (int i = 0; i < elementsNb; i++) {
                this.list_plate.add(Food.create(sharedPreferences.getString(String.valueOf(i), "")));
            }
            if (elementsNb == 0)
                //No data found in the sharedPreference --> default init
                //TO DO : change default init for final version
                this.init_list();
        }

        //Initialisation of the listView
        food_listView = (ListView)findViewById(R.id.Plate_list);
        //Listener for the ListView

        food_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detail = null;
                Bundle data = new Bundle();
                data.putString(ELEMENT, list_plate.get(position).toString());
                data.putInt(ID, position);
                data.putString(ACTION, EDIT_ELEMENT);
                if (list_plate.get(position).getClass().equals(Drink.class)) {
                    detail = new Intent(getApplicationContext(), AddDrinkActivity.class);
                } else if (list_plate.get(position).getClass().equals(Plate.class)) {
                    detail = new Intent(getApplicationContext(), AddPlateActivity.class);
                }
                detail.putExtras(data);
                startActivityForResult(detail, EDIT_FOOD);
            }
        });
        food_listView.setAdapter(new FoodAdapter(context, list_plate));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == this.EDIT_FOOD) {
                this.list_plate.set(result.getIntExtra(ID, 0), Food.create(result.getStringExtra(ELEMENT)));
                ((FoodAdapter)this.food_listView.getAdapter()).notifyDataSetChanged();
            } else if(requestCode == this.ADD_FOOD) {
                Food element = Food.create(result.getStringExtra(ELEMENT));
                if (element != null)
                    this.list_plate.add(element);
                ((FoodAdapter)this.food_listView.getAdapter()).notifyDataSetChanged();
            }
        }
    }

    private void init_list() {
        this.list_plate.add(new Plate());
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {

        for (int i = 0; i < this.list_plate.size(); i++) {
            bundle.putString(String.valueOf(i), this.list_plate.get(i).toString());
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle bundle) {

        for (int i = 0; i < bundle.size(); i++) {
            this.list_plate.add(Food.create(bundle.getString(String.valueOf(i))));
        }
    }

    /**
     * Save data (menu) in sharedPreference for permanent storage
     */
    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        for (int i = 0; i < this.list_plate.size(); i++) {
            editor.putString(String.valueOf(i), this.list_plate.get(i).toString());
        }
        editor.putInt(NUMBER_OF_ELEMENTS, this.list_plate.size());
        editor.commit();
    }
}
