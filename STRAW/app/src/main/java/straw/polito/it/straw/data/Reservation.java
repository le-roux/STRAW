package straw.polito.it.straw.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import straw.polito.it.straw.DateDisplayer;
import straw.polito.it.straw.TimeDisplayer;
import straw.polito.it.straw.utils.Logger;

/**
 * Created by Sylvain on 07/04/2016.
 */
public class Reservation implements TimeDisplayer, DateDisplayer{

    private int numberPeople;
    private GregorianCalendar time;
    private ArrayList<Food> plates;
    private ArrayList<Food> drinks;
    private String foodList;
    private String restaurant;
    private String customer;
    private String id;
    private int status;
    private int place;

    public static final int INSIDE = 0;
    public static final int OUTSIDE = 1;
    public static final int NO_PREFERENCE = 2;

    /**
     * Possible values for the "status" attribute.
     */
    public static final int PENDING = 0;
    public static final int ACCEPTED = 1;
    public static final int DISCARDED = 2;
    public static final int CHANGED = 3;
    public static final int PASSED = 4;

    public static final String NUMBER_PEOPLE = "numberPeople";
    public static final String DAY = "day";
    public static final String MONTH = "month";
    public static final String YEAR = "year";
    public static final String HOUR = "hourOfDay";
    public static final String MINUTES = "minutes";
    public static final String PLATES = "Plates";
    public static final String DRINKS = "Drinks";
    public static final String RESERVATION = "Reservation";
    public static final String RESTAURANT = "restaurant";
    public static final String CUSTOMER = "customer";
    public static final String PLACE = "place";
    public static final String STATUS = "status";
    public static final String ID = "id";
    public static final String FOOD_LIST = "foodList";

    public Reservation() {
        this(0, new ArrayList<Food>(), new ArrayList<Food>(), NO_PREFERENCE);
    }

    public Reservation(int numberPeople, ArrayList<Food> plates, ArrayList<Food> drinks) {
        this(numberPeople, plates, drinks, NO_PREFERENCE);
    }

    public Reservation(int numberPeople, ArrayList<Food> plates, ArrayList<Food> drinks, int place) {
        this.numberPeople = numberPeople;
        this.plates = plates;
        this.drinks = drinks;
        this.time = new GregorianCalendar();
        this.place = place;
        this.foodList = null;
        this.status = PENDING;
    }

    public int getNumberPeople() {
        return this.numberPeople;
    }

    public void setNumberPeople(int numberPeople) {
        this.numberPeople = numberPeople;
    }

    public String getFoodList() {
        if (this.foodList == null) {
            StringBuilder builder = new StringBuilder();
            for (Food plate : this.plates) {
                builder.append(plate.getName())
                        .append(", ");
            }
            for (Food drink : this.drinks) {
                builder.append(drink.getName())
                        .append(", ");
            }
            if (builder.length() > 2)
                builder.delete(builder.length() - 2, builder.length() - 1);
            this.foodList = builder.toString();
        }
        return this.foodList;
    }

    public void setFoodList(String foodList) {
        this.foodList = foodList;
    }

    @JsonIgnore
    public ArrayList<Food> getPlates() {
        return this.plates;
    }

    @JsonIgnore
    public ArrayList<Food> getDrinks() {
        return this.drinks;
    }

    @JsonIgnore
    public void setPlates(ArrayList<Food> plates) {
        this.plates = plates;
    }

    @JsonIgnore
    public void setDrinks(ArrayList<Food> drinks) {
        this.drinks = drinks;
    }

    @JsonIgnore
    public GregorianCalendar getTime() {
        return this.time;
    }

    public int getHourOfDay() {
        return this.time.get(Calendar.HOUR_OF_DAY);
    }

    public void setHourOfDay(int hourOfDay) {
        this.time.set(Calendar.HOUR_OF_DAY, hourOfDay);
    }

    public int getMinutes() {
        return this.time.get(Calendar.MINUTE);
    }

    public void setMinutes(int minutes) {
        this.time.set(Calendar.MINUTE, minutes);
    }

    @Override
    public void setIs24HFormat(boolean format) {
        //To do
    }

    public int getMonth() {
        return this.time.get(Calendar.MONTH);
    }

    public void setMonth(int month) {
        this.time.set(Calendar.MONTH, month);
    }

    public int getDay() {
        return this.time.get(Calendar.DAY_OF_MONTH);
    }

    public void setDay(int day) {
        this.time.set(Calendar.DAY_OF_MONTH, day);
    }

    public int getYear() {
        return this.time.get(Calendar.YEAR);
    }

    public void setYear(int year) {
        this.time.set(Calendar.YEAR, year);
    }

    @JsonIgnore
    public void setTime(GregorianCalendar gregorianCalendar) {
        this.time = gregorianCalendar;
    }

    @JsonIgnore
    public void setTime(int hour, int minute) {
        int year = this.time.get(Calendar.YEAR);
        int month = this.time.get(Calendar.MONTH);
        int day = this.time.get(Calendar.DAY_OF_MONTH);
        this.time.set(year, month, day, hour, minute);
    }

    @JsonIgnore
    public void setTime(int year, int month, int day, int hour, int minute) {
        this.time.set(year, month, day, hour, minute);
    }

    @JsonIgnore
    public void setDate(int year, int month, int day) {
        this.time.set(year, month, day);
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public int getPlace() {
        return this.place;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public String getRestaurant() {
        return this.restaurant;
    }

    public void setCustomer (String customer) {
        this.customer = customer;
    }

    public String getCustomer() {
        return this.customer;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
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
            jsonObject.put(PLACE, this.getPlace());
            jsonObject.put(RESTAURANT, this.getRestaurant());
            jsonObject.put(CUSTOMER, this.getCustomer());
            jsonObject.put(STATUS, this.getStatus());
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
            reservation.setRestaurant(jsonObject.getString(RESTAURANT));
            reservation.setCustomer(jsonObject.getString(CUSTOMER));
            reservation.setStatus(jsonObject.getInt(STATUS));
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

    @JsonIgnore
    public GregorianCalendar getCalendar() {
        GregorianCalendar calendar = new GregorianCalendar(this.getYear(), this.getMonth(), this.getDay(), this.getHourOfDay(), this.getMinutes());
        return calendar;
    }
}