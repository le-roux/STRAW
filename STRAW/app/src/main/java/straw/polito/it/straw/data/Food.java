package straw.polito.it.straw.data;

import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sylvain on 01/04/2016.
 */
public abstract class Food {
    private String name;
    private double price;
    private String imageURI;
    public static final String NAME = "NAME";
    public static final String TYPE = "TYPE";
    public static final String PRICE = "PRICE";
    public static final String IMAGE_URI = "IMAGE_URI";

    public Food(String name, double price, String imageURI) {
        this.name = name;
        this.price = price;
        this.imageURI = imageURI;
    }

    public Food() {
        this.name = "Default";
        this.price = 0d;
        this.imageURI = null;
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

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public String getImageURI() {
        return this.imageURI;
    }

    public abstract String getDescription();

    public abstract void save(SharedPreferences.Editor editor);

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
                food.setPrice(jsonObject.getDouble(PRICE));
                food.setImageURI(jsonObject.getString(IMAGE_URI));
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            return food;
        } else {
            return null;
        }
    }

    public static Food create(String food_descriptor) {
        try {
            return (Food.create(new JSONObject(food_descriptor)));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
