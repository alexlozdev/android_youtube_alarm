package com.gu.galarm.http;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.gu.galarm.R;
import com.gu.galarm.listener.OnGetVideosListener;
import com.gu.galarm.listener.OnHttpResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WebService {

    private static final String TAG = "WebService";

    private static final String HEADER_AUTH_TOKEN_KEY = "Auth-Token";

    private static final int API_CODE_SUCCESS = 200;

    //private static final String SERVER_URL = "http://192.168.1.113/api";
    private static final String SERVER_URL = "https://www.googleapis.com/youtube";

    //private static final String URL_POST_CHECK_REGISTERED = SERVER_URL + "/login.php";
    private static final String URL_GET_VIDEOLIST = SERVER_URL + "/v3/search?";


    private static WebService mInstance = null;
    private static Context mContext;
    //private static String mStrDeviceNumber = "";

    public static WebService getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new WebService(context);
        } else {
            mContext = context;
        }

        return mInstance;
    }

    public WebService(Context context) {
        mContext = context;
    }

    private AsyncHttpClient getAsyncHttpClient(boolean authToken) {
        AsyncHttpClient client = new AsyncHttpClient();

        if (authToken) {
            client.addHeader(HEADER_AUTH_TOKEN_KEY, "xxx");
        }

        return client;
    }

    private AsyncHttpResponseHandler handleHttpResponseListener(final OnHttpResponseListener listener) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String response) {
                JSONObject object;
                listener.onSuccess(statusCode, response);

            }

            @Override
            public void onFailure(Throwable e, String errorMessage) {
                listener.onFailure("HTTP connection Failure");
                Log.e(TAG, "HTTP Failure = " + errorMessage);
            }
        };
    }

    private void get(AsyncHttpClient client, String url, RequestParams params, OnHttpResponseListener listener) {
        Log.i(TAG, "Method = GET");
        Log.i(TAG, "URL = " + url);
        Log.i(TAG, "Params = " + params.toString());

        client.get(url, params, handleHttpResponseListener(listener));
    }

    private void get(AsyncHttpClient client, String url, OnHttpResponseListener listener) {
        Log.i(TAG, "Method = GET");
        Log.i(TAG, "URL = " + url);

        client.get(url, handleHttpResponseListener(listener));
    }

    private void post(AsyncHttpClient client, String url, RequestParams params, OnHttpResponseListener listener) {
        Log.i(TAG, "Method = POST");
        Log.i(TAG, "URL = " + url);
        Log.i(TAG, "Params = " + params.toString());

        client.post(url, params, handleHttpResponseListener(listener));
    }

    private void post(AsyncHttpClient client, String url, OnHttpResponseListener listener) {
        Log.i(TAG, "Method = POST");
        Log.i(TAG, "URL = " + url);

        client.post(url, handleHttpResponseListener(listener));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //. http url login
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void urlGetVideoList(final String strKey , final OnGetVideosListener listener) {
        AsyncHttpClient client = getAsyncHttpClient(false);

        RequestParams params = new RequestParams();
        params.put("part", "snippet");
        params.put("q", strKey);
        params.put("type", "video");
        params.put("key", mContext.getString(R.string.api_key));
        params.put("maxResults", "20");
        //String str = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=beauty&type=video&key=AIzaSyAUKQ8H3cEYjPTCHVin1i6r3mlk4xFkglc&max-results=1";
        get(client, URL_GET_VIDEOLIST, params, new OnHttpResponseListener() {
        //get(client, str, null, new OnHttpResponseListener() {

            @Override
            public void onSuccess(int code, String strData) {
                //String msg = data.optString("msg");
                if (code == API_CODE_SUCCESS) {
                    //. set update-time
                    Log.i(TAG, "PERF = GET SUCCESS");
                    //JSONArray jsonClassList = data.optJSONArray("perform_list");
                    ArrayList<YoutubeVideo> youtubeVideoList = new ArrayList<>();

                    try {

                        JSONObject jsonData = new JSONObject(strData);
                        JSONArray jsonItems = jsonData.getJSONArray("items");
                        for (int i = 0; i < jsonItems.length(); i++) {
                            YoutubeVideo youtubeVideo = new YoutubeVideo();

                            JSONObject jsonObject = jsonItems.getJSONObject(i);

                            JSONObject jsonId = jsonObject.getJSONObject("id");
                            youtubeVideo.video_id = jsonId.optString("videoId");

                            JSONObject jsonSnippet = jsonObject.getJSONObject("snippet");
                            youtubeVideo.title = jsonSnippet.optString("title");
                            youtubeVideo.description = jsonSnippet.optString("description");

                            JSONObject jsonThumb = jsonSnippet.getJSONObject("thumbnails");
                            JSONObject jsonThumbDefault = jsonThumb.getJSONObject("default");
                            youtubeVideo.thumb = jsonThumbDefault.optString("url");

                            youtubeVideoList.add(youtubeVideo);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    listener.onSuccess(youtubeVideoList);
                } else {
                    listener.onFailure("Failed to get youtube video data");
                }
            }

            @Override
            public void onFailure(String message) {
                listener.onFailure(message);

            }
        });
    }



}
