package com.cashLoan.money.upload;

import com.cashLoan.money.upload.option.LemonUploadOptions;

import java.io.File;
import java.util.List;

public class UploaderExecutor implements IUploaderInterface {

    private static UploaderExecutor executor;
    private UploaderExecutor() {
        uploaderInterface = UploaderFactory.create();
    }
    public static UploaderExecutor getInstance() {
        if (executor == null) {
            synchronized (UploaderExecutor.class) {
                if (executor == null) {
                    executor = new UploaderExecutor();
                }
            }
        }
        return executor;
    }

    private IUploaderInterface uploaderInterface;

    @Override
    public void uploadIcon(File file, IUploadStatusListener listener) {
        if (uploaderInterface == null) {
            return;
        }
        uploaderInterface.uploadIcon(file,listener);
    }

    @Override
    public void uploadPic(String type, File file, IUploadStatusListener listener) {
        if (uploaderInterface == null) {
            return;
        }
        uploaderInterface.uploadPic(type, file, listener);
    }

    @Override
    public void uploadVideo(String type, File file, IUploadStatusListener listener) {
        if (uploaderInterface == null) {
            return;
        }
        uploaderInterface.uploadVideo(type, file, listener);
    }

    @Override
    public void uploadVideo(String type,File file, IUploadStatusListener listener, LemonUploadOptions options) {
        if (uploaderInterface == null) {
            return;
        }
        uploaderInterface.uploadVideo(type,file, listener, options);
    }

    @Override
    public void multiUploadPics(String type,List<String> paths, IMultiUploadStatusListener listener) {
        if (uploaderInterface == null) {
            return;
        }
        uploaderInterface.multiUploadPics(type,paths, listener);
    }

    @Override
    public void uploadLog(File file, IUploadStatusListener listener) {
        if (uploaderInterface == null) {
            return;
        }
        uploaderInterface.uploadLog(file, listener);
    }
}
