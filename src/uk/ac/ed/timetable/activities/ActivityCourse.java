package uk.ac.ed.timetable.activities;


import java.util.*;
import uk.ac.ed.widgets.*;
import uk.ac.ed.timetable.CommonActivity;
import uk.ac.ed.timetable.R;
import uk.ac.ed.timetable.RepeatData;
import uk.ac.ed.timetable.database.DataRowCourse;
import uk.ac.ed.timetable.database.DataTable;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import android.view.*;


//New course activity
public class ActivityCourse extends CommonActivity
{	
	//fields
	private Calendar dateStart = null;
	private DataRowCourse dataRow = null;
	private DataTable dataTable = null;

	//views
	private TouchEdit edSubject = null;
	private Button btnStartDate = null;
	private Button btnStartTime = null;
	private CheckBox chkAllDay = null;
	private CheckBox chkAlarm = null;
	private Button btnRepeatSelect = null;
	
	private ImageButton btnDelete = null;
	private ImageButton btnSave = null;
	
	//repeat data
	private int iRepeatType = -1;
	private int iRepeatEvery = 0;
	private Calendar dateEndOn = null;

  //methods
  @Override
  public void onCreate(Bundle icicle)
  {
    super.onCreate(icicle);      
    setContentView(R.layout.course);
          
    //initialize objects
    dataRow = new DataRowCourse(userdb);
  	dataTable = new DataTable(dataRow);

    //check startup mode and open data
  	if (GetStartMode() == StartMode.EDIT)
  		if (!OpenDataForEdit(dataTable))
  			finish();
        	
    //initialize views
    InitViews();
    InitState();
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

  }
      
  @Override
  public void onPause()
  {
  	super.onPause();
  	
  }
  
  private void InitViews()
  {
  	edSubject = (TouchEdit)findViewById(R.id.edCourseSubject); 	
  	btnStartDate = (Button)findViewById(R.id.btnCourseStartDate);
  	btnStartTime = (Button)findViewById(R.id.btnCourseStartTime);  	 
  	btnStartDate.setVisibility(ImageButton.VISIBLE);
  	btnRepeatSelect = (Button)findViewById(R.id.btnRepeatSelect);
  	btnRepeatSelect.setVisibility(ImageButton.INVISIBLE);
  	btnStartDate.setVisibility(ImageButton.GONE);
  	
  	btnSave = (ImageButton)findViewById(R.id.btnCourseSave);
  	btnSave.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SaveData();
			}		
  	});
  	
  	btnDelete = (ImageButton)findViewById(R.id.btnCourseDelete);
  	btnDelete.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				DeleteData();
			}		
  	});
  }    

  private void SetStartDateByAgendaView(Calendar calDate)
  {
  	if (getIntent() != null)
  	{
  		Bundle extras = getIntent().getExtras();  	  	
	  	if (extras != null)
	  	{  	
				if (extras.containsKey(CommonActivity.bundleAgendaView))
				{
					int iView = extras.getInt(CommonActivity.bundleAgendaView);
					if (iView == 1) //day
					{
						long lStartDate = extras.getLong(CommonActivity.bundleAgendaViewStartDate);
						calDate.setTimeInMillis(lStartDate);
					}  		
				} 
	  	}
  	}
  }
    
  private void SetStartTimeForDayAgendaView(Calendar calDate)
  {
  	if (getIntent() != null)
  	{
  		Bundle extras = getIntent().getExtras();  	  	
	  	if (extras != null)
	  	{  	
				if (extras.containsKey(CommonActivity.bundleHourOfDay))
				{
					int iView = extras.getInt(CommonActivity.bundleAgendaView);
					if (iView == 1) //day
					{
						int iHourOfDay = extras.getInt(CommonActivity.bundleHourOfDay);
						int iMinutes = extras.getInt(CommonActivity.bundleMinutes);					
						calDate.set(Calendar.HOUR_OF_DAY, iHourOfDay);
						calDate.set(Calendar.MINUTE, iMinutes);					
					}  		
				}
	  	}
  	}
  }
  
  private void updateStartDateTimeForNewCourse(Calendar calDate)
  {
  	int iHour = calDate.get(Calendar.HOUR_OF_DAY);
  	int iMinute = calDate.get(Calendar.MINUTE);
  	
  	if (iHour < 23)
  		iHour += 1;
  	iMinute = 0;
  	
  	calDate.set(Calendar.HOUR_OF_DAY, iHour);
  	calDate.set(Calendar.MINUTE, iMinute);
  	calDate.set(Calendar.SECOND, 0);
  	calDate.set(Calendar.MILLISECOND, 0); 
  }
  
  private void InitState()
  {
  	//title
  	String sSubTitle = utils.GetResStr(R.string.titleDefaultCourse); 

  	//date values
  	dateStart = Calendar.getInstance();
  	dateEndOn = Calendar.getInstance();
  	
  	dataRow.SetDuration(prefs.iMinutesDuration);

  	//INSERT MODE
  	if (GetStartMode() == StartMode.NEW)
  	{
  		sSubTitle = utils.GetResStr(R.string.titleNewCourse);
      btnDelete.setVisibility(View.INVISIBLE);
  		
			//initialize data
  		SetStartDateByAgendaView(dateStart);
  		updateStartDateTimeForNewCourse(dateStart);
    	SetStartTimeForDayAgendaView(dateStart);
    	
    	chkAllDay.setChecked(false);
      
    	//repeat data
    	iRepeatType = 0; //none
    	iRepeatEvery = 1;
    	dateEndOn.setTimeInMillis(0); //no end date     	
  	}
  	
  	//EDIT MODE
  	if (GetStartMode() == StartMode.EDIT)
  	{
      sSubTitle = utils.GetResStr(R.string.titleEditCourse);
      
      dateStart.setTimeInMillis(dataRow.GetStartDate().getTimeInMillis());
      
  		btnDelete.setVisibility(View.VISIBLE);
  		edSubject.setText(dataRow.GetSubject());
    	
    	//repeat data
    	iRepeatType = dataRow.GetRepeat().GetRepeatTypeAsInt();
    	iRepeatEvery = dataRow.GetRepeat().GetEvery();
    	dateEndOn.setTimeInMillis(dataRow.GetRepeat().GetEndOnDate().getTimeInMillis());
  	}

  	restoreStateFromFreezeIfRequired();
  	
  	SetActivityTitle(sSubTitle);
    UpdateStartDateTimeView();

    UpdateRepeatInfo();

  	//set focus to subject
  	edSubject.requestFocus();
  	if (GetStartMode() == StartMode.EDIT)
  		edSubject.setSelection(edSubject.length());  	
  }

  private void UpdateRepeatInfo()
  {
  	btnRepeatSelect.setText(GetRepeatInfo());  	
  }

  private String GetRepeatInfo()
  {
  	String s = utils.GetResStr(R.string.strRepeatTypeNone);  	
  	String sUntil = utils.GetResStr(R.string.strRepeatInfoUntil);  	
  	String sEndDate = (dateEndOn.getTimeInMillis() == 0)?"":" " + sUntil + " " + utils.GetLongDate(dateEndOn);
  	//daily
  	if (iRepeatType == 1)
  		s = String.format(utils.GetResStr(R.string.strRepeatInfoDaily), iRepeatEvery, sEndDate);  		
  	//weekly
    if (iRepeatType == 2)
  		s = String.format(utils.GetResStr(R.string.strRepeatInfoWeekly), iRepeatEvery, sEndDate);
    //monthly
    if (iRepeatType == 3)
  		s = String.format(utils.GetResStr(R.string.strRepeatInfoMonthly), iRepeatEvery, sEndDate);
    //monthly
    if (iRepeatType == 4)
  		s = String.format(utils.GetResStr(R.string.strRepeatInfoYearly), sEndDate);    
  	return s;
  }
  
  private void UpdateStartDateTimeView()
  {
	  btnStartTime.setText(utils.GetWeekDay(dateStart)+", "+utils.GetLongTime(dateStart, prefs.b24HourMode));
  }
  
  protected void onSizeChanged(int w, int h, int oldw, int oldh)
  {
  	
  }

  public void SaveData()
  {
  	//check date if no repeat
  	if ((iRepeatType == 0) && (DateBeforeNow(dateStart)))
  		return;
  	  	
  	dataRow.SetSubject(edSubject.getText().toString());
  	dataRow.SetStartDate(dateStart);
  	dataRow.SetAllDay(false);
  	dataRow.SetAlarm(false);
  	
		//set repeat type
  	RepeatData rd = dataRow.GetRepeat(); 	  	
		rd.SetRepeatTypeAsInt(iRepeatType);  			
		rd.SetEvery(iRepeatEvery);		
		rd.SetEndOnDate(dateEndOn.getTimeInMillis());
		
		if (SaveDataToTable(dataTable))
			CloseActivity(dataTable);		
  }  
	
  public void DeleteData()
  {  
  	DataTable.setCorDelete(true);
  	if (DeleteDataFromTable(dataTable))
			CloseActivity(dataTable);
  }
  
  @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
  	super.onActivityResult(requestCode, resultCode, data);
  	  	
  	Bundle extras = CommonActivity.getIntentExtras(data);
  	if (extras != null)
  	{  			  	
	  	//check for date widget edit request code
	  	if (requestCode == DateWidget.SELECT_DATE_REQUEST)
	  	{
	    	final long lDate = DateWidget.GetSelectedDateOnActivityResult(requestCode, resultCode, extras, dateStart);
	    	if (lDate != -1)
	    	{
	  			UpdateStartDateTimeView();
	  			return;
	    	}
	  	}
	  	
	  	//check for time widget edit request code
	  	if ((requestCode == TimeWidget.SELECT_TIME_REQUEST) && (resultCode == RESULT_OK))
			{
	  		final int iHour = TimeWidget.GetSelectedTimeHourOnActivityResult(requestCode, resultCode, extras);
	  		final int iMinute = TimeWidget.GetSelectedTimeMinuteOnActivityResult(requestCode, resultCode, extras);    		
		  	dateStart.set(Calendar.HOUR_OF_DAY, iHour);
		  	dateStart.set(Calendar.MINUTE, iMinute);
				chkAllDay.setChecked(false);
				UpdateStartDateTimeView();
				return;
			}
	  	
	  	//get KeyboardWidget result
	  	if ((requestCode == KeyboardWidget.EDIT_TEXT_REQUEST) && (resultCode == RESULT_OK)) 
	  	{
				String sText = KeyboardWidget.GetTextOnActivityResult(requestCode, resultCode, extras);    			
				edSubject.setText(sText);
				return;
	  	}
  	
  	}
  }  
         
  @Override
  public void SaveDateValuesBeforeChange(Bundle data)
  {
  	super.SaveDateValuesBeforeChange(data);
  	if (GetStartMode() == StartMode.EDIT)
  	{
  		data.putLong("dateStart", dataRow.GetStartDate().getTimeInMillis());
  		data.putLong("dateEndOn", dataRow.GetRepeat().GetEndOnDate().getTimeInMillis());
  	}
  }
  
  @Override
  public boolean DateValuesChanged(Bundle data)
  {
  	super.DateValuesChanged(data);
  	if (GetStartMode() == StartMode.EDIT)
  	{
  		if (dateStart.getTimeInMillis() != data.getLong("dateStart"))
  			return true;
  	}
  	return false;
  }

  @Override
  protected void onSaveInstanceState(Bundle outState)
  {
  	super.onSaveInstanceState(outState);
  	//save controls state
  	outState.putString("subject", edSubject.getText().toString());
  	outState.putBoolean("alarm", false);  	
  	outState.putLong("dateStart", dateStart.getTimeInMillis());  	
  	outState.putBoolean("allday", chkAllDay.isChecked());  	
  	outState.putInt("repeatType", iRepeatType);
  	outState.putInt("repeatEvery", iRepeatEvery);  	  	
  	outState.putLong("dateEndOn", dateEndOn.getTimeInMillis());  	
  }  
  
	@Override
	protected void restoreStateFromFreeze()
	{
 		edSubject.setText(freeze.getString("subject"));  	
 		chkAlarm.setChecked(freeze.getBoolean("alarm"));
 		dateStart.setTimeInMillis(freeze.getLong("dateStart"));
 		chkAllDay.setChecked(false);
 		iRepeatType = freeze.getInt("repeatType");
 		iRepeatEvery = freeze.getInt("repeatEvery");
 		dateEndOn.setTimeInMillis(freeze.getLong("dateEndOn"));
	}
  
}
