package straw.polito.it.straw;

import android.app.Application;

import com.firebase.client.Firebase;

import straw.polito.it.straw.utils.DatabaseUtils;
import straw.polito.it.straw.utils.SharedPreferencesHandler;

/**
 * Created by Sylvain on 29/04/2016.
 */
public class StrawApplication extends Application {

    public static String FIREBASEURL = "https://straw.firebaseio.com/";
    public static String serverAPIKey = "AIzaSyBB7AlcpdUG9MoOJ1e4vemmPklMjElanKU";
    public static String senderID = "478407627881";
    private DatabaseUtils databaseUtils;
    private SharedPreferencesHandler sharedPreferencesHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        this.sharedPreferencesHandler = new SharedPreferencesHandler(getApplicationContext());
        this.databaseUtils = new DatabaseUtils(getApplicationContext(), this.sharedPreferencesHandler);
    }

    public DatabaseUtils getDatabaseUtils() {
        return this.databaseUtils;
    }

    public SharedPreferencesHandler getSharedPreferencesHandler() {
        return this.sharedPreferencesHandler;
    }
}
