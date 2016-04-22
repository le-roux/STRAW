package straw.polito.it.straw;

/**
 * Created by Sylvain on 15/04/2016.
 */
public interface DateDisplayer {
    int getYear();
    int getMonth();
    int getDay();
    void setDate(int year, int month, int day);
}
