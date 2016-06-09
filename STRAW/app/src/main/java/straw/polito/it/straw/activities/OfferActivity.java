package straw.polito.it.straw.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.adapter.OffersListAdapter;
import straw.polito.it.straw.data.Offer;
import straw.polito.it.straw.utils.Logger;

public class OfferActivity extends AppCompatActivity {

    ListView offerts_listView;
    Button create_button;
    Button returnB;
    ArrayList<Offer> offerts;
    SharedPreferences mShared;
    int CREATE_REQ=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offert);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("OFFERS");
        setSupportActionBar(toolbar);

        initialize();
        mShared= PreferenceManager.getDefaultSharedPreferences(this);
        offerts = new ArrayList<>();
        if(mShared.contains("Offerts")) {
            Logger.d("Offerts "+mShared.getString("Offerts","Error") );
           try{
               JSONArray jarr =new JSONArray(mShared.getString("Offerts","Error"));
               offerts=new ArrayList<>();
               for(int i=0;i<jarr.length();i++){
                   offerts.add(new Offer(jarr.get(i).toString()));
               }

           }catch(Exception ex){
               Logger.d(ex.getMessage());
           }

        }


        offerts_listView.setAdapter(new OffersListAdapter(getBaseContext(),offerts));
        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShared.edit().putString("combo_l", "[]").commit();
                Intent i = new Intent(getBaseContext(), CreateOfferActivity.class);
                startActivityForResult(i, CREATE_REQ);
            }
        });
        returnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CREATE_REQ && data!=null){
            if(data.hasExtra("new_combo")) {
                offerts.add(new Offer(mShared.getString("Combo","Error")));
                ((OffersListAdapter)this.offerts_listView.getAdapter()).notifyDataSetChanged();
                Logger.d("New Combo created! ");
            }
        }
    }

    private void initialize() {
        offerts_listView = (ListView) findViewById(R.id.offerts_listView);
        create_button = (Button) findViewById(R.id.create_offert_button);
        returnB=(Button)findViewById(R.id.return_button);
    }

    @Override
    protected void onStop() {
        super.onStop();
        JSONArray jarr=new JSONArray();
        for (int i=0;i<offerts.size();i++){
            try {
                jarr.put(i,offerts.get(i).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mShared.edit().putString("Offerts",jarr.toString()).commit();
    }
}
