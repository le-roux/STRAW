package straw.polito.it.straw;

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

    public float getVolume() {
        return this.volume;
    }

    @Override
    public String getDescription() {
        String description = String.valueOf(this.volume) + "L";
        return description;
    }
}
