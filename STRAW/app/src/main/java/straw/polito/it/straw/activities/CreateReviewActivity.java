package straw.polito.it.straw.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.data.Review;
import straw.polito.it.straw.data.User;
import straw.polito.it.straw.utils.DatabaseUtils;

public class CreateReviewActivity extends AppCompatActivity {

    private EditText desc;
    private RatingBar rate;
    private Button submit;
    private Manager man;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_review);
        final StrawApplication application = (StrawApplication)getApplication();

        // Init the data
        this.man = new Manager(getIntent().getExtras().getString(SearchDetailActivity.RESTAURANT));
        this.user = application.getSharedPreferencesHandler().getCurrentUser();

        // Retrieve the displayed components
        initialize();

        //Add a listener on the "Submit" button to add the review to the restaurant ones
        this.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Review review = new Review(user.getEmail(), rate.getRating(),
                        desc.getText().toString(), man.getRes_name());

                // Add the new review to the user reviews list
                user.getReviews().add(review);
                application.getSharedPreferencesHandler().storeCurrentUser(user.toString());

                // Add the review to the restaurant reviews list
                DatabaseUtils databaseUtils = application.getDatabaseUtils();
                man.getReviews().add(review);
                databaseUtils.addReview(man.getRes_name(),user.getEmail(), review);

                Intent resultIntent = new Intent();
                resultIntent.putExtra(Review.REVIEW, review.toString());
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

    }

    private void initialize() {
        desc = (EditText) findViewById(R.id.desc);
        rate = (RatingBar) findViewById(R.id.rate);
        submit= (Button) findViewById(R.id.submit);
    }
}
