package straw.polito.it.straw.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import straw.polito.it.straw.StrawApplication;
import straw.polito.it.straw.utils.Logger;

public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    private SharedPreferences sharedPreferences;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);

        try {

            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(StrawApplication.senderID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Logger.d("GCM Registration Token: " + token);

            sendRegistrationToServer(token);

            sharedPreferences.edit().putBoolean("tokenSW", true).apply();
        } catch (Exception e) {
            sharedPreferences.edit().putBoolean("tokenSW", false).apply();
        }
        Intent registrationComplete = new Intent("complete");
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        sharedPreferences.edit().putString("tokenGCM",token).apply();
    }

}
