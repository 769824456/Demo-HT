package com.hengtian.HTMobileLibrary.pulltorefresh;

import java.util.Date;

import com.hengtian.HTMobileLibrary.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;


public class PullToRefreshView extends LinearLayout {
    private static final String     TAG                          = "PullToRefreshView";
    // refresh states
    private static final int        PULL_TO_REFRESH              = 2;
    private static final int        RELEASE_TO_REFRESH           = 3;
    private static final int        REFRESHING                   = 4;
    private static final int        NO_MORE_DATA                 = 5;
    // pull state
    private static final int        PULL_UP_STATE                = 0;
    private static final int        PULL_DOWN_STATE              = 1;
    private boolean                 enablePullTorefresh          = true;
    private boolean                 enablePullLoadMoreDataStatus = true;
    /**
     * last y
     */
    private int                     mLastMotionY;
    /**
     * lock
     */
    private boolean                 mLock;
    /**
     * header view
     */
    private View                    mHeaderView;
    /**
     * footer view
     */
    private View                    mFooterView;
    /**
     * list or grid
     */
    private AdapterView<?>          mAdapterView;
    /**
     * scrollview
     */
    private ScrollView              mScrollView;
    /**
     * header view height
     */
    private int                     mHeaderViewHeight;
    /**
     * footer view height
     */
    private int                     mFooterViewHeight;
    /**
     * header view image
     */
    private ImageView               mHeaderImageView;
    /**
     * footer view image
     */
    private ImageView               mFooterImageView;
    /**
     * header tip text
     */
    private TextView                mHeaderTextView;
    /**
     * footer tip text
     */
    private TextView                mFooterTextView;
    /**
     * header refresh time
     */
    private TextView                mHeaderUpdateTextView;
    /**
     * footer refresh time
     */
    // private TextView mFooterUpdateTextView;
    /**
     * header progress bar
     */
    private ProgressBar             mHeaderProgressBar;
    /**
     * footer progress bar
     */
    private ProgressBar             mFooterProgressBar;
    /**
     * layout inflater
     */
    private LayoutInflater          mInflater;
    /**
     * header view current state
     */
    private int                     mHeaderState;
    /**
     * footer view current state
     */
    private int                     mFooterState;
    /**
     * pull state,pull up or pull down;PULL_UP_STATE or PULL_DOWN_STATE
     */
    private int                     mPullState;
    /**
     *  change the arrow to point up
     */
    private RotateAnimation         mFlipAnimation;
    /**
     * rotate arrow
     */
    private RotateAnimation         mReverseFlipAnimation;
    /**
     * footer refresh listener
     */
    private OnFooterRefreshListener mOnFooterRefreshListener;
    /**
     * footer refresh listener
     */
    private OnHeaderRefreshListener mOnHeaderRefreshListener;
    private boolean                 isLast                       = false;

    /**
     * last update time
     */
    // private String mLastUpdateTime;
    public PullToRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullToRefreshView(Context context) {
        super(context);
        init();
    }

    private void init() {
        // Load all of the animations we need in code rather than through XML
        mFlipAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setDuration(250);
        mFlipAnimation.setFillAfter(true);
        mReverseFlipAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(250);
        mReverseFlipAnimation.setFillAfter(true);
        mInflater = LayoutInflater.from(getContext());
        // add header view here, and assure it is the first to be added to the top of the linearlayout
        addHeaderView();
    }

    private void addHeaderView() {
        // header view
        mHeaderView = mInflater.inflate(R.layout.refresh_header, this, false);
        mHeaderImageView = (ImageView) mHeaderView.findViewById(R.id.pull_to_refresh_image);
        mHeaderTextView = (TextView) mHeaderView.findViewById(R.id.pull_to_refresh_text);
        mHeaderUpdateTextView = (TextView) mHeaderView.findViewById(R.id.pull_to_refresh_updated_at);
        mHeaderProgressBar = (ProgressBar) mHeaderView.findViewById(R.id.pull_to_refresh_progress);
        // header layout
        measureView(mHeaderView);
        mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, mHeaderViewHeight);
        // set the value of topMargin to be the negative of the header view height, 
        // the purpose is to hide it at the top of the layout
        params.topMargin = -(mHeaderViewHeight);
        // mHeaderView.setLayoutParams(params1);
        addView(mHeaderView, params);
    }

    private void addFooterView() {
        // footer view
        mFooterView = mInflater.inflate(R.layout.refresh_footer, this, false);
        mFooterImageView = (ImageView) mFooterView.findViewById(R.id.pull_to_load_image);
        mFooterTextView = (TextView) mFooterView.findViewById(R.id.pull_to_load_text);
        mFooterProgressBar = (ProgressBar) mFooterView.findViewById(R.id.pull_to_load_progress);
        // footer layout
        measureView(mFooterView);
        mFooterViewHeight = mFooterView.getMeasuredHeight();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, mFooterViewHeight);
        // int top = getHeight();
        // params.topMargin =getHeight();
        addView(mFooterView, params);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // add the footer view here, and assure add it to the bottom of the layout
        addFooterView();
        initContentAdapterView();
    }

    /**
     * init AdapterView like ListView,GridView and so on;or init ScrollView
     */
    private void initContentAdapterView() {
        int count = getChildCount();
        if (count < 3) {
            throw new IllegalArgumentException("this layout must contain 3 child views,and AdapterView or ScrollView must in the second position!");
        }
        View view = null;
        for (int i = 0; i < count - 1; ++i) {
            view = getChildAt(i);
            if (view instanceof AdapterView<?>) {
                mAdapterView = (AdapterView<?>) view;
            }
            if (view instanceof ScrollView) {
                // finish later
                mScrollView = (ScrollView) view;
            }
        }
        if (mAdapterView == null && mScrollView == null) {
            throw new IllegalArgumentException("must contain a AdapterView or ScrollView in this layout!");
        }
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        int y = (int) e.getRawY();
        switch (e.getAction()) {
        case MotionEvent.ACTION_DOWN:
            // intercept the down action, and record y-coordinate.  
            mLastMotionY = y;
            break;
        case MotionEvent.ACTION_MOVE:
            // deltaY > 0 : motion down; < 0: motion up
            int deltaY = y - mLastMotionY;
            if (isRefreshViewScroll(deltaY)) {
                return true;
            }
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mLock) {
            return true;
        }
        int y = (int) event.getRawY();
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            // onInterceptTouchEvent is already record
            // mLastMotionY = y;
            break;
        case MotionEvent.ACTION_MOVE:
            int deltaY = y - mLastMotionY;
            if (mPullState == PULL_DOWN_STATE) {
                Log.i(TAG, " pull down!parent view move!");
                headerPrepareToRefresh(deltaY);
                // setHeaderPadding(-mHeaderViewHeight);
            } else if (mPullState == PULL_UP_STATE) {
                Log.i(TAG, "pull up!parent view move!");
                footerPrepareToRefresh(deltaY);
            }
            mLastMotionY = y;
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            int topMargin = getHeaderTopMargin();
            if (mPullState == PULL_DOWN_STATE) {
                if (topMargin >= 0) {
                    // begin to refresh
                    headerRefreshing();
                } else {
                    // does not refresh. hide again 
                    setHeaderTopMargin(-mHeaderViewHeight);
                }
            } else if (mPullState == PULL_UP_STATE) {
                if (Math.abs(topMargin) >= mHeaderViewHeight + mFooterViewHeight) {
                    footerRefreshing();
                } else {
                    setHeaderTopMargin(-mHeaderViewHeight);
                }
            }
            break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * @param deltaY
     *            deltaY > 0 : motion down, < 0: motion up
     */
    private boolean isRefreshViewScroll(int deltaY) {
        if (mHeaderState == REFRESHING || mFooterState == REFRESHING) {
            return false;
        }
        // for ListView and GridView
        if (mAdapterView != null) {
            // child view(ListView or GridView) scroll to top
            if (deltaY > 0) {
                if (!enablePullTorefresh) {
                    return false;
                }
                View child = mAdapterView.getChildAt(0);
                if (child == null) {
                    return false;
                }
                if (mAdapterView.getFirstVisiblePosition() == 0 && child.getTop() == 0) {
                    mPullState = PULL_DOWN_STATE;
                    return true;
                }
                int top = child.getTop();
                int padding = mAdapterView.getPaddingTop();
                if (mAdapterView.getFirstVisiblePosition() == 0 && Math.abs(top - padding) <= 11) {// 这里之前用3可以判断,但现在不行,还没找到原因
                    mPullState = PULL_DOWN_STATE;
                    return true;
                }
            } else if (deltaY < 0) {
                if (!enablePullLoadMoreDataStatus) {
                    return false;
                }
                View lastChild = mAdapterView.getChildAt(mAdapterView.getChildCount() - 1);
                if (lastChild == null) {
                    return false;
                }
                if (lastChild.getBottom() <= getHeight() && mAdapterView.getLastVisiblePosition() == mAdapterView.getCount() - 1) {
                    mPullState = PULL_UP_STATE;
                    return true;
                }
            }
        }
        // for ScrollView
        if (mScrollView != null) {
            // chile scroll view scroll to the top
            View child = mScrollView.getChildAt(0);
            if (deltaY > 0 && mScrollView.getScrollY() == 0) {
                mPullState = PULL_DOWN_STATE;
                return true;
            } else if (deltaY < 0 && child.getMeasuredHeight() <= getHeight() + mScrollView.getScrollY()) {
                mPullState = PULL_UP_STATE;
                return true;
            }
        }
        return false;
    }

    /**
     * header prepare to refresh, the finger has not release yet
     * @param deltaY
     *            ,the distance of finger slipped
     */
    private void headerPrepareToRefresh(int deltaY) {
        int newTopMargin = changingHeaderViewTopMargin(deltaY);
        // if head view topMargin>=0，it means the header view has already showed totally, now need modify the prompt message of header view
        if (newTopMargin >= 0 && mHeaderState != RELEASE_TO_REFRESH) {
            mHeaderTextView.setText(R.string.pull_to_refresh_release_label);
            mHeaderUpdateTextView.setVisibility(View.VISIBLE);
            mHeaderImageView.clearAnimation();
            mHeaderImageView.startAnimation(mFlipAnimation);
            mHeaderState = RELEASE_TO_REFRESH;
        } else if (newTopMargin < 0 && newTopMargin > -mHeaderViewHeight) {
            mHeaderImageView.clearAnimation();
            mHeaderImageView.startAnimation(mFlipAnimation);
            // mHeaderImageView.
            mHeaderTextView.setText(R.string.pull_to_refresh_pull_label);
            mHeaderState = PULL_TO_REFRESH;
        }
    }

    /**
     * footer prepare to refresh, the finger has not release yet
     * @param deltaY
     *            ,the distance of finger slipped
     */
    private void footerPrepareToRefresh(int deltaY) {
        int newTopMargin = changingHeaderViewTopMargin(deltaY);
        if (Math.abs(newTopMargin) >= (mHeaderViewHeight + mFooterViewHeight) && mFooterState != RELEASE_TO_REFRESH) {
            // mFooterTextView.setText(R.string.pull_to_refresh_footer_release_label);
            mFooterImageView.clearAnimation();
            mFooterImageView.startAnimation(mFlipAnimation);
            mFooterState = RELEASE_TO_REFRESH;
        } else if (Math.abs(newTopMargin) < (mHeaderViewHeight + mFooterViewHeight)) {
            mFooterImageView.clearAnimation();
            mFooterImageView.startAnimation(mFlipAnimation);
            // mFooterTextView.setText(R.string.pull_to_refresh_footer_pull_label);
            mFooterState = PULL_TO_REFRESH;
        }
    }

    private int changingHeaderViewTopMargin(int deltaY) {
        LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
        float newTopMargin = params.topMargin + deltaY * 0.3f;
        if (deltaY > 0 && mPullState == PULL_UP_STATE && Math.abs(params.topMargin) <= mHeaderViewHeight) {
            return params.topMargin;
        }
        if (deltaY < 0 && mPullState == PULL_DOWN_STATE && Math.abs(params.topMargin) >= mHeaderViewHeight) {
            return params.topMargin;
        }
        params.topMargin = (int) newTopMargin;
        mHeaderView.setLayoutParams(params);
        invalidate();
        return params.topMargin;
    }

    public void headerRefreshing() {
        mHeaderState = REFRESHING;
        setHeaderTopMargin(0);
        mHeaderImageView.setVisibility(View.GONE);
        mHeaderImageView.clearAnimation();
        mHeaderImageView.setImageDrawable(null);
        mHeaderProgressBar.setVisibility(View.VISIBLE);
        mHeaderTextView.setText(R.string.pull_to_refresh_refreshing_label2);
        mFooterTextView.setText(R.string.pull_to_refresh_footer_pull_label);
        if (mOnHeaderRefreshListener != null) {
            mOnHeaderRefreshListener.onHeaderRefresh(this);
        }
    }

    private void footerRefreshing() {
        mFooterState = REFRESHING;
        int top = mHeaderViewHeight + mFooterViewHeight;
        setHeaderTopMargin(-top);
        mFooterImageView.setVisibility(View.GONE);
        mFooterImageView.clearAnimation();
        mFooterImageView.setImageDrawable(null);
        mFooterProgressBar.setVisibility(View.VISIBLE);
        mFooterTextView.setText(R.string.pull_to_refresh_footer_refreshing_label);
        if (mOnFooterRefreshListener != null) {
            mOnFooterRefreshListener.onFooterRefresh(this);
        }
    }

    public void showFooterRefreshing() {
        // mFooterState = REFRESHING;
        // int top = mHeaderViewHeight + mFooterViewHeight;
        // setHeaderTopMargin(-top);
        mFooterImageView.setVisibility(View.GONE);
        mFooterImageView.clearAnimation();
        mFooterImageView.setImageDrawable(null);
        mFooterProgressBar.setVisibility(View.VISIBLE);
        mFooterTextView.setText(R.string.pull_to_refresh_footer_refreshing_label);
    }

    private void setHeaderTopMargin(int topMargin) {
        LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
        params.topMargin = topMargin;
        mHeaderView.setLayoutParams(params);
        invalidate();
    }

    public void onHeaderRefreshComplete() {
        setHeaderTopMargin(-mHeaderViewHeight);
        mHeaderImageView.setVisibility(View.VISIBLE);
        // mHeaderImageView.setImageResource(R.drawable.ic_pulltorefresh_arrow);
        mHeaderTextView.setText(R.string.pull_to_refresh_pull_label);
        mHeaderProgressBar.setVisibility(View.GONE);
        mHeaderState = PULL_TO_REFRESH;
        setLastUpdated("last update time: " + new Date().toLocaleString());
    }

    /**
     * Resets the list to a normal state after a refresh.
     * 
     * @param lastUpdated
     *            Last updated at.
     */
    public void onHeaderRefreshComplete(CharSequence lastUpdated) {
        setLastUpdated(lastUpdated);
        onHeaderRefreshComplete();
    }

    public void onFooterRefreshComplete(boolean flag) {
        isLast = flag;
        setHeaderTopMargin(-mHeaderViewHeight);
        mFooterImageView.setVisibility(View.VISIBLE);
        // mFooterImageView.setImageResource(R.drawable.ic_pulltorefresh_arrow_up);
        if (isLast) {
            mFooterTextView.setText(R.string.pull_to_refresh_no_more_data);
        } else {
            mFooterTextView.setText(R.string.pull_to_refresh_footer_pull_label);
        }
        mFooterTextView.setText(R.string.pull_to_refresh_footer_pull_label);
        mFooterProgressBar.setVisibility(View.GONE);
        // mHeaderUpdateTextView.setText("");
        mFooterState = PULL_TO_REFRESH;
    }

    public void onFooterRefreshComplete(int size) {
        if (size > 0) {
            mFooterView.setVisibility(View.VISIBLE);
        } else {
            mFooterView.setVisibility(View.GONE);
        }
        setHeaderTopMargin(-mHeaderViewHeight);
        mFooterImageView.setVisibility(View.VISIBLE);
        // mFooterImageView.setImageResource(R.drawable.ic_pulltorefresh_arrow_up);
        mFooterTextView.setText(R.string.pull_to_refresh_footer_pull_label);
        mFooterProgressBar.setVisibility(View.GONE);
        // mHeaderUpdateTextView.setText("");
        mFooterState = PULL_TO_REFRESH;
    }

    // public void onFooterRefreshLast() {
    // setHeaderTopMargin(-mHeaderViewHeight);
    // mFooterView.setVisibility(View.VISIBLE);
    // mFooterImageView.setVisibility(View.VISIBLE);
    // // mFooterImageView.setImageResource(R.drawable.ic_pulltorefresh_arrow_up);
    // mFooterTextView.setText(R.string.pull_to_refresh_no_more_data);
    // mFooterProgressBar.setVisibility(View.GONE);
    // // mHeaderUpdateTextView.setText("");
    // mFooterState = NO_MORE_DATA;
    // // enablePullLoadMoreDataStatus = false;
    // }
    /**
     * Set a text to represent when the list was last updated.
     * 
     * @param lastUpdated
     *            Last updated at.
     */
    public void setLastUpdated(CharSequence lastUpdated) {
        if (lastUpdated != null) {
            mHeaderUpdateTextView.setVisibility(View.VISIBLE);
            mHeaderUpdateTextView.setText(lastUpdated);
        } else {
            mHeaderUpdateTextView.setVisibility(View.GONE);
        }
    }

    private int getHeaderTopMargin() {
        LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
        return params.topMargin;
    }

    // /**
    // * lock
    // */
    // private void lock() {
    // mLock = true;
    // }
    //
    // /**
    // * unlock
    // */
    // private void unlock() {
    // mLock = false;
    // }
    /**
     * set headerRefreshListener
     * @description
     * @param headerRefreshListener
     */
    public void setOnHeaderRefreshListener(OnHeaderRefreshListener headerRefreshListener) {
        mOnHeaderRefreshListener = headerRefreshListener;
    }

    public void setOnFooterRefreshListener(OnFooterRefreshListener footerRefreshListener) {
        mOnFooterRefreshListener = footerRefreshListener;
    }

    /**
     * Interface definition for a callback to be invoked when list/grid footer
     * view should be refreshed.
     */
    public interface OnFooterRefreshListener {
        public void onFooterRefresh(PullToRefreshView view);
    }

    /**
     * Interface definition for a callback to be invoked when list/grid header
     * view should be refreshed.
     */
    public interface OnHeaderRefreshListener {
        public void onHeaderRefresh(PullToRefreshView view);
    }

    public boolean isEnablePullTorefresh() {
        return enablePullTorefresh;
    }

    public void setEnablePullTorefresh(boolean enablePullTorefresh) {
        this.enablePullTorefresh = enablePullTorefresh;
    }

    public boolean isEnablePullLoadMoreDataStatus() {
        return enablePullLoadMoreDataStatus;
    }

    public void setEnablePullLoadMoreDataStatus(boolean enablePullLoadMoreDataStatus) {
        this.enablePullLoadMoreDataStatus = enablePullLoadMoreDataStatus;
    }
}
