package com.cashLoan.money.upload;

import com.dzfd.gids.baselibs.utils.LogUtils;

import java.io.File;

public class Util {
    public static final String TAG = "UploadLog";
    public static boolean checkFileValidCallback(File file, IUploadStatusListener listener) {
        if (file == null || !file.exists()) {
            failCallback(listener, UploadConst.Code.FILE_NOT_EXIST, "file is not exist");
            return false;
        }
        return true;
    }

    public static void failCallback(IUploadStatusListener listener, int code, String msg) {
        LogUtils.d(TAG, "failCallback() called with: listener = [" + listener + "], code = [" + code + "], msg = [" + msg + "]");
        if (listener != null) {
            listener.uploadFail(code, msg);
        }
    }

    public static void successCallback(IUploadStatusListener listener, String url) {
        LogUtils.d(TAG, "successCallback() called with: listener = [" + listener + "], url = [" + url + "]");
        if (listener != null) {
            listener.uploadSuccess(url);
        }
    }
}
