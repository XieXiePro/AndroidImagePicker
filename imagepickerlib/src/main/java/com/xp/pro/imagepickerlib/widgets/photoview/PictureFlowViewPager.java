package com.xp.pro.imagepickerlib.widgets.photoview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class PictureFlowViewPager extends ImgViewPager {


	public PictureFlowViewPager(Context context) {
		super(context, null);
	}
	
	public PictureFlowViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    	return false;
    }
}
