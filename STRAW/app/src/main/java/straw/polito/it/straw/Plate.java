package straw.polito.it.straw;

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
    private ArrayList<String> ingredients;
    private boolean vegan;
    private boolean glutenFree;
    public static final String PLATE = "PLATE";
    public static final String VEGAN = "VEGAN";
    public static final String GLUTEN_FREE = "GLUTEN_FREE";
    public static final String INGREDIENTS = "INGREDIENTS";
    private static final int FIELDS_NB = 6;

    public Plate() {
        super();
        this.ingredients = new ArrayList<String>();
        this.vegan = false;
        this.glutenFree = false;
    }

    public Plate(String name, float price, String imageUri, boolean vegan, boolean glutenFree) {
        super(name, price, imageUri);
        this.vegan = vegan;
        this.glutenFree = glutenFree;
        this.ingredients = new ArrayList<String>();
    }

    public void addIngredient(String ingredient) {
        this.ingredients.add(ingredient);
    }

    public void removeIngredient(String ingredient) {
        this.ingredients.remove(ingredient);
    }

    public String getIngredients() {
        String ingredients = "";
        for (String ingredient : this.ingredients) {
            ingredients += ingredient;
        }
        return ingredients;
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
        String description = "";
        for (String ingredient : this.ingredients) {
            description += ingredient;
            description += " ";
        }
        if (this.isGlutenFree())
            description += "GLUTEN FREE ";
        if (this.isVegan())
            description += "VEGAN ";
        return description;
    }

    @Override
    public void save (SharedPreferences.Editor editor, String id) {
        editor.putString(id, this.toString());
        editor.commit();
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("TYPE", "PLATE");
            jsonObject.putOpt("NAME", this.getName());
            jsonObject.put("PRICE", this.getPrice());
            jsonObject.putOpt("IMAGE_URI", this.getImageURI());
            jsonObject.put("VEGAN", this.isVegan());
            jsonObject.put("GLUTEN_FREE", this.isGlutenFree());
            for (int i = 0; i < this.ingredients.size(); i++) {
                jsonObject.putOpt(String.valueOf(i), this.ingredients.get(i));
            }
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
            for (int i = 0; i < jsonObject.length() - FIELDS_NB; i++) {
                plate.ingredients.add(jsonObject.getString(String.valueOf(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return plate;
    }
}
