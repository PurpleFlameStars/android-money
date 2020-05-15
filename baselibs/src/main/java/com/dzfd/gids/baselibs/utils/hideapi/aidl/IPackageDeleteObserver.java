package com.dzfd.gids.baselibs.utils.hideapi.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * Created by liujiangming on 15-12-31.
 */
public interface IPackageDeleteObserver extends IInterface {
    void packageDeleted(String var1, int var2) throws RemoteException;

    public abstract static class Stub extends Binder implements IPackageDeleteObserver {
        private static final String DESCRIPTOR = "android.content.pm.IPackageDeleteObserver";
        static final int TRANSACTION_packageDeleted = 1;

        public Stub() {
            this.attachInterface(this, "android.content.pm.IPackageDeleteObserver");
        }

        public static IPackageDeleteObserver asInterface(IBinder obj) {
            if(obj == null) {
                return null;
            } else {
                IInterface iin = obj.queryLocalInterface("android.content.pm.IPackageDeleteObserver");
                return (IPackageDeleteObserver)(iin != null && iin instanceof IPackageDeleteObserver?(IPackageDeleteObserver)iin:new IPackageDeleteObserver.Stub.Proxy(obj));
            }
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch(code) {
                case 1:
                    data.enforceInterface("android.content.pm.IPackageDeleteObserver");
                    String _arg0 = data.readString();
                    int _arg1 = data.readInt();
                    this.packageDeleted(_arg0, _arg1);
                    return true;
                case 1598968902:
                    reply.writeString("android.content.pm.IPackageDeleteObserver");
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        private static class Proxy implements IPackageDeleteObserver {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return "android.content.pm.IPackageDeleteObserver";
            }

            public void packageDeleted(String packageName, int returnCode) throws RemoteException {
                Parcel _data = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("android.content.pm.IPackageDeleteObserver");
                    _data.writeString(packageName);
                    _data.writeInt(returnCode);
                    this.mRemote.transact(1, _data, (Parcel)null, 1);
                } finally {
                    _data.recycle();
                }

            }
        }
    }
}
