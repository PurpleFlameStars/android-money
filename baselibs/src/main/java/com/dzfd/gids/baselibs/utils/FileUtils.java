package com.dzfd.gids.baselibs.utils;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Set;

/**
 * Created by zheng on 2018/12/12.
 */

public class FileUtils {

    private static final String TAG = "FileUtil";
    private static final int DEFAULT_BUFFER_SIZE = 8 * 1024;

    public static boolean makeDir(String dir) {
        if (TextUtils.isEmpty(dir)) {
            return false;
        }

        File f = new File(dir);
        if (!f.exists()) {
            return f.mkdirs();
        }

        return true;
    }

    public static String formatFileSize(long fileS) {// Convert file size
        if (fileS < 0) {
            return "未知大小";
        }
        DecimalFormat df = new DecimalFormat("0.0");
        String fileSizeString;
        if (fileS < 1024) {
            fileSizeString = df.format(Rounding(fileS)) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format(Rounding((double) fileS / 1024)) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format(Rounding((double) fileS / 1048576)) + "M";
        } else {
            fileSizeString = df.format(Rounding((double) fileS / 1073741824)) + "G";
        }
        return fileSizeString;
    }

    private static double Rounding(double d) {
        BigDecimal bd = new BigDecimal(d);
        bd.setScale(1, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    public static boolean IsFileExist(String file) {
        if (TextUtils.isEmpty(file)) {
            return false;
        }
        File f = new File(file);
        return f.exists();
    }

    // storage/emulated/0/360Download/file.txt  ->  file.txt
    public static String getFileName(String path) {
        return new File(path).getName();
    }

    // storage/emulated/0/360Download/file.txt  ->  file
    public static String getFileNameEx(String file) {
        int nPos = file.lastIndexOf(".");
        if (nPos > 0) {
            file = file.substring(0, nPos);
            nPos = file.lastIndexOf("/");
            if (nPos > 0) {
                file = file.substring(nPos + 1);
            }
        }
        return file;
    }

    public static long getFileLen(String file) {
        if (file == null)
            return 0;
        File f = new File(file);
        return f.length();
    }

    // 获得一个目录下 所有文件的大小 包括子目录
    public static long getDirectorySize(String filePath) {
        if (filePath == null)
            return 0;

        File file = new File(filePath);
        if (!file.isDirectory())
            return 0;

        long size = 0;
        File list[] = file.listFiles();
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                if (list[i].isDirectory()) {
                    size += getDirectorySize(list[i].getAbsolutePath());
                } else {
                    size += list[i].length();
                }
            }
        }

        return size;
    }

    // 获得一个目录下 所有文件的个数  不包括子目录
    public static int getDirectoryNum(String filePath) {
        if (filePath == null)
            return 0;

        File file = new File(filePath);
        if (!file.isDirectory())
            return 0;

        int size = 0;
        File list[] = file.listFiles();
        if (list != null) {
            size = list.length;
        }

        return size;
    }

    public static boolean makeSureFileExist(File file) {
        if (file == null) {
            return false;
        }

        if (file.exists()) {
            return true;
        }

        return createNewFile(file);
    }

    private static boolean createNewFile(File file) {
        if (file == null) {
            return false;
        }

        if (file.exists()) {
            return true;
        }

        if (makeSureParentExist(file)) {
            try {
                return file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private static boolean makeSureParentExist(File file) {
        if (file == null) {
            return false;
        }
        File parent = file.getParentFile();
        if (parent == null) {
            return false;
        }
        if (parent.exists()) {
            return true;
        }
        return makeDir(parent.getPath());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static long getAvailableBytesOfSdCard() {
        return getAvailableBytes(new File(SDCardUtils.getSDCardPath()));
    }

    // data/data/com.qihoo.appstore
    public static long getAvailableBytesOfDataDir() {
        return getAvailableBytes(Environment.getDataDirectory());
    }

    public static boolean checkPathWithSize(File root, long needSize) {
        long avilableBytes = getAvailableBytes(root);
        return avilableBytes > needSize;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 是否在目录层级内
     *
     * @param root      根目彿
     * @param path      根目录中的子目录
     * @param hierarchy 层级数，假娀昿1则永远返回true＿
     */
    public static boolean isInHierarchy(File root, File path, int hierarchy) {
        if (hierarchy < 0) {
            return true;
        } else {
            String[] nodes = path.getPath().substring(root.getPath().length()).split(File.separator);
            return nodes.length - 1 <= hierarchy;
        }
    }

    public static boolean moveFile(String srcFileName, String destFileName) {
        File srcFile = new File(srcFileName);
        if (!srcFile.exists() || !srcFile.isFile())
            return false;

        File destFile = new File(destFileName);
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        return srcFile.renameTo(destFile);
    }

    public static File copyFile(String oldPath, String newPath) {

        LogUtils.safeCheck(!TextUtils.isEmpty(oldPath) && !TextUtils.isEmpty(newPath));
        if (TextUtils.isEmpty(oldPath) || TextUtils.isEmpty(newPath)) {
            return null;
        }

        File oldFile = new File(oldPath);
        File newFile = new File(newPath);

        int byteread;

        InputStream in = null;
        FileOutputStream out = null;

        try {

            if (newFile.exists()) {

                String newName = "";
                String suffix = "";

                int pos = newPath.lastIndexOf(".");
                if (pos > 0) {
                    newName = newPath.substring(0, pos);
                    suffix = newPath.substring(pos, newPath.length());
                } else {
                    newName = newPath;
                }

                int index = 0;

                do {
                    index++;
                    newFile = new File(newName + " (" + index + ")" + suffix);
                } while (newFile.exists());

                if (!newFile.getParentFile().exists()) {
                    newFile.getParentFile().mkdirs();
                }

            }

            synchronized (newFile) {
                if (!newFile.getParentFile().exists()) {
                    newFile.getParentFile().mkdirs();
                }
            }

            if (oldFile.exists()) {
                in = new FileInputStream(oldPath);
                out = new FileOutputStream(newFile);
                byte[] buffer = new byte[1024];
                while ((byteread = in.read(buffer)) != -1) {
                    out.write(buffer, 0, byteread);
                }
                in.close();
                newFile.setLastModified(oldFile.lastModified());
            }

            return newFile;

        } catch (Exception e) {
            LogUtils.e("FileUtil", e.toString());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 拷贝文件
     *
     * @param sourceFile 源文仿
     * @param destFile   目标文件
     * @return 是否拷贝成功
     */
    public static boolean copyFile(File sourceFile, File destFile) {
        boolean isCopyOk = false;
        byte[] buffer;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        // 如果此时没有文件夹目录就创建
        String canonicalPath = "";
        try {
            canonicalPath = destFile.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!destFile.exists()) {
            if (canonicalPath.lastIndexOf(File.separator) >= 0) {
                canonicalPath = canonicalPath.substring(0, canonicalPath.lastIndexOf(File.separator));

                isCopyOk = FileUtils.makeDir(canonicalPath);

            }
        }

        if (!isCopyOk) {
            return false;
        }

        try {
            bis = new BufferedInputStream(new FileInputStream(sourceFile), DEFAULT_BUFFER_SIZE);
            bos = new BufferedOutputStream(new FileOutputStream(destFile), DEFAULT_BUFFER_SIZE);
            buffer = new byte[DEFAULT_BUFFER_SIZE];
            int len;
            while ((len = bis.read(buffer, 0, DEFAULT_BUFFER_SIZE)) != -1) {
                bos.write(buffer, 0, len);
            }
            bos.flush();
            isCopyOk = sourceFile.length() == destFile.length();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isCopyOk;
    }

    public static void copyDir(File src, File dst) {
        if (!dst.exists()) {
            dst.mkdirs();
        }
        File[] files = src.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 先判断剩余空间是否不足.如果不足则退出
                    try {
                        StatFs fs = new StatFs(dst.getPath());
                        long freeSize = (long) fs.getBlockSize() * (long) fs.getAvailableBlocks();
                        if (1024 >= freeSize) {
                            throw new RuntimeException("NO_ENOUGH_SPACE");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    copyDir(file, new File(dst, file.getName()));
                } else {
                    try {
                        StatFs fs = new StatFs(dst.getPath());
                        long freeSize = (long) fs.getBlockSize() * (long) fs.getAvailableBlocks();
                        if (file.length() + 1024 >= freeSize) {
                            throw new RuntimeException("NO_ENOUGH_SPACE");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    File destFile = new File(dst, file.getName());
                    if (destFile.exists()) {
                        LogUtils.d(TAG, "file exist");
                    } else {
                        if (file.canRead() && !copyFile(file, destFile)) {
                            throw new RuntimeException("Copy file fail");
                        }
                    }
                }
            }
        }
    }

    /**
     * 拷贝asets下的文件到指定文件
     *
     * @param context
     * @param fileName
     * @param file
     * @return
     */
    public static boolean copyAssetsFile(Context context, String fileName, File file) {
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        if (!parent.exists()) {
            return false;
        }
        File temp = new File(file + ".temp");
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(context.getAssets().open(fileName));
            bos = new BufferedOutputStream(new FileOutputStream(temp));
            byte[] buffer = new byte[8 * 1024];
            int len;
            while ((len = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            bos.flush();
            if (temp.renameTo(file)) {
                return true;
            } else {
                temp.delete();
                file.delete();
            }
        } catch (FileNotFoundException e) {
            if (LogUtils.isDebug()) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            if (LogUtils.isDebug()) {
                e.printStackTrace();
            }
        } finally {
            safeClose(bis);
            safeClose(bos);
        }
        return false;
    }

    /**
     * 安全关闭流对象
     *
     * @param closeable
     */
    private static void safeClose(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            if (LogUtils.isDebug()) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取文件内容到字节数绿
     */
    public static byte[] readFileToBytes(File file) {
        byte[] bytes = null;
        if (file.exists()) {
            byte[] buffer;
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            ByteArrayOutputStream baos = null;
            try {
                bis = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
                baos = new ByteArrayOutputStream();
                bos = new BufferedOutputStream(baos, DEFAULT_BUFFER_SIZE);
                buffer = new byte[DEFAULT_BUFFER_SIZE];
                int len;
                while ((len = bis.read(buffer, 0, DEFAULT_BUFFER_SIZE)) != -1) {
                    bos.write(buffer, 0, len);
                }
                bos.flush();
                bytes = baos.toByteArray();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bos != null) {
                        bos.close();
                    }
                    if (baos != null) {
                        baos.close();
                    }
                    if (bis != null) {
                        bis.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytes;
    }

    public static byte[] readFileToBytes(File file, long offset, long len) {
        byte[] bytes = null;
        if (file.exists() && offset >= 0 && len > offset && offset < file.length()) {
            RandomAccessFile raf = null;
            ByteArrayOutputStream bos = null;
            try {
                raf = new RandomAccessFile(file, "r");
                raf.seek(offset);
                bos = new ByteArrayOutputStream();
                int b;
                long count = 0;
                while ((b = raf.read()) != -1 && count < len) {
                    bos.write(b);
                    count++;
                }
                bos.flush();
                bytes = bos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (raf != null) {
                        raf.close();
                    }
                    if (bos != null) {
                        bos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytes;
    }

    public static boolean writeBytesToFile(File file, byte[] bytes, long offset, int byteCount) {
        boolean isOk = false;
        if (!file.exists()) {
            try {
                isOk = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (file.exists() && bytes != null && offset >= 0) {
            RandomAccessFile raf = null;
            try {
                raf = new RandomAccessFile(file, "rw");
                raf.seek(offset);
                raf.write(bytes, 0, byteCount);
                isOk = true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (raf != null) {
                        raf.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return isOk;
    }

    /**
     * 读取文件内容到字符串
     */
    public static String readFileToString(File file) {
        return readFileToString(file, null);
    }

    /**
     * 读取文件内容到字符串
     */
    public static String readFileToString(File file, String encoding) {
        String result = null;
        if (file.exists()) {
            char[] buffer;
            BufferedReader br = null;
            InputStreamReader isr = null;
            BufferedWriter bw = null;
            StringWriter sw = new StringWriter();
            try {
                isr = encoding == null ? new InputStreamReader(new FileInputStream(file)) : new InputStreamReader(new FileInputStream(file), encoding);
                br = new BufferedReader(isr);
                bw = new BufferedWriter(sw);
                buffer = new char[DEFAULT_BUFFER_SIZE];
                int len;
                while ((len = br.read(buffer, 0, DEFAULT_BUFFER_SIZE)) != -1) {
                    bw.write(buffer, 0, len);
                }
                bw.flush();
                result = sw.toString();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bw != null) {
                        bw.close();
                    }
                    if (br != null) {
                        br.close();
                    }
                    if (isr != null) {
                        isr.close();
                    }
                    sw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 写字符串到文件，文件父目录娀果不存在，会自动创建
     */
    public static boolean writeStringToFile(File file, String content) {
        return writeStringToFile(file, content, false);
    }

    /**
     * 写字符串到文件，文件父目录娀果不存在，会自动创建
     */
    public static boolean writeStringToFile(File file, String content, boolean isAppend) {
        boolean isWriteOk = false;
        char[] buffer;
        int count = 0;
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            if (!file.exists()) {
                createNewFileAndParentDir(file);
            }
            if (file.exists()) {
                br = new BufferedReader(new StringReader(content));
                bw = new BufferedWriter(new FileWriter(file, isAppend));
                buffer = new char[DEFAULT_BUFFER_SIZE];
                int len;
                while ((len = br.read(buffer, 0, DEFAULT_BUFFER_SIZE)) != -1) {
                    bw.write(buffer, 0, len);
                    count += len;
                }
                bw.flush();
            }
            isWriteOk = content.length() == count;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isWriteOk;
    }

    /**
     * 写字节数组到文件，文件父目录如果不存在，会自动创廿
     */
    public static boolean writeBytesToFile(File file, byte[] bytes) {
        return writeBytesToFile(file, bytes, false);
    }

    /**
     * 写字节数组到文件，文件父目录如果不存在，会自动创廿
     */
    public static boolean writeBytesToFile(File file, byte[] bytes, boolean isAppend) {
        boolean isWriteOk = false;
        byte[] buffer;
        int count = 0;
        ByteArrayInputStream bais = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            if (!file.exists()) {
                createNewFileAndParentDir(file);
            }
            if (file.exists()) {
                bos = new BufferedOutputStream(new FileOutputStream(file, isAppend), DEFAULT_BUFFER_SIZE);
                bais = new ByteArrayInputStream(bytes);
                bis = new BufferedInputStream(bais, DEFAULT_BUFFER_SIZE);
                buffer = new byte[DEFAULT_BUFFER_SIZE];
                int len;
                while ((len = bis.read(buffer, 0, DEFAULT_BUFFER_SIZE)) != -1) {
                    bos.write(buffer, 0, len);
                    count += len;
                }
                bos.flush();
            }
            isWriteOk = bytes.length == count;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (bis != null) {
                    bis.close();
                }
                if (bais != null) {
                    bais.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isWriteOk;
    }

    /**
     * 创建文件父目彿
     */
    public static boolean createParentDir(File file) {
        boolean isMkdirs = true;
        if (!file.exists()) {
            File dir = file.getParentFile();
            if (!dir.exists()) {
                isMkdirs = dir.mkdirs();
            }
        }
        return isMkdirs;
    }

    /**
     * 创建文件及其父目彿
     */
    public static boolean createNewFileAndParentDir(File file) {
        boolean isCreateNewFileOk;
        isCreateNewFileOk = createParentDir(file);
        // 创建父目录失败，直接返回false，不再创建子文件
        if (isCreateNewFileOk) {
            if (!file.exists()) {
                try {
                    isCreateNewFileOk = file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    isCreateNewFileOk = false;
                }
            }
        }
        return isCreateNewFileOk;
    }


    /**
     * 根据文件名称获取文件的后缿͗符串
     *
     * @param filename 文件的名秿可能带路徿
     * @return 文件的后缿͗符串
     */
    public static String getFileExtension(String filename) {
        if (!TextUtils.isEmpty(filename)) {
            int dotPos = filename.lastIndexOf('.');
            if (0 <= dotPos) {
                return filename.substring(dotPos + 1);
            }
        }
        return "";
    }

    // /a/b/cccc/t.txt  => /a/b/cccc/t
    public static String getPathExcludeExtension(String filename) {
        if (!TextUtils.isEmpty(filename)) {
            int dotPos = filename.lastIndexOf('.');
            if (0 <= dotPos) {
                return filename.substring(0, dotPos);
            } else {
                return filename;
            }
        }
        return "";
    }

    public static String modifyFileExtension(String fileName, String newExtension) {
        if (TextUtils.isEmpty(fileName)) {
            return fileName;
        }
        File oldFile = new File(fileName);
        if (oldFile == null || !oldFile.exists()) {
            return fileName;
        }
        int nPos1 = fileName.lastIndexOf(".");
        String newFileName = "";
        if (nPos1 > 0) {
            newFileName = fileName.substring(0, nPos1) + newExtension;
        } else {
            newFileName = fileName + newExtension;
        }
        oldFile.renameTo(new File(newFileName));
        LogUtils.d(TAG, "modifyFileExtension() called with: fileName = [" + fileName + "], newFileName = [" + newFileName + "]");
        return newFileName;
    }

    /**
     * 根据路径删除指定的目录或文件，无论存在与吿
     *
     * @param sPath 要删除的目录或文仿
     * @return 删除成功返回 true，否则返囿false〿
     */
    public static boolean deleteFileOrDirectory(String sPath) {
        File file = new File(sPath);
        // 判断目录或文件是否存圿
        if (!file.exists()) { // 不存在返囿false
            return false;
        } else {
            // 判断是否为文仿
            if (file.isFile()) { // 为文件时调用删除文件方法
                return deleteFile(sPath);
            } else { // 为目录时调用删除目录方法
                return deleteDirectory(sPath);
            }
        }
    }

    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件吿
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String sPath) {
        if (TextUtils.isEmpty(sPath)) {
            return false;
        }
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            flag = file.delete();
        }
        return flag;
    }

    public static boolean deleteDirectory(String sPath) {
        return deleteDirectoryImp(sPath, null);
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @return 目录删除成功返回true，否则返回false
     */

    public interface IDeleteFileFilter {
        boolean isDelete(File file);
    }

    public static boolean deleteDirectoryImp(String sPath, IDeleteFileFilter deleteFileFilter) {
        // 如果sPath不以文件分隔符结尾，自动添加文件分隔笿
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        // 如果dir对应的文件不存在，或者不是一个目录，则ꀥǿ
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹下的所有文仿包括子目彿
        File[] files = dirFile.listFiles();
        if (files == null) {
            return false;
        }
        for (int i = 0; i < files.length; i++) {
            // 删除子文仿
            if (files[i].isFile()) {
                if (deleteFileFilter == null || deleteFileFilter.isDelete(files[i])) {
                    flag = deleteFile(files[i].getAbsolutePath());
                    if (!flag) {
                        break;
                    }
                }
            } // 删除子目彿
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag) {
            return false;
        }
        // 删除当前目录
        return dirFile.delete();
    }

    public static boolean renameFile(String newFileName, String oldFileName) {
        File oldfile = new File(oldFileName);
        File newfile = new File(newFileName);
        if (!oldfile.exists()) {
            return false;
        }
        boolean renameResult = oldfile.renameTo(newfile);
        oldfile.delete();
        return renameResult;
    }

    public static String getUniqueFilename(String rootPath, String filename, String extension) {
        String ext = null;
        String name = null;
        try {
            if (extension == null) {
                ext = filename.substring(filename.lastIndexOf("."), filename.length());
                name = filename.substring(0, filename.lastIndexOf("."));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(ext)) {
            name = filename;
            ext = extension;
        }
        String fullFilename = rootPath + "/" + name + ext;
        if (!new File(fullFilename).exists()) {
            return fullFilename;
        }

        for (int magnitude = 1; magnitude < 1000000000; magnitude++) {
            fullFilename = rootPath + "/" + name + " (" + magnitude + ")" + ext;
            if (!new File(fullFilename).exists()) {
                return fullFilename;
            }
        }

        return null;
    }


    public static boolean pathFileExist(String sPath) {
        if (TextUtils.isEmpty(sPath)) {
            return false;
        }
        File dirFile = new File(sPath);
        return dirFile != null && dirFile.exists();
    }

    public static void flushQuietly(Flushable in) {
        if (in != null) {
            try {
                in.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeQuietly(Closeable in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void enumFilesUnderDir(String dir, Set<String> setFiles) {
        if (setFiles == null) {
            throw new RuntimeException("");
        }

        File directory = new File(dir);
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        enumFilesUnderDir(dir, setFiles);
                    } else if (file.isFile()) {
                        setFiles.add(file.getAbsolutePath());
                    }
                }
            }
        }
    }

    public static long getFileModifyTime(String file) {
        if (TextUtils.isEmpty(file)) {
            return 0;
        }

        File f = new File(file);
        if (f.exists() && f.isFile()) {
            return f.lastModified();
        }
        return 0;
    }

    public static String getExceptionWhenCreate(String file) {
        try {
            new File(file).createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
            return "exception: " + e1.getMessage() + " " + e1.getCause() + " " + FileUtils.pathFileExist(file);
        } catch (Throwable e1) {
            return "throwable: " + e1.getMessage() + " " + e1.getCause() + " " + FileUtils.pathFileExist(file);
        }
        return "";
    }

    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            //file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }

    public static String getFileAbsolutePath(Activity context, Uri fileUri) {
        if (context == null || fileUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, fileUri)) {
            if (isExternalStorageDocument(fileUri)) {
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(fileUri)) {
                String id = DocumentsContract.getDocumentId(fileUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(fileUri)) {
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(fileUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(fileUri))
                return fileUri.getLastPathSegment();
            return getDataColumn(context, fileUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(fileUri.getScheme())) {
            return fileUri.getPath();
        }
        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(index);
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String readAssertResource(Context context, String strAssertFileName) {
        AssetManager assetManager = context.getAssets();
        String strResponse = "";
        try {
            InputStream ims = assetManager.open(strAssertFileName);
            strResponse = getStringFromInputStream(ims);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strResponse;
    }

    private static String getStringFromInputStream(InputStream a_is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(a_is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
        return sb.toString();
    }


    public static String LoadJsonFromFile(String Filepath) {
        String strJson = "";
        File file  = new File(Filepath);
        if (file == null || !file.exists()) {
            return strJson;
        }
        try {
            FileInputStream inStream = new FileInputStream(file);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length ;
            while ((length = inStream.read(buffer)) != -1) {
                stream.write(buffer, 0, length);
            }
            stream.close();
            inStream.close();
            strJson = stream.toString();
        } catch (FileNotFoundException e) {
            return "";
        } catch (IOException e) {
            return "";
        }
        return strJson;
    }
    public static void SaveJsonToFile(String filename, String filecontent)  {

        //这里就不要用openFileOutput了,那个是往手机内存中写数据的
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(filename);
            if(output!=null){
                output.write(filecontent.getBytes());
                //将String字符串以字节流的形式写入到输出流中
                output.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String LoadJsonFromAsset(Context context, String Filepath) {
        String strJson;
        InputStream is ;
        try {
            is = context.getAssets().open(Filepath);
            int len=is.available();
            byte[] buffer = new byte[len];
            is.read(buffer);
            is.close();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            stream.write(buffer,0,len);
            strJson=stream.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return strJson;
    }
    public static File getExistDir(File file) {
        if (file != null) {
            while (true) {
                if (file.isDirectory() && file.exists()) {
                    break;
                } else {
                    file = file.getParentFile();
                    if (file == null) { //  getExistDir(new File("360Download/a"));
                        break;
                    }
                }
            }
        }
        return file;
    }
    public static long getAvailableBytes(File root) {
        if (root == null)
            return 0;

        root = getExistDir(root);

        if (root.exists()) {
            StatFs stat;
            try {
                stat = new StatFs(root.getPath());
            } catch (IllegalArgumentException e) {
//                java.lang.IllegalArgumentException
//                at android.os.StatFs.native_setup(Native Method)
//                at android.os.StatFs.<init>(StatFs.java:32)
//                at com.qihoo.utils.FileUtils.getAvailableBytes(AppStore:309)
//                at com.qihoo.utils.FileUtils.checkPathWithSize(AppStore:318)
//                at com.qihoo.downloadmanager.CheckDownloadCondition.checkDisk(AppStore:104)
//                at com.qihoo.appstore.download.DownloadSuccessAction.doApkInstall(AppStore:229)
//                at com.qihoo.appstore.download.DownloadSuccessAction.onDownloadSuccess(AppStore:104)
//                at com.qihoo.downloadservice.DownloadServiceDelegate.onNotifyDownloadInfo(AppStore:92)
//                at com.android.downloader.core.DownloadServiceListener$1.run(AppStore:36)
//                at android.os.Handler.handleCallback(Handler.java:615)
//                at android.os.Handler.dispatchMessage(Handler.java:92)
//                at android.os.Looper.loop(Looper.java:137)
//                at android.app.ActivityThread.main(ActivityThread.java:4914)
//                at java.lang.reflect.Method.invokeNative(Native Method)
//                at java.lang.reflect.Method.invoke(Method.java:511)
//                at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:808)
//                at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:575)
//                at dalvik.system.NativeStart.main(Native Method)

                return 0;
            }
            long availableBlocks = (long) stat.getAvailableBlocks() - (long) 4;
            return stat.getBlockSize() * availableBlocks;
        } else {
            return 0;
        }
    }
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }


    /**
     * 从输入流读取数据
     * @param inStream
     * @return  byte[]
     * @throws IOException
     * @throws Exception
     */
    public static byte[] read(InputStream inStream){
        if (inStream == null)   return null;
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            outSteam.flush();
        } catch (IOException e) {
            return null;
        } finally {
            safeClose(outSteam);
            safeClose(inStream);
        }
        return outSteam.toByteArray();
    }

}
