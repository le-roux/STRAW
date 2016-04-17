package straw.polito.it.straw;

/**
 * Created by sylva on 15/04/2016.
 */
public interface TimeDisplayer {
    void setTime(int hour, int minute);
    int getHourOfDay();
    int getMinutes();
    void setIs24HFormat(boolean format);
}
