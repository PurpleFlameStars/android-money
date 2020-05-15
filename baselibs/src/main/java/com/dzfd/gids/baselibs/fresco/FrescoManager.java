package com.dzfd.gids.baselibs.fresco;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Process;

import com.dzfd.gids.baselibs.network.NetUtils;
import com.dzfd.gids.baselibs.utils.ContextUtils;
import com.dzfd.gids.baselibs.utils.StorageUtils;
import com.dzfd.gids.baselibs.utils.thread.PriorityThreadFactory;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.common.memory.MemoryTrimType;
import com.facebook.common.memory.MemoryTrimmable;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ExecutorSupplier;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.producers.BaseNetworkFetcher;
import com.facebook.imagepipeline.producers.BaseProducerContextCallbacks;
import com.facebook.imagepipeline.producers.Consumer;
import com.facebook.imagepipeline.producers.FetchState;
import com.facebook.imagepipeline.producers.NetworkFetcher;
import com.facebook.imagepipeline.producers.ProducerContext;
import com.facebook.net.FrescoTTNetFetcher;
import com.optimize.statistics.FrescoTraceListener;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.content.ComponentCallbacks2.TRIM_MEMORY_BACKGROUND;
import static android.content.ComponentCallbacks2.TRIM_MEMORY_COMPLETE;
import static android.content.ComponentCallbacks2.TRIM_MEMORY_MODERATE;
import static android.content.ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL;
import static android.content.ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW;
import static android.content.ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE;
import static android.content.ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN;

/**
 * Created by zheng on 2019/4/8.
 */

public class FrescoManager {
    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();//分配的可用内存，Nexus5 Android6.0.1的值是48M
    private static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 4;//使用的缓存数量
    private static final String IMAGE_PIPELINE_CACHE_DIR = "fresco_image_cache";//默认图所放路径的文件夹名
    private static final int MAX_DISK_CACHE_VERYLOW_SIZE = 10 * ByteConstants.MB;//默认图极低磁盘空间缓存的最大值
    private static final int MAX_DISK_CACHE_LOW_SIZE = 30 * ByteConstants.MB;//默认图低磁盘空间缓存的最大值
    private static final int MAX_DISK_CACHE_SIZE = 50 * ByteConstants.MB;//默认图磁盘缓存的最大值

    private static List<MemoryTrimmable> memoryTrimmableList = new ArrayList<>();

    public static void DefaultInitFresco(Context cxt) {
        Fresco.initialize(cxt);
    }

    public static void InitFresco(Context cxt, Application app, int vcode, String chanel, String appname) {
        final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(
                MAX_MEMORY_CACHE_SIZE, // 内存缓存中总图片的最大大小,以字节为单位。
                Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ? 380 : Integer.MAX_VALUE,     // 内存缓存中图片的最大数量。
                MAX_MEMORY_CACHE_SIZE / 4, // 内存缓存中准备清除但尚未被删除的总图片的最大大小,以字节为单位。
                Integer.MAX_VALUE,     // 内存缓存中准备清除的总图片的最大数量。
                MAX_MEMORY_CACHE_SIZE / 8);    // 内存缓存中单个图片的最大大小。

        //修改内存图片缓存数量，空间策略（这个方式有点恶心）
        Supplier<MemoryCacheParams> mSupplierMemoryCacheParams = new Supplier<MemoryCacheParams>() {
            @Override
            public MemoryCacheParams get() {
                return bitmapCacheParams;
            }
        };
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(cxt)
                .setBaseDirectoryPath(getImageCacheDir())//缓存图片基路径
                .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_DIR)//文件夹名
                .setMaxCacheSize(MAX_DISK_CACHE_SIZE)//默认缓存的最大大小。
                .setMaxCacheSizeOnLowDiskSpace(MAX_DISK_CACHE_LOW_SIZE)//缓存的最大大小,使用设备时低磁盘空间。
                .setMaxCacheSizeOnVeryLowDiskSpace(MAX_DISK_CACHE_VERYLOW_SIZE)//缓存的最大大小,当设备极低磁盘空间
                .build();

        MemoryTrimmableRegistry memoryTrimmableRegistry = new MemoryTrimmableRegistry() {
            @Override
            public void registerMemoryTrimmable(MemoryTrimmable memoryTrimmable) {
                memoryTrimmableList.add(memoryTrimmable);
            }

            @Override
            public void unregisterMemoryTrimmable(MemoryTrimmable memoryTrimmable) {
                memoryTrimmableList.remove(memoryTrimmable);
            }
        };

        ThreadFactory threadFactory = new PriorityThreadFactory("FrescoManager", Process.THREAD_PRIORITY_BACKGROUND);

        ImagePipelineConfig.Builder builder = ImagePipelineConfig.newBuilder(cxt)//OkHttpImagePipelineConfigFactory.newBuilder(cxt, LemonHttpClient.getFrescoHttpClient())
                .setProgressiveJpegConfig(new SimpleProgressiveJpegConfig())
                .experiment()
                .setBitmapPrepareToDraw(true, 0, Integer.MAX_VALUE, true)
                .setBitmapMemoryCacheParamsSupplier(mSupplierMemoryCacheParams)//内存缓存配置（一级缓存，已解码的图片）
                .setExecutorSupplier(new DefaultExecutorSupplier(threadFactory))//线程池配置
                .setMainDiskCacheConfig(diskCacheConfig)//磁盘缓存配置（总，三级缓存）
                .setMemoryTrimmableRegistry(memoryTrimmableRegistry) //内存用量的缩减,有时我们可能会想缩小内存用量。比如应用中有其他数据需要占用内存，不得不把图片缓存清除或者减小 或者我们想检查看看手机是否已经内存不够了。
                .setBitmapsConfig(Bitmap.Config.RGB_565)
                .setResizeAndRotateEnabledForNetwork(true)
                .setDownsampleEnabled(true);// 必须配合ResizeOptions一起使用，否则磁盘缓存失效
        if (android.os.Build.VERSION.SDK_INT > 23) {
            Set<RequestListener> listeners = new HashSet<>();
            String appid = "172258";
            String m2 = NetUtils.getIns().getUid();
            String strvanme = NetUtils.getIns().getAppVertion();
            listeners.add(new FrescoTraceListener(cxt, appid, m2, strvanme, chanel));
            builder.setNetworkFetcher(new FrescoTTNetFetcher(app, appid, m2, String.valueOf(vcode), strvanme, chanel, appname))
                    .setRequestListeners(listeners);
        } else {
            builder.setNetworkFetcher(new DefaultHttpUrlConnectionNetworkFetcher(threadFactory));
        }
        ImagePipelineConfig imagePipelineConfig = builder.build();
        ImagePipelineConfig.getDefaultImageRequestConfig().setProgressiveRenderingEnabled(true);
        Fresco.initialize(cxt, imagePipelineConfig);
    }

    public static File getImageCacheDir() {
        return new File(StorageUtils.getCacheDirectory(ContextUtils.getApplicationContext()), "image");
    }

    public static void clearCache() {
        if (!Fresco.hasBeenInitialized()) {
            return;
        }
        Fresco.getImagePipelineFactory().getImagePipeline().clearMemoryCaches();
        Fresco.getImagePipelineFactory().getBitmapCountingMemoryCache().clear();
        Fresco.getImagePipelineFactory().getEncodedCountingMemoryCache().clear();
    }

    //============================================
    public static class DefaultExecutorSupplier implements ExecutorSupplier {
        private final Executor mIoBoundExecutor;
        private final Executor mDecodeExecutor;
        private final Executor mBackgroundExecutor;
        private final Executor mLightWeightBackgroundExecutor;

        public DefaultExecutorSupplier(ThreadFactory threadFactory) {
            mIoBoundExecutor = getThreadPoolExecutor(5, threadFactory);
            mDecodeExecutor = getThreadPoolExecutor(3, threadFactory);
            mBackgroundExecutor = getThreadPoolExecutor(1, threadFactory);
            mLightWeightBackgroundExecutor = getThreadPoolExecutor(1, threadFactory);
        }

        @Override
        public Executor forLocalStorageRead() {
            return mIoBoundExecutor;
        }

        @Override
        public Executor forLocalStorageWrite() {
            return mIoBoundExecutor;
        }

        @Override
        public Executor forDecode() {
            return mDecodeExecutor;
        }

        @Override
        public Executor forBackgroundTasks() {
            return mBackgroundExecutor;
        }

        @Override
        public Executor forLightweightBackgroundTasks() {
            return mLightWeightBackgroundExecutor;
        }
    }

    public static class DefaultHttpUrlConnectionNetworkFetcher extends BaseNetworkFetcher<FetchState> {
        private static final int MAX_REDIRECTS = 2;
        public static final int HTTP_TEMPORARY_REDIRECT = 307;
        public static final int HTTP_PERMANENT_REDIRECT = 308;
        private final ExecutorService mExecutorService;


        public DefaultHttpUrlConnectionNetworkFetcher(ThreadFactory threadFactory) {
            mExecutorService = getThreadPoolExecutor(5, threadFactory);
        }

        @Override
        public FetchState createFetchState(Consumer<EncodedImage> consumer, ProducerContext context) {
            return new FetchState(consumer, context);
        }

        @Override
        public void fetch(final FetchState fetchState, final NetworkFetcher.Callback callback) {
            final Future<?> future = mExecutorService.submit(
                    new Runnable() {
                        @Override
                        public void run() {
                            fetchSync(fetchState, callback);
                        }
                    });
            fetchState.getContext().addCallbacks(
                    new BaseProducerContextCallbacks() {
                        @Override
                        public void onCancellationRequested() {
                            if (future.cancel(false)) {
                                callback.onCancellation();
                            }
                        }
                    });
        }

        void fetchSync(FetchState fetchState, NetworkFetcher.Callback callback) {
            HttpURLConnection connection = null;

            try {
                connection = downloadFrom(fetchState.getUri(), MAX_REDIRECTS);

                if (connection != null) {
                    callback.onResponse(connection.getInputStream(), -1);
                }
            } catch (IOException e) {
                callback.onFailure(e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

        }

        private HttpURLConnection downloadFrom(Uri uri, int maxRedirects) throws IOException {

            final int DEFAULT_TIMEOUT_MS = 15 * 1000;

            HttpURLConnection connection = (HttpURLConnection) new URL(uri.toString()).openConnection();
            connection.setConnectTimeout(DEFAULT_TIMEOUT_MS);
            connection.setReadTimeout(DEFAULT_TIMEOUT_MS);
            int responseCode = connection.getResponseCode();

            if (isHttpSuccess(responseCode)) {
                return connection;

            } else if (isHttpRedirect(responseCode)) {
                String nextUriString = connection.getHeaderField("Location");
                connection.disconnect();

                Uri nextUri = (nextUriString == null) ? null : Uri.parse(nextUriString);
                String originalScheme = uri.getScheme();

                if (maxRedirects > 0 && nextUri != null && !nextUri.getScheme().equals(originalScheme)) {
                    return downloadFrom(nextUri, maxRedirects - 1);
                } else {
                    String message = maxRedirects == 0
                            ? error("UID %s follows too many redirects", uri.toString())
                            : error("UID %s returned %d without a valid redirect", uri.toString(), responseCode);
                    throw new IOException(message);
                }

            } else {
                connection.disconnect();
                throw new IOException(String
                        .format("Image UID %s returned HTTP code %d", uri.toString(), responseCode));
            }
        }

        private static boolean isHttpSuccess(int responseCode) {
            return (responseCode >= HttpURLConnection.HTTP_OK &&
                    responseCode < HttpURLConnection.HTTP_MULT_CHOICE);
        }

        private static boolean isHttpRedirect(int responseCode) {
            switch (responseCode) {
                case HttpURLConnection.HTTP_MULT_CHOICE:
                case HttpURLConnection.HTTP_MOVED_PERM:
                case HttpURLConnection.HTTP_MOVED_TEMP:
                case HttpURLConnection.HTTP_SEE_OTHER:
                case HTTP_TEMPORARY_REDIRECT:
                case HTTP_PERMANENT_REDIRECT:
                    return true;
                default:
                    return false;
            }
        }

        private static String error(String format, Object... args) {
            return String.format(Locale.getDefault(), format, args);
        }
    }

    private static ThreadPoolExecutor getThreadPoolExecutor(int threadCount, ThreadFactory threadFactory) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                threadCount,
                threadCount,
                35 * 1000L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                threadFactory);
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    public static void trim(int leve) {
        if (!Fresco.hasBeenInitialized()) {
            return;
        }
        if (memoryTrimmableList != null) {
            for (MemoryTrimmable trimmable : memoryTrimmableList) {
                switch (leve) {
                    case TRIM_MEMORY_RUNNING_MODERATE://应用程序可见-内存较少-5-位于LRU顶部系统即将进入低内存状态
                    case TRIM_MEMORY_RUNNING_LOW: //应用程序可见-内存低-10-位于LRU顶部-设备的可用内存越来越少
                        trimmable.trim(MemoryTrimType.OnSystemLowMemoryWhileAppInForeground);
                        break;
                    case TRIM_MEMORY_RUNNING_CRITICAL://应用程序可见-15-内存紧张-位于LRU顶部-其他进程可能会销毁以获得更多可用内存
                        trimmable.trim(MemoryTrimType.OnSystemMemoryCriticallyLowWhileAppInForeground);
                        break;
                    case TRIM_MEMORY_UI_HIDDEN://应用程序不可见-20-程序处于后台应当释放一些内存
                        trimmable.trim(MemoryTrimType.OnAppBackgrounded);
                        break;
                    case TRIM_MEMORY_BACKGROUND://应用程序不可见-内存低-位于LRU顶部，但是位置在下降-40--设备当前的运行环境内存较少
                    case TRIM_MEMORY_MODERATE://应用程序不可见-内存低-位于LRU中部-60-设备当前的运行环境内存较少
                        trimmable.trim(MemoryTrimType.OnSystemLowMemoryWhileAppInBackground);
                        break;
                    case TRIM_MEMORY_COMPLETE://应用程序不可见-内存低-位于LRU底部-80-应用程序可能会被杀死
                        trimmable.trim(MemoryTrimType.OnCloseToDalvikHeapLimit);
                        clearCache();
                        break;
                }
            }
        }
    }
}
