
package uk.ac.ed.timetable.reminder;


import java.util.*;
import android.content.Context;
import uk.ac.ed.timetable.Utils;
import uk.ac.ed.timetable.database.*;
import uk.ac.ed.timetable.dataview.*;
import uk.ac.ed.timetable.agenda.*;


public class AlarmDataView
{
	//Comparator type for courses
	public class ApptsItemsComparator implements Comparator<AlarmDataViewItem>
	{
		public int compare(AlarmDataViewItem item1, AlarmDataViewItem item2)
		{
			if (item1.IsOverdue() || item2.IsOverdue())
			{
				//overdue items for sorting				
				if (item1.GetOverdueDays() < item2.GetOverdueDays())
					return 1;
				if (item1.GetOverdueDays() > item2.GetOverdueDays())
					return -1;
				
				if (item1.GetOverdueDays() == item2.GetOverdueDays())
				{
					String s1 = item1.sSubject;
					String s2 = item2.sSubject;				
					return s1.compareTo(s2);				
				}
				
				return 0;				
			} else {
				//today items for sorting
				if (item1.TimeAsSeconds() == item2.TimeAsSeconds())
				{
					String s1 = item1.sSubject;
					String s2 = item2.sSubject;				
					return s1.compareTo(s2);
				} else {
					
					if (item1.TimeAsSeconds() > item2.TimeAsSeconds())
						return 1;
					if (item1.TimeAsSeconds() < item2.TimeAsSeconds())
						return -1;
	
					if (item1.TimeAsSeconds() == item2.TimeAsSeconds())
					{
						String s1 = item1.sSubject;
						String s2 = item2.sSubject;
						return s1.compareTo(s2);
					}
				}
			
			}
			return 0;
		}		
	};

	//Comparator type for assignments
	public class AssignmentsItemsComparator implements Comparator<AlarmDataViewItem>
	{
		public int compare(AlarmDataViewItem item1, AlarmDataViewItem item2)
		{
			if (item1.lPriority == item2.lPriority)
			{
				String s1 = item1.sSubject;
				String s2 = item2.sSubject;
				return s1.compareTo(s2);
			} else {
				
				if (item1.lPriority > item2.lPriority)
					return 1;
				if (item1.lPriority < item2.lPriority)
					return -1;

				if (item1.lPriority == item2.lPriority)
				{
					String s1 = item1.sSubject;
					String s2 = item2.sSubject;
					return s1.compareTo(s2);
				}
			}			
			return 0;
		}		
	};
	
	
	//fields
	protected ArrayList<AlarmDataViewItem> alarmItemsAppts = new ArrayList<AlarmDataViewItem>();	
	protected ArrayList<AlarmDataViewItem> alarmItemsAssignments = new ArrayList<AlarmDataViewItem>();	
	private ApptsItemsComparator fnApptsItemsCmp = null;	
	private AssignmentsItemsComparator fnAssignmentsItemsCmp = null;	
	
	//fields
	protected Context ctx = null;
	protected Database db = null;
	protected Utils utils = null;
	protected AlarmService.AlarmPrefs prefs = null;
	
	//fields
	private  DataViewCourse dataViewAppt = null;
	private  DataViewAssignment dataViewAssignment = null;

	//fields
	private final int agendaViewType = AgendaView.viewMode.TODAY_ALARM;  
	private Calendar calStartDate = Calendar.getInstance();
	private AlarmsManager alarmsManager = null;

	//methods
	public AlarmDataView(Context ctx, Database db, AlarmService.AlarmPrefs prefs, Utils utils)
	{
		this.ctx = ctx;
		this.db = db;
		this.prefs = prefs;
		this.utils = utils;
		
    //create alarsm manager instance
  	alarmsManager = new AlarmsManager(this.db, this.prefs);
		
		this.fnApptsItemsCmp = new ApptsItemsComparator();		
		this.fnAssignmentsItemsCmp = new AssignmentsItemsComparator();		

		dataViewAppt = new DataViewCourse(db, prefs);
		dataViewAssignment = new DataViewAssignment(db, prefs);
	}
	
	private boolean ReloadData()
	{
  	if (db.DatabaseReady())
  	{
  		dataViewAppt.ReloadTable();
  		dataViewAssignment.ReloadTable();
			
	    dataViewAppt.FilterData(calStartDate, agendaViewType);
    	dataViewAssignment.FilterData(calStartDate, agendaViewType);
    	
    	return true;
  	}
  	
  	return false;
	}
		
	public boolean CollectAlarmItems()
	{
		calStartDate.setTimeInMillis(System.currentTimeMillis());
		
		final int iHour = calStartDate.get(Calendar.HOUR_OF_DAY);  
		final int iMinute = calStartDate.get(Calendar.MINUTE);
		final int iCurrTimeKey = (iHour * 100) + iMinute;			
		
		alarmItemsAppts.clear();
		alarmItemsAssignments.clear();
		
		if (ReloadData())
		{
			
			//collect courses
			for (int i = 0; i < dataViewAppt.GetRowsCountTotal(); i++)
			{
				DataViewItem row = dataViewAppt.GetRow(i, agendaViewType);
				if ((row != null) && (row.bAlarm))
				{
					if (row.TimeOverdue(iCurrTimeKey))
					{						
						if (alarmsManager.CanShowAlarmToday(AlarmDataViewItem.iOrderAppts, row.lID, row.IsRepeat()))
						{							
							AlarmDataViewItem item = new AlarmDataViewItem(row.lID, row.sSubject, AlarmDataViewItem.iOrderAppts, row.bAlarm);
							item.SetRepeatDays(row.GetVisibleDays());
							item.Set(row.GetStartHour(), row.GetStartMinute(), row.GetDuration());
							alarmItemsAppts.add(item);
						}
					}
				}
			}
			
			//collect assignments
			for (int i = 0; i < dataViewAssignment.GetRowsCountTotal(); i++)
			{
				DataViewItem row = dataViewAssignment.GetRow(i, agendaViewType);
				if ((row != null) && (row.bAlarm))
				{					
					if (alarmsManager.CanShowAlarmToday(AlarmDataViewItem.iOrderAssignments, row.lID, row.IsRepeat()))
					{					
						AlarmDataViewItem item = new AlarmDataViewItem(row.lID, row.sSubject, AlarmDataViewItem.iOrderAssignments, row.bAlarm);
						item.Set(row.GetPriority());
						alarmItemsAssignments.add(item);
					}
				}
			}
			
			Collections.sort(alarmItemsAppts, fnApptsItemsCmp);
			Collections.sort(alarmItemsAssignments, fnAssignmentsItemsCmp);
			
			return true;
		}
		return false;
	}

	public ArrayList<AlarmDataViewItem> GetApptsData()
	{
		return alarmItemsAppts;
	}

	public ArrayList<AlarmDataViewItem> GetAssignmentsData()
	{
		return alarmItemsAssignments;
	}

}
