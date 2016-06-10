package straw.polito.it.straw.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Andres Camilo Jimenez on 13/04/2016.
 */
public class Offer {
    ArrayList<Food> combo;
    double price;

    public Offer() {
        this.combo = new ArrayList<>();
        this.price = 0;
    }

    public Offer(String offer){
        try {
            JSONObject oj = new JSONObject(offer);
            this.price = new Double(String.valueOf(oj.get("price")));
            String c = (String) oj.get("combo");
            JSONArray jarr = new JSONArray(c);
            this.combo = new ArrayList<>();
            for(int i = 0; i < jarr.length(); i++){
                this.combo.add(Food.create(jarr.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    @Override
    public String toString() {
        JSONObject oj=new JSONObject();
        try {
            oj.put("price",this.price);
            JSONArray jarr=new JSONArray();

            for(int i=0;i<combo.size();i++){
                jarr.put(new JSONObject(combo.get(i).toString()));
            }
            oj.put("combo",jarr.toString());
            return oj.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ArrayList<Food> getCombo() {
        return combo;
    }

    public void setCombo(ArrayList<Food> combo) {
        this.combo = combo;
    }


}
