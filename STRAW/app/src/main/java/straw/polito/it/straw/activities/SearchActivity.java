package straw.polito.it.straw.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import straw.polito.it.straw.R;

public class SearchActivity extends AppCompatActivity {



    Button button_edit;
    Button button_quicksearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        this.button_edit = (Button)findViewById(R.id.edit_button_search);
        button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
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


    }
}
