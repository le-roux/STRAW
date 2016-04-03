package straw.polito.it.straw;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Andres Camilo Jimenez on 01/04/2016.
 */
public class CreateAccountAdapter extends BaseAdapter {
    Context context;
    List<String> data;
    HashMap<Integer,String> input;
    SharedPreferences mShared;

    public HashMap<Integer, String> getInput() {
        return input;
    }

    private static LayoutInflater inflater = null;
    private String TAG ="CreateAccountAdapter";

    public CreateAccountAdapter(Context context, List<String> data) {
        this.context = context;
        this.data = data;
        input=new HashMap<>();
        mShared= PreferenceManager.getDefaultSharedPreferences(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.row, null);

        final List<String> types = new ArrayList<>();
        types.add("");
        types.add(context.getString(R.string.bar));
        types.add(context.getString(R.string.restaurant));
        types.add(context.getString(R.string.canteen));
        types.add(context.getString(R.string.ta));

        final TextView text = (TextView) vi.findViewById(R.id.text_textView);
        final EditText edit_text = (EditText) vi.findViewById(R.id.text_editText);
        final Spinner sp= (Spinner)vi.findViewById(R.id.r_type_spinner);

        /*TextWatcher watcher= new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (!edit_text.getText().toString().equals("")) {
                    input.put(position,edit_text.getText().toString());
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do something or nothing.
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Do something or nothing
            }
        };*/

        text.setVisibility(View.INVISIBLE);

        edit_text.setVisibility(View.INVISIBLE);
       // edit_text.addTextChangedListener(watcher);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                input.put(position,types.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp.setVisibility(View.INVISIBLE);

        if(data.get(position).equals(context.getString(R.string.r_type))){
            text.setVisibility(View.VISIBLE);
            sp.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item,types);
            sp.setAdapter(adapter);
            text.setText(data.get(position));

        }else {
            text.setVisibility(View.VISIBLE);
            edit_text.setVisibility(View.VISIBLE);
            text.setText(data.get(position));
            //edit_text.setText(input.get(position));
            Log.v(TAG,"->"+data.get(position)+" = "+input.get(position));
        }
        return vi;
    }
}
