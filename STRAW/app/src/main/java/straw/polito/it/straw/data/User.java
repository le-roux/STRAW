package straw.polito.it.straw.data;

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
    private String type;
    private String pref_time;
    private String image;
    private String phoneNumber;
    private ArrayList<User> friends;

    public User() {
    }

    public User(String s){
        this.friends = new ArrayList<>();
        try {
            JSONObject jo=new JSONObject(s);
            this.email=jo.getString("email");
            this.university=jo.getString("uni");
            this.diet=jo.getString("diet");
            this.type=jo.getString("type");
            this.pref_time=jo.getString("pref_t");
            this.image=jo.getString("image");
        } catch (JSONException e) {
            Logger.d(e.getMessage());
        }
    }

    @Override
    public String toString() {
        JSONObject jo=new JSONObject();
        try {
            jo.put("email",this.email);
            jo.put("uni",this.university);
            jo.put("diet",this.diet);
            jo.put("type",this.type);
            jo.put("pref_t",this.pref_time);
            jo.put("image",this.image);
            return jo.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
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

    public String getPref_time() {
        return pref_time;
    }

    public void setPref_time(String pref_time) {
        this.pref_time = pref_time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<User> getFriends() {
        return this.friends;
    }

    public void addFriend(User user) {
        this.friends.add(user);
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
}
