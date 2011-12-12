
package uk.ac.ed.timetable.agenda;


import java.util.*;
import uk.ac.ed.timetable.Timetable;
import uk.ac.ed.timetable.dataview.DataView;
import uk.ac.ed.timetable.dataview.DataViewItem;
import uk.ac.ed.timetable.views.ViewWeekDayItem;
import android.graphics.Paint;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;


public class AgendaViewWeek extends AgendaView
{
	//fields
	private ArrayList<ViewWeekDayItem> vecDayItems = new ArrayList<ViewWeekDayItem>();
	protected LinearLayout llayParentWeek = null;
	Calendar calWeekStart = Calendar.getInstance();
	
	//fields
	private Paint mpt = new Paint();
	
	//methods
	public AgendaViewWeek(Timetable main)
	{
		super(main);
		
		LinearLayout.LayoutParams layParams =
			new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, main.GetViewSpaceHeight());
		
		//create parent content layout for week view (don't know why not works for llayParent
		llayParentWeek = new LinearLayout(main);
		llayParentWeek.setPadding(0, 0, 0, 0);
		llayParentWeek.setOrientation(LinearLayout.HORIZONTAL);
		llayParentWeek.setLayoutParams(layParams);
	}
	
	@Override
	public int GetViewType()
	{
		return viewMode.WEEK;
	}
	
	@Override
	public int GetViewIndex()
	{
		return 2;
	}
	
  private ViewWeekDayItem.OnItemClick mWeekDayItemClick = new ViewWeekDayItem.OnItemClick()
  {
		public void OnClick(ViewWeekDayItem item)
		{
			OnDayItemClick(item);
		}
  };
	
	@Override
	public void Rebuild()
	{
		llayParent.removeAllViews();
		llayParentWeek.removeAllViews();
		
    final int iParentWidth = main.GetViewSpaceWidth();
    final int iParentHeight = main.GetViewSpaceHeight() - 5;
    int iDefaultWidth = 0;
    int iWidth = 0;
    
    final int iSpaceWidthTime = ViewWeekDayItem.GetSpaceWidthTime(mpt, main.prefs.b24HourMode);
    final int iSpaceHeightHeader = ViewWeekDayItem.GetSpaceHeightHeader(mpt);

		//build 7 days week view
		vecDayItems.clear();
		for (int iDay = 0; iDay < 7; iDay++)
		{
    	ViewWeekDayItem item = new ViewWeekDayItem(main, iSpaceHeightHeader);
    	
    	//set event
    	item.SetItemClick(mWeekDayItemClick);
    	
    	//set size
    	if (iDay == 0)
    	{
    			item.SetTimeMargin(iSpaceWidthTime, main.prefs.b24HourMode);
        	iDefaultWidth = (iParentWidth - iSpaceWidthTime) / 7;
        	iWidth = iDefaultWidth + iSpaceWidthTime;
    	} else {
        	iWidth = iDefaultWidth;
    	}
    	item.SetSize(iWidth, iParentHeight);
    	
    	//add to layout
			vecDayItems.add(item);
			llayParentWeek.addView(item);			
		}

		llayParent.addView(llayParentWeek);
	}
	
	public void OnDayItemClick(ViewWeekDayItem item)
	{
		Calendar calDate = Calendar.getInstance();
		calDate.setTimeInMillis(item.GetDate().getTimeInMillis());
		main.SetAgendaView(AgendaView.viewMode.DAY, calDate);
	}
		
	@Override
	public void RebuildViewCourses(DataView dataView)
	{
		final boolean bIsViewToday = IsViewToday();
		final int iCurrentHour = getTodayCurrentHour();
		
		calWeekStart.setTimeInMillis(GetViewStartDate().getTimeInMillis());		
		calWeekStart.setFirstDayOfWeek(main.prefs.iFirstDayOfWeek);

		//setup weekdays data
		Calendar calDateToday = main.getDateToday();
		for (int iDay = 0; iDay < 7; iDay++)
		{
    	ViewWeekDayItem item = vecDayItems.get(iDay);    	    
			item.ClearTimeItems();
    	item.SetDate(calWeekStart, calDateToday);
			item.SetCurrentHour((bIsViewToday)?iCurrentHour:-1);    	
    	calWeekStart.add(Calendar.DAY_OF_YEAR, 1);
		}
				
		//rebuild view
		for (int i = 0; i < dataView.GetRowsCountTotal(); i++)
		{
			//get data item
			DataViewItem row = dataView.GetRow(i, this.GetViewType());
			if (row != null)
			{
				//iterate repeat week days in data item
				for (int iDay = 0; iDay < 7; iDay++)
				{
					//get item visible date
					if (row.GetVisibleDay(iDay))
					{
						//get week day item
						ViewWeekDayItem item = vecDayItems.get(iDay);
						item.AddTimeItem(row.GetStartHour(), row.GetStartMinute(), row.iDurationInMinutes);
					}
				}					
				
			}
		}
		
		llayParentWeek.requestLayout();
		llayParentWeek.invalidate();
	}

	@Override
	public void RebuildViewNotes(DataView dataView)
	{
	}

	@Override
	public void RebuildViewAssignments(DataView dataView)
	{
	}

	@Override
	public void UpdateTimeFormat()
	{		
	  int iParentWidth = main.GetViewSpaceWidth();
		int iParentHeight = main.GetViewSpaceHeight();
	  int iDefaultWidth = 0;
	  int iWidth = 0;
	  
    final int iSpaceWidthTime = ViewWeekDayItem.GetSpaceWidthTime(mpt, main.prefs.b24HourMode);
	  
		for (int iDay = 0; iDay < 7; iDay++)
		{
	  	ViewWeekDayItem item = vecDayItems.get(iDay);    	
	  	if (iDay == 0)
	  	{
  				item.SetTimeMargin(iSpaceWidthTime, main.prefs.b24HourMode);
	      	iDefaultWidth = (iParentWidth - iSpaceWidthTime) / 7;
	      	iWidth = iDefaultWidth + iSpaceWidthTime;
	  	} else {
	      	iWidth = iDefaultWidth;    	
	  	}
	  	item.SetSize(iWidth, iParentHeight);
		}				
	}
	
}
