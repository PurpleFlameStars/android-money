package com.GoLemon.supplier.config;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

public enum  SysMediaPath {
    SYS_PATH_MOVIES(Environment.DIRECTORY_MOVIES),
    SYS_PATH_MUSICS(Environment.DIRECTORY_MUSIC),
    SYS_PATH_PICTURES(Environment.DIRECTORY_PICTURES),
    SYS_PATH_DOWNLOAD(Environment.DIRECTORY_DOWNLOADS);

    private String _dirType;
    SysMediaPath(String dirtype) {
        _dirType=dirtype;
    }
    public String getMediaPath(){
        return Environment.getExternalStoragePublicDirectory(_dirType).getAbsolutePath();
    }
    public static void SendMedieStoreScan(Context cxt, String filepath){
        Uri uri = Uri.fromFile(new File(filepath));
        Intent intent=new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri);
        cxt.sendBroadcast(intent);
    }
}
