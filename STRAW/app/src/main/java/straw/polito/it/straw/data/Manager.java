package straw.polito.it.straw.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;

import straw.polito.it.straw.utils.Logger;

/**
 * Created by Andres Camilo Jimenez on 03/04/2016.
 */
public class Manager {

    private String telephone;
    private String address;
    private double latitude;
    private double longitude;
    private String res_name;
    private String res_type;
    private int seats;
    private double min_price;
    private double max_price;
    private String food_type;
    private String image;
    private String email;
    private ArrayList<Review> reviews;

    public static final String SEATS_AVAILABLE = "SeatsAvailable";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    public static final String TYPE = "Type";
    public static final int BAR = 0;
    public static final int RESTAURANT = 1;
    public static final int CANTEEN = 2;
    public static final int TAKEAWAY = 3;

    public Manager() {
    }

    public Manager(String man) {
        this.reviews = new ArrayList<>();
        try {
            JSONObject oj = new JSONObject(man);
            telephone = (String) oj.get("tel");
            address = (String) oj.get("addr");
            res_name = (String) oj.get("r_n");
            res_type = (String) oj.get("r_t");
            seats = (int) oj.get("seats");
            image = (String) oj.get("photo");
            email = (String) oj.get("email");
            min_price =oj.getDouble("min");
            max_price =oj.getDouble("max");
            food_type =oj.getString("food");
            this.latitude = oj.getDouble(LATITUDE);
            this.longitude = oj.getDouble(LONGITUDE);
            JSONArray jarr = new JSONArray(oj.getString("reviews"));
            for(int i=0;i<jarr.length();i++){
                reviews.add(new Review(jarr.getJSONObject(i).toString()));
            }
        } catch (JSONException e) {
            Logger.d("Error creating the manager");
        }
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public String getRes_name() {
        return res_name;
    }

    public void setRes_name(String res_name) {
        this.res_name = res_name;
    }

    public String getRes_type() {
        return res_type;
    }

    public void setRes_type(String res_type) {
        this.res_type = res_type;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public double getMin_price() {
        return min_price;
    }

    public void setMin_price(double min_price) {
        this.min_price = min_price;
    }

    public double getMax_price() {
        return max_price;
    }

    public void setMax_price(double max_price) {
        this.max_price = max_price;
    }

    public String getFood_type() {
        return food_type;
    }

    public void setFood_type(String food_type) {
        this.food_type = food_type;
    }

    public String toJSONObject() {
        JSONObject oj = new JSONObject();
        try {
            oj.put("tel", telephone);
            oj.put("addr", address);
            oj.put("r_n", res_name);
            oj.put("r_t", res_type);
            oj.put("seats", seats);
            oj.put("photo", image);
            oj.put("email", email);
            oj.put("min",min_price);
            oj.put("max",max_price);
            oj.put("food",food_type);
            oj.put(LATITUDE, this.latitude);
            oj.put(LONGITUDE, this.longitude);
            JSONArray jarr = new JSONArray();

            if (this.reviews != null) {
                for (Review r : reviews) {
                    jarr.put(r.toJSONObject());
                }
                oj.put("reviews", jarr);
            }
            return oj.toString();
        } catch (JSONException e) {
            Logger.d("Error storing the manager");
        }
        return null;
    }


    public JSONObject toJSONObjectTrans() {
        JSONObject oj = new JSONObject();
        try {
            oj.put("tel", telephone);
            oj.put("addr", address);
            oj.put("r_n", res_name);
            oj.put("r_t", res_type);
            oj.put("seats", seats);
            oj.put("photo", image);
            oj.put("email", email);
            oj.put("min",min_price);
            oj.put("max",max_price);
            oj.put("food",food_type);
            oj.put(LATITUDE, this.latitude);
            oj.put(LONGITUDE, this.longitude);
            JSONArray jarr = new JSONArray();

            if (this.reviews != null) {
                for (Review r : reviews) {
                    jarr.put(r.toJSONObject());
                }
                oj.put("reviews", jarr);
            }
            return oj;
        } catch (JSONException e) {
            Logger.d("Error storing the manager");
        }
        return null;
    }
    public static Comparator<Manager> PriceComparator = new Comparator<Manager>() {
        @Override
        public int compare(Manager m1, Manager m2) {
            double p1 = m1.getMin_price();
            double p2 = m2.getMin_price();
            return Double.compare(p1,p2);
        }
    };


}

