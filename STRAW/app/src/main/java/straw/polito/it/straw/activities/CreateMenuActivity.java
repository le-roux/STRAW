package straw.polito.it.straw.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import straw.polito.it.straw.data.Drink;
import straw.polito.it.straw.data.Food;
import straw.polito.it.straw.adapter.FoodAdapter;
import straw.polito.it.straw.data.Plate;
import straw.polito.it.straw.R;

public class CreateMenuActivity extends AppCompatActivity {

    private static final int EDIT_FOOD = 1;
    public static final String ELEMENT = "Element";

    private ListView listViewPlate;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_menu);
        this.context = this;
        final ArrayList<Food> list_plate= new ArrayList<Food>();
        list_plate.add(new Plate());



        listViewPlate = (ListView) findViewById( R.id.Plate_list);
        listViewPlate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detail = null;
                Bundle data = new Bundle();
                data.putString(ELEMENT, list_plate.get(position).toString());
                if(list_plate.get(position).getClass() == Drink.class) {
                    detail = new Intent(getApplicationContext(), Drink.class);
                } else if (list_plate.get(position).getClass() == Plate.class) {
                    detail = new Intent(getApplicationContext(), Plate.class);
                }
                startActivityForResult(detail, EDIT_FOOD);
            }
        });
        listViewPlate.setAdapter(new FoodAdapter(context, list_plate));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if(requestCode == this.EDIT_FOOD) {

        }
    }
}
