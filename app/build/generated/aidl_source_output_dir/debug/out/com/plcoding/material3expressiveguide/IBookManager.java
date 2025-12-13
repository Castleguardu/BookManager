/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Using: C:\Users\wen\AppData\Local\Android\Sdk\build-tools\35.0.0\aidl.exe -pC:\Users\wen\AppData\Local\Android\Sdk\platforms\android-36\framework.aidl -oE:\androidProjectAll\Material3ExpressiveGuide-master\Material3ExpressiveGuide-master\app\build\generated\aidl_source_output_dir\debug\out -IE:\androidProjectAll\Material3ExpressiveGuide-master\Material3ExpressiveGuide-master\app\src\main\aidl -IE:\androidProjectAll\Material3ExpressiveGuide-master\Material3ExpressiveGuide-master\app\src\debug\aidl -IC:\Users\wen\.gradle\caches\8.13\transforms\50dc1e789c9c155c182fcb92a40163f2\transformed\core-1.17.0\aidl -IC:\Users\wen\.gradle\caches\8.13\transforms\14854b126c0729a7482532651a76d4a7\transformed\versionedparcelable-1.1.1\aidl -dC:\Users\wen\AppData\Local\Temp\aidl8867652556263435973.d E:\androidProjectAll\Material3ExpressiveGuide-master\Material3ExpressiveGuide-master\app\src\main\aidl\com\plcoding\material3expressiveguide\IBookManager.aidl
 */
package com.plcoding.material3expressiveguide;
public interface IBookManager extends android.os.IInterface
{
  /** Default implementation for IBookManager. */
  public static class Default implements com.plcoding.material3expressiveguide.IBookManager
  {
    @Override public java.util.List<com.plcoding.material3expressiveguide.data.Book> getBookList() throws android.os.RemoteException
    {
      return null;
    }
    @Override public void addBook(com.plcoding.material3expressiveguide.data.Book book) throws android.os.RemoteException
    {
    }
    @Override public void deleteBook(com.plcoding.material3expressiveguide.data.Book book) throws android.os.RemoteException
    {
    }
    @Override public void registerListener(com.plcoding.material3expressiveguide.INewBookArrivedListener listener) throws android.os.RemoteException
    {
    }
    @Override public void unregisterListener(com.plcoding.material3expressiveguide.INewBookArrivedListener listener) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements com.plcoding.material3expressiveguide.IBookManager
  {
    /** Construct the stub at attach it to the interface. */
    @SuppressWarnings("this-escape")
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.plcoding.material3expressiveguide.IBookManager interface,
     * generating a proxy if needed.
     */
    public static com.plcoding.material3expressiveguide.IBookManager asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof com.plcoding.material3expressiveguide.IBookManager))) {
        return ((com.plcoding.material3expressiveguide.IBookManager)iin);
      }
      return new com.plcoding.material3expressiveguide.IBookManager.Stub.Proxy(obj);
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
        case TRANSACTION_getBookList:
        {
          java.util.List<com.plcoding.material3expressiveguide.data.Book> _result = this.getBookList();
          reply.writeNoException();
          _Parcel.writeTypedList(reply, _result, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_addBook:
        {
          com.plcoding.material3expressiveguide.data.Book _arg0;
          _arg0 = _Parcel.readTypedObject(data, com.plcoding.material3expressiveguide.data.Book.CREATOR);
          this.addBook(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_deleteBook:
        {
          com.plcoding.material3expressiveguide.data.Book _arg0;
          _arg0 = _Parcel.readTypedObject(data, com.plcoding.material3expressiveguide.data.Book.CREATOR);
          this.deleteBook(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_registerListener:
        {
          com.plcoding.material3expressiveguide.INewBookArrivedListener _arg0;
          _arg0 = com.plcoding.material3expressiveguide.INewBookArrivedListener.Stub.asInterface(data.readStrongBinder());
          this.registerListener(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_unregisterListener:
        {
          com.plcoding.material3expressiveguide.INewBookArrivedListener _arg0;
          _arg0 = com.plcoding.material3expressiveguide.INewBookArrivedListener.Stub.asInterface(data.readStrongBinder());
          this.unregisterListener(_arg0);
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
    private static class Proxy implements com.plcoding.material3expressiveguide.IBookManager
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
      @Override public java.util.List<com.plcoding.material3expressiveguide.data.Book> getBookList() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<com.plcoding.material3expressiveguide.data.Book> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getBookList, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createTypedArrayList(com.plcoding.material3expressiveguide.data.Book.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void addBook(com.plcoding.material3expressiveguide.data.Book book) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _Parcel.writeTypedObject(_data, book, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_addBook, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void deleteBook(com.plcoding.material3expressiveguide.data.Book book) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _Parcel.writeTypedObject(_data, book, 0);
          boolean _status = mRemote.transact(Stub.TRANSACTION_deleteBook, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void registerListener(com.plcoding.material3expressiveguide.INewBookArrivedListener listener) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongInterface(listener);
          boolean _status = mRemote.transact(Stub.TRANSACTION_registerListener, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void unregisterListener(com.plcoding.material3expressiveguide.INewBookArrivedListener listener) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongInterface(listener);
          boolean _status = mRemote.transact(Stub.TRANSACTION_unregisterListener, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
    }
    static final int TRANSACTION_getBookList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_addBook = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_deleteBook = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_registerListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_unregisterListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
  }
  /** @hide */
  public static final java.lang.String DESCRIPTOR = "com.plcoding.material3expressiveguide.IBookManager";
  public java.util.List<com.plcoding.material3expressiveguide.data.Book> getBookList() throws android.os.RemoteException;
  public void addBook(com.plcoding.material3expressiveguide.data.Book book) throws android.os.RemoteException;
  public void deleteBook(com.plcoding.material3expressiveguide.data.Book book) throws android.os.RemoteException;
  public void registerListener(com.plcoding.material3expressiveguide.INewBookArrivedListener listener) throws android.os.RemoteException;
  public void unregisterListener(com.plcoding.material3expressiveguide.INewBookArrivedListener listener) throws android.os.RemoteException;
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
    static private <T extends android.os.Parcelable> void writeTypedList(
        android.os.Parcel parcel, java.util.List<T> value, int parcelableFlags) {
      if (value == null) {
        parcel.writeInt(-1);
      } else {
        int N = value.size();
        int i = 0;
        parcel.writeInt(N);
        while (i < N) {
    writeTypedObject(parcel, value.get(i), parcelableFlags);
          i++;
        }
      }
    }
  }
}
