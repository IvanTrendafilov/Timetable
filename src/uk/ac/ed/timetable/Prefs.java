
package uk.ac.ed.timetable;


import java.util.Calendar;
import android.content.*;


public class Prefs
{
	//private fields
	private final String sPrefsName = "TimetablePrefs";
	private SharedPreferences prefs = null;

	//prefs data
  private int iMinutesOffset; //offset start of new course
  public int iMinutesDuration; //duration of course
  public boolean b24HourMode; //time mode
  public int iFirstDayOfWeek; //first day of week
  public boolean bShowAllAssignments; //show all assignments in today, or hide undued
  public int iSnoozeCount; //count of snooze times to change alarm to cleared
  public int iSnoozeMinutesOverdue; //snooze minutes alarm overdue
	
  public Prefs(Context ctx)
  {
  	prefs = ctx.getSharedPreferences(sPrefsName, Context.MODE_WORLD_WRITEABLE);
  	Load();
  }
  
	public boolean Save()
	{
		try
		{
			SharedPreferences.Editor ed = prefs.edit();
			
	    ed.putInt("iMinutesOffset", iMinutesOffset);
	    ed.putInt("iMinutesDuration", 50);       
	    ed.putBoolean("b24HourMode", b24HourMode);          
	    ed.putInt("iFirstDayOfWeek", iFirstDayOfWeek);	    
	    ed.putBoolean("bShowAllAssignments", bShowAllAssignments);
	    ed.putInt("iSnoozeCount", iSnoozeCount);	    
	    ed.putInt("iSnoozeMinutesOverdue", iSnoozeMinutesOverdue);	    
	
	    //return ed.commit();
	    
	    ed.commit();
	    
	    return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public void Load()
	{
  	iMinutesOffset = prefs.getInt("iMinutesOffset", 30);
  	iMinutesDuration = prefs.getInt("iMinutesDuration", 50);
  	b24HourMode = prefs.getBoolean("b24HourMode", true);
  	iFirstDayOfWeek = prefs.getInt("iFirstDayOfWeek", Calendar.MONDAY);
  	bShowAllAssignments = prefs.getBoolean("bShowAllAssignments", true);
  	iSnoozeCount = prefs.getInt("iSnoozeCount", 5);
  	iSnoozeMinutesOverdue = prefs.getInt("iSnoozeMinutesOverdue", 5);  	
	}
}
