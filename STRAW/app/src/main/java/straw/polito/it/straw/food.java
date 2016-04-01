package straw.polito.it.straw;

/**
 * Created by Sylvain on 01/04/2016.
 */
public class Food {
    private String name;
    private float price;
    private String imageURI;

    public Food(String name, float price, String imageURI) {
        this.name = name;
        this.price = price;
        this.imageURI = imageURI;
    }

    public Food() {
        this.name = "Default";
        this.price = 0f;
        this.imageURI = null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getPrice() {
        return this.price;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public String getImageURI() {
        return this.imageURI;
    }
}
