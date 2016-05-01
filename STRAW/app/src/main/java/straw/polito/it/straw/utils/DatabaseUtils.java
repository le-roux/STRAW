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
import java.util.concurrent.ExecutionException;

import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.data.Menu;
import straw.polito.it.straw.data.User;

/**
 * Created by Sylvain on 29/04/2016.
 */
public class DatabaseUtils {
    private Firebase firebase;
    private Context context;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private SharedPreferencesHandler sharedPreferencesHandler;

    /**
     * Names of the first-level nodes in the Firebase database
     */
    public static String MENU = "menu";
    public static String MANAGER = "manager";
    public static String USER = "user";

    /**
     * A simple constructor, invoked in StrawApplication.onCreate()
     * @param context
     */
    public DatabaseUtils(Context context, SharedPreferencesHandler sharedPreferencesHandler) {
        this.context = context;
        this.connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.networkInfo = connectivityManager.getActiveNetworkInfo();
        this.sharedPreferencesHandler = sharedPreferencesHandler;
        this.firebase = new Firebase(StrawApplication.FIREBASEURL);
    }

    /**
     * Give a simple way to store data in the remote database
     * @param children : List of the nodes that must be entered before storing the data.
     * @param data : the actual data to store.
     * @return : return true if saving is possible, false otherwise.
     */
    public boolean saveData(ArrayList<String> children, String data) {
        String params[] = new String[children.size() + 1];
        children.toArray(params);
        params[params.length - 1] = data;
        new StoreAsyncTask().execute(params);
        return true;
    }

    /**
     * Allows to perform the sending of data to the database in a secondary thread
     */
    private class StoreAsyncTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... params) {
            /**
             * Check if the network is available
             */
            if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
                String[] result = new String[2];
                result[0] = MANAGER;
                result[1] = params[params.length - 1];
                return result;
            }

            Firebase ref = firebase;
            for (int i = 0; i < params.length - 1; i++) {
                ref = ref.child(params[i]);
                Logger.d(params[i]);
            }
            ref.setValue(params[params.length - 1]);
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                /**
                 * Impossible to send the data to the remote database
                 * Store it locally in the sharedPreferences
                 */
                Toast.makeText(context, R.string.NoNetwork, Toast.LENGTH_LONG).show();
                if (result[0].equals(MANAGER)) {
                    sharedPreferencesHandler.storeCurrentManager(result[1]);
                }
            }
        }
    }

    /**
     * Allows to retrieve a String from the Firebase database in a secondary thread.
     */
    private class RetrieveAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            /**
             * Check if the network is available
             */
            if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
                publishProgress();
                return null;
            } else {
                Firebase ref = firebase;
                for (String string : params)
                    ref = ref.child(string);
                String data = "";
                ref.addValueEventListener(new RetrieverListener(data));
                return data;
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            Toast.makeText(context, R.string.NoNetwork, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * A simple class used to retrieve a String from the Firebase database.
     */
    private class RetrieverListener implements ValueEventListener {

        private String data;

        public RetrieverListener(String data) {
            this.data = data;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                data = dataSnapshot.getValue(String.class);
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }

    /**
     * Retrieve the menu of the restaurant from the Firebase database
     * @param restaurantName : the name of the restaurant of which we want to get the menu
     * @param menu : the internal representation of a complete menu (Plates + Drinks)
     */
    public void retrieveMenu(String restaurantName, final ArrayList[] menu) {
        String children[] = new String[2];
        children[0] = MENU;
        children[1] = restaurantName;

        /**
         * Retrieve the string representation from the database
         */
        RetrieveAsyncTask task = new RetrieveAsyncTask();
        task.execute(children);
        String data;
        try {
            data = task.get();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, R.string.NoNetwork, Toast.LENGTH_LONG).show();
            return;
        }

        if (data == null) {
            Toast.makeText(context, R.string.ErrorNetwork, Toast.LENGTH_LONG).show();
            return;
        }

        /**
         * Use the string representation to re-create the menu
         */
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(data);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonArray = null;
        }

        if (jsonArray != null) {
            Menu.restoreMenu(jsonArray, menu);
        }
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
     * Store the profile of a manager in the Firebase database
     * @param manager : the manager profile to save
     * @return : return true if saving is possible, false otherwise.
     */
    public boolean saveManagerProfile(Manager manager) {
        ArrayList<String> children = new ArrayList<>();
        children.add(MANAGER);
        children.add(manager.getEmail());
        return this.saveData(children, manager.toJSONObject());
    }

    /**
     * Retrieve the full profile of a manager (identified by the manager email address) from
     *                      Firebase database.
     * @param managerEmail : used as the key to find the manager profile.
     * @return : The manager profile retrieved from the database, or null if it's not possible
     *          to retrieve proper data.
     */
    public Manager retrieveManagerProfile(String managerEmail) {
        String children[] = new String[2];
        children[0] = MANAGER;
        children[1] = managerEmail;
        RetrieveAsyncTask task = new RetrieveAsyncTask();
        task.execute(children);
        String data;
        try {
            data = task.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return new Manager(data);
    }

    /**
     * Store the profile of a customer in the Firebase database
     * @param user : the profile to store
     * @return : return true if saving is possible, false otherwise.
     */
    public boolean saveUserProfile(User user) {
        ArrayList<String> children = new ArrayList<>();
        children.add(USER);
        children.add(user.getEmail());
        return this.saveData(children, user.toString());
    }

    /**
     * Retrieve the full profile of a user (identified by the user email address) from the
     *              Firebase database.
     * @param userEmail : used as the key to find the profile.
     * @return : the profile, or null if it's not possible to retrieve proper data.
     */
    public User retrieveUserProfile(String userEmail) {
        String[] children = new String[2];
        children[0] = USER;
        children[1] = userEmail;
        RetrieveAsyncTask task = new RetrieveAsyncTask();
        task.execute(children);
        String data;
        try {
            data = task.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return new User(data);
    }


}
