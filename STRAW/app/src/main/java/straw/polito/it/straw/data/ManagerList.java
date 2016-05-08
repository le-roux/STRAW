package straw.polito.it.straw.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by tibo on 01/05/2016.
 */


public class ManagerList {


    public static final String LISTMAN = "ListMan";

    public static void restoreListMan(JSONArray jsonArray, ArrayList listman) {
        try {
            int index = 0;
            int ManagerNumber = jsonArray.getInt(index++);
            for (int i = 0; i < ManagerNumber; i++) {
                listman.add(new Manager(jsonArray.get(index++).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static ArrayList<Manager> getManager(JSONArray jsonArray) {
        ArrayList<Manager> list = new ArrayList<>();
        try {
            int index = 0;
            int ManagersNumber = jsonArray.getInt(index++);
            for (int i = 0; i < ManagersNumber; i++) {
                list.add(new Manager(jsonArray.get(index++).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
    public static JSONArray saveListMan(ArrayList listman) {
        JSONArray jsonArray = new JSONArray();
        int index = 0;
        try {
            jsonArray.put(index, listman.size());

            index++;
            for (int i = 0; i < listman.size(); i++) {
                jsonArray.put(index, listman.get(i).toString());
                index++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }
    public static void saveListManInSharedPreferences(Context context, ArrayList listman) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(LISTMAN, saveListMan(listman).toString());
        editor.commit();
    }
    public static JSONArray getListManFromSharedPreferences(Context context) {
        JSONArray jsonArray;
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            jsonArray = new JSONArray(sharedPreferences.getString(ManagerList.LISTMAN, ""));
        } catch (JSONException e) {
            e.printStackTrace();
            jsonArray = new JSONArray();
        }
        return jsonArray;
    }
}
