
package uk.ac.ed.timetable;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.app.AlertDialog;


public class Utils 
{
	private Context ctx = null;
	
	public static int ANIM_ALPHA_DURATION = 100;
	public static int ANIM_TRANSLATE_DURATION = 30;	
	
	public static int MSGTYPE_DEFAULT = 0;
	public static int MSGTYPE_INFO = 1;
	public static int MSGTYPE_WARNING = 2;
	public static int MSGTYPE_ERROR = 3;
	
	private SimpleDateFormat dateFormatWeekDay = new SimpleDateFormat("EEEE");
	private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM");
	private SimpleDateFormat dateFormatLong = new SimpleDateFormat("EEEE, d MMMM yyyy");
	private SimpleDateFormat dateFormatShort = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat dateFormatSql = new SimpleDateFormat("dd-MM-yyyy kk:mm.ss");

	//UTILS
	public Utils(Context context)
	{
		ctx = context;
	}
	
	public String GetWeekDay(Calendar date)
	{
		return dateFormatWeekDay.format(date.getTime());
	}

	public String GetMonth(Calendar date)
	{
		return dateFormatMonth.format(date.getTime());
	}
	
	public String GetLongDate(Calendar date)
	{
		return dateFormatLong.format(date.getTime());
	}
	
	public String GetShortDate(Calendar date)
	{
		return dateFormatShort.format(date.getTime());
	}
	
	//WARNING: String.format is VERY SLOW, not for paint draw !
	public String GetLongTime(Calendar date, boolean b24HourMode)
	{
		String s = "";
		if (b24HourMode)
		{
			s = String.format("%tk:%tM", date, date);
		} else {			
			s = String.format("%tk:%tM", date, date);
			if (date.get(Calendar.AM_PM) == 0) //AM						
				s = String.format("%tl:%tM am", date, date, date.get(Calendar.AM_PM));
			if (date.get(Calendar.AM_PM) == 1) //PM						
				s = String.format("%tl:%tM pm", date, date, date.get(Calendar.AM_PM));
		}
		return s; 
	}

	public String GetResStr(int id)
	{
		return ctx.getResources().getString(id);
	}

	public void alert(String msg)
	{
		int iconId = 0;
		AlertDialog.Builder dlg = new AlertDialog.Builder(ctx);		
		dlg.setMessage(msg);
		dlg.setPositiveButton(GetResStr(R.string.msgBoxButtonOk), null);		
		dlg.setTitle(ctx.getClass().getName().toString());		
		dlg.setIcon(iconId);		
		dlg.create();
		dlg.show();
	}
	
	public void alert(int i)
	{
		Integer ii = i;
		alert(ii.toString());
	}
	
	public void ShowMsgResStr(int i, int iMsgType)
	{
		String sTitle = GetResStr(R.string.app_name);
		int iconId = 0;
		if (iMsgType == MSGTYPE_INFO)
		{
			sTitle = GetResStr(R.string.msgTypeInfo);
			iconId = R.drawable.msgicon_info;
		}
		if (iMsgType == MSGTYPE_WARNING)
		{
			sTitle = GetResStr(R.string.msgTypeWarning);
			iconId = R.drawable.msgicon_warning;
		}
		if (iMsgType == MSGTYPE_ERROR)
		{
			sTitle = GetResStr(R.string.msgTypeError);
			iconId = R.drawable.msgicon_error;
		}					
		AlertDialog.Builder dlg = new AlertDialog.Builder(ctx);		
		dlg.setMessage(GetResStr(i));
		dlg.setPositiveButton(GetResStr(R.string.msgBoxButtonOk), null);		
		dlg.setTitle(sTitle);		
		dlg.setIcon(iconId);		
		dlg.create();
		dlg.show();
	}
	
	//sql format: "dd-MM-yyyy kk:mm.ss"
	public static Calendar SqlStrToDate(String s, Calendar dateOut, Calendar dateFail)
	{
		if (s.length() == 19)
		{
			int dd = Integer.parseInt(s.substring(0, 2));
			int MM = Integer.parseInt(s.substring(3, 5));
			int yyyy = Integer.parseInt(s.substring(6, 10));
			int kk = Integer.parseInt(s.substring(11, 13));
			int mm = Integer.parseInt(s.substring(14, 16));
			int ss = Integer.parseInt(s.substring(17, 19));
			dateOut.set(yyyy, MM - 1, dd, kk, mm, ss);
			return dateOut;
		}
		return dateFail;		
	}
	
	public String DateToSqlStr(Calendar date)
	{
		return dateFormatSql.format(date.getTime());
	}
	
	public static int GetTimeAsSeconds(Calendar date)
	{
		return (date.get(Calendar.HOUR_OF_DAY) * 3600) +
			date.get(Calendar.MINUTE) * 60;
	}

	public static void ClearCalendarTime(Calendar cal)
	{
		cal.clear(Calendar.MILLISECOND);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.HOUR_OF_DAY);
	}
	
	public static boolean YearDaysEqual(Calendar calDate, Calendar calDateTo)
	{
		if (calDate.get(Calendar.YEAR) == calDateTo.get(Calendar.YEAR))
			if (calDate.get(Calendar.MONTH) == calDateTo.get(Calendar.MONTH))
				if (calDate.get(Calendar.DAY_OF_MONTH) == calDateTo.get(Calendar.DAY_OF_MONTH))
					return true;
		return false;
	}

	public static boolean YearDaysGreater(Calendar calDate, Calendar calDateTo)
	{
		if (calDate.get(Calendar.YEAR) >= calDateTo.get(Calendar.YEAR))
			if (calDate.get(Calendar.MONTH) >= calDateTo.get(Calendar.MONTH))
				if (calDate.get(Calendar.DAY_OF_MONTH) >= calDateTo.get(Calendar.DAY_OF_MONTH))
					return true;
		return false;
	}
	
	//compare time: for reminder to show
	public static boolean IsTimeOverdued(Calendar calDate, Calendar calDueDate)
	{
		if ((calDueDate.compareTo(calDate) == 0) || (calDueDate.compareTo(calDate) == 1))
			return true;
		return false;
	}
	
	//compare time: for calendar view display
	public static boolean IsInTimeRange(Calendar calDateStart, Calendar calDate, int iDurationInMinutes)
	{
		if (calDate.get(Calendar.HOUR_OF_DAY) == calDateStart.get(Calendar.HOUR_OF_DAY))
			if (calDate.get(Calendar.MINUTE) >= calDateStart.get(Calendar.MINUTE))
				if (calDate.get(Calendar.MINUTE) <= (calDateStart.get(Calendar.MINUTE) + iDurationInMinutes))
					return true;
		return false;
	}	
	
	//example key: 200712122359
	public static long GetDateTimeKey(Calendar calDate)
	{
		long lYear = calDate.get(Calendar.YEAR) * 100000000;
		long lMonth = calDate.get(Calendar.MONTH) * 1000000;
		long lDay = calDate.get(Calendar.DAY_OF_MONTH) * 10000;
		long lHour = calDate.get(Calendar.HOUR_OF_DAY) * 100;
		long lMinute = calDate.get(Calendar.MINUTE);
		return lYear + lMonth + lDay + lHour + lMinute;
	}

	public static String CapitalizeFirstLetter(String sText)
	{
		return sText.substring(0,1).toUpperCase() + sText.substring(1, sText.length()).toLowerCase();
	}

	public static String getAppVersionName(Context ctx)
	{
		try
		{
			PackageInfo pi = ctx.getPackageManager().getPackageInfo("uk.ac.ed.timetable", 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
		}
		return "";
	}
	
  public static void startAlphaAnimIn(View view)
  {
		AlphaAnimation anim = new AlphaAnimation(0.5F, 1);
		anim.setDuration(ANIM_ALPHA_DURATION);
		anim.startNow();
		view.startAnimation(anim);
  }
  
  public static void startTranslateAnimIn(View view)
  {
		TranslateAnimation anim = new TranslateAnimation(0, 0, - view.getHeight(), 0);
		anim.setDuration(ANIM_TRANSLATE_DURATION);
		anim.startNow();
		view.startAnimation(anim);
  }  
	
	/**
	 * @param cal
	 * @return an ISO 8601-compliant date able to correctly calculate week numbers...or at least
	 * provide a place to correctly implement it later if it is true that week numbers are locale-specific
	 */
  public static Calendar getIso8601Calendar(Calendar cal) {
  	Calendar result = Calendar.getInstance();
  	result.setTimeInMillis(cal.getTimeInMillis());
  	result.clear(Calendar.WEEK_OF_YEAR);
  	result.setMinimalDaysInFirstWeek(4);
  	result.setFirstDayOfWeek(Calendar.MONDAY);
  	return result;
  }
}
