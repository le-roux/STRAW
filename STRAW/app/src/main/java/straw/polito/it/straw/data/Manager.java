package straw.polito.it.straw.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;

import straw.polito.it.straw.utils.DistanceComparator;
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

    /**
     * Strings used as keys when storing a Manager object in the sharedPreferences.
     */
    public static final String SEATS_AVAILABLE = "SeatsAvailable";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String TELEPHONE = "telephone";
    public static final String ADDRESS = "address";
    public static final String RESTAURANT_NAME = "restaurantName";
    public static final String TYPE = "restaurantType";
    public static final String PHOTO = "photo";
    public static final String EMAIL_ADDRESS = "email";
    public static final String PRICE_MIN = "min";
    public static final String PRICE_MAX = "max";
    public static final String FOOD_TYPE = "foodType";

    public static final int BAR = 0;
    public static final int RESTAURANT = 1;
    public static final int CANTEEN = 2;
    public static final int TAKEAWAY = 3;

    public Manager() {
        this.reviews = new ArrayList<>();
    }

    public Manager(String man) {
        this.reviews = new ArrayList<>();
        try {
            JSONObject oj = new JSONObject(man);
            this.telephone = oj.getString(TELEPHONE);
            this.address = oj.getString(ADDRESS);
            this.res_name = oj.getString(RESTAURANT_NAME);
            this.res_type = oj.getString(TYPE);
            this.seats = oj.getInt(SEATS_AVAILABLE);
            this.image = oj.getString(PHOTO);
            this.email = oj.getString(EMAIL_ADDRESS);
            this.min_price = oj.getDouble(PRICE_MIN);
            this.max_price = oj.getDouble(PRICE_MAX);
            this.food_type = oj.getString(FOOD_TYPE);
            this.latitude = oj.getDouble(LATITUDE);
            this.longitude = oj.getDouble(LONGITUDE);
            try {
                JSONArray jarr = new JSONArray(oj.getString("reviews"));
                for(int i=0;i<jarr.length();i++){
                    this.reviews.add(new Review(jarr.getJSONObject(i).toString()));
                }
            } catch (Exception e){
                this.reviews = new ArrayList<>();
            }
        } catch (JSONException e) {
            Logger.d("Error creating the manager");
        }
    }

    @JsonIgnore
    public ArrayList<Review> getReviews() {
        return reviews;
    }

    @JsonIgnore
    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public void addReview(Review review) {
        this.reviews.add(review);
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

    /**
     * Compute the rate of the restaurant as the average of all the reviews rate.
     * @return : the rate of the restaurant.
     */
    @JsonIgnore
    public float getRate() {
        if (this.reviews.size() != 0) {
            float rate = 0;
            for (Review review : this.reviews) {
                rate += review.getRate();
            }
            return rate / this.reviews.size();
        } else
            return 0;
    }

    public String toJSONObject() {
        JSONObject oj = new JSONObject();
        try {
            oj.put(TELEPHONE, telephone);
            oj.put(ADDRESS, address);
            oj.put(RESTAURANT_NAME, res_name);
            oj.put(TYPE, res_type);
            oj.put(SEATS_AVAILABLE, seats);
            oj.put(PHOTO, image);
            oj.put(EMAIL_ADDRESS, email);
            oj.put(PRICE_MIN,min_price);
            oj.put(PRICE_MAX,max_price);
            oj.put(FOOD_TYPE,food_type);
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
            oj.put(TELEPHONE, telephone);
            oj.put(ADDRESS, address);
            oj.put(RESTAURANT_NAME, res_name);
            oj.put(TYPE, res_type);
            oj.put(SEATS_AVAILABLE, seats);
            oj.put(PHOTO, image);
            oj.put(EMAIL_ADDRESS, email);
            oj.put(PRICE_MIN,min_price);
            oj.put(PRICE_MAX,max_price);
            oj.put(FOOD_TYPE,food_type);
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

    public static Comparator<Manager> RatingComparator = new Comparator<Manager>() {
        @Override
        public int compare(Manager lhs, Manager rhs) {
            if (lhs.getRate() > rhs.getRate()) {
                return -1;
            } else if (lhs.getRate() == rhs.getRate()) {
                return 0;
            } else {
                return 1;
            }
        }
    };

    public static DistanceComparator getDistanceComparator(double userLatitude, double userLongitude) {
        return new DistanceComparator(userLatitude, userLongitude);
    }

}
