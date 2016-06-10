package straw.polito.it.straw.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.adapter.ReviewAdapter;
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.data.Review;
import straw.polito.it.straw.utils.DatabaseUtils;
import straw.polito.it.straw.utils.ImageManager;
import straw.polito.it.straw.utils.Logger;

public class SearchDetailActivity extends AppCompatActivity {

    private TextView name;
    private TextView price;
    private TextView menu;
    private TextView book;
    private TextView add_rev;
    private TextView nav;
    private ImageView img;
    private ListView review;
    private RatingBar ratingBar;
    private TextView address;
    private Manager man;


    private LocationManager locationManager;
    private LocationListener locationListener;
    private double latitude;
    private double longitude;

    private static int REQ_CODE_REV = 1;

    public static final String RESTAURANT = "Restaurant";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        if (toolbar != null) {
            toolbar.setTitle(getString(R.string.Search_Detail));
            setSupportActionBar(toolbar);
        }
        if (savedInstanceState == null)
            man = new Manager(getIntent().getExtras().getString(RESTAURANT));
        else // The activity is restarting
            this.man = new Manager(savedInstanceState.getString(RESTAURANT));
        initialize();
        // Prepare the elements to display
        name.setText(man.getRes_name());
        StringBuilder builder = new StringBuilder();
        builder.append(this.man.getMin_price())
                .append(" - ")
                .append(this.man.getMax_price())
                .append(" €");
        price.setText(builder.toString());
        this.ratingBar.setRating(this.man.getRate());
        this.address.setText(this.man.getAddress());

        review.setAdapter(new ReviewAdapter(getBaseContext(),man.getReviews(),false));

        ImageManager.setImage(this, img, man.getImage());

        setListeners();
        this.locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
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

        // Acquire the current location.
        try {
            this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {
        }
    }

    private void setListeners() {
        // Open the menu of the restaurant
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), DisplayMenuActivity.class);
                i.putExtra(RESTAURANT, man.toJSONObject());
                startActivity(i);
            }
        });

        // Open the page to book a table in the restaurant
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), BookTableActivity.class);
                i.putExtra(BookTableActivity.RESTAURANT, man.toJSONObject());
                startActivity(i);
            }
        });

        // Open the page to add a review
        add_rev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(),CreateReviewActivity.class);
                i.putExtra(RESTAURANT,man.toJSONObject());
                startActivityForResult(i,REQ_CODE_REV);
            }
        });

        // Open Google Maps and show the route from current location to the restaurant
        nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr="+latitude+","+longitude+"&daddr="+man.getLatitude()+","+man.getLongitude()));
                startActivity(intent);
            }
        });
    }

    private void initialize() {
        this.img = (ImageView)findViewById(R.id.img);
        this.name = (TextView)findViewById(R.id.name);
        this.price = (TextView)findViewById(R.id.price);
        this.menu = (TextView)findViewById(R.id.menu);
        this.book = (TextView)findViewById(R.id.booking);
        this.add_rev = (TextView)findViewById(R.id.add_review);
        this.review = (ListView) findViewById(R.id.reviews);
        this.nav = (TextView)findViewById(R.id.navigate);
        this.ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        this.address = (TextView)findViewById(R.id.address);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ_CODE_REV) {
            if(data!=null) {
                if (data.getExtras().containsKey(Review.REVIEW)) {
                    this.man.addReview(new Review(data.getStringExtra(Review.REVIEW)));
                    ((ReviewAdapter) this.review.getAdapter()).notifyDataSetChanged();
                    this.ratingBar.setRating(this.man.getRate());
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(RESTAURANT, this.man.toJSONObject());
    }
}
