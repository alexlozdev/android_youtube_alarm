package com.gu.galarm.alarmmanager.receiver;

import android.app.AlarmManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gu.galarm.db.Alarm;
import com.gu.galarm.alarmmanager.AlarmHandler;
import com.gu.galarm.util.AlarmRepository;

import java.util.List;


public class RestoreOnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Application application = (Application) context.getApplicationContext();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        AlarmHandler mAlarmHandler = new AlarmHandler(context);
        AlarmRepository mRepository = new AlarmRepository(application);
        List<Alarm> mAllAlarms;
        mAllAlarms = mRepository.getAllAlarms().getValue();
        //alarm.setActive(false);
        //mRepository.update(alarm);

        for (Alarm alarm : mAllAlarms) {
            if (alarm.isActive()){
                mAlarmHandler.scheduleAlarm(alarm);
            }

        }


    }
}
