package com.cashLoan.money.upload;

import com.cashLoan.money.upload.option.LemonUploadOptions;

import java.io.File;
import java.util.List;

public interface IUploaderInterface {
    void uploadIcon(File file, IUploadStatusListener listener);
    void uploadPic(String type, File file, IUploadStatusListener listener);
    void uploadVideo(String type, File file, IUploadStatusListener listener);
    void uploadVideo(String type, File file, IUploadStatusListener listener, LemonUploadOptions options);
    void multiUploadPics(String type, List<String> paths, IMultiUploadStatusListener listener);
    void uploadLog(File file, IUploadStatusListener listener);
}
