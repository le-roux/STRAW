package straw.polito.it.straw.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import straw.polito.it.straw.R;
import straw.polito.it.straw.activities.DisplayReservationsActivity;
import straw.polito.it.straw.activities.HomeActivity;
import straw.polito.it.straw.utils.Logger;

public class MyGcmListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Logger.d("Message Received!");
        if(data.containsKey("reservation")){
            String msg = data.getString("reservation");

            sendNotification(msg);
        }

    }
    private void sendNotification(String message) {
        String arr[]=message.split("::::");

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.pizza)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.pizza))
                        .setContentTitle("STRAW")
                        .setContentText("You have a new reservation!");

        mBuilder.setAutoCancel(true);

        Intent resultIntent = new Intent(this, DisplayReservationsActivity.class);
        resultIntent.putExtra("GameID", arr[0]);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(HomeActivity.class);
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

}
