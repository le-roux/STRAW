package straw.polito.it.straw.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.data.Menu;
import straw.polito.it.straw.data.Reservation;
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
    public static final String MENU = "menu";
    public static final String MANAGER = "manager";
    public static final String USER = "user";
    public static final String RESERVATION = "reservation";

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
                Logger.d(params[i]);
                ref = ref.child(params[i]);
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
    public boolean saveManagerProfile(Manager manager, String uid) {
        ArrayList<String> children = new ArrayList<>();
        children.add(MANAGER);
        children.add(uid);
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

    /**
     * Store a reservation in the database.
     * @param reservation : the reservation to store.
     * @return : return true if saving is possible, false otherwise.
     */
    public boolean saveReservation(Reservation reservation) {
        ArrayList<String> children = new ArrayList<>();
        children.add(RESERVATION);
        Logger.d("save reservation : " + reservation.getRestaurant().getRes_name());
        children.add(reservation.getRestaurant().getRes_name());
        children.add(reservation.getCustomer().getEmail());
        return this.saveData(children, reservation.toString());
    }

    /**
     * Retrieve a reservation from the Firebase database.
     * @param restaurantName : the name of the restaurant in which the reservation has been done.
     * @param customerEmail : the email address of the customer who did the reservation.
     * @return : the reservation retrieved or null if it's not possible to retrieve proper data.
     */
    public Reservation retrieveReservation(String restaurantName, String customerEmail) {
        String[] children = new String[3];
        children[0] = RESERVATION;
        children[1] = restaurantName;
        children[2] = customerEmail;
        RetrieveAsyncTask task = new RetrieveAsyncTask();
        task.execute(children);
        String data;
        try {
            data = task.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return Reservation.create(data);
    }

    /**
     * Retrieve all the reservations for a restaurant.
     * @param restaurantName : the name of the restaurant.
     * @return : An ArrayList of Reservation or null if it's not possible to retrieve proper data.
     */
    public ArrayList<Reservation> retrieveReservations(String restaurantName) {
        ArrayList<Reservation> reservations = new ArrayList<>();
        String[] children = new String[2];
        children[0] = RESERVATION;
        children[1] = restaurantName;
        RetrieveAsyncTask task = new RetrieveAsyncTask();
        task.execute(children);
        String data;
        try {
            data = task.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                reservations.add(Reservation.create(jsonArray.getString(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return reservations;

    }

    /**
     * Create a new user in the Firebase database
     * @param emailAddress : the login of the new user
     * @param password : the password of the new user
     * @return true if the creation succeeded, false otherwise
     */
    public void createUser(String emailAddress, String password) {
        CreateUserAsyncTask task = new CreateUserAsyncTask();
        String[] params = new String[2];
        params[0] = emailAddress;
        params[1] = password;
        task.execute(params);
    }

    /**
     * A simple AsyncTask that performs the creation of a new user in the Firebase database in
     * a secondary thread.
     */
    private class CreateUserAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(final String[] params) {
            firebase.createUser(params[0], params[1], new Firebase.ValueResultHandler<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> result) {
                    /**
                     * Display a message telling the user that everything worked fine
                     */
                    Toast.makeText(context, R.string.m_c, Toast.LENGTH_SHORT).show();
                    /**
                     * Store the profile in the database
                     */
                    String uid = (String)result.get("uid");
                    if (params[2].equals(SharedPreferencesHandler.MANAGER)) {
                        Manager manager = sharedPreferencesHandler.getCurrentManager();
                        saveManagerProfile(manager, uid);
                    }
                    else {
                        //TODO save user profile
                    }
                    /**
                     * Log in
                     */
                    logIn(params[0], params[1]);
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    Logger.d(firebaseError.getMessage());
                    Toast.makeText(context, R.string.ErrorNetwork, Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }
    }

    /**
     * Try to authenticate the user according to it's login and password.
     * @param emailAddress : the login of the requested account
     * @param password : the password of the requested account
     */
    public void logIn(String emailAddress, String password) {
        String[] params = new String[2];
        params[0] = emailAddress;
        params[1] = password;
        LogInAsyncTask task = new LogInAsyncTask();
        task.execute(params);
    }

    /**
     * A simple AsyncTask that performs the authentication of the users in a secondary thread.
     */
    private class LogInAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            firebase.authWithPassword(params[0], params[1], new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    Toast.makeText(context, R.string.log_in, Toast.LENGTH_SHORT).show();
                    final String uid = authData.getUid();
                    firebase.child(USER).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                User user = dataSnapshot.getValue(User.class);
                                //TODO : save it in sharedPreferences and launch proper activity
                            } else {
                                firebase.child(MANAGER).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Manager manager = dataSnapshot.getValue(Manager.class);
                                        //TODO : save it in sharedPreferences and launch proper activity
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                    //TODO : Launch the proper activity
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    Toast.makeText(context, R.string.error_log_in, Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }
    }
}
