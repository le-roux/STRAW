package straw.polito.it.straw.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;

/**
 * Created by Sylvain on 29/04/2016.
 *
 * Provide
 */
public class DatabaseUtils {
    private Firebase firebase;
    private Context context;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    public static String MENU = "menu";

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
     * @return : return true if saving is possible, false otherwise
     */
    public boolean saveData(ArrayList<String> children, String data) {
        if (this.networkInfo != null && this.networkInfo.isConnectedOrConnecting()) {
            Firebase ref = this.firebase;
            for (String child : children) {
                ref = ref.child(child);
            }
            ref.setValue(data);
            return true;
        } else {
            Toast.makeText(this.context, R.string.NoNetwork, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public boolean saveMenu(String restaurantName, String data) {
        ArrayList<String> children = new ArrayList<>();
        children.add(MENU);
        children.add(restaurantName);
        return this.saveData(children, data);
    }

    private class MenuAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //TO DO
            return null;
        }
    }
}

