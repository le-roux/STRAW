package straw.polito.it.straw.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import straw.polito.it.straw.R;
import straw.polito.it.straw.RestaurantFilter;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.activities.ProfileManagerActivity;
import straw.polito.it.straw.activities.SearchActivity;
import straw.polito.it.straw.adapter.FoodExpandableAdapter;
import straw.polito.it.straw.adapter.ReservationAdapter;
import straw.polito.it.straw.adapter.RestaurantListAdapter;
import straw.polito.it.straw.data.Drink;
import straw.polito.it.straw.data.Food;
import straw.polito.it.straw.data.Friend;
import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.data.Menu;
import straw.polito.it.straw.data.Plate;
import straw.polito.it.straw.data.Reservation;
import straw.polito.it.straw.data.Review;
import straw.polito.it.straw.data.User;

/**
 * Created by Sylvain on 29/04/2016.
 */
public class DatabaseUtils {
    private Firebase firebase;
    private Context context;
    private SharedPreferencesHandler sharedPreferencesHandler;

    /**
     * Names of the first-level nodes in the Firebase database
     */
    public static final String MENU = "menu";
    public static final String MANAGER = "manager";
    public static final String USER = "user";
    public static final String RESERVATIONS = "reservations";
    public static final String RESTAURANT_RESERVATIONS = "restaurantReservations";
    public static final String RESTAURANTS = "restaurants";
    public static final String NAMECHECK = "restaurantsName";
    public static final String PLATES = "plates";
    public static final String DRINKS = "drinks";
    public static final String REVIEWS = "reviews";
    public static final String FRIENDS = "friends";

    /**
     * A simple constructor, invoked in StrawApplication.onCreate()
     *
     * @param context
     */
    public DatabaseUtils(Context context, SharedPreferencesHandler sharedPreferencesHandler) {
        this.context = context;
        this.sharedPreferencesHandler = sharedPreferencesHandler;
        this.firebase = new Firebase(StrawApplication.FIREBASEURL);
    }

    /**
     * Store the profile of a manager in the Firebase database
     *
     * @param manager : the manager profile to save
     */
    public void saveManagerProfile(Manager manager, String uid, String password, boolean logIn, ProgressDialog dialog) {
        //TODO save in sharedPreferences here
        SaveManagerAsyncTask task = new SaveManagerAsyncTask(uid, password, logIn, dialog);
        task.execute(manager);
    }

    /**
     * Store the profile of a manager in the Firebase database.
     * The manager must be authenticated because it's uid will be retrieved according to this
     * authentication.
     *
     * @param manager : The manager profile to store.
     */
    public void saveManagerProfile(Manager manager) {
        saveManagerProfile(manager, null, null, false, null);
    }


    private class SaveManagerAsyncTask extends AsyncTask<Manager, Void, Void> {

        /**
         * The uid of the current manager. It's used as the key to store the profile in the
         * Firebase database.
         */
        private String uid;
        private String password;
        private boolean changeActivity;
        private ProgressDialog dialog;

        public SaveManagerAsyncTask(String uid, String password, boolean changeActivity, ProgressDialog dialog) {
            this.uid = uid;
            this.password = password;
            this.changeActivity = changeActivity;
            this.dialog = dialog;
        }

        @Override
        protected Void doInBackground(final Manager... params) {
            /**
             * The uid is unknown, retrieve the one of the current authenticated user.
             */
            if (this.uid == null) {
                AuthData auth = firebase.getAuth();
                if (auth == null) {
                    /**
                     * There is not current authenticated user
                     */
                    if (dialog != null)
                        dialog.dismiss();
                    return null;
                }
                /**
                 * Get the uid of the current authenticated user
                 */
                this.uid = auth.getUid();
            }

            /**
             * Check if the restaurant name is already used
             */
            Firebase ref = firebase.child(NAMECHECK).child(params[0].getRes_name());
            ref.setValue(this.uid, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase resultRef) {
                    if (firebaseError == null) {
                        /**
                         * This restaurant name is available, store the data in the database.
                         */
                        if (dialog != null)
                            dialog.setMessage(context.getString(R.string.StoringData) + " 0/2");
                        /**
                         * Save the data in the manager part (with uid as key)
                         */
                        Firebase ref = firebase.child(MANAGER).child(uid);
                        ref.setValue(params[0], new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase resultRef) {
                                if (firebaseError == null) {
                                    /**
                                     * No error, continue the sequence
                                     */
                                    if (dialog != null) {
                                        String message = context.getString(R.string.StoringData) + " 1/2";
                                        dialog.setMessage(message);
                                    }
                                    /**
                                     * Store the data in the restaurant part (with restaurant name
                                     * as key).
                                     */
                                    Firebase ref = firebase.child(RESTAURANTS).child(params[0].getRes_name());
                                    ref.setValue(params[0], new Firebase.CompletionListener() {
                                        @Override
                                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                            if (firebaseError == null) {
                                                /**
                                                 * No error, continue the sequence
                                                 */
                                                if (dialog != null) {
                                                    dialog.dismiss();
                                                }
                                                if (changeActivity) {
                                                    /**
                                                     * Launch the profile activity
                                                     */
                                                    Intent intent = new Intent(context, ProfileManagerActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    context.startActivity(intent);
                                                }
                                            } else {
                                                /**
                                                 * An error occurred when trying to save the profile
                                                 * in the restaurant part.
                                                 */
                                                String message = firebaseError.getMessage();
                                                if (dialog != null)
                                                    dialog.setMessage(message);
                                                else
                                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                                                /**
                                                 * Erase the data already stored :
                                                 *  1 - data in the manager part
                                                 *  2 - entry in the namecheck part
                                                 */
                                                Firebase ref = firebase.child(MANAGER).child(uid);
                                                ref.setValue(null, new Firebase.CompletionListener() {
                                                    @Override
                                                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                                        Firebase ref = firebase.child(NAMECHECK).child(params[0].getRes_name());
                                                        ref.setValue(null, new Firebase.CompletionListener() {
                                                            @Override
                                                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                                                if (dialog != null)
                                                                    dialog.dismiss();
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                } else {
                                    /**
                                     * An error occurred when trying to save the profile in the
                                     * manager part.
                                     */
                                    String message = firebaseError.getMessage();
                                    if (dialog != null)
                                        dialog.setMessage(message);
                                    else
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                                    /**
                                     * Erase the data already stored :
                                     *  1 - entry in the namecheck part
                                     */
                                    Firebase ref = firebase.child(NAMECHECK).child(params[0].getRes_name());
                                    ref.setValue(null, new Firebase.CompletionListener() {
                                        @Override
                                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                            if (dialog != null)
                                                dialog.dismiss();
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        if (password != null) {
                            if (dialog != null)
                                dialog.setMessage(context.getString(R.string.RewindChange));
                            firebase.removeUser(params[0].getEmail(), password, new Firebase.ResultHandler() {
                                @Override
                                public void onSuccess() {
                                    Logger.d("account removed");
                                    if (dialog != null)
                                        dialog.dismiss();
                                    Toast.makeText(context, R.string.NameAlreadyUsed, Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onError(FirebaseError firebaseError) {
                                    if (dialog != null)
                                        dialog.dismiss();
                                    Toast.makeText(context, R.string.NameAlreadyUsed, Toast.LENGTH_LONG).show();
                                    Toast.makeText(context, firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            if (dialog != null)
                                dialog.dismiss();
                        }
                    }
                }
            });
            return null;
        }
    }

    /**
     * Store the profile of a customer in the Firebase database
     *
     * @param user : the profile to store
     * @return : return true
     */
    public void saveCustomerProfile(User user, String uid, boolean logIn, ProgressDialog dialog) {
        SaveCustomerAsyncTask task = new SaveCustomerAsyncTask(uid, logIn, dialog);
        task.execute(user);
    }

    private class SaveCustomerAsyncTask extends AsyncTask<User, Void, Void> {

        private String uid;
        private boolean changeActivity;
        private ProgressDialog dialog;

        public SaveCustomerAsyncTask(String uid, boolean changeActivity, ProgressDialog dialog) {
            this.uid = uid;
            this.changeActivity = changeActivity;
            this.dialog = dialog;
        }

        @Override
        protected Void doInBackground(final User... params) {
            if (this.uid == null) {
                AuthData auth = firebase.getAuth();
                if (auth == null) {
                    /**
                     * There is no current authenticated user
                     */
                    if (dialog != null)
                        dialog.dismiss();
                    Toast.makeText(context, context.getString(R.string.NoAuth), Toast.LENGTH_LONG).show();
                    return null;
                } else {
                    this.uid = auth.getUid();
                }
            }

            /**
             * Store the data in the database
             */
            Firebase ref = firebase.child(USER).child(this.uid);
            ref.setValue(params[0], new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError == null) {
                        /**
                         * Everything worked well
                         */
                        if (dialog != null)
                            dialog.dismiss();
                        if (changeActivity) {
                            Intent intent = new Intent(context, SearchActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                        else {
                            Toast.makeText(context, context.getResources().getString(R.string.ProfileCreationSuccessful), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        /**
                         * An error occurred
                         */
                        if (dialog != null)
                            dialog.dismiss();
                        Toast.makeText(context, firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
            return null;
        }
    }

    /**
     * Create a new user in the Firebase database
     *
     * @param emailAddress : the login of the new user
     * @param password     : the password of the new user
     * @return true if the creation succeeded, false otherwise
     */
    public void createUser(String emailAddress, String password, String type, ProgressDialog dialog) {
        CreateUserAsyncTask task = new CreateUserAsyncTask(dialog);
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
        private ProgressDialog dialog;

        public CreateUserAsyncTask(ProgressDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        protected Void doInBackground(final String[] params) {
            Logger.d("asking for creation");
            firebase.createUser(params[0], params[1], new Firebase.ValueResultHandler<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> result) {
                    Logger.d("creation successful");
                    /**
                     * Display a message telling the user that everything worked fine
                     */
                    dialog.setMessage(context.getResources().getString(R.string.m_c));
                    /**
                     * Authenticate the user with the newly created account
                     */
                    firebase.authWithPassword(params[0], params[1], new Firebase.AuthResultHandler() {
                        @Override
                        public void onAuthenticated(AuthData authData) {
                            /**
                             * Store the profile in the database and launch the next activity
                             */
                            String uid = authData.getUid();
                            if (params[2].equals(SharedPreferencesHandler.MANAGER)) {
                                Manager manager = sharedPreferencesHandler.getCurrentManager();
                                saveManagerProfile(manager, uid, params[1], true, dialog);
                            } else {
                                User user = sharedPreferencesHandler.getCurrentUser();
                                saveCustomerProfile(user, uid, true, dialog);
                            }
                        }
                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            if (dialog != null)
                                dialog.dismiss();
                            Toast.makeText(context, R.string.error_log_in, Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    /**
                     * Error when trying to create a new account
                     */
                    if (dialog != null)
                        dialog.dismiss();
                    Toast.makeText(context, firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }
    }

    /**
     * Try to authenticate the user according to it's login and password and retrieve it's profile
     * if asked.
     *
     * @param emailAddress    : the login of the requested account
     * @param password        : the password of the requested account
     * @param retrieveProfile : true if the profile must be downloaded from the database, false if
     *                        must first be searched in the sharedPreferences.
     *                        Be sure that the local version is up-to-date when putting false
     */
    public void logIn(String emailAddress, String password, boolean retrieveProfile, ProgressDialog dialog) {
        Logger.d("Log in requested");
        dialog.setMessage(context.getResources().getString(R.string.LoggingIn));
        String[] params = new String[2];
        params[0] = emailAddress;
        params[1] = password;
        LogInAsyncTask task = new LogInAsyncTask(retrieveProfile, dialog);
        task.execute(params);
    }

    /**
     * A simple AsyncTask that performs the authentication of the users in a secondary thread
     * and launch the proper Profile activity.
     */
    private class LogInAsyncTask extends AsyncTask<String, Void, Void> {

        private ProgressDialog dialog;
        private boolean retrieveProfile;

        public LogInAsyncTask(boolean retrieveProfile, ProgressDialog dialog) {
            this.retrieveProfile = retrieveProfile;
            this.dialog = dialog;
        }

        @Override
        protected Void doInBackground(final String... params) {
            Logger.d("Ask for login");
            firebase.authWithPassword(params[0], params[1], new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    String text = context.getString(R.string.log_in) + " - " + context.getString(R.string.RetrievingData);
                    dialog.setMessage(text);
                    if (!retrieveProfile) {
                        Logger.d("Take local profile");
                        /**
                         * No need to retrieve the profile from the remote database.
                         * First, try to see if it's the current user and if not, try with the
                         * current manager.
                         */
                        User user = sharedPreferencesHandler.getCurrentUser();
                        if (user != null && user.getEmail().equals(params[0])) {
                            dialog.dismiss();
                            Intent intent = new Intent(context, SearchActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            return;
                        } else {
                            Manager manager = sharedPreferencesHandler.getCurrentManager();
                            if (manager != null && manager.getEmail().equals(params[0])) {
                                dialog.dismiss();
                                Intent intent = new Intent(context, ProfileManagerActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                                return;
                            } else {
                                Toast.makeText(context, R.string.NoMatchingProfile, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    /**
                     * It's necessary to download the profile from the remote database.
                     */
                    final String uid = authData.getUid();
                    firebase.child(USER).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                /**
                                 * It's a customer (and not a manager). Retrieve the profile.
                                 * Friends lists will be retrieved manually just after for
                                 * simplicity.
                                 */
                                User user = dataSnapshot.getValue(User.class);
                                /**
                                 * Retrieve the friends list
                                 */
                                for (DataSnapshot data : dataSnapshot.child(FRIENDS).getChildren()) {
                                    Friend friend = data.getValue(Friend.class);
                                    user.addFriend(friend);
                                }
                                /**
                                 * Store the retrieved profile in sharedPreferences for next accesses
                                 */
                                sharedPreferencesHandler.storeCurrentUser(user.toString());
                                /**
                                 * Launch the next activity : SearchActivity
                                 */
                                Intent intent = new Intent(context, SearchActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                dialog.dismiss();
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
                                            dialog.dismiss();
                                            context.startActivity(intent);
                                        } else {
                                            Toast.makeText(context, R.string.error_log_in, Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {
                                        Logger.d("error cancelled : " + firebaseError.getMessage());
                                        dialog.dismiss();
                                        Toast.makeText(context, R.string.ErrorNetwork, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            Logger.d("error cancelled2 : " + firebaseError.getMessage());
                            dialog.dismiss();
                            Toast.makeText(context, R.string.ErrorNetwork, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    Logger.d("error log in : " + firebaseError.getMessage());
                    dialog.dismiss();
                    Toast.makeText(context, R.string.error_log_in, Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }
    }

    /**
     * Store the menu of the restaurant in the Firebase database
     *
     * @param restaurantName : the name of the restaurant, that will be used as the key for
     *                       storing the data.
     * @param menu           : the actual data to store.
     */
    public void saveMenu(String restaurantName, ArrayList[] menu, ProgressDialog dialog) {
        /**
         * Launch the storage
         */
        SaveMenuAsyncTask task = new SaveMenuAsyncTask(restaurantName, dialog);
        task.execute(menu);
    }

    private class SaveMenuAsyncTask extends AsyncTask<ArrayList[], Void, Void> {

        private String restaurantName;
        private ProgressDialog dialog;

        public SaveMenuAsyncTask(String restaurantName, ProgressDialog dialog) {
            this.dialog = dialog;
            this.restaurantName = restaurantName;
        }

        @Override
        protected Void doInBackground(ArrayList[]... params) {
            Firebase ref = firebase.child(MENU).child(this.restaurantName).child(PLATES);
            ArrayList<Plate> plates = params[0][Menu.PLATES];
            final int plateNb = plates.size();
            for (Plate plate : plates)
                ref.push().setValue(plate, new ProgressCompletionListener(context.getString(R.string.SavingPlates), plateNb));

            ref = firebase.child(MENU).child(this.restaurantName).child(DRINKS);
            ArrayList<Drink> drinks = params[0][Menu.DRINKS];
            int drinkNb = drinks.size();
            for (Drink drink : drinks)
                ref.push().setValue(drink, new ProgressCompletionListener(context.getString(R.string.SavingDrinks), drinkNb));
            dialog.dismiss();
            return null;
        }

        /**
         * A simple completion listener that displays the progress in the ProgressDialog
         */
        private class ProgressCompletionListener implements Firebase.CompletionListener {

            private String message;
            private int maxValue;
            private int progress;

            public ProgressCompletionListener(String message, int maxValue) {
                this.message = message;
                this.maxValue = maxValue;
            }

            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError == null) {
                    /**
                     * No error
                     */
                    progress++;
                    String text = message + ' ' + progress + '/' + maxValue;
                    dialog.setMessage(text);
                }
                //TODO in case of error ???
            }
        }
    }

    public void saveFood(String restaurantName, Food food, ProgressDialog dialog) {
        SaveFoodAsyncTask task = new SaveFoodAsyncTask(restaurantName, dialog);
        task.execute(food);
    }

    private class SaveFoodAsyncTask extends AsyncTask<Food, Void, Void> {

        private String restaurantName;
        private ProgressDialog dialog;

        public SaveFoodAsyncTask(String restaurantName, ProgressDialog dialog) {
            this.restaurantName = restaurantName;
            this.dialog = dialog;
        }

        @Override
        protected Void doInBackground(Food... params) {
            Logger.d("saving food");
            Firebase ref;
            /**
             * Go in the proper subnode according to the type of food
             */
            if (params[0].getClass().equals(Plate.class))
                ref = firebase.child(MENU).child(this.restaurantName).child(PLATES);
            else
                ref = firebase.child(MENU).child(this.restaurantName).child(DRINKS);
            /**
             * Store/update the data
             */
            ref.child(params[0].getName()).setValue(params[0], new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    dialog.dismiss();
                }
            });
            return null;
        }
    }

    public void removeFood(String restaurantName, int type, String foodName) {
        String[] children = new String[3];
        children[0] = restaurantName;
        if (type == Menu.PLATES)
            children[1] = PLATES;
        else
            children[1] = DRINKS;
        children[2] = foodName;
        RemoveFoodAsyncTask task = new RemoveFoodAsyncTask();
        task.execute(children);
    }

    private class RemoveFoodAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            Firebase ref = firebase.child(MENU);
            for (String child : params)
                ref = ref.child(child);
            ref.setValue(null);
            return null;
        }
    }

    /**
     * Retrieve the menu of the restaurant from the Firebase database
     *
     * @param restaurantName : the name of the restaurant of which we want to get the menu
     * @param adapter           : the adapter that displays the menu
     */
    public void retrieveMenu(String restaurantName, FoodExpandableAdapter adapter) {
        /**
         * Retrieve the string representation from the database
         */
        RetrieveMenuAsyncTask task = new RetrieveMenuAsyncTask(restaurantName, adapter);
        ArrayList<Food>[] menu = new ArrayList[2];
        menu[Menu.PLATES] = (ArrayList<Food>)adapter.getGroup(Menu.PLATES);
        menu[Menu.DRINKS] = (ArrayList<Food>)adapter.getGroup(Menu.DRINKS);
        task.execute(menu);
    }

    public void retrieveMenu(String restaurantName, ArrayList<Food>[] menu) {
        RetrieveMenuAsyncTask task = new RetrieveMenuAsyncTask(restaurantName);
        task.execute(menu);
    }

    private class RetrieveMenuAsyncTask extends AsyncTask<ArrayList<Food>[], Void, Void> {

        private String restaurantName;
        private FoodExpandableAdapter adapter;

        public RetrieveMenuAsyncTask(String restaurantName) {
            this.restaurantName = restaurantName;
            this.adapter = null;
        }

        public RetrieveMenuAsyncTask(String restaurantName, FoodExpandableAdapter adapter) {
            this.restaurantName = restaurantName;
            this.adapter = adapter;
        }

        @Override
        protected Void doInBackground(ArrayList<Food>[]... params) {
            final ArrayList<Food> plates = (ArrayList<Food>)params[0][Menu.PLATES];
            plates.clear();
            if (this.adapter != null)
                this.adapter.notifyDataSetChanged();
            Firebase ref = firebase.child(MENU).child(this.restaurantName).child(PLATES);
            ref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    plates.add(dataSnapshot.getValue(Plate.class));
                    if (adapter != null)
                        adapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            final ArrayList<Food> drinks = (ArrayList<Food>)params[0][Menu.DRINKS];
            drinks.clear();
            if (adapter != null)
                adapter.notifyDataSetChanged();
            ref = firebase.child(MENU).child(this.restaurantName).child(DRINKS);
            ref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    drinks.add(dataSnapshot.getValue(Drink.class));
                    if (adapter != null)
                        adapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
            return null;
        }
    }

    /**
     * Retrieve the list of all the managers
     *
     * @
     * @return : An ArrayList of Manager or null if it's not possible to retrieve proper data.
     */
    public void retrieveRestaurantList(RestaurantListAdapter adapter, RestaurantListAdapter adapterForFilter, ProgressDialog dialog, RestaurantFilter filter) {
        RetrieveRestaurantsAsyncTask task = new RetrieveRestaurantsAsyncTask(dialog, filter);
        task.execute(adapter,adapterForFilter);
    }

    private class RetrieveRestaurantsAsyncTask extends AsyncTask<RestaurantListAdapter, Void, Void> {

        private ProgressDialog dialog;
        private RestaurantFilter filter;

        public RetrieveRestaurantsAsyncTask(ProgressDialog dialog, RestaurantFilter filter) {
            this.dialog = dialog;
            this.filter = filter;
        }

        @Override
        protected Void doInBackground(final RestaurantListAdapter... params) {
            Firebase ref = firebase.child(RESTAURANTS);
            ref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dialog != null)
                        dialog.dismiss();
                    Manager restaurant = dataSnapshot.getValue(Manager.class);
                    DataSnapshot reviews = dataSnapshot.child(REVIEWS);
                    for (DataSnapshot review : reviews.getChildren()) {
                        restaurant.addReview(review.getValue(Review.class));
                    }
                    params[0].getList().add(restaurant);
                    params[1].getList().add(restaurant);
                    params[0].notifyDataSetChanged();
                    params[1].notifyDataSetChanged();
                    filter.filter();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    params[0].getList().remove(dataSnapshot.getValue(Manager.class));
                    params[1].getList().remove(dataSnapshot.getValue(Manager.class));
                    params[0].getList().add(dataSnapshot.getValue(Manager.class));
                    params[1].getList().add(dataSnapshot.getValue(Manager.class));
                    params[0].notifyDataSetChanged();
                    params[1].notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    params[0].getList().remove(dataSnapshot.getValue(Manager.class));
                    params[1].getList().remove(dataSnapshot.getValue(Manager.class));
                    params[0].notifyDataSetChanged();
                    params[1].notifyDataSetChanged();
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
            return null;
        }
    }

    /**
     * Add a review to a restaurant
     * @param restaurantName : the name of the restaurant
     * @param review : the review to add
     */
    public void addReview(String restaurantName, Review review) {
        AddReviewAsyncTask task = new AddReviewAsyncTask(restaurantName);
        task.execute(review);
    }

    /**
     * A simple AsyncTask used to save a review in the database
     */
    private class AddReviewAsyncTask extends AsyncTask<Review, Void, Void> {

        private String restaurantName;

        public AddReviewAsyncTask(String restaurantName) {
            this.restaurantName = restaurantName;
        }

        @Override
        protected Void doInBackground(Review... params) {
            Firebase ref = firebase.child(RESTAURANTS).child(this.restaurantName).child(REVIEWS);
            ref.push().setValue(params[0], new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError != null) {
                        Toast.makeText(context, firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
            return null;
        }
    }

    /**
     * Allows to retrieve a String from the Firebase database in a secondary thread.
     */
    private class RetrieveAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            Firebase ref = firebase;
            for (String string : params)
                ref = ref.child(string);
            String data = "";
            ref.addValueEventListener(new RetrieverListener(data));
            return data;
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
     * Retrieve the full profile of a manager (identified by the manager email address) from
     * Firebase database.
     *
     * @param managerEmail : used as the key to find the manager profile.
     * @return : The manager profile retrieved from the database, or null if it's not possible
     * to retrieve proper data.
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
     * Retrieve the full profile of a user (identified by the user email address) from the
     * Firebase database.
     *
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
     *
     * @param reservation : the reservation to store.
     */
    public void saveReservation(Reservation reservation) {
        Logger.d("save reservation : " + reservation.getRestaurant());
        StoreReservationAsyncTask task = new StoreReservationAsyncTask();
        task.execute(reservation);
    }

    private class StoreReservationAsyncTask extends AsyncTask<Reservation, Void, Void> {

        @Override
        protected Void doInBackground(final Reservation... params) {
            /**
             * Store the reservation in global reservations list.
             */
            Firebase ref = firebase.child(RESERVATIONS);
            final String id = ref.push().getKey();
            params[0].setId(id);
            ref.child(id).setValue(params[0], new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebaseRef) {
                    if (firebaseError == null) {
                        /**
                         * Store the reservation id in the restaurant reservations list.
                         */
                        Firebase ref = firebase.child(RESTAURANT_RESERVATIONS).child(params[0].getRestaurant());
                        ref.push().setValue(id);
                        /**
                         * Store the reservation id in the customer reservations list.
                         */
                        ref = firebase.child(USER).child(firebase.getAuth().getUid());
                        ref.child(RESERVATIONS).push().setValue(id);
                        Toast.makeText(context, R.string.ReservationSent, Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(context, firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            return null;
        }
    }

    /**
     * Retrieve all the reservations for a restaurant.
     *
     * @param restaurantName : the name of the restaurant.
     * @return : An ArrayList of Reservation or null if it's not possible to retrieve proper data.
     */
    public void retrieveReservations(String restaurantName, ReservationAdapter adapter, ProgressDialog dialog) {
        RetrieveReservationsAsyncTask task = new RetrieveReservationsAsyncTask(restaurantName, dialog);
        task.execute(adapter);
    }

    private class RetrieveReservationsAsyncTask extends AsyncTask<ReservationAdapter, Void, Void> {

        private String restaurantName;
        private ProgressDialog dialog;

        public RetrieveReservationsAsyncTask(String restaurantName, ProgressDialog dialog) {
            this.restaurantName = restaurantName;
            this.dialog = dialog;
        }

        @Override
        protected Void doInBackground(final ReservationAdapter... params) {
            Firebase ref = firebase.child(RESTAURANT_RESERVATIONS).child(this.restaurantName);
            params[0].getReservationList().clear();
            params[0].notifyDataSetChanged();
            ref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    String reservationId = dataSnapshot.getValue(String.class);
                    Firebase ref = firebase.child(RESERVATIONS).child(reservationId);
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Reservation reservation = dataSnapshot.getValue(Reservation.class);
                            params[0].getReservationList().add(reservation);
                            params[0].notifyDataSetChanged();
                            if (dialog != null)
                                dialog.dismiss();
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
            return null;
        }
    }

    public void updateReservation(Map<String, Object> attributes) {
        UpdateReservationAsyncTask task = new UpdateReservationAsyncTask();
        task.execute(attributes);
    }

    public void updateReservation(Map<String, Object> attributes, ProgressDialog dialog) {
        UpdateReservationAsyncTask task = new UpdateReservationAsyncTask(dialog);
        task.execute(attributes);
    }

    public void updateReservationStatus(String id, int status) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(Reservation.ID, id);
        map.put(Reservation.STATUS, status);
        updateReservation(map);
    }

    private class UpdateReservationAsyncTask extends AsyncTask<Map<String, Object>, Void, Void> {

        private ProgressDialog dialog;

        public UpdateReservationAsyncTask() {
            this.dialog = null;
        }

        public UpdateReservationAsyncTask(ProgressDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        protected Void doInBackground(Map<String, Object>... params) {
            String id = (String)params[0].get(Reservation.ID);
            if (id == null || id.equals("")) {
                if (dialog != null)
                    dialog.dismiss();
                return null;
            }
            Firebase ref = firebase.child(RESERVATIONS).child(id);
            ref.updateChildren(params[0], new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (dialog != null)
                        dialog.dismiss();
                    if (firebaseError != null)
                        Toast.makeText(context, firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            return null;
        }
    }

    public void addFriend(Friend friend) {
        String id = firebase.getAuth().getUid();
        Firebase ref = firebase.child(USER).child(id).child(FRIENDS);
        ref.push().setValue(friend, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null)
                    Toast.makeText(context, firebaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
