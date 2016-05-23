package straw.polito.it.straw;

import android.widget.Adapter;

/**
 * Created by Sylvain on 23/05/2016.
 */
public interface AdapterFragment {
    void setAdapter(Adapter adapter);
    Adapter getAdapter();
}
