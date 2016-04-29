package straw.polito.it.straw;

import android.app.Application;
import android.net.ConnectivityManager;

import com.firebase.client.Firebase;

import straw.polito.it.straw.utils.DatabaseUtils;

/**
 * Created by Sylvain on 29/04/2016.
 */
public class StrawApplication extends Application {

    public static String FIREBASEURL = "https://vivid-fire-6651.firebaseio.com/";
    private DatabaseUtils databaseUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        this.databaseUtils = new DatabaseUtils(getApplicationContext());
    }

    public DatabaseUtils getDatabaseUtils() {
        return this.databaseUtils;
    }
}
