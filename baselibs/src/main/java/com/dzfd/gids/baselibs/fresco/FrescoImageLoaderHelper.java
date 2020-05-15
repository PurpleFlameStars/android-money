//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.dzfd.gids.baselibs.fresco;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.dzfd.gids.baselibs.R;
import com.dzfd.gids.baselibs.utils.AndroidUtilsCompat;
import com.dzfd.gids.baselibs.utils.BitmapUtils;
import com.dzfd.gids.baselibs.utils.BunToast;
import com.dzfd.gids.baselibs.utils.ContextUtils;
import com.dzfd.gids.baselibs.utils.FileUtils;
import com.dzfd.gids.baselibs.utils.LogUtils;
import com.dzfd.gids.baselibs.utils.thread.ThreadUtils;
import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBufferInputStream;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.drawable.ScalingUtils.ScaleType;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FrescoImageLoaderHelper {
    private static final String TAG = "Fresco";
    public static final int MAX_BITMAP_WIDTH_PIXEL = 270;
    private static RoundingParams circleRoundingParams = (new RoundingParams()).setRoundAsCircle(true);

    public FrescoImageLoaderHelper() {

    }

    public static void clearCache() {
        Fresco.getImagePipelineFactory().getImagePipeline().clearMemoryCaches();
        Fresco.getImagePipelineFactory().getBitmapCountingMemoryCache().clear();
        Fresco.getImagePipelineFactory().getEncodedCountingMemoryCache().clear();
        Fresco.getImagePipeline().clearMemoryCaches();
    }

    public static void clearDiskCaches() {
        Fresco.getImagePipeline().clearDiskCaches();
    }
    public static void SetPlayceHolder_e2e5ec(SimpleDraweeView thumbView){
        if(thumbView==null){
            return;
        }
        Drawable playceholder=thumbView.getResources().getDrawable(R.drawable.e2e5ec);
        SetPlayceHolder(thumbView,playceholder);
    }
    public static void SetPlayceHolder_f1f3f8(SimpleDraweeView thumbView){
        if(thumbView==null){
            return;
        }
        Drawable playceholder=thumbView.getResources().getDrawable(R.drawable.f1f3f8);
        SetPlayceHolder(thumbView,playceholder);

    }
    public static void SetPlayceHolder(SimpleDraweeView thumbView,Drawable playceholder){
        if(thumbView==null || playceholder == null){
            return;
        }
        GenericDraweeHierarchy hierarchy =thumbView.getHierarchy();
        if(playceholder!=null){
            hierarchy.setPlaceholderImage(playceholder, ScalingUtils.ScaleType.FIT_XY);
        }
    }
    public static String deleteImageQuery(String url){
        if(TextUtils.isEmpty(url)){
            return "";
        }
        int index=url.indexOf("?imageView2/0/w/");
        if(index!=-1){
            url=url.substring(0,index);
        }
        return url;

    }
    public static String _UpdataImageQuery(String url,int width,int height){

        url=deleteImageQuery(url);
        if(TextUtils.isEmpty(url)){
            return url;
        }else{
            String newurl=url+"?"+"imageView2/0/w/"+String.valueOf(width)+"/h/"+String.valueOf(height);
            return newurl;
        }
    }
    public static String _setImageByUrl(View view, String url,int width,int height){
        if(TextUtils.isEmpty(url)){
            return "";
        }
        String newurl=_UpdataImageQuery(url,width,height);
        setImageByUrl(view,newurl);
        return newurl;
    }
    public static void setImageByUrl(View view, String url) {
        if (view != null && view instanceof SimpleDraweeView) {
            SimpleDraweeView draweeView = (SimpleDraweeView) view;
            if (url == null) {
                url = "";
            }

            draweeView.setImageURI(Uri.parse(url));
        }
    }

    public static void setImageByUrl(View view, String url, ScaleType scaleType) {
        if (view != null && view instanceof SimpleDraweeView) {
            SimpleDraweeView draweeView = (SimpleDraweeView) view;
            if (url == null) {
                url = "";
            }

            GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(ContextUtils.getApplicationContext().getResources());
            GenericDraweeHierarchy hierarchy = builder.setPlaceholderImageScaleType(ScaleType.FIT_XY).setActualImageScaleType(scaleType).build();
            draweeView.setHierarchy(hierarchy);
            draweeView.setImageURI(Uri.parse(url));
        }
    }

    public static void setImageByUrl(View view, String url, int width, int height) {
        if (view != null && view instanceof SimpleDraweeView) {
            SimpleDraweeView draweeView = (SimpleDraweeView) view;
            if (url == null) {
                url = "";
            }

            if (width == 0 || height == 0) {
                setImageByUrl(view, url);
                return;
            }

            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url)).setResizeOptions(new ResizeOptions(width, height)).setProgressiveRenderingEnabled(false).build();
            PipelineDraweeController controller = (PipelineDraweeController) ((Fresco.newDraweeControllerBuilder().setUri(url).setImageRequest(request)).setOldController(draweeView.getController())).build();
            draweeView.setController(controller);
        }
    }
    public static void LoadImage(View view,String url,int width,int height,boolean resize,boolean ProgressiveRender){
        if(view == null || !(view instanceof SimpleDraweeView) || TextUtils.isEmpty(url)){
            return;
        }
        if (width == 0 || height == 0) {
            setImageByUrl(view, url);
            return;
        }
        SimpleDraweeView draweeView = (SimpleDraweeView) view;
        ImageRequestBuilder imageRequestBuilder=ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setProgressiveRenderingEnabled(ProgressiveRender);
        if(resize){
            imageRequestBuilder.setResizeOptions(new ResizeOptions(width, height));
        }
        ImageRequest request =imageRequestBuilder.build();

        PipelineDraweeControllerBuilder builder= Fresco.newDraweeControllerBuilder();
        builder.setImageRequest(request)
                .setOldController(draweeView.getController());

        draweeView.setController(builder.build());

    }

    public static void setImageByFilePath(View view, String filePath) {
        if (view != null && view instanceof SimpleDraweeView) {
            SimpleDraweeView draweeView = (SimpleDraweeView) view;
            setImageByFilePath(draweeView, filePath, -1);
        }
    }

    public static void setImageByFilePath(View view, String filePath, int id) {
        if (view != null && view instanceof SimpleDraweeView) {
            SimpleDraweeView draweeView = (SimpleDraweeView) view;
            if (filePath == null || !(new File(filePath)).exists()) {
                filePath = "";
            }

            if (id > 0) {
                GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(ContextUtils.getApplicationContext().getResources());
                GenericDraweeHierarchy hierarchy = builder.setPlaceholderImageScaleType(ScaleType.FIT_XY).setFailureImage(ContextUtils.getApplicationContext().getResources().getDrawable(id), ScaleType.FIT_XY).build();
                draweeView.setHierarchy(hierarchy);
            }

            draweeView.setImageURI(Uri.parse("file://" + filePath));
        }
    }

    public static void setImageByFilePath(View view, String filePath, ControllerListener controllerListener) {
        if (view != null && view instanceof SimpleDraweeView) {
            SimpleDraweeView draweeView = (SimpleDraweeView) view;
            if (filePath == null) {
                filePath = "";
            }

            setImageByUrl(draweeView, Uri.parse("file://" + filePath).toString(), (ControllerListener) controllerListener);
        }
    }

    public static void setImageByFilePath(View view, int width, int height, String filePath, int id) {
        if (view != null && view instanceof SimpleDraweeView) {
            SimpleDraweeView draweeView = (SimpleDraweeView) view;
            if (filePath == null || !(new File(filePath)).exists()) {
                filePath = "";
            }

            if (id > 0) {
                GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(ContextUtils.getApplicationContext().getResources());
                GenericDraweeHierarchy hierarchy = builder.setPlaceholderImageScaleType(ScaleType.FIT_XY).setFailureImage(ContextUtils.getApplicationContext().getResources().getDrawable(id), ScaleType.FIT_XY).build();
                draweeView.setHierarchy(hierarchy);
            }

            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse("file://" + filePath)).setResizeOptions(new ResizeOptions(width, height)).setProgressiveRenderingEnabled(true).setAutoRotateEnabled(true).build();
            PipelineDraweeController controller = (PipelineDraweeController) (( Fresco.newDraweeControllerBuilder().setImageRequest(request)).setOldController(draweeView.getController())).build();
            draweeView.setController(controller);
        }
    }

    public static void setImageByResouceId(View view, int resouceId) {
        if (view != null && view instanceof SimpleDraweeView) {
            SimpleDraweeView draweeView = (SimpleDraweeView) view;
            draweeView.setImageURI(Uri.parse("res://" + "com.golemon.wegoo" + "/" + resouceId));
        }
    }

    public static void setImageByUrl(View view, String url, ControllerListener controllerListener) {
        if (view != null && view instanceof SimpleDraweeView) {
            SimpleDraweeView draweeView = (SimpleDraweeView) view;
            if (url == null) {
                url = "";
            }



            setImageByUrl(draweeView, url, (ControllerListener) controllerListener, (GenericDraweeHierarchy) null);
        }
    }

    public static boolean isInMemoryCache(String url) {
        if (!TextUtils.isEmpty(url)) {
            try {
                return Fresco.getImagePipeline().isInBitmapMemoryCache(Uri.parse(url));
            } catch (Exception var2) {
                ;
            }
        }

        return false;
    }

    public static void setImageplaceHolderRes(View view, int placeHolderRes) {
        if (view != null && view instanceof SimpleDraweeView) {
            SimpleDraweeView draweeView = (SimpleDraweeView) view;
            if (placeHolderRes > 0) {
                setImageByDrawable(draweeView, AndroidUtilsCompat.getDrawable(ContextUtils.getApplicationContext().getResources(), placeHolderRes));
            }

        }
    }

    public static void setImageByDrawable(View view, Drawable drawable) {
        if (view != null && view instanceof SimpleDraweeView) {
            SimpleDraweeView draweeView = (SimpleDraweeView) view;
            if (drawable != null) {
                if (drawable instanceof LayerDrawable) {
                    drawable = new BitmapDrawable(ContextUtils.getApplicationContext().getResources(), BitmapUtils.drawableToBitamp((Drawable) drawable));
                }

                GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(ContextUtils.getApplicationContext().getResources());
                GenericDraweeHierarchy hierarchy = builder.setPlaceholderImageScaleType(ScaleType.FIT_XY).setPlaceholderImage((Drawable) drawable).setActualImageScaleType(ScaleType.FIT_XY).build();
                draweeView.setHierarchy(hierarchy);
            }

        }
    }

    public static void setViewBackGround(String url, final View view) {
        getBitmapFromUrl(url, new BaseBitmapDataSubscriber() {
            protected void onNewResultImpl(Bitmap bitmap) {
                if (view != null) {
                    final Drawable background = new BitmapDrawable(ContextUtils.getApplicationContext().getResources(), BitmapUtils.copyBitmap(bitmap));
                    ThreadUtils.runOnUiThread(new Runnable() {
                        public void run() {
                            AndroidUtilsCompat.setBackgroundDrawable(view, background);
                        }
                    });
                }

            }

            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
            }
        });
    }

    public static void getBitmapFromUrl(String url, BaseBitmapDataSubscriber baseBitmapDataSubscriber) {
        if (url == null) {
            url = "";
        }

        getBitmapFromImageRequest(ImageRequest.fromUri(Uri.parse(url)), baseBitmapDataSubscriber);
    }

    public static void getBitmapFromImageRequest(ImageRequest imageRequest, BaseDataSubscriber baseBitmapDataSubscriber) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, (Object) null);
        dataSource.subscribe(baseBitmapDataSubscriber, CallerThreadExecutor.getInstance());
    }

    public static void setImageDrawable(final String url, final ImageView view, final int width, final int height) {
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url)).setResizeOptions(new ResizeOptions(width, height)).setProgressiveRenderingEnabled(true).build();
        getBitmapFromImageRequest(imageRequest, new BaseBitmapDataSubscriber() {
            protected void onNewResultImpl(Bitmap bitmap) {
                if (bitmap != null) {
                    LogUtils.e("Fresco", "setImageDrawable onNewResultImpl: bitmap size" + bitmap.getHeight() + "*" + bitmap.getWidth() + ",config:" + bitmap.getConfig() + ",width:" + width + ",height:" + height + ",url:" + url);
                }

                if (view != null) {
                    final Drawable background = new BitmapDrawable(ContextUtils.getApplicationContext().getResources(), BitmapUtils.copyBitmap(bitmap));
                    ThreadUtils.runOnUiThread(new Runnable() {
                        public void run() {
                            view.setImageDrawable(background);
                        }
                    });
                }

            }

            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
            }
        });
    }

    public static void setImageDrawable(final String url, final ImageView view) {
        ImageRequest imageRequest = ImageRequest.fromUri(Uri.parse(url));
        getBitmapFromImageRequest(imageRequest, new BaseBitmapDataSubscriber() {
            protected void onNewResultImpl(Bitmap bitmap) {
                if (bitmap != null) {
                    LogUtils.e("Fresco", "setImageDrawable onNewResultImpl: bitmap size" + bitmap.getHeight() + "*" + bitmap.getWidth() + ",config:" + bitmap.getConfig() + ",url:" + url);
                }

                if (view != null) {
                    final Drawable background = new BitmapDrawable(ContextUtils.getApplicationContext().getResources(), BitmapUtils.copyBitmap(bitmap));
                    ThreadUtils.runOnUiThread(new Runnable() {
                        public void run() {
                            view.setImageDrawable(background);
                        }
                    });
                }

            }

            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
            }
        });
    }

    public static void getBitmapFromUrl(@NonNull String url, BaseDataSubscriber baseDataSubscriber) {
        if (url == null) {
            url = "";
        }

        getBitmapFromImageRequest(ImageRequest.fromUri(Uri.parse(url)), baseDataSubscriber);
    }

    public static Bitmap getBitmapFromUrlSync(@NonNull String url) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Bitmap[] res = new Bitmap[]{null};
        getBitmapFromUrl(url, new BaseBitmapDataSubscriber() {
            protected void onNewResultImpl(Bitmap bitmap) {
                res[0] = BitmapUtils.copyBitmap(bitmap);
                countDownLatch.countDown();
            }

            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await(20L, TimeUnit.SECONDS);
        } catch (InterruptedException var4) {
            var4.printStackTrace();
        }

        return res[0];
    }
    public static void SaveImageToGallery(Context cxt,String url){
        Bitmap sourceBitmap=getBitmapFromUrlSync(url);
        if(sourceBitmap!=null){
            String path = MediaStore.Images.Media.insertImage(
                    cxt.getContentResolver(),
                    sourceBitmap, "", ""
            );
            if (path != null) {
                String csmsg=cxt.getString(R.string.save_image_sucess);
                csmsg=String.format(csmsg,path);
                BunToast.showLong(cxt, csmsg);
            } else {
                BunToast.showLong(cxt,R.string.save_image_faild);
            }
        }
    }

    public static void setRoundImageByUrl(SimpleDraweeView draweeView, String url) {
        setRoundImageByUrl(draweeView, url, (Drawable) null, circleRoundingParams);
    }

    public static void setRoundImageByUrl(View view, String url, Drawable placeHolder, RoundingParams roundingParams) {
        if (view != null && view instanceof SimpleDraweeView) {
            SimpleDraweeView draweeView = (SimpleDraweeView) view;
            GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(ContextUtils.getApplicationContext().getResources());
            GenericDraweeHierarchy hierarchy = builder.setRoundingParams(roundingParams == null ? circleRoundingParams : roundingParams).setPlaceholderImage(placeHolder).build();
            draweeView.setHierarchy(hierarchy);
            draweeView.setImageURI(Uri.parse(url));
        }
    }

    public static void setImageByAssert(View view, String url, ControllerListener controllerListener) {
        if (view != null && view instanceof SimpleDraweeView) {
            SimpleDraweeView draweeView = (SimpleDraweeView) view;
            url = "asset:///" + url;
            setImageByUrl(draweeView, url, (ControllerListener) controllerListener);
        }
    }

    public static void setImageByUrl(View view, String url, int placeHolderRes, ControllerListener controllerListener) {
        if (view != null && view instanceof SimpleDraweeView) {
            SimpleDraweeView draweeView = (SimpleDraweeView) view;
            if (placeHolderRes > 0) {
                setImageByUrl(draweeView, url, (Drawable) ContextUtils.getApplicationContext().getResources().getDrawable(placeHolderRes), (ControllerListener) controllerListener);
            } else {
                setImageByUrl(draweeView, url, (ControllerListener) controllerListener);
            }

        }
    }

    public static void setImageByUrl(View view, String url, Drawable placeHolderDrawable, ControllerListener controllerListener) {
        if (view != null && view instanceof SimpleDraweeView) {
            SimpleDraweeView draweeView = (SimpleDraweeView) view;
            if (placeHolderDrawable != null) {
                GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(ContextUtils.getApplicationContext().getResources());
                GenericDraweeHierarchy hierarchy = builder.setPlaceholderImageScaleType(ScaleType.FIT_XY).setPlaceholderImage(placeHolderDrawable).build();
                setImageByUrl(draweeView, url, (ControllerListener) controllerListener, (GenericDraweeHierarchy) hierarchy);
            } else {
                setImageByUrl(draweeView, url, (ControllerListener) controllerListener, (GenericDraweeHierarchy) null);
            }

        }
    }

    public static void setImageByUrl(View view, String lowResUrl, String highResUrl, int placeHolderRes, ControllerListener controllerListener) {
        if (view != null && view instanceof SimpleDraweeView) {
            SimpleDraweeView draweeView = (SimpleDraweeView) view;
            if (placeHolderRes > 0) {
                GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(ContextUtils.getApplicationContext().getResources());
                GenericDraweeHierarchy hierarchy = builder.setPlaceholderImageScaleType(ScaleType.FIT_XY).setPlaceholderImage(ContextUtils.getApplicationContext().getResources().getDrawable(placeHolderRes)).build();
                setImageByUrl(draweeView, lowResUrl, highResUrl, controllerListener, hierarchy);
            } else {
                setImageByUrl(draweeView, lowResUrl, highResUrl, controllerListener, (GenericDraweeHierarchy) null);
            }

        }
    }

    public static void setImageByUrl(View view, String url, ControllerListener controllerListener, GenericDraweeHierarchy hierarchy) {
        if (view != null && view instanceof SimpleDraweeView) {
            SimpleDraweeView draweeView = (SimpleDraweeView) view;
            if (url == null) {
                url = "";
            }

            if (hierarchy != null) {
                draweeView.setHierarchy(hierarchy);
            }

            DraweeController controller = ((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) Fresco.newDraweeControllerBuilder().setControllerListener(controllerListener)).setOldController(draweeView.getController())).setUri(Uri.parse(url)).build();
            draweeView.setController(controller);
        }
    }

    public static void setImageByUrl(View view, String lowResUrl, String highResUrl, ControllerListener controllerListener, GenericDraweeHierarchy hierarchy) {
        if (view != null && view instanceof SimpleDraweeView) {
            SimpleDraweeView draweeView = (SimpleDraweeView) view;
            if (lowResUrl == null) {
                lowResUrl = "";
            }

            if (highResUrl == null) {
                highResUrl = "";
            }

            if (hierarchy != null) {
                draweeView.setHierarchy(hierarchy);
            }

            DraweeController controller = ((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) ((PipelineDraweeControllerBuilder) Fresco.newDraweeControllerBuilder().setControllerListener(controllerListener)).setOldController(draweeView.getController())).setLowResImageRequest(ImageRequest.fromUri(lowResUrl))).setImageRequest(ImageRequest.fromUri(highResUrl))).build();
            draweeView.setController(controller);
        }
    }

    public static String getFileUrl(String filePath) {
        return "file://" + filePath;
    }

    public static String getFilePath(String filePath) {
        return !TextUtils.isEmpty(filePath) && filePath.contains("file://") ? filePath.substring(7) : filePath;
    }

    public static void prefetchImage(String url) {
        if (!TextUtils.isEmpty(url)) {
            Fresco.getImagePipeline().prefetchToBitmapCache(ImageRequest.fromUri(Uri.parse(url)), (Object) null);
        }
    }

    public static void prefetchImageToDiskCache(String url) {
        if (!TextUtils.isEmpty(url)) {
            Fresco.getImagePipeline().prefetchToDiskCache(ImageRequest.fromUri(Uri.parse(url)), (Object) null);
        }
    }

    public static void setRoundingParams(View view, float topLeft, float topRight, float bottomRight, float bottomLeft) {
        if (view != null && view instanceof SimpleDraweeView) {
            SimpleDraweeView draweeView = (SimpleDraweeView) view;
            GenericDraweeHierarchy hierarchy = (GenericDraweeHierarchy) draweeView.getHierarchy();
            if (hierarchy != null) {
                hierarchy.setRoundingParams(RoundingParams.fromCornersRadii(topLeft, topRight, bottomRight, bottomLeft));
                draweeView.setHierarchy(hierarchy);
            }
        }
    }

    public static long getDiskCacheSize() {
        return Fresco.getImagePipelineFactory().getMainFileCache().getSize();
    }

    public static File getLocalImageFile(String loadUri) {
        File file = null;
        if (TextUtils.isEmpty(loadUri)) {
            return null;
        } else {
            ImageRequest imageRequest = ImageRequest.fromUri(loadUri);
            CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(imageRequest, Uri.parse(loadUri));
            if (ImagePipelineFactory.getInstance().getMainFileCache().hasKey(cacheKey)) {
                BinaryResource resource = ImagePipelineFactory.getInstance().getMainFileCache().getResource(cacheKey);
                if (resource != null) {
                    file = ((FileBinaryResource) resource).getFile();
                }
            }

            return file;
        }
    }

    /**
     * 从网络下载图片
     * 1 根据提供的图片URL，获取图片数据流
     * 2 将得到的数据流写入指定路径的本地文件
     */
    public static void downloadImage(Context context, final String url, final IDownloadResult loadFileResult) {
        if (loadFileResult == null) return;
        if (TextUtils.isEmpty(url)) {
            loadFileResult.onResult(null);
            return;
        }

        Uri uri = Uri.parse(url);
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(uri);
        ImageRequest imageRequest = builder.build();

        DataSource<CloseableReference<PooledByteBuffer>> dataSource = imagePipeline.fetchEncodedImage(imageRequest, context);
        dataSource.subscribe(new BaseDataSubscriber<CloseableReference<PooledByteBuffer>>() {
            @Override
            public void onNewResultImpl(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
                if (!dataSource.isFinished()) {
                    return;
                }

                CloseableReference<PooledByteBuffer> imageReference = dataSource.getResult();
                if (imageReference != null) {
                    final CloseableReference<PooledByteBuffer> closeableReference = imageReference.clone();
                    try {
                        PooledByteBuffer pooledByteBuffer = closeableReference.get();
                        InputStream inputStream = new PooledByteBufferInputStream(pooledByteBuffer);
                        File photoFile = loadFileResult.getFile();
                        byte[] data = FileUtils.read(inputStream);
                        if (data == null) {
                            loadFileResult.onResult(null);
                            return;
                        }
                        boolean suc = FileUtils.writeBytesToFile(photoFile, data);
                        if (!suc) {
                            loadFileResult.onResult(null);
                            return;
                        }
//                        LogUtils.e("sticker", "downloadImage yes!!!!!!!!!! "+url);
                        loadFileResult.onResult(photoFile);
                    } catch (Exception e) {
                        loadFileResult.onResult(null);
                        e.printStackTrace();
                    } finally {
                        imageReference.close();
                        closeableReference.close();
                    }
                }
            }

            @Override
            public void onProgressUpdate(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
                int progress = (int) (dataSource.getProgress() * 100);
                loadFileResult.onProgress(progress);
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                loadFileResult.onResult(null);
                Throwable throwable = dataSource.getFailureCause();
                if (throwable != null) {
                    Log.e("ImageLoader", "onFailureImpl = " + throwable.toString());
                }
            }
        }, CallerThreadExecutor.getInstance());
    }

}
