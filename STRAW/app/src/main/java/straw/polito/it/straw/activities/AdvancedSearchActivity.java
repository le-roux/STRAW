package straw.polito.it.straw.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import straw.polito.it.straw.AddressContainer;
import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.utils.AddressChooserFragment;
import straw.polito.it.straw.utils.Area;
import straw.polito.it.straw.utils.Logger;
import straw.polito.it.straw.utils.SharedPreferencesHandler;

public class AdvancedSearchActivity extends AppCompatActivity implements AddressContainer {

    private RadioGroup radioGroup;
    private EditText address;
    private TextView GPSText;
    private Spinner areaSpinner;
    private Spinner restaurantTypeSpinner;
    private Button searchButton;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private int restaurantType;
    private List<Address> addressList;
    private double latitude;
    private double longitude;

    private static final int GPS = R.id.current;
    private static final int ADDRESS = R.id.address;
    private static final int AREA = R.id.area;

    private static final String RADIOINDEX = "radioindex";
    private static final String AREAINDEX = "areaindex";
    private static final String ADDRESSNAME = "address";
    private static final String RESTAURANTTYPE = "restaurantType";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("ADVANCED SEARCH");
        setSupportActionBar(toolbar);

        this.locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        /**
         * Initialize the different views.
         */
        this.radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        this.address = (EditText)findViewById(R.id.addressText);
        this.GPSText = (TextView)findViewById(R.id.GPSText);
        this.areaSpinner = (Spinner)findViewById(R.id.areaSpinner);
        this.restaurantTypeSpinner = (Spinner)findViewById(R.id.spinner);
        this.searchButton = (Button)findViewById(R.id.search_button);

        prepareSpinners();

        /**
         * Display the proper field according to what type of location is selected.
         */
        this.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case (GPS): {
                        address.setVisibility(View.INVISIBLE);
                        areaSpinner.setVisibility(View.INVISIBLE);
                        GPSText.setVisibility(View.VISIBLE);
                        break;
                    }
                    case (ADDRESS): {
                        GPSText.setVisibility(View.INVISIBLE);
                        areaSpinner.setVisibility(View.INVISIBLE);
                        address.setVisibility(View.VISIBLE);
                        break;
                    }
                    case (AREA): {
                        GPSText.setVisibility(View.INVISIBLE);
                        address.setVisibility(View.INVISIBLE);
                        areaSpinner.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
        });

        /**
         * Ask the user to select the proper address (if needed) and start the transition toward the
         * next activity (which displays the results of the search).
         */
        this.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int placeType = radioGroup.getCheckedRadioButtonId();
                if (placeType == ADDRESS) {
                    if (address.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), R.string.InvalidAddress, Toast.LENGTH_LONG).show();
                    } else
                        addressList = AddressChooserFragment.showAddressChooser(AdvancedSearchActivity.this, address.getText().toString());
                }
                else
                    search();
            }
        });

        /**
         * Set a listener that will acquire the current location once.
         */
        this.locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                try {
                    locationManager.removeUpdates(locationListener);
                } catch (SecurityException e) {
                    Logger.d("Location rights have been removed");
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        /**
         * Acquire the current location.
         */
        try {
            this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            this.GPSText.setText(R.string.OK);
        } catch (SecurityException e) {
            this.GPSText.setText(R.string.LocationRefused);
        }
    }

    /**
     * Called by the AddressChooserFragment when selecting an address.
     * Get the proper coordinates and call search().
     * @param i : the index of the address selected
     */
    @Override
    public void setAddressNumber(int i) {
        Address address = this.addressList.get(i);
        this.latitude = address.getLatitude();
        this.longitude = address.getLongitude();
        search();
    }

    /**
     * Launch the activity that displays the results and pass to it the info selected by the
     * customer.
     */
    public void search() {
        Intent intent = new Intent(getApplicationContext(), QuickSearchActivity.class);
        intent.putExtra(Manager.LATITUDE, latitude);
        intent.putExtra(Manager.LONGITUDE, longitude);
        intent.putExtra(Manager.TYPE, restaurantType);
        startActivity(intent);
    }

    /**
     * Set the content and listeners on the different spinners.
     */
    public void prepareSpinners() {
        StrawApplication application = (StrawApplication)getApplication();
        SharedPreferencesHandler sharedPreferencesHandler = application.getSharedPreferencesHandler();
        final Area[] areas = sharedPreferencesHandler.getAreaList();
        this.areaSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, areas));
        this.areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                latitude = areas[position].getLatitude();
                longitude = areas[position].getLongitude();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.restaurantTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                restaurantType = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(RADIOINDEX, this.radioGroup.getCheckedRadioButtonId());
        outState.putInt(AREAINDEX, this.areaSpinner.getSelectedItemPosition());
        outState.putString(ADDRESSNAME, this.address.getText().toString());
        outState.putInt(RESTAURANTTYPE, this.restaurantType);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        this.radioGroup.check(savedInstanceState.getInt(RADIOINDEX));
        this.areaSpinner.setSelection(savedInstanceState.getInt(AREAINDEX));
        this.address.setText(savedInstanceState.getString(ADDRESSNAME));
        this.restaurantTypeSpinner.setSelection(savedInstanceState.getInt(RESTAURANTTYPE));
    }
}
