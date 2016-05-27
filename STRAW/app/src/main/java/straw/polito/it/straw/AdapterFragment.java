package straw.polito.it.straw;

import android.widget.Adapter;

/**
 * Created by Sylvain on 23/05/2016.
 */
public interface AdapterFragment {
    /**
     * Set the adapter that contains all the data
     * @param adapter : the adapter to set
     */
    void setAdapter(Adapter adapter);


    Adapter getAdapter();

    /**
     * Indicates that the data set has changed and thus that the displayed content must be updated.
     */
    void notifyDataSetChanged();
}
