package straw.polito.it.straw;

import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sylvain on 01/04/2016.
 */
public class Drink extends food {
    /**
     * The volume (in Liter) of the drink
     */
    private double volume;
    public static final String DRINK = "DRINK";
    public static final String VOLUME = "VOLUME";

    public Drink() {
        super();
        this.volume = 1f;
    }

    public Drink(String name, double price, String imageUri, double Volume) {
        super(name, price, imageUri);
        this.volume = volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getVolume()
    {
        return this.volume;
    }

    @Override
    public String getDescription() {
        String description = String.valueOf(this.volume) + "L";
        return description;
    }

    @Override
    public void save(SharedPreferences.Editor editor, String id) {
        editor.putString(id, this.toString());
        editor.commit();
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt(food.TYPE, DRINK);
            jsonObject.putOpt(food.NAME, this.getName());
            jsonObject.put(food.PRICE, this.getPrice());
            jsonObject.putOpt(food.IMAGE_URI, this.getImageURI());
            jsonObject.put(VOLUME, this.getVolume());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static Drink create(JSONObject jsonObject) {
        Drink drink = new Drink();
        try {
            drink.setVolume(jsonObject.getDouble(VOLUME));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return drink;
    }
}
