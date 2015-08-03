package com.hengtian.HTMobileLibrary.pulltorefresh;

import com.hengtian.HTMobileLibrary.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * loading layout of pull-down loading
 */
public class RotateLoadingLayout extends LoadingLayout {
    static final int          ROTATION_ANIMATION_DURATION = 1200;
    static final Interpolator ANIMATION_INTERPOLATOR      = new LinearInterpolator();
    private RelativeLayout    mHeaderContainer;
    private ImageView         mArrowImageView;
    private TextView          mHintTextView;
    private TextView          mHeaderTimeView;
    private TextView          mHeaderTimeViewTitle;
    private Animation         mRotateAnimation;

    /**
     * construction method
     */
    public RotateLoadingLayout(Context context) {
        super(context);
        init(context);
    }

    /**
     * construction method
     */
    public RotateLoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mHeaderContainer = (RelativeLayout) findViewById(R.id.pull_to_refresh_header_content);
        mArrowImageView = (ImageView) findViewById(R.id.pull_to_refresh_header_arrow);
        mHintTextView = (TextView) findViewById(R.id.pull_to_refresh_header_hint_textview);
        mHeaderTimeView = (TextView) findViewById(R.id.pull_to_refresh_header_time);
        mHeaderTimeViewTitle = (TextView) findViewById(R.id.pull_to_refresh_last_update_time_text);
        mArrowImageView.setScaleType(ScaleType.CENTER);
        mArrowImageView.setImageResource(R.drawable.default_ptr_rotate);
        float pivotValue = 0.5f;    // SUPPRESS CHECKSTYLE
        float toDegree = 720.0f;    // SUPPRESS CHECKSTYLE
        mRotateAnimation = new RotateAnimation(0.0f, toDegree, Animation.RELATIVE_TO_SELF, pivotValue, Animation.RELATIVE_TO_SELF, pivotValue);
        mRotateAnimation.setFillAfter(true);
        mRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
        mRotateAnimation.setDuration(ROTATION_ANIMATION_DURATION);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRotateAnimation.setRepeatMode(Animation.RESTART);
    }

    @Override
    protected View createLoadingView(Context context, AttributeSet attrs) {
        View container = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header2, null);
        return container;
    }

    @Override
    public void setLastUpdatedLabel(CharSequence label) {
        mHeaderTimeViewTitle.setVisibility(TextUtils.isEmpty(label) ? View.INVISIBLE : View.VISIBLE);
        mHeaderTimeView.setText(label);
    }

    @Override
    public int getContentSize() {
        if (null != mHeaderContainer) {
            return mHeaderContainer.getHeight();
        }
        return (int) (getResources().getDisplayMetrics().density * 60);
    }

    @Override
    protected void onStateChanged(State curState, State oldState) {
        super.onStateChanged(curState, oldState);
    }

    @Override
    protected void onReset() {
        resetRotation();
        mHintTextView.setText(R.string.pull_to_refresh_header_hint_normal);
    }

    @Override
    protected void onReleaseToRefresh() {
        mHintTextView.setText(R.string.pull_to_refresh_header_hint_ready);
    }

    @Override
    protected void onPullToRefresh() {
        mHintTextView.setText(R.string.pull_to_refresh_header_hint_normal);
    }

    @Override
    protected void onRefreshing() {
        resetRotation();
        mArrowImageView.startAnimation(mRotateAnimation);
        mHintTextView.setText(R.string.pull_to_refresh_header_hint_loading);
    }

    @SuppressLint("NewApi")
	@Override
    public void onPull(float scale) {
        float angle = scale * 180f; // SUPPRESS CHECKSTYLE
        mArrowImageView.setRotation(angle);
    }

    @SuppressLint("NewApi")
	private void resetRotation() {
        mArrowImageView.clearAnimation();
        mArrowImageView.setRotation(0);
    }
}
