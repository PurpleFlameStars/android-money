package com.dzfd.gids.baselibs.apk;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.util.DisplayMetrics;

import com.dzfd.gids.baselibs.utils.ContextUtils;
import com.dzfd.gids.baselibs.utils.FileUtils;
import com.dzfd.gids.baselibs.utils.ReflectUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author zhanglong on 2017/6/21.
 */
public class ApkSignHelper {

    public static final String CLASSNAME_PAGEAGEPARSE = "android.content.pm.PackageParser";
    public static final String CLASSNAME_PAGEAGEPARSE_PACKAGE = "android.content.pm.PackageParser$Package";

    public static String getApkSignMd5(String apkFile) {
        // 尽量少调用该函数，签名一般返回是一个数组。
        String[] signatureArray = getApkSignMd5Array(apkFile);
        if (signatureArray != null && signatureArray.length > 0) {
            return signatureArray[0];
        }
        return null;
    }

    public static String[] getApkSignMd5Array(String apkPath) {
        String[] signatureArray = null;
        if (FileUtils.pathFileExist(apkPath)) {
            signatureArray = getApkSignArrayAndPkgName1(apkPath, null, false);
            if (signatureArray == null || signatureArray.length == 0) {
                signatureArray = getApkSignArrayAndPkgName2(apkPath, null, false);
            }
            if (signatureArray == null || signatureArray.length == 0) {
                signatureArray = getApkSignArrayAndPkgName3(apkPath, null, false);
            }
        }
        return signatureArray;
    }


    public static Object parsePackage(String archiveFilePath, int flags) {
        // 这是一个Package 解释器, 是隐藏的
        try {
            Object packageParser;
            // 构造函数的参数只有一个, apk文件的路径
            if (Build.VERSION.SDK_INT >= 21) {//android 5.0系统改方法做了改变
                packageParser = ReflectUtils.getObjectConstructor(CLASSNAME_PAGEAGEPARSE)
                        .newInstance();
            } else {
                packageParser = ReflectUtils.getObjectConstructor(CLASSNAME_PAGEAGEPARSE, String.class)
                        .newInstance(archiveFilePath);
            }
            // 这个是与显示有关的, 里面涉及到一些像素显示等等, 我们使用默认的情况
            DisplayMetrics metrics = new DisplayMetrics();
            metrics.setToDefaults();
            final File sourceFile = new File(archiveFilePath);
            // 这里就是解析了, 四个参数,
            // 源文件File,
            // 目的文件路径(看Android安装器源码, 用的是源文件路径, 但名字却是destFileName)
            // 显示, DisplayMetrics metrics
            // flags, 这个标记类型，比如PackageManager.GET_SIGNATURES表示需要签名信息

            Object pkg;
            if (Build.VERSION.SDK_INT >= 21) {
                Method parsePackageMethod = packageParser.getClass().getMethod("parsePackage", File.class, int.class);
                pkg = parsePackageMethod.invoke(packageParser, sourceFile, flags);
            } else {
                Method parsePackageMethod = packageParser.getClass().getMethod("parsePackage", File.class, String.class, DisplayMetrics.class, int.class);
                pkg = parsePackageMethod.invoke(packageParser, sourceFile, archiveFilePath, metrics, flags);
            }
            if (pkg == null) {
                return null;
            }
            //这里只取出而不校验，如果要校验，第二个参数传0
            if (Build.VERSION.SDK_INT >= 21) {
                Method collectCertificates = packageParser.getClass().getDeclaredMethod("collectCertificates", ReflectUtils.classForName(CLASSNAME_PAGEAGEPARSE_PACKAGE), File.class, int.class);
                collectCertificates.setAccessible(true);
                collectCertificates.invoke(packageParser, pkg, sourceFile, 1);
            } else {
                Method collectCertificates = packageParser.getClass().getDeclaredMethod("collectCertificates", ReflectUtils.classForName(CLASSNAME_PAGEAGEPARSE_PACKAGE), int.class);
                collectCertificates.invoke(packageParser, pkg, 1);
            }
            return pkg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

        // 应用程序信息包, 这个公开的, 不过有些函数, 变量没公开
        // PackageInfo pkgInfo=null;
        // try {
        // pkgInfo=PackageParser.generatePackageInfo(pkg, null, flags,0,0);
        // } catch (Exception e) {
        // // TODO: handle exception
        // Log.e(TAG, "--generatePackageInfo error--");
        // return null;
        // }
        // return pkgInfo;
    }


    public static String[] getApkSignArrayAndPkgName1(String apkPath, String[] packageName, boolean onlyPackageName) {
        try {
            Object info2 = parsePackage(apkPath, onlyPackageName ? 0 : PackageManager.GET_SIGNATURES);
            if (info2 != null) {
                if (packageName != null && packageName.length == 1) {
                    packageName[0] = (String) ReflectUtils.getField(info2, "packageName");
                }
                if (!onlyPackageName) {
                    Signature[] signatureArray = (Signature[]) ReflectUtils.getField(info2, "mSignatures");
                    if (signatureArray != null && signatureArray.length > 0) {
                        String signatureArrarRet[] = new String[signatureArray.length];
                        for (int i = 0; i < signatureArray.length; i++) {
                            if (signatureArray[i] != null) {
                                byte[] byteSignature = signatureArray[i].toByteArray();
                                signatureArrarRet[i] = HashUtils.getHash(Arrays.toString(byteSignature)).toLowerCase();
                            }
                        }
                        return signatureArrarRet;
                    }
                }
                return null;
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }


    public static String[] getApkSignArrayAndPkgName2(String apkPath, String[] packageName, boolean onlyPackageName) {
        try {
            //这种方式在nexus 5 android 5.0上会卡住。
            Object pkg;
            try {
                //package 太大，有可能导致oom
                pkg = parsePackage(apkPath, onlyPackageName ? 0 : PackageManager.GET_SIGNATURES);
            } catch (OutOfMemoryError e) {
                pkg = null;
            }
            if (pkg == null) {
                return null;
            }

            if (packageName != null && packageName.length == 1) {
                packageName[0] = (String) ReflectUtils.getObjectFieldNoDeclared(ReflectUtils.getField(pkg, "applicationInfo"), "packageName");
            }
            if (!onlyPackageName) {
                Signature[] signatureArray = getApkSignature(pkg);
                if (signatureArray != null && signatureArray.length > 0) {
                    String signatureArrarRet[] = new String[signatureArray.length];
                    for (int i = 0; i < signatureArray.length; i++) {
                        if (signatureArray[i] != null) {
                            byte[] byteSignature = signatureArray[i].toByteArray();
                            signatureArrarRet[i] = HashUtils.getHash(Arrays.toString(byteSignature)).toLowerCase();
                        }
                    }
                    return signatureArrarRet;
                }
            }

            return null;

        } catch (Exception e) {
            return null;
        }
    }


    public static String[] getApkSignArrayAndPkgName3(String apkPath, String[] packageNameRet, boolean onlyPackageName) {
        try {
            PackageManager pm = ContextUtils.getApplicationContext().getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(apkPath, onlyPackageName ? 0 : PackageManager.GET_SIGNATURES);
            if (info != null) {
                if (packageNameRet != null && packageNameRet.length == 1) {
                    if (info.applicationInfo != null) {
                        packageNameRet[0] = info.applicationInfo.packageName;
                    }
                }
                if (!onlyPackageName) {
                    if (info.signatures != null && info.signatures.length > 0) {
                        String signatureArrayRet[] = new String[info.signatures.length];
                        for (int i = 0; i < info.signatures.length; i++) {
                            if (info.signatures[i] != null) {
                                byte[] signature = info.signatures[i].toByteArray();
                                signatureArrayRet[i] = HashUtils.getHash(Arrays.toString(signature)).toLowerCase();
                            }
                        }
                        return signatureArrayRet;
                    }
                }
            }
        } catch (Throwable throwable) {
        }
        return null;
    }

    private static Signature[] getApkSignature(Object pkg) {

        Signature[] sigs = new Signature[0];
        try {
            sigs = (Signature[]) ReflectUtils.getField(pkg, "mSignatures");
        } catch (Exception e) {
        }
        if (sigs == null) {
            return null;// 有一些系统应用获取不到签名信息（比如google电子市场），sigs会为空，所以做特别处理，以防报错
        }
        if (sigs.length <= 0) {
            return null;
        }
        return sigs;
    }

}
