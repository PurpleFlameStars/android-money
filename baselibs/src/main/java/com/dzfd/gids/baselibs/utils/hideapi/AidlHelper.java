package com.dzfd.gids.baselibs.utils.hideapi;

import android.content.pm.PackageStats;

import com.dzfd.gids.baselibs.utils.hideapi.aidl.IPackageDataObserver;
import com.dzfd.gids.baselibs.utils.hideapi.aidl.IPackageInstallObserver;
import com.dzfd.gids.baselibs.utils.hideapi.aidl.IPackageStatsObserver;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by zhangxun-xy on 4/8/15.
 */
public class AidlHelper {

    public static Object wrapIPackageInstallObserverStub(final IPackageInstallObserver.Stub stub) {
        try {
            Class clazz = Class.forName("android.content.pm.IPackageInstallObserver$Stub");
            return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if ("packageInstalled".equals(method.getName())) {
                        stub.packageInstalled((String) args[0], (Integer) args[1]);
                        return null;
                    } else if ("asBinder".equals(method.getName())) {
                        return stub.asBinder();
                    } else {
                        return method.invoke(stub, args);
                    }
                }
            });
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object wrapIPackageStatsObserverStub(final IPackageStatsObserver.Stub stub) {
        try {
            Class clazz = Class.forName("android.content.pm.IPackageStatsObserver$Stub");
            return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if ("onGetStatsCompleted".equals(method.getName())) {
                        stub.onGetStatsCompleted((PackageStats) args[0], (Boolean) args[1]);
                        return null;
                    } else if ("asBinder".equals(method.getName())) {
                        return stub.asBinder();
                    } else {
                        return method.invoke(stub, args);
                    }
                }
            });
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object wrapIPackageDataObserverStub(final IPackageDataObserver.Stub stub) {
        try {
            Class clazz = Class.forName("android.content.pm.IPackageDataObserver$Stub");
            return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if ("onRemoveCompleted".equals(method.getName())) {
                        stub.onRemoveCompleted((String) args[0], (Boolean) args[1]);
                        return null;
                    } else if ("asBinder".equals(method.getName())) {
                        return stub.asBinder();
                    } else {
                        return method.invoke(stub, args);
                    }
                }
            });
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
