package straw.polito.it.straw;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class CreateMenu extends AppCompatActivity {



    private ListView listViewPlate;
    private Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_menu);
        List<Food> liste_plate= new ArrayList<Food>();
        liste_plate.add(new Food("Pizza", "4.99", ));


        //listViewPlate = ( ListView ) findViewById( R.id._list);
        listViewPlate.setAdapter(new FoodAdapter(ctx, R.layout.list_design, listViewPlate));
    }
}
