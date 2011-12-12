/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\hate\\src\\uk\\ac\\ed\\timetable\\reminder\\IAlarmService.aidl
 */
package uk.ac.ed.timetable.reminder;
public interface IAlarmService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements uk.ac.ed.timetable.reminder.IAlarmService
{
private static final java.lang.String DESCRIPTOR = "uk.ac.ed.timetable.reminder.IAlarmService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an uk.ac.ed.timetable.reminder.IAlarmService interface,
 * generating a proxy if needed.
 */
public static uk.ac.ed.timetable.reminder.IAlarmService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof uk.ac.ed.timetable.reminder.IAlarmService))) {
return ((uk.ac.ed.timetable.reminder.IAlarmService)iin);
}
return new uk.ac.ed.timetable.reminder.IAlarmService.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getPid:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getPid();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements uk.ac.ed.timetable.reminder.IAlarmService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public int getPid() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPid, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_getPid = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public int getPid() throws android.os.RemoteException;
}
