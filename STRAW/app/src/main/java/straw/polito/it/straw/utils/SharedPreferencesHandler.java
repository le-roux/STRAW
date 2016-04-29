package straw.polito.it.straw.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import straw.polito.it.straw.data.Manager;

/**
 * Created by Sylvain on 29/04/2016.
 */
public class SharedPreferencesHandler {

    //Key used for indexing the current manager in the sharedPreferences
    public static String MANAGER = "Manager";

    private SharedPreferences sharedPreferences;

    public SharedPreferencesHandler(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * @return : the manager stored in the sharedPreferences if available
     */
    public Manager getCurrentManager() {
        String description = this.sharedPreferences.getString(MANAGER, "");
        if (description.equals("")) {
            return null;
        } else {
            return new Manager(description);
        }
    }
}
