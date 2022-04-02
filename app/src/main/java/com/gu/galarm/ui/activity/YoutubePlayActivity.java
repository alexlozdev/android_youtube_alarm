package com.gu.galarm.ui.activity;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.ebanx.swipebtn.OnActiveListener;
//import com.ebanx.swipebtn.SwipeButton;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.gu.galarm.db.Alarm;
import com.gu.galarm.R;
import com.gu.galarm.alarmmanager.AlarmHandler;
import com.gu.galarm.listener.OnYoutubeHomeListener;
import com.gu.galarm.util.AlarmRepository;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class YoutubePlayActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, View.OnClickListener {

    public static int TYPE_SELECT = 0;
    public static int TYPE_ALARM = 1;

    int mAlarmId = 0;
    String mStrLabel = "";
    String mStrVideoID = "";
    String mStrVideoTitle = "";
    TextView mTvSelect;
    LinearLayout mLayCancel;
    LinearLayout mLayTitile;
    private LinearLayout mLayAlarmPlay;
    private LinearLayout mLayBottom;
    TextView mTvTime;
    TextView mTvLabel;
    Button mBtnSnooze;
    Button mBtnStop;


    int mType = TYPE_SELECT;
    public static OnYoutubeHomeListener mOnYoutubeHomeListener;

    //private SwipeButton mSwipeBtn;

    private AlarmRepository mRepository;
    private Alarm mAlarm;
    AlarmHandler mAlarmHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_play);

        mTvSelect = (TextView) findViewById(R.id.tvSelect);
        mLayCancel = (LinearLayout) findViewById(R.id.llBack);
        mLayTitile = (LinearLayout) findViewById(R.id.llTitle);
        mLayAlarmPlay = (LinearLayout) findViewById(R.id.llAlarm);
        mLayBottom = (LinearLayout) findViewById(R.id.llBottom);
        //mSwipeBtn = (SwipeButton) findViewById(R.id.swipeNoState);
        mTvTime = (TextView) findViewById(R.id.tvTime);
        mTvLabel = (TextView) findViewById(R.id.tvLabel);
        mBtnSnooze = (Button) findViewById(R.id.btnSnooze);
        mBtnStop = (Button) findViewById(R.id.btnStop);

        mTvSelect.setOnClickListener(this);
        mLayCancel.setOnClickListener(this);
        mBtnSnooze.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);


        Intent intent = getIntent();
        mStrVideoTitle =  intent.getStringExtra("video_title");
        mStrVideoID =  intent.getStringExtra("video_id");
        mStrLabel =  intent.getStringExtra("label");
        mType =  intent.getIntExtra("type", 0);
        mAlarmId = intent.getIntExtra("alarm_id", 0);

        if (mType == TYPE_ALARM) {
            mLayTitile.setVisibility(View.INVISIBLE);
            //mSwipeBtn.setOnActiveListener(this);
            mTvTime.setText(getStringTime());
            mTvLabel.setText(mStrLabel);

            if (mStrVideoID == null || mStrVideoID.isEmpty() ) {
                mStrVideoID = "qRGjSmk2ccQ";
            }

        } else {
            mLayAlarmPlay.setVisibility(View.GONE);
            mLayBottom.setVisibility(View.GONE);
        }

        Application application = ((Application) getApplicationContext());
        mRepository = new AlarmRepository(application);
        try {
            mAlarm = mRepository.getAlarmById(mAlarmId);
            //alarm.setActive(false);
            //mRepository.update(alarm);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mAlarmHandler = new AlarmHandler(this);
        repeatAlarm();

        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player_view);
        youTubePlayerView.initialize(getResources().getString(R.string.api_key), (YouTubePlayer.OnInitializedListener)this);

        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        // Vibrate for 400 milliseconds
        v.vibrate(1000);

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, boolean b) {

        if (youTubePlayer == null)
            return;

        if (!b) {
            youTubePlayer.cueVideo(mStrVideoID);
            youTubePlayer.loadVideo(mStrVideoID, 0);
            youTubePlayer.play();
        }

        // Add listeners to YouTubePlayer instance
        youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
            @Override public void onAdStarted() { return; }
            @Override public void onError(YouTubePlayer.ErrorReason arg0) { }
            @Override public void onLoaded(String arg0) { }
            @Override public void onLoading() { youTubePlayer.play();}
            @Override public void onVideoEnded() { }
            @Override public void onVideoStarted() { }
        });

        youTubePlayer.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
            @Override public void onBuffering(boolean arg0) { }
            @Override public void onPaused() { }
            @Override public void onPlaying() { }
            @Override public void onSeekTo(int arg0) { }
            @Override public void onStopped() { }
        });

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "Failed to initialize.", Toast.LENGTH_LONG).show();
    }

    private void setOnYoutubeHomeListener(OnYoutubeHomeListener onYoutubeHomeListener) {
        mOnYoutubeHomeListener = onYoutubeHomeListener;
    }

   
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        /*
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >=27) {
            setTurnScreenOn(true);
            setShowWhenLocked(true);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
        //window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        */

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSelect:
                onSelect();
                break;

            case R.id.llBack:
                onCancel();
                break;

            case R.id.btnSnooze:
                onSnooze();
                break;

            case R.id.btnStop:
                onStopAlarm();
                break;
        }
    }

    private void repeatAlarm() {
        if (mAlarm != null && mRepository != null) {
            if (mAlarm.isRepeating()) {
                //mAlarm.setActive(false);
                //mRepository.update(mAlarm);
                mAlarmHandler.scheduleAlarm(mAlarm);
            }
        }
    }

    private void onStopAlarm() {
        if (mAlarm != null && mRepository != null) {
            if (mAlarm.isRepeating() == false) {
                mAlarm.setActive(false);
                mRepository.update(mAlarm);
            }
        }

        finish();
    }

    private void onSnooze() {
        if (mAlarmHandler != null && mAlarm != null)
            mAlarmHandler.snoozeAlarm(mAlarm);

        finish();
    }

    private void onSelect() {
        mOnYoutubeHomeListener.onSelectVideo(mStrVideoID, mStrVideoTitle);
        finish();
    }

    private void onCancel() {
        finish();
    }

    /*
    @Override
    public void onActive() {
        if (mAlarm != null && mRepository != null) {
            if (mAlarm.isRepeating() == false) {
                mAlarm.setActive(false);
                mRepository.update(mAlarm);
            }
        }

        finish();
    }*/

    public String getStringTime() {

        Calendar calendar = Calendar.getInstance();
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
}
