package com.dzfd.gids.baselibs.network;

import android.text.TextUtils;
import android.util.Log;

import com.dzfd.gids.baselibs.listener.NetWorkListener;
import com.dzfd.gids.baselibs.listener.RequestCell;
import com.dzfd.gids.baselibs.utils.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

/**
 * Created by zheng on 2018/7/5.
 */

public  class DownloadFileObserver implements Observer<ResponseBody>{
    private static  String TAG="DOWNFILE:";
    private String filepath="";
    private NetWorkListener<String> mcallback;
    private RequestCell mcell;
    public DownloadFileObserver(NetWorkListener<String> listener,String file, RequestCell cell){
        mcallback=listener;
        filepath=file;
        mcell=cell;

    }

    @Override
    public void onSubscribe(Disposable d) {
        if(mcallback!=null){
            mcallback.onLoading(mcell);
        }
    }

    @Override
    public void onNext(ResponseBody responseBody) {
        LogUtils.d(TAG, "server contacted and has file");
        boolean writtenToDisk = writeResponseBodyToDisk(responseBody);
        if(writtenToDisk){
            if(mcallback!=null){
                mcallback.OnSucess(filepath,mcell);
            }
        }else{
            if(mcallback!=null){
                mcallback.onFailed(mcell,8404,"save file error");
            }
        }

        LogUtils.d(TAG, "file download was a success? " + writtenToDisk);
    }

    @Override
    public void onError(Throwable e) {
        int errcode=0;
        String errmsg="";
        if(e instanceof HttpException){
            HttpException exception=(HttpException)e;
            errcode=exception.response().code();
            errmsg=exception.response().message();
        }else if(e instanceof Exception){
            Exception exception=(Exception)e;
            errcode=20;
            errmsg=exception.getMessage();
        }
        if(mcallback !=null){
            mcallback.onFailed(mcell,errcode,errmsg);
        }
    }

    @Override
    public void onComplete() {
        LogUtils.d(TAG,"download file finished");
        if(mcallback!=null){
            mcallback.onComplete();
            mcallback=null;
        }

    }
     public  String getDesFile(){
        return filepath;
     }

    boolean writeResponseBodyToDisk(ResponseBody body) {
        String filepath=getDesFile();
        if(TextUtils.isEmpty(filepath)){
            return false;
        }
        File futureStudioIconFile = new File(getDesFile());
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d("downfile", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}
