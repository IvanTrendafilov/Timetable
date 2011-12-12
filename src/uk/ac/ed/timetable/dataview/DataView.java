	
package uk.ac.ed.timetable.dataview;


import java.util.*;
import uk.ac.ed.timetable.agenda.AgendaView;
import uk.ac.ed.timetable.database.Database;
import uk.ac.ed.timetable.Prefs;
import android.database.*;
import android.database.sqlite.*;


public abstract class DataView
{	
	//fields
	private final String sQuery = "select * from %s";
	protected Database db = null;
	protected Prefs prefs = null;
	protected String sTableName = "";
	protected ArrayList<DataViewItem> rows = new ArrayList<DataViewItem>();
	private Calendar calViewStartDate = Calendar.getInstance();
	
	//methods
	public DataView(Database db, Prefs prefs)
	{
		this.db = db;
		this.prefs = prefs;
	}

	protected abstract void AddItem(Cursor cr);
	
	protected abstract void FilterDataPrepare(final Calendar calStartDate, final int agendaViewType);
	
	protected abstract void FilterDataForView(DataViewItem item, final Calendar calStartDate, final int agendaViewType);
	
	protected abstract void SortView();
	
	public boolean ReloadTable()
	{
  	//get data
		Database.Result result = CollectRowsData();
		if (result == Database.Result.Success)
			return true;
		return false;		
	}	
		
  public Database.Result CollectRowsData()
  {
  	Database.Result result = Database.Result.errUnknown;  	
		rows.clear();
		String sql = String.format(sQuery, sTableName);
		SQLiteDatabase sqldb = db.GetSQLiteDb();		
		Cursor cr = sqldb.rawQuery(sql, null);
		if (cr == null)
		{
			result = Database.Result.errCantGetData;
		} else {
			if (cr.getCount() > 0)
			{
				cr.moveToFirst();
				while (!cr.isAfterLast())
				{
					try
					{
						AddItem(cr);
						result = Database.Result.Success;						
					} catch (Exception e) {
						return Database.Result.errCantGetDataFromTable;
					}
					cr.moveToNext();
				}
			} else {
				result = Database.Result.errCantFindData;
			}
			SortView();
		}
		cr.close();
		return result;
  }
    
	public DataViewItem GetRow(int index, final int agendaViewType)	
	{
		try
		{
			DataViewItem values = rows.get(index);			
			if (agendaViewType == values.viewMode)
				return values;
		} catch (IndexOutOfBoundsException e) {
		}
		return null;
	}
	
	public int GetRowsCountForView(final int agendaViewType)
	{
		int iCount = 0;
		for (int i = 0; i < rows.size(); i++)
			if (rows.get(i).viewMode == agendaViewType)
				iCount++;
		return iCount;
	}

	public int GetRowsCountTotal()
	{
		return rows.size();
	}
		
	public int getDaysRangeForView(final int agendaViewType)
	{	
		if (agendaViewType == AgendaView.viewMode.TODAY)
			return 0;

		if (agendaViewType == AgendaView.viewMode.DAY)
			return 0;

		if (agendaViewType == AgendaView.viewMode.WEEK)
			return 7;

		if (agendaViewType == AgendaView.viewMode.MONTH)
			return 42;
	
		//counting days backward !!!
		if (agendaViewType == AgendaView.viewMode.TODAY_ALARM)
			return 7;
		
		return 0;
	}			
		
	public void FilterData(final Calendar calStartDate, final int agendaViewType)
	{
		calViewStartDate.setTimeInMillis(calStartDate.getTimeInMillis());
		calViewStartDate.setFirstDayOfWeek(prefs.iFirstDayOfWeek);

		FilterDataPrepare(calViewStartDate, agendaViewType);

		for (int i = 0; i < rows.size(); i++)
		{
			DataViewItem item = rows.get(i);
			item.viewMode = AgendaView.viewMode.NONE;
			FilterDataForView(item, calViewStartDate, agendaViewType);
		}
	}

}
