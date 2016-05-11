package straw.polito.it.straw.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import straw.polito.it.straw.R;
import straw.polito.it.straw.adapter.RestaurantListAdapter;
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.StrawApplication;

import straw.polito.it.straw.data.User;
import straw.polito.it.straw.utils.Logger;
import straw.polito.it.straw.utils.DatabaseUtils;
import straw.polito.it.straw.utils.SharedPreferencesHandler;
import straw.polito.it.straw.utils.Area;
import android.widget.AdapterView.OnItemSelectedListener;


public class QuickSearchActivity extends AppCompatActivity{

    private ArrayList<Manager> restaurant_list;
    private ArrayList<Manager> restaurant_list_tmp;
    private Spinner spinner1;
    private Spinner spinner2;
    private ListView restaurant_listView;
    SharedPreferences mShared;
    private StrawApplication application;
    private SharedPreferences sharedPreferences;
    private Button filtersButton;
    public String FoodFilter;
    public String PlaceFilter;
    private double latitude;
    private double longitude;
    private int restaurantType;
    //private TextView t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_search);
        this.application = (StrawApplication)getApplication();

        Intent intent = getIntent();
        if (intent.hasExtra(Manager.LATITUDE)) {
            /**
             * Retrieve the info provided by AdvancedSearch activity
             */
            this.latitude = intent.getDoubleExtra(Manager.LATITUDE, 0);
            this.longitude = intent.getDoubleExtra(Manager.LONGITUDE, 0);
            this.restaurantType = intent.getIntExtra(Manager.TYPE, 0);
        } else {
            /**
             * Retrieve the info associated with the user profile
             */
            User user = this.application.getSharedPreferencesHandler().getCurrentUser();
            Area[] areas = this.application.getSharedPreferencesHandler().getAreaList();
            for (Area area : areas) {
                if (area.getName().equals(user.getUniversity())) {
                    this.latitude = area.getLatitude();
                    this.longitude = area.getLongitude();
                    break;
                }
            }
        }

        this.filtersButton = (Button) findViewById(R.id.addfilter);
        restaurant_list = new ArrayList<>();
        restaurant_list_tmp = new ArrayList<>();
        this.FoodFilter = "";
        this.PlaceFilter = "";
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
        restaurant_listView.setAdapter(new RestaurantListAdapter(getBaseContext(), restaurant_list));
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
        filtersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
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
        spinner2 = (Spinner) findViewById(R.id.spinner2);

        spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    SortByRating(parent, view, id);
                } else if (position == 1) {
                    SortByLocation(parent, view, id);
                } else if (position == 2) {
                    SortByPrice(parent, view, id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner2.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    PlaceFilter = "";
                    filter(FoodFilter, PlaceFilter);
                } else if (position == 1) {
                    PlaceFilter = "Restaurant";
                    filter(FoodFilter, PlaceFilter);
                } else if (position == 2) {
                    PlaceFilter = "Canteen";
                    filter(FoodFilter, PlaceFilter);
                } else if (position == 3) {
                    PlaceFilter = "Take Away";
                    filter(FoodFilter, PlaceFilter);
                } else if (position == 4) {
                    PlaceFilter = "Bar";
                    filter(FoodFilter,PlaceFilter);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    public void SortByRating(AdapterView<?> parent, View view,long id){
        Toast.makeText(parent.getContext(),
                "List of restaurant sort by rating",
                Toast.LENGTH_SHORT).show();
    }
    public void SortByLocation(AdapterView<?> parent, View view,long id){
        Toast.makeText(parent.getContext(),
                "List of restaurant sort by location",
                Toast.LENGTH_SHORT).show();

        Collections.sort(restaurant_list,Manager.getDistanceComparator(latitude, longitude));
        ((RestaurantListAdapter) restaurant_listView.getAdapter()).notifyDataSetChanged();
    }
    public void SortByPrice(AdapterView<?> parent, View view,long id){
        Toast.makeText(parent.getContext(),
                "List of restaurant sort by price",
                Toast.LENGTH_SHORT).show();
        Collections.sort(restaurant_list, Manager.PriceComparator);
        ((RestaurantListAdapter) restaurant_listView.getAdapter()).notifyDataSetChanged();
    }

    public void addListenerOnButton() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
    }


    private void selectImage() {
        final CharSequence[] options = {"No filter",getString(R.string.pizzeria), getString(R.string.italian),getString(R.string.jap),getString(R.string.kebap),getString(R.string.chinese),getString(R.string.other), "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(QuickSearchActivity.this);
        builder.setTitle("Choose a filter");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("No filter")) {
                    FoodFilter = "";
                    TextView t1 = (TextView) findViewById(R.id.typeOfFood);
                    t1.setText("Every type");
                    filter(FoodFilter, PlaceFilter);
                } else if (options[item].equals("Pizzeria")) {
                    FoodFilter = "Pizzeria";
                    TextView t1 = (TextView) findViewById(R.id.typeOfFood);
                    t1.setText("Pizzeria");
                    filter(FoodFilter, PlaceFilter);
                } else if (options[item].equals("Italian")) {
                    FoodFilter = "Italian";
                    TextView t1 = (TextView) findViewById(R.id.typeOfFood);
                    t1.setText(FoodFilter);
                    filter(FoodFilter, PlaceFilter);
                } else if (options[item].equals("Japanese")) {
                    FoodFilter = "Japanese";
                    TextView t1 = (TextView) findViewById(R.id.typeOfFood);
                    t1.setText("Japanese");
                    filter(FoodFilter, PlaceFilter);
                } else if (options[item].equals("Kebap")) {
                    FoodFilter = "Kebap";
                    TextView t1 = (TextView) findViewById(R.id.typeOfFood);
                    t1.setText("Kebap");
                    filter(FoodFilter, PlaceFilter);
                } else if (options[item].equals("Chinese")) {
                    FoodFilter = "Chinese";
                    TextView t1 = (TextView) findViewById(R.id.typeOfFood);
                    t1.setText("Chinese");
                    filter(FoodFilter, PlaceFilter);
                } else if (options[item].equals("Other")) {
                    FoodFilter = "Other";
                    TextView t1 = (TextView) findViewById(R.id.typeOfFood);
                    t1.setText("Other");
                    filter(FoodFilter, PlaceFilter);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void filter(String foodfilter, String placefilter){
        restaurant_list.addAll(restaurant_list_tmp);
        restaurant_list_tmp.clear();
        for (int a = 0; a < restaurant_list.size(); a++) {
            if(foodfilter.equals("") && placefilter.equals("")){
            }
            else if(foodfilter.equals("")){
                if(!restaurant_list.get(a).getRes_type().equals(placefilter)){
                    restaurant_list_tmp.add(restaurant_list.get(a));
                    restaurant_list.remove(a);
                }
            }
            else if(placefilter.equals("")){
                if(!restaurant_list.get(a).getFood_type().equals(foodfilter)){
                    restaurant_list_tmp.add(restaurant_list.get(a));
                    restaurant_list.remove(a);
                }
            }
            else{
                if(restaurant_list.get(a).getRes_type().equals(placefilter) && restaurant_list.get(a).getFood_type().equals(foodfilter)){
                }
                else{
                    restaurant_list_tmp.add(restaurant_list.get(a));
                    restaurant_list.remove(a);
                }
            }
        }
        ((RestaurantListAdapter) restaurant_listView.getAdapter()).notifyDataSetChanged();
    }





}














