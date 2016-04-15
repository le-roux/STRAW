package straw.polito.it.straw.data;

import android.content.Context;
import android.util.AttributeSet;
import android.util.StringBuilderPrinter;
import android.widget.TextView;

import straw.polito.it.straw.Timer;

/**
 * Created by sylva on 15/04/2016.
 */
public class TimerDisplay extends TextView implements Timer {

    private int hour;
    private int minute;

    public TimerDisplay(Context context) {
        this(context, null, 0);
    }

    public TimerDisplay(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }
    public TimerDisplay(Context context, AttributeSet attributeSet, int style) {
        super(context, attributeSet, style);
        this.hour = 0;
        this.minute = 0;
    }

    @Override
    public void setTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        updateText();
    }

    public void updateText() {
        StringBuilder text = new StringBuilder();
        //TO DO : take care of 12/24h hour format
        if(this.hour < 10)
            text.append('0');
        text.append(String.valueOf(this.hour))
                .append(":");
        if(this.minute < 10)
            text.append(('0'));
        text.append(String.valueOf(this.minute));
        this.setText(text.toString());
    }

    @Override
    public int getHour() {
        return this.hour;
    }

    @Override
    public int getMinutes() {
        return this.minute;
    }
}
