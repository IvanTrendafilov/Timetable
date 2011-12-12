
package uk.ac.ed.timetable.database;


//AssignmentData class
public class DataRowAlarm extends DataRow
{	
	//Table field indexes for field select speedup
	public static class fid
	{
		public static final int ID = 0;
		public static final int Type = 1;
		public static final int RefID = 2;
		public static final int ActionClear = 3;
		public static final int ActionSnooze = 4;
		public static final int SnoozeCount = 5;
	};
	
	//Table definition
	private final DataField[] TableDef = {
			new DataField(fid.ID, "_ID", DataField.Type.INT, true, true),
			new DataField(fid.Type, "Type", DataField.Type.TEXT, true, false),
			new DataField(fid.RefID, "RefID", DataField.Type.INT, true, false),
			new DataField(fid.ActionClear, "ActionClear", DataField.Type.INT, true, false),
			new DataField(fid.ActionSnooze, "ActionSnooze", DataField.Type.INT, true, false),
			new DataField(fid.SnoozeCount, "ActionSnoozeCount", DataField.Type.INT, true, false),			
	};

	//fields
	public long lType = 0;
	public long lRefID = 0;
	public long lActionClear = 0;
	public long lActionSnooze = 0;
	public long lSnoozeCount = 0;			
	
	//methods
	public DataRowAlarm(Database userdb)
	{
		super(userdb);
		SetTableDefinition(TableDef);
	}
		
	//methods
	@Override
	public String toString()
	{
		return "";
	}
	
	@Override
	public boolean Validate()
	{
		return true;
	}
	
	@Override
	public void SetValuesForDataRow()
	{
		ClearContentValues();

		Value(fid.Type).set(lType);
		Value(fid.RefID).set(lRefID);
		Value(fid.ActionClear).set(lActionClear);
		Value(fid.ActionSnooze).set(lActionSnooze);
		Value(fid.SnoozeCount).set(lSnoozeCount);
	}
	
	@Override
	public void GetValuesFromDataRow()
	{
		lType = Value(fid.Type).asLong();
		lRefID = Value(fid.RefID).asLong();
		lActionClear = Value(fid.ActionClear).asLong();
		lActionSnooze = Value(fid.ActionSnooze).asLong();
		lSnoozeCount = Value(fid.SnoozeCount).asLong();
	}

	@Override
	public String GetTableName()
	{
		return Database.sTableNameAlarms;
	}

}
