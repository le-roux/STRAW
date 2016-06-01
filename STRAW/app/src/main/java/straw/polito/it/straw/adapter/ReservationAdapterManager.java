package straw.polito.it.straw.adapter;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.activities.DisplayReservationsActivity;
import straw.polito.it.straw.data.Reservation;
import straw.polito.it.straw.fragments.TimePickerFragment;

/**
 * Created by Sylvain on 07/04/2016.
 */
public class ReservationAdapterManager extends ReservationAdapter {

    public ReservationAdapterManager(Context context) {
        super(context);
    }

    public ReservationAdapterManager(Context context, ArrayList<Reservation> reservationList,
                                     DisplayReservationsActivity activity) {
        super(context, reservationList, activity);
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
        bundle.putBoolean(ReservationAdapterManager.ADAPTER, true);
        fragment.setArguments(bundle);
        fragment.show(this.parentActivity.getFragmentManager(), "timePicker");
    }

    @Override
    protected void setSpecificItems(View view, final int position) {
        TextView moreOptions = (TextView) view.findViewById(R.id.moreOptionsLink);
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
                                        databaseUtils.updateReservationStatus(reservationList.get(position).getId(), Reservation.DISCARDED);
                                        ReservationAdapterManager.this.notifyDataSetChanged();
                                        reservationList.get(position).setStatus(Reservation.DISCARDED);
                                        setIconVisible(CANCEL_ICON);
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

    }

    @Override
    public void init(int position) {
        if (position < this.reservationList.size() && position >= 0) {
            switch (this.reservationList.get(position).getStatus()) {
                case (Reservation.PENDING): {
                    setIconVisible(null);
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
                    setIconVisible(WAIT_ICON);
                    break;
                }
            }
        }
    }

    @Override
    protected View setSpecificView(LayoutInflater layoutInflater) {
        return layoutInflater.inflate(R.layout.reservation, null);
    }
}