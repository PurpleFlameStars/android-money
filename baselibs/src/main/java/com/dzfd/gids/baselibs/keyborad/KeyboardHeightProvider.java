/*
 * This file is part of Siebe Projects samples.
 *
 * Siebe Projects samples is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Siebe Projects samples is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License
 * along with Siebe Projects samples.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dzfd.gids.baselibs.keyborad;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;

import com.dzfd.gids.baselibs.R;
import com.dzfd.gids.baselibs.utils.DeviceUtils;

import java.lang.reflect.Method;


/**
 * The keyboard height provider, this class uses a PopupWindow
 * to calculate the window height when the floating keyboard is opened and closed. 
 */
public class KeyboardHeightProvider extends PopupWindow {

    /** The tag for logging purposes */
    private final static String TAG = "sample_KeyboardHeightProvider";

    /** The keyboard height observer */
    private KeyboardHeightObserver observer;

    /** The cached landscape height of the keyboard */
    private int keyboardLandscapeHeight;

    /** The cached portrait height of the keyboard */
    private int keyboardPortraitHeight;

    /** The view that is used to calculate the keyboard height */
    private View popupView;

    /** The _PlayerRootView view */
    private View parentView;

    /** The root activity that uses this KeyboardHeightProvider */
    private Activity activity;
    private int lastContentHigh;

    private int lastRectBottom;

    /**
     * Construct a new KeyboardHeightProvider
     * 
     * @param activity The _PlayerRootView activity
     */
    public KeyboardHeightProvider(Activity activity) {
		super(activity);
        this.activity = activity;
        lastContentHigh=0;
        lastRectBottom=0;

        LayoutInflater inflator = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        this.popupView = inflator.inflate(R.layout.popupwindow, null, false);
        setContentView(popupView);

        setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_RESIZE | LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);

        parentView = activity.findViewById(android.R.id.content);

//        parentView = ((ViewGroup)activity.findViewById(android.R.id.content)).getChildAt(0);
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        activity.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);

        setWidth(0);
        setHeight(LayoutParams.MATCH_PARENT);

        popupView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    if (popupView != null) {
                        handleOnGlobalLayout();
                    }
                }
            });
    }

    /**
     * Start the KeyboardHeightProvider, this must be called after the onResume of the Activity.
     * PopupWindows are not allowed to be registered before the onResume has finished
     * of the Activity.
     */
    public void start() {
        if (!isShowing() && parentView.getWindowToken() != null) {
            setBackgroundDrawable(new ColorDrawable(0));
            /**
             * 调用post方法，等待parentView加载完成，尝试fix以上问题
             */
            parentView.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0);
                    } catch (Exception e) {
                        /* 1 android.view.ViewRootImpl.setView(ViewRootImpl.java:788)
                         * 2 android.view.WindowManagerGlobal.addView(WindowManagerGlobal.java:359)
                         * 3 android.view.WindowManagerImpl.addView(WindowManagerImpl.java:93)
                         * 4 android.widget.PopupWindow.invokePopup(PopupWindow.java:1433)
                         * 5 android.widget.PopupWindow.showAtLocation(PopupWindow.java:1203)
                         * 6 android.widget.PopupWindow.showAtLocation(PopupWindow.java:1170)
                         * 7 com.dzfd.gids.baselibs.c.b.a(KeyboardHeightProvider.java:123)
                         * 8 com.GoLemon.Base.activity.BunBaseActivity.I(BunBaseActivity.java:231)
                         */
                    }
                }
            });
        }
    }

    /**
     * Close the keyboard height provider, 
     * this provider will not be used anymore.
     */
    public void close() {
        this.observer = null;
        dismiss();
    }

    /** 
     * Set the keyboard height observer to this provider. The 
     * observer will be notified when the keyboard height has changed. 
     * For example when the keyboard is opened or closed.
     * 
     * @param observer The observer to be added to this provider.
     */
    public void setKeyboardHeightObserver(KeyboardHeightObserver observer) {
        this.observer = observer;
    }
   
    /**
     * Get the screen orientation
     *
     * @return the screen orientation
     */
    private int getScreenOrientation() {
        return activity.getResources().getConfiguration().orientation;
    }
    private static int getVirtualBarHeight(Context context) {
        int vh = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = DeviceUtils.getDisplayMetrics(context);

        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            vh = dm.heightPixels - windowManager.getDefaultDisplay().getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vh;
    }
    private static int getStatusBarHeight(Context cxt){
        int height = 0;
        int resourceId = cxt.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = cxt.getResources().getDimensionPixelSize(resourceId);
        }
        return height;
    }

    /**
     * Popup window itself is as big as the window of the Activity. 
     * The keyboard can then be calculated by extracting the popup view bottom 
     * from the activity window height. 
     */
    private void handleOnGlobalLayout() {
        Point screenSize = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(screenSize);
        Rect rect = new Rect();
        popupView.getWindowVisibleDisplayFrame(rect);

        // REMIND, you may like to change this using the fullscreen size of the phone
        // and also using the status bar and navigation bar heights of the phone to calculate
        // the keyboard height. But this worked fine on a Nexus.
        int orientation = getScreenOrientation();
        int contenth=parentView.getHeight();

        int fixNaviBarHeight = 0;
        if (contenth > 0 && screenSize.y > 0 && popupView.getHeight() > 0) {
            //popWindow高度实际上可能没有MATCH_PARENT 例如有navigation但全屏隐藏的时候 因此在需要的时候加上这个offset
            //但不需要加的时候不能加
            if (popupView.getHeight() < contenth && lastRectBottom < contenth && (lastRectBottom==screenSize.y || popupView.getHeight()==screenSize.y))
                fixNaviBarHeight = contenth - screenSize.y;
//            LogUtils.e("KeyboardHeightProvider", "fixNaviBarHeight = " + fixNaviBarHeight);
//            LogUtils.e("KeyboardHeightProvider", "popupView h= " + popupView.getHeight());
        }

        int contentoffset=0;
        int visibleoffset=0;
        if(contenth!=lastContentHigh){
            if(lastContentHigh!=0){
                contentoffset =contenth-lastContentHigh;
            }
            lastContentHigh=contenth;
        }
        if(lastRectBottom!=rect.bottom){
            if(lastRectBottom!=0){
                if ((lastRectBottom == screenSize.y && rect.bottom == parentView.getHeight())
                        || lastRectBottom == parentView.getHeight() && rect.bottom == screenSize.y) {
                    //此时是navigation的变化 可忽略 否则导致接收者（例如评论框）负margin
                    visibleoffset = 0;
                } else {
                    visibleoffset = rect.bottom - lastRectBottom;
                }
                //高度补上fixNaviBarHeight
                visibleoffset = visibleoffset > 0 ? visibleoffset+fixNaviBarHeight : visibleoffset-fixNaviBarHeight;
//                LogUtils.e("KeyboardHeightProvider", "handleOnGlobalLayout rect.bottom="+rect.bottom + " lastRectBottom=" + lastRectBottom);
            }
            lastRectBottom=rect.bottom;
        }
        int keyboardHeight=contentoffset-visibleoffset;
//        LogUtils.e("KeyboardHeightProvider", "handleOnGlobalLayout  keyboardHeight=" + keyboardHeight);
        int botttombar=getVirtualBarHeight(activity);
        int statusbar=getStatusBarHeight(activity);
        if (keyboardHeight == 0) {
            return;
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            this.keyboardPortraitHeight = keyboardHeight; 
            notifyKeyboardHeightChanged(keyboardPortraitHeight, orientation);
        } 
        else {
            this.keyboardLandscapeHeight = keyboardHeight; 
            notifyKeyboardHeightChanged(keyboardLandscapeHeight, orientation);
        }
    }

    /**
     *
     */
    private void notifyKeyboardHeightChanged(int height, int orientation) {
        if (observer != null) {
            observer.onKeyboardHeightChanged(height, orientation);
        }
    }
}
