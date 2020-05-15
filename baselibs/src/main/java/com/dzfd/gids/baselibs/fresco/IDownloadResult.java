package com.dzfd.gids.baselibs.fresco;

import java.io.File;

/**
 * 下载图片的结果监听器
 */
public abstract class IDownloadResult implements IResult<File> {

    private File mFile;

    public IDownloadResult(File filePath) {
        this.mFile = filePath;
    }

    public File getFile() {
        return mFile;
    }

    public void onProgress(int progress) {
    }

    @Override
    public abstract void onResult(File filePath);

}
