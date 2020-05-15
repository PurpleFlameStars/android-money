package com.dzfd.gids.baselibs.utils;

import android.content.ClipData;
import android.content.Context;
import android.text.TextUtils;

/**
 * Created by zhangdecheng on 2016/12/12.
 */
public class CopyUtils {

    private static final String TAG = "CopyUtils";

    public interface CopyListener {
        void onSuccess();
    }

    public static void doCopy(Context context, String content, CopyListener listener) {
        if (TextUtils.isEmpty(content)) {
            return;
        }

        try {
            int currentVersion = android.os.Build.VERSION.SDK_INT;

            if (currentVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", content);
                clipboard.setPrimaryClip(clip);
            } else {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(content);
            }

            if (listener != null) {
                listener.onSuccess();
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "ClipboardManager error", e);
        }
    }

    public static String getTextFromClipboard(Context context) {
        try {
            int currentVersion = android.os.Build.VERSION.SDK_INT;

            if (currentVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                if(clipboard != null && clipboard.hasPrimaryClip()) {
                    ClipData clipData = clipboard.getPrimaryClip();
                    ClipData.Item item = clipData.getItemAt(0);
                    if(item != null && item.getText() != null) {
                        return item.getText().toString();
                    }
                }
            } else {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                if(clipboard != null && clipboard.getText() != null) {
                    return clipboard.getText().toString();
                }
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "ClipboardManager error", e);
        }
        return "";
    }
}
