package straw.polito.it.straw.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import straw.polito.it.straw.utils.Logger;

/**
 * Created by AndresPC on 15/04/2016.
 */
public class User {
    private String email;
    private String university;
    private String diet;
    private String tokenGCM;
    private String type;
    private int prefTimeMinutes;
    private int prefTimeHour;
    private String image;
    private String phoneNumber;
    private ArrayList<Friend> friends;
    private ArrayList<Review> reviews;
    /**
     * List of the IDs of the reservations done by this customer.
     */
    private ArrayList<Reservation> reservations;

    /**
     * Keys used to store the data in the sharedPreferences
     */
    public static final String FRIENDS_LIST = "friendsList";
    public static final String EMAIL_ADDRESS = "email";
    public static final String UNIVERSITY = "university";
    public static final String DIET = "diet";
    public static final String CUSTOMER_TYPE = "type";
    public static final String PREFERRED_TIME = "prefTime";
    public static final String IMAGE = "image";
    public static final String RESERVATIONS = "reservations";
    public static final String REVIEWS = "reviews";
    public static final String TOKEN_GCM = "token_gcm";
    public static final String PREF_TIME_HOUR = "prefTimeHour";
    public static final String PREF_TIME_MINUTES = "prefTimeMinutes";

    /**
     * Basic constructor used by Firebase to create an instance of User when downloading it.
     */
    public User() {
        this.friends = new ArrayList<>();
        this.reservations = new ArrayList<>();
        this.reviews = new ArrayList<>();
        this.prefTimeHour = 11;
        this.prefTimeMinutes = 0;
    }

    public User(String s){
        this.friends = new ArrayList<>();
        this.reservations = new ArrayList<>();
        this.reviews = new ArrayList<>();
        try {
            JSONObject jo = new JSONObject(s);
            this.email = jo.getString(EMAIL_ADDRESS);
            this.university = jo.getString(UNIVERSITY);
            this.diet = jo.getString(DIET);
            this.type = jo.getString(CUSTOMER_TYPE);
            this.image = jo.getString(IMAGE);
            this.tokenGCM = jo.getString(TOKEN_GCM);
            try {
                this.prefTimeHour = jo.getInt(PREF_TIME_HOUR);
                this.prefTimeMinutes = jo.getInt(PREF_TIME_MINUTES);
            } catch (JSONException e) {
                //Default values
                this.prefTimeHour = 11;
                this.prefTimeMinutes =0;
            }

            /**
             * Retrieve the friends list.
             */
            JSONArray jsonArray = jo.getJSONArray(FRIENDS_LIST);
            for (int i = 0; i < jsonArray.length(); i++) {
                this.friends.add(new Friend(jsonArray.get(i).toString()));
            }

            /**
             * Retrieve the reservations list.
             */
            if (jo.has(RESERVATIONS)) {
                JSONArray reservationsArray = jo.getJSONArray(RESERVATIONS);
                for (int i = 0; i < reservationsArray.length(); i++)
                    this.reservations.add(Reservation.create(reservationsArray.getString(i)));
            }
            if (jo.has(REVIEWS)) {
                JSONArray rev = jo.getJSONArray(REVIEWS);
                for (int i = 0; i < rev.length(); i++)
                    this.reviews.add(new Review(rev.get(i).toString()));
            }
        } catch (JSONException e) {
            Logger.d("Creating user error "+e.getMessage());
        }
    }

    /**
     * @return : the stringified version of the JSON object representing this user.
     */
    @Override
    public String toString() {
        JSONObject jo=new JSONObject();
        try {
            jo.put(EMAIL_ADDRESS, this.email);
            jo.put(UNIVERSITY, this.university);
            jo.put(DIET, this.diet);
            jo.put(CUSTOMER_TYPE, this.type);
            jo.put(IMAGE, this.image);
            jo.put(TOKEN_GCM,this.tokenGCM);
            jo.put(PREF_TIME_HOUR, this.prefTimeHour);
            jo.put(PREF_TIME_MINUTES, this.prefTimeMinutes);

            /**
             * Store the friends list.
             */
            JSONArray jsonArray = new JSONArray();
            for (Friend friend : this.friends) {
                jsonArray.put(friend.toJSONObject());
            }
            jo.put(FRIENDS_LIST, jsonArray);

            /**
             * Store the reservation(s) list
             */
            JSONArray reservationsArray = new JSONArray();
            for (Reservation reservation : this.reservations)
                reservationsArray.put(reservation.toString());
            jo.put(RESERVATIONS, reservationsArray);

            JSONArray reviewsArray = new JSONArray();
            for (Review rev : this.reviews)
                reviewsArray.put(rev.toString());
            jo.put(REVIEWS, reviewsArray);

            return jo.toString();
        } catch (JSONException e) {
            Logger.d("Error when converting user into string");
            return null;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getDiet() {
        return diet;
    }

    public void setDiet(String diet) {
        this.diet = diet;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPrefTimeMinutes() {
        return this.prefTimeMinutes;
    }

    public void setPrefTimeMinutes(int minutes) {
        this.prefTimeMinutes = minutes;
    }

    public int getPrefTimeHour() {
        return this.prefTimeHour;
    }

    public void setPrefTimeHour(int hour) {
        this.prefTimeHour = hour;
    }

    public String getImage() {
        return image;
    }

    public String getTokenGCM() {
        return tokenGCM;
    }

    public void setTokenGCM(String tokenGCM) {
        this.tokenGCM = tokenGCM;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @JsonIgnore
    public ArrayList<Friend> getFriends() {
        return this.friends;
    }

    @JsonIgnore
    public void setFriends(ArrayList<Friend> friends) {
        this.friends = friends;
    }

    public void addFriend(Friend friend) {
        this.friends.add(friend);
    }
    public void addReview(Review rev) {
        this.reviews.add(rev);
    }

    public void removeFriend(User user) {
        this.friends.remove(user);
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    @JsonIgnore
    public ArrayList<Reservation> getReservations() {
        return this.reservations;
    }

    @JsonIgnore
    public void setReservations(ArrayList<Reservation> reservations) {
        this.reservations = reservations;
    }

    @JsonIgnore
    public void addReservation(Reservation reservation) {
        this.reservations.add(reservation);
    }

    @JsonIgnore
    public ArrayList<Review> getReviews() {
        return reviews;
    }

    @JsonIgnore
    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }
}
