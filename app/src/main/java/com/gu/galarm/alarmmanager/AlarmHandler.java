package com.gu.galarm.alarmmanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.gu.galarm.db.Alarm;
import com.gu.galarm.R;
import com.gu.galarm.alarmmanager.receiver.AlarmReceiver;
import com.gu.galarm.alarmmanager.receiver.TimeShiftReceiver;

import java.util.Calendar;
import java.util.List;


/**
 * Class to control alarm scheduling/cancelling
 */
public class AlarmHandler {

    public static final String EXTRA_ID = "extra_id";

    private final String TAG = "AlarmHandler";

    private Context mContext;
    //private View mSnackBarAnchor;

    public AlarmHandler(Context context) {
        this.mContext = context;
        //this.mSnackBarAnchor = snackBarAnchor;
    }

    /**
     * Schedule alarm TimeShiftReceiver an hour before alarm's time using AlarmManager
     *
     * @param alarm Alarm to schedule
     */
    public void scheduleAlarmEX(Alarm alarm) {
        if (!alarm.isActive()) {
            return;
        }

        // Get PendingIntent to TimeShiftReceiver Broadcast channel
        //Intent intent = new Intent(mContext, TimeShiftReceiver.class);
        Intent intent = new Intent(mContext, AlarmReceiver.class);
        intent.putExtra(EXTRA_ID, alarm.id);
        intent.putExtra("video_id", alarm.video_id);
        intent.putExtra("video_title", alarm.video_title);
        intent.putExtra("label", alarm.label);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, alarm.id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        long nextAlarmRing = 0; // used in Snackbar

        if (alarmManager == null) {
            alarm.setActive(false);
            Toast.makeText(mContext, R.string.alarm_set_error, Toast.LENGTH_LONG).show();
            return;
        }

        if (alarm.isRepeating()) {
            // get list of time to ring in milliseconds for each active day, and repeat weekly
            List<Long> timeToWeeklyRings = alarm.getTimeToWeeklyRings();
            Calendar calendar = Calendar.getInstance();
            for (long millis : timeToWeeklyRings) {
                calendar.setTimeInMillis(millis);
                Log.i(TAG, "Setting weekly repeat at " + calendar.getTime().toString());
                alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        millis,// - TimeUnit.HOURS.toMillis(1), // need to call TimeShift an hour early
                        AlarmManager.INTERVAL_DAY * 7,
                        pendingIntent);

                if (millis < nextAlarmRing || nextAlarmRing == 0) nextAlarmRing = millis;
            }
        } else {
            nextAlarmRing = alarm.getTimeToNextRing(); // get time until next alarm ring

            //long alarmMils = nextAlarmRing - TimeUnit.HOURS.toMillis(1);
            //long alarmMils = System.currentTimeMillis() + 20* 1000;//nextAlarmRing - System.currentTimeMillis();

            Log.i(TAG, "setting alarm " + alarm.id + " to AlarmManager");
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    nextAlarmRing, // need to call TimeShift an hour early
                    pendingIntent);
        }

        String timeUntilNextRing = getStringOfTimeUntilNextRing(nextAlarmRing - System.currentTimeMillis());
        // Show snackbar to notify user
        Toast.makeText(mContext, String.format(mContext.getString(R.string.alarm_set), timeUntilNextRing), Toast.LENGTH_LONG).show();

    }

    public void scheduleAlarm(Alarm alarm) {
        if (!alarm.isActive()) {
            return;
        }

        Intent intent = new Intent(mContext, AlarmReceiver.class);
        intent.putExtra(EXTRA_ID, alarm.id);
        intent.putExtra("video_id", alarm.video_id);
        intent.putExtra("video_title", alarm.video_title);
        intent.putExtra("label", alarm.label);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, alarm.id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        //long nextAlarmRing = System.currentTimeMillis() + 1000 * 20;//alarm.getTimeToNextRing(); // get time until next alarm ring
        long nextAlarmRing = 0;
        nextAlarmRing = alarm.getTimeToNextRing();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextAlarmRing, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextAlarmRing, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, nextAlarmRing, pendingIntent);
        }

        String timeUntilNextRing = getStringOfTimeUntilNextRing(nextAlarmRing - System.currentTimeMillis());
        Toast.makeText(mContext, String.format(mContext.getString(R.string.alarm_set), timeUntilNextRing), Toast.LENGTH_LONG).show();

    }


    public void snoozeAlarm(Alarm alarm) {
        if (!alarm.isActive()) {
            return;
        }

        // Get PendingIntent to TimeShiftReceiver Broadcast channel
        //Intent intent = new Intent(mContext, TimeShiftReceiver.class);
        Intent intent = new Intent(mContext, AlarmReceiver.class);
        intent.putExtra(EXTRA_ID, alarm.id);
        intent.putExtra("video_id", alarm.video_id);
        intent.putExtra("video_title", alarm.video_title);
        intent.putExtra("label", alarm.label);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, alarm.id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        long nextAlarmRing = System.currentTimeMillis() + 10 * 60 * 1000;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextAlarmRing, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextAlarmRing, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, nextAlarmRing, pendingIntent);
        }

        //String timeUntilNextRing = getStringOfTimeUntilNextRing(nextAlarmRing - System.currentTimeMillis());
        // Show snackbar to notify user
        //Toast.makeText(mContext, String.format(mContext.getString(R.string.alarm_set), timeUntilNextRing), Toast.LENGTH_LONG).show();

    }

    /**
     * Converts milliseconds to # day(s), # hour(s), # minute(s)
     *
     * @param millisToRing milliseconds to next alarm ring
     * @return a user readable String of time until next alarm ring
     */
    private String getStringOfTimeUntilNextRing(long millisToRing) {
        long seconds = millisToRing / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        minutes -= hours * 60;
        hours -= days * 24;

        if (days == 0 && hours == 0 && minutes == 0) {
            return "less than a minute";
        }

        StringBuilder sbTime = new StringBuilder();
        if (days >= 1) {
            sbTime.append(days);
            sbTime.append(days > 1 ? " days" : " day");
            if (hours >= 1 || minutes >= 1) {
                sbTime.append(", ");
            }
        }
        if (hours >= 1) {
            sbTime.append(hours);
            sbTime.append(hours > 1 ? " hours" : " hour");
            if (minutes >= 1) {
                sbTime.append(", ");
            }
        }
        if (minutes >= 1) {
            sbTime.append(minutes);
            sbTime.append(minutes > 1 ? " minutes" : " minute");
        }

        return sbTime.toString();
    }

    /**
     * Schedule alarm notification based on time until next alarm
     *
     * @param timeToRing time to next alarm in milliseconds
     * @param alarmID    ID of alarm to ring
     */
    public void scheduleAlarmWithTime(int timeToRing, int alarmID) {
        // Calculate time until alarm from millis since epoch
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, timeToRing);
        long alarmTimeInMillis = calendar.getTimeInMillis();

        // Get PendingIntent to AlarmReceiver Broadcast channel
        Intent intent = new Intent(mContext, AlarmReceiver.class);
        intent.putExtra(EXTRA_ID, alarmID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.i(TAG, "setting timed alarm " + alarmID + " to AlarmManager for " + alarmTimeInMillis + " milliseconds");
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent);
        }
    }

    /**
     * Cancel alarm notification and TimeShiftIntent using AlarmManager
     *
     * @param alarm Alarm to cancel
     */
    public void cancelAlarm(Alarm alarm) {
        if (alarm.isActive()) {
            return;
        }

        // Get PendingIntent to AlarmReceiver Broadcast channel
        Intent intent = new Intent(mContext, AlarmReceiver.class);
        Intent shiftIntent = new Intent(mContext, TimeShiftReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, alarm.id, intent, PendingIntent.FLAG_NO_CREATE);
        PendingIntent shiftPendingIntent = PendingIntent.getBroadcast(mContext, alarm.id, shiftIntent, PendingIntent.FLAG_NO_CREATE);

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Log.i(TAG, "cancelling alarm " + alarm.id);

        if (alarmManager != null) {
            // PendingIntent may be null if the alarm hasn't been set
            if (pendingIntent != null) alarmManager.cancel(pendingIntent);
            if (shiftPendingIntent != null) alarmManager.cancel(shiftPendingIntent);
        }

        // Show snackbar to notify user
        Toast.makeText(mContext, String.format(mContext.getString(R.string.alarm_cancelled)), Toast.LENGTH_LONG).show();
        //Snackbar.make(mSnackBarAnchor, mContext.getString(R.string.alarm_cancelled), Snackbar.LENGTH_SHORT).show();
    }
}
