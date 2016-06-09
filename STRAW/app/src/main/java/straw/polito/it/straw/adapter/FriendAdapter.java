package straw.polito.it.straw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.data.Friend;

/**
 * Created by Sylvain on 25/04/2016.
 */
public class FriendAdapter extends BaseAdapter {

    private ArrayList<Friend> friends;
    private Context context;
    private ArrayList<CheckBox> checkBoxes;

    public FriendAdapter(Context context, ArrayList<Friend> friends) {
        this.context = context;
        this.friends = friends;
        this.checkBoxes = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return this.friends.size();
    }

    @Override
    public Object getItem(int position) {
        return this.friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.friend, null);
            CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.checkbox);
            this.checkBoxes.add(checkBox);
        }
        TextView userName = (TextView)convertView.findViewById(R.id.User_name);
        userName.setText(this.friends.get(position).getName());
        TextView address = (TextView)convertView.findViewById(R.id.address);
        address.setText(this.friends.get(position).getAddresses());
        return convertView;
    }

    public ArrayList<CheckBox> getCheckboxes() {
        return this.checkBoxes;
    }
}
