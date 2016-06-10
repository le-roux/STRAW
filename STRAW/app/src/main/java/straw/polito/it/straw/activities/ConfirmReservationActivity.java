package straw.polito.it.straw.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import straw.polito.it.straw.CompletionActivity;
import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.adapter.FoodExpandableAdapterRemove;
import straw.polito.it.straw.data.Food;
import straw.polito.it.straw.data.Reservation;
import straw.polito.it.straw.data.User;
import straw.polito.it.straw.utils.DatabaseUtils;
import straw.polito.it.straw.utils.DateDisplay;
import straw.polito.it.straw.utils.PriceDisplay;
import straw.polito.it.straw.utils.SharedPreferencesHandler;
import straw.polito.it.straw.utils.TimerDisplay;

public class ConfirmReservationActivity extends AppCompatActivity implements CompletionActivity{

    private TextView numberPeople;
    private TextView place;
    private DateDisplay date;
    private TimerDisplay time;
    private ExpandableListView list_item;
    private PriceDisplay price;
    private Button confirmButton;
    private String tokenTo;
    private Reservation reservation;
    private Resources resources;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_reservation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("CONFIRM  RESERVATION");
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        this.tokenTo=intent.getExtras().getString("tokenGCM");
        this.resources = getResources();
        this.reservation = Reservation.create(intent.getStringExtra(Reservation.RESERVATION));

        this.numberPeople = (TextView)findViewById(R.id.number_people);
        this.place = (TextView)findViewById(R.id.place);
        this.date = (DateDisplay)findViewById(R.id.Date);
        this.time = (TimerDisplay)findViewById(R.id.Time);
        this.list_item = (ExpandableListView)findViewById(R.id.list_item);
        this.price = (PriceDisplay)findViewById(R.id.Price);
        this.confirmButton = (Button)findViewById(R.id.confirm_button);

        StringBuilder builder = new StringBuilder();
        builder.append(this.reservation.getNumberPeople())
                .append(' ')
                .append(this.resources.getString(R.string.Persons));
        this.numberPeople.setText(builder.toString());

        if (this.reservation.getPlace() == Reservation.OUTSIDE)
            this.place.setText(this.resources.getString(R.string.Outside));
        else if (this.reservation.getPlace()== Reservation.INSIDE)
            this.place.setText(this.resources.getString(R.string.Inside));
        else
            this.place.setText(this.getResources().getString(R.string.NoPreference));
        this.date.setDate(this.reservation.getYear(), this.reservation.getMonth(), this.reservation.getDay());
        this.time.setTime(this.reservation.getHourOfDay(), this.reservation.getMinutes());
        FoodExpandableAdapterRemove adapter = new FoodExpandableAdapterRemove(getApplicationContext(), this.reservation.getPlates(), this.reservation.getDrinks());
        this.list_item.setAdapter(adapter);

        //Expand all the groups
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            this.list_item.expandGroup(i);
        }

        double price = 0;
        for (Food plate : this.reservation.getPlates())
            price += plate.getPrice();
        for (Food drink : this.reservation.getDrinks())
            price += drink.getPrice();
        this.price.setPrice(price);

        this.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * Add the reservation to the reservations list of the customer (if it's not
                 * already in it [if the user clicks more than once on the button]).
                 */
                StrawApplication application = (StrawApplication)getApplication();
                SharedPreferencesHandler sharedPreferencesHandler = application.getSharedPreferencesHandler();
                User user = sharedPreferencesHandler.getCurrentUser();
                user.addReservation(reservation);
                sharedPreferencesHandler.storeCurrentUser(user.toString());
                /**
                 * Store the reservation in the database (both for the customer and the restaurant).
                 */
                dialog = new ProgressDialog(ConfirmReservationActivity.this);
                dialog.setIndeterminate(true);
                dialog.setMessage(ConfirmReservationActivity.this.getString(R.string.SendingReservation));
                dialog.setCancelable(false);
                dialog.show();
                DatabaseUtils databaseUtils = ((StrawApplication)getApplication()).getDatabaseUtils();
                databaseUtils.saveReservation(reservation, ConfirmReservationActivity.this);
                post();
             }
        });
    }

    public void post(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL object = new URL("https://gcm-http.googleapis.com/gcm/send");

                    HttpURLConnection con = (HttpURLConnection) object.openConnection();
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Authorization", "key="+StrawApplication.serverAPIKey);
                    con.setRequestMethod("POST");

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("to",tokenTo);
                    jsonObject.put("delay_while_idle",true);
                    JSONObject res = new JSONObject();
                    res.put("reservation",reservation.toString());
                    jsonObject.put("data",res);
                    OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                    wr.write(jsonObject.toString());
                    wr.flush();
                    wr.close();

                    StringBuilder sb = new StringBuilder();
                    int HttpResult = con.getResponseCode();
                    if (HttpResult == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(
                                new InputStreamReader(con.getInputStream(), "utf-8"));
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        br.close();
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }).start();

    }

    // Called by the secondary thread once the reservation has been saved in the database
    @Override
    public void onComplete() {
        if (this.dialog != null)
            dialog.dismiss();
        Intent intent = new Intent(this, SearchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
