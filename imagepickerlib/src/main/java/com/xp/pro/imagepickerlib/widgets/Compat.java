package com.xp.pro.imagepickerlib.widgets;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.annotation.RequiresApi;
import android.view.View;

public class Compat {
	
	private static final int SIXTY_FPS_INTERVAL = 1000 / 60;
	
	public static void postOnAnimation(View view, Runnable runnable) {
		if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
			postSDK16OnAnimation(view, runnable);
		} else {
			view.postDelayed(runnable, SIXTY_FPS_INTERVAL);
		}
	}


	@RequiresApi(api = VERSION_CODES.JELLY_BEAN)
	public static void postSDK16OnAnimation(View view, Runnable r) {
		view.postOnAnimation(r);
	}

}
