package com.dzfd.gids.baselibs.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 压缩工具类
 */
public class CompressUtils {
    /**
     * 降低图像质量，可以达到图片体积减小到500KB以下。
     *
     * @param image
     * @return
     */
    public static Bitmap compressBitmapByLowerQulity(Bitmap image, int maxSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 70, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 70;

        while (baos.toByteArray().length / 1024 > maxSize && options > 10) { // 循环判断如果压缩后图片是否大于500kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        try {
            ByteArrayInputStream isBm = new ByteArrayInputStream(
                    baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inSampleSize = 4;
            Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, bitmapOptions);// 把ByteArrayInputStream数据生成图片
            //noinspection ConstantConditions
            if (!image.isRecycled()) {
                image.recycle();
                image = null;
            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return image;
        }

    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    private static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }


    /**
     * 压缩图片到指定尺寸
     *
     * @param srcPath
     * @param width
     * @param height
     * @return 图像bitmap
     */
    public static Bitmap getCompressedBitmap(String srcPath, float width, float height) {
        LogUtils.d("CompressUtils", "srcPath=" + srcPath);
        int degree = readPictureDegree(srcPath);

        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        // newOpts.inSampleSize = 8;

        Bitmap bitmap = null;// 此时返回bm为空
        BitmapFactory.decodeFile(srcPath, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        LogUtils.d("CompressUtils", "newOpts.outWidth=" + newOpts.outWidth + "  newOpts.outHeight=" + newOpts.outHeight);

        //float hh = width;
        //float ww = height;
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > height) {// 如果宽度大的话根据高度固定大小缩放
            be = (int) (newOpts.outHeight / height);
        } else if (w < h && h > width) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / width);
        }
        if (be <= 0) {
            be = 1;
        }

        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        try {
            bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
            LogUtils.d("CompressUtils", "degree=" + degree);
            if (degree != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(degree);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }

        } catch (Exception e) {
        }
        if (bitmap == null) {
            return null;
        }
        return bitmap;
    }

    /**
     * 压缩图片尺寸到指定大小,并保存到硬盘
     *
     * @param srcPath
     * @param width
     * @param height
     * @return
     */
    public static File getCompressedBitmapFileSyc(String srcPath, float width, float height, String cacheDir) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap ;
        bitmap = BitmapFactory.decodeFile(srcPath, options);
        if (options.outWidth < width && options.outHeight< height){
            if (bitmap != null){
                bitmap.recycle();
                bitmap = null ;
            }
            return new File(srcPath);
        }else {
            // 压缩
            bitmap = getCompressedBitmap(srcPath, width, height);
            File cacheFile = new File(cacheDir);
            if (!cacheFile.exists()) {				//如果不存在，那就建立这个文件夹
                cacheFile.mkdirs();
            }
            File file = new File(cacheFile, System.currentTimeMillis() + ".jpg");// 将要保存图片的路径
            return compressAndSaveBitmap(bitmap, file);
        }
    }

    /**
     * 压缩图片Bitmap到指定的路径
     *
     * @param bitmap
     * @param file
     * @return
     */
    public static File compressAndSaveBitmap(Bitmap bitmap, File file) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            //noinspection ConstantConditions
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 压缩图片Bitmap到指定的路径
     *
     * @param bitmap
     * @param filePath
     * @return
     */
    public static void compressAndSaveBitmap(Bitmap bitmap, String filePath) {
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {

            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        System.gc();
    }

    public static File getCompressedBitmapFileSyc(Bitmap srcBitmap, String imageDir) {
        File file = new File(Environment.getExternalStorageDirectory()
                + imageDir + "/"
                + System.currentTimeMillis() + ".jpg");// 将要保存图片的路径
        // 将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(file));
            srcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 异步获取压缩文件.
     *
     * @param srcBitmap
     * @param compressImageCompleteResult
     */
    public static void getCompressedBitmapFileAsyc(final Bitmap srcBitmap,
                                                   final ICompressImageCompleteResult compressImageCompleteResult,
                                                   final String imageDir) {
        AsyncTask<Object, Object, File> task = new AsyncTask<Object, Object, File>() {
            @Override
            protected File doInBackground(Object... params) {
                //File file = getCompressedBitmapFileSyc(srcBitmap);
                return getCompressedBitmapFileSyc(srcBitmap, imageDir);
            }

            /**
             * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
             * @param result
             */
            @Override
            protected void onPostExecute(File result) {
                compressImageCompleteResult.onResult(result);
            }
        };
        task.execute();
    }

    /**
     * 异步获取一个压缩过的图片文件.
     */
    @SuppressWarnings("unchecked")
    public static void getCompressedBitmapFileAsyc(final String srcPath,
                                                   final float width, final float height,
                                                   final ICompressImageCompleteResult compressImageCompleteResult,
                                                   final String imageDir) {
        AsyncTask<Object, Object, File> task = new AsyncTask<Object, Object, File>() {
            @Override
            protected File doInBackground(Object... params) {
                //File file = getCompressedBitmapFileSyc(srcPath, width, height);
                return getCompressedBitmapFileSyc(srcPath, width, height, imageDir);
            }

            /**
             * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
             * @param result
             */
            @Override
            protected void onPostExecute(File result) {
                compressImageCompleteResult.onResult(result);
            }
        };
        task.execute();
    }

    public static  String encryptBASE64(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        try {
            byte[] encode = str.getBytes("UTF-8");
            // base64 加密
            return new String(Base64.encode(encode, 0, encode.length, Base64.NO_WRAP), "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
    public static  String encryptBASE64(byte[] encode) {
        if (encode == null || encode.length == 0) {
            return null;
        }
        try {
            // base64 加密
            return new String(Base64.encode(encode, 0, encode.length, Base64.NO_WRAP), "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
    public static  byte[] decryptBASE641(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        try {
            byte[] encode = str.getBytes("UTF-8");
            // base64 解密
            return Base64.decode(encode, 0, encode.length, Base64.NO_WRAP);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
    public static  String decryptBASE64(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        try {
            byte[] encode = str.getBytes("UTF-8");
            // base64 解密
            return new String(Base64.decode(encode, 0, encode.length, Base64.NO_WRAP), "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return null;
    }
    /**
     * GZIP 加密
     *
     * @param str
     * @return
     */
    public static  byte[] encryptGZIP(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }

        try {
            // gzip压缩
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(baos);
            gzip.write(str.getBytes("UTF-8"));

            gzip.close();

            byte[] encode = baos.toByteArray();

            baos.flush();
            baos.close();

            // base64 加密
            return encode;
//          return new String(encode, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * GZIP 解密
     *
     * @param str
     * @return
     */
    public static  String decryptGZIP(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        try {

            byte[] decode = str.getBytes("UTF-8");
            return decryptGZIP(decode);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
    public static  String decryptGZIP(byte[] decode) {
        if (decode == null || decode.length == 0) {
            return null;
        }
        int BUFFER_SIZE = 1024;
        try {
            //gzip 解压缩
            ByteArrayInputStream bais = new ByteArrayInputStream(decode);
            GZIPInputStream gzip = new GZIPInputStream(bais);

            byte[] buf = new byte[BUFFER_SIZE];
            int len = 0;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            while((len=gzip.read(buf, 0, BUFFER_SIZE))!=-1){
                baos.write(buf, 0, len);
            }
            gzip.close();
            baos.flush();

            decode = baos.toByteArray();

            baos.close();

            return new String(decode, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");

        if (src == null || src.length <= 0) {
            return null;
        }

        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public interface ICompressImageCompleteResult {
        void onResult(File file);
    }


}
