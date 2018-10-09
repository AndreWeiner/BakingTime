package com.example.android.bakingtime.Ui;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakingtime.Data.Step;
import com.example.android.bakingtime.R;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepInstructionFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private final static String USER_AGENT = "userAgent";
    private final static String STEP_POSITION = "position";
    private final static String PLAYER_POSITION = "player_position";
    private final static String PLAYER_STATE = "player_state";
    private final static String STEPS = "steps";
    private final static String PANE_MODE = "pane";
    private final static double VIDEO_ASPECT = 16.0 / 9.0;
    @BindView(R.id.step_full_description)
    TextView mDesView;
    @BindView(R.id.back_button)
    Button mPrevButton;
    @BindView(R.id.next_button)
    Button mNextButton;
    @BindView(R.id.player_view)
    PlayerView mPlayerView;
    @BindView(R.id.exo_artwork)
    ImageView mPreviewView;
    private ArrayList<Step> mSteps;
    private int mCurrentPos;
    private long mPlayerPosition;
    private SimpleExoPlayer mExoPlayer;
    private boolean mTwoPane;
    private boolean mPlayerReady;

    public StepInstructionFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.step_instruction_fragment, container, false);
        ButterKnife.bind(this, rootView);
        mPlayerPosition = 0;
        mPlayerReady = true;

        if (savedInstanceState != null) {
            mCurrentPos = savedInstanceState.getInt(STEP_POSITION);
            mSteps = savedInstanceState.getParcelableArrayList(STEPS);
            mTwoPane = savedInstanceState.getBoolean(PANE_MODE);
            mPlayerPosition = savedInstanceState.getLong(PLAYER_POSITION);
            mPlayerReady = savedInstanceState.getBoolean(PLAYER_STATE);
        }

        if (mTwoPane) {
            mPrevButton.setVisibility(View.GONE);
            mNextButton.setVisibility(View.GONE);
        } else {
            mPrevButton.setOnClickListener(this);
            mNextButton.setOnClickListener(this);
            hideOrShowButtons();
        }

        // set description
        mDesView.setText(mSteps.get(mCurrentPos).getDescription());
        // create and start player
        if (internetAvailable()) {
            mPlayerView.setVisibility(View.VISIBLE);
            mPlayerView.getLayoutParams().height = findOptimalHeight(
                    getResources().getConfiguration().orientation
            );
            createPlayer();
        } else {
            mPlayerView.setVisibility(View.GONE);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STEP_POSITION, mCurrentPos);
        outState.putParcelableArrayList(STEPS, mSteps);
        outState.putBoolean(PANE_MODE, mTwoPane);
        mPlayerPosition = mExoPlayer.getCurrentPosition();
        outState.putLong(PLAYER_POSITION, mPlayerPosition);
        mPlayerReady = mExoPlayer.getPlayWhenReady();
        outState.putBoolean(PLAYER_STATE, mPlayerReady);
    }

    private int findOptimalHeight(int orientation) {
        final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        if (mTwoPane) {
            final int screenWidthInDp = displayMetrics.widthPixels;
            double weightI = getResources().getInteger(R.integer.instruction_weight);
            double weightL = getResources().getInteger(R.integer.list_weight);
            return (int) (screenWidthInDp / VIDEO_ASPECT * weightI / (weightI + weightL));
        } else {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                final int screenHeightInDp = displayMetrics.heightPixels;
                hideSystemUI();
                return screenHeightInDp;
            } else {
                final int screenWidthInDp = displayMetrics.widthPixels;
                return (int) (screenWidthInDp / VIDEO_ASPECT);
            }
        }
    }

    private void hideSystemUI() {
        View decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    public void setSteps(ArrayList<Step> steps) {
        mSteps = steps;
    }

    public void setPosition(int position) {
        mCurrentPos = position;
    }

    public void setTwoPane(boolean twoPane) {
        mTwoPane = twoPane;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    public void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    public void onNextButtonClick() {
        if (mCurrentPos < mSteps.size() - 1) {
            mCurrentPos++;
            mPlayerPosition = 0;
            mPlayerReady = true;
            createPlayer();
            Step currStep = mSteps.get(mCurrentPos);
            mDesView.setText(currStep.getDescription());
            hideOrShowButtons();
        }
    }

    public void onPreviousButtonClick() {
        if (mCurrentPos > 0) {
            mCurrentPos--;
            mPlayerPosition = 0;
            mPlayerReady = true;
            createPlayer();
            Step currStep = mSteps.get(mCurrentPos);
            mDesView.setText(currStep.getDescription());
            hideOrShowButtons();
        }
    }

    private void hideOrShowButtons() {
        if (mCurrentPos == 0) {
            mPrevButton.setVisibility(View.GONE);
        } else {
            mPrevButton.setVisibility(View.VISIBLE);
        }
        if (mCurrentPos == mSteps.size() - 1) {
            mNextButton.setVisibility(View.GONE);
        } else {
            mNextButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                onPreviousButtonClick();
                break;
            case R.id.next_button:
                onNextButtonClick();
                break;
        }
    }

    private void createPlayer() {
        if (mExoPlayer != null) {
            releasePlayer();
        }
        String thumbUrl = mSteps.get(mCurrentPos).getThumbnailUrl();
        if (!thumbUrl.equals("")) {
            new ThumbnailAsyncTask().execute(thumbUrl);
        } else {
            mPlayerView.hideController();
            mPreviewView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mPreviewView.setImageResource(R.drawable.recipe_120);
            mPreviewView.setVisibility(View.VISIBLE);
        }
        String urlString = mSteps.get(mCurrentPos).getVideoUrl();
        Uri videoUri = null;
        if (!urlString.equals("")) {
            videoUri = Uri.parse(urlString);
        }

        if (videoUri != null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);
            String userAgent = Util.getUserAgent(getActivity(), USER_AGENT);
            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                    getActivity(), Util.getUserAgent(getActivity(), userAgent), bandwidthMeter
            );
            MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(videoUri);
            mExoPlayer.prepare(mediaSource);
            mPlayerView.setPlayer(mExoPlayer);
            mExoPlayer.seekTo(mPlayerPosition);
            mExoPlayer.setPlayWhenReady(mPlayerReady);
        }

    }

    private boolean internetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private class ThumbnailAsyncTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                Bitmap thumbNail = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                return thumbNail;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            mPlayerView.hideController();
            mPreviewView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mPreviewView.setVisibility(View.VISIBLE);
            if (bitmap != null) {
                mPreviewView.setImageBitmap(bitmap);
            } else {
                mPreviewView.setImageResource(R.drawable.recipe_120);
            }
        }
    }
}
