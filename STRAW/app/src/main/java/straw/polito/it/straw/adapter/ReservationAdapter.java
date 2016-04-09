package straw.polito.it.straw.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import straw.polito.it.straw.R;
import straw.polito.it.straw.activities.DisplayReservationsActivity;
import straw.polito.it.straw.activities.HomeActivity;
import straw.polito.it.straw.data.Reservation;

/**
 * Created by Sylvain on 07/04/2016.
 */




public class ReservationAdapter extends BaseAdapter {

    private ArrayList<Reservation> reservationList;
    private static Context context;
    private DisplayReservationsActivity parentActivity;

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
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.reservation, null);
        }
        final Reservation dataModel = reservationList.get(position);
        //Get the views of the item
        TextView numberPeople = (TextView) convertView.findViewById(R.id.number_people);
        TextView time = (TextView) convertView.findViewById(R.id.time);
        TextView plates = (TextView) convertView.findViewById(R.id.plates);

        Button handle_button = (Button) convertView.findViewById(R.id.HandleButton);
        handle_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());

                builder.setTitle("Handle this order");
                final CharSequence[] options = {"Accept", "Refuse", "Cancel"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Accept")) {
                            AlertDialog.Builder builder_accept = new AlertDialog.Builder(view.getRootView().getContext());
                            builder_accept.setTitle("Accept this order?");
                            final CharSequence[] options = {"Yes", "Cancel"};
                            builder_accept.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int item) {
                                    if (options[item].equals("Yes")) {
                                        Toast.makeText(context, "This order is confirmed \n A notification has been sent to the customer",
                                                Toast.LENGTH_LONG).show();
                                        return;

                                    } else if (options[item].equals("Cancel")) {
                                        dialog.dismiss();
                                    }
                                }
                            });
                            builder_accept.show();

                        } else if (options[item].equals("Refuse")) {
                            AlertDialog.Builder builder_refuse = new AlertDialog.Builder(view.getRootView().getContext());
                            builder_refuse.setTitle("Refuse this order?");
                            final CharSequence[] options = {"Yes", "Cancel"};
                            builder_refuse.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int item) {
                                    if (options[item].equals("Yes")) {
                                        reservationList.remove(position);
                                        ReservationAdapter.this.notifyDataSetChanged();
                                        Toast.makeText(context, "This order is refused \n A notification has been sent to the customer",
                                                Toast.LENGTH_LONG).show();
                                        return;

                                    } else if (options[item].equals("Cancel")) {
                                        dialog.dismiss();
                                    }
                                }
                            });
                            builder_refuse.show();

                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });




                builder.show();

            }
        });
        handle_button.setFocusable(false);

        //Fill them with appropriate values
        numberPeople.setText(this.reservationList.get(position).getNumberPeople() + " " +
                context.getString(R.string.Persons));
        time.setText(this.reservationList.get(position).getTimeString());
        plates.setText(this.reservationList.get(position).getPlates());

        return convertView;
    }

    public static void confirm_accept(View view) {

    }
    public static void confirm_refuse() {

    }

}


