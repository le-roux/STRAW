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
import straw.polito.it.straw.straw.polito.it.straw.utils.Logger;

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
        TextView moreOptions = (TextView) convertView.findViewById(R.id.moreOptionsLink);

        moreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Logger.d("more options clicked");
                final Context context = view.getRootView().getContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle(context.getString(R.string.MoreOptions));
                //When modifying this array, don't forget to modify also the following identifiers
                final CharSequence[] options = {context.getString(R.string.Discard),
                        context.getString(R.string.ChangeTime),
                        context.getString(R.string.Cancel)};

                final int DISCARD_RESERVATION = 0;
                final int CHANGE_TIME = 1;
                final int CANCEL = 2;
                Logger.d("step 1");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == DISCARD_RESERVATION) {
                            AlertDialog.Builder builder_refuse = new AlertDialog.Builder(context);
                            builder_refuse.setTitle(context.getString(R.string.RefuseOrder));
                            final CharSequence[] options = {context.getString(R.string.Yes),
                                    context.getString(R.string.Cancel)};

                            builder_refuse.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int item) {
                                    if (options[item].equals(context.getString(R.string.Yes))) {
                                        reservationList.remove(position);
                                        ReservationAdapter.this.notifyDataSetChanged();
                                        Toast.makeText(context, context.getString(R.string.OrderRefusedToast),
                                                Toast.LENGTH_LONG).show();
                                        return;

                                    } else if (options[item].equals(context.getString(R.string.Cancel))) {
                                        dialog.dismiss();
                                    }
                                }
                            });
                            builder_refuse.show();

                        } else if (item == CHANGE_TIME) {
                            //TO DO : offer change time option
                        } else if (item == CANCEL) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
                Logger.d("step 2");
            }
        });

        Button accept_button = (Button) convertView.findViewById(R.id.AcceptButton);
        accept_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder_accept = new AlertDialog.Builder(view.getRootView().getContext());
                builder_accept.setTitle(context.getString(R.string.AcceptOrder));
                final CharSequence[] options = {context.getString(R.string.Yes),
                        context.getString(R.string.Cancel)};

                builder_accept.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals(context.getString(R.string.Yes))) {
                            reservationList.remove(position);
                            ReservationAdapter.this.notifyDataSetChanged();
                            Toast.makeText(context, context.getString(R.string.OrderAcceptedToast),
                                    Toast.LENGTH_LONG).show();
                            return;

                        } else if (options[item].equals(context.getString(R.string.Cancel))) {
                            dialog.dismiss();
                        }
                    }
                });
                builder_accept.show();
            }
        });

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


