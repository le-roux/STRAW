package straw.polito.it.straw.adapter;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.activities.DisplayReservationsActivity;
import straw.polito.it.straw.utils.DateDisplay;
import straw.polito.it.straw.data.Reservation;
import straw.polito.it.straw.utils.TimerDisplay;
import straw.polito.it.straw.utils.TimePickerFragment;

/**
 * Created by Sylvain on 07/04/2016.
 */
public class ReservationAdapter extends BaseAdapter {

    private ArrayList<Reservation> reservationList;
    private static Context context;
    private DisplayReservationsActivity parentActivity;

    public static final String ADAPTER = "Adapter";

    public ReservationAdapter (Context context) {
        this.reservationList = new ArrayList<Reservation>();
        this.context = context;
    }

    public ReservationAdapter(Context context, ArrayList<Reservation> reservationList,
                              DisplayReservationsActivity activity) {
        this.reservationList = reservationList;
        this.context = context;
        this.parentActivity = activity;
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

        //Get the views of the item
        TextView numberPeople = (TextView) convertView.findViewById(R.id.number_people);
        DateDisplay dateDisplay = (DateDisplay) convertView.findViewById(R.id.Date);
        TimerDisplay timerDisplay = (TimerDisplay)convertView.findViewById(R.id.Timer);
        TextView plates = (TextView) convertView.findViewById(R.id.plates);
        TextView moreOptions = (TextView) convertView.findViewById(R.id.moreOptionsLink);

        moreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
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
                            showTimePickerFragmentDialog(position);
                        } else if (item == CANCEL) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
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
        if(position < this.reservationList.size()) {
            Reservation reservation = this.reservationList.get(position);
            numberPeople.setText(reservation.getNumberPeople() + " " +
                    context.getString(R.string.Persons));
            dateDisplay.setDate(reservation.getYear(), reservation.getMonth(), reservation.getDay());
            timerDisplay.setTime(reservation.getHourOfDay(), reservation.getMinutes());
            plates.setText(this.reservationList.get(position).getFoodList());
        }

        return convertView;
    }

    public static void confirm_accept(View view) {
        //TO DO : Send notification to the customer
    }
    public static void confirm_refuse() {
        //TO DO :Send notification to the customer
    }

    /**
     * Display a time picker on top of the current activity
     * @param position
     */
    public void showTimePickerFragmentDialog(int position) {
        DialogFragment fragment = new TimePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Reservation.RESERVATION, position);
        bundle.putBoolean(ReservationAdapter.ADAPTER, true);
        fragment.setArguments(bundle);
        fragment.show(this.parentActivity.getFragmentManager(), "timePicker");
    }
}