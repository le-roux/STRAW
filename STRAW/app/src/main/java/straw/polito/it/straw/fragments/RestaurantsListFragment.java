package straw.polito.it.straw.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;

import straw.polito.it.straw.R;
import straw.polito.it.straw.activities.SearchDetailActivity;
import straw.polito.it.straw.adapter.RestaurantListAdapter;
import straw.polito.it.straw.data.Manager;

/**
 * Created by Sylvain on 23/05/2016.
 */
public class RestaurantsListFragment extends Fragment {

    private RestaurantListAdapter adapter;
    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.listView = (ListView)inflater.inflate(R.layout.list, container, false);
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), SearchDetailActivity.class);
                i.putExtra(SearchDetailActivity.RESTAURANT, ((Manager)adapter.getItem(position)).toJSONObject());
                startActivity(i);
            }
        });
        if (this.adapter != null)
            this.listView.setAdapter(this.adapter);
        return this.listView;
    }

    public void setAdapter(RestaurantListAdapter adapter) {
        this.adapter = adapter;
        if (this.listView != null)
            this.listView.setAdapter(this.adapter);
    }

    public Adapter getAdapter() {
        return this.adapter;
    }
}
