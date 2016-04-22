package straw.polito.it.straw.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.Calendar;

import straw.polito.it.straw.TimeDisplayer;

/**
 * Created by Sylvain on 15/04/2016.
 */
public class TimerDisplay extends TextView implements TimeDisplayer {

    private int hourOfDay;
    private int minute;
    private boolean is24HFormat;

    public TimerDisplay(Context context) {
        this(context, null, 0);
    }

    public TimerDisplay(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }
    public TimerDisplay(Context context, AttributeSet attributeSet, int style) {
        super(context, attributeSet, style);
        //By default, the values are the current ones
        Calendar calendar = Calendar.getInstance();
        this.hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        this.minute = calendar.get(Calendar.MINUTE);
        this.is24HFormat = true;
        updateText();
    }

    @Override
    public void setTime(int hourOfDay, int minute) {
        this.hourOfDay = hourOfDay;
        this.minute = minute;
        updateText();
    }

    public void updateText() {
        StringBuilder text = new StringBuilder();
        int hour = this.hourOfDay;
        if (!this.is24HFormat)
            if (hour > 12)
                hour -= 12;
            else if (hour == 0)
                hour = 12;
        if(hour < 10)
            text.append('0');
        text.append(String.valueOf(hour))
                .append(":");
        if(this.minute < 10)
            text.append(('0'));
        text.append(String.valueOf(this.minute));
        if (!this.is24HFormat)
            if (this.hourOfDay < 12)
                text.append(" AM");
            else
                text.append(" PM");
        this.setText(text.toString());
    }

    public void setIs24HFormat(boolean format) {
        this.is24HFormat = format;
    }

    public int getHourOfDay() {
        return this.hourOfDay;
    }

    @Override
    public int getMinutes() {
        return this.minute;
    }
}