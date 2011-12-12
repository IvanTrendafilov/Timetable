
package uk.ac.ed.timetable.reminder;


import uk.ac.ed.timetable.R;
import uk.ac.ed.timetable.Utils;


public class AlarmDataViewItem
{
	//fields
	public static final int iOrderAppts = 0;
	public static final int iOrderAssignments = 1;
	
	//fields
	private long lID = -1;
	protected String sSubject = "";
	private int iOrder = -1;
	private int iHour = 0;
	private int iMinute = 0;
	private int iDurationInMinutes = 0;
	protected boolean bAlarm = false;
	protected long lPriority = -1;
	private long lRepeatDaysBitMask = 0;

	//methods
	public AlarmDataViewItem(long lID, String sSubject, int iOrder, boolean bAlarm)
	{
		this.lID = lID;
		this.sSubject = sSubject;
		this.iOrder = iOrder;
		this.bAlarm = bAlarm;
	}
	
	public void Set(int iHour, int iMinute, int iDurationInMinutes)
	{
		this.iHour = iHour;
		this.iMinute = iMinute;
		this.iDurationInMinutes = iDurationInMinutes;
	}
	
	public void Set(long lPriority)
	{
		this.lPriority = lPriority;
	}
	
	public int TimeAsSeconds()
	{
		return (iMinute * 60) + (iHour * 3600);						
	}
	
	public long GetID()
	{
		return lID;
	}
	
	public int GetOrder()
	{
		return this.iOrder;			
	}
	
	public int GetDuration()
	{
		return iDurationInMinutes;
	}

	public void SetRepeatDays(long lDaysData)
  {
		lRepeatDaysBitMask = lDaysData;
  }
  
	//returns true if 0, +1, +2, and so on day back is active
  public boolean GetVisibleDay(int iDay)
  {
  	long bitMask = 0x01L << iDay;
  	return ((lRepeatDaysBitMask & bitMask) != 0);
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
  
  public boolean IsOverdue()
  {
  	long bitMask = 0x01L;
  	return ((lRepeatDaysBitMask & bitMask) == 0);
  }
  
  public int GetTimeKey()
  {
  	return (iHour * 100) + iMinute;
  }
  
  public String GetTimeAsText(boolean b24HourMode)
  {
		String s = "";			
		String sm = "AM";
		if (iHour >= 12)
			sm = "PM";
		int iDisplayHour = iHour;	
		if (iDisplayHour == 0)
			iDisplayHour = 12;
		if (iDisplayHour > 12)
			iDisplayHour -= 12;
		//format time
		if (b24HourMode)
		{
			s = String.format("%1$d:%2$02d", iHour, iMinute);
		} else {
			s = String.format("%1$d:%2$02d %3$s", iDisplayHour, iMinute, sm);
		}
		return s;
  }
  
	public String GetText(Utils utils, boolean b24HourMode)
	{
		final int iOverdueDays = GetOverdueDays();
		String s = "";
		
		String sText = new String(sSubject);			
		sText = sText.replace("\n", " ");			
		sText = Utils.CapitalizeFirstLetter(sText);
		
		//appt info
		if (iOrder == iOrderAppts)
		{
			if (iOverdueDays == 0)
			{
				String sTime = GetTimeAsText(b24HourMode);					
				s = String.format("%1$s. %2$s", sTime, sText);
			} else {
				String sOverdueText = utils.GetResStr(R.string.msgDaysOverdue);
				s = String.format("%1$s. (%2$d %3$s)", sText, iOverdueDays, sOverdueText);
				if (iOverdueDays == 1)
				{
					sOverdueText = utils.GetResStr(R.string.msgDayOverdue);
					s = String.format("%1$s. (%2$s)", sText, sOverdueText);
				}
			}
		}
		
		//assignment info
		if (iOrder == iOrderAssignments)
		{
			s = String.format("%2$s. (%1$d)", lPriority, sText);
		}
		
		return s;
	}
	
	public String GetHashString()
	{
		String s = "";
		s += Long.toString(lID);
		s += sSubject;
		s += Integer.toString(iOrder);
		s += Integer.toString(iHour);
		s += Integer.toString(iMinute);
		s += Boolean.toString(bAlarm);
		s += Long.toString(lPriority);
		s += Long.toString(lRepeatDaysBitMask);
		return s;
	}
		
}
