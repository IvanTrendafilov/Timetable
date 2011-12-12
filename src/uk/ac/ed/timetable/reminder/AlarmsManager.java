
package uk.ac.ed.timetable.reminder;


import java.util.ArrayList;
import android.database.Cursor;
import uk.ac.ed.timetable.Prefs;
import uk.ac.ed.timetable.database.*;
import java.util.*;


public class AlarmsManager
{
	//types
	public static final int iAction_NONE = 0;
	public static final int iAction_CLEAR = 1;
	public static final int iAction_SNOOZE = 2;
	public static final int iAction_RESET = 3;

	//fields
	private Calendar calNow = Calendar.getInstance();			
	private Calendar calActionClear = Calendar.getInstance();		
	private Calendar calActionSnooze = Calendar.getInstance();		
	
	//fields
	private Database db = null;
	private Prefs prefs = null;
	private DataRowAlarm rowAlarm = null;
	private DataTable tblAlarms = null;
	
	//fields
	private ArrayList<AlarmDialogDataItem> vecAlarms = new ArrayList<AlarmDialogDataItem>();
		
	//methods
	public AlarmsManager(Database db, Prefs prefs)
	{
		this.db = db;
		this.prefs = prefs;
		rowAlarm = new DataRowAlarm(db);		
		tblAlarms = new DataTable(rowAlarm);		
	}
	
	public void clear()
	{
		vecAlarms.clear();
	}	

	public void putAlarmToProcess(AlarmDialogDataItem alarm, int iAction)
	{
		alarm.setAction(iAction);
		vecAlarms.add(alarm);
	}
	
	public void processAll()
	{
		for (int i = 0; i < vecAlarms.size(); i++)
		{
			final Database.Result result = processAlarm(vecAlarms.get(i));
			if (result != Database.Result.Success)
			{
				//utils.MsgResStr(Database.GetErrDesc(result), Utils.MsgType.Error);
				//break;
			}
		}		
	}
	
	private Database.Result processAlarm(AlarmDialogDataItem alarm)
	{
		final int iType = alarm.getOrderFilter();
		final long lRefID = alarm.getID();
		final int iAction = alarm.getAction();
		
		//update alarms table
		final Database.Result result = UpdateAlarmDataRow(iType, lRefID, iAction);
		return result;
	}
		
	private void SetAlarmAction(int iType, long lRefID, int iAction)
	{
		long lCurrTimeNow = System.currentTimeMillis();

		rowAlarm.lType = iType;
		rowAlarm.lRefID = lRefID;

  	rowAlarm.lActionClear = 0;
  	rowAlarm.lActionSnooze = 0;
		
		if (iAction == iAction_CLEAR)
		{
	  	rowAlarm.lActionClear = lCurrTimeNow;
	  	rowAlarm.lSnoozeCount = 0;
		}
		
		if (iAction == iAction_SNOOZE)
		{
	  	rowAlarm.lActionSnooze = lCurrTimeNow;
			rowAlarm.lSnoozeCount = rowAlarm.lSnoozeCount + 1;
			
			//clear alarm if snooze count has max value 
			if (rowAlarm.lSnoozeCount > prefs.iSnoozeCount)
				SetAlarmAction(iType, lRefID, iAction_CLEAR);
		}
		
		if (iAction == iAction_RESET)
		{
	  	rowAlarm.lActionClear = 0;
	  	rowAlarm.lActionSnooze = 0;
	  	rowAlarm.lSnoozeCount = 0;	  	
		}
	}
	
	private Database.Result UpdateAlarmDataRow(int iType, long lRefID, int iAction)
	{
  	Database.Result result = Database.Result.errUnknown;  	
		final Cursor cr = tblAlarms.LocateAlarmDataRow(iType, lRefID);
		if (cr == null)
		{
			result = Database.Result.errCantGetData;
		} else {
			//update table
			if (cr.getCount() > 0)
			{
				//update existing row
				if (rowAlarm.GetValuesFromCursor(cr))
				{
					try
					{
						rowAlarm.GetValuesFromDataRow();
						SetAlarmAction(iType, lRefID, iAction);
						cr.close();
						result = DataRow_update(iType, lRefID);
					} catch (Exception e) {
						return Database.Result.errCantGetValuesFromDataRow;
					}
				} else {
					result = Database.Result.errCantGetDataFromTable;
				}
			} else {
				//insert new row
				rowAlarm.lSnoozeCount = 1;
				SetAlarmAction(iType, lRefID, iAction);
				result = DataRow_insert();
			}
		}
		return result;						
	}
	
	private Database.Result DataRow_insert()
	{
  	Database.Result result = Database.Result.errUnknown;		
  	//get data to ContentValues
  	rowAlarm.SetValuesForDataRow();
  	//insert data row
		long lRowId = db.GetSQLiteDb().insert(tblAlarms.GetTableName(), null, rowAlarm.GetContentValues());
		if (lRowId != -1)
			result = Database.Result.Success;
		return result;						
	}

	private Database.Result DataRow_update(int iType, long lRefID)
	{
  	Database.Result result = Database.Result.errUnknown;		
  	//get data to ContentValues
  	rowAlarm.SetValuesForDataRow();
		//update data row
		final String sWhere = String.format("Type = %d and RefID = %d", iType, lRefID);		
		long lRowsUpdated = db.GetSQLiteDb().update(tblAlarms.GetTableName(), rowAlarm.GetContentValues(), sWhere, null);
		if (lRowsUpdated == 1)		  	
			result = Database.Result.Success;		
		return result;						
	}

	public static boolean DeleteAlarm(Database db, Prefs prefs, DataTable tblSource, long lRefID)
	{
		if (AlarmsManager.AlarmToProcess(tblSource))
		{
			AlarmsManager am = new AlarmsManager(db, prefs);
			return (am.DataRow_delete(tblSource, lRefID) == Database.Result.Success);			
		}
		return false;		
	}
	
	public static boolean ResetAlarm(Database db, Prefs prefs, DataTable tblSource, long lRefID)
	{
		if (AlarmsManager.AlarmToProcess(tblSource))
		{
			AlarmsManager am = new AlarmsManager(db, prefs);
			return (am.DataRow_reset(tblSource, lRefID) == Database.Result.Success);			
		}
		return false;		
	}
	
	private static boolean AlarmToProcess(DataTable tblSource)
	{
		//get source data/table type
		int iType = -1;
		if (tblSource.GetTableName().equals(Database.sTableNameCourses))
			iType = AlarmDataViewItem.iOrderAppts;
		if (tblSource.GetTableName().equals(Database.sTableNameAssignments))
			iType = AlarmDataViewItem.iOrderAssignments;		
		//process alarm
		if (iType != -1)
			return true;
		return false;		
	}

	public Database.Result DataRow_reset(DataTable tblSource, long lRefID)
	{
		//get source data/table type
		int iType = -1;
		if (tblSource.GetTableName().equals(Database.sTableNameCourses))
			iType = AlarmDataViewItem.iOrderAppts;
		if (tblSource.GetTableName().equals(Database.sTableNameAssignments))
			iType = AlarmDataViewItem.iOrderAssignments;		
		//delete alarm
  	Database.Result result = Database.Result.errUnknown;
		if (iType != -1)
		{
			//delete data row
			result = UpdateAlarmDataRow(iType, lRefID, iAction_RESET);			
		}
		return result;						
	}
	
	public Database.Result DataRow_delete(DataTable tblSource, long lRefID)
	{
		//get source data/table type
		int iType = -1;
		if (tblSource.GetTableName().equals(Database.sTableNameCourses))
			iType = AlarmDataViewItem.iOrderAppts;
		if (tblSource.GetTableName().equals(Database.sTableNameAssignments))
			iType = AlarmDataViewItem.iOrderAssignments;		
		//delete alarm
  	Database.Result result = Database.Result.errUnknown;
		if (iType != -1)
		{
			//delete data row
			result = DataRow_delete(iType, lRefID);
		}
		return result;						
	}
	
	private Database.Result DataRow_delete(int iType, long lRefID)
	{
  	Database.Result result = Database.Result.errUnknown;
		//delete data row
		final String sWhere = String.format("Type = %d and RefID = %d", iType, lRefID);		
 		long lRowsUpdated = db.GetSQLiteDb().delete(tblAlarms.GetTableName(), sWhere, null);
		if (lRowsUpdated == 1)		  	
			result = Database.Result.Success;		
		return result;						
	}
	
	private boolean IsAlarmTodayCleared()
	{
		if (rowAlarm.lActionClear > 0)
		{
			calNow.setTimeInMillis(System.currentTimeMillis());			
			calActionClear.setTimeInMillis(rowAlarm.lActionClear);
			
			if (calActionClear.get(Calendar.YEAR) == calNow.get(Calendar.YEAR))
				if (calActionClear.get(Calendar.MONTH) == calNow.get(Calendar.MONTH))
					if (calActionClear.get(Calendar.DAY_OF_MONTH) == calNow.get(Calendar.DAY_OF_MONTH))
						return true;
		}
		return false;
	}

	private boolean IsAlarmCleared()
	{
		return (rowAlarm.lActionClear > 0);
	}
	
	private boolean IsAlarmSnoozed()
	{
		return (rowAlarm.lActionSnooze > 0);
	}
	
	private boolean IsAlarmSnoozeOverdue()
	{
		if (rowAlarm.lActionSnooze > 0)
		{
			calNow.setTimeInMillis(System.currentTimeMillis());						
			calActionSnooze.setTimeInMillis(rowAlarm.lActionSnooze);
			
			//minus one, because we test AFTER
			final int iSnoozeMinutes = prefs.iSnoozeMinutesOverdue - 1;
			
			calActionSnooze.add(Calendar.MINUTE, iSnoozeMinutes);			
			if (calNow.after(calActionSnooze))
				return true;
		}
		return false;
	}
	
	//input: iType: appt/assignment, lRefID of course/assignment to check
	public boolean CanShowAlarmToday(int iType, long lRefID, final boolean bIsRepeat)
	{
		final Cursor cr = tblAlarms.LocateAlarmDataRow(iType, lRefID);
		if ((cr != null) && (cr.getCount() > 0))
		{
			if (rowAlarm.GetValuesFromCursor(cr))
			{
				rowAlarm.GetValuesFromDataRow();
				cr.close();
				
				//check alarm show condition
				if (bIsRepeat)
				{
					if (IsAlarmTodayCleared())
						return false;					
				} else {
					if (IsAlarmCleared())
						return false;
				}
				
				//check alarm snooze condition				
				if (IsAlarmSnoozed())
					if (!IsAlarmSnoozeOverdue())
						return false;
				
			}
		}
		return true;
	}

}
