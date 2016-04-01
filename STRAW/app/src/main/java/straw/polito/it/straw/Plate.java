package straw.polito.it.straw;

import java.util.ArrayList;

/**
 * Created by Sylvain on 01/04/2016.
 */
public class Plate extends Food {

    /**
        List of the ingredients present in the plate
     */
    private ArrayList<String> ingredients;
    private boolean vegan;
    private boolean glutenFree;

    public Plate() {
        super();
        this.ingredients =new ArrayList<String>();
        this.vegan = false;
        this.glutenFree = false;
    }

    public void addIngredient(String ingredient) {
        this.ingredients.add(ingredient);
    }

    public void removeIngredient(String ingredient) {
        this.ingredients.remove(ingredient);
    }

    public void setVegan(boolean vegan) {
        this.vegan = vegan;
    }

    public boolean isVegan() {
        return this.vegan;
    }

    public void setGlutenFree (boolean glutenFree) {
        this.glutenFree = glutenFree;
    }

    public boolean isGlutenFree() {
        return this.glutenFree;
    }

}