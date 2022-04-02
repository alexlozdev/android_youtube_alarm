package com.gu.galarm.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gu.galarm.http.YoutubeVideo;
import com.gu.galarm.listener.OnYoutubeHomeListener;
import com.gu.galarm.ui.activity.MainActivity;
import com.gu.galarm.R;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * ArrayAdapter used to populate MainActivity Alarms ListView
 */
public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoViewHolder> {

    private final String TAG = "VideoListAdapter";
    private ArrayList<YoutubeVideo> mArrVideoList = null;
    OnYoutubeHomeListener mOnHomeLister;
    /**
     * View for each alarm
     */
    class VideoViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout mlayItem;
        private final TextView mTvTitle;
        private final TextView mTvDesp;
        private final ImageView mIvThumb;

        private VideoViewHolder(View itemView) {
            super(itemView);

            mlayItem = (LinearLayout) itemView.findViewById(R.id.llItem);
            mTvTitle = (TextView) itemView.findViewById(R.id.tvVideoTitle);
            mTvDesp = (TextView) itemView.findViewById(R.id.tvVideoDesp);
            mIvThumb = (ImageView) itemView.findViewById(R.id.ivThumb);
        }
    }

    // Layout members
    private final LayoutInflater mInflater;
    public VideoListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        setHasStableIds(true); // so Switch interaction has smooth animations
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder viewHolder, int position) {
        YoutubeVideo video = mArrVideoList.get(position);

        boolean bEdit = MainActivity.mbEdit;
        boolean bDelete = false;

        //viewHolder.mTvTitle.setText("avb"); // set alarm time

        viewHolder.mTvTitle.setText(video.title); // set alarm time
        viewHolder.mTvDesp.setText(video.description);

        ImageView bindImage = viewHolder.mIvThumb;
        String pathToFile = "https://inducesmile.com/wp-content/uploads/2015/03/mobile.jpg";
        DownloadImageWithURLTask downloadTask = new DownloadImageWithURLTask(bindImage);
        //downloadTask.execute(pathToFile);
        downloadTask.execute(video.thumb);
        //viewHolder.mIvThumb.setImageBitmap(bmp);

        // Add Button click listeners
        addPlayListener(video, viewHolder);

    }

    @Override
    public int getItemCount() {
        //mEmptyTextView.setVisibility(mAlarms.size() > 0 ? View.GONE : View.VISIBLE);
        if (mArrVideoList != null)
            return mArrVideoList.size();
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void addPlayListener(final YoutubeVideo video, final VideoViewHolder viewHolder) {
        viewHolder.mlayItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnHomeLister != null)
                    mOnHomeLister.onPlayVideo(video);
            }
        });
    }

    public void setVideoList(ArrayList<YoutubeVideo> arrVideoList) {
        mArrVideoList = arrVideoList;
    }

    public void setOnHomeLister(OnYoutubeHomeListener onHomeLister) {
        mOnHomeLister = onHomeLister;
    }

    private class DownloadImageWithURLTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageWithURLTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String pathToFile = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(pathToFile).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
