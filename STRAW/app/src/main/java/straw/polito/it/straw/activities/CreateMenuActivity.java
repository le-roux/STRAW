package straw.polito.it.straw.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.adapter.FoodAdapter;
import straw.polito.it.straw.adapter.FoodAdapterRemove;
import straw.polito.it.straw.data.Drink;
import straw.polito.it.straw.data.Food;
import straw.polito.it.straw.data.Plate;


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
    public static final String MENU = "Menu";

    private ExpandableListView food_listView;
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
                Intent intent = new Intent(getApplicationContext(), CreatePlateActivity.class);
                intent.putExtra(ACTION, ADD_ELEMENT);
                startActivityForResult(intent, ADD_FOOD);
            }
        });

        //Listener for the "Add drink" button
        this.add_drink_button = (Button)findViewById(R.id.add_drink_button);
        this.add_drink_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateDrinkActivity.class);
                intent.putExtra(ACTION, ADD_ELEMENT);
                startActivityForResult(intent, ADD_FOOD);
            }
        });

        this.list_plate = new ArrayList<Food>();
        if (savedInstanceState == null) {
            //No data temporarily stored
            //Get number of elements stored in sharedPreference
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(this.sharedPreferences.getString(MENU, ""));
            } catch (JSONException e) {
                e.printStackTrace();
                jsonArray = new JSONArray();
            }
            //Retrieve element(s) from sharedPreference
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    this.list_plate.add(Food.create(jsonArray.get(i).toString()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (jsonArray.length() == 0)
                //No data found in the sharedPreference --> default init
                //TO DO : change default init for final version
                this.init_list();
        }

        //Initialisation of the listView
        food_listView = (ExpandableListView)findViewById(R.id.Plate_list);
        //Listener for the ListView

        food_listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
                Intent detail = null;
                Bundle data = new Bundle();
                data.putString(ELEMENT, list_plate.get(childPosition).toString());
                data.putInt(ID, childPosition);
                data.putString(ACTION, EDIT_ELEMENT);
                if (list_plate.get(childPosition).getClass().equals(Drink.class)) {
                    detail = new Intent(getApplicationContext(), CreateDrinkActivity.class);
                } else if (list_plate.get(childPosition).getClass().equals(Plate.class)) {
                    detail = new Intent(getApplicationContext(), CreatePlateActivity.class);
                }
                detail.putExtras(data);
                startActivityForResult(detail, EDIT_FOOD);
                return true;
            }
        });
        food_listView.setAdapter((ExpandableListAdapter)new FoodAdapterRemove(context, list_plate));
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
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < this.list_plate.size(); i++) {
            try {
                jsonArray.put(i, this.list_plate.get(i).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        editor.putString(MENU, jsonArray.toString());
        editor.commit();
    }
}
