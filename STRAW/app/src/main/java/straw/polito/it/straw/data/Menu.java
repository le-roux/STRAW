package straw.polito.it.straw.data;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Sylvain on 22/04/2016.
 */
public class Menu
{
    public static final int PLATES = 0;
    public static final int DRINKS = 1;

    public static void restoreData(JSONArray jsonArray, ArrayList[] menu) {
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

    /**
     * Structure of the record :
     * JSONArray
     * [0] --> number of plates
     * [1]--[numberOfPlates] --> plates
     * [numberOfPlates + 1] --> number of drinks
     * [numberOfPlates + 2]--[numberOfPlates + numberOfDrinks + 1] --> drinks
     * @return A JSONArray following this structure and containing the menu of the restaurant
     */
    public static JSONArray saveData(ArrayList[] menu) {
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
}
