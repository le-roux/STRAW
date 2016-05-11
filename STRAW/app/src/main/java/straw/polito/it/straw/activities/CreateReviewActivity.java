package straw.polito.it.straw.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
        man = new Manager(getIntent().getExtras().getString(SearchDetailActivity.RESTAURANT));
        final StrawApplication application = (StrawApplication)getApplication();
        user = application.getSharedPreferencesHandler().getCurrentUser();
        initialize();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Review review = new Review(user.getEmail(),rate.getRating(),desc.getText().toString());
                man.getReviews().add(review);
                DatabaseUtils databaseUtils = application.getDatabaseUtils();
                databaseUtils.addReview(man.getRes_name(), review);
                Intent resultIntent = new Intent();
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
