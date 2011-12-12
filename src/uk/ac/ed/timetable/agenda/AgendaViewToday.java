
package uk.ac.ed.timetable.agenda;


import java.util.*;

import uk.ac.ed.timetable.Timetable;
import uk.ac.ed.timetable.R;
import uk.ac.ed.timetable.dataview.DataView;
import uk.ac.ed.timetable.dataview.DataViewItem;
import uk.ac.ed.timetable.views.ViewTodayItemCourse;
import uk.ac.ed.timetable.views.ViewTodayItemHeader;
import uk.ac.ed.timetable.views.ViewTodayItemNote;
import uk.ac.ed.timetable.views.ViewTodayItemAssignment;
import android.view.View;
import android.widget.LinearLayout;
import android.graphics.*;


public class AgendaViewToday extends AgendaView
{	
	//fields
	private Vector<ViewTodayItemHeader> tdhitems = new Vector<ViewTodayItemHeader>();
	
	//fields
	private final int iTopPadding = 2;
	private final int iBottomPadding = 2;
	private final int iHeadAppts = 0;
	private final int iHeadAssignments = 1;
	private final int iHeadNotes = 2;
	
	//fields
	private int iSpaceWidthTime = 0;
	private int iSpaceWidthMinutes = 0; 
	private int iSpaceWidthUSTimeMark = 0;
	
	//fields
	private Paint mpt = new Paint();
	
	//methods
	public AgendaViewToday(Timetable main)
	{
		super(main);
		iSpaceWidthTime = ViewTodayItemCourse.GetSpaceWidthTime(mpt);
		iSpaceWidthMinutes = ViewTodayItemCourse.GetSpaceWidthMinutes(mpt);
		iSpaceWidthUSTimeMark = ViewTodayItemCourse.GetSpaceWidthUSTimeMark(mpt);
	}

	@Override
	public int GetViewType()
	{
		return viewMode.TODAY;
	}
	
	@Override
	public int GetViewIndex()
	{
		return 0;
	}
	
	@Override
	public void Rebuild()
	{
		llayParent.removeAllViews();

		ViewTodayItemHeader tdhi = null;
		
		//create today item headers
		for (int i = 0; i < 3; i++)
			tdhitems.add(new ViewTodayItemHeader(main));
		
		//init courses
		tdhi = InitHeaderItem(iHeadAppts, ViewTodayItemHeader.ViewType.Courses, R.string.labTodayItemCourses);		
  	llayParentAppt.addView(tdhi, lparams);

		//init assignments
		tdhi = InitHeaderItem(iHeadAssignments, ViewTodayItemHeader.ViewType.Assignments, R.string.labTodayItemAssignments);
  	llayParentAssignment.addView(tdhi, lparams);

		//init notes
		tdhi = InitHeaderItem(iHeadNotes, ViewTodayItemHeader.ViewType.Notes, R.string.labTodayItemNotes);
  	llayParentNote.addView(tdhi, lparams);
  	
  	llayParent.addView(llayParentAppt, lparams);
  	llayParent.addView(llayParentAssignment, lparams);
  	llayParent.addView(llayParentNote, lparams);
	}

	public ViewTodayItemHeader InitHeaderItem(int index, ViewTodayItemHeader.ViewType type, int iResStrId)
	{
		ViewTodayItemHeader tdhi = null;
		//init courses
		tdhi = tdhitems.get(index);
		tdhi.SetType(type);
		tdhi.SetText(main.utils.GetResStr(iResStrId));
		tdhi.SetInfoText(sTextNone);
		tdhi.setPadding(0, iTopPadding, 0, iBottomPadding);
		//set event
		tdhi.SetItemClick(new ViewTodayItemHeader.OnHeaderItemClick() {
			public void OnClick(View v, ViewTodayItemHeader.ViewType type) {
				doHeaderItemClick(v, type);
			}
		});
		return tdhi;
	}
	
	public void RemoveChildViewsFromHeader(LinearLayout llay)
	{
		while (llay.getChildCount() > 1)
		{
			View v = llay.getChildAt(1);
			if (v == null)
				break;
			if (v.getClass() != ViewTodayItemHeader.class)
				llay.removeViewInLayout(v);
		}
		llayParent.invalidate();
	}

	public void UpdateInfoText(ViewTodayItemHeader tdhi, int iRowsCount)
	{
		if (iRowsCount == 0)
		{
			tdhi.SetInfoText(sTextNone);			
		} else {
			tdhi.SetInfoText(Integer.toString(iRowsCount));
		}
		
	}
	
	@Override
	public void RebuildViewCourses(DataView dataView)
	{
		RemoveChildViewsFromHeader(llayParentAppt);
		
		for (int i = 0; i < dataView.GetRowsCountTotal(); i++)
		{
			DataViewItem row = dataView.GetRow(i, this.GetViewType());
			if (row != null)
			{
				ViewTodayItemCourse item = new ViewTodayItemCourse(main);
				item.SetRowId(row.lID);
				item.SetItemTime(row.GetStartHour(), row.GetStartMinute(), false, main.prefs.b24HourMode, iSpaceWidthTime, iSpaceWidthMinutes, iSpaceWidthUSTimeMark);
				item.SetItemData(row.sSubject, row.bAlarm, row.IsRepeat());
				item.SetItemClick(onApptItemClick);
				llayParentAppt.addView(item, lparams);
			}
		}
		UpdateInfoText(tdhitems.get(iHeadAppts), dataView.GetRowsCountForView(this.GetViewType()));
	}

	@Override
	public void RebuildViewAssignments(DataView dataView)
	{
		RemoveChildViewsFromHeader(llayParentAssignment);
		
		for (int i = 0; i < dataView.GetRowsCountTotal(); i++)
		{
			DataViewItem row = dataView.GetRow(i, this.GetViewType());
			if (row != null)
			{
				ViewTodayItemAssignment item = new ViewTodayItemAssignment(main);
				item.SetRowId(row.lID);
				item.SetItemData(row.bDone, row.sSubject, row.bAlarm);
				item.SetItemClick(onAssignmentItemClick);
				llayParentAssignment.addView(item, lparams);
			}
		}			
		UpdateInfoText(tdhitems.get(iHeadAssignments), dataView.GetRowsCountForView(this.GetViewType()));
	}

	@Override
	public void RebuildViewNotes(DataView dataView)
	{
		RemoveChildViewsFromHeader(llayParentNote);
		
		for (int i = 0; i < dataView.GetRowsCountTotal(); i++)
		{
			DataViewItem row = dataView.GetRow(i, this.GetViewType());
			if (row != null)
			{
				ViewTodayItemNote item = new ViewTodayItemNote(main);
				item.SetRowId(row.lID);
				item.SetItemData(row.sSubject);
				item.SetItemClick(onNoteItemClick);
				llayParentNote.addView(item, lparams);
			}
		}			
		UpdateInfoText(tdhitems.get(iHeadNotes), dataView.GetRowsCountForView(this.GetViewType()));
	}

	@Override
	public void UpdateTimeFormat()
	{
	}
	
}
