package com.gu.galarm.alarmmanager.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gu.galarm.alarmmanager.AlarmSoundControl;
import com.gu.galarm.alarmmanager.AlarmHandler;

/**
 * BroadcastReceiver to stop alarm ringing
 */
public class AlarmStopReceiver extends BroadcastReceiver {

    private final String TAG = "AlarmStopReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        int alarmID = intent.getIntExtra(AlarmHandler.EXTRA_ID, 0);

        Log.i(TAG, "Stopping ringing and cancelling alarm " + alarmID);
        AlarmSoundControl alarmSoundControl = AlarmSoundControl.getInstance();
        alarmSoundControl.stopAlarmSound();

        // Dismiss notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) notificationManager.cancelAll();
    }
}
