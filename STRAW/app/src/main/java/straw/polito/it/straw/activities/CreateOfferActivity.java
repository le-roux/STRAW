package straw.polito.it.straw.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.adapter.OfferAdapter;
import straw.polito.it.straw.data.Food;
import straw.polito.it.straw.data.Menu;
import straw.polito.it.straw.data.Offer;

public class CreateOfferActivity extends AppCompatActivity {

    ListView food_listView;
    ListView combo_listView;
    Button finish;
    Button returnB;
    ArrayList<Food> menu;
    SharedPreferences mShared;
    ArrayList<Food> combo;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_offer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("CREATE  OFFER");
        setSupportActionBar(toolbar);
        initialize();
        mShared= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        menu=new ArrayList<Food>();
        combo=new ArrayList<Food>();

        JSONArray jsonArray = Menu.getMenuFromSharedPreferences(this.getApplicationContext());
        ArrayList<Food> tmp = Menu.getPlates(jsonArray);
        for (Food food : tmp) {
            menu.add(food);
        }
        tmp = Menu.getDrinks(jsonArray);
        for (Food food : tmp) {
            menu.add(food);
        }
        if(mShared.contains("combo_l")){
            try {
                JSONArray jarr=new JSONArray(mShared.getString("combo_l","Error"));
                for (int i=0;i<jarr.length();i++){
                    JSONObject jo= new JSONObject(jarr.get(i).toString());
                    combo.add(Food.create(jo));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        food_listView.setAdapter(new OfferAdapter(getApplicationContext(),menu,false));
        food_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                combo.add(menu.get(position));
                ((OfferAdapter) combo_listView.getAdapter()).notifyDataSetChanged();
            }
        });
        combo_listView.setAdapter(new OfferAdapter(getApplicationContext(), combo, true));

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(CreateOfferActivity.this);
                View promptsView = li.inflate(R.layout.alert_price, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateOfferActivity.this);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
                userInput.setText("0");
                // set dialog message
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Offer offer = new Offer();
                                offer.setCombo(combo);
                                offer.setPrice(Float.parseFloat(userInput.getText().toString()));
                                mShared.edit().putString("Combo", offer.toString()).commit();
                                Intent i = new Intent();
                                i.putExtra("new_combo", true);
                                setResult(OfferActivity.RESULT_OK, i);
                                mShared.edit().putString("combo_l", "[]").commit();
                                finish();
                            }
                        }).setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });
        returnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initialize() {
        food_listView=(ListView)findViewById(R.id.food_listView);
        combo_listView=(ListView)findViewById(R.id.combo_listView);
        finish=(Button)findViewById(R.id.finish_button);
        returnB=(Button)findViewById(R.id.return_button);
    }

    @Override
    protected void onStop() {
        super.onStop();
        JSONArray jarr= new JSONArray();
        for (int i=0;i<combo.size();i++){
            try {
                jarr.put(i,combo.get(i).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mShared.edit().putString("combo_l",jarr.toString()).commit();
    }

}
