
package uk.ac.ed.timetable.database;


import java.util.*;


//AssignmentData class
public class DataRowAssignment extends DataRow
{
	//Table field indexes for field select speedup
	public static class fid
	{
		public static final int ID = 0;
		public static final int Subject = 1;
		public static final int Done = 2;
		public static final int Priority = 3;
		public static final int Alarm = 4;
		public static final int DueDate = 5;
	};
	
	//Table definition
	private final DataField[] TableDef = {
			new DataField(fid.ID, "_ID", DataField.Type.INT, true, true),
			new DataField(fid.Subject, "Subject", DataField.Type.TEXT, true, false),
			new DataField(fid.Done, "Done", DataField.Type.BOOL, true, false),
			new DataField(fid.Priority, "Priority", DataField.Type.INT, true, false),
			new DataField(fid.Alarm, "Alarm", DataField.Type.BOOL, true, false),
			new DataField(fid.DueDate, "DueDate", DataField.Type.INT, true, false),			
	};

	
	//fields
	private String sSubject = "";
	private boolean bDone = false;
	private int iPriority = 1;				
	private boolean bAlarm = false;			
	private Calendar calDueDate = Calendar.getInstance();
	
	
	//methods
	public DataRowAssignment(Database userdb)
	{
		super(userdb);
		SetTableDefinition(TableDef);
	}
	
	//setters
	public void SetSubject(String value)
	{
		sSubject = new String(value.trim());
	}
	public void SetDone(boolean value)
	{
		bDone = value;
	}
	public void SetPriority(long value)
	{
		iPriority = (int)value;
	}
	public void SetAlarm(boolean value)
	{
		bAlarm = value;
	}
	public void SetDueDate(Calendar value)
	{
		if (value == null)
		{
			calDueDate.setTimeInMillis(0);	
		} else {
			calDueDate.setTimeInMillis(value.getTimeInMillis());	
			calDueDate.set(Calendar.SECOND, 0);
			calDueDate.set(Calendar.MILLISECOND, 0);			
		}
	}

	//getters
	public String GetSubject()
	{
		return sSubject;
	}
	public boolean GetDone()
	{
		return bDone;
	}
	public int GetPriority()
	{
		return iPriority;
	}
	public boolean GetAlarm()
	{
		return bAlarm;
	}	
	public boolean UsingDueDate()
	{
		return (calDueDate.getTimeInMillis() > 0);
	}	
	
	public Calendar GetDueDate()
	{
		return calDueDate;	
	}
	
	/*
	public String toString()
	{
		String s = "";
		s += sSubject + "\n";
		s += String.format("done: %b, priority: %i, alarm: %b", bDone, iPriority, bAlarm) + "\n";
		s += DateFormat.format("EEEE, dd-MM-yyyy, hh:mm", calDueDate).toString() + "\n";
		return s;
	}
	*/
	
	@Override
	public boolean Validate()
	{
		if (sSubject.length() > 0 && UsingDueDate()==true)
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
		Value(fid.Done).set(GetDone());
		Value(fid.Priority).set(GetPriority());
		Value(fid.Alarm).set(GetAlarm());
		
		Value(fid.DueDate).setNull();
		if (UsingDueDate())
			Value(fid.DueDate).set(GetDueDate());
	}
	
	@Override
	public void GetValuesFromDataRow()
	{
		SetSubject(Value(fid.Subject).asString());
		SetDone(Value(fid.Done).asBoolean());
		SetPriority(Value(fid.Priority).asLong());	
		SetAlarm(Value(fid.Alarm).asBoolean());
			
		if (Value(fid.DueDate).isNull())
		{
			SetDueDate(null);
		} else {
			SetDueDate(Value(fid.DueDate).asCalendar());
		}
	}

	@Override
	public String GetTableName()
	{
		return Database.sTableNameAssignments;
	}

}
