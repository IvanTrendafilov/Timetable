
package uk.ac.ed.timetable.database;


public class DataRowNote extends DataRow
{
	//Table field indexes for field select speedup
	public static class fid
	{
		public static final int ID = 0;
		public static final int Subject = 1;
	};
	
	//Table definition
	private final DataField[] TableDef = {
			new DataField(fid.ID, "_ID", DataField.Type.INT, true, true),
			new DataField(fid.Subject, "Subject", DataField.Type.TEXT, true, false),
	};

	
	//fields
	private String sSubject = "";
	
	
	//methods
	public DataRowNote(Database userdb)
	{
		super(userdb);
		SetTableDefinition(TableDef);
	}
	
	//setters
	public void SetSubject(String value)
	{
		sSubject = new String(value.trim());
	}

	//getters
	public String GetSubject()
	{
		return sSubject;
	}
	
	//methods
	@Override
	public String toString()
	{
		String s = "";
		s += sSubject + "\n";
		return s;
	}
	
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
	}
	
	@Override
	public void GetValuesFromDataRow()
	{
		SetSubject(Value(fid.Subject).asString());
	}

	@Override
	public String GetTableName()
	{
		return Database.sTableNameNotes;
	}

}
