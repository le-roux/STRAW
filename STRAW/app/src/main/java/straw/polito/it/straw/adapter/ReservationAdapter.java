package straw.polito.it.straw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.data.Reservation;

/**
 * Created by Sylvain on 07/04/2016.
 */
public class ReservationAdapter extends BaseAdapter {

    private ArrayList<Reservation> reservationList;
    private Context context;

    public ReservationAdapter(Context context) {
        this.reservationList = new ArrayList<Reservation>();
        this.context = context;
    }

    public ReservationAdapter(Context context, ArrayList<Reservation> reservationList) {
        this.reservationList = reservationList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.reservationList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.reservationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.reservation, null);
        }
        //Get the views of the item
        TextView numberPeople = (TextView)convertView.findViewById(R.id.number_people);
        TextView time = (TextView)convertView.findViewById(R.id.time);
        TextView plates = (TextView)convertView.findViewById(R.id.plates);

        //Fill them with appropriate values
        numberPeople.setText(this.reservationList.get(position).getNumberPeople() + " " +
                context.getString(R.string.Persons));
        time.setText(this.reservationList.get(position).getTimeString());
        plates.setText(this.reservationList.get(position).getPlates());

        return convertView;
    }
}
