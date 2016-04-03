package straw.polito.it.straw.data;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Andres Camilo Jimenez on 03/04/2016.
 */
public class Manager {

    private String name;
    private String pwd;
    private int telephone;
    private String address;
    private String res_name;
    private String res_type;
    private int seats;
    private String image;

    public Manager() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public int getTelephone() {
        return telephone;
    }

    public void setTelephone(int telephone) {
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
    public String toJSONObject(){
        JSONObject oj=new JSONObject();
        try {
            oj.put("user_n",name);
            oj.put("pwd",pwd);
            oj.put("tel",telephone);
            oj.put("addr",address);
            oj.put("r_n",res_name);
            oj.put("r_t",res_type);
            oj.put("seats",seats);
            oj.put("photo",image);
            return oj.toString();
        } catch (JSONException e) {
            Log.v("Manager", "Error creating the manager");
        }
        return null;
    }
}
