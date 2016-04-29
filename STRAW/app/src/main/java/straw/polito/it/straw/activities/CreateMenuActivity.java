package straw.polito.it.straw.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.adapter.FoodExpandableAdapter;
import straw.polito.it.straw.adapter.FoodExpandableAdapterRemove;
import straw.polito.it.straw.data.Drink;
import straw.polito.it.straw.data.Food;
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.data.Menu;
import straw.polito.it.straw.data.Plate;
import straw.polito.it.straw.utils.DatabaseUtils;


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
    public static final String TYPE = "it.polito.straw.Type";

    private ExpandableListView food_listView;
    private ArrayList[] goods;
    private Context context;
    private Button add_plate_button;
    private Button add_drink_button;

    private SharedPreferences sharedPreferences;
    private Manager manager;
    private StrawApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_menu);

        this.application = (StrawApplication)getApplication();

        this.context = this;
        this.context = getApplicationContext();
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.manager = ((StrawApplication)getApplication()).getSharedPreferencesHandler().getCurrentManager();
        //TO DO : react if this.manager is null

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
        this.goods[Menu.PLATES] = new ArrayList<Food>();
        this.goods[Menu.DRINKS] = new ArrayList<Food>();

        if (savedInstanceState == null) {
            //No data temporarily stored
            //Try to retrieve it from the database
            this.application.getDatabaseUtils().retrieveMenu(this.application.getSharedPreferencesHandler().getCurrentManager().getRes_name(), this.goods);
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
                if (groupPosition == Menu.DRINKS) {
                    detail = new Intent(getApplicationContext(), CreateDrinkActivity.class);
                } else if (groupPosition == Menu.PLATES) {
                    detail = new Intent(getApplicationContext(), CreatePlateActivity.class);
                }
                detail.putExtras(data);
                startActivityForResult(detail, EDIT_FOOD);
                return true;
            }
        });
        food_listView.setAdapter(new FoodExpandableAdapterRemove(context, goods[Menu.PLATES], goods[Menu.DRINKS]));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            int type = result.getIntExtra(TYPE, Menu.PLATES);
            if (requestCode == CreateMenuActivity.EDIT_FOOD) {
                this.goods[type].set(result.getIntExtra(ID, 0), Food.create(result.getStringExtra(ELEMENT)));
            } else if(requestCode == CreateMenuActivity.ADD_FOOD) {
                Food element = Food.create(result.getStringExtra(ELEMENT));
                if (element != null)
                    this.goods[type].add(element);
            }
            ((FoodExpandableAdapter)this.food_listView.getExpandableListAdapter()).notifyDataSetChanged();
        }
        /**
         * Store the new data in the database
         */
        this.application.getDatabaseUtils().saveMenu(this.application.getSharedPreferencesHandler().getCurrentManager().getRes_name(), Menu.saveMenu(this.goods).toString());
    }

    private void init_list() {
        this.goods[Menu.PLATES].add(new Plate());
        this.goods[Menu.DRINKS].add(new Drink());
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        JSONArray jsonArray = Menu.saveMenu(this.goods);
        bundle.putString(Menu.MENU, jsonArray.toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle bundle) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(bundle.getString(Menu.MENU, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonArray != null)
            Menu.restoreMenu(jsonArray, this.goods);
    }

    /**
     * Save data (menu) for permanent storage (in the Firebase database if possible,
     * in sharedPreferences otherwise.
     */
    @Override
    public void onStop() {
        super.onStop();
        DatabaseUtils utils = ((StrawApplication)getApplication()).getDatabaseUtils();
        boolean saved = utils.saveMenu(this.manager.getRes_name(), Menu.saveMenu(this.goods).toString());
        //If it's impossible to save the data in the database, temporarily store them locally
        if (!saved)
            Menu.saveMenuInSharedPreferences(this.context, this.goods);
        //TO DO : retry later to send the data to the database
    }
}