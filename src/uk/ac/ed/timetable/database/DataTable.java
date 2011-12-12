
package uk.ac.ed.timetable.database;


import android.database.Cursor;


public class DataTable
{
	//fields
	private DataRow dataRow = null;
	private static boolean CorDelete=false;

	//methods
	public DataTable(DataRow dataRow)
	{
		this.dataRow = dataRow;
	}
	
	public Database GetUserDb()
	{
		return dataRow.GetUserDb(); 
	}
	
	public String GetTableName()
	{
		return dataRow.GetTableName();
	}
	
	public DataRow GetDataRow()
	{
		return dataRow;
	}
				
	public boolean CreateTable()
	{
		if (GetUserDb().TableExists(GetTableName()))
		{
			return true;
		} else {
			return GetUserDb().ExecSQL(GetSqlTableDefinition(GetTableName(), dataRow.GetTableDef()));
		}
	}
	
	public String GetSqlTableDefinition(String sTableName, DataField[] vecTableDef)
	{
		String def = "CREATE TABLE " + sTableName + " (";
		for (int i = 0; i < vecTableDef.length; i++)
		{
			def += vecTableDef[i].GetColumnDefinition();
			if (i < (vecTableDef.length - 1))
				def += ", ";
		}	
		def += ")";
		return def;
	}
	
	public long InsertValues()
	{
		long lRowId = GetUserDb().GetSQLiteDb().insert(GetTableName(), null, dataRow.GetContentValues());
    return lRowId; 
	}
	
	public long UpdateValues(long lRowId)
	{
		String sWhere = String.format("_ID = %d", lRowId);
		long lRowsUpdated = GetUserDb().GetSQLiteDb().update(GetTableName(), dataRow.GetContentValues(), sWhere, null);
		return lRowsUpdated;
	}
	
	public static void setCorDelete(boolean a) {
		CorDelete=a;
	}
	
  public long DeleteDataRow(long lRowId)
  {
  	if(CorDelete) {
  		Cursor cr = GetUserDb().GetSQLiteDb().rawQuery("SELECT SUBJECT FROM (SELECT * FROM COURSES WHERE _ID = "+lRowId+")",null);
  		cr.moveToFirst();
  		String sWhere = String.format("Subject = \"%s\"",cr.getString(0));
  		cr.close();
  		GetUserDb().GetSQLiteDb().delete(GetTableName(), sWhere, null);
  		CorDelete=false;
  		return 1;
  	}
  	else {
  		String sWhere = String.format("_ID = %d",lRowId);
  		long lRowsUpdated = GetUserDb().GetSQLiteDb().delete(GetTableName(), sWhere, null);
  		return lRowsUpdated;
  	}	 
	}
		

	
	public Cursor LocateDataRow(long lRowId)
	{
		final String s = "select * from %s where _ID = %d";		
		String sql = String.format(s, GetTableName(), lRowId);
		Cursor cr = GetUserDb().GetSQLiteDb().rawQuery(sql, null);
		//if cursor valid, set first data row as current
		if ((cr != null) && (cr.getCount() > 0))
			cr.moveToFirst();
		return cr;
	}
	
	public Cursor LocateAlarmDataRow(int iType, long lRefID)
	{
		final String s = "select * from %s where Type = %d and RefID = %d";		
		String sql = String.format(s, GetTableName(), iType, lRefID);
		Cursor cr = GetUserDb().GetSQLiteDb().rawQuery(sql, null);
		
		//if cursor valid, set first data row as current
		if ((cr != null) && (cr.getCount() > 0))
			cr.moveToFirst();
		else
			cr.close();
		
		return cr;
	}	
	
	public Database.Result UpdateData(boolean bInsertMode, long lEditRowId)
	{
		Database.Result result = Database.Result.errUnknown;
		if (GetUserDb().IsOpened())
		{			
			try
			{
				dataRow.SetValuesForDataRow();
			} catch (Exception e) {
				return Database.Result.errCantSetValuesForDataRow;					
			}
			//select update mode
			if (bInsertMode)
			{
				//insert new data row
				long lRowId = InsertValues();
				if (lRowId > 0)
				{
					result = Database.Result.Success;
				} else {
					result = Database.Result.errCantInsertNewData;
				}
			} else {
				//update existing data row
				long lRowsUpdated = UpdateValues(lEditRowId);
				if (lRowsUpdated == 1)
				{
					result = Database.Result.Success;
				} else {
					result = Database.Result.errCantUpdateData;
				}									
			}
		} else {		
			result = Database.Result.errNoDbAccess;
		}
		return result;
	}
	
	public Database.Result DeleteData(long iRowId)
	{		
		Database.Result result = Database.Result.errUnknown;
		if (GetUserDb().IsOpened())
		{			
			if (GetUserDb().TableExists(GetTableName()))
			{
				long lRowsDeleted = DeleteDataRow(iRowId);
				if (lRowsDeleted == 1)
				{
					result = Database.Result.Success;
				} else {
					result = Database.Result.errCantDeleteData;
				}
			} else {
				result = Database.Result.errTableNotExists;
			}
		} else {		
			result = Database.Result.errNoDbAccess;
		}
		return result;		
	}
	
  public Database.Result GetRowDataForEdit(long lRowId)
  {
  	Database.Result result = Database.Result.errUnknown;
		//get requested data row
		Cursor cr = LocateDataRow(lRowId);
		if (cr == null)
		{
			result = Database.Result.errCantGetData;
		} else {
			if (cr.getCount() > 0)
			{
				if (dataRow.GetValuesFromCursor(cr))
				{
					try
					{
						dataRow.GetValuesFromDataRow();
					} catch (Exception e) {
						return Database.Result.errCantGetValuesFromDataRow;					
					}					
					result = Database.Result.Success;
				} else {
					result = Database.Result.errCantGetDataFromTable;	    	
				}
				cr.close();
			} else {
				result = Database.Result.errCantFindData;
			}
		}
		return result;
  }
  
}

