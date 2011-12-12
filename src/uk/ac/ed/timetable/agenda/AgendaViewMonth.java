
package uk.ac.ed.timetable.agenda;


import java.util.ArrayList;
import java.util.Calendar;
import uk.ac.ed.timetable.Timetable;
import uk.ac.ed.timetable.dataview.DataView;
import uk.ac.ed.timetable.dataview.DataViewItem;
import uk.ac.ed.timetable.views.ViewMonthWeekItem;
import android.graphics.Paint;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;


public class AgendaViewMonth extends AgendaView
{
	//fields
	private ArrayList<ViewMonthWeekItem> vecWeekItems = new ArrayList<ViewMonthWeekItem>();
	protected LinearLayout llayParentMonth = null;
	private Calendar calMonthStart = Calendar.getInstance();

	//fields
	private Paint mpt = new Paint();

	//month
	public AgendaViewMonth(Timetable main)
	{
		super(main);
		
		LinearLayout.LayoutParams layParams =
			new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, main.GetViewSpaceHeight());		
		
		//create parent content layout for week view (don't know why not works for llayParent
		llayParentMonth = new LinearLayout(main);
		llayParentMonth.setPadding(0, 0, 0, 0);
		llayParentMonth.setOrientation(LinearLayout.VERTICAL);
		llayParentMonth.setLayoutParams(layParams);		
	}
	
	@Override
	public int GetViewType()
	{
		return viewMode.MONTH;
	}
	
	@Override
	public int GetViewIndex()
	{
		return 3;
	}

  private ViewMonthWeekItem.OnItemClick mWeekItemClick = new ViewMonthWeekItem.OnItemClick()
  {
		public void OnClick(ViewMonthWeekItem item)
		{
			OnWeekItemClick(item);
		}
  };
	
	@Override
	public void Rebuild()
	{
		llayParent.removeAllViews();
		llayParentMonth.removeAllViews();
		
    final int iParentWidth = main.GetViewSpaceWidth();
  	int iParentHeight = 0;  	
  	
    final int iWeekNrMarginWidth = ViewMonthWeekItem.GetSpaceWidthWeekNr(mpt);
    final int iSpaceHeightHeader = ViewMonthWeekItem.GetSpaceHeightHeader(mpt); 	
		
		//build 6 weeks month view
		vecWeekItems.clear();
    for (int iWeek = 0; iWeek < 6; iWeek++)
    {
    	ViewMonthWeekItem item = new ViewMonthWeekItem(main, ((iWeek == 0)?iSpaceHeightHeader:0), iWeekNrMarginWidth);
    	
    	//set event
    	item.SetItemClick(mWeekItemClick);
    	
    	//set size
      if (iWeek == 0)
      {
        iParentHeight = (main.GetViewSpaceHeight() - iSpaceHeightHeader);        
        iParentHeight /= 6;
	     	item.SetSize(iParentWidth, iParentHeight + iSpaceHeightHeader);
      } else {
	     	item.SetSize(iParentWidth, iParentHeight);
      }    	
    	
    	//add to layout
     	vecWeekItems.add(item);
     	llayParentMonth.addView(item);
    }
				
    llayParent.addView(llayParentMonth);
	}
	
	public void OnWeekItemClick(ViewMonthWeekItem item)
	{
		Calendar calDate = Calendar.getInstance();
		calDate.setTimeInMillis(item.getWeekStartDate().getTimeInMillis());
		main.SetAgendaView(AgendaView.viewMode.WEEK, calDate);
	}
	
	@Override
	public void RebuildViewCourses(DataView dataView)
	{
		calMonthStart.setTimeInMillis(GetViewStartDate().getTimeInMillis());		
		calMonthStart.setFirstDayOfWeek(main.prefs.iFirstDayOfWeek);		
		
		//setup weeks data
		Calendar calDateToday = main.getDateToday();
    for (int iWeek = 0; iWeek < 6; iWeek++)
		{
			ViewMonthWeekItem item = vecWeekItems.get(iWeek);			
      final boolean bDaysHeader = (iWeek == 0);
    	item.SetWeekStartDate(bDaysHeader, calMonthStart, GetCurrentSelectedMonth(), calDateToday);      
    	calMonthStart.add(Calendar.WEEK_OF_YEAR, 1);
		}
        
 		//rebuild view
		for (int i = 0; i < dataView.GetRowsCountTotal(); i++)
		{
			//get data item
			DataViewItem row = dataView.GetRow(i, this.GetViewType());
			if (row != null)
			{
				int iDay = 0;
		    for (int iWeek = 0; iWeek < 6; iWeek++)
		    {
	  			ViewMonthWeekItem item = vecWeekItems.get(iWeek);	  			
		    	for (int iWeekDay = 0; iWeekDay < 7; iWeekDay++)
		    	{
		    		if (row.GetVisibleDay(iDay))
		    			item.setDayData(iWeekDay, true);
		    		iDay++;
		    	}		    	
		    }		    
			}
		}
		
    llayParentMonth.requestLayout();
    llayParentMonth.invalidate();		
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
	}
	
}
