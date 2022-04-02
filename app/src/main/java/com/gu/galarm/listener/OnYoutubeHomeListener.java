package com.gu.galarm.listener;

import com.gu.galarm.http.YoutubeVideo;

/**
 * Created by Puls on 2/27/2019.
 */

public interface OnYoutubeHomeListener {
    void onPlayVideo(YoutubeVideo video);

    void onGoAddAlarm();

    void onSelectVideo(String strVideoId, String strVideoTitle);

}