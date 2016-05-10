package straw.polito.it.straw.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import straw.polito.it.straw.R;
import straw.polito.it.straw.adapter.RestaurantListAdapter;
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.StrawApplication;

import straw.polito.it.straw.utils.Logger;
import straw.polito.it.straw.utils.DatabaseUtils;
import straw.polito.it.straw.utils.SharedPreferencesHandler;
import android.widget.AdapterView.OnItemSelectedListener;


public class QuickSearchActivity extends AppCompatActivity{

    private ArrayList<Manager> restaurant_list;
    private ArrayList<Manager> restaurant_list_tmp;
    private Spinner spinner1;
    private Spinner spinner2;
    private ListView restaurant_listView;
    SharedPreferences mShared;
    private StrawApplication application;
    private SharedPreferences sharedPreferences;
    private Button filtersButton;
    //private TextView t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_search);
        this.filtersButton = (Button) findViewById(R.id.addfilter);
        Spinner staticSpinner = (Spinner) findViewById(R.id.spinner1);
        //TextView t1 = (TextView) findViewById(R.id.typeFood);
        restaurant_list = new ArrayList<>();
        restaurant_list_tmp = new ArrayList<>();
        init_list();
        mShared= PreferenceManager.getDefaultSharedPreferences(this);
        String tmp = "";
        if(mShared.contains("ManagerList")){
            try{
            String ss = mShared.getString("ManagerList", "Error");
            JSONArray jarr = new JSONArray(ss);
                for (int i = 0; i < jarr.length(); i++) {
                    try {
                        tmp = jarr.get(i).toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    restaurant_list.add(new Manager(tmp));
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
        restaurant_listView.setAdapter(new RestaurantListAdapter(getBaseContext(), restaurant_list));
        this.restaurant_listView = (ListView) findViewById(R.id.restaurant_list);

        addListenerOnButton();
        addListenerOnSpinnerItemSelection();

        restaurant_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getBaseContext(), SearchDetailActivity.class);
                i.putExtra("res", restaurant_list.get(position).toJSONObject());
                startActivity(i);
            }
        });
        filtersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }
    public RestaurantListAdapter getAdapter() {
        return (RestaurantListAdapter)this.restaurant_listView.getAdapter();
    }
    private void init_list() {
        this.restaurant_listView = (ListView) findViewById(R.id.restaurant_list);
    }

    public void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);

        spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    SortByRating(parent, view, id);
                } else if (position == 1) {
                    SortByLocation(parent, view, id);
                } else if (position == 2) {
                    SortByPrice(parent, view, id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    public void SortByRating(AdapterView<?> parent, View view,long id){
        Toast.makeText(parent.getContext(),
                "List of restaurant sort by rating",
                Toast.LENGTH_SHORT).show();
    }
    public void SortByLocation(AdapterView<?> parent, View view,long id){
        Toast.makeText(parent.getContext(),
                "List of restaurant sort by location",
                Toast.LENGTH_SHORT).show();
    }
    public void SortByPrice(AdapterView<?> parent, View view,long id){
        Toast.makeText(parent.getContext(),
                "List of restaurant sort by price",
                Toast.LENGTH_SHORT).show();

        Collections.sort(restaurant_list,Manager.PriceComparator);
        ((RestaurantListAdapter) restaurant_listView.getAdapter()).notifyDataSetChanged();
    }
    public void addListenerOnButton() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
    }

    private void selectImage() {
        final CharSequence[] options = {"No filter",getString(R.string.pizzeria), getString(R.string.italian),getString(R.string.jap),getString(R.string.kebap),getString(R.string.chinese),getString(R.string.other), "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(QuickSearchActivity.this);
        builder.setTitle("Choose a filter");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("No filter")) {
                    TextView t1 = (TextView) findViewById(R.id.typeOfFood);
                    t1.setText("Every type");
                    restaurant_list.addAll(restaurant_list_tmp);
                    restaurant_list_tmp.clear();
                    ((RestaurantListAdapter) restaurant_listView.getAdapter()).notifyDataSetChanged();

                } else if (options[item].equals("Pizzeria")) {
                    TextView t1 = (TextView) findViewById(R.id.typeOfFood);
                    t1.setText(R.string.pizzeria);
                    restaurant_list.addAll(restaurant_list_tmp);
                    restaurant_list_tmp.clear();
                    for (int a = 0; a < restaurant_list.size(); a++) {
                        if (!restaurant_list.get(a).getFood_type().equals("Pizzeria")) {
                            restaurant_list_tmp.add(restaurant_list.get(a));
                            restaurant_list.remove(a);
                        }
                    }
                    ((RestaurantListAdapter) restaurant_listView.getAdapter()).notifyDataSetChanged();
                } else if (options[item].equals("Italian")) {
                    TextView t1 = (TextView) findViewById(R.id.typeOfFood);
                    t1.setText(R.string.italian);
                    restaurant_list.addAll(restaurant_list_tmp);
                    restaurant_list_tmp.clear();
                    for (int a = 0; a < restaurant_list.size(); a++) {
                        if (!restaurant_list.get(a).getFood_type().equals("Italian")) {
                            restaurant_list_tmp.add(restaurant_list.get(a));
                            restaurant_list.remove(a);

                        }
                    }
                    ((RestaurantListAdapter) restaurant_listView.getAdapter()).notifyDataSetChanged();
                } else if (options[item].equals("Japanese")) {
                    TextView t1 = (TextView) findViewById(R.id.typeOfFood);
                    t1.setText(R.string.jap);
                    restaurant_list.addAll(restaurant_list_tmp);
                    restaurant_list_tmp.clear();
                    for (int a = 0; a < restaurant_list.size(); a++) {
                        if (!restaurant_list.get(a).getFood_type().equals("Japanese")) {
                            restaurant_list_tmp.add(restaurant_list.get(a));
                            restaurant_list.remove(a);

                        }
                    }
                    ((RestaurantListAdapter) restaurant_listView.getAdapter()).notifyDataSetChanged();
                } else if (options[item].equals("Kebap")) {
                    TextView t1 = (TextView) findViewById(R.id.typeOfFood);
                    t1.setText(R.string.kebap);
                    restaurant_list.addAll(restaurant_list_tmp);
                    restaurant_list_tmp.clear();
                    for (int a = 0; a < restaurant_list.size(); a++) {
                        if (!restaurant_list.get(a).getFood_type().equals("Kebap")) {
                            restaurant_list_tmp.add(restaurant_list.get(a));
                            restaurant_list.remove(a);

                        }
                    }
                    ((RestaurantListAdapter) restaurant_listView.getAdapter()).notifyDataSetChanged();
                } else if (options[item].equals("Chinese")) {
                    TextView t1 = (TextView) findViewById(R.id.typeOfFood);
                    t1.setText(R.string.chinese);
                    restaurant_list.addAll(restaurant_list_tmp);
                    restaurant_list_tmp.clear();
                    for (int a = 0; a < restaurant_list.size(); a++) {
                        if (!restaurant_list.get(a).getFood_type().equals("Chinese")) {
                            restaurant_list_tmp.add(restaurant_list.get(a));
                            restaurant_list.remove(a);

                        }
                    }
                    ((RestaurantListAdapter) restaurant_listView.getAdapter()).notifyDataSetChanged();
                } else if (options[item].equals("Other")) {
                    TextView t1 = (TextView) findViewById(R.id.typeOfFood);
                    t1.setText(R.string.other);
                    restaurant_list.addAll(restaurant_list_tmp);
                    restaurant_list_tmp.clear();
                    for (int a = 0; a < restaurant_list.size(); a++) {
                        if (!restaurant_list.get(a).getFood_type().equals("Other")) {
                            restaurant_list_tmp.add(restaurant_list.get(a));
                            restaurant_list.remove(a);

                        }
                    }
                    ((RestaurantListAdapter) restaurant_listView.getAdapter()).notifyDataSetChanged();
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

}
