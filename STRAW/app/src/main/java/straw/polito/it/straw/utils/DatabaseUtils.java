package straw.polito.it.straw.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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
import straw.polito.it.straw.activities.ProfileManagerActivity;
import straw.polito.it.straw.activities.ProfileUserActivity;
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
    public static final String RESERVATIONS = "reservations";
    public static final String RESTAURANTS = "restaurants";

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
     */
    public void saveMenu(String restaurantName, String data) {
        String[] children = new String[3];
        children[0] = MENU;
        children[1] = restaurantName;
        children[2] = data;
        SaveMenuAsyncTask task = new SaveMenuAsyncTask();
        task.execute(children);
        return;
    }

    private class SaveMenuAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            Firebase ref = firebase.child(params[0]).child(params[1]);
            ref.setValue(params[2]);
            return null;
        }
    }

    /**
     * Store the profile of a manager in the Firebase database
     * @param manager : the manager profile to save
     */
    public boolean saveManagerProfile(Manager manager, String uid, boolean wait) {
        SaveManagerAsyncTask task = new SaveManagerAsyncTask(uid);
        task.execute(manager);
        if (wait) {
            try {
                task.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            } catch (ExecutionException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Store the profile of a manager in the Firebase database.
     * The manager must be authenticated because it's uid will be retrieved according to this
     * authentication.
     * @param manager : The manager profile to store.
     */
    public void saveManagerProfile(Manager manager) {
        saveManagerProfile(manager, null, false);
    }


    private class SaveManagerAsyncTask extends AsyncTask<Manager, Void, Boolean> {

        /**
         * The uid of the current manager. It's used as the key to store the profile in the
         * Firebase database.
         */
        private String uid;

        public SaveManagerAsyncTask(String uid) {
            this.uid = uid;
        }

        @Override
        protected Boolean doInBackground(final Manager... params) {
            /**
             * The uid is unknown, retrieve it according to the current authenticated user.
             */
            if (this.uid == null) {
                this.uid = firebase.getAuth().getUid();
            }
            /**
             * Check if the restaurant name is already used
             */
            Firebase ref = firebase.child(RESTAURANTS).child(params[0].getRes_name());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Logger.d("actual storage");
                    if (dataSnapshot.exists()) {
                        Toast.makeText(context, R.string.NameAlreadyUsed, Toast.LENGTH_LONG).show();
                    } else {
                        /**
                         * The actual storage of the data
                         */
                        Firebase ref = firebase.child(MANAGER).child(uid);
                        ref.setValue(params[0]);
                        ref = firebase.child(RESTAURANTS).child(params[0].getRes_name());
                        ref.setValue(params[0]);
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Logger.d("error when checking if restaurant already exists : " + firebaseError.getMessage());
                }
            });
            Logger.d("return from saveManager");
            return null;
        }
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
     * @return : return true
     */
    public boolean saveUserProfile(User user, String uid) {
        SaveUserAsyncTask task = new SaveUserAsyncTask(uid);
        task.execute(user);
        return true;
    }

    private class SaveUserAsyncTask extends AsyncTask<User, Void, Void> {

        private String uid;

        public SaveUserAsyncTask(String uid) {
            this.uid = uid;
        }

        @Override
        protected Void doInBackground(User... params) {
            Firebase ref = firebase.child(USER).child(uid);
            ref.setValue(params[0]);
            return null;
        }
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
     * @return : return true
     */
    public boolean saveReservation(Reservation reservation) {
        ArrayList<String> children = new ArrayList<>();
        children.add(RESERVATIONS);
        Logger.d("save reservation : " + reservation.getRestaurant().getRes_name());
        children.add(reservation.getRestaurant().getRes_name());
        children.add(reservation.getCustomer().getEmail());
        StoreReservationAsyncTask task = new StoreReservationAsyncTask(reservation.getRestaurant().getRes_name(), reservation.getCustomer().getEmail());
        task.execute(reservation);
        return true;
    }

    private class StoreReservationAsyncTask extends AsyncTask<Reservation, Void, Void> {

        String restaurantName;
        String customerName;

        public StoreReservationAsyncTask(String restaurantName, String customerName) {
            this.restaurantName = restaurantName;
            this.customerName = customerName;
        }

        @Override
        protected Void doInBackground(Reservation... params) {
            Firebase ref = firebase.child(RESERVATIONS).child(restaurantName).child(customerName);
            ref.setValue(params[0]);
            return null;
        }
    }

    /**
     * Retrieve a reservation from the Firebase database.
     * @param restaurantName : the name of the restaurant in which the reservation has been done.
     * @param customerEmail : the email address of the customer who did the reservation.
     * @return : the reservation retrieved or null if it's not possible to retrieve proper data.
     */
    public Reservation retrieveReservation(String restaurantName, String customerEmail) {
        String[] children = new String[3];
        children[0] = RESERVATIONS;
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
        children[0] = RESERVATIONS;
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
    public void createUser(String emailAddress, String password, String type, ProgressBarFragment fragment) {
        if(fragment != null && fragment.isAdded())
            fragment.setText(R.string.AccountCreation);
        CreateUserAsyncTask task = new CreateUserAsyncTask(fragment);
        String[] params = new String[3];
        params[0] = emailAddress;
        params[1] = password;
        params[2] = type;
        task.execute(params);
    }

    /**
     * A simple AsyncTask that performs the creation of a new user in the Firebase database in
     * a secondary thread.
     */
    private class CreateUserAsyncTask extends AsyncTask<String, Void, Void> {
        private ProgressBarFragment fragment;

        public CreateUserAsyncTask(ProgressBarFragment fragment) {
            this.fragment = fragment;
        }

        @Override
        protected Void doInBackground(final String[] params) {
            Logger.d("asking for creation");
            firebase.createUser(params[0], params[1], new Firebase.ValueResultHandler<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> result) {
                    Logger.d("creation successfull");
                    /**
                     * Display a message telling the user that everything worked fine
                     */
                    fragment.setText(R.string.m_c);
                    /**
                     * Store the profile in the database
                     */
                    String uid = (String)result.get("uid");
                    if (params[2].equals(SharedPreferencesHandler.MANAGER)) {
                        Manager manager = sharedPreferencesHandler.getCurrentManager();
                        saveManagerProfile(manager, uid, true);
                        Logger.d("return from profile storage");
                    }
                    else {
                        User user = sharedPreferencesHandler.getCurrentUser();
                        saveUserProfile(user, uid);
                    }
                    /**
                     * Log in
                     */
                    logIn(params[0], params[1], false, fragment);
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    Logger.d("error creation user : " + firebaseError.getMessage());
                    fragment.dismiss();
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
    public void logIn(String emailAddress, String password, boolean retrieveProfile, ProgressBarFragment fragment) {
        Logger.d("Log in requested");
        if(fragment != null && fragment.isAdded())
            fragment.setText(R.string.LoggingIn);
        String[] params = new String[2];
        params[0] = emailAddress;
        params[1] = password;
        LogInAsyncTask task = new LogInAsyncTask(retrieveProfile, fragment);
        task.execute(params);
    }

    /**
     * A simple AsyncTask that performs the authentication of the users in a secondary thread
     * and launch the proper Profile activity.
     */
    private class LogInAsyncTask extends AsyncTask<String, Void, Void> {

        private ProgressBarFragment fragment;
        private boolean retrieveProfile;

        public LogInAsyncTask(boolean retrieveProfile, ProgressBarFragment fragment) {
            this.retrieveProfile = retrieveProfile;
            this.fragment = fragment;
        }

        @Override
        protected Void doInBackground(final String... params) {
            Logger.d("Ask for login");
            firebase.authWithPassword(params[0], params[1], new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    Logger.d("Login successfull");
                    String text = context.getResources().getString(R.string.log_in) + " - " + context.getResources().getString(R.string.RetrievingData);
                    fragment.setText(text);
                    if (retrieveProfile) {
                        final String uid = authData.getUid();
                        firebase.child(USER).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    /**
                                     * It's a customer (and not a manager). Retrieve the profile, store
                                     * it in the sharedPreferences for future access and launch
                                     * the proper activity.
                                     */
                                    User user = dataSnapshot.getValue(User.class);
                                    sharedPreferencesHandler.storeCurrentUser(user.toString());
                                    Intent intent = new Intent(context, ProfileUserActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                } else {
                                    /**
                                     * It's a manager. Retrieve the profile, store it in the
                                     * sharedPreferences and launch the proper activity.
                                     */
                                    firebase.child(MANAGER).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                Manager manager = dataSnapshot.getValue(Manager.class);
                                                sharedPreferencesHandler.storeCurrentManager(manager.toJSONObject());
                                                Intent intent = new Intent(context, ProfileManagerActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                context.startActivity(intent);
                                            } else {
                                                Toast.makeText(context, R.string.error_log_in, Toast.LENGTH_LONG).show();
                                                fragment.dismiss();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {
                                            Logger.d("error cancelled : " + firebaseError.getMessage());
                                            fragment.dismiss();
                                            Toast.makeText(context, R.string.ErrorNetwork, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                                Logger.d("error cancelled2 : " + firebaseError.getMessage());
                                fragment.dismiss();
                                Toast.makeText(context, R.string.ErrorNetwork, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Logger.d("Take local profile");
                        fragment.dismiss();
                        /**
                         * No need to retrieve the profile from the remote database.
                         * First, try to see if it's the current user and if not, try with the
                         * current manager.
                         */
                        User user = sharedPreferencesHandler.getCurrentUser();
                        if (user != null && user.getEmail().equals(params[0])) {
                            Intent intent = new Intent(context, ProfileUserActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        } else {
                            Manager manager = sharedPreferencesHandler.getCurrentManager();
                            if (manager != null && manager.getEmail().equals(params[0])) {
                                Intent intent = new Intent(context, ProfileManagerActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            } else {
                                Toast.makeText(context, R.string.NoMatchingProfile, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    Logger.d("error log in : " + firebaseError.getMessage());
                    fragment.dismiss();
                    Toast.makeText(context, R.string.error_log_in, Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }
    }
}
