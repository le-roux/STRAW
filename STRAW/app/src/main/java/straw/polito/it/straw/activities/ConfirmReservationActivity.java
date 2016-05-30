package straw.polito.it.straw.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.adapter.FoodExpandableAdapterRemove;
import straw.polito.it.straw.data.Food;
import straw.polito.it.straw.data.Reservation;
import straw.polito.it.straw.data.User;
import straw.polito.it.straw.utils.DatabaseUtils;
import straw.polito.it.straw.utils.DateDisplay;
import straw.polito.it.straw.utils.Logger;
import straw.polito.it.straw.utils.PriceDisplay;
import straw.polito.it.straw.utils.SharedPreferencesHandler;
import straw.polito.it.straw.utils.TimerDisplay;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfirmReservationActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_reservation);
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

        if (this.reservation.getPlace().equals(Reservation.Place.OUTSIDE))
            this.place.setText(this.resources.getString(R.string.Outside));
        else if (this.reservation.getPlace().equals(Reservation.Place.INSIDE))
            this.place.setText(this.resources.getString(R.string.Inside));
        else
            this.place.setText(this.getResources().getString(R.string.NoPreference));
        this.date.setDate(this.reservation.getYear(), this.reservation.getMonth(), this.reservation.getDay());
        this.time.setTime(this.reservation.getHourOfDay(), this.reservation.getMinutes());
        this.list_item.setAdapter(new FoodExpandableAdapterRemove(getApplicationContext(), this.reservation.getPlates(), this.reservation.getDrinks()));
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
                ProgressDialog dialog = new ProgressDialog(ConfirmReservationActivity.this);
                dialog.setIndeterminate(true);
                dialog.setMessage(ConfirmReservationActivity.this.getString(R.string.SendingReservation));
                dialog.setCancelable(false);
                dialog.show();
                DatabaseUtils databaseUtils = ((StrawApplication)getApplication()).getDatabaseUtils();
                databaseUtils.saveReservation(reservation, dialog);
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
                    Logger.d("Request "+jsonObject.toString());
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
                        Logger.d("LOL " + sb.toString());
                    } else {
                        Logger.d("LEL " +con.getResponseMessage());
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
}
