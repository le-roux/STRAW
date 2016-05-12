package straw.polito.it.straw.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;

import com.firebase.client.Firebase;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.utils.Logger;

/**
 * Created by Sylvain on 22/04/2016.
 */
public class Menu
{
    public static final int PLATES = 0;
    public static final int DRINKS = 1;

    public static final String MENU = "Menu";

    /**
     *
     * @param jsonArray : the array containing the saved data
     * @param menu : the ArrayLists to fill/complete with new data
     */
    public static void restoreMenu(JSONArray jsonArray, ArrayList[] menu) {
        try {
            int index = 0;
            //Get plates number
            int platesNumber = jsonArray.getInt(index++);
            //Get plates
            for (int i = 0; i < platesNumber; i++) {
                menu[PLATES].add(Food.create(jsonArray.get(index++).toString()));
            }
            //Get drinks number
            int drinksNumber = jsonArray.getInt(index++);
            //Get drinks
            for (int i = 0; i < drinksNumber; i++) {
                menu[DRINKS].add(Food.create(jsonArray.get(index++).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Food> getPlates(JSONArray jsonArray) {
        ArrayList<Food> plates = new ArrayList<>();
        try {
            int index = 0;
            //Get plates number
            int platesNumber = jsonArray.getInt(index++);
            //Get plates
            for (int i = 0; i < platesNumber; i++) {
                plates.add(Food.create(jsonArray.get(index++).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return plates;
    }

    public static ArrayList<Food> getDrinks(JSONArray jsonArray) {
        ArrayList<Food> drinks = new ArrayList<>();
        try {
            int index = 0;
            //Get plates number
            int platesNumber = jsonArray.getInt(index++);
            //Get drinks number
            index += platesNumber;
            int drinksNumber = jsonArray.getInt(index++);
            for (int i = 0; i < drinksNumber; i++) {
                drinks.add(Food.create(jsonArray.get(index++).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return drinks;
    }

    public static ArrayList<Food> convertFood(JSONArray jsonArray) {
        ArrayList<Food> foods = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                foods.add(Food.create(jsonArray.get(i).toString()));
            }
        } catch (JSONException e) {
            return foods;
        }
        return foods;
    }

    /**
     * Structure of the record :
     * JSONArray
     * [0] --> number of plates
     * [1]--[numberOfPlates] --> plates
     * [numberOfPlates + 1] --> number of drinks
     * [numberOfPlates + 2]--[numberOfPlates + numberOfDrinks + 1] --> drinks
     * @return A JSONArray following this structure and containing the menu of the restaurant
     */
    public static JSONArray saveMenu(ArrayList[] menu) {
        JSONArray jsonArray = new JSONArray();
        int index = 0;
        try {
            jsonArray.put(index, menu[PLATES].size());

            index++;
            for (int i = 0; i < menu[PLATES].size(); i++) {
                jsonArray.put(index, menu[PLATES].get(i).toString());
                index++;
            }
            jsonArray.put(index++, menu[DRINKS].size());
            for (int i = 0; i < menu[DRINKS].size(); i++) {
                jsonArray.put(index++, menu[DRINKS].get(i).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public static void saveMenuInSharedPreferences(Context context, ArrayList[] menu) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(MENU, saveMenu(menu).toString());
        editor.commit();
    }

    public static JSONArray getMenuFromSharedPreferences(Context context) {
        JSONArray jsonArray;
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            jsonArray = new JSONArray(sharedPreferences.getString(Menu.MENU, ""));
        } catch (JSONException e) {
            e.printStackTrace();
            jsonArray = new JSONArray();
        }
        return jsonArray;
    }
}
