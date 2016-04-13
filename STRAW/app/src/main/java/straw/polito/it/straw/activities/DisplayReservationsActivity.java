package straw.polito.it.straw.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.adapter.ReservationAdapter;
import straw.polito.it.straw.data.Reservation;
import straw.polito.it.straw.straw.polito.it.straw.utils.Logger;

public class DisplayReservationsActivity extends AppCompatActivity {

    private ListView reservationList_View;
    private ArrayList<Reservation> reservationList;

    public static final String RESERVATION = "Reservation";
    public static final String RESERVATION_ID = "Reservation_id";

    public static final int MANAGE_RESERVATION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_reservations);

        this.reservationList = new ArrayList<Reservation>();
        if (savedInstanceState == null) {
            this.reservationList.add(new Reservation(2, "pasta"));
            this.reservationList.add(new Reservation(4, "pasta, pizza"));
            this.reservationList.add(new Reservation(2, "gnocchis, mineral water"));
        }

        this.reservationList_View = (ListView)findViewById(R.id.reservations_list);
        this.reservationList_View.setAdapter(new ReservationAdapter(getApplicationContext(),
                this.reservationList, this));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            if(requestCode == MANAGE_RESERVATION_REQUEST_CODE) {
                this.reservationList.remove(result.getIntExtra(RESERVATION_ID, 0));
                ((ReservationAdapter)this.reservationList_View.getAdapter()).notifyDataSetChanged();
            }
        }
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
}