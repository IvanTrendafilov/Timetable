
package uk.ac.ed.timetable.database;


import java.util.Vector;
import uk.ac.ed.timetable.R;
import android.content.*;
import android.database.sqlite.*;
import android.database.*;


public class Database
{
	//types
	public enum Result
	{
		Success,
		errUnknown,
		errCantInsertNewData, 
		errCantUpdateData, 
		errCantCreateTable, 
		errNoDbAccess,
		errCantGetDataFromTable,	    	
		errCantFindData,
		errCantGetData,		
		errCantDeleteData,
		errTableNotExists,
		errCantSetValuesForDataRow,
		errCantGetValuesFromDataRow,
	};
		
	//fields
	public static final String sTableNameCourses = "Courses";
	public static final String sTableNameAssignments = "Assignments";
	public static final String sTableNameNotes = "Notes";
	public static final String sTableNameAlarms = "Alarms";
  
  //fields
	private final String dbName = "TimetableDatabase.db";
	private Context ctx = null;
	private SQLiteDatabase db = null;
	private Result resultDbTablesCreated = Result.errUnknown;	
	
	//methods
	public Database(Context context)
	{
		ctx = context;
		Open();
		CreateTables();
	}
	
	public final String Name()
	{
		return dbName;
	}

	public static int GetErrDesc(Result result)
	{
		int msgId = R.string.errUnknown;
		if (result == Result.errCantInsertNewData)
			msgId = R.string.errCantInsertNewData;
		if (result == Result.errCantUpdateData)
			msgId = R.string.errCantUpdateData;
		if (result == Result.errCantCreateTable)
			msgId = R.string.errCantCreateTable;
		if (result == Result.errNoDbAccess)
			msgId = R.string.errNoDbAccess;
		if (result == Result.errCantGetDataFromTable)
			msgId = R.string.errCantGetDataFromTable;
		if (result == Result.errCantFindData)
			msgId = R.string.errCantFindData;
		if (result == Result.errCantGetData)
			msgId = R.string.errCantGetData;
		if (result == Result.errCantDeleteData)
			msgId = R.string.errCantDeleteData;
		if (result == Result.errTableNotExists)
			msgId = R.string.errTableNotExists;
		if (result == Result.errCantSetValuesForDataRow)
			msgId = R.string.errCantSetValuesForDataRow;
		if (result == Result.errCantGetValuesFromDataRow)
			msgId = R.string.errCantGetValuesFromDataRow;					
		return msgId;
	}
		
	public boolean Open()
	{
		boolean bSuccess = false;		
		//open / create database		
		db = ctx.openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null);
		//test result
		if (IsOpened())
		{
			bSuccess = true;
		} else {
			db = null;
		}
		return bSuccess;
	}
	
	public void Close()
	{
		if (IsOpened())
			db.close();
	}
	
	public boolean IsOpened()
	{
		if (db != null)
			return true;
		return false;
	}
	
	public SQLiteDatabase GetSQLiteDb()
	{
		return db;
	}
	
	public boolean Delete()
	{
		Close();
		return ctx.deleteDatabase(dbName);
	}
	
	public boolean ExecSQL(String sql)
	{
		boolean bSuccess = false;
		try
		{
			if (IsOpened())
				db.execSQL(sql);
			bSuccess = true;
		} catch (SQLException e) {
		}		
		return bSuccess;
	}
	
	public boolean TableExists(String sTableName)
	{
		boolean bResult = false;
		if (IsOpened())
		{
			String sql = "select name from sqlite_master where type = 'table' and name = '%s'";		
			sql = String.format(sql, sTableName);			
			Cursor cr = db.rawQuery(sql, null);						
			if (cr.getCount() > 0)
				bResult = true;
			cr.close();
		}
		return bResult;
	}
	
	private void CreateTables()
	{
		resultDbTablesCreated = Database.Result.errUnknown;
		if (IsOpened())
		{			
			Vector<DataRow> vecDataRows = new Vector<DataRow>();
			vecDataRows.add(new DataRowCourse(this));
			vecDataRows.add(new DataRowAssignment(this));
			vecDataRows.add(new DataRowNote(this));
			vecDataRows.add(new DataRowAlarm(this));
			
			for (int i = 0; i < vecDataRows.size(); i++)
			{
				DataTable dataTable = new DataTable(vecDataRows.get(i));				
				if (dataTable.CreateTable())
				{	
					resultDbTablesCreated = Database.Result.Success;				
				} else {
					resultDbTablesCreated = Database.Result.errCantCreateTable;
					break;
				}
			}			
		} else {		
			resultDbTablesCreated = Database.Result.errNoDbAccess;
		}	
	}
	
	public boolean TablesCreated()
	{
		return resultDbTablesCreated == Result.Success;		
	}

	public synchronized boolean DatabaseReady()
	{
		return (IsOpened() && TablesCreated());
	}
	
	public Result TablesCreationResult()
	{
		return resultDbTablesCreated;
		
	}
		
	public void finalize(){
		
		db.close();		
		
	}
	
	
	
}
