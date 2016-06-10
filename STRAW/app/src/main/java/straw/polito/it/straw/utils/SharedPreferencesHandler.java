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
    public static final String MANAGERLIST = "ManagerList";
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
     * Save the list of managers in a JSONArray in the sharedPreferences
     * @param description : a string describing the JSONarray
     */
    public void storeListManager(String description) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(MANAGERLIST, description);
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

    /**
     * Temporary, it must move later on the database
     */
    public Area[] getAreaList() {
        Area[] areas = new Area[2];
        areas[0] = new Area("Polito", 45.0345, 7.3942);
        areas[1] = new Area("Polimi", 45.2840, 9.1338);
        return areas;
    }
    public void removeMemory(){
        if(this.sharedPreferences.contains("remember")){
            this.sharedPreferences.edit().remove("remember").apply();
        }

    }
}
