/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Using: C:\Users\wen\AppData\Local\Android\Sdk\build-tools\35.0.0\aidl.exe -pC:\Users\wen\AppData\Local\Android\Sdk\platforms\android-36\framework.aidl -oE:\androidProjectAll\Material3ExpressiveGuide-master\Material3ExpressiveGuide-master\app\build\generated\aidl_source_output_dir\debug\out -IE:\androidProjectAll\Material3ExpressiveGuide-master\Material3ExpressiveGuide-master\app\src\main\aidl -IE:\androidProjectAll\Material3ExpressiveGuide-master\Material3ExpressiveGuide-master\app\src\debug\aidl -IC:\Users\wen\.gradle\caches\8.13\transforms\50dc1e789c9c155c182fcb92a40163f2\transformed\core-1.17.0\aidl -IC:\Users\wen\.gradle\caches\8.13\transforms\14854b126c0729a7482532651a76d4a7\transformed\versionedparcelable-1.1.1\aidl -dC:\Users\wen\AppData\Local\Temp\aidl6642673271824859922.d E:\androidProjectAll\Material3ExpressiveGuide-master\Material3ExpressiveGuide-master\app\src\main\aidl\com\plcoding\material3expressiveguide\INewBookArrivedListener.aidl
 */
package com.plcoding.material3expressiveguide;
public interface INewBookArrivedListener extends android.os.IInterface
{
  /** Default implementation for INewBookArrivedListener. */
  public static class Default implements com.plcoding.material3expressiveguide.INewBookArrivedListener
  {
    @Override public void onNewBookArrived(com.plcoding.material3expressiveguide.data.Book newBook) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements com.plcoding.material3expressiveguide.INewBookArrivedListener
  {
    /** Construct the stub at attach it to the interface. */
    @SuppressWarnings("this-escape")
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.plcoding.material3expressiveguide.INewBookArrivedListener interface,
     * generating a proxy if needed.
     */
    public static com.plcoding.material3expressiveguide.INewBookArrivedListener asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof com.plcoding.material3expressiveguide.INewBookArrivedListener))) {
        return ((com.plcoding.material3expressiveguide.INewBookArrivedListener)iin);
      }
      return new com.plcoding.material3expressiveguide.INewBookArrivedListener.Stub.Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      java.lang.String descriptor = DESCRIPTOR;
      if (code >= android.os.IBinder.FIRST_CALL_TRANSACTION && code <= android.os.IBinder.LAST_CALL_TRANSACTION) {
        data.enforceInterface(descriptor);
      }
      if (code == INTERFACE_TRANSACTION) {
        reply.writeString(descriptor);
        return true;
      }
      switch (code)
      {
        case TRANSACTION_onNewBookArrived:
        {
          com.plcoding.material3expressiveguide.data.Book _arg0;
          _arg0 = _Parcel.readTypedObject(data, com.plcoding.material3expressiveguide.data.Book.CREATOR);
          this.onNewBookArrived(_arg0);
          reply.writeNoException();
          break;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
      return true;
    }
    private static class Proxy implements com.plcoding.material3expressiveguide.INewBookArrivedListener
    {
      private android.os.IBinder mRemote;
      Proxy(android.os.IBinder remote)
      {
        mRemote = remote;
      }
      @Override public android.os.IBinder asBinder()
      {
        return mRemote;
      }
      public java.lang.String getInterfaceDescriptor()
      {
        return DESCRIPTOR;
      }
      @Override public void onNewBookArrived(com.plcoding.material3expressiveguide.data.Book newBook) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _Parcel.writeTypedObject(_data, newBook, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onNewBookArrived, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
    }
    static final int TRANSACTION_onNewBookArrived = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
  }
  /** @hide */
  public static final java.lang.String DESCRIPTOR = "com.plcoding.material3expressiveguide.INewBookArrivedListener";
  public void onNewBookArrived(com.plcoding.material3expressiveguide.data.Book newBook) throws android.os.RemoteException;
  /** @hide */
  static class _Parcel {
    static private <T> T readTypedObject(
        android.os.Parcel parcel,
        android.os.Parcelable.Creator<T> c) {
      if (parcel.readInt() != 0) {
          return c.createFromParcel(parcel);
      } else {
          return null;
      }
    }
    static private <T extends android.os.Parcelable> void writeTypedObject(
        android.os.Parcel parcel, T value, int parcelableFlags) {
      if (value != null) {
        parcel.writeInt(1);
        value.writeToParcel(parcel, parcelableFlags);
      } else {
        parcel.writeInt(0);
      }
    }
  }
}
