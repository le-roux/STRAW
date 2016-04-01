package straw.polito.it.straw;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andres Camilo Jimenez on 01/04/2016.
 */
public class CreateAccountAdapter extends BaseAdapter {
    Context context;
    List<String> data;
    SharedPreferences mShared;

    private static LayoutInflater inflater = null;

    public CreateAccountAdapter(Context context, List<String> data) {
        this.context = context;
        this.data = data;
        mShared= context.getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.row, null);
        TextView text = (TextView) vi.findViewById(R.id.text_textView);
        final EditText edit_text = (EditText) vi.findViewById(R.id.text_editText);
        Spinner sp= (Spinner)vi.findViewById(R.id.r_type_spinner);

        TextWatcher watcher= new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (!edit_text.getText().toString().equals("")) {
                   // mShared.edit().commit()
                }

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do something or nothing.
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Do something or nothing
            }
        };

        text.setVisibility(View.INVISIBLE);
        edit_text.setVisibility(View.INVISIBLE);
        sp.setVisibility(View.INVISIBLE);

        if(data.get(position).equals(context.getString(R.string.r_type))){
            text.setVisibility(View.VISIBLE);
            sp.setVisibility(View.VISIBLE);
            List<String> types = new ArrayList<>();
            types.add(context.getString(R.string.bar));
            types.add(context.getString(R.string.restaurant));
            types.add(context.getString(R.string.canteen));
            types.add(context.getString(R.string.ta));

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,types);
            sp.setAdapter(adapter);
            text.setText(data.get(position));

        }else {
            text.setVisibility(View.VISIBLE);
            edit_text.setVisibility(View.VISIBLE);
            text.setText(data.get(position));
        }

        return vi;
    }
}
