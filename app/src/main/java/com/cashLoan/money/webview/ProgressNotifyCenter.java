package com.cashLoan.money.webview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public enum ProgressNotifyCenter {
    INSTANCE;
    private List<INotifyCallback> mNotifyCallbackList = new ArrayList<>();

    public void addNotifyCallback(INotifyCallback callback) {
        if(callback == null) {
            return ;
        }
        mNotifyCallbackList.add(callback);
    }

    public void removeNotifyCallback(INotifyCallback callback) {
        if(mNotifyCallbackList == null || callback == null) {
            return ;
        }
        Iterator<INotifyCallback> it = mNotifyCallbackList.iterator();
        while (it.hasNext()) {
            INotifyCallback item = it.next();
            if(item == callback) {
                it.remove();
                break;
            }
        }
    }

    public void onProgressChange(String url, int progress) {
        for(INotifyCallback callback : mNotifyCallbackList) {
            callback.onProgressChange(url,progress);
        }
    }

    public void onPageClose(String url) {
        for(INotifyCallback callback : mNotifyCallbackList) {
            callback.onPageClose(url);
        }
        mNotifyCallbackList.clear();
    }

    public interface INotifyCallback {
        void onProgressChange(String url, int progress);
        void onPageClose(String url);
    }

}
