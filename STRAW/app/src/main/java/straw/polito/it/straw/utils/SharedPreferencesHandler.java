package straw.polito.it.straw.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import straw.polito.it.straw.data.Manager;
import straw.polito.it.straw.data.User;

/**
 * Created by Sylvain on 29/04/2016.
 */
public class SharedPreferencesHandler {

    //Key used for indexing the current manager in the sharedPreferences
    public static final String MANAGER = "Manager";
    public static final String USER = "User";

    private SharedPreferences sharedPreferences;
    private Context context;

    public SharedPreferencesHandler(Context context) {
        this.context = context;
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

    /**
     * Save the profile of the current manager in the sharedPreferences
     * @param description : the stringified version of the JSONObject describing the manager profile
     */
    public void storeCurrentManager(String description) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(MANAGER, description);
        editor.apply();
    }

    /**
     *
     * @return : the profile of the current user as saved in the sharedPreferences.
     */
    public User getCurrentUser() {
        String description = this.sharedPreferences.getString(USER, "");
        if (description.equals("")) {
            return null;
        } else {
            return new User(description);
        }
    }

    /**
     * Save the profile of the user in the sharedPreferences
     * @param description : the stringified version of the JSONObject describing the user profile.
     */
    public void storeCurrentUser(String description) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(USER, description);
        editor.apply();
    }
}
