package com.cashLoan.money.webview;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;


import com.dzfd.gids.baselibs.utils.DeviceUtils;

import java.util.HashMap;

public class KeyboardShowObserver {
    private static KeyboardShowObserver INSTANCE;
    private HashMap<OnKeyboardShowListener, CustomOnGlobalLayoutListener> keyboardListeners;

    public static KeyboardShowObserver getInstance() {
        if (INSTANCE == null) {
            synchronized (KeyboardShowObserver.class) {
                if (INSTANCE == null) {
                    INSTANCE = new KeyboardShowObserver();
                }
            }
        }
        return INSTANCE;
    }

    private KeyboardShowObserver() {
        keyboardListeners = new HashMap<>();
    }

    public synchronized void registerKeyboardShowListener(OnKeyboardShowListener listener, View rootView, boolean register) {
        if (register) {
            if (!keyboardListeners.containsKey(listener)) {
                CustomOnGlobalLayoutListener globalLayoutListener = new CustomOnGlobalLayoutListener(rootView, listener);
                keyboardListeners.put(listener, globalLayoutListener);
                rootView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
            }
        } else {
            if (keyboardListeners.containsKey(listener)) {
                CustomOnGlobalLayoutListener globalLayoutListener = keyboardListeners.get(listener);
                rootView.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
                keyboardListeners.remove(listener);
            }
        }
    }

    public interface OnKeyboardShowListener {
        void onKeyboardHeightChange(int height);
    }

    private class CustomOnGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        private View rootView;
        private OnKeyboardShowListener listener;
        private Rect r = new Rect();
        private boolean keyboardShowing;

        public CustomOnGlobalLayoutListener(View rootView, OnKeyboardShowListener listener) {
            this.rootView = rootView;
            this.listener = listener;
        }

        @Override
        public void onGlobalLayout() {
            //获取当前界面可视部分
            ((Activity) rootView.getContext()).getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
            //获取屏幕的高度
            int height = DeviceUtils.getScreenHeightNoCache(rootView.getContext());
            //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
            int heightDifference = height - r.bottom;
            boolean showing = heightDifference > height / 4;
            if (keyboardShowing && !showing || !keyboardShowing && showing) {
                keyboardShowing = showing;
                int[] location = new int[2];
                rootView.getLocationOnScreen(location);
                int keyboardHeight = rootView.getHeight() + location[1] - (height - heightDifference);
                if (listener != null) {
                    listener.onKeyboardHeightChange(keyboardHeight);
                }
            }
        }
    }
}
