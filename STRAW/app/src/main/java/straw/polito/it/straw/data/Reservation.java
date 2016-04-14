package straw.polito.it.straw.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Sylvain on 07/04/2016.
 */
public class Reservation {

    private int numberPeople;
    private GregorianCalendar time;
    private String plates;

    public static final String NUMBER_PEOPLE = "NumberPeople";
    public static final String TIME = "Time";
    public static final String PLATES = "Plates";
    public static final String RESERVATION = "Reservation";

    public Reservation() {
        this.numberPeople = 0;
        this.time = new GregorianCalendar();
        this.plates = "";
    }

    public Reservation(int numberPeople, String plates) {
        this.numberPeople = numberPeople;
        this.plates = plates;
        this.time = new GregorianCalendar();
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

    public int getHour() {
        return this.time.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinutes() {
        return this.time.get(Calendar.MINUTE);
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

    public JSONObject save() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(NUMBER_PEOPLE, this.numberPeople);
            jsonObject.putOpt(PLATES, this.plates);
            jsonObject.putOpt(TIME, this.time);
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
            reservation.setTime((GregorianCalendar)jsonObject.get(TIME));
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
