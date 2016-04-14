package straw.polito.it.straw.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.data.Offer;

/**
 * Created by Andres Camilo Jimenez on 13/04/2016.
 */
public class OffersListAdapter extends BaseAdapter {

    private ArrayList<Offer> offers;
    private Context context;

    public OffersListAdapter(Context context,ArrayList<Offer>offer) {
        this.offers = offer;
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.offers.size();
    }

    @Override
    public Object getItem(int position) {
        return this.offers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<Offer> getOffers() {
        return offers;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.offers_list_design, null);

            ImageView remove_button = (ImageView) convertView.findViewById(R.id.imageView);

                remove_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        offers.remove(position);
                        OffersListAdapter.this.notifyDataSetChanged();
                        SharedPreferences mShared= PreferenceManager.getDefaultSharedPreferences(context);
                        JSONArray jarr=new JSONArray();
                        for (int i=0;i<offers.size();i++){
                            try {
                                jarr.put(i,offers.get(i).toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        mShared.edit().putString("Offerts",jarr.toString()).commit();
                    }
                });
                remove_button.setFocusable(false);


        }

        TextView title = (TextView)convertView.findViewById(R.id.PlateName);
        title.setFocusable(false);
        TextView price = (TextView)convertView.findViewById(R.id.PlatePrice);
        price.setFocusable(false);

        //Fill the View with the proper data
        if (position < this.offers.size()) {
            title.setText("Combo "+(position+1));
            price.setText(String.valueOf(this.offers.get(position).getPrice()) + " €");
        }
        return convertView;
    }
}
