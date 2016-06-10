package straw.polito.it.straw.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.adapter.ReviewAdapter;
import straw.polito.it.straw.data.User;
import straw.polito.it.straw.utils.DatabaseUtils;

/**
 * Created by Andres Camilo Jimenez on 22/05/2016.
 */
public class ReviewsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);
        DatabaseUtils db=((StrawApplication)getActivity().getApplication()).getDatabaseUtils();
        User uN=db.retrieveUserProfile();

        ListView listView = (ListView)view.findViewById(R.id.list_item);
        ReviewAdapter adapter = new ReviewAdapter(getActivity().getBaseContext(),uN.getReviews(),true);
        listView.setAdapter(adapter);
        return view;
    }
}
