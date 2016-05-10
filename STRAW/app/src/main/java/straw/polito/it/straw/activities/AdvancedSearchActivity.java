package straw.polito.it.straw.activities;

import android.content.Intent;
import android.location.Address;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

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

    private int restaurantType;
    private List<Address> addressList;
    private double latitude;
    private double longitude;

    private static final int GPS = R.id.current;
    private static final int ADDRESS = R.id.address;
    private static final int AREA = R.id.area;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search);

        this.radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        this.address = (EditText)findViewById(R.id.addressText);
        this.GPSText = (TextView)findViewById(R.id.GPSText);
        this.areaSpinner = (Spinner)findViewById(R.id.areaSpinner);
        this.restaurantTypeSpinner = (Spinner)findViewById(R.id.spinner);
        this.searchButton = (Button)findViewById(R.id.search_button);

        StrawApplication application = (StrawApplication)getApplication();
        SharedPreferencesHandler sharedPreferencesHandler = application.getSharedPreferencesHandler();
        final Area[] areas = sharedPreferencesHandler.getAreaList();
        this.areaSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.text_view, areas));
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
                    }
                    default: {
                        GPSText.setVisibility(View.INVISIBLE);
                        address.setVisibility(View.INVISIBLE);
                        areaSpinner.setVisibility(View.VISIBLE);
                    }
                }
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

        this.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int placeType = radioGroup.getCheckedRadioButtonId();
                if (placeType == ADDRESS)
                    addressList = AddressChooserFragment.showAddressChooser(AdvancedSearchActivity.this, address.getText().toString());
                else
                    search();
            }
        });
    }

    @Override
    public void setAddressNumber(int i) {
        Address address = this.addressList.get(i);
        this.latitude = address.getLatitude();
        this.longitude = address.getLongitude();
        search();
    }

    public void search() {
        Intent intent = new Intent(getApplicationContext(), QuickSearchActivity.class);
        Bundle bundle = new Bundle();
        bundle.putDouble(Manager.LATITUDE, latitude);
        bundle.putDouble(Manager.LONGITUDE, longitude);
        bundle.putInt(Manager.TYPE, restaurantType);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
