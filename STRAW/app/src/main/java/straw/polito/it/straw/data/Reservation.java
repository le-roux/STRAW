package straw.polito.it.straw.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import straw.polito.it.straw.DateDisplayer;
import straw.polito.it.straw.TimeDisplayer;

/**
 * Created by Sylvain on 07/04/2016.
 */
public class Reservation implements TimeDisplayer, DateDisplayer{

    private int numberPeople;
    private GregorianCalendar time;
    private ArrayList<Food> plates;
    private ArrayList<Food> drinks;
    public enum Place {INSIDE, OUTSIDE, NO_PREFERENCE};
    public static final int INSIDE = 0;
    public static final int OUTSIDE = 1;
    public static final int NO_PREFERENCE = 2;

    private Place place;

    public static final String NUMBER_PEOPLE = "NumberPeople";
    public static final String DAY = "Day";
    public static final String MONTH = "Month";
    public static final String YEAR = "Year";
    public static final String HOUR = "Hour";
    public static final String MINUTES = "Minutes";
    public static final String PLATES = "Plates";
    public static final String DRINKS = "Drinks";
    public static final String RESERVATION = "Reservation";
    public static  final String PLACE = "Place";

    public Reservation() {
        this(0, new ArrayList<Food>(), new ArrayList<Food>(), Place.NO_PREFERENCE);
    }

    public Reservation(int numberPeople, ArrayList<Food> plates, ArrayList<Food> drinks) {
        this(numberPeople, plates, drinks, Place.NO_PREFERENCE);
    }

    public Reservation(int numberPeople, ArrayList<Food> plates, ArrayList<Food> drinks, Place place) {
        this.numberPeople = numberPeople;
        this.plates = plates;
        this.drinks = drinks;
        this.time = new GregorianCalendar();
        this.place = place;
    }

    public int getNumberPeople() {
        return this.numberPeople;
    }

    public void setNumberPeople(int numberPeople) {
        this.numberPeople = numberPeople;
    }

    public String getFoodList() {
        StringBuilder builder = new StringBuilder();
        for (Food plate : this.plates) {
            builder.append(plate.toString())
                    .append(", ");
        }
        for (Food drink : this.drinks) {
            builder.append(drink.toString())
                    .append(", ");
        }
         builder.delete(builder.length() - 2, builder.length() - 1);
        return builder.toString();
    }

    public ArrayList<Food> getPlates() {
        return this.plates;
    }

    public ArrayList<Food> getDrinks() {
        return this.drinks;
    }

    public void setPlates(ArrayList<Food> plates) {
        this.plates = plates;
    }

    public void setDrinks(ArrayList<Food> drinks) {
        this.drinks = drinks;
    }

    public GregorianCalendar getTime() {
        return this.time;
    }

    public int getHourOfDay() {
        return this.time.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinutes() {
        return this.time.get(Calendar.MINUTE);
    }

    @Override
    public void setIs24HFormat(boolean format) {
        //To do
    }

    public int getMonth() {
        return this.time.get(Calendar.MONTH);
    }

    public int getDay() {
        return this.time.get(Calendar.DAY_OF_MONTH);
    }
    public int getYear() {
        return this.time.get(Calendar.YEAR);
    }

    public void setTime(GregorianCalendar gregorianCalendar) {
        this.time = gregorianCalendar;
    }

    public void setTime(int hour, int minute) {
        int year = this.time.get(Calendar.YEAR);
        int month = this.time.get(Calendar.MONTH);
        int day = this.time.get(Calendar.DAY_OF_MONTH);
        this.time.set(year, month, day, hour, minute);
    }

    public void setTime(int year, int month, int day, int hour, int minute) {
        this.time.set(year, month, day, hour, minute);
    }

    public void setDate(int year, int month, int day) {
        this.time.set(year, month, day);
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    private void setPlace(int place) {
        switch(place) {
            case INSIDE: this.place = Place.INSIDE; break;
            case OUTSIDE: this.place = Place.OUTSIDE; break;
            default : this.place = Place.NO_PREFERENCE; break;
        }
    }

    public Place getPlace() {
        return this.place;
    }

    private int getPlaceInt() {
        switch (this.place) {
            case INSIDE:
                return INSIDE;
            case OUTSIDE:
                return OUTSIDE;
            default:
                return NO_PREFERENCE;
        }
    }

    public JSONObject save() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(NUMBER_PEOPLE, this.numberPeople);
            JSONArray platesArray = new JSONArray();
            for (int i = 0; i < this.plates.size(); i++)
                platesArray.put(i, this.plates.get(i).toString());
            jsonObject.putOpt(PLATES, platesArray);
            JSONArray drinksArray = new JSONArray();
            for (int i = 0; i < this.drinks.size(); i++)
                drinksArray.put(i, this.drinks.get(i).toString());
            jsonObject.put(DRINKS, drinksArray);
            jsonObject.put(YEAR, this.getYear());
            jsonObject.put(MONTH, this.getMonth());
            jsonObject.put(DAY, this.getDay());
            jsonObject.put(HOUR, this.getHourOfDay());
            jsonObject.put(MINUTES, this.getMinutes());
            jsonObject.put(PLACE, this.getPlaceInt());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public String toString() {
        return this.save().toString();
    }

    public static Reservation create(JSONObject jsonObject) {
        Reservation reservation = new Reservation();
        try {
            reservation.setNumberPeople(jsonObject.getInt(NUMBER_PEOPLE));
            JSONArray platesArray = jsonObject.getJSONArray(PLATES);
            ArrayList<Food> plates = new ArrayList<>();
            for (int i = 0; i < platesArray.length(); i++)
                plates.add(Food.create(platesArray.getString(i)));
            reservation.setPlates(plates);
            JSONArray drinksArray = jsonObject.getJSONArray(DRINKS);
            ArrayList<Food> drinks = new ArrayList<>();
            for (int i = 0; i < drinksArray.length(); i++)
                drinks.add(Food.create(drinksArray.getString(i)));
            reservation.setDrinks(drinks);
            int year = jsonObject.getInt(YEAR);
            int month = jsonObject.getInt(MONTH);
            int day = jsonObject.getInt(DAY);
            int hour = jsonObject.getInt(HOUR);
            int minutes = jsonObject.getInt(MINUTES);
            reservation.setTime(year, month, day, hour, minutes);
            reservation.setPlace(jsonObject.getInt(PLACE));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return reservation;
    }

    public static Reservation create(String description) {
        try {
            return create(new JSONObject(description));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}