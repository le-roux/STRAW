package straw.polito.it.straw.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.adapter.ReservationAdapter;
import straw.polito.it.straw.data.Reservation;
import straw.polito.it.straw.utils.Logger;

public class DisplayReservationsActivity extends AppCompatActivity {

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
            int reservation_number = this.sharedPreferences.getInt(RESERVATION_NUMBER, 0);
            String description;
            for (int i = 0; i < reservation_number; i++) {
                description = this.sharedPreferences.getString(Reservation.RESERVATION + String.valueOf(i), "");
                this.reservationList.add(Reservation.create(description));
            }
            //For test purpose only
            if (reservation_number == 0) {
                this.reservationList.add(new Reservation(2, "pasta"));
                this.reservationList.add(new Reservation(4, "pasta, pizza"));
                this.reservationList.add(new Reservation(2, "gnocchis, mineral water"));
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
        for (int i = 0; i < this.reservationList.size(); i++) {
            editor.putString(Reservation.RESERVATION + String.valueOf(i),
                    this.reservationList.get(i).toString());
        }
        editor.putInt(RESERVATION_NUMBER, this.reservationList.size());
        editor.commit();
    }
}