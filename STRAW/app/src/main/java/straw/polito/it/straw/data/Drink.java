package straw.polito.it.straw.data;

import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sylvain on 01/04/2016.
 */
public class Drink extends Food {
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

    public Drink(String name, double price, String imageUri, double volume) {
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
    public void save(SharedPreferences.Editor editor) {
        editor.putString(this.getName(), this.toString());
        editor.commit();
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt(Food.TYPE, DRINK);
            jsonObject.putOpt(Food.NAME, this.getName());
            jsonObject.put(Food.PRICE, this.getPrice());
            jsonObject.putOpt(Food.IMAGE, this.getImage());
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
