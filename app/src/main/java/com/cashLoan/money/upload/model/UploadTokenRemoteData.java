package com.cashLoan.money.upload.model;

import android.text.TextUtils;

import com.cashLoan.money.network.LemonRemoteData;

import java.io.Serializable;

/**
 * 文件上传需要使用的token数据
 */
public class UploadTokenRemoteData extends LemonRemoteData implements Serializable {
    private static final long serialVersionUID = -7909958615582549725L;
    public UploadToken data;
    public class UploadToken implements Serializable {
        private static final long serialVersionUID = -3367556759410321143L;
        public String zone;
        public String token;
        public String url;
        public String domain;
        public String key;
    }

    @Override
    public boolean dataIsValid() {
        return (data != null && !TextUtils.isEmpty(data.token) && !TextUtils.isEmpty(data.key) && !TextUtils.isEmpty(data.url));
    }
}
