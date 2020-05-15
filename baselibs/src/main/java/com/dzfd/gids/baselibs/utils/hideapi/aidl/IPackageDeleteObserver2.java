package com.dzfd.gids.baselibs.utils.hideapi.aidl;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * Created by liujiangming on 15-12-31.
 */
public interface IPackageDeleteObserver2 extends IInterface {
    void onUserActionRequired(Intent var1) throws RemoteException;

    public void onPackageDeleted(String var1, int var2, String var3) throws RemoteException;

    public abstract static class Stub extends Binder implements IPackageDeleteObserver2 {
        private static final String DESCRIPTOR = "android.content.pm.IPackageDeleteObserver2";
        static final int TRANSACTION_onUserActionRequired = 1;
        static final int TRANSACTION_onPackageDeleted = 2;

        public Stub() {
            this.attachInterface(this, "android.content.pm.IPackageDeleteObserver2");
        }

        public static IPackageDeleteObserver2 asInterface(IBinder obj) {
            if(obj == null) {
                return null;
            } else {
                IInterface iin = obj.queryLocalInterface("android.content.pm.IPackageDeleteObserver2");
                return (IPackageDeleteObserver2)(iin != null && iin instanceof IPackageDeleteObserver2?(IPackageDeleteObserver2)iin:new IPackageDeleteObserver2.Stub.Proxy(obj));
            }
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch(code) {
                case 1:
                    data.enforceInterface("android.content.pm.IPackageDeleteObserver2");
                    Intent _arg01;
                    if(0 != data.readInt()) {
                        _arg01 = (Intent) Intent.CREATOR.createFromParcel(data);
                    } else {
                        _arg01 = null;
                    }

                    this.onUserActionRequired(_arg01);
                    return true;
                case 2:
                    data.enforceInterface("android.content.pm.IPackageDeleteObserver2");
                    String _arg0 = data.readString();
                    int _arg1 = data.readInt();
                    String _arg2 = data.readString();
                    this.onPackageDeleted(_arg0, _arg1, _arg2);
                    return true;
                case 1598968902:
                    reply.writeString("android.content.pm.IPackageDeleteObserver2");
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        private static class Proxy implements IPackageDeleteObserver2 {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return "android.content.pm.IPackageDeleteObserver2";
            }

            public void onUserActionRequired(Intent intent) throws RemoteException {
                Parcel _data = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("android.content.pm.IPackageDeleteObserver2");
                    if(intent != null) {
                        _data.writeInt(1);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }

                    this.mRemote.transact(1, _data, (Parcel)null, 1);
                } finally {
                    _data.recycle();
                }

            }

            public void onPackageDeleted(String packageName, int returnCode, String msg) throws RemoteException {
                Parcel _data = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("android.content.pm.IPackageDeleteObserver2");
                    _data.writeString(packageName);
                    _data.writeInt(returnCode);
                    _data.writeString(msg);
                    this.mRemote.transact(2, _data, (Parcel)null, 1);
                } finally {
                    _data.recycle();
                }

            }
        }
    }
}
