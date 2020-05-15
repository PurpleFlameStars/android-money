package com.cashLoan.money.upload;

import java.util.List;

/**
 * 批量上传文件的监听
 */
public interface IMultiUploadStatusListener {
//    void uploadSuccess(String resultUrl);
//    void uploadFail(int code, String msg);
    void onResult(List<String> successUrls, List<String> failPaths);
    void uploadSuccess(List<String> successUrls);
}
