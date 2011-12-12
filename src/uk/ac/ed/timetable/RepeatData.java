
package uk.ac.ed.timetable;


import java.util.Calendar;


public class RepeatData
{
	//fields
	//RepeatType
	// 0: None
	// 1: Daily
	// 2: Weekly
	// 3: Monthly
	// 4: Yearly	
	private final int iRepeatTypeMin =  0;	
	private final int iRepeatTypeMax =  4;
	
	//fields
	private int iRepeatType =  0;	
	private int iEvery = 1; //repeat frequency (every day/week/month)
	
	//cached values
	private RepeatDataValue dvStartDate = new RepeatDataValue();
	private RepeatDataValue dvTestDate = new RepeatDataValue();	
	
	//fields
	private Calendar calEndOnDate = Calendar.getInstance();
	
	//cached values
	private long lEndDateMs = 0;
	private int lEndDate_DST_OFFSET = 0;
	
	//methods
	public RepeatData()
	{
	}
	
	public void SetStartDate(Calendar value)
	{
		dvStartDate.getFromCalendar(value);
	}
		
	public int GetEvery()
	{
		return iEvery;
	}
	
	public boolean UsingEndOnDate()
	{
		return (lEndDateMs > 0);		
	}
	
	public Calendar GetEndOnDate()
	{
		return calEndOnDate;
	}
	
	public int GetRepeatTypeAsInt()
	{
		return iRepeatType;
	}
		
	public void SetEvery(int value)
	{
		iEvery = value;
	}
	
	public void SetEndOnDate(Calendar value)
	{
		if (value == null)
		{
			calEndOnDate.setTimeInMillis(0);
			lEndDateMs = 0;
		} else {
			calEndOnDate.set(value.get(Calendar.YEAR), value.get(Calendar.MONTH), value.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
			calEndOnDate.set(Calendar.MILLISECOND, 0);
			lEndDateMs = calEndOnDate.getTimeInMillis();
		}
				
		lEndDate_DST_OFFSET = calEndOnDate.get(Calendar.DST_OFFSET);
	}
	
	public void SetEndOnDate(long value)
	{
		if (value == 0)
		{
			calEndOnDate.setTimeInMillis(0);
			lEndDateMs = 0;
		} else {
			calEndOnDate.setTimeInMillis(value);
			calEndOnDate.set(Calendar.HOUR_OF_DAY, 12);
			calEndOnDate.set(Calendar.MINUTE, 0);
			calEndOnDate.set(Calendar.SECOND, 0);
			calEndOnDate.set(Calendar.MILLISECOND, 0);
			lEndDateMs = value;
		}

		lEndDate_DST_OFFSET = calEndOnDate.get(Calendar.DST_OFFSET);
	}
	
	public void SetRepeatTypeAsInt(int value)
	{
		if (value < iRepeatTypeMin)
				value = iRepeatTypeMin;
		if (value > iRepeatTypeMax)
				value = iRepeatTypeMax;
		iRepeatType = value;
	}
	
	//date part compare, returns diffrence value
	private int GetDayDiff()
	{		
		//calc days
		long lMsDiff = (dvStartDate.lMS - (dvTestDate.lMS + dvTestDate.lDST_OFFSET));		
		return (int)(lMsDiff / 86400000);
	}
	
	private int GetMonthDiff()
	{
		int iYears = dvTestDate.iYear - dvStartDate.iYear;		
		int iMonths = dvTestDate.iMonth - dvStartDate.iMonth;		
		iMonths += (iYears * 12);
		return iMonths;
	}
	
	private boolean IsDateOutOfEnd()
	{
		if (UsingEndOnDate())
		{			
			//calc days
			long lMsDiff = (dvTestDate.lMS - (lEndDateMs + lEndDate_DST_OFFSET));		
			float iDays = (lMsDiff / 86400000);
			if ((int)iDays > 0)
				return true;
		}
		return false;
	}

	private boolean IsDateEqualOnce()
	{	
		boolean bResult = false;
		
		if (dvStartDate.iDay == dvTestDate.iDay)
			if (dvStartDate.iMonth == dvTestDate.iMonth)
				if (dvStartDate.iYear == dvTestDate.iYear)
					bResult = true;
		
		if (IsDateOutOfEnd())
			bResult = false;
	
		return bResult;
	}
	
	private boolean IsDateEqualDaily()
	{	
		boolean bResult = false;
		
		//check if date in range
		int iDays = GetDayDiff();
		if (iDays <= 0)
			if ((iDays % iEvery) == 0)
				bResult = true;
		
		if (IsDateOutOfEnd())
			bResult = false;
	
		return bResult;
	}
	
	private boolean IsDateEqualWeekly()
	{	
		boolean bResult = false;
		
		//check if date in range
		int iDays = GetDayDiff();
		if (iDays <= 0)
		{
			int iModValue = iEvery * 7;
			if ((((iDays % iModValue) <= 0) && ((iDays % iModValue) >= -6)))
			{
				if (dvStartDate.iDayOfWeek == dvTestDate.iDayOfWeek)										
					bResult = true;
			}
		}
		
		if (IsDateOutOfEnd())
			bResult = false;
		
		return bResult;
	}

	private boolean IsDateEqualMonthly()
	{
		boolean bResult = false;
		
		//check if date in range
		int iDays = GetDayDiff();
		if (iDays <= 0)
		{
			int iMonthDay = dvStartDate.iDay;

			if (iMonthDay >  dvTestDate.iMaxMonthDay)
				iMonthDay = dvTestDate.iMaxMonthDay;

			if (dvTestDate.iDay == iMonthDay)
			{
				int iMonths = GetMonthDiff();
				if ((iMonths % iEvery) == 0)
					bResult = true;
			}
		}
			
		if (IsDateOutOfEnd())
			bResult = false;
	
		return bResult;		
	}

	private boolean IsDateEqualYearly()
	{
		boolean bResult = false;
		
		//check if date in range
		int iDays = GetDayDiff();
		if (iDays <= 0)
		{
			//compare months
			if (dvStartDate.iMonth == dvTestDate.iMonth)
			{
				int iMonthDay = dvStartDate.iDay;			
				
				if (iMonthDay >  dvTestDate.iMaxMonthDay)
					iMonthDay = dvTestDate.iMaxMonthDay;
							
				if (dvTestDate.iDay == iMonthDay)
					bResult = true;
			}
		}
			
		if (IsDateOutOfEnd())
			bResult = false;
	
		return bResult;
	}

	public boolean IsDateEqual(Calendar value)
	{
		dvTestDate.getFromCalendar(value);

		//test case
		switch (iRepeatType)
		{
			case 0: //None
				return IsDateEqualOnce();
			case 1: //Daily
				return IsDateEqualDaily();
			case 2: //Weekly
				return IsDateEqualWeekly();
			case 3: //Monthly:
				return IsDateEqualMonthly();
			case 4: //Yearly:
				return IsDateEqualYearly();
		}	
		
		return false;		
	}

	public boolean IsDateEqual(RepeatDataValue value)
	{
		dvTestDate = value;

		//test case
		switch (iRepeatType)
		{
			case 0: //None
				return IsDateEqualOnce();
			case 1: //Daily
				return IsDateEqualDaily();
			case 2: //Weekly
				return IsDateEqualWeekly();
			case 3: //Monthly:
				return IsDateEqualMonthly();
			case 4: //Yearly:
				return IsDateEqualYearly();
		}	
		
		return false;		
	}
	
}
