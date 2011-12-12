/**
 * Main activity
 */
package uk.ac.ed.timetable;


import java.util.*;
import java.text.*;
import uk.ac.ed.timetable.agenda.*;
import uk.ac.ed.timetable.database.*;
import uk.ac.ed.timetable.dataview.*;
import uk.ac.ed.timetable.views.*;
import uk.ac.ed.timetable.reminder.AlarmService;
import uk.ac.ed.widgets.*;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.*;
import android.widget.LinearLayout.*;
import android.content.Intent;
import android.os.*;
import android.provider.Settings;
import android.content.*;
import android.content.res.Configuration;


//Main activity
public class Timetable extends CommonActivity
{
	//fields
	
	protected Timetable base = null;
	private Calendar dateToday = Calendar.getInstance();
	private SimpleDateFormat dateFormatFull = new SimpleDateFormat("EE, dd-MM-yyyy");
	private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM yyyy");
	
	//fields
  private int iCurrentAgendaViewType = AgendaView.viewMode.TODAY; 
  
  //menu items
  private final int miNewAppt = Menu.FIRST;
  private final int miNewAssignment = Menu.FIRST + 1;
  private final int miNewNote = Menu.FIRST + 2;
  private final int miShowAllAssignments = Menu.FIRST + 3;
  private final int miOptions = Menu.FIRST + 4;
  private final int mTimeZone = Menu.FIRST + 5;
  private final int miAbout = Menu.FIRST + 6;
	
	//views
  private ScrollView scrollViewAgenda = null;
	protected RelativeLayout rlayAgendaTop = null;
	
	@SuppressWarnings("all")
	protected RelativeLayout rlayAgenda = null;
	
	@SuppressWarnings("all")
	protected RelativeLayout rlayAgendaView = null;	
	protected LinearLayout llayAgendaData = null;		
	
	//views
	private AgendaView CurrentAgendaView = null;
	private AgendaViewToday AgendaViewToday = null;
	private AgendaViewDay AgendaViewDay = null;
	private AgendaViewWeek AgendaViewWeek = null;
	private AgendaViewMonth AgendaViewMonth = null;

	//fields
	protected  DataViewCourse dataViewAppt = null;
	protected  DataViewAssignment dataViewAssignment = null;
	protected  DataViewNote dataViewNote = null;
	
	//fields
	private Handler handlerUpdateDate = new Handler();
	private Handler handlerUpdateView = new Handler();	
	private final static int iHandlerUpdateTime = 1000 * 5;
	private int iUpdateDate_minute = 0;
	
	//views
	protected TextView labWeekStr = null;
	protected TextView labWeekNr = null;
	
	private TextView labSelectViewItem = null;	
	private ViewImgButton btnSelectViewItemPrev = null;
	private Button btnSelectViewItemToday = null;
	private ViewImgButton btnSelectViewItemNext = null;
	
	//bottom buttons
	private Button btnSetViewToday = null;
	private Button btnSetViewDay = null;
	private Button btnSetViewWeek = null;
	private Button btnSetViewMonth = null;
  //methods
  @Override
  public void onCreate(Bundle icicle)
  {
  	super.onCreate(icicle);
	  base = this;
	  setContentView(R.layout.agenda);
	  
	  InitViews();
		InitStateOnce();
		StartReminderService();
  }

  @Override
  public void onStart()
  {
  	super.onStart();
  	
  }
  
  @Override
  public void onResume()
  {
  	super.onResume();
	  InitViews();
		InitStateOnce();
  }
    
  @Override
  public void onPause()
  {
  	super.onPause();
  	
  }
  
  @Override
  public void onStop()
  {
  	super.onStop();

  }
  
  @Override
  public void onDestroy()
  {
  	super.onDestroy();
  	handlerUpdateDate.removeCallbacks(handlerUpdateDateAssignment);
  }
  
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
      super.onCreateOptionsMenu(menu);

      //build menu
      MenuItem mi = menu.add(0, miNewAppt, 0, R.string.actionNewCourse);
      mi.setShortcut('1', 'a');
      mi.setIcon(R.drawable.menuiconappt);
      
      mi = menu.add(0, miNewAssignment, 1, R.string.actionNewAssignment);
      mi.setShortcut('2', 't');
      mi.setIcon(R.drawable.menuiconassgs);
      
      mi = menu.add(0, miNewNote, 2, R.string.actionNewNote);      
      mi.setShortcut('3', 'n');
      mi.setIcon(R.drawable.menuiconnote);
            
      mi = menu.add(2, miShowAllAssignments, 3, R.string.actionShowAllAssignments);
      mi.setCheckable(true);
      mi.setChecked(prefs.bShowAllAssignments);
      menuItemUpdateIcons(mi);
            
      mi = menu.add(4, miOptions, 4, R.string.actionOptions);
      mi.setShortcut('5', 'o');
      mi.setIcon(R.drawable.menuiconprefs);
      
      mi = menu.add(4, mTimeZone, 5, R.string.TimeZone);
      mi.setIcon(R.drawable.menuiconabout);
      
      mi = menu.add(4, miAbout, 6, R.string.actionAbout);
      mi.setIcon(R.drawable.menuiconabout);
      
      return true;
  }  

  public void menuItemUpdateIcons(MenuItem item)
  {
  	if (item.getItemId() == miShowAllAssignments)
	  	if (item.isChecked())
	  	{
	  		item.setIcon(R.drawable.menuiconshowassgsdue);
	  	} else {
	  		item.setIcon(R.drawable.menuiconshowassgs);  		
	  	}  	
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
      switch (item.getItemId())
      {
	      case miNewAppt:
	      	openActCourse(-1, -1, -1);
	      	break;
	      case miNewAssignment:
	      	openActAssignment(-1);
	      	break;
	      case miNewNote:  
	      	openActNote(-1);
	      	break;
	      case miShowAllAssignments:
	      {
	      	item.setChecked(!item.isChecked());	      	
	      	prefs.bShowAllAssignments = item.isChecked();
	      	prefs.Save();
	      	RefreshData();
	      	menuItemUpdateIcons(item);
	      	break;
	      }
	      case miOptions:
	      	openActOptions();
	      	break;
	      case mTimeZone:
	      	showTimeZone();
	      	break;
	      case miAbout:
	      	openActViewAbout();
	      	break;
      }
      return super.onOptionsItemSelected(item);
  }
  
  //initialize views
  private void InitViews()
  {
  	//localize DateWidget
  	DateWidget.setStrings(utils.GetResStr(R.string.strDateWidgetSelect), 
  			utils.GetResStr(R.string.strDateWidgetSelected), 
  			utils.GetResStr(R.string.strDateWidgetNone));
  	
  	//localize TimeWidget
  	TimeWidget.setStrings(utils.GetResStr(R.string.strTimeWidgetSelect),
  			utils.GetResStr(R.string.strTimeWidgetNone),
  			utils.GetResStr(R.string.strTimeWidgetSet));
  	
  	rlayAgendaTop = (RelativeLayout)findViewById(R.id.rlayAgendaTop);
  	rlayAgenda = (RelativeLayout)findViewById(R.id.rlayAgenda);
  	rlayAgendaView = (RelativeLayout)findViewById(R.id.rlayAgendaView);
  	
  	llayAgendaData = (LinearLayout)findViewById(R.id.llayAgendaData);
  	
  	btnSelectViewItemPrev = (ViewImgButton)findViewById(R.id.btnSelectViewItemPrev);
  	btnSelectViewItemPrev.SetButtonIcon(R.drawable.btnprev, -1);
  	btnSelectViewItemPrev.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CurrentAgendaView.SetPrevViewItem();
				RefreshAgendaAfterViewItemChange();
			}		
  	});
  	
  	btnSelectViewItemToday = (Button)findViewById(R.id.btnSelectViewItemToday);
  	btnSelectViewItemToday.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CurrentAgendaView.SetTodayViewItem();
				RefreshAgendaAfterViewItemChange();
			}		
  	});  	

  	btnSelectViewItemNext = (ViewImgButton)findViewById(R.id.btnSelectViewItemNext);
  	btnSelectViewItemNext.SetButtonIcon(R.drawable.btnnext, 0);
  	btnSelectViewItemNext.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CurrentAgendaView.SetNextViewItem();
				RefreshAgendaAfterViewItemChange();
			}		
  	});  	
  	
  	labWeekStr = (TextView)findViewById(R.id.labWeekStr);
  	labWeekNr = (TextView)findViewById(R.id.labWeekNr);
  	
  	labSelectViewItem = (TextView)findViewById(R.id.labSelectViewItem);
  	
  	//initialize change view bottom buttons
  	btnSetViewToday = (Button)findViewById(R.id.btnSetViewToday);
  	btnSetViewToday.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
     		//ToggleBottomButtonsState(btnSetViewToday);      		
     		SetAgendaView(AgendaView.viewMode.TODAY, LatestDateToday());
			}		
  	});  	

  	btnSetViewDay = (Button)findViewById(R.id.btnSetViewDay);
  	btnSetViewDay.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
     		//ToggleBottomButtonsState(btnSetViewDay);
     		SetAgendaView(AgendaView.viewMode.DAY, LatestDateToday());
			}		
  	});  	

  	btnSetViewWeek = (Button)findViewById(R.id.btnSetViewWeek);
  	btnSetViewWeek.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
     		//ToggleBottomButtonsState(btnSetViewWeek);
     		SetAgendaView(AgendaView.viewMode.WEEK, LatestDateToday());
			}		
  	});  	

  	btnSetViewMonth = (Button)findViewById(R.id.btnSetViewMonth);
  	btnSetViewMonth.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
     		//ToggleBottomButtonsState(btnSetViewMonth);
     		SetAgendaView(AgendaView.viewMode.MONTH, LatestDateToday());
			}		
  	});
  	
  	
  	//initialize data views
		dataViewAppt = new DataViewCourse(userdb, prefs);
		dataViewAssignment = new DataViewAssignment(userdb, prefs);
		dataViewNote = new DataViewNote(userdb, prefs);
  			
		InitAgendaViewToday();
  }
  
  public void ToggleBottomButtonsState(CompoundButton btnClicked)
  {
  	btnClicked.setEnabled(false);
  	btnClicked.requestFocus();
  	
  	if (btnClicked != btnSetViewToday)
  		btnSetViewToday.setEnabled(true);
  	if (btnClicked != btnSetViewDay)
  		btnSetViewDay.setEnabled(true);
  	if (btnClicked != btnSetViewWeek)
  		btnSetViewWeek.setEnabled(true);
  	if (btnClicked != btnSetViewMonth)
  		btnSetViewMonth.setEnabled(true);
  }
  
  public void UpdateBottomButtonsStateByCurrentView()
  {
  	/*
    if (iCurrentAgendaViewType == AgendaView.viewMode.TODAY)
    	ToggleBottomButtonsState(btnSetViewToday);
    if (iCurrentAgendaViewType == AgendaView.viewMode.DAY)
   		ToggleBottomButtonsState(btnSetViewDay);
    if (iCurrentAgendaViewType == AgendaView.viewMode.WEEK)
   		ToggleBottomButtonsState(btnSetViewWeek);
    if (iCurrentAgendaViewType == AgendaView.viewMode.MONTH)
    	ToggleBottomButtonsState(btnSetViewMonth);
    	*/  	
  }

  public void InitAgendaViewToday()
  {
  	//initialize today agenda view
  	AgendaViewToday = new AgendaViewToday(this);
  	AgendaViewToday.Rebuild();
	
  	//set click event for all agenda views
  	AgendaView.SetItemClick(new AgendaView.OnViewItemClick() {
  		public void OnClick(View v, Bundle extras)
  		{
  			long lRowId = extras.getLong(CommonActivity.bundleRowId);
  			if (extras.getString("type").equals(ViewTodayItemHeader.ViewType.Courses.toString()))
  			{
  				if (extras.containsKey(CommonActivity.bundleHourOfDay))
  				{
  					openActCourse(lRowId, 
  							extras.getInt(CommonActivity.bundleHourOfDay),
  							extras.getInt(CommonActivity.bundleMinutes));
  				} else {
  					openActCourse(lRowId, -1, -1);
  				}
  			}
  			if (extras.getString("type").equals(ViewTodayItemHeader.ViewType.Assignments.toString()))
  				openActAssignment(lRowId);
  			if (extras.getString("type").equals(ViewTodayItemHeader.ViewType.Notes.toString()))
  				openActNote(lRowId);
  		}
  	});
	  	
  	//initialize all other agenda views
  	AgendaViewDay = new AgendaViewDay(Timetable.this);
  	AgendaViewDay.Rebuild();

  	AgendaViewWeek = new AgendaViewWeek(Timetable.this);
  	AgendaViewWeek.Rebuild();
  	
  	AgendaViewMonth = new AgendaViewMonth(Timetable.this);  	
  	AgendaViewMonth.Rebuild();

  	//initialize scrollable content
  	scrollViewAgenda = new ScrollView(this);
  	LayoutParams layParams = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.FILL_PARENT); 
  	scrollViewAgenda.setLayoutParams(layParams);  	
  }
  
  private void InitStateOnce()
  {
		ReloadAllDataTables();
   	//ToggleBottomButtonsState(btnSetViewToday);   		
		SetAgendaView(AgendaView.viewMode.TODAY, LatestDateToday());
		
		//refresh view (relayout bug)
		ForceUpdateLayout();
		
		//schedule handler update date assignment
		handlerUpdateDate.removeCallbacks(handlerUpdateDateAssignment);
		handlerUpdateDate.postDelayed(handlerUpdateDateAssignment, iHandlerUpdateTime);

		//focus default button
		btnSetViewToday.requestFocus();
  }
    
  private void ForceUpdateLayout()
  {
		handlerUpdateView.removeCallbacks(handlerUpdateViewAssignment);
		handlerUpdateView.postDelayed(handlerUpdateViewAssignment, 100);
  }
  
  private Runnable handlerUpdateViewAssignment = new Runnable()
  {
    public void run()
    {
    	rlayAgenda.postInvalidate();
    	rlayAgenda.requestLayout();
    	rlayAgendaView.postInvalidate();
      rlayAgendaView.requestLayout(); 
    }
  };
  
  private Runnable handlerUpdateDateAssignment = new Runnable()
  {
    public void run()
    {
    	try
    	{
    		UpdateTodayDate();
    		
    		//refresh data, if system timer minute changed
    		if (iUpdateDate_minute != dateToday.get(Calendar.MINUTE))
    		{
    			iUpdateDate_minute = dateToday.get(Calendar.MINUTE);
    			
    	    //autorefresh only today view
    	    if (iCurrentAgendaViewType == AgendaView.viewMode.TODAY)
    	    {
    	      if (CurrentAgendaView != null)
    	      	CurrentAgendaView.SetViewStartDate(LatestDateToday());
    	    	RefreshData();
    	    }
    		}
    	} finally {
      	handlerUpdateDate.postDelayed(this, iHandlerUpdateTime);    		
    	}      
    }
  };
  
	//refresh start view date  
  public synchronized void UpdateTodayDate()
  {
  	dateToday.setTimeInMillis(System.currentTimeMillis());
  	dateToday.setFirstDayOfWeek(prefs.iFirstDayOfWeek);  	
  }
  
  public synchronized void RefreshData()
  {
  	//set title
  	SetActivityTitle(utils.GetLongDate(dateToday));
  	
  	//set week nr label
    SetWeekNrText(CurrentAgendaView.GetViewStartDate());
    
    //update weeknr view
    UpdateWeekNrInfoVisibility();
    
    //update view info view
    UpdateSelectViewText(iCurrentAgendaViewType);
    
    //update button text date
    UpdateCurrentViewItemDate();

    //reload data
  	if (userdb.DatabaseReady())
  	{
	    //filter data
	    dataViewAppt.FilterData(CurrentAgendaView.GetViewStartDate(), CurrentAgendaView.GetViewType());
	    if (iCurrentAgendaViewType == AgendaView.viewMode.TODAY)
	    {
	    	dataViewAssignment.FilterData(CurrentAgendaView.GetViewStartDate(), CurrentAgendaView.GetViewType());
	    	dataViewNote.FilterData(CurrentAgendaView.GetViewStartDate(), CurrentAgendaView.GetViewType());
	    }
	    //rebuild views
			CurrentAgendaView.RebuildViewCourses(dataViewAppt);
	    if (iCurrentAgendaViewType == AgendaView.viewMode.TODAY)
	    {
	    	CurrentAgendaView.RebuildViewAssignments(dataViewAssignment);
	    	CurrentAgendaView.RebuildViewNotes(dataViewNote);
	    }
  	}
  	
    //set scroll view top position
  	scrollViewAgenda.scrollTo(0, 0);  	
  }
  
  public void RefreshAgendaAfterViewItemChange()
  {
  	btnSelectViewItemToday.setText("...");
    SetWeekNrText(CurrentAgendaView.GetViewStartDate());
    UpdateWeekNrInfoVisibility();

    //filter data
    dataViewAppt.FilterData(CurrentAgendaView.GetViewStartDate(), CurrentAgendaView.GetViewType());

    //rebuild view
		CurrentAgendaView.RebuildViewCourses(dataViewAppt);
		
		UpdateCurrentViewItemDate();
  } 
  
  public void UpdateCurrentViewItemDate()
  {
		String s = "";
    if (iCurrentAgendaViewType == AgendaView.viewMode.DAY)  	
  		s = dateFormatFull.format(CurrentAgendaView.GetViewStartDate().getTime()).toString();
    if (iCurrentAgendaViewType == AgendaView.viewMode.WEEK)
  		s = dateFormatFull.format(CurrentAgendaView.GetViewStartDate().getTime()).toString();
    if (iCurrentAgendaViewType == AgendaView.viewMode.MONTH)
  		s = dateFormatMonth.format(CurrentAgendaView.GetCurrentSelectedMonthAsCalendar().getTime()).toString();
		btnSelectViewItemToday.setText(s);
  }
  
  //main program date holder
  public Calendar getDateToday()
  {
  	dateToday.setFirstDayOfWeek(prefs.iFirstDayOfWeek);
  	return dateToday;
  }

  public Calendar LatestDateToday()
  {
  	UpdateTodayDate();
  	return getDateToday();
  }
  
  public void SetWeekNrText(Calendar date)
  {
  	int iNr = Utils.getIso8601Calendar(date).get(Calendar.WEEK_OF_YEAR);
  	// convert the week to Edinburgh University week format
  	if(iNr >= 38 && iNr <= 50) {
  		iNr = iNr - 37;	
  	}
  	else if(iNr >= 2 && iNr <=12) {
  		iNr = iNr - 1;
  	}
  	else { 
  		// unchanged, in case we are not in semester
  		}
  	labWeekNr.setText(Integer.toString(iNr));
  }

  public void UpdateWeekNrInfoVisibility()
  {
    if (iCurrentAgendaViewType == AgendaView.viewMode.MONTH)
    {
    	labWeekStr.setVisibility(View.INVISIBLE);
    	labWeekNr.setVisibility(View.INVISIBLE);
    } else {
    	labWeekStr.setVisibility(View.VISIBLE);
    	labWeekNr.setVisibility(View.VISIBLE);    	
    }
  }
  
  public void ReloadDataTable(String sTableNameToReload)
  {
  	if (userdb.DatabaseReady())
  	{
  		if (sTableNameToReload.equals(Database.sTableNameCourses))
  			dataViewAppt.ReloadTable();
  		if (sTableNameToReload.equals(Database.sTableNameAssignments))
  			dataViewAssignment.ReloadTable();
  		if (sTableNameToReload.equals(Database.sTableNameNotes))
   			dataViewNote.ReloadTable();
  	}  	
  }

  public void ReloadAllDataTables()
  {
  	if (userdb.DatabaseReady())
  	{
			dataViewAppt.ReloadTable();
 			dataViewAssignment.ReloadTable();
 			dataViewNote.ReloadTable();
  	}
  }
    
  private void UpdateSelectViewText(int viewType)
  {
  	String s = "";
    if (iCurrentAgendaViewType == AgendaView.viewMode.DAY)
    	s = utils.GetResStr(R.string.labSelectViewDay);
    if (iCurrentAgendaViewType == AgendaView.viewMode.WEEK)
    	s = utils.GetResStr(R.string.labSelectViewWeek);
    if (iCurrentAgendaViewType == AgendaView.viewMode.MONTH)
    	s = utils.GetResStr(R.string.labSelectViewMonth);  	
  	labSelectViewItem.setText(s);
  }
  
  public synchronized void SetAgendaView(int viewType, Calendar calViewDate)
  {
  	if (userdb.DatabaseReady())
  	{
  		//init view
	    iCurrentAgendaViewType = viewType;
	    
	    ShowTopControls(iCurrentAgendaViewType != AgendaView.viewMode.TODAY);
	    
	    //change type
	    if (iCurrentAgendaViewType == AgendaView.viewMode.TODAY)
	    	CurrentAgendaView = AgendaViewToday;
	    if (iCurrentAgendaViewType == AgendaView.viewMode.DAY)
	    	CurrentAgendaView = AgendaViewDay;
	    if (iCurrentAgendaViewType == AgendaView.viewMode.WEEK)
	    	CurrentAgendaView = AgendaViewWeek;
	    if (iCurrentAgendaViewType == AgendaView.viewMode.MONTH)
	    	CurrentAgendaView = AgendaViewMonth;

	    if (CurrentAgendaView != null)
	    	CurrentAgendaView.SetViewStartDate(calViewDate);

	    //reload data
	    RefreshData();

	    //set view change buttons
	    UpdateBottomButtonsStateByCurrentView();       
	    	    
	  	VisibleLayoutContentRemove();  	
	  	VisibleLayoutContentAdd();
	    
  	} else {
			utils.ShowMsgResStr(Database.GetErrDesc(userdb.TablesCreationResult()), Utils.MSGTYPE_ERROR);  		
  	}
  }
  
  private void VisibleLayoutContentRemove()
  {
  	llayAgendaData.removeAllViews(); 
  	scrollViewAgenda.removeAllViews();  	  
  }
  
  private void VisibleLayoutContentAdd()
  {
  	if (iCurrentAgendaViewType == AgendaView.viewMode.TODAY)
	  {
	    scrollViewAgenda.addView(CurrentAgendaView.GetParentLayout());
	    llayAgendaData.addView(scrollViewAgenda);
	  }
	  if (iCurrentAgendaViewType == AgendaView.viewMode.DAY)
	  {
	    scrollViewAgenda.addView(CurrentAgendaView.GetParentLayout());
	    llayAgendaData.addView(scrollViewAgenda);
	  }
	  if (iCurrentAgendaViewType == AgendaView.viewMode.WEEK)
	  {
	    llayAgendaData.addView(CurrentAgendaView.GetParentLayout());	    
	  }
	  if (iCurrentAgendaViewType == AgendaView.viewMode.MONTH)
	  {
	    llayAgendaData.addView(CurrentAgendaView.GetParentLayout());
	  }
  }
      
  private void ShowTopControls(boolean bEnable)
  {
  	//enable
  	btnSelectViewItemPrev.setEnabled(bEnable);
  	btnSelectViewItemToday.setEnabled(bEnable);
  	btnSelectViewItemNext.setEnabled(bEnable);
		rlayAgendaTop.setEnabled(bEnable);

		//focus
  	btnSelectViewItemPrev.setFocusable(bEnable);
  	btnSelectViewItemToday.setFocusable(bEnable);
  	btnSelectViewItemNext.setFocusable(bEnable);
		rlayAgendaTop.setFocusable(bEnable);
		
		//visibility
  	btnSelectViewItemPrev.setVisibility((bEnable)?View.VISIBLE:View.INVISIBLE);
  	btnSelectViewItemToday.setVisibility((bEnable)?View.VISIBLE:View.INVISIBLE);
  	btnSelectViewItemNext.setVisibility((bEnable)?View.VISIBLE:View.INVISIBLE);  	

		//resize layout
  	MarginLayoutParams mlp = (MarginLayoutParams)rlayAgendaTop.getLayoutParams();
  	mlp.setMargins(0, 0, 0, 0);
  	rlayAgendaTop.getLayoutParams().height = 0;
  	if (bEnable)
  	{
  		rlayAgendaTop.getLayoutParams().height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
    	mlp.setMargins(0, 0, 0, 6);
  	}
  	
		//refresh view (relayout bug)
		ForceUpdateLayout();
  }
  
  protected void onSizeChanged(int w, int h, int oldw, int oldh)
  {
  	//todo...
  }
  
  public void openActCourse(long lRowId, int iHourOfDay, int iMinutes)
  {
  	bundleOtherDataStartup.clear();  	
  	if (lRowId == -1)
  	{
  		bundleOtherDataStartup.putInt(CommonActivity.bundleAgendaView, CurrentAgendaView.GetViewIndex());
  		bundleOtherDataStartup.putLong(CommonActivity.bundleAgendaViewStartDate, CurrentAgendaView.GetViewStartDate().getTimeInMillis());
  		
  		if (iHourOfDay != -1)
  			bundleOtherDataStartup.putInt(CommonActivity.bundleHourOfDay, iHourOfDay);
  		if (iMinutes != -1)
  			bundleOtherDataStartup.putInt(CommonActivity.bundleMinutes, iMinutes);
  				
 // 		OpenActivity(0, "android.intent.action.Timetable.ACTION_MODE_NEW_COURSE");
  			OpenActivity(0,"android.intent.action.Timetable.ACTION_MODE_VIEW_COURSE");
  	} else {
  		OpenActivity(0, "android.intent.action.Timetable.ACTION_MODE_EDIT_COURSE", lRowId);
  	}
  }     

  public void openActAssignment(long lRowId)
  {  	
  	bundleOtherDataStartup.clear();
  	if (lRowId == -1)
  		OpenActivity(0, "android.intent.action.Timetable.ACTION_MODE_NEW_ASSIGNMENT");
  	else
  		OpenActivity(0, "android.intent.action.Timetable.ACTION_MODE_EDIT_ASSIGNMENT", lRowId);
  }     

  public void openActNote(long lRowId)
  {  	
  	bundleOtherDataStartup.clear();
  	if (lRowId == -1)
  		OpenActivity(0, "android.intent.action.Timetable.ACTION_MODE_NEW_NOTE");
  	else
  		OpenActivity(0, "android.intent.action.Timetable.ACTION_MODE_EDIT_NOTE", lRowId);
  }     

  public void openActOptions()
  {
  	bundleOtherDataStartup.clear();
  	OpenActivity(0, "android.intent.action.Timetable.ACTION_MODE_EDIT_OPTIONS");
  }

  public void openActViewAbout()
  {
  	bundleOtherDataStartup.clear();
  	OpenActivity(0, "android.intent.action.Timetable.ACTION_MODE_VIEW_ABOUT");
  }
  
  @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
  	super.onActivityResult(requestCode, resultCode, data);
  	
  	Bundle extras = CommonActivity.getIntentExtras(data);
  	if (extras != null)
  	{  	
	  	if (resultCode == RESULT_OK)
	  	{
				//state refresh, because of table updated
				if (extras.containsKey(CommonActivity.bundleTableUpdated))
				{
					String sTableNameToReload = extras.getString(CommonActivity.bundleTableUpdated);
					ReloadDataTable(sTableNameToReload);
					RefreshData();
					UpdateReminderService(this, prefs, CommonActivity.bundleTableUpdated);
				}
				//state refresh, because of options updated
				if (extras.containsKey(CommonActivity.bundleOptionsUpdated))
				{
					prefs.Load();  				  				  			
					if (CurrentAgendaView.TimeFormatChanged())
						CurrentAgendaView.UpdateTimeFormat();  					
					CurrentAgendaView.SetTodayViewItem();
					RefreshData();
					UpdateReminderService(this, prefs, CommonActivity.bundleOptionsUpdated);
				}
	  	}
  	}
  }
  
	public int GetViewSpaceWidth()
	{
  	//return rlayAgendaView.getWidth();
		return 320 - 8;
	}
	
	public int GetViewSpaceHeight()
	{
		//return rlayAgendaView.getHeight();
		return 340;
	}
	
	public boolean StartReminderService()
	{
    ComponentName cpn = startService(new Intent(Timetable.this, AlarmService.class));
    return (cpn != null);
	}

	/*
	public void StopReminderService()
	{
		stopService(new Intent(Timetable.this, AlarmService.class));
	}
	*/
	
	public static boolean UpdateReminderService(Context context, Prefs prefs, String sKey)
	{
    Bundle args = new Bundle();
    args.putBoolean(sKey, true);
    //put additional prefs
    args.putBoolean("b24HourMode", prefs.b24HourMode);
    args.putInt("iFirstDayOfWeek", prefs.iFirstDayOfWeek);
    args.putInt("iSnoozeCount", prefs.iSnoozeCount);
    args.putInt("iSnoozeMinutesOverdue", prefs.iSnoozeMinutesOverdue);
    //update service
    Intent intent = new Intent(context, AlarmService.class);
    intent.putExtras(args);
    ComponentName cpn = context.startService(intent);
    return (cpn != null);	
	}

	@Override
	protected void restoreStateFromFreeze()
	{
		
	}
	
	 public void showTimeZone()
	  {
	         //bundleOtherDataStartup.clear();
	         //OpenActivity(0, "android.intent.action.Timetable.ACTION_MODE_VIEW_TIME_ZONE");
	         Configuration userConfig = new Configuration();
	    Settings.System.getConfiguration( getContentResolver(), userConfig );
	    Calendar cal = Calendar.getInstance( userConfig.locale);
	    TimeZone tz = cal.getTimeZone();
	    Toast.makeText(this, tz.getDisplayName(true, TimeZone.LONG)+" ("+tz.getDisplayName(true, TimeZone.SHORT)+")", Toast.LENGTH_LONG).show();
	  }
	
}

