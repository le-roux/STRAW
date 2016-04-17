package straw.polito.it.straw.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;

import straw.polito.it.straw.Timer;

/**
 * Created by Sylvain on 07/04/2016.
 */
public class Reservation implements Timer{

    private int numberPeople;
    private GregorianCalendar time;
    private String plates;
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
    public static final String RESERVATION = "Reservation";
    public static  final String PLACE = "Place";

    public Reservation() {
        this(0, "", Place.NO_PREFERENCE);
    }

    public Reservation(int numberPeople, String plates) {
        this(numberPeople, plates, Place.NO_PREFERENCE);
    }

    public Reservation(int numberPeople, String plates, Place place) {
        this.numberPeople = numberPeople;
        this.plates = plates;
        this.time = new GregorianCalendar();
        this.place = place;
    }

    public int getNumberPeople() {
        return this.numberPeople;
    }

    public void setNumberPeople(int numberPeople) {
        this.numberPeople = numberPeople;
    }

    public String getPlates() {
        return this.plates;
    }

    public void setPlates(String plates) {
        this.plates = plates;
    }

    public GregorianCalendar getTime() {
        return this.time;
    }

    public String getTimeString() {
        String time = "";
        StringBuilder stringBuilder = new StringBuilder();
        int day = this.time.get(Calendar.DAY_OF_MONTH);
        if (day < 10)
            stringBuilder.append('0');
        stringBuilder.append(day)
                .append("/");
        int month = this.time.get(Calendar.MONTH);
        if (month < 10)
            stringBuilder.append('0');
        stringBuilder.append(month)
                .append("  ");
        int hour = this.time.get(Calendar.HOUR_OF_DAY);
        if (hour < 10)
            stringBuilder.append('0');
        stringBuilder.append(hour)
                .append(":");
        int minute = this.time.get(Calendar.MINUTE);
        if (minute < 10)
            stringBuilder.append('0');
        stringBuilder.append(minute);
        return stringBuilder.toString();
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
            jsonObject.putOpt(PLATES, this.plates);
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
            reservation.setPlates(jsonObject.getString(PLATES));
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