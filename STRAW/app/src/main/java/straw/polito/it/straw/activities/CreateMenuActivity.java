package straw.polito.it.straw.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

    private Manager manager;
    private StrawApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        if (toolbar != null) {
            toolbar.setTitle(getString(R.string.Create_Menu));
            setSupportActionBar(toolbar);
        }
        this.application = (StrawApplication)getApplication();

        this.context = this;
        this.context = getApplicationContext();

        if(getIntent().hasExtra(SearchDetailActivity.RESTAURANT)) {
            /**
             * Display menu for customers
             */
            this.manager = new Manager(getIntent().getStringExtra(SearchDetailActivity.RESTAURANT));
            this.add_plate_button = (Button) findViewById(R.id.add_plate_button);
            this.add_drink_button = (Button) findViewById(R.id.add_drink_button);
            this.add_plate_button.setVisibility(View.INVISIBLE);
            this.add_drink_button.setVisibility(View.INVISIBLE);
        } else {
            /**
             * Create menu
             */
            this.manager = ((StrawApplication)getApplication()).getSharedPreferencesHandler().getCurrentManager();
            //TO DO : react if this.manager is null
            //Listener for the "Add plate" button
            this.add_plate_button = (Button) findViewById(R.id.add_plate_button);
            this.add_plate_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), CreatePlateActivity.class);
                    intent.putExtra(ACTION, ADD_ELEMENT);
                    startActivityForResult(intent, ADD_FOOD);
                }
            });

            //Listener for the "Add drink" button
            this.add_drink_button = (Button) findViewById(R.id.add_drink_button);
            this.add_drink_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), CreateDrinkActivity.class);
                    intent.putExtra(ACTION, ADD_ELEMENT);
                    startActivityForResult(intent, ADD_FOOD);
                }
            });
        }
        this.goods = new ArrayList[2];
        this.goods[Menu.PLATES] = new ArrayList<Food>();
        this.goods[Menu.DRINKS] = new ArrayList<Food>();

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
        FoodExpandableAdapterRemove adapter = new FoodExpandableAdapterRemove(context, goods[Menu.PLATES], goods[Menu.DRINKS]);
        adapter.setSyncWithDatabase(true, application);
        food_listView.setAdapter(adapter);
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            this.food_listView.expandGroup(i);
        }

        /**
         * If data has been temporarily stored, onRestoreInstanceState will be called and take care
         * of their restoration.
         */
        if (savedInstanceState == null) {
            /**
             * No data temporarily stored, retrieve the menu from the database.
             */
            this.application.getDatabaseUtils().retrieveMenu(this.manager.getRes_name(), adapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        /**
         * Prepare some useful variables
         */
        DatabaseUtils databaseUtils = this.application.getDatabaseUtils();
        String restaurantName = this.manager.getRes_name();
        /**
         * Actually get the result
         */
        if (resultCode == Activity.RESULT_OK) {
            Food element = Food.create(result.getStringExtra(ELEMENT));

            /**
             * Create an indeterminate ProgressBar dialog to make the user wait
             */
            ProgressDialog dialog = new ProgressDialog(CreateMenuActivity.this);
            dialog.setIndeterminate(true);
            dialog.setMessage(context.getResources().getString(R.string.SavingMenu));
            dialog.setCancelable(false);
            dialog.show();
            /**
             * Store or update the data in the database
             */
            databaseUtils.saveFood(restaurantName, element, dialog);
            /**
             * Update the display
             */
            ((FoodExpandableAdapter)this.food_listView.getExpandableListAdapter()).notifyDataSetChanged();
        }
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
}