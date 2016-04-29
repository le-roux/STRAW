package straw.polito.it.straw.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import straw.polito.it.straw.data.Manager;

/**
 * Created by Sylvain on 29/04/2016.
 */
public class SharedPreferencesHandler {
    public static String MANAGER = "Manager";

    private SharedPreferences sharedPreferences;

    public SharedPreferencesHandler(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Manager getCurrentManager() {
        String description = this.sharedPreferences.getString(MANAGER, "");
        if (description.equals("")) {
            return null;
        } else {
            return new Manager(description);
        }
    }
}
