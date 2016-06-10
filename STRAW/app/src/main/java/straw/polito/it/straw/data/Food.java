package straw.polito.it.straw.data;

import android.content.SharedPreferences;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sylvain on 01/04/2016.
 *
 * This class describes the features common to all the types of food.
 *
 * Known subclasses : Drink, Plate
 */
public abstract class Food {
    private String name;
    private double price;
    private String image;

    //Keys for storing and retrieving the data in any dictionary
    public static final String NAME = "NAME";
    public static final String TYPE = "TYPE";
    public static final String PRICE = "PRICE";
    public static final String IMAGE = "IMAGE";

    //Default values
    public static final String DEFAULT_NAME ="Default";
    public static final double DEFAULT_PRICE = 0d;

    /**
     * Complete constructor
     * @param name
     * @param price
     * @param image
     */
    public Food(String name, double price, String image) {
        this.name = name;
        this.price = price;
        this.image = image;
    }

    /**
     * Default constructor
     */
    public Food() {
        this.name = "Default";
        this.price = 0d;
        this.image = null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return this.price;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return this.image;
    }

    /**
     * Returns a description of the food element, to use in the ListView
     * @return A string describing the content of the food element
     */
    @JsonIgnore
    public abstract String getDescription();

    /**
     * Store the data describing the food element in the shared preferences
     * @param editor The SharedPreferences editor in which to store the data
     */
    public abstract void save(SharedPreferences.Editor editor);

    /**
     * Create a food object using the data provided
     * @param jsonObject The data to use to create the object
     * @return The food object created
     */
    public static Food create(JSONObject jsonObject) {
        String type = null;
        try {
            type = jsonObject.getString(TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Food food;
        if (type.equals(Drink.DRINK)) {
            food = Drink.create(jsonObject);
        } else if(type.equals(Plate.PLATE)) {
            food = Plate.create(jsonObject);
        } else {
            return null;
        }
        if(food != null) {
            try {
                food.setName(jsonObject.getString(NAME));
            } catch (JSONException e) {
                food.setName(DEFAULT_NAME);
            }
            try {
                food.setPrice(jsonObject.getDouble(PRICE));
            } catch (JSONException e) {
                food.setPrice(DEFAULT_PRICE);
            }
            try {
                food.setImage(jsonObject.getString(IMAGE));
            } catch (JSONException e) {
                food.setImage(null);
            }
            return food;
        } else {
            return null;
        }
    }

    /**
     * Create a food object using the data provided
     * @param food_descriptor
     * @return
     */
    public static Food create(String food_descriptor) {
        try {
            return (Food.create(new JSONObject(food_descriptor)));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
