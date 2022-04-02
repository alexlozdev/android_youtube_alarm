package com.gu.galarm.ui.fragment;


import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gu.galarm.http.WebService;
import com.gu.galarm.listener.OnGetVideosListener;
import com.gu.galarm.listener.OnYoutubeHomeListener;
import com.gu.galarm.R;
import com.gu.galarm.http.YoutubeVideo;
import com.gu.galarm.ui.VideoListAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class YoutubeHomeFragment extends Fragment implements View.OnClickListener {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public ArrayList<YoutubeVideo> mArrVideoList = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LinearLayout mLayAdd;
    private EditText mEdtSearch;
    private TextView mTvSearch;

    OnYoutubeHomeListener mOnHomeLister;

    RecyclerView mRecyclerView;
    VideoListAdapter mAdapter;
    Context mContext;

    public YoutubeHomeFragment() {
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
    public static YoutubeHomeFragment newInstance(String param1, String param2) {
        YoutubeHomeFragment fragment = new YoutubeHomeFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_youtube_home, container, false);

        mContext = getContext();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rycVideoList);
        mAdapter = new VideoListAdapter(mContext);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter.setOnHomeLister(mOnHomeLister);
        mEdtSearch = (EditText) rootView.findViewById(R.id.edtSearch);


        mEdtSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN)&& (keyCode == KeyEvent.KEYCODE_ENTER) ) {
                    searchVideo();
                    return true;
                }

                return false;
            }
        });

        mTvSearch = (TextView) rootView.findViewById(R.id.tvSearch);
        mTvSearch.setOnClickListener(this);

        mLayAdd = (LinearLayout) rootView.findViewById(R.id.llAdd);
        mLayAdd.setOnClickListener(this);

        //return inflater.inflate(R.layout.fragment_home, container, false);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlRepeat:
                break;

            case R.id.tvSearch:
                searchVideo();
                break;

            case R.id.llAdd:
                goAddAlarm();
                break;
        }
    }

    private void goAddAlarm() {
        mOnHomeLister.onGoAddAlarm();
    }

    private void searchVideo() {
        String strKey = mEdtSearch.getText().toString();
        if (strKey.isEmpty()) {
            return;
        }
        getVideoListFromYoutube();
        mEdtSearch.clearFocus();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEdtSearch.getWindowToken(), 0);
    }

    public void setOnHomeLister(OnYoutubeHomeListener onHomeLister) {
        mOnHomeLister = onHomeLister;
    }

    private void getVideoListFromYoutube() {
        mArrVideoList.clear();
        final Context context = getContext();
        String strSearchKey = mEdtSearch.getText().toString();
        WebService.getInstance(context).urlGetVideoList(strSearchKey, new OnGetVideosListener() {
            @Override
            public void onSuccess(ArrayList<YoutubeVideo> videoList) {
                mArrVideoList = videoList;
                mAdapter.setVideoList(videoList);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(context, "failed to get the Youtube Video list.", Toast.LENGTH_SHORT);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

      /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

}
