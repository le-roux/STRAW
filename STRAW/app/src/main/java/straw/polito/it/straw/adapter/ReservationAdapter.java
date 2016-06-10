package straw.polito.it.straw.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import straw.polito.it.straw.R;
import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.activities.DisplayReservationsActivity;
import straw.polito.it.straw.data.Reservation;
import straw.polito.it.straw.utils.DatabaseUtils;
import straw.polito.it.straw.utils.DateDisplay;
import straw.polito.it.straw.utils.TimerDisplay;

/**
 * Created by Sylvain on 07/04/2016.
 */
public abstract class ReservationAdapter extends BaseAdapter {

    protected ArrayList<Reservation> reservationList;
    protected static Context context;
    protected Activity parentActivity;
    protected DatabaseUtils databaseUtils;
    protected Button acceptButton;
    protected ImageView statusImage;
    protected TextView place;

    public static final String ADAPTER = "Adapter";
    public static final String ACCEPT_ICON = "android.resource://straw.polito.it.straw/drawable/check";
    //Created by ?? from Noun Project
    public static final String CANCEL_ICON = "android.resource://straw.polito.it.straw/drawable/discard";
    //Created by Herbert Spencer from Noun Project
    public static final String WAIT_ICON = "android.resource://straw.polito.it.straw/drawable/wait";
    //Created by Arthur Shlain from Noun Project

    public ReservationAdapter (Context context) {
        this.reservationList = new ArrayList<Reservation>();
        this.context = context;
        StrawApplication application = (StrawApplication)((Activity)context).getApplication();
        this.databaseUtils = application.getDatabaseUtils();
    }

    public ReservationAdapter(Context context, ArrayList<Reservation> reservationList,
                              Activity activity) {
        this.reservationList = reservationList;
        this.context = context;
        this.parentActivity = activity;
        StrawApplication application = (StrawApplication)((Activity)context).getApplication();
        this.databaseUtils = application.getDatabaseUtils();
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

    public ArrayList<Reservation> getReservationList() {
        return this.reservationList;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = setSpecificView(layoutInflater);
        }

        //Get the views of the item
        TextView numberPeople = (TextView) convertView.findViewById(R.id.number_people);
        final DateDisplay dateDisplay = (DateDisplay) convertView.findViewById(R.id.Date);
        TimerDisplay timerDisplay = (TimerDisplay)convertView.findViewById(R.id.Timer);
        TextView plates = (TextView) convertView.findViewById(R.id.plates);
        this.acceptButton = (Button) convertView.findViewById(R.id.AcceptButton);
        this.statusImage = (ImageView)convertView.findViewById(R.id.state);
        this.place = (TextView)convertView.findViewById(R.id.place);
        if (this.reservationList.get(position).getStatus() == Reservation.DISCARDED)
            setSpecificItems(convertView, position, false);
        else
            setSpecificItems(convertView, position, true);

        init(position);
        switch(this.reservationList.get(position).getPlace()) {
            case (Reservation.INSIDE) : {
                this.place.setText(R.string.Inside);
                break;
            }
            case (Reservation.OUTSIDE) : {
                this.place.setText(R.string.Outside);
                break;
            }
            default : {
                this.place.setText(R.string.NoPreference);
                break;
            }
        }

        acceptButton.setOnClickListener(new View.OnClickListener() {
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
                            databaseUtils.updateReservationStatus(reservationList.get(position).getId(), Reservation.ACCEPTED);
                            reservationList.get(position).setStatus(Reservation.ACCEPTED);
                            setIconVisible(ACCEPT_ICON);
                            ReservationAdapter.this.notifyDataSetChanged();
                            Toast.makeText(context, context.getString(R.string.OrderAcceptedToast),
                                    Toast.LENGTH_LONG).show();
                            databaseUtils.sendReservationNotification(reservationList.get(position).getCustomer(),context.getString(R.string.AcceptOrder),reservationList.get(position).getRestaurant());
                            return;

                        } else if (options[item].equals(context.getString(R.string.Cancel))) {
                            dialog.dismiss();
                        }
                    }
                });
                builder_accept.show();
            }
        });

        if(this.reservationList != null) {
            //Fill the view with the appropriate values
            if (position < this.reservationList.size()) {
                Reservation reservation = this.reservationList.get(position);
                StringBuilder builder = new StringBuilder();
                builder.append(reservation.getNumberPeople())
                        .append(' ')
                        .append(context.getString(R.string.Persons));
                numberPeople.setText(builder.toString());
                dateDisplay.setDate(reservation.getYear(), reservation.getMonth(), reservation.getDay());
                timerDisplay.setTime(reservation.getHourOfDay(), reservation.getMinutes());
                plates.setText(this.reservationList.get(position).getFoodList());
            }
        }
        return convertView;
    }

    /**
     * Change the icon/button displayed on the right of the item.
     * @param icon : null to make the "Accept" button visible,
     *             one value among WAIT_ICON, ACCEPT_ICON and CANCEL_ICON
     *             otherwise.
     */
    public void setIconVisible(String icon) {
        if (icon == null) {
            this.acceptButton.setVisibility(View.VISIBLE);
            this.statusImage.setVisibility(View.INVISIBLE);
        } else {
            this.statusImage.setImageURI(Uri.parse(icon));
            this.statusImage.setVisibility(View.VISIBLE);
            this.acceptButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Allow the subclasses to add other View objects (provided that they
     * are present in the layout set).
     * @param view : The View object of the layout of the item.
     * @param position : The position of the current item in the reservationList.
     */
    protected abstract void setSpecificItems(View view, int position, boolean modifiable);

    /**
     * Adapt the icon/button according to the status of the reservation.
     * @param position : The position of the current item in the reservationList.
     */
    public abstract void init(int position);

    /**
     * Set the desired layout for the items.
     * @param layoutInflater : A layoutInflater used to retrieve the View object from the layout.
     * @return : the view corresponding to the layout.
     */
    protected abstract View setSpecificView(LayoutInflater layoutInflater);
}