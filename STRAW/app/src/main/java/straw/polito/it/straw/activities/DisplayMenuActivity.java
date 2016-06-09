package straw.polito.it.straw.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ExpandableListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.adapter.FoodExpandableAdapter;
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.data.Menu;
import straw.polito.it.straw.utils.Logger;


public class DisplayMenuActivity extends AppCompatActivity {

    private ArrayList[] goods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("DISPLAY  MENU");
        setSupportActionBar(toolbar);
        this.goods = new ArrayList[2];
        this.goods[Menu.PLATES] = new ArrayList<>();
        this.goods[Menu.DRINKS] = new ArrayList<>();

        // Initialisation of the listView
        ExpandableListView food_listView = (ExpandableListView)findViewById(R.id.Plate_list);
        if (food_listView == null) {
            Logger.d("Impossible to find the ExpandableListView in DisplayMenuActivity");
            finish();
            return;
        }

        // Initialisation of the adapter
        FoodExpandableAdapter adapter = new FoodExpandableAdapter(getApplicationContext(),
                goods[Menu.PLATES], goods[Menu.DRINKS]);
        food_listView.setAdapter(adapter);

        // Expand the groups
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            food_listView.expandGroup(i);
        }

        /* If data has been temporarily stored, onRestoreInstanceState will be called and take care
           of their restoration */
        if (savedInstanceState == null) {
            // No data temporarily stored, retrieve the menu from the database
            StrawApplication application = (StrawApplication)getApplication();
            Manager manager = new Manager(getIntent().getStringExtra(SearchDetailActivity.RESTAURANT));
            application.getDatabaseUtils().retrieveMenu(manager.getRes_name(), adapter);
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