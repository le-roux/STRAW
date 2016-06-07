package straw.polito.it.straw.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import straw.polito.it.straw.R;
import straw.polito.it.straw.adapter.ReviewAdapter;
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.data.Review;
import straw.polito.it.straw.utils.ImageManager;

public class SearchDetailActivity extends AppCompatActivity {

    private TextView name;
    private TextView price;
    private TextView menu;
    private TextView book;
    private TextView add_rev;
    private ImageView img;
    private ListView review;
    private RatingBar ratingBar;
    private Manager man;

    private static int REQ_CODE_REV=1;

    public static final String RESTAURANT = "Restaurant";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("SEARCH DETAIL");
        setSupportActionBar(toolbar);
        if (savedInstanceState == null)
            man = new Manager(getIntent().getExtras().getString(RESTAURANT));
        else
            this.man = new Manager(savedInstanceState.getString(RESTAURANT));
        initialize();

        name.setText(man.getRes_name());
        StringBuilder builder = new StringBuilder();
        builder.append(this.man.getMin_price())
                .append(" - ")
                .append(this.man.getMax_price())
                .append(" €");
        price.setText(builder.toString());

        this.ratingBar.setRating(this.man.getRate());

        review.setAdapter(new ReviewAdapter(getBaseContext(),man.getReviews()));

        ImageManager.setImage(this, img, man.getImage());

        setListeners();
    }

    private void setListeners() {
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(),DisplayMenuActivity.class);
                i.putExtra("active",false);
                i.putExtra(RESTAURANT, man.toJSONObject());
                startActivity(i);
            }
        });
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(),BookTableActivity.class);
                i.putExtra(BookTableActivity.RESTAURANT, man.toJSONObject());
                startActivity(i);
            }
        });
        add_rev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(),CreateReviewActivity.class);
                i.putExtra(RESTAURANT,man.toJSONObject());
                startActivityForResult(i,REQ_CODE_REV);
            }
        });
    }

    private void initialize() {
        img=(ImageView)findViewById(R.id.img);
        name=(TextView)findViewById(R.id.name);
        price=(TextView)findViewById(R.id.price);
        menu=(TextView)findViewById(R.id.menu);
        book=(TextView)findViewById(R.id.booking);
        add_rev=(TextView)findViewById(R.id.add_review);
        review = (ListView) findViewById(R.id.reviews);
        this.ratingBar = (RatingBar)findViewById(R.id.ratingBar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.man.addReview(new Review(data.getStringExtra(Review.REVIEW)));
        ((ReviewAdapter)this.review.getAdapter()).notifyDataSetChanged();
        this.ratingBar.setRating(this.man.getRate());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(RESTAURANT, this.man.toJSONObject());
    }
}
