package com.android.imhome.myhome;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import com.android.imhome.R;
import com.android.imhome.util.PreferencesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceTransitionsIntentService extends JobIntentService {

    private static final int JOB_ID = 573;
    public static final String TAG = "GeoFence";
    private static final String CHANNEL_ID = "channel_01";

    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, GeofenceTransitionsIntentService.class, JOB_ID, intent);
    }

    private void sendNotification(String geofenceId) {
        int notificationId = (int) (Math.random() * 100000);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(GeofenceTransitionsIntentService.this);
        builder.setSmallIcon(R.drawable.ic_home)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_home))
                .setContentTitle(getString(R.string.app_name))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentText(getString(R.string.welcome_home))
                .setAutoCancel(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
            }
            builder.setChannelId(CHANNEL_ID);
        }

        if (mNotificationManager != null) {
            mNotificationManager.notify(notificationId, builder.build());
        }
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, "onHandleIntent: " + errorMessage);
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            for (int i = 0; i < triggeringGeofences.size(); i++) {
                if (triggeringGeofences.get(i).getRequestId().equals(MyHomeSettingActivity.GEOFENCE_ID)){
                    sendNotification(triggeringGeofences.get(i).getRequestId());
                    PreferencesUtil.Companion.putIsHomed(getApplicationContext(), geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER);
                    GeoFenceLiveData.Companion.setIsHome(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER);
                    break;
                }
            }
        }
    }
}
