
package uk.ac.ed.timetable.database;


import android.content.ContentValues; 
import android.database.Cursor;


public abstract class DataRow
{
	//fields
	protected Database userdb = null;
	private DataField[] vecTableDef = null;
	private ContentValues values = new ContentValues();
	

	//methods
	public DataRow(Database userdb)
	{
		this.userdb = userdb;
	}
		
	public Database GetUserDb()
	{
		return userdb; 
	}
	
	public void SetTableDefinition(DataField[] vecTableDef)
	{
		this.vecTableDef = vecTableDef;		
		//initialize field parent
		UpdateDataFieldsParentRow(this);
	}
	
	public void UpdateDataFieldsParentRow(DataRow row)
	{
		for (int i = 0; i < vecTableDef.length; i++)
			vecTableDef[i].SetParentRow(row);
	}
	
	public void CopyTableDefinition(DataRow data)
	{		
		SetTableDefinition(data.vecTableDef);		
	}
	
	public DataField[] GetTableDef()
	{
		return vecTableDef;
	}
	
	public boolean Validate()
	{
		return false;
	}
	
	public void ClearContentValues()
	{
		values.clear();
	}
	
	public ContentValues GetContentValues()
	{
		return values;
	}

	public void SetContentValues(ContentValues values)
	{
		this.values = values;
		UpdateDataFieldsParentRow(this);
	}
	
	public boolean CopyContentValues(ContentValues values)
	{
		this.values = values;
		UpdateDataFieldsParentRow(this);
		try
		{
			GetValuesFromDataRow();
			return true;
		} catch (Exception e) {
		}					
		return false;
	}
	
	public DataField Value(int idx)
	{
		return vecTableDef[idx];
	}
	
	public String fieldName(int idx)
	{
		return vecTableDef[idx].GetName();	
	}	
	
	public boolean GetValuesFromCursor(Cursor cr)
	{
		if ((cr != null) && (cr.getPosition() != -1))
		{
			for (int idx = 0; idx < vecTableDef.length; idx++)
			{
				DataField field = Value(idx);
				//check if null value
				if (cr.isNull(idx))
				{
					field.setNull();
				} else {
					final DataField.Type t = field.GetType();
					//parse value by type
					if (t == DataField.Type.INT)
						field.set(cr.getLong(idx));
					if (t == DataField.Type.TEXT)
						field.set(cr.getString(idx));
					if (t == DataField.Type.BOOL)
						field.set((cr.getInt(idx) == 1)?true:false);
				}				
			}
			return true;
		}
		return false;
	}
	
	//sets fields values data (ContentValues values contener) from parent object fields
	public abstract void SetValuesForDataRow();

	//gets data from fields values (ContentValues values contener) to parent object fields
	public abstract void GetValuesFromDataRow();

	public abstract String GetTableName();	
	
}

