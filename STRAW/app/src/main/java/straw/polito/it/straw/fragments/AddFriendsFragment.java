package straw.polito.it.straw.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import straw.polito.it.straw.R;
import straw.polito.it.straw.UserContainer;

/**
 * Created by Sylvain on 13/05/2016.
 */
public class AddFriendsFragment extends Fragment {

    private ViewGroup viewGroup;
    private Button addButton;
    private Button doneButton;
    private ListView friendsListView;
    private UserContainer container;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.container = (UserContainer)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        /**
         * Inflate the layout for this fragment
         */
        this.viewGroup = (ViewGroup)inflater.inflate(R.layout.fragment_add_friends, container, false);
        /**
         * Retrieve the graphical components
         */
        this.addButton = (Button)this.viewGroup.findViewById(R.id.add_button);
        this.doneButton = (Button)this.viewGroup.findViewById(R.id.done_button);
        this.friendsListView = (ListView)this.viewGroup.findViewById(R.id.list_friends);
        /**
         * Set the listeners
         */
        this.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        this.doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = AddFriendsFragment.this.container.getFragmentManager().beginTransaction();
                transaction.remove(AddFriendsFragment.this);
                transaction.commit();
            }
        });

        return this.viewGroup;

    }

}
