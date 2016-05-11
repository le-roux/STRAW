package straw.polito.it.straw.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.adapter.ReviewAdapter;
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.data.Review;
import straw.polito.it.straw.utils.ImageManager;
import straw.polito.it.straw.utils.SharedPreferencesHandler;

public class SearchDetailActivity extends AppCompatActivity {

    private TextView name;
    private TextView price;
    private TextView menu;
    private TextView book;
    private TextView preorder;
    private TextView add_rev;
    private ImageView img;
    private ListView review;
    private Manager man;

    private static int REQ_CODE_REV=1;

    public static final String RESTAURANT = "Restaurant";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_detail);
        man =new Manager(getIntent().getExtras().getString("res"));
        initialize();
        name.setText(man.getRes_name());
        price.setText("SET PRICE");

        ImageManager.setImage(this, img, Uri.parse(man.getImage()).toString() );

        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        man = new Manager(sp.getString("Manager","Error"));
        review.setAdapter(new ReviewAdapter(getBaseContext(),man.getReviews()));

        ImageManager.setImage(this, img, man.getImage());

        setListeners();
    }

    private void setListeners() {
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(),CreateMenuActivity.class);
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
                i.putExtra("res",man.toJSONObject());
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
        //preorder=(TextView)findViewById(R.id.preorder);
        add_rev=(TextView)findViewById(R.id.add_review);
        review = (ListView) findViewById(R.id.reviews);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
      //  if(requestCode==REQ_CODE_REV){
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        man = new Manager(sp.getString("Manager","Error"));
        review.setAdapter(new ReviewAdapter(getBaseContext(),man.getReviews()));
       // }
    }
}
