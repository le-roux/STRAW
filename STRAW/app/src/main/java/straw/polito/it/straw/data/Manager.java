package straw.polito.it.straw.data;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.StringTokenizer;

import straw.polito.it.straw.utils.Logger;

/**
 * Created by Andres Camilo Jimenez on 03/04/2016.
 */
public class Manager {

    private String pwd;
    private String telephone;
    private String address;
    private String res_name;
    private String res_type;
    private int seats;
    private String image;
    private String email;

    public static final String SEATS_AVAILABLE = "SeatsAvailable";
    public static final String NAME = "NAME";
    //public static final String PRICE = "PRICE";
    public static final String IMAGE_URI = "IMAGE_URI";
    private static final String DEFAULT_NAME = "Default";
    //private static final String DEFAULT_PRICE = "€€€";

    public Manager() {
    }

    public Manager(String man) {

        try {
            JSONObject oj = new JSONObject(man);
            pwd = (String) oj.get("pwd");
            telephone = (String) oj.get("tel");
            address = (String) oj.get("addr");
            res_name = (String) oj.get("r_n");
            res_type = (String) oj.get("r_t");
            seats = (int) oj.get("seats");
            image = (String) oj.get("photo");
            email = (String) oj.get("email");
        } catch (JSONException e) {
            Logger.d("Error creating the manager");
        }
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


    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
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


    public String toJSONObject() {
        JSONObject oj = new JSONObject();
        try {
            oj.put("pwd", pwd);
            oj.put("tel", telephone);
            oj.put("addr", address);
            oj.put("r_n", res_name);
            oj.put("r_t", res_type);
            oj.put("seats", seats);
            oj.put("photo", image);
            oj.put("email", email);
            return oj.toString();
        } catch (JSONException e) {
            Logger.d("Error storing the manager");
        }
        return null;
    }
    /**
    public static Manager create(JSONObject jsonObject) {
        Manager manager = new Manager();
        try {
            manager.setGlutenFree(jsonObject.getBoolean(GLUTEN_FREE));
            manager.setVegan(jsonObject.getBoolean(VEGAN));
            manager.setIngredients(jsonObject.getString(INGREDIENTS));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return manager;
    }
     */


}












