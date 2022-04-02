package com.gu.galarm.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayerView;
import com.gu.galarm.http.YoutubeVideo;
import com.gu.galarm.listener.OnYoutubeHomeListener;
import com.gu.galarm.ui.VideoListAdapter;
import com.gu.galarm.R;

import java.util.ArrayList;


public class YoutubePlayFragment extends Fragment  implements OnInitializedListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public ArrayList<YoutubeVideo> mArrVideoList = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private WebView mWvPlay;
    private WebView myWebView;

    OnYoutubeHomeListener mOnHomeLister;

    RecyclerView mRecyclerView;
    VideoListAdapter mAdapter;
    Context mContext;
    String mStrVideoID = "";

    public YoutubePlayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YoutubePlayFragment newInstance(String param1, String param2) {
        YoutubePlayFragment fragment = new YoutubePlayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_youtube_play, container, false);

        mContext = getContext();
        //mWvPlay = (WebView) rootView.findViewById(R.id.wvPlay);

        String url = getArguments().getString("url");
        mStrVideoID = getArguments().getString("video_id");
        //mWvPlay.loadUrl(url);

        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) rootView.findViewById(R.id.youtube_player_view);
        youTubePlayerView.initialize(mContext.getResources().getString(R.string.api_key), (OnInitializedListener)mContext);
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        /*
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }


    public void setOnHomeLister(OnYoutubeHomeListener onHomeLister) {
        mOnHomeLister = onHomeLister;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

        if (youTubePlayer == null)
            return;

        if (!b) {
            youTubePlayer.cueVideo(mStrVideoID);

        }

        // Add listeners to YouTubePlayer instance
        youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
            @Override public void onAdStarted() { }
            @Override public void onError(YouTubePlayer.ErrorReason arg0) { }
            @Override public void onLoaded(String arg0) { }
            @Override public void onLoading() { }
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
        Toast.makeText(mContext, "Failed to initialize.", Toast.LENGTH_LONG).show();
    }


}



