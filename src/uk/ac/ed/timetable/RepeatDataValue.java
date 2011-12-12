
package uk.ac.ed.timetable;


import java.util.Calendar;

//date value for faster, cached access
public class RepeatDataValue
{
	//fields
	public long lMS = 0;
	public int iYear = 0;
	public int iMonth = 0;
	public int iDay = 0;
	public int iDayOfWeek = 0;
	public int lDST_OFFSET = 0;
	public int iMaxMonthDay = 0;
	
	//methods
	public void getFromCalendar(Calendar value)
	{
		iYear = value.get(Calendar.YEAR);
		iMonth = value.get(Calendar.MONTH);
		iDay = value.get(Calendar.DAY_OF_MONTH);
		
		//set Calendar
		value.set(iYear, iMonth, iDay, 12, 0, 0);
		value.set(Calendar.MILLISECOND, 0);
		
		//cache values
		iDayOfWeek = value.get(Calendar.DAY_OF_WEEK);
		lMS = value.getTimeInMillis();
		lDST_OFFSET = value.get(Calendar.DST_OFFSET);		
		iMaxMonthDay = value.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
}
