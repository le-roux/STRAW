package straw.polito.it.straw.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.data.User;

/**
 * Created by Sylvain on 25/04/2016.
 */
public class UserAdapter extends BaseAdapter {

    private ArrayList<User> users;
    private Context context;
    private ArrayList<CheckBox> checkBoxes;

    public UserAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public int getCount() {
        return this.users.size();
    }

    @Override
    public Object getItem(int position) {
        return this.users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.user, null);
            CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.checkbox);
            this.checkBoxes.add(checkBox);
        }
        ImageView imageView = (ImageView)convertView.findViewById(R.id.photo_imageView);
        imageView.setImageURI(Uri.parse(this.users.get(position).getImage()));
        TextView userName = (TextView)convertView.findViewById(R.id.User_name);
        userName.setText(this.users.get(position).getEmail());
        return convertView;
    }

    public ArrayList<CheckBox> getCheckboxes() {
        return this.checkBoxes;
    }
}
