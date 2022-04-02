package com.gu.galarm.listener;


import com.gu.galarm.http.YoutubeVideo;

import java.util.ArrayList;

public interface OnGetVideosListener {
	void onSuccess(ArrayList<YoutubeVideo> videoList);

    void onFailure(String message);
}



