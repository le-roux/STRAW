package straw.polito.it.straw.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.adapter.RestaurantListAdapter;
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.utils.CustomOnItemSelectedListener;

public class QuickSearchActivity extends AppCompatActivity {

    private ArrayList<Manager> restaurant_list;
    private Spinner spinner1;
    private ListView restaurant_listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_search);
        this.restaurant_list = new ArrayList<Manager>();




        this.restaurant_list.add(new Manager("Chez Tonton"));
        this.restaurant_list.add(new Manager("Chez Tonton"));
        this.restaurant_list.add(new Manager("Chez Tonton"));
        this.restaurant_list.add(new Manager("Chez Tonton"));
        this.restaurant_list.add(new Manager("Chez Tonton"));
        this.restaurant_list.add(new Manager("Chez Tonton"));
        this.restaurant_list.add(new Manager("Chez Tonton"));
        this.restaurant_list.add(new Manager("Chez Tonton"));


        this.restaurant_listView = (ListView) findViewById(R.id.restaurant_list);
        this.restaurant_listView.setAdapter(new RestaurantListAdapter(getApplicationContext(),this.restaurant_list, this));

        addListenerOnButton();
        addListenerOnSpinnerItemSelection();


    }
    public RestaurantListAdapter getAdapter() {
        return (RestaurantListAdapter)this.restaurant_listView.getAdapter();
    }


    private void initialize() {
        restaurant_listView = (ListView) findViewById(R.id.restaurant_list);
    }



    public void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }
    public void addListenerOnButton() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
    }
}
