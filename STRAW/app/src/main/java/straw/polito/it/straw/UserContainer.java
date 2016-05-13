package straw.polito.it.straw;

import android.app.FragmentManager;

import java.util.ArrayList;

import straw.polito.it.straw.data.Friend;
import straw.polito.it.straw.data.User;

/**
 * Created by Sylvain on 13/05/2016.
 */
public interface UserContainer {
    FragmentManager getFragmentManager();
    User getUser();
}
