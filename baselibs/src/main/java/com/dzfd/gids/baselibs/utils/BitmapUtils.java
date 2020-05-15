package com.dzfd.gids.baselibs.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Environment;
import android.view.View;

import com.dzfd.gids.baselibs.utils.FileUtils;
import com.dzfd.gids.baselibs.utils.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Darren on 2019/2/16.
 */

public class BitmapUtils {

    public static Bitmap getOvalBitmap(Bitmap bitmap){

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx){

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
            .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static int getBitmapSize(Bitmap bitmap){
        if(bitmap != null){
            try{
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                    return bitmap.getAllocationByteCount();
                }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1){
                    return bitmap.getByteCount();
                }else{
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            }catch (Exception e){
            }
        }
        return 0;
    }

    public static Bitmap getBitmapFromFile(File dst, int width, int height) {
        if (null != dst && dst.exists()) {
            BitmapFactory.Options opts = null;
            if (width > 0 && height > 0) {
                opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(dst.getPath(), opts);
                final int minSideLength = Math.min(width, height);
                opts.inSampleSize = computeSampleSize(opts, minSideLength, width * height);
                opts.inJustDecodeBounds = false;
                opts.inInputShareable = true;
                opts.inPurgeable = true;
            }
            try {
                return BitmapFactory.decodeFile(dst.getPath(), opts);
            } catch (OutOfMemoryError e) {
                try {
                    return BitmapFactory.decodeFile(dst.getPath(), opts);
                } catch (OutOfMemoryError e2) {
                    e2.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Bitmap drawableToBitamp(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
            return bitmapDrawable.getBitmap();
        } else {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    }

    public static Bitmap getBitmapFromResource(Resources resources, int id, int width, int height) {
        BitmapFactory.Options opts = null;
        if (width > 0 && height > 0) {
            opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(resources, id, opts);
            final int minSideLength = Math.min(width, height);
            opts.inSampleSize = computeSampleSize(opts, minSideLength, width * height);
            opts.inJustDecodeBounds = false;
            opts.inInputShareable = true;
            opts.inPurgeable = true;
        }
        try {
            return BitmapFactory.decodeResource(resources, id, opts);
        } catch (OutOfMemoryError e) {
            try {
                return BitmapFactory.decodeResource(resources, id, opts);
            } catch (OutOfMemoryError e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }


    public static Bitmap getBitmapFromResourceWithHighQuality(Resources resources, int id, int width, int height) {
        try {
            Bitmap b = BitmapFactory.decodeResource(resources, id, new BitmapFactory.Options());
            return b != null ? b : getBitmapFromResource(resources, id, width, height);
        } catch (Throwable e) {
            return getBitmapFromResource(resources, id, width, height);
        }
    }

    public static byte[] bitmapToBytes(final Bitmap bitmap, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] result = null;
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            result = output.toByteArray();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (needRecycle) {
                bitmap.recycle();
            }
        }
        return result;
    }

    private static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    public static int computeSampleSize1(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int roundedSize = 1;
        if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
            int sample1 = options.outWidth / reqWidth;
            int sample2 = options.outHeight / reqHeight;
            roundedSize = sample1 < sample2 ? sample1 : sample2;
        }
        if (roundedSize < 1) {
            roundedSize = 1;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = maxNumOfPixels == -1 ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = minSideLength == -1 ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            return lowerBound;
        }

        if (maxNumOfPixels == -1 && minSideLength == -1) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static boolean savePicToPath(Bitmap b, File path, int quality, Bitmap.CompressFormat format) {
        if (b == null || path == null) {
            return false;
        }
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (!sdCardExist) {
            return false;
        }

        FileUtils.makeDir(path.getParentFile().getPath());


        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            boolean success = b.compress(format, quality, fos);
            fos.flush();
            return success;
        } catch (IOException e) {
            return false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e2) {
                    if (LogUtils.isDebug()) {
                        e2.printStackTrace();
                    }
                }
            }
        }
    }


    public static void savePicToSdCardRootPath(Bitmap b, String fileName, int quality) {
        File sdDir = Environment.getExternalStorageDirectory();
        File path = new File(sdDir.getPath() + "/" + fileName);
        savePicToPath(b, path, quality, Bitmap.CompressFormat.PNG);
    }

    public static Bitmap getBitmapFromView(View view) {
        return getBitmapFromView(view, false);
    }

    private static Bitmap getBitmapFromView(View view, boolean forceHighQuality) {
        try {
            return getBitmapFromView(view, 1, forceHighQuality);
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    private static final int TRY_GET_BITMAP_FROM_VIEW_MAX_REPEAT_TIME = 2;

    private static Bitmap getBitmapFromView(View view, int tryTime, boolean forceHighQuality) {
        boolean willNotCacheDrawingBefore = view.willNotCacheDrawing();
        view.setWillNotCacheDrawing(false);

        int drawingCacheBackgroundColorBefore = view.getDrawingCacheBackgroundColor();
        view.setDrawingCacheBackgroundColor(0);
        int drawingCacheQualityBefore = view.getDrawingCacheQuality();
        if (drawingCacheBackgroundColorBefore != 0) {
            view.destroyDrawingCache();
        }
        if (tryTime > 1) {
            view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        }
        view.buildDrawingCache();
        Bitmap cacheBitmap = view.getDrawingCache();
        if (cacheBitmap == null || cacheBitmap.isRecycled()) {
            view.setDrawingCacheQuality(drawingCacheQualityBefore);
            view.setWillNotCacheDrawing(willNotCacheDrawingBefore);
            view.setDrawingCacheBackgroundColor(drawingCacheBackgroundColorBefore);

            if (tryTime < TRY_GET_BITMAP_FROM_VIEW_MAX_REPEAT_TIME) {
                handleOutOfMemory();
                return getBitmapFromView(view, tryTime + 1, forceHighQuality);
            }
            return null;
        }

        Bitmap bitmap = createBitmap(cacheBitmap, cacheBitmap.getWidth(), cacheBitmap.getHeight(), forceHighQuality || tryTime == 1 ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.ARGB_4444);

        if (bitmap == cacheBitmap) {
            bitmap = createBitmap(cacheBitmap);
        }

        view.destroyDrawingCache();

        view.setDrawingCacheQuality(drawingCacheQualityBefore);
        view.setWillNotCacheDrawing(willNotCacheDrawingBefore);
        view.setDrawingCacheBackgroundColor(drawingCacheBackgroundColorBefore);

        return bitmap;
    }

    /**
     * 用于压缩时旋转图片
     *
     * @throws IOException
     * @throws OutOfMemoryError
     */
    public static Bitmap rotateBitmap(String srcFilePath, Bitmap bitmap) throws IOException, OutOfMemoryError {
        ExifInterface exif = new ExifInterface(srcFilePath);
        float degree = 0F;
        switch (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90F;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180F;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270F;
                break;
            default:
                break;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(degree, bitmap.getWidth(), bitmap.getHeight());
        Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (bitmap != b2) {
            bitmap.recycle();
            bitmap = b2;
        }
        return bitmap;
    }

    private static Bitmap createBitmap(Bitmap src) {
        try {
            return Bitmap.createBitmap(src);
        } catch (OutOfMemoryError e) {
            handleOutOfMemory();
            return Bitmap.createBitmap(src);
        }
    }


    public static Bitmap copyBitmap(Bitmap source) {
        if (source == null) {
            return null;
        }
        try {
            return createBitmap(source, source.getWidth(), source.getHeight(), source.getConfig());
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            handleOutOfMemory();
            return null;
        } catch (Exception e){
            return null;
        }
    }

    private static Bitmap createBitmap(Bitmap source, int width, int height, Bitmap.Config config) {
        try {
            if (config == null) {
                config = Bitmap.Config.ARGB_4444;//不要改成565，要不然一些png图会丢失透明信息。
            }
            Bitmap target = createBitmap(width, height, config);
            target.setDensity(source.getDensity());
            Canvas canvas = new Canvas(target);
            Paint paint = new Paint();
            paint.setDither(true);
            paint.setAntiAlias(true);
            Rect src = new Rect(0, 0, source.getWidth(), source.getHeight());
            Rect dst = new Rect(0, 0, width, height);
            canvas.drawBitmap(source, src, dst, paint);
            return target;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return source;
        }
    }

    private static Bitmap createBitmap(int width, int height, Bitmap.Config config) {
        try {
            return Bitmap.createBitmap(width, height, config);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            handleOutOfMemory();
            return Bitmap.createBitmap(width, height, config);
        }
    }

    public static Bitmap createVideoThumbnail(String url, int timeMills) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(url, new HashMap<String, String>());
            bitmap = retriever.getFrameAtTime(timeMills, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
            }
        }
        return bitmap;
    }

    public static Bitmap createLocalVideoThumbnail(String path) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(path);
            bitmap = retriever.getFrameAtTime();
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
            }
        }
        return bitmap;
    }

    private static void handleOutOfMemory() {
        System.gc();
    }

    /**
     * @param bitmap     原图
     * @param edgeLength 希望得到的正方形部分的边长
     * @return 缩放截取正中部分后的位图。
     */
    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength) {
        if (null == bitmap || edgeLength <= 0) {
            return null;
        }

        Bitmap result = bitmap;
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();

        if (widthOrg >= edgeLength && heightOrg >= edgeLength) {
            //压缩到一个最小长度是edgeLength的bitmap
            int longerEdge = (edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
            int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
            int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
            Bitmap scaledBitmap;
            try {
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
            } catch (Exception e) {
                return null;
            }

            //从图中截取正中间的正方形部分。
            int xTopLeft = (scaledWidth - edgeLength) / 2;
            int yTopLeft = (scaledHeight - edgeLength) / 2;

            try {
                result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
                scaledBitmap.recycle();
            } catch (Exception e) {
                return null;
            }
        }
        return result;
    }

}
