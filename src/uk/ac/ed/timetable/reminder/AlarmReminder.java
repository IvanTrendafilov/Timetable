package uk.ac.ed.timetable.reminder;

import uk.ac.ed.timetable.*;
import uk.ac.ed.timetable.views.*;
import java.util.ArrayList;
import java.util.Calendar;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import android.app.NotificationManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Bundle;
import android.os.Vibrator;

public class AlarmReminder
{
	// fields

	private Context context = null;
	protected AlarmService.AlarmPrefs prefs = null;
	protected Utils utils = null;
	private ArrayList<AlarmDataViewItem> alarmItems = new ArrayList<AlarmDataViewItem>();
	private NotificationManager mNM = null;
	private final int iNotifyID1 = 1;
	private int iLastAlarmsHash = 0;
	private Calendar calHashToday = Calendar.getInstance();
	// private AlarmSound alarmSound = null;

	// fields
	private String msgYouHaveASTS = "";
	private String msgYouHaveTS = "";
	private String msgYouHaveAS = "";
	private String msgASTS = "";
	private String msgTS = "";
	private String msgAS = "";
	private String strReminder = "";

	// fields
	private String sAppName = "";

	// methods
	public AlarmReminder(Context ctx, AlarmService.AlarmPrefs prefs, Utils utils)
	{
		this.context = ctx;
		this.prefs = prefs;
		this.utils = utils;

		sAppName = this.context.getString(R.string.app_name);
		mNM = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		RemoveNotify();

		// localize strings
		msgYouHaveASTS = utils.GetResStr(R.string.msgYouHaveASTS);
		msgYouHaveTS = utils.GetResStr(R.string.msgYouHaveTS);
		msgYouHaveAS = utils.GetResStr(R.string.msgYouHaveAS);
		msgASTS = utils.GetResStr(R.string.msgASTS);
		msgTS = utils.GetResStr(R.string.msgTS);
		msgAS = utils.GetResStr(R.string.msgAS);
		strReminder = utils.GetResStr(R.string.strReminder);

	}

	public void Clear()
	{
		alarmItems.clear();
	}

	public void RemoveNotify()
	{
		mNM.cancel(iNotifyID1);
	}

	public void AddAlarmData(ArrayList<AlarmDataViewItem> items)
	{
		this.alarmItems.addAll(items);
	}

	public boolean IsAlarm()
	{
		return (alarmItems.size() > 0);
	}

	public int GetItemsCount(int iOrderFilter)
	{
		int iCount = 0;
		for (int index = 0; index < alarmItems.size(); index++)
		{
			AlarmDataViewItem item = alarmItems.get(index);
			if (item.GetOrder() == iOrderFilter)
				iCount++;
		}
		return iCount;
	}

	public String GetPopupMsg(int iCountAppts, int iCountAssignments)
	{
		String msg = String.format(msgYouHaveASTS, iCountAppts, iCountAssignments);
		if ((iCountAppts == 0) && (iCountAssignments > 0))
			msg = String.format(msgYouHaveTS, iCountAssignments);
		if ((iCountAssignments == 0) && (iCountAppts > 0))
			msg = String.format(msgYouHaveAS, iCountAppts);
		return msg;
	}

	public String GetStatusMsg(int iCountAppts, int iCountAssignments)
	{
		String msg = String.format(msgASTS, iCountAppts, iCountAssignments);
		if ((iCountAppts == 0) && (iCountAssignments > 0))
			msg = String.format(msgTS, iCountAssignments);
		if ((iCountAssignments == 0) && (iCountAppts > 0))
			msg = String.format(msgAS, iCountAppts);
		return msg;
	}

	public String GetTodayHashString()
	{
		calHashToday.setTimeInMillis(System.currentTimeMillis());
		return Integer.toString(calHashToday.get(Calendar.YEAR))
				+ Integer.toString(calHashToday.get(Calendar.MONTH))
				+ Integer.toString(calHashToday.get(Calendar.DAY_OF_MONTH));
	}

	public String GetAlarmsHashString()
	{
		String sMsg = "";
		for (int index = 0; index < alarmItems.size(); index++)
		{
			AlarmDataViewItem item = alarmItems.get(index);
			sMsg += item.GetHashString();
		}
		//add today hash, for changing hash for next day compare (repeat courses)
		sMsg += GetTodayHashString();
		return sMsg;
	}

	public boolean AlarmsChanged()
	{
		String sAlarmsHashString = GetAlarmsHashString();
		if (sAlarmsHashString.hashCode() == iLastAlarmsHash)
			return false;
		iLastAlarmsHash = sAlarmsHashString.hashCode();
		return true;
	}

	public void PutDataItemsToBundle(Bundle data, int iOrderFilter)
	{
		int iKeyIndex = 0;
		for (int i = 0; i < alarmItems.size(); i++)
		{
			AlarmDataViewItem item = alarmItems.get(i);
			if (item.GetOrder() == iOrderFilter)
			{
				String sKey = "_" + Integer.toString(iOrderFilter) + "_"
						+ Integer.toString(iKeyIndex);

				data.putLong("rowid" + sKey, item.GetID());
				data.putString("text" + sKey, item.GetText(utils, prefs.b24HourMode));

				iKeyIndex++;
			}
		}
		data.putInt("typetotal_" + Integer.toString(iOrderFilter), iKeyIndex);
	}

	public PendingIntent getAlarmDialogIntent()
	{
		Intent intentAlarmDialog = new Intent("android.intent.action.Timetable.ACTION_MODE_VIEW_ALARMDIALOG");
		Bundle data = new Bundle();
		PutDataItemsToBundle(data, AlarmDataViewItem.iOrderAppts);
		PutDataItemsToBundle(data, AlarmDataViewItem.iOrderAssignments);
		intentAlarmDialog.putExtras(data);
		// return PendingIntent.getActivity(this.context, 0, intentAlarmDialog,
		// PendingIntent.FLAG_CANCEL_CURRENT);
		return PendingIntent.getActivity(this.context, 0, intentAlarmDialog, 0);
	}

	public void ShowNotification()
	{
		if (IsAlarm())
		{
			int iCountAppts = GetItemsCount(AlarmDataViewItem.iOrderAppts);
			int iCountAssignments = GetItemsCount(AlarmDataViewItem.iOrderAssignments);

			// is there something to show ?
			if ((iCountAppts > 0) || (iCountAssignments > 0))
			{
				// check if alarms changed from last time
				if (AlarmsChanged())
				{
					// remove last notify from status bar
					RemoveNotify();

					// make notify text
					final String sTitle = sAppName + ": " + strReminder;
					String sMessage = GetPopupMsg(iCountAppts, iCountAssignments);

					// make notify status
					String sStatus = GetStatusMsg(iCountAppts, iCountAssignments);

					// show popup message
					ViewToastMsg viewToastMsg = new ViewToastMsg(this.context, sTitle, sMessage);
					Toast toast = new Toast(this.context);
					toast.setDuration(Toast.LENGTH_LONG);
					toast.setView(viewToastMsg);
					toast.show();

					// show status bar notification item
					Notification notifyItem = new Notification(R.drawable.stat_notify_alarm, sStatus, System.currentTimeMillis());

					AlarmSound sound = new AlarmSound(context);
					sound.play();
					
					Log.i(this.getClass().getName(), "Vibrator is starting");
					Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);					
					v.vibrate(2000);
					//.vibrate(pattern, 0);
					//v.cancel();
					Log.i(this.getClass().getName(), "Vibrator is stopped");

					notifyItem.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS;
					notifyItem.setLatestEventInfo(this.context, strReminder, sStatus, getAlarmDialogIntent());

					// run
					mNM.notify(iNotifyID1, notifyItem);
				}
			}
		} else
		{
			RemoveNotify();
			iLastAlarmsHash = 0;
		}
	}
	

}
