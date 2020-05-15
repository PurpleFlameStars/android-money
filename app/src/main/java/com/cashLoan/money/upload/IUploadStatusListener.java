package com.cashLoan.money.upload;

/**
 * 上传文件的监听
 */
public interface IUploadStatusListener {
    void uploadSuccess(String resultUrl);
    void uploadFail(int code, String msg);
}
