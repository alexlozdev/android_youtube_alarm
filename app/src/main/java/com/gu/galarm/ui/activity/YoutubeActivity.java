package com.gu.galarm.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.gu.galarm.listener.OnAddAlarmListener;
import com.gu.galarm.listener.OnYoutubeHomeListener;
import com.gu.galarm.R;
import com.gu.galarm.http.YoutubeVideo;
import com.gu.galarm.ui.fragment.YoutubeHomeFragment;
import com.gu.galarm.ui.fragment.YoutubePlayFragment;

public class YoutubeActivity extends AppCompatActivity implements OnYoutubeHomeListener {

    private YoutubeHomeFragment mHomeFragment = null;
    private YoutubePlayFragment mPlayFragment = null;
    public static OnAddAlarmListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        showHomeFragment();

    }

    private void showHomeFragment() {
        //getSupportActionBar().setTitle(R.string.app_name);

        if (mHomeFragment == null) {
            mHomeFragment = new YoutubeHomeFragment();
            mHomeFragment.setOnHomeLister(this);
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mHomeFragment);
        transaction.commit();
    }

    private void showYoutubePlayer(YoutubeVideo video) {
        YoutubePlayActivity.mOnYoutubeHomeListener = (OnYoutubeHomeListener) this;
        Intent intent = new Intent(this, YoutubePlayActivity.class);
        intent.putExtra("video_id", video.video_id);
        intent.putExtra("video_title", video.title);
        startActivity(intent);
    }

    private void showPlayFragment(YoutubeVideo video) {
        /*
        if (mPlayFragment == null) {
            mPlayFragment = new YoutubePlayFragment();
            mPlayFragment.setOnHomeLister(this);
        }

        Bundle bundle = new Bundle(2);
        String strUrl = "https://www.youtube.com/watch?v=" + video.video_id;
        bundle.putString("url", strUrl);
        bundle.putString("video_id", video.video_id);
        mPlayFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mPlayFragment);
        transaction.commit();
        */
    }

    private void addLabel() {
        if (mListener != null) {
            //AddAlarmActivity.mAlarm.label = mTvLabel.getText().toString();
            mListener.onVideoClicked();
        }

        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }


    @Override
    public void onPlayVideo(YoutubeVideo video) {
        //showPlayFragment(video);
        showYoutubePlayer(video);
    }

    @Override
    public void onGoAddAlarm() {
        finish();
    }

    @Override
    public void onSelectVideo(String strVideoId, String strVideoTitle) {
        AddAlarmActivity.mAlarm.video_id = strVideoId;
        AddAlarmActivity.mAlarm.video_title = strVideoTitle;
        if (mListener != null)
            mListener.onVideoClicked();

        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
