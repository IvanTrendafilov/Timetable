
package uk.ac.ed.timetable.database;


import java.util.*;
import uk.ac.ed.timetable.RepeatData;


public class DataRowCourse extends DataRow
{
	//Table field indexes for field select speedup
	public static class fid
	{
		public static final int ID = 0;
		public static final int Subject = 1;
		public static final int StartDate = 2;
		public static final int DurationInMinutes = 3;
		public static final int AllDay = 4;
		public static final int Alarm = 5;
		public static final int RepeatType = 6;
		public static final int RepeatEvery = 7;
		public static final int RepeatEndOnDate = 8;
	};
	
	//Table definition
	private final DataField[] TableDef = {
			new DataField(fid.ID, "_ID", DataField.Type.INT, true, true),
			new DataField(fid.Subject, "Subject", DataField.Type.TEXT, true, false),
			new DataField(fid.StartDate, "StartDate", DataField.Type.INT, true, false),
			new DataField(fid.DurationInMinutes, "DurationInMinutes", DataField.Type.INT, true, false),
			new DataField(fid.AllDay, "AllDay", DataField.Type.BOOL, true, false),
			new DataField(fid.Alarm, "Alarm", DataField.Type.BOOL, true, false),
			new DataField(fid.RepeatType, "RepeatType", DataField.Type.INT, true, false),
			new DataField(fid.RepeatEvery, "RepeatEvery", DataField.Type.INT, true, false),
			new DataField(fid.RepeatEndOnDate, "RepeatEndOnDate", DataField.Type.INT, true, false),
	};
	
	
	//fields
	private String sSubject = "";
	private Calendar calDateStart = Calendar.getInstance();
	private Calendar calDateStop = Calendar.getInstance();	
	private int iDurationInMinutes = 15; 	
	private boolean bAllDay = false;
	private boolean bAlarm = true;
	private RepeatData Repeat = new RepeatData();
	
	
	//methods
	public DataRowCourse(Database userdb)
	{
		super(userdb);
		SetTableDefinition(TableDef);
	}
	
	//setters
	public void SetSubject(String value)
	{
		sSubject = new String(value.trim());
	}
	public void SetStartDate(Calendar calDate)
	{
		calDateStart.setTimeInMillis(calDate.getTimeInMillis());		
		calDateStart.set(Calendar.SECOND, 0);
		calDateStart.set(Calendar.MILLISECOND, 0);
	}
	public void SetDuration(long value)
	{
		iDurationInMinutes = (int)value;
	}
	public void SetAllDay(boolean value)
	{
		bAllDay = value;
	}
	public void SetAlarm(boolean value)
	{
		bAlarm = value;
	}
	
	//getters
	public String GetSubject()
	{
		return sSubject;
	}
	public Calendar GetStartDate()
	{
		return calDateStart;
	}
	public int GetDuration()
	{
		return iDurationInMinutes;
	}
	public Calendar GetStopDate()
	{
		calDateStop.setTimeInMillis(calDateStart.getTimeInMillis());
		calDateStop.add(Calendar.MINUTE, iDurationInMinutes);
		return calDateStop;
	}
	public boolean GetAllDay()
	{
		return bAllDay;
	}
	public boolean GetAlarm()
	{
		return bAlarm;
	}
	public RepeatData GetRepeat()
	{
		return Repeat;
	}
		
/*
	public String toString()
	{
		String s = "";
		s += sSubject + "\n";
		
		SimpleDateFormat dateFormatFull = new SimpleDateFormat("EEEE, dd-MM-yyyy, hh:mm");
		dateFormatFull.format(calDateStart);
		
s += DateFormat.format("EEEE, dd-MM-yyyy, hh:mm", calDateStart).toString() + "\n";
		
		s += String.format("allday: %b, alarm: %b", bAllDay, bAlarm) + "\n";
		s += String.format("repeat: %d", GetRepeat().GetRepeatTypeAsInt()) + "\n";		
		s += String.format("every: %d, endon: %b", GetRepeat().GetEvery(), GetRepeat().UsingEndOnDate()) + "\n";
		
s += DateFormat.format("EEEE, dd-MM-yyyy, ", GetRepeat().GetEndOnDate()) + "\n";
 
		return s;
	}
*/
	
	@Override
	public boolean Validate()
	{
		if (sSubject.length() > 0)
		{			
			return true;
		}
		return false;
	}
	
	@Override
	public void SetValuesForDataRow()
	{
		ClearContentValues();

		Value(fid.Subject).set(GetSubject());					
		Value(fid.StartDate).set(GetStartDate());
		Value(fid.DurationInMinutes).set(GetDuration());					
		Value(fid.AllDay).set(GetAllDay());
		Value(fid.Alarm).set(GetAlarm());
		
		int iRepeatType = GetRepeat().GetRepeatTypeAsInt();
		
		Value(fid.RepeatType).set(iRepeatType);						
		Value(fid.RepeatEvery).setNull();
		Value(fid.RepeatEndOnDate).setNull();

		//if repeat type != NONE
		if (iRepeatType != 0)
		{
			Value(fid.RepeatEvery).set(GetRepeat().GetEvery());
			if (GetRepeat().UsingEndOnDate())
			{
				Value(fid.RepeatEndOnDate).set(GetRepeat().GetEndOnDate());							
			}
		}
	}

	@Override
	public void GetValuesFromDataRow()
	{
		//subject values is not required for compare
		SetSubject(Value(fid.Subject).asString());
		
		SetStartDate(Value(fid.StartDate).asCalendar());
		SetDuration(Value(fid.DurationInMinutes).asLong());
		
		SetAllDay(Value(fid.AllDay).asBoolean());
		SetAlarm(Value(fid.Alarm).asBoolean());

		long lRepeatType = Value(fid.RepeatType).asLong();						
		GetRepeat().SetRepeatTypeAsInt((int)lRepeatType);
		
		//if repeat type == NONE
		GetRepeat().SetEndOnDate(null);
		if (lRepeatType == 0)
		{
			GetRepeat().SetEvery(1);
		} else {
			GetRepeat().SetEvery((int)Value(fid.RepeatEvery).asLong());			
			if (!Value(fid.RepeatEndOnDate).isNull())
			{
				Calendar calDate = Value(fid.RepeatEndOnDate).asCalendar();
				GetRepeat().SetEndOnDate(calDate);
			}
		}
	}

	@Override
	public String GetTableName()
	{
		return Database.sTableNameCourses;
	}	
	
}
