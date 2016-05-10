package straw.polito.it.straw.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import straw.polito.it.straw.R;

public class SearchActivity extends AppCompatActivity {

    private Button button_edit;
    private Button button_quicksearch;
    private Button advancedSearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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
