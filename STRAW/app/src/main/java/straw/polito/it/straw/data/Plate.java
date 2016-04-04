package straw.polito.it.straw.data;

import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sylvain on 01/04/2016.
 */
public class Plate extends Food {

    /**
        List of the ingredients present in the plate
     */
    private String ingredients;
    private boolean vegan;
    private boolean glutenFree;
    public static final String PLATE = "PLATE";
    public static final String VEGAN = "VEGAN";
    public static final String GLUTEN_FREE = "GLUTEN_FREE";
    public static final String INGREDIENTS = "INGREDIENTS";

    public Plate() {
        super();
        this.ingredients = "";
        this.vegan = false;
        this.glutenFree = false;
    }

    public Plate(String name, float price, String imageUri, boolean vegan, boolean glutenFree) {
        super(name, price, imageUri);
        this.vegan = vegan;
        this.glutenFree = glutenFree;
        this.ingredients = "";
    }

        public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getIngredients() {
        return this.ingredients;
    }

    public void setVegan(boolean vegan) {
        this.vegan = vegan;
    }

    public boolean isVegan() {
        return this.vegan;
    }

    public void setGlutenFree (boolean glutenFree) {
        this.glutenFree = glutenFree;
    }

    public boolean isGlutenFree() {
        return this.glutenFree;
    }

    @Override
    public String getDescription() {
        String description = this.ingredients;
        if (this.isGlutenFree())
            description += " GLUTEN FREE ";
        if (this.isVegan())
            description += "VEGAN";
        return description;
    }

    @Override
    public void save (SharedPreferences.Editor editor) {
        editor.putString(this.getName(), this.toString());
        editor.commit();
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt(Food.TYPE, PLATE);
            jsonObject.putOpt(Food.NAME, this.getName());
            jsonObject.put(Food.PRICE, this.getPrice());
            jsonObject.putOpt(Food.IMAGE_URI, this.getImageURI());
            jsonObject.put(VEGAN, this.isVegan());
            jsonObject.put(GLUTEN_FREE, this.isGlutenFree());
            jsonObject.putOpt(INGREDIENTS, this.ingredients);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static Plate create(JSONObject jsonObject) {
        Plate plate = new Plate();
        try {
            plate.setGlutenFree(jsonObject.getBoolean(GLUTEN_FREE));
            plate.setVegan(jsonObject.getBoolean(VEGAN));
            plate.setIngredients(jsonObject.getString(INGREDIENTS));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return plate;
    }
}
