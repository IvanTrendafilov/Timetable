
package uk.ac.ed.timetable.dataview;


import java.util.Calendar;
import uk.ac.ed.timetable.agenda.AgendaView;


public class DataViewItem
{
	//fields
	public long lID = -1;
	public String sSubject = "";
	
	private long lStartDate = 0;
	private int iHour = 0;
	private int iMinute = 0;
	
	public long lRepeatEndOnDate = 0;
	public long lDueDate = 0;
	
	public int iDurationInMinutes = 0;
	public boolean bAlarm = false;
	public boolean bDone = false;
	public boolean bAllDay = false;
	public long lPriority = -1;
	public int iRepeatType = 0;
	public int iRepeatEvery = 1;		
	public int viewMode = AgendaView.viewMode.NONE;
	
	private long lRepeatDaysBitMask = 0;		
	
	//methods
	public void Clear()
	{
		lRepeatDaysBitMask = 0;
	}
	
	public boolean IsRepeat()
	{
		return (iRepeatType > 0);
	}
	
	public boolean UseRepeatEndOnDate()
	{
		return (lRepeatEndOnDate > 0);			
	}
	
	public boolean UseDueDate()
	{
		return (lDueDate > 0);
	}
	
	public void SetStartDate(Calendar calUtilDate, long lStartDate)
	{
		this.lStartDate = lStartDate;
  	calUtilDate.setTimeInMillis(this.lStartDate);
		iHour = calUtilDate.get(Calendar.HOUR_OF_DAY);  
		iMinute = calUtilDate.get(Calendar.MINUTE);
	}
	
	public long GetStartDateAsLong()
	{
		return lStartDate;
	}
	
	public int GetStartHour()
	{
		if (bAllDay)
			return 0;
		return iHour;
	}

	public int GetStartMinute()
	{
		if (bAllDay)
			return 0;
		return iMinute;
	}

	public int GetDuration()
	{
		return iDurationInMinutes;
	}
	
	public long GetPriority()
	{
		return lPriority;
	}
	
  public void SetVisibleDay(int iDay)
  {
		lRepeatDaysBitMask |= 0x01L << iDay; 
  }
  
  public boolean GetVisibleDay(int iDay)
  {
  	long bitMask = 0x01L << iDay;
  	return ((lRepeatDaysBitMask & bitMask) != 0);
  }
  
  public long GetVisibleDays()
  {
  	return lRepeatDaysBitMask; 
  }
  
  //if bit 0 set -> time occurs for 0 hour, etc...
  public long GetTimeDataAsBitMask(int iDay)
  {
  	if (GetVisibleDay(iDay))
  	{
	  	int iHour = GetStartHour();	  	
	  	long bitHour = 0x01L << iHour;	  	
	  	return bitHour;
  	}
  	return 0;
  }
  
  public int GetOverdueDays()
  {
  	long bitMask = 0x01L;
  	for (int iDayOffset = 0; iDayOffset < 7; iDayOffset++)
  	{
  		if ((lRepeatDaysBitMask & bitMask) == bitMask)
  			return iDayOffset;
  		bitMask <<= 1;
  	}
  	return 0;
  }
  
  public int GetTimeKey()
  {
  	return (GetStartHour() * 100) + GetStartMinute();
  }
  
  public boolean TimeOverdue(int iCurrTimeKey)
  {
		final int iOverdueDays = GetOverdueDays();
		
		//test if time overdue
		if (iOverdueDays == 0)
		{
			if (iCurrTimeKey >= GetTimeKey())
				return true;
		}
		
		//some days overdue
		if (iOverdueDays > 0)
			return true;
  	
		return false;
  }
  
}
