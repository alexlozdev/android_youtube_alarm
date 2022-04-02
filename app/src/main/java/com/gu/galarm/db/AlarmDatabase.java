package com.gu.galarm.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.gu.galarm.db.converter.BooleanArrayConverter;
import com.gu.galarm.db.converter.DateConverter;

import java.util.Calendar;
import java.util.Date;

/**
 * Backend Database
 */
@Database(entities = {Alarm.class}, version = 1, exportSchema = false)
@TypeConverters({DateConverter.class, BooleanArrayConverter.class})
public abstract class AlarmDatabase extends RoomDatabase {

    private static AlarmDatabase mINSTANCE;

    @VisibleForTesting
    public static final String DATABASE_NAME = "alarm-db";

    public abstract AlarmDao alarmModel();

    public static AlarmDatabase getInstance(final Context context) {
        if (mINSTANCE == null) {
            synchronized (AlarmDatabase.class) {
                if (mINSTANCE == null) {
                    mINSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AlarmDatabase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration() // TODO Add Proper Migration
                            .addCallback(roomDatabaseCallback)
                            .build();
                }
            }
        }
        return mINSTANCE;
    }

    /**
     * Override the onCreate method to populate the database.
     */
    private static RoomDatabase.Callback roomDatabaseCallback = new RoomDatabase.Callback() {

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            //new PopulateDbAsync(mINSTANCE).execute();
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
    };

    /**
     * Populate the database in the background when app is first created.
     */
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final AlarmDao alarmModel;

        PopulateDbAsync(AlarmDatabase db) {
            alarmModel = db.alarmModel();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            String str = "08:00 am";
            //DateFormat formatter = new SimpleDateFormat("hh:mm aa");
            Date time = Calendar.getInstance().getTime();
            /*
            try {
                time = formatter.parse(str);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }*/
            boolean[] activeDays = {false, true, true, true, true, true, false};

            //LatLng start = new LatLng(43.6426, 79.3871);
            //String startPlace = "CN Tower";
            //LatLng end = new LatLng(43.6777, 79.6248);
            //String endPlace = "Toronto Pearson International Airport";

            Alarm alarm = new Alarm(
                    time, false, activeDays, "alarm", "qRGjSmk2ccQ", "Bear");
                    //start, end,
                    //startPlace, endPlace,
                    //"drive"
            //);
            alarmModel.insert(alarm);
            return null;
        }
    }
}
