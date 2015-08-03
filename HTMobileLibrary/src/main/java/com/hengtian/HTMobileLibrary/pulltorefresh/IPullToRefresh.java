package com.hengtian.HTMobileLibrary.pulltorefresh;

import com.hengtian.HTMobileLibrary.pulltorefresh.PullToRefreshBase.OnRefreshListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;


/**
 * the interface of pull to refresh
 * 
 */
public interface IPullToRefresh<T extends View> {
    /**
     * set whether pull down is enabled
     * 
     * @param pullRefreshEnabled true means enabled，false means disabled
     */
    public void setPullRefreshEnabled(boolean pullRefreshEnabled);

    /**
     * set whether pull up is enabled
     * 
     * @param pullLoadEnabled true means enabled，false means disabled
     */
    public void setPullLoadEnabled(boolean pullLoadEnabled);

    /**
     *  set whether auto load when scroll to the bottom
     * 
     * @param scrollLoadEnabled true means the function of pull-up will be disabled 
     */
    public void setScrollLoadEnabled(boolean scrollLoadEnabled);

    public boolean isPullRefreshEnabled();

    public boolean isPullLoadEnabled();

    /**
     * whether auto load when scroll to the bottom  
     * 
     * @return true - yes，false - no
     */
    public boolean isScrollLoadEnabled();

    public void setOnRefreshListener(OnRefreshListener<T> refreshListener);

    public void onPullDownRefreshComplete();

    public void onPullUpRefreshComplete();

    public T getRefreshableView();

    public LoadingLayout getHeaderLoadingLayout();

    public LoadingLayout getFooterLoadingLayout();

    /**
     *  set last update time label
     * 
     */
    public void setLastUpdatedLabel(CharSequence label);
}
