package straw.polito.it.straw.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import straw.polito.it.straw.data.Food;
import straw.polito.it.straw.adapter.FoodAdapter;
import straw.polito.it.straw.data.Plate;
import straw.polito.it.straw.R;

public class CreateMenuActivity extends AppCompatActivity {



    private ListView listViewPlate;
    private Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_menu);
        ctx=this;
        List<Food> liste_plate= new ArrayList<Food>();
        liste_plate.add(new Plate());



        listViewPlate = ( ListView ) findViewById( R.id.Plate_list);
        listViewPlate.setAdapter(new FoodAdapter(ctx));
    }
}
