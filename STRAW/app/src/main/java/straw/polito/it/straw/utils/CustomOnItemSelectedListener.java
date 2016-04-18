package straw.polito.it.straw.utils;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * Created by tibo on 16/04/2016.
 */
public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
        Toast.makeText(parent.getContext(),
                "List of restaurant " + parent.getItemAtPosition(pos).toString(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {}

}
