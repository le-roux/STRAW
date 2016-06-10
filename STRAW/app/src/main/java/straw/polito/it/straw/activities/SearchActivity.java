package straw.polito.it.straw.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.data.User;
import straw.polito.it.straw.utils.DatabaseUtils;
import straw.polito.it.straw.utils.SharedPreferencesHandler;

public class SearchActivity extends AppCompatActivity {

    private Button button_edit;
    private Button button_quicksearch;
    private Button advancedSearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        if (toolbar != null) {
            toolbar.setTitle(getString(R.string.Search_Options));
            setSupportActionBar(toolbar);
        }

        SharedPreferencesHandler sharedPreferencesHandler = new SharedPreferencesHandler(getApplicationContext());
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if(sharedPreferences.contains("tokenGCM")){
            User u =sharedPreferencesHandler.getCurrentUser();
            String newToken=sharedPreferences.getString("tokenGCM","Error");
            if(u.getTokenGCM()!=null){
                if(!u.getTokenGCM().equals(newToken) && !newToken.equals("Error")){
                    DatabaseUtils databaseUtils =((StrawApplication)getApplication()).getDatabaseUtils();
                    databaseUtils.updateToken(u.getEmail(),newToken,DatabaseUtils.USER);
                }
            }

        }

        this.button_edit = (Button)findViewById(R.id.profileButton);
        button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileUserActivity.class);
                startActivity(intent);
            }
        });
        this.button_quicksearch = (Button)findViewById(R.id.quick_button_search);
        button_quicksearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QuickSearchActivity.class);
                startActivity(intent);
            }
        });
        this.advancedSearchButton = (Button)findViewById(R.id.advanced_search_button);
        this.advancedSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AdvancedSearchActivity.class);
                startActivity(intent);
            }
        });


    }
}
