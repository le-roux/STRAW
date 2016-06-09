package straw.polito.it.straw.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Andres Camilo Jimenez on 03/05/2016.
 */
public class Review {

    private String user;
    private String restaurant;
    private float rate;
    private String description;

    public static final String REVIEW = "review";

    //Necessary for firebase to build instances of this class
    public Review() {
        this("",0,"","");
    }

    public Review(String user,float rate, String description,String restaurant) {
        this.user = user;
        this.rate = rate;
        this.restaurant = restaurant;
        this.description = description;
    }

    public Review(String review){
        try {
            JSONObject jo = new JSONObject(review);
            user = jo.get("user").toString();
            restaurant = jo.get("restaurant").toString();
            rate = Float.valueOf(String.valueOf(jo.get("rate")));
            description = jo.getString("desc");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Review(JSONObject jo){
        try {
            user = jo.get("user").toString();
            restaurant = jo.get("restaurant").toString();
            rate = Float.valueOf(String.valueOf(jo.get("rate")));
            description = jo.getString("desc");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public String toString() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("user",user);
            jo.put("rate",rate);
            jo.put("restaurant",restaurant);
            jo.put("desc",description);
            return jo.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
    public JSONObject toJSONObject() {
        JSONObject jo =new JSONObject();
        try {
            jo.put("user",user);
            jo.put("rate",rate);
            jo.put("restaurant",restaurant);
            jo.put("desc",description);
            return jo;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
