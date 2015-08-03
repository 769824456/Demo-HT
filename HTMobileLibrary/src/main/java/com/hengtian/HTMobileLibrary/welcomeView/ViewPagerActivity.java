package com.hengtian.HTMobileLibrary.welcomeView;

import java.util.ArrayList;
import java.util.List;

import com.hengtian.HTMobileLibrary.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class ViewPagerActivity extends Activity {
	
	private ViewPager mViewPager;
	private List<ImageView> mDotImageViews = new ArrayList<ImageView>();
	private ArrayList<View> mViews = new ArrayList<View>();
	private ViewPagerAdapter mPagerAdapter ;
	private LinearLayout mLayoutDot;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);     
    }    
    
    public void setViews(ArrayList<View> views){
    	this.mViews = views;   
        mViewPager = (ViewPager)findViewById(R.id.welcome_viewpager);
        mLayoutDot = (LinearLayout)findViewById(R.id.ll_dot);
        
        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());     
        
        mPagerAdapter = new ViewPagerAdapter(mViews);
		mViewPager.setAdapter(mPagerAdapter);
    	refreshView();
    }

    private void refreshView() {
    	mLayoutDot.removeAllViews();
    	mDotImageViews.clear();
    	for(int i=0;i<mViews.size();i++){
    		ImageView imageView = new ImageView(this);
    		imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    		imageView.setImageDrawable(getResources().getDrawable(i==0?R.drawable.page_now:R.drawable.page));
    		mDotImageViews.add(imageView);
    		mLayoutDot.addView(imageView);
    	}
		mLayoutDot.invalidate();
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
    	
    	
		public void onPageSelected(int page) {
			if(page==0){
				mDotImageViews.get(page).setImageDrawable(getResources().getDrawable(R.drawable.page_now));
				if(mViews.size()>1){
					mDotImageViews.get(page+1).setImageDrawable(getResources().getDrawable(R.drawable.page));
				}
			}else if(page==mViews.size()-1){
				mDotImageViews.get(page).setImageDrawable(getResources().getDrawable(R.drawable.page_now));
				mDotImageViews.get(page-1).setImageDrawable(getResources().getDrawable(R.drawable.page));
			}else{

				mDotImageViews.get(page).setImageDrawable(getResources().getDrawable(R.drawable.page_now));
				mDotImageViews.get(page+1).setImageDrawable(getResources().getDrawable(R.drawable.page));
				mDotImageViews.get(page-1).setImageDrawable(getResources().getDrawable(R.drawable.page));
			}
		}
		
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		
		public void onPageScrollStateChanged(int arg0) {
		}
	}
    
    
}
