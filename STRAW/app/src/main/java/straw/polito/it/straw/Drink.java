package straw.polito.it.straw;

import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sylvain on 01/04/2016.
 */
public class Drink extends Food {
    /**
     * The volume (in Liter) of the drink
     */
    private float volume;

    public Drink() {
        super();
        this.volume = 1f;
    }

    public Drink(String name, float price, String imageUri, float Volume) {
        super(name, price, imageUri);
        this.volume = volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getVolume()
    {
        return this.volume;
    }

    @Override
    public String getDescription() {
        String description = String.valueOf(this.volume) + "L";
        return description;
    }

    @Override
    public void save(SharedPreferences.Editor editor, int id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("TYPE", "DRINK");
            jsonObject.putOpt("NAME", this.getName());
            jsonObject.put("PRICE", this.getPrice());
            jsonObject.putOpt("IMAGE_URI", this.getImageURI());
            jsonObject.put("VOLUME", this.getVolume());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editor.putString(String.valueOf(id), jsonObject.toString());
        editor.commit();
    }
}
