package straw.polito.it.straw.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.adapter.FoodExpandableAdapter;
import straw.polito.it.straw.data.Food;
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.data.Menu;


public class DisplayMenuActivity extends AppCompatActivity {

    private ExpandableListView food_listView;
    private ArrayList[] goods;
    private Context context;

    private Manager manager;
    private StrawApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_menu);

        this.application = (StrawApplication)getApplication();

        this.context = this;
        this.context = getApplicationContext();

        this.manager = new Manager(getIntent().getStringExtra(SearchDetailActivity.RESTAURANT));
        this.goods = new ArrayList[2];
        this.goods[Menu.PLATES] = new ArrayList<>();
        this.goods[Menu.DRINKS] = new ArrayList<>();

        //Initialisation of the listView
        food_listView = (ExpandableListView)findViewById(R.id.Plate_list);

        FoodExpandableAdapter adapter = new FoodExpandableAdapter(context, goods[Menu.PLATES], goods[Menu.DRINKS]);
        food_listView.setAdapter(adapter);

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