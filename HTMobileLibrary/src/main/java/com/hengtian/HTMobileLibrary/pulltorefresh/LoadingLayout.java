package com.hengtian.HTMobileLibrary.pulltorefresh;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * define the same methods of Header and Footer
 * 
 */
public abstract class LoadingLayout extends FrameLayout implements ILoadingLayout {
    private View  mContainer;
    private State mCurState = State.NONE;
    private State mPreState = State.NONE;

    /**
     * construction method
     */
    public LoadingLayout(Context context) {
        this(context, null);
    }

    /**
     * construction method
     */
    public LoadingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * construction method
     */
    public LoadingLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs) {
        mContainer = createLoadingView(context, attrs);
        if (null == mContainer) {
            throw new NullPointerException("Loading view can not be null.");
        }
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(mContainer, params);
    }

    /**
     * show or hide the layout
     */
    public void show(boolean show) {
        // If is showing, do nothing.
        if (show == (View.VISIBLE == getVisibility())) {
            return;
        }
        ViewGroup.LayoutParams params = mContainer.getLayoutParams();
        if (null != params) {
            if (show) {
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            } else {
                params.height = 0;
            }
            requestLayout();
            setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /**
     * set last update time label
     */
    public void setLastUpdatedLabel(CharSequence label) {
    }

    public void setLoadingDrawable(Drawable drawable) {
    }

    public void setPullLabel(CharSequence pullLabel) {
    }

    public void setRefreshingLabel(CharSequence refreshingLabel) {
    }

    public void setReleaseLabel(CharSequence releaseLabel) {
    }

    @Override
    public void setState(State state) {
        if (mCurState != state) {
            mPreState = mCurState;
            mCurState = state;
            onStateChanged(state, mPreState);
        }
    }

    @Override
    public State getState() {
        return mCurState;
    }

    @Override
    public void onPull(float scale) {
    }

    protected State getPreState() {
        return mPreState;
    }

    protected void onStateChanged(State curState, State oldState) {
        switch (curState) {
        case RESET:
            onReset();
            break;
        case RELEASE_TO_REFRESH:
            onReleaseToRefresh();
            break;
        case PULL_TO_REFRESH:
            onPullToRefresh();
            break;
        case REFRESHING:
            onRefreshing();
            break;
        case NO_MORE_DATA:
            onNoMoreData();
            break;
        case NO_DATA:
            onNoData();
            break;
        default:
            break;
        }
    }

    protected void onReset() {
    }

    protected void onPullToRefresh() {
    }

    protected void onReleaseToRefresh() {
    }

    protected void onRefreshing() {
    }

    protected void onNoMoreData() {
    }

    protected void onNoData() {
    }

    public abstract int getContentSize();

    protected abstract View createLoadingView(Context context, AttributeSet attrs);
}
