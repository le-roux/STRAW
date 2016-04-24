package straw.polito.it.straw.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import straw.polito.it.straw.BaseAdapterContainer;
import straw.polito.it.straw.R;
import straw.polito.it.straw.adapter.ReservationAdapter;
import straw.polito.it.straw.data.Drink;
import straw.polito.it.straw.data.Food;
import straw.polito.it.straw.data.Plate;
import straw.polito.it.straw.data.Reservation;

public class DisplayReservationsActivity extends AppCompatActivity implements BaseAdapterContainer {

    private ListView reservationList_View;
    private ArrayList<Reservation> reservationList;
    private SharedPreferences sharedPreferences;

    public static final String RESERVATION_NUMBER = "ReservationNumber";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_reservations);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        this.reservationList = new ArrayList<Reservation>();
        if (savedInstanceState == null) {
            String description = "";
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(this.sharedPreferences.getString(Reservation.RESERVATION, ""));
            } catch (JSONException e) {
                e.printStackTrace();
                jsonArray = new JSONArray();
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    description = jsonArray.get(i).toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                this.reservationList.add(Reservation.create(description));
            }

            //For test purpose only
            if (jsonArray.length() == 0) {
                ArrayList<Food> plates = new ArrayList<>();
                plates.add(new Plate());
                ArrayList<Food> drinks = new ArrayList<>();
                drinks.add(new Drink());
                this.reservationList.add(new Reservation(2, plates, drinks));
                this.reservationList.add(new Reservation(4, plates, drinks));
                this.reservationList.add(new Reservation(2, plates, drinks));
            }
        }

        this.reservationList_View = (ListView)findViewById(R.id.reservations_list);
        this.reservationList_View.setAdapter(new ReservationAdapter(getApplicationContext(),
                this.reservationList, this));
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        for (int i = 0; i < this.reservationList.size(); i++) {
            b.putString(String.valueOf(i), this.reservationList.get(i).toString());
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle b) {
        for (int i = 0; i < b.size(); i++) {
            this.reservationList.add(Reservation.create(b.getString(String.valueOf(i))));
        }
    }

    public ReservationAdapter getAdapter() {
        return (ReservationAdapter)this.reservationList_View.getAdapter();
    }

    /**
     * Save data (reservations) in sharedPreference for permanent storage
     */
    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < this.reservationList.size(); i++) {
            try {
                jsonArray.put(i, this.reservationList.get(i).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        editor.putString(Reservation.RESERVATION, jsonArray.toString());
        editor.commit();
    }
}