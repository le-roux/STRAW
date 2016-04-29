package straw.polito.it.straw.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.data.Menu;

/**
 * Created by Sylvain on 29/04/2016.
 */
public class DatabaseUtils {
    private Firebase firebase;
    private Context context;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    /**
     * Names of the first-level nodes in the Firebase database
     */
    public static String MENU = "menu";

    /**
     * A simple constructor, invoked in StrawApplication.onCreate()
     * @param context
     */
    public DatabaseUtils(Context context) {
        this.context = context;
        this.connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.networkInfo = connectivityManager.getActiveNetworkInfo();
        this.firebase = new Firebase(StrawApplication.FIREBASEURL);
    }

    /**
     * Give a simple way to store data in the remote database
     * @param children : List of the nodes that must be entered before storing the data.
     * @param data : the actual data to store.
     * @return : return true if saving is possible, false otherwise.
     */
    public boolean saveData(ArrayList<String> children, String data) {
        if (this.networkInfo != null && this.networkInfo.isConnectedOrConnecting()) {
            String params[] = new String[children.size() + 1];
            children.toArray(params);
            params[params.length - 1] = data;
            new DatabaseAsyncTask().execute(params);
            return true;
        } else {
            Toast.makeText(this.context, R.string.NoNetwork, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    /**
     * Retrieve the menu of the restaurant from the Firebase database
     * @param restaurantName : the name of the restaurant of which we want to get the menu
     * @param menu : the internal representation of a complete menu (Plates + Drinks)
     */
    public void retrieveMenu(String restaurantName, final ArrayList[] menu) {
        //TO DO : check the availability of the network
        Firebase ref = this.firebase.child(MENU).child(restaurantName);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    JSONArray jsonArray;
                    if(dataSnapshot.getValue() == null) {
                        /**
                         * No data are available
                         */
                        Logger.d("Null value retrieved");
                        return;
                    }

                    try {
                        jsonArray = new JSONArray(dataSnapshot.getValue(String.class));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        jsonArray = null;
                    }

                    if(jsonArray != null) {
                        Menu.restoreMenu(jsonArray, menu);
                    }
                }
                /**
                 * If dataSnapshot is null, there is a problem, so don't do anything
                 */
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    /**
     * Store the menu of the restaurant in the Firebase database
     * @param restaurantName : the name of the restaurant, that will be used as the key for
     *                       storing the data.
     * @param data : the actual data to store.
     * @return : return true if saving is possible, false otherwise.
     */
    public boolean saveMenu(String restaurantName, String data) {
        ArrayList<String> children = new ArrayList<>();
        children.add(MENU);
        children.add(restaurantName);
        return this.saveData(children, data);
    }

    /**
     * Allows to perform the sending of data to the database in a secondary thread
     */
    private class DatabaseAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            Firebase ref = firebase;
            for (int i = 0; i < params.length - 1; i++) {
                ref = ref.child(params[i]);
            }
            ref.setValue(params[params.length - 1]);
            return null;
        }
    }
}
