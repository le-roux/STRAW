package straw.polito.it.straw.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.data.Food;

public class AddPlateActivity extends AppCompatActivity {

    private ArrayList<Food> menu;
    private ListView menu_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plate);

        this.menu = new ArrayList<>(); //TO DO : change it
        this.menu_view = (ListView)findViewById(R.id.list_item);


    }
}
