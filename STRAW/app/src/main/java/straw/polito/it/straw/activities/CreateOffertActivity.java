package straw.polito.it.straw.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import straw.polito.it.straw.R;
import straw.polito.it.straw.adapter.FoodAdapter;
import straw.polito.it.straw.data.Food;
import straw.polito.it.straw.straw.polito.it.straw.utils.Logger;
import straw.polito.it.straw.straw.polito.it.straw.utils.ObjectSerializer;

public class CreateOffertActivity extends AppCompatActivity {

    ListView offerts_listView;
    Button create_button;
    ArrayList<Food> offerts;
    SharedPreferences mShared;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_offert);

        initialize();
        mShared= PreferenceManager.getDefaultSharedPreferences(this);

        if(!mShared.contains("Offerts")) {
            offerts = new ArrayList<>();
        }
        offerts_listView.setAdapter(new FoodAdapter(getBaseContext(), offerts));

    }

    private void initialize() {
        offerts_listView=(ListView)findViewById(R.id.offerts_listView);
        create_button=(Button)findViewById(R.id.create_button);
    }
    @Override
    public void onSaveInstanceState(Bundle bundle) {
        try {
            mShared.edit().putString("Offerts", ObjectSerializer.serialize(offerts)).commit();
        } catch (IOException e) {
            Logger.d("Error serializing offerts");
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle bundle) {
        try {
            offerts = (ArrayList<Food>) ObjectSerializer.deserialize(mShared.getString("Offerts", ObjectSerializer.serialize(new ArrayList<Food>())));
        } catch (IOException e) {
            Logger.d("Error deserializing offerts");
        }

    }
}
