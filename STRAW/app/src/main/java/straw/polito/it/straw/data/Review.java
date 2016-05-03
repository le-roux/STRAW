package straw.polito.it.straw.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Andres Camilo Jimenez on 03/05/2016.
 */
public class Review {

    private User user;
    private Manager restaurant;
    private float rate;
    private String description;

    public Review(User user, Manager restaurant, float rate, String description) {
        this.user = user;
        this.restaurant = restaurant;
        this.rate = rate;
        this.description = description;
    }

    public Review(String review){
        try {
            JSONObject jo=new JSONObject(review);
            user=new User(jo.get("user").toString());
            restaurant=new Manager(jo.get("restaurant").toString());
            rate=Float.valueOf(String.valueOf(jo.get("rate")));
            description=jo.getString("desc");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        JSONObject jo =new JSONObject();
        try {
            jo.put("user",user.toString());
            jo.put("restaurant",restaurant.toJSONObject());
            jo.put("rate",rate);
            jo.put("desc",description);
            return jo.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Manager getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Manager restaurant) {
        this.restaurant = restaurant;
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
