package com.gu.galarm.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;


import com.gu.galarm.db.converter.BooleanArrayConverter;
import com.gu.galarm.db.converter.DateConverter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Entity(tableName = "alarms")
//@TypeConverters({DateConverter.class, BooleanArrayConverter.class, LatLngConverter.class})
@TypeConverters({DateConverter.class, BooleanArrayConverter.class})
public class Alarm implements Parcelable {

    // Class members

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "alarm_id")
    public int id;

    @ColumnInfo(name = "alarm_time")
    public Date time;

    @ColumnInfo(name = "alarm_active")
    public boolean active;

    // Must have length 7
    // i'th item being true means alarm is active on i'th day
    @ColumnInfo(name = "alarm_active_days")
    public boolean[] activeDays;

    @ColumnInfo(name = "alarm_label")
    public String label;

    @ColumnInfo(name = "video_id")
    public String video_id;

    @ColumnInfo(name = "video_title")
    public String video_title;

    /*
    @ColumnInfo(name = "alarm_start_point")
    public LatLng startPoint;
    @ColumnInfo(name = "alarm_end_point")
    public LatLng endPoint;
    @ColumnInfo(name = "alarm_start_place")
    public String startPlace;
    @ColumnInfo(name = "alarm_end_place")
    public String endPlace;
    @ColumnInfo(name = "alarm_transportation_mode")
    public String transMode;
    */
    public Alarm() {
    }

    // Getters/Setters

    /*
    public void setTime(String stringTime) {
        DateFormat formatter = new SimpleDateFormat("hh:mm aa");
        try {
            time = formatter.parse(stringTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }*/

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isRepeating() {
        // Iterate through active days to see if one is true
        boolean isRepeat = false;
        for (boolean bool : activeDays) {
            if (bool) isRepeat = true;
        }
        return isRepeat;
    }


    // Ignored Members

    // Used to get user-readable String representation of activeDays
    @Ignore
    private static final String[] daysOfWeek =
            {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    @Ignore
    public Alarm(Date time, boolean active, boolean[] activeDays, String label, String video_id, String video_title) {
        this.time = time;
        this.active = active;
        this.activeDays = activeDays;
        this.label = label;
        this.video_id = video_id;
        this.video_title = video_title;

        //this.startPoint = startPoint;
        //this.endPoint = endPoint;
        //this.startPlace = startPlace;
        //this.endPlace = endPlace;
        //this.transMode = transMode;
    }

    /**
     * Get String of alarm ring time in 12 hour format
     */
    public String getStringTime() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        String stringTime = getFormattedTime(hour, minute);
        return stringTime;
    }

    private String getFormattedTime(int hour, int minute) {
        String meridian = "AM";
        if (hour >= 12) {
            meridian = "PM";
        }

        if (hour > 12) {
            hour -= 12;
        } else if (hour == 0) {
            hour = 12;
        }

        String formattedTime;
        if (minute < 10) {
            formattedTime = hour + ":0" + minute + " " + meridian;
        } else {
            formattedTime = hour + ":" + minute + " " + meridian;
        }

        return formattedTime;
    }

    @Ignore
    public String getStringOfActiveDays() {
        return getStringOfActiveDays(activeDays);
    }

    /**
     * Get a simple user-readable representation of activeDays
     *
     * @param activeDays boolean array of days alarm is active
     * @return a String of alarm's active days
     */
    @Ignore
    public static String getStringOfActiveDays(boolean[] activeDays) {
        // Build string based on which indices are true in activeDays
        StringBuilder builder = new StringBuilder();
        int activeCount = 0;
        for (int i = 0; i < 7; i++) {
            if (activeDays[i]) {
                String formattedDay = daysOfWeek[i].substring(0, 3) + ", ";
                builder.append(formattedDay);
                activeCount++;
            }
        }

        if (activeCount == 7) {
            return "everyday";
        } else if (activeCount == 0) {
            return "never";
        }

        boolean satInArray = activeDays[6]; // "Saturday" in activeDays
        boolean sunInArray = activeDays[0]; // "Sunday" in activeDays

        if (satInArray && sunInArray && activeCount == 2) {
            return "weekends";
        } else if (!satInArray && !sunInArray && activeCount == 5) {
            return "weekdays";
        }

        if (builder.length() > 1) {
            builder.setLength(builder.length() - 2);
        }

        return builder.toString();
    }

    /**
     * Get time until next alarm ring in milliseconds since epoch
     */
    @Ignore
    public long getTimeToNextRing() {
        Calendar calendar = getAlarmTimeAsCalendar();

        int nowDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (isRepeating()) {

            if (activeDays[nowDayOfWeek -1] == true) {
                if (calendar.getTimeInMillis() > System.currentTimeMillis() + 1000 * 20) {
                    return calendar.getTimeInMillis();
                }
            }

            // calendar day : 1. sunday, 2: monday, 7 : saturday
            int nextDay = nowDayOfWeek;
            for (int i = 1; i <= 7; i++) {
                nextDay = nextDay + 1;
                if (nextDay == 8)
                    nextDay = 1;

                // activeday 0: sunday
                if (activeDays[nextDay-1] == true) {
                    return calendar.getTimeInMillis() + i*24*3600*1000;
                }

            }

        } else {
            if (calendar.getTimeInMillis() < System.currentTimeMillis()) { // alarm time has passed for today
                calendar.add(Calendar.DAY_OF_MONTH, 1); // set alarm to ring tomorrow
            }
        }


        Log.i("Alarm.java", "Alarm ring time is " + calendar.getTime().toString());
        return calendar.getTimeInMillis();
    }

    /**
     * Get Alarm time as a calendar object set to current date\
     */
    @Ignore
    @NonNull
    private Calendar getAlarmTimeAsCalendar() {
        //String[] timeString = new SimpleDateFormat("HH:mm").format(this.time).split(":");
        //int hour = Integer.parseInt(timeString[0]);
        //int minute = Integer.parseInt(timeString[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, hour);
        now.set(Calendar.MINUTE, minute);
        now.set(Calendar.SECOND, 0);

        return now;
    }

    /**
     * Get the time to ring in milliseconds since epoch for each day alarm is active
     *
     * @return a List of milliseconds to the next alarm ring for each active day
     */
    @Ignore
    public List<Long> getTimeToWeeklyRings() {
        Calendar calendar = getAlarmTimeAsCalendar();
        long currAlarmTime = calendar.getTimeInMillis();

        List<Long> weekRingTimes = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            if (activeDays[i]) { // if alarm is active on that day
                weekRingTimes.add(getCorrectRingDay(currAlarmTime, i));
            }
        }

        return weekRingTimes;
    }

    /**
     * Correctly get the next alarm ring day based on the day it's active and current day of week
     *
     * @param alarmTime long of milliseconds since epoch of alarm time **today**
     * @param activeDay int of day alarm is active on
     * @return long of milliseconds since epoch of next alarm ring time on active day
     */
    @Ignore
    private long getCorrectRingDay(long alarmTime, int activeDay) {
        Calendar currentCalendar = Calendar.getInstance();
        int currDay = currentCalendar.get(Calendar.DAY_OF_WEEK);
        currDay--; // index using 0 = Sunday

        if ((alarmTime < System.currentTimeMillis() && currDay == activeDay) // alarm time has passed for today
                || currDay != activeDay) { // current day is not an active day

            if (activeDay > currDay) { // Alarm is active a later day of the week
                alarmTime += TimeUnit.MILLISECONDS.convert(activeDay - currDay, TimeUnit.DAYS);
            } else { // Have to move the alarm time to next week's active day
                alarmTime += TimeUnit.MILLISECONDS.convert(7 - currDay, TimeUnit.DAYS);
                alarmTime += TimeUnit.MILLISECONDS.convert(activeDay, TimeUnit.DAYS);
            }
        }
        return alarmTime;
    }

    // Parcelable implementation

    @Ignore
    public int describeContents() {
        return 0;
    }

    /**
     * Write all alarm contents to Parcel out
     */
    @Ignore
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeLong(DateConverter.toTimestamp(time));
        out.writeByte((byte) (active ? 1 : 0));
        out.writeBooleanArray(activeDays);
        out.writeString(label);
        out.writeString(video_id);
        out.writeString(video_title);

        //Bundle bundle = new Bundle();
        //bundle.putParcelable("START_POINT", startPoint);
        //bundle.putParcelable("END_POINT", endPoint);
        //bundle.putString("START_PLACE", startPlace);
        //bundle.putString("END_PLACE", endPlace);
        //bundle.putString("TRANS_MODE", transMode);

        //out.writeBundle(bundle);
    }



    @Ignore
    public static final Parcelable.Creator<Alarm> CREATOR = new Parcelable.Creator<Alarm>() {
        public Alarm createFromParcel(Parcel in) {
            return new Alarm(in);
        }

        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };

    /**
     * Construct alarm from Parcel of data written to using writeToParcel (above)
     */
    @Ignore
    private Alarm(Parcel in) {
        id = in.readInt();

        Long timestamp = in.readLong();
        time = DateConverter.toDate(timestamp);

        active = in.readByte() != 0;

        activeDays = new boolean[7];
        in.readBooleanArray(activeDays);

        label = in.readString();

        video_id = in.readString();

        video_title = in.readString();

        //Bundle args = in.readBundle(getClass().getClassLoader());
        //startPoint = args.getParcelable("START_POINT");
        //endPoint = args.getParcelable("END_POINT");
        //startPlace = args.getString("START_PLACE");
        //endPlace = args.getString("END_PLACE");
        //transMode = args.getString("TRANS_MODE");
    }
}
