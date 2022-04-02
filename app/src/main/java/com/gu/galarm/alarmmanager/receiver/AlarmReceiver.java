package com.gu.galarm.alarmmanager.receiver;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.gu.galarm.alarmmanager.AlarmHandler;
import com.gu.galarm.ui.activity.YoutubePlayActivity;
import com.gu.galarm.util.AlarmRepository;
import com.gu.galarm.R;

/**
 * BroadcastReceiver to setup and display alarm notification
 */
public class AlarmReceiver extends BroadcastReceiver {

    private final String TAG = "AlarmReceiver";
    private final String CHANNEL_ID = "AlarmReceiverChannel";
    private SharedPreferences mPreferences;
    private Context mContext;

    private String mStrVideoId = "";
    private String mStrVideoTitle = "";
    private String mStrLabel = "";

    private AlarmRepository mRepository;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "received alarm intent");

        //android.os.Debug.waitForDebugger();
        // Initialize preferences
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        int alarmID = intent.getIntExtra(AlarmHandler.EXTRA_ID, 0);
        mStrVideoId = intent.getStringExtra("video_id");
        mStrVideoTitle = intent.getStringExtra("video_title");
        mStrLabel = intent.getStringExtra("label");
        mContext = context;

        startYoutubePlay(context, alarmID);
        //Toast.makeText(context,"A;ladfasdfsdafasdf", Toast.LENGTH_LONG);

        /*
        Application application = ((Application)context.getApplicationContext());
        mRepository = new AlarmRepository(application);
        try {
            Alarm alarm = mRepository.getAlarmById(alarmID);
            alarm.setActive(false);
            mRepository.update(alarm);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
        //mAllAlarms = mRepository.getAllAlarms();


        /*
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create and add notification channel
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, TAG, NotificationManager.IMPORTANCE_HIGH);
        if (notificationManager != null) notificationManager.createNotificationChannel(mChannel);

        Notification notification = buildNotification(
                context,
                getStopAlarmIntent(context, alarmID),
                getSnoozeAlarmIntent(context, alarmID)
        );

        // Play alarm ringing sound
        AlarmSoundControl alarmSoundControl = AlarmSoundControl.getInstance();
        alarmSoundControl.playAlarmSound(context.getApplicationContext());

        Log.i(TAG, "displaying notification for alarm " + alarmID);
        if (notificationManager != null) notificationManager.notify(alarmID, notification);
        */

    }

    private void startYoutubePlay(Context context, int alramId) {
        Intent intent = new Intent(context, YoutubePlayActivity.class);
        intent.putExtra("alarm_id", alramId);
        intent.putExtra("type", 1);
        intent.putExtra("video_id", mStrVideoId);
        intent.putExtra("video_title", mStrVideoTitle);
        intent.putExtra("label", mStrLabel);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        /*
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        try {
            pi.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
        */
        //mRepository = new AlarmRepository(application);
        //mAllAlarms = mRepository.getAllAlarms();
    }

    /**
     * Get PendingIntent to Snooze Alarm
     *
     * @param context current App context
     * @param alarmID ID of alarm to handle
     * @return PendingIntent to AlarmSnooze(Broadcast)Receiver
     */
    private PendingIntent getSnoozeAlarmIntent(Context context, int alarmID) {
        Intent snoozeAlarmIntent = new Intent(context, AlarmSnoozeReceiver.class);
        snoozeAlarmIntent.putExtra(AlarmHandler.EXTRA_ID, alarmID);
        snoozeAlarmIntent.setAction("Snooze Alarm");
        return PendingIntent.getBroadcast(context, 0, snoozeAlarmIntent, 0);
    }

    /**
     * Get PendingIntent to Stop Alarm
     *
     * @param context current App context
     * @param alarmID ID of alarm to handle
     * @return PendingIntent to AlarmStop(Broadcast)Receiver
     */
    private PendingIntent getStopAlarmIntent(Context context, int alarmID) {
        Intent stopAlarmIntent = new Intent(context, AlarmStopReceiver.class);
        stopAlarmIntent.putExtra(AlarmHandler.EXTRA_ID, alarmID);
        stopAlarmIntent.setAction("Stop Alarm");
        return PendingIntent.getBroadcast(context, 0, stopAlarmIntent, 0);
    }

    /**
     * Build the alarm notification
     *
     * @param context                  current App context
     * @param stopAlarmPendingIntent   PendingIntent object to stop the alarm ringing
     * @param snoozeAlarmPendingIntent Pending Intent object to snooze the alarm ringing
     * @return a Notification object of the built alarm notification
     */
    private Notification buildNotification(Context context, PendingIntent stopAlarmPendingIntent, PendingIntent snoozeAlarmPendingIntent) {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.alarm_notification_title))
                .addAction(R.drawable.ic_launcher_background, context.getString(R.string.stop),
                        stopAlarmPendingIntent)
                .addAction(R.drawable.ic_launcher_background, context.getString(R.string.snooze),
                        snoozeAlarmPendingIntent);

        // Set vibrate based on preferences, default is vibrate
        if (!mPreferences.getBoolean("alarm_ring_vibrate", true)) {
            Log.i(TAG, "notification vibrate set to false");
            notification.setVibrate(null);
        }

        return notification.build();
    }
}
