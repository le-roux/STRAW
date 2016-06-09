package straw.polito.it.straw.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.data.Reservation;

/**
 * Created by Sylvain on 19/05/2016.
 */
public class ReservationAdapterCustomer extends ReservationAdapter {

    public ReservationAdapterCustomer(Context context) {
        super(context);
    }

    public ReservationAdapterCustomer(Context context, ArrayList<Reservation> reservationList,
                                     Activity activity) {
        super(context, reservationList, activity);
    }


    @Override
    protected void setSpecificItems(View view, int position) {
        TextView restaurantName = (TextView)view.findViewById(R.id.RestaurantName);
        restaurantName.setText(((Reservation)this.getItem(position)).getRestaurant());
    }

    @Override
    public void init(int position) {
        if (position < this.reservationList.size() && position >= 0) {
            switch (this.reservationList.get(position).getStatus()) {
                case (Reservation.PENDING): {
                    setIconVisible(WAIT_ICON);
                    break;
                }
                case (Reservation.ACCEPTED): {
                    setIconVisible(ACCEPT_ICON);
                    break;
                }
                case (Reservation.DISCARDED): {
                    setIconVisible(CANCEL_ICON);
                    break;
                }
                case (Reservation.CHANGED): {
                    setIconVisible(null);
                    break;
                }
            }
        }
    }

    @Override
    protected View setSpecificView(LayoutInflater layoutInflater) {
        return layoutInflater.inflate(R.layout.reservation_customer, null);
    }
}
