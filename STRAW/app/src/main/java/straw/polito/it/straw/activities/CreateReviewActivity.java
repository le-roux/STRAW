package straw.polito.it.straw.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import straw.polito.it.straw.R;
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.data.Review;
import straw.polito.it.straw.data.User;
import straw.polito.it.straw.utils.SharedPreferencesHandler;

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
        man=new Manager(getIntent().getExtras().getString("res"));
        user=new SharedPreferencesHandler(getBaseContext()).getCurrentUser();
        initialize();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                man.getReviews().add(new Review(user.getEmail(),rate.getRating(),desc.getText().toString()));
                //SAVE IN THE DATABASE
                //new SharedPreferencesHandler(getBaseContext()).storeCurrentManager(man.toJSONObject());
                SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                sp.edit().putString("Manager",man.toJSONObject()).commit();
                Intent resultIntent = new Intent();
                setResult(SearchDetailActivity.RESULT_OK, resultIntent);
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