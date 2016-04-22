package straw.polito.it.straw.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.adapter.FoodExpandableAdapter;
import straw.polito.it.straw.adapter.FoodExpandableAdapterRemove;
import straw.polito.it.straw.data.Drink;
import straw.polito.it.straw.data.Food;
import straw.polito.it.straw.data.Plate;


public class CreateMenuActivity extends AppCompatActivity {

    //Request code for the intents
    private static final int EDIT_FOOD = 1;
    private static final int ADD_FOOD = 2;

    //Index in the goods array
    private static final int PLATES = 0;
    private static final int DRINKS = 1;

    //Keys for storing and retrieving the data in bundles or sharedPreference
    public static final String ELEMENT = "it.polito.straw.Element";
    public static final String ID = "it.polito.straw.Id";
    public static final String ACTION = "it.polito.straw.Action";
    public static final String ADD_ELEMENT = "it.polito.straw.Add";
    public static final String EDIT_ELEMENT = "it.polito.straw.Edit";
    public static final String TYPE = "it.polito.straw.Type";
    public static final String MENU = "Menu";

    private ExpandableListView food_listView;
    private ArrayList[] goods;
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

        this.goods = new ArrayList[2];
        this.goods[PLATES] = new ArrayList<Food>();
        this.goods[DRINKS] = new ArrayList<Food>();

        if (savedInstanceState == null) {
            //No data temporarily stored
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(this.sharedPreferences.getString(MENU, ""));
            } catch (JSONException e) {
                e.printStackTrace();
                jsonArray = new JSONArray();
            }
            //Retrieve element(s) from the jsonArray
            restoreData(jsonArray);
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
                data.putString(ELEMENT, goods[groupPosition].get(childPosition).toString());
                data.putInt(ID, childPosition);
                data.putString(ACTION, EDIT_ELEMENT);
                if (groupPosition == DRINKS) {
                    data.putInt(TYPE, DRINKS);
                    detail = new Intent(getApplicationContext(), CreateDrinkActivity.class);
                } else if (groupPosition == PLATES) {
                    data.putInt(TYPE, PLATES);
                    detail = new Intent(getApplicationContext(), CreatePlateActivity.class);
                }
                detail.putExtras(data);
                startActivityForResult(detail, EDIT_FOOD);
                return true;
            }
        });
        food_listView.setAdapter(new FoodExpandableAdapterRemove(context, goods[PLATES], goods[DRINKS]));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            int type = result.getIntExtra(TYPE, PLATES);
            if (requestCode == this.EDIT_FOOD) {
                this.goods[type].set(result.getIntExtra(ID, 0), Food.create(result.getStringExtra(ELEMENT)));
                ((FoodExpandableAdapter)this.food_listView.getAdapter()).notifyDataSetChanged();
            } else if(requestCode == this.ADD_FOOD) {
                Food element = Food.create(result.getStringExtra(ELEMENT));
                if (element != null)
                    this.goods[type].add(element);
                ((FoodExpandableAdapter)this.food_listView.getAdapter()).notifyDataSetChanged();
            }
        }
    }

    private void init_list() {
        this.goods[PLATES].add(new Plate());
        this.goods[DRINKS].add(new Drink());
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        JSONArray jsonArray = saveData();
        bundle.putString(MENU, jsonArray.toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle bundle) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(bundle.getString(MENU, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonArray != null)
            restoreData(jsonArray);
    }

    /**
     * Save data (menu) in sharedPreference for permanent storage
     */
    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        JSONArray jsonArray = saveData();
        editor.putString(MENU, jsonArray.toString());
        editor.commit();
    }

    private void restoreData(JSONArray jsonArray) {
        try {
            int index = 0;
            //Get plates number
            int platesNumber = jsonArray.getInt(index++);
            //Get plates
            for (int i = 0; i < platesNumber; i++) {
                this.goods[PLATES].add(Food.create(jsonArray.get(index++).toString()));
            }
            //Get drinks number
            int drinksNumber = jsonArray.getInt(index++);
            //Get drinks
            for (int i = 0; i < drinksNumber; i++) {
                this.goods[DRINKS].add(Food.create(jsonArray.get(index++).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONArray saveData() {
        JSONArray jsonArray = new JSONArray();
        int index = 0;
        try {
            jsonArray.put(index, this.goods[PLATES].size());

            index++;
            for (int i = 0; i < this.goods[PLATES].size(); i++) {
                jsonArray.put(index, this.goods[PLATES].get(i).toString());
                index++;
            }
            jsonArray.put(index, this.goods[DRINKS].size());
            for (int i = 0; i < this.goods[DRINKS].size(); i++) {
                jsonArray.put(index, this.goods[DRINKS].get(i).toString());
                index++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }
}
