package straw.polito.it.straw.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import straw.polito.it.straw.R;
import straw.polito.it.straw.RestaurantFilter;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.adapter.RestaurantListAdapter;
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.data.User;
import straw.polito.it.straw.utils.Area;


public class QuickSearchActivity extends AppCompatActivity implements RestaurantFilter{

    private ArrayList<Manager> restaurant_list;
    private ArrayList<Manager> restaurant_list_tmp;
    private Spinner spinner1;
    private Spinner spinner2;
    private ListView restaurant_listView;
    private StrawApplication application;
    private Button filtersButton;
    public String FoodFilter;
    public String PlaceFilter;
    private double latitude;
    private double longitude;
    private int restaurantType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("QUICK  SEARCH");
        setSupportActionBar(toolbar);
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
                    this.restaurantType = -1;
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
        restaurant_list = new ArrayList<>();
        restaurant_list_tmp = new ArrayList<>();
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(this.getString(R.string.RetrievingRestaurants));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        RestaurantListAdapter adapter = new RestaurantListAdapter(getApplicationContext(), restaurant_list);
        RestaurantListAdapter adapter2 = new RestaurantListAdapter(getApplicationContext(), restaurant_list_tmp);
        this.application.getDatabaseUtils().retrieveRestaurantList(adapter, dialog, this);
        this.application.getDatabaseUtils().retrieveRestaurantList(adapter2, dialog, this);
        restaurant_listView.setAdapter(adapter);
        this.restaurant_listView = (ListView) findViewById(R.id.restaurant_list);

        addListenerOnButton();
        addListenerOnSpinnerItemSelection();

        restaurant_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getBaseContext(), SearchDetailActivity.class);
                i.putExtra(SearchDetailActivity.RESTAURANT, restaurant_list.get(position).toJSONObject());
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
                    filter();
                } else if (position == 1) {
                    PlaceFilter = "Restaurant";
                    filter();
                } else if (position == 2) {
                    PlaceFilter = "Canteen";
                    filter();
                } else if (position == 3) {
                    PlaceFilter = "Take Away";
                    filter();
                } else if (position == 4) {
                    PlaceFilter = "Bar";
                    filter();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /**
         * Initial value
         */
        spinner2.setSelection(this.restaurantType + 1);
        switch(this.restaurantType) {
            case(0): {
                PlaceFilter = "Restaurant";
                break;
            }
            case (1): {
                PlaceFilter = "Canteen";
                break;
            }
            case (2): {
                PlaceFilter = "Take Away";
                break;
            }
            case (3): {
                PlaceFilter = "Bar";
                break;
            } default: {
                PlaceFilter = "";
                break;
            }
        }

    }
    public void SortByRating(AdapterView<?> parent, View view,long id){
        Toast.makeText(parent.getContext(),
                "List of restaurant sort by rating",
                Toast.LENGTH_SHORT).show();
        Collections.sort(restaurant_list, Manager.RatingComparator);
        ((RestaurantListAdapter) restaurant_listView.getAdapter()).notifyDataSetChanged();
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
                    filter();
                    ((RestaurantListAdapter) restaurant_listView.getAdapter()).notifyDataSetChanged();
                } else if (options[item].equals("Pizzeria")) {
                    FoodFilter = "Pizzeria";
                    TextView t1 = (TextView) findViewById(R.id.typeOfFood);
                    t1.setText("Pizzeria");
                    Foodfilter();
                    filter();
                } else if (options[item].equals("Italian")) {
                    FoodFilter = "Italian";
                    TextView t1 = (TextView) findViewById(R.id.typeOfFood);
                    t1.setText(FoodFilter);
                    filter();
                } else if (options[item].equals("Japanese")) {
                    FoodFilter = "Japanese";
                    TextView t1 = (TextView) findViewById(R.id.typeOfFood);
                    t1.setText("Japanese");
                    filter();
                } else if (options[item].equals("Kebap")) {
                    FoodFilter = "Kebap";
                    TextView t1 = (TextView) findViewById(R.id.typeOfFood);
                    t1.setText("Kebap");
                    filter();
                } else if (options[item].equals("Chinese")) {
                    FoodFilter = "Chinese";
                    TextView t1 = (TextView) findViewById(R.id.typeOfFood);
                    t1.setText("Chinese");
                    filter();
                } else if (options[item].equals("Other")) {
                    FoodFilter = "Other";
                    TextView t1 = (TextView) findViewById(R.id.typeOfFood);
                    t1.setText("Other");
                    filter();
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void filter() {
        restaurant_list.clear();
        if(FoodFilter.equals("") && PlaceFilter.equals("")){
            restaurant_list.addAll(restaurant_list_tmp);
        }
        else if(FoodFilter.equals("")){
            Placefilter();
        }
        else if(PlaceFilter.equals("")){
            Foodfilter();
        }
        else{
            for (int a = 0; a < restaurant_list_tmp.size(); a++) {
                if(restaurant_list_tmp.get(a).getRes_type().equals(PlaceFilter) && restaurant_list_tmp.get(a).getFood_type().equals(FoodFilter)){
                    restaurant_list.add(restaurant_list_tmp.get(a));
                }
            }
        }
        ((RestaurantListAdapter) restaurant_listView.getAdapter()).notifyDataSetChanged();
    }

    public void Foodfilter(){
        restaurant_list.clear();
        for (int a = 0; a < restaurant_list_tmp.size(); a++) {
            if(restaurant_list_tmp.get(a).getFood_type().equals(FoodFilter) || FoodFilter.equals("")){
                restaurant_list.add(restaurant_list_tmp.get(a));
            }
        }
        ((RestaurantListAdapter) restaurant_listView.getAdapter()).notifyDataSetChanged();
    }

    public void Placefilter(){
        restaurant_list.clear();
        for (int a = 0; a < restaurant_list_tmp.size(); a++) {
            if(restaurant_list_tmp.get(a).getRes_type().equals(PlaceFilter) || PlaceFilter.equals("")){
                restaurant_list.add(restaurant_list_tmp.get(a));
            }
        }
        ((RestaurantListAdapter) restaurant_listView.getAdapter()).notifyDataSetChanged();
    }
}

