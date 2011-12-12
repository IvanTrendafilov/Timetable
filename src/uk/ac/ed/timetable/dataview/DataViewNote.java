
package uk.ac.ed.timetable.dataview;


import java.util.*;
import uk.ac.ed.timetable.Prefs;
import uk.ac.ed.timetable.database.DataRowNote;
import uk.ac.ed.timetable.database.Database;
import android.database.Cursor;


public class DataViewNote extends DataView
{
	//Comparator type
	public class RowsComparator implements Comparator<DataViewItem>
	{
		public int compare(DataViewItem item1, DataViewItem item2)
		{
			String s1 = item1.sSubject;
			String s2 = item2.sSubject;				
			return s1.compareTo(s2);
		}		
	};
	
	//fields
	private RowsComparator fnCmp = null; 

	//methods
	public DataViewNote(Database db, Prefs prefs)
	{
		super(db, prefs);
		sTableName = Database.sTableNameNotes;
		fnCmp = new RowsComparator();
	}

	@Override
	public void AddItem(Cursor cr)
	{
		DataViewItem item = new DataViewItem();

		item.lID = cr.getLong(DataRowNote.fid.ID);
		item.sSubject = cr.getString(DataRowNote.fid.Subject);

		rows.add(item);
	}
	
	@Override
	public void FilterDataForView(DataViewItem item, final Calendar calStartDate, final int agendaViewType)
	{
		item.viewMode = agendaViewType;
	}
	
	@Override
	protected void FilterDataPrepare(final Calendar calStartDate, final int agendaViewType)
	{
		
	}
	
	@Override
	public void SortView()
	{
		Collections.sort(rows, fnCmp);
	}
		
}
