package straw.polito.it.straw.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import java.util.ArrayList;

import straw.polito.it.straw.BaseAdapterContainer;
import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.adapter.ReservationAdapterManager;
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.data.Reservation;

public class DisplayReservationsActivity extends AppCompatActivity implements BaseAdapterContainer {

    private ListView reservationList_View;
    private ArrayList<Reservation> reservationList;
    private Manager manager;
    private boolean pastReservations = false;

    public static final String PAST_RESERVATIONS = "pastReservations";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_reservations);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("DISPLAY  RESERVATIONS");
        setSupportActionBar(toolbar);
        Intent intent = getIntent();

        this.pastReservations = intent.getBooleanExtra(PAST_RESERVATIONS, false);

        this.reservationList = new ArrayList<>();

        this.reservationList_View = (ListView)findViewById(R.id.reservations_list);
        ReservationAdapterManager adapter = new ReservationAdapterManager(this,
                this.reservationList, this);
        this.reservationList_View.setAdapter(adapter);
        StrawApplication application = (StrawApplication)getApplication();
        this.manager = application.getSharedPreferencesHandler().getCurrentManager();
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(this.getString(R.string.RetrievingReservations));
        dialog.setIndeterminate(true);
        dialog.show();
        if (this.pastReservations)
            application.getDatabaseUtils().retrieveRestaurantPastReservations(this.manager.getRes_name(), adapter, dialog);
        else
            application.getDatabaseUtils().retrieveRestaurantReservations(this.manager.getRes_name(), adapter, dialog);
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

    public ReservationAdapterManager getAdapter() {
        return (ReservationAdapterManager)this.reservationList_View.getAdapter();
    }

    /**
     * Save data (reservations) in sharedPreference for permanent storage
     */
    @Override
    public void onStop() {
        super.onStop();
        /*SharedPreferences.Editor editor = this.sharedPreferences.edit();
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < this.reservationList.size(); i++) {
            try {
                jsonArray.put(i, this.reservationList.get(i).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        editor.putString(Reservation.RESERVATION, jsonArray.toString());
        editor.commit();*/
    }
}