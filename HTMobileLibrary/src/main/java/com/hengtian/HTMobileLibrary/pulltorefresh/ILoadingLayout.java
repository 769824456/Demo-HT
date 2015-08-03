package com.hengtian.HTMobileLibrary.pulltorefresh;

/**
 * pull-up and pull-down UI interface
 * 
 */
public interface ILoadingLayout {
    /**
     * current state
     */
    public enum State {
        /**
         * Initial state
         */
        NONE,
        /**
         * When the UI is in a state which means that user is not interacting
         * with the Pull-to-Refresh function.
         */
        RESET,
        /**
         * When the UI is being pulled by the user, but has not been pulled far
         * enough so that it refreshes when released.
         */
        PULL_TO_REFRESH,
        /**
         * When the UI is being pulled by the user, and <strong>has</strong>
         * been pulled far enough so that it will refresh when released.
         */
        RELEASE_TO_REFRESH,
        /**
         * When the UI is currently refreshing, caused by a pull gesture.
         */
        REFRESHING,
        /**
         * When the UI is currently refreshing, caused by a pull gesture.
         */
        @Deprecated
        LOADING,
        /**
         * No more data
         */
        NO_MORE_DATA,
        /**
         * No more data
         */
        NO_DATA
    }

    public void setState(State state);

    public State getState();

    /**
     * get current height of the layout，the critical value of refresh
     * 
     * @return height
     */
    public int getContentSize();

    /**
     * call the method when pull
     * 
     * @param scale : the scale of pulling
     */
    public void onPull(float scale);
}
