package straw.polito.it.straw.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.Calendar;

import straw.polito.it.straw.DateDisplayer;

/**
 * Created by Sylvain on 15/04/2016.
 */
public class DateDisplay extends TextView implements DateDisplayer {

    private int year;
    private int month;
    private int day;

    public DateDisplay(Context context) {
        this(context, null, 0);
    }

    public DateDisplay(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public DateDisplay (Context context, AttributeSet attributeSet, int style) {
        super(context, attributeSet, style);
        //By default, values are the current date
        Calendar calendar = Calendar.getInstance();
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
        updateText();
    }

    @Override
    public int getYear() {
        return this.year;
    }

    @Override
    public int getMonth() {
        return this.month;
    }

    @Override
    public int getDay() {
        return this.day;
    }

    @Override
    public void setDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        updateText();
    }

    public void updateText() {
        String text = getDate(this.day, this.month, this.year);
        this.setText(text);
    }

    public static String getDate(int day, int month, int year) {
        StringBuilder builder = new StringBuilder();
        if (day < 10)
            builder.append('0');
        builder.append(day)
                .append('/');
        if(month < 10)
            builder.append('0');
        builder.append(month + 1)
                .append('/')
                .append(year);
        return builder.toString();
    }
}
