package straw.polito.it.straw.activities;

import android.app.AlertDialog;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.Collections;

import straw.polito.it.straw.AdapterFragment;
import straw.polito.it.straw.R;
import straw.polito.it.straw.RestaurantFilter;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.adapter.RestaurantListAdapter;
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.data.User;
import straw.polito.it.straw.fragments.RestaurantMapFragment;
import straw.polito.it.straw.fragments.RestaurantsListFragment;
import straw.polito.it.straw.utils.Area;


public class QuickSearchActivity extends FragmentActivity implements RestaurantFilter{

    private ArrayList<Manager> restaurant_list;
    private ArrayList<Manager> restaurant_list_tmp;
    private Spinner spinner1;
    private Spinner spinner2;
    private StrawApplication application;
    private Button filtersButton;
    public String FoodFilter;
    public String PlaceFilter;
    private double latitude;
    private double longitude;
    private int restaurantType;
    private AdapterFragment fragment;
    private FragmentManager fragmentManager;
    private RestaurantListAdapter adapter;
    private int currentFragment;
    private Button fragmentButton;
    private ImageView fragmentIcon;

    private static final int LIST = 0;
    private static final int MAP = 1;

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
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(this.getString(R.string.RetrievingRestaurants));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        this.adapter = new RestaurantListAdapter(getApplicationContext(), restaurant_list);
        RestaurantListAdapter adapterForFilter = new RestaurantListAdapter(getApplicationContext(), restaurant_list_tmp);
        this.application.getDatabaseUtils().retrieveRestaurantList(this.adapter, adapterForFilter, dialog, this);

        addListenerOnButton();
        addListenerOnSpinnerItemSelection();

        filtersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        this.fragmentButton = (Button)findViewById(R.id.mapButton);
        this.fragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFragment();
            }
        });
        this.fragmentIcon = (ImageView)findViewById(R.id.mapIcon);

        RelativeLayout fragmentCard = (RelativeLayout) findViewById(R.id.mapCard);
        fragmentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFragment();
            }
        });

        /**
         * Prepare the fragment to display
         */
        this.fragmentManager = this.getSupportFragmentManager();
        this.currentFragment = MAP;
        toggleFragment();
    }

    private void setSupportActionBar(Toolbar toolbar) {
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
        spinner2.setSelection(this.restaurantType);
        switch(this.restaurantType) {
            case(1): {
                PlaceFilter = "Restaurant";
                break;
            }
            case (2): {
                PlaceFilter = "Canteen";
                break;
            }
            case (3): {
                PlaceFilter = "Take Away";
                break;
            }
            case (4): {
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
        fragment.notifyDataSetChanged();
    }
    public void SortByLocation(AdapterView<?> parent, View view,long id){
        Toast.makeText(parent.getContext(),
                "List of restaurant sort by location",
                Toast.LENGTH_SHORT).show();

        Collections.sort(restaurant_list,Manager.getDistanceComparator(latitude, longitude));
        fragment.notifyDataSetChanged();
    }
    public void SortByPrice(AdapterView<?> parent, View view,long id){
        Toast.makeText(parent.getContext(),
                "List of restaurant sort by price",
                Toast.LENGTH_SHORT).show();
        Collections.sort(restaurant_list, Manager.PriceComparator);
        fragment.notifyDataSetChanged();
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
                } else if (options[item].equals("Pizzeria")) {
                    FoodFilter = "Pizzeria";
                    TextView t1 = (TextView) findViewById(R.id.typeOfFood);
                    t1.setText("Pizzeria");
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
        fragment.notifyDataSetChanged();
    }

    public void Foodfilter(){
        for (int a = 0; a < restaurant_list_tmp.size(); a++) {
            if(restaurant_list_tmp.get(a).getFood_type().equals(FoodFilter) || FoodFilter.equals("")){
                restaurant_list.add(restaurant_list_tmp.get(a));
            }
        }
        fragment.notifyDataSetChanged();
    }

    public void Placefilter(){
        for (int a = 0; a < restaurant_list_tmp.size(); a++) {
            if(restaurant_list_tmp.get(a).getRes_type().equals(PlaceFilter) || PlaceFilter.equals("")){
                restaurant_list.add(restaurant_list_tmp.get(a));
            }
        }
        fragment.notifyDataSetChanged();
    }

    /**
     * Change the currently displayed fragment to a RestaurantListFragment
     * or to a RestaurantMapFragment
     */
    public void toggleFragment() {
        FragmentTransaction transaction = this.fragmentManager.beginTransaction();
        if (this.currentFragment == MAP) {
            this.fragment = new RestaurantsListFragment();
            transaction.replace(R.id.id_relativeLayoutQuickSearch2, (Fragment)this.fragment);
            this.currentFragment = LIST;
            this.fragmentButton.setText(R.string.map);
            this.fragmentIcon.setImageURI(Uri.parse("android.resource://straw.polito.it.straw/drawable/map"));
        } else {
            this.fragment = RestaurantMapFragment.createInstance(this.latitude, this.longitude, this);
            transaction.replace(R.id.id_relativeLayoutQuickSearch2, ((RestaurantMapFragment)this.fragment).getFragment());
            this.currentFragment = MAP;
            this.fragmentButton.setText(R.string.list);
            this.fragmentIcon.setImageURI(Uri.parse("android.resource://straw.polito.it.straw/drawable/list"));
            //Created by Viktor Vorobyev for Noun Project
        }
        this.fragment.setAdapter(this.adapter);
        transaction.commit();
    }

}

