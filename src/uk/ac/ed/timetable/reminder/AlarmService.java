
package uk.ac.ed.timetable.reminder;


import java.util.Calendar;
import uk.ac.ed.timetable.CommonActivity;
import uk.ac.ed.timetable.Prefs;
import uk.ac.ed.timetable.Utils;
import uk.ac.ed.timetable.database.Database;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.*;


public class AlarmService extends Service
{
	//local service alarm prefs
	protected class AlarmPrefs extends Prefs
	{
		//methods
		public AlarmPrefs(Context ctx)
		{
			super(ctx);			
		}		
		protected void GetServicePrefs(Bundle arguments)
		{
			if (arguments != null)
			{
				if (arguments.containsKey("b24HourMode"))
					b24HourMode = arguments.getBoolean("b24HourMode");
				if (arguments.containsKey("iFirstDayOfWeek"))
					iFirstDayOfWeek = arguments.getInt("iFirstDayOfWeek");
				if (arguments.containsKey("iSnoozeCount"))
					iSnoozeCount = arguments.getInt("iSnoozeCount");
				if (arguments.containsKey("iSnoozeMinutesOverdue"))
					iSnoozeMinutesOverdue = arguments.getInt("iSnoozeMinutesOverdue");
			}
		}
	}
	
	//fields
	protected static final int GET_PID_TRANSACTION = IBinder.FIRST_CALL_TRANSACTION;
		
	//fields
	private Utils utils = null;
	private AlarmPrefs prefs = null;
	private Database db = null;
	private AlarmDataView dataView = null;
	private AlarmReminder reminder = null;
	
	//fields
	private Calendar dateToday = Calendar.getInstance();	
	private Handler handlerUpdateDate = new Handler();
	private final static int iHandlerUpdateTime = 1000 * 3;
	private int iUpdateDate_minute = 0;
	
	
	@Override
	public void onCreate()
	{
		//initialize local alarm prefs
		db = new Database(this);
		utils = new Utils(this);
		prefs = new AlarmPrefs(this);
		
		dataView = new AlarmDataView(this, db, prefs, utils);
		reminder = new AlarmReminder(this, prefs, utils);

		//schedule handler update date assignment
		handlerUpdateDate.removeCallbacks(handlerUpdateDateAssignment);
		handlerUpdateDate.postDelayed(handlerUpdateDateAssignment, iHandlerUpdateTime);		
	}
	
	@Override
	public void onStart(Intent intent, int startId) 
	{
		Bundle args = intent.getExtras();
		if (args != null)
		{
			//if data tables updated, refresh data
			if (args.containsKey(CommonActivity.bundleTableUpdated))
			{
				RefreshAlarmData();
			}			
			//if prefs updated, reload prefs
			if (args.containsKey(CommonActivity.bundleOptionsUpdated))
			{
				//get updated options
				prefs.GetServicePrefs(args);
			}
		}
	}    
	
	@Override
	public void onDestroy()
	{
		//service stopped
		reminder.RemoveNotify();		
  	handlerUpdateDate.removeCallbacks(handlerUpdateDateAssignment);		
	}
	
  public synchronized void UpdateTodayDate()
  {
  	dateToday.setTimeInMillis(System.currentTimeMillis());
  	dateToday.setFirstDayOfWeek(prefs.iFirstDayOfWeek);  	
  }
	
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
    			RefreshAlarmData();
    		}
    	} finally {
      	handlerUpdateDate.postDelayed(this, iHandlerUpdateTime);
    	}      
    }
  };
  
  private synchronized void RefreshAlarmData()
  {
		iUpdateDate_minute = dateToday.get(Calendar.MINUTE);
		if (dataView.CollectAlarmItems())
		{
			reminder.Clear();
			reminder.AddAlarmData(dataView.GetApptsData());
			reminder.AddAlarmData(dataView.GetAssignmentsData());
			reminder.ShowNotification();
		}  	
  }

	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub
		return null;
	}
    
}

