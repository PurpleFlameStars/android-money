package com.dzfd.gids.baselibs.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class DensityUtils {
	
	private DensityUtils() {
		throw new UnsupportedOperationException("cannot be instantiated");
	}


	public static int dp2px(Context context, float dpVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dpVal, context.getResources().getDisplayMetrics());
	}


	public static int sp2px(Context context, float spVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				spVal, context.getResources().getDisplayMetrics());
	}


	public static float px2dp(Context context, float pxVal) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (pxVal / scale);
	}

	public static float px2sp(Context context, float pxVal) {
		return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
	}

	public static int dip2px(float dip) {
	    try {
			final float scale = getDisplayMetrics().density;
			return (int) (dip * scale + 0.5f);
		}catch (Exception e) {
	    	return 0;
		}
	}

	public static DisplayMetrics dm = null;

	public static DisplayMetrics getDisplayMetrics() {
		if (dm == null) {
			dm = ContextUtils.getApplicationContext().getResources().getDisplayMetrics();
		}
		return dm;
	}

}
