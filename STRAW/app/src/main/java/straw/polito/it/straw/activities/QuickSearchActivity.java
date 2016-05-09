package straw.polito.it.straw.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import straw.polito.it.straw.R;
import straw.polito.it.straw.adapter.RestaurantListAdapter;
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.utils.CustomOnItemSelectedListener;
import straw.polito.it.straw.utils.Logger;
import straw.polito.it.straw.utils.DatabaseUtils;
import straw.polito.it.straw.utils.SharedPreferencesHandler;

public class QuickSearchActivity extends AppCompatActivity {

    private ArrayList<Manager> restaurant_list;
    private Spinner spinner1;
    private ListView restaurant_listView;
    SharedPreferences mShared;
    private StrawApplication application;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_search);
        restaurant_list = new ArrayList<>();
        init_list();
        mShared= PreferenceManager.getDefaultSharedPreferences(this);
        String tmp = "";
        if(mShared.contains("ManagerList")){
            try{
            String ss = mShared.getString("ManagerList", "Error");
            JSONArray jarr = new JSONArray(ss);
                for (int i = 0; i < jarr.length(); i++) {
                    try {
                        tmp = jarr.get(i).toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    restaurant_list.add(new Manager(tmp));
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
        /*for testing*/
        restaurant_list.add(new Manager());
        restaurant_list.add(new Manager());
        restaurant_list.add(new Manager());
        restaurant_list.add(new Manager());
        restaurant_list.add(new Manager());
        restaurant_list.add(new Manager());

        restaurant_listView.setAdapter(new RestaurantListAdapter(getBaseContext(),restaurant_list));
        this.restaurant_listView = (ListView) findViewById(R.id.restaurant_list);
        addListenerOnButton();
        addListenerOnSpinnerItemSelection();

        restaurant_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getBaseContext(), SearchDetailActivity.class);
                i.putExtra("res", restaurant_list.get(position).toJSONObject());
                startActivity(i);
            }
        });
    }
    public RestaurantListAdapter getAdapter() {
        return (RestaurantListAdapter)this.restaurant_listView.getAdapter();
    }
    private void init_list() {
        this.restaurant_listView = (ListView) findViewById(R.id.restaurant_list);
    }
    public void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }
    public void addListenerOnButton() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
    }



}
