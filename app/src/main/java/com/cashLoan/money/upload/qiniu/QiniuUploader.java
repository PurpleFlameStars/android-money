package com.cashLoan.money.upload.qiniu;

import com.cashLoan.money.upload.IMultiUploadStatusListener;
import com.cashLoan.money.upload.IUploadStatusListener;
import com.cashLoan.money.upload.IUploaderInterface;
import com.cashLoan.money.upload.UploadConst;
import com.cashLoan.money.upload.Util;
import com.cashLoan.money.upload.model.UploadLogTokenData;
import com.cashLoan.money.upload.model.UploadTokenRemoteData;
import com.cashLoan.money.upload.option.LemonUploadOptions;
import com.cashLoan.money.upload.request.HeadIconUploadTokenRequest;
import com.cashLoan.money.upload.request.LogUploadTokenRequest;
import com.cashLoan.money.upload.request.PicUploadTokenRequest;
import com.cashLoan.money.upload.request.VideoUploadTokenRequest;
import com.dzfd.gids.baselibs.listener.NetWorkListenerAdapter;
import com.dzfd.gids.baselibs.listener.NetWorkListenerImpl;
import com.dzfd.gids.baselibs.listener.RequestCell;
import com.dzfd.gids.baselibs.network.NetUtils;
import com.dzfd.gids.baselibs.utils.ContextUtils;
import com.dzfd.gids.baselibs.utils.LogUtils;
import com.qiniu.android.common.FixedZone;
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QiniuUploader implements IUploaderInterface {
    private static final String TAG = Util.TAG;

    private final String zone0 = "zone0";
    private final String zone1 = "zone1";
    private final String zone2 = "zone2";
    private final String zoneNa0 = "zoneNa0";
    private final String zoneAs0 = "zoneAs0";
    private Map<String, Zone> zoneMap = new HashMap<String, Zone>() {
        private static final long serialVersionUID = -4226659070915103052L;
        {
            put(zone0, FixedZone.zone0);
            put(zone1, FixedZone.zone1);
            put(zone2, FixedZone.zone2);
            put(zoneNa0, FixedZone.zoneNa0);
            put(zoneAs0, FixedZone.zoneAs0);
        }
    };
    //sdk对应上传图片所在的机房区域，最好往所在区域上传图片
    private String localZone;
    private UploadManager uploadManager;

    //检查初始化UploadManager
    private synchronized void checkUploadManager() {
        if (uploadManager != null) {
            return;
        }
        Configuration.Builder builder = new Configuration.Builder()
                .chunkSize(512 * 1024)        // 分片上传时，每片的大小。 默认256K
                .putThreshhold(1024 * 1024)   // 启用分片上传阀值。默认512K
                .connectTimeout(10)           // 链接超时。默认10秒
                .useHttps(true)               // 是否使用https上传域名
//                .recorder(recorder)           // recorder分片上传时，已上传片记录器。默认null
//                .recorder(recorder, keyGen)   // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                .responseTimeout(60);          // 服务器响应超时。默认60秒

        Zone zone = zoneMap.get(localZone);
        if (zone == null) {
            zone = FixedZone.zoneNa0;
        }
        builder.zone(zone);

        Configuration configuration = builder.build();
        // 重用uploadManager。一般地，只需要创建一个uploadManager对象
        uploadManager = new UploadManager(configuration);
    }

    @Override
    public void uploadIcon(final File file, final IUploadStatusListener listener) {
        if (!Util.checkFileValidCallback(file, listener)) {
            return;
        }

        new HeadIconUploadTokenRequest().getToken("",getNetworkListener(listener, file, null));
    }

    @Override
    public void uploadPic(String type, final File file, final IUploadStatusListener listener) {
        if (!Util.checkFileValidCallback(file, listener)) {
            return;
        }

        new PicUploadTokenRequest().getToken(type,getNetworkListener(listener, file, null));
    }

    @Override
    public void uploadVideo(String type, final File file, final IUploadStatusListener listener) {
        if (!Util.checkFileValidCallback(file, listener)) {
            return;
        }

        new VideoUploadTokenRequest().getToken(type,getNetworkListener(listener, file, null));
    }

    @Override
    public void uploadVideo(String type,final File file, final IUploadStatusListener listener, LemonUploadOptions options) {
        if (!Util.checkFileValidCallback(file, listener)) {
            return;
        }

        new VideoUploadTokenRequest().getToken(type,getNetworkListener(listener, file, options));
    }

    private NetWorkListenerImpl<UploadTokenRemoteData> getNetworkListener(final IUploadStatusListener listener, final File file, final LemonUploadOptions options) {
        return new NetWorkListenerImpl<UploadTokenRemoteData>() {
            @Override
            public void OnSucess(UploadTokenRemoteData Data, RequestCell cell) {
                super.OnSucess(Data, cell);
                if (Data == null || !Data.dataIsValid()) {
                    Util.failCallback(listener, UploadConst.Code.TOKEN_ERROR, "token error");
                    return;
                }
                UploadTokenRemoteData.UploadToken token = Data.data;
                localZone = token.zone;
                uploadFile(file, token.url, token.key, token.token, listener, options);
            }

            @Override
            public void onFailed(RequestCell cell, int code, String msg) {
                super.onFailed(cell, code, msg);
                Util.failCallback(listener, UploadConst.Code.TOKEN_ERROR, "token error");
            }
        };
    }

    private void uploadFile(File file, final String url, String key, String token, final IUploadStatusListener listener) {
        uploadFile(file, url, key, token, listener, null);
    }

    private void uploadFile(File file, final String url, String key, String token, final IUploadStatusListener listener, final LemonUploadOptions options) {
        checkUploadManager();
        if (uploadManager == null) {
            Util.failCallback(listener, UploadConst.Code.UPLOAD_MANAGER_NULL, "upload manager is null");
            return;
        }
        uploadManager.put(file, key, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                LogUtils.d(TAG, "complete() called with: key = [" + key + "], info = [" + info + "], response = [" + response + "]");
                if (info != null && info.isOK()) {
                    Util.successCallback(listener, url);
                } else {
                    Util.failCallback(listener, UploadConst.Code.UPLOAD_QINNIU_FAIL, "upload to qiniu fail");
                }
            }
        }, new UploadOptions(null, null, false, null,
                new UpCancellationSignal(){
                    public boolean isCancelled(){
                        if (options != null && options.getCancelListener() != null) {
                            return options.getCancelListener().isCancel();
                        } else {
                            return false;
                        }
                    }
                }));
    }

    @Override
    public void multiUploadPics(String type,final List<String> paths, final IMultiUploadStatusListener listener) {
        if (paths == null || paths.size() <= 0) {
            return;
        }
        final Map<String, String> successUrlMap = new HashMap<>();
        final Map<String, String> failPathMap = new HashMap<>();
        for (final String item : paths) {
            uploadPic(type, new File(item), new IUploadStatusListener() {
                @Override
                public void uploadSuccess(String resultUrl) {
                    successUrlMap.put(item, resultUrl);
                    checkMultiCallback(paths, successUrlMap, failPathMap, listener);
                }

                @Override
                public void uploadFail(int code, String msg) {
                    failPathMap.put(item, item);
                    checkMultiCallback(paths, successUrlMap, failPathMap, listener);
                }
            });
        }

    }

    private void checkMultiCallback(List<String> originPaths, Map<String, String> successUrlMap, Map<String, String> failPathsMap, IMultiUploadStatusListener listener) {
        if (listener == null || originPaths == null || originPaths.size() <= 0) {
            return;
        }
        if (successUrlMap == null || failPathsMap == null) {
            return;
        }
        int originLength = originPaths.size();
        int successLength = successUrlMap.size();
        int failLength = failPathsMap.size();
        if (originLength == successLength) {
            List<String> successResult = new ArrayList<>();
            for (String item : originPaths) {
                if (successUrlMap.containsKey(item)) {
                    successResult.add(successUrlMap.get(item));
                }
            }
            listener.uploadSuccess(successResult);
        } else if (originLength == successLength + failLength) {
            List<String> successResult = new ArrayList<>();
            List<String> failResult = new ArrayList<>();
            for (String item : originPaths) {
                if (successUrlMap.containsKey(item)) {
                    successResult.add(successUrlMap.get(item));
                } else if (failPathsMap.containsKey(item)) {
                    failResult.add(item);
                }
            }
            listener.onResult(successResult, failResult);
        }
    }

    @Override
    public void uploadLog(final File file, final IUploadStatusListener listener) {
        if (!Util.checkFileValidCallback(file, listener)) {
            return;
        }

        new LogUploadTokenRequest().getToken(new NetWorkListenerAdapter<UploadLogTokenData>() {
            @Override
            public void OnSucess(UploadLogTokenData Data, RequestCell cell) {
                if (Data != null && Data.dataIsValid()) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                    UploadTokenRemoteData.UploadToken token = Data.data;
                    token.key = NetUtils.getUid(ContextUtils.getApplicationContext()) + "_"
                            + format.format(new Date(System.currentTimeMillis())) + ".log";
                    localZone = token.zone;
                    uploadFile(file, token.url, token.key, token.token, listener, null);
                }
            }

            @Override
            public void onFailed(RequestCell cell, int code, String msg) {

            }
        });
    }
}
