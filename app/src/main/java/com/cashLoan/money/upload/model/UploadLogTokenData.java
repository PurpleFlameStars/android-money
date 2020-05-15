package com.cashLoan.money.upload.model;

import android.text.TextUtils;

public class UploadLogTokenData extends UploadTokenRemoteData {
    @Override
    public boolean dataIsValid() {
        return (data != null && !TextUtils.isEmpty(data.token));
    }
}
