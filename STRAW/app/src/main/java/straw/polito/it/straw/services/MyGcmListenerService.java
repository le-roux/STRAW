package straw.polito.it.straw.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.android.gms.gcm.GcmListenerService;

import straw.polito.it.straw.R;
import straw.polito.it.straw.activities.DisplayInvitationActivity;
import straw.polito.it.straw.activities.DisplayReservationsActivity;
import straw.polito.it.straw.activities.InviteFriendActivity;
import straw.polito.it.straw.utils.Logger;

public class MyGcmListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Logger.d("Message Received!");
        if(data.containsKey("reservation")){
            String msg = data.getString("reservation");
            sendNotificationReservation(msg);
        }
        if(data.containsKey("invitation")){
            String msg = data.getString("invitation");
            String restaurantName = data.getString(InviteFriendActivity.RESTAURANT);
            sendNotificationInvitation(msg, restaurantName);
        }
        if(data.containsKey("res_change")){
            String msg = data.getString("res_change");
            String restaurantName = data.getString("restaurant");
            sendNotificationReservationChange(msg, restaurantName);
        }


    }
    private void sendNotificationReservation(String message) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.pizza)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.pizza))
                        .setContentTitle("STRAW")
                        .setContentText("You have a new reservation!");

        mBuilder.setAutoCancel(true);

        Intent resultIntent = new Intent(this, DisplayReservationsActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        (int) System.currentTimeMillis(),
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setLocalOnly(true);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
        Logger.d("NOTIFICATION CREATED!!!!!!!!!!!!!!!" );
    }
    private void sendNotificationReservationChange(String message, String restaurantName) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.pizza)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.pizza))
                        .setContentTitle("STRAW")
                        .setContentText(message);

        mBuilder.setAutoCancel(true);

        Intent resultIntent = new Intent(this, DisplayReservationsActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        (int) System.currentTimeMillis(),
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setLocalOnly(true);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
        Logger.d("NOTIFICATION CREATED!!!!!!!!!!!!!!!" );
    }
    private void sendNotificationInvitation(String message, String restaurantName) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.pizza)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.pizza))
                        .setContentTitle("STRAW")
                        .setContentText("Your friend "+message+" has invited you to eat!");

        Intent resultIntent = new Intent(this, DisplayInvitationActivity.class);
        resultIntent.putExtra(InviteFriendActivity.INVITATION, message);
        resultIntent.putExtra(InviteFriendActivity.RESTAURANT, restaurantName);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        (int) System.currentTimeMillis(),
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setLocalOnly(true);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
        Logger.d("NOTIFICATION CREATED!!!!!!!!!!!!!!!" );
    }

}
