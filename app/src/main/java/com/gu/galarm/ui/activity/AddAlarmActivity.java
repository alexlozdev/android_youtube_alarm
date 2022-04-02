package com.gu.galarm.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.gu.galarm.R;
import com.gu.galarm.alarmmanager.AlarmHandler;
import com.gu.galarm.db.Alarm;
import com.gu.galarm.ui.SetRepeatDaysDialogFragment;
import com.gu.galarm.viewmodel.AlarmViewModel;

import java.util.Calendar;
import java.util.Date;

import com.gu.galarm.listener.OnAddAlarmListener;

/**
 * Used to create and edit alarms, depending on REQUEST_CODE
 */
public class AddAlarmActivity extends AppCompatActivity implements SetRepeatDaysDialogFragment.OnDialogCompleteListener, View.OnClickListener , OnAddAlarmListener{

    private final String TAG = "AddAlarmActivity";

    private static final int SET_START_LOCATION_ACTIVITY_REQUEST_CODE = 1;
    private static final int SET_END_LOCATION_ACTIVITY_REQUEST_CODE = 2;

    // Key values for returning intent.
    public static final String EXTRA_BUNDLE = "com.gu.galarm.AddAlarmActivity.BUNDLE";
    public static final String EXTRA_ALARM = "com.gu.galarm.AddAlarmActivity.ALARM";
    public static final String EXTRA_DELETE = "com.gu.galarm.AddAlarmActivity.DELETE";

    private AlarmViewModel alarmViewModel;
    private int mCurrRequestCode; // current request code - static values in MainActivity

    public static Alarm mAlarm;
    // Data objects
    private Calendar calendar;
    // View objects

    RelativeLayout mRlRepeat;
    RelativeLayout mRlLabel;
    RelativeLayout mRlVideo;

    TextView mTvSave;
    TextView mTvCancel;
    TimePicker mTimePicker;

    TextView mTvRepeat;
    TextView mTvLabel;
    TextView mTvVideo;

    private AlarmHandler mAlarmHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        mRlRepeat = (RelativeLayout) findViewById(R.id.rlRepeat);
        mRlLabel = (RelativeLayout) findViewById(R.id.rlLabel);
        mRlVideo = (RelativeLayout) findViewById(R.id.rlVideo);

        mRlRepeat.setOnClickListener(this);
        mRlLabel.setOnClickListener(this);
        mRlVideo.setOnClickListener(this);

        calendar = Calendar.getInstance();

        mTvSave = (TextView) findViewById(R.id.tvTitleSave);
        mTvCancel = (TextView) findViewById(R.id.tvTitleCancel);
        mTvSave.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);

        mTimePicker = (TimePicker) findViewById(R.id.timePicker);

        mTvRepeat = (TextView) findViewById(R.id.tvRepeat);
        mTvLabel = (TextView) findViewById(R.id.tvLabel);
        mTvVideo = (TextView) findViewById(R.id.tvVideo);

        // Get a new or existing ViewModel from the ViewModelProvider.
        alarmViewModel = ViewModelProviders.of(this).get(AlarmViewModel.class);
        mAlarmHandler = new AlarmHandler(this);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_BUNDLE)) { // Activity called to edit an alarm.
            Bundle args = intent.getBundleExtra(EXTRA_BUNDLE);
            mAlarm = args.getParcelable(EXTRA_ALARM);
            mCurrRequestCode = MainActivity.EDIT_ALARM_ACTIVITY_REQUEST_CODE;
        } else {
            mCurrRequestCode = MainActivity.NEW_ALARM_ACTIVITY_REQUEST_CODE;
        }

        if (mCurrRequestCode == MainActivity.EDIT_ALARM_ACTIVITY_REQUEST_CODE) {

            setTitle(R.string.edit_alarm);
            mTvVideo.setText(mAlarm.video_title);
            //addModeButtonListeners(mAlarm.transMode);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mAlarm.time);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            mTimePicker.setCurrentHour(hour);
            mTimePicker.setCurrentMinute(minute);

            mTvRepeat.setText(mAlarm.getStringOfActiveDays());
            mTvLabel.setText(mAlarm.label);
        } else {
            mAlarm = new Alarm();
            mAlarm.activeDays = new boolean[7];
            mAlarm.active = false;

            setInitialAlarmTime();
            //addModeButtonListeners("drive");
            //mRepeatTextView.setText(R.string.never);
        }

        mTvRepeat.setText(mAlarm.getStringOfActiveDays());
    }


    /**
     * Initialize TimeTextView and Alarm's time with current time
     */
    private void setInitialAlarmTime() {
        // Get time and set it to alarm time TextView
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        Date date = calendar.getTime();
        mAlarm.time = date;
        //String currentTime = getFormattedTime(hour, minute);
        //mAlarm.setTime(currentTime);
    }


    /**
     * Get Bundle of arguments for SetRepeatDaysDialogFragment, arguments include alarm's active days.
     */
    @NonNull
    private Bundle getBundle() {
        Bundle args = new Bundle();

        args.putBooleanArray("activeDays", mAlarm.activeDays);
        return args;
    }

    /**
     * This method is called when SetRepeatDaysDialogFragment completes, we get the selectedDays
     * and apply that to alarm
     *
     * @param selectedDaysBools boolean array of selected days of the week
     */
    public void onDialogComplete(boolean[] selectedDaysBools) {
        mAlarm.activeDays = selectedDaysBools;
        String formattedActiveDays = Alarm.getStringOfActiveDays(mAlarm.activeDays);
        //mRepeatTextView.setText(formattedActiveDays);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_add_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * Updates UI with data received from MapActivity
     *
     * @param requestCode request code varies on whether start or end location set
     * @param resultCode  whether activity successfully completed
     * @param data        reply Intent, contains extras that vary based on request code
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            return;
        }

        if (requestCode == SET_START_LOCATION_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            // Get extras
            //mAlarm.startPlace = data.getStringExtra(MapsActivity.EXTRA_PLACE);
            Bundle args = data.getBundleExtra(MapsActivity.BUNDLE_POINT);
            //mAlarm.startPoint = args.getParcelable(MapsActivity.EXTRA_LATLNG);

            // Set place to start location textView
            //mStartLocTextView.setText(mAlarm.startPlace);

        } else if (requestCode == SET_END_LOCATION_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            // Get extra
            //mAlarm.endPlace = data.getStringExtra(MapsActivity.EXTRA_PLACE);
            Bundle args = data.getBundleExtra(MapsActivity.BUNDLE_POINT);
            //mAlarm.endPoint = args.getParcelable(MapsActivity.EXTRA_LATLNG);
            // Set place to start location textView
            //mEndLocTextView.setText(mAlarm.endPlace);
        }
    }

    /**
     * Return a string of the form hh:mm aa from given hour and minute
     *
     * @param hour   integer of hour in 24h format
     * @param minute integer of minute
     * @return a String of formatted time
     */
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlRepeat:
                startRepeatActivity();
                break;

            case R.id.rlLabel:
                startLabelActivity();
                break;

            case R.id.rlVideo:
                startVideoActivity();
                break;

            case R.id.tvTitleSave:
                addAlarm();
                break;

            case R.id.tvTitleCancel:
                cancelAlarm();
                break;
        }
    }

    private void startRepeatActivity()
    {
        RepeatActivity.mListener = (OnAddAlarmListener)this;
        Intent intent = new Intent(this, RepeatActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    private void startLabelActivity()
    {
        LabelActivity.mListener = (OnAddAlarmListener)this;
        Intent intent = new Intent(this, LabelActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    private void startVideoActivity()
    {
        YoutubeActivity.mListener = (OnAddAlarmListener)this;
        Intent intent = new Intent(this, YoutubeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    private void getTimePicker(){
        int hour, minute;
        if (mAlarm.time == null) {
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            Date date = calendar.getTime();
            mAlarm.time = date;
        } else {
            Calendar calendar = Calendar.getInstance();
            //calendar.setTime(mAlarm.time);
            hour = mTimePicker.getCurrentHour();
            minute = mTimePicker.getCurrentMinute();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            mAlarm.time = calendar.getTime();
        }
        //String formattedTime = getFormattedTime(mTimePicker.getCurrentHour(), mTimePicker.getCurrentMinute());
        //mAlarm.setTime(formattedTime);
    }

    private void addAlarm() {
       getTimePicker();
        Intent replyIntent = new Intent();

        if (mCurrRequestCode == MainActivity.EDIT_ALARM_ACTIVITY_REQUEST_CODE) {
            alarmViewModel.update(mAlarm);
        } else {
            alarmViewModel.insert(mAlarm);
        }

        mAlarmHandler.scheduleAlarm(mAlarm);
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    private void cancelAlarm() {
        finish();
    }

    @Override
    public void onRepeatClicked() {
        mTvRepeat.setText(mAlarm.getStringOfActiveDays());
    }

    @Override
    public void onLabelClicked() {
        mTvLabel.setText(mAlarm.label);
    }

    @Override
    public void onVideoClicked() {
        mTvVideo.setText(mAlarm.video_title);
    }
}
