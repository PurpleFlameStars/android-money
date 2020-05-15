package com.cashLoan.money.upload;

import com.cashLoan.money.upload.qiniu.QiniuUploader;

public class UploaderFactory {
    public static IUploaderInterface create() {
        return new QiniuUploader();
    }
}
