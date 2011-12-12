
package uk.ac.ed.timetable.dataview;


import java.util.*;

import uk.ac.ed.timetable.Prefs;
import uk.ac.ed.timetable.Utils;
import uk.ac.ed.timetable.agenda.AgendaView;
import uk.ac.ed.timetable.database.DataRowAssignment;
import uk.ac.ed.timetable.database.Database;
import android.database.Cursor;



public class DataViewAssignment extends DataView
{
	//Comparator type
	public class RowsComparator implements Comparator<DataViewItem>
	{
		public int compare(DataViewItem item1, DataViewItem item2)
		{
			long iPriority1 = item1.lPriority;
			long iPriority2 = item2.lPriority;
			
			if (iPriority1 > iPriority2)
				return 1;
			if (iPriority1 < iPriority2)
				return -1;
			
			if (iPriority1 == iPriority2)
			{
				long d1 = item1.lDueDate;
				long d2 = item2.lDueDate;
				if (d1 >= d2) return 1;
				else return -1;
			}
				
			return 0;
		}		
	};
	
	//fields
	private Calendar calDueDateCmp = Calendar.getInstance();	
	private RowsComparator fnCmp = null; 

	//methods
	public DataViewAssignment(Database db, Prefs prefs)
	{
		super(db, prefs);
		sTableName = Database.sTableNameAssignments;
		fnCmp = new RowsComparator();
	}
	
	@Override
	public void AddItem(Cursor cr)
	{
		DataViewItem item = new DataViewItem();
		
		item.lID = cr.getLong(DataRowAssignment.fid.ID);
		item.sSubject = cr.getString(DataRowAssignment.fid.Subject);
		item.bDone = (cr.getLong(DataRowAssignment.fid.Done) == 1); 							
		item.lPriority = cr.getLong(DataRowAssignment.fid.Priority);
		item.bAlarm = (cr.getLong(DataRowAssignment.fid.Alarm) == 1);
				
		if (!cr.isNull(DataRowAssignment.fid.DueDate))
			item.lDueDate = cr.getLong(DataRowAssignment.fid.DueDate);
		
		
		rows.add(item);
	}
	
	@Override
	public void FilterDataForView(DataViewItem item, final Calendar calStartDate, final int agendaViewType)
	{		
		//agenda view Today
		if (agendaViewType == AgendaView.viewMode.TODAY)
		{
			if (prefs.bShowAllAssignments) // regularly use "prefs.bShowAllAssignments", but here forcing to show
			{
				item.viewMode = agendaViewType;	
			} else {
				if (item.UseDueDate())
				{
					calDueDateCmp.setTimeInMillis(item.lDueDate);
					if (Utils.YearDaysEqual(calStartDate, calDueDateCmp))
						item.viewMode = agendaViewType;
				} else {				
					item.viewMode = agendaViewType;	
				}
			}
		}
		
		//view for alarm
		if (agendaViewType == AgendaView.viewMode.TODAY_ALARM)
		{
			if (!item.bDone)
			{
				if (item.UseDueDate())
				{
					calDueDateCmp.setTimeInMillis(item.lDueDate);
					if (Utils.YearDaysGreater(calStartDate, calDueDateCmp))
						item.viewMode = agendaViewType;
				} else {				
					item.viewMode = agendaViewType;				
				}
			}
		}
		
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
