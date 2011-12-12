
package uk.ac.ed.timetable.views;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.*;


public class ViewTodayItemCourse extends ViewTodayItem
{
	//fields
	private final static int iColorTime = 0xFFFF8800;
	private final static int iColorTimeFocused = 0xFFFFCC66;	
	private final static int iColorMark = 0xFFAA5500;
	private static final String sStrTime = "00:00";
	private static final String sStrMinutes = ":00";
	private static final String sStrUSTimeMark = "mm"; //am/pm
	private static final String sStrUSTimeAM = "am";
	private static final String sStrUSTimePM = "pm";
  private final static Typeface tfMono = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);

	//default hour width, full time width, am/pm width
	private int iSpaceWidthUSTimeMark = 0;
	private int iSpaceWidthTimeArea = 0;
	private int iSpaceWidthTime = 0;
	  
	//fields
	private int iHour = 0;
	private int iMinutes = 0;
	private boolean b24HourMode = false;  
	private boolean bAlarm = false;
	private boolean bRepeat = false;
	private boolean bShowMinutesOnly = false;

	//methods
	public ViewTodayItemCourse(Context context)
	{
		super(context);
	}
	
	public void SetItemData(String sText, boolean bAlarm, boolean bRepeat)
	{
		SetText(sText);
		this.bAlarm = bAlarm;
		this.bRepeat = bRepeat;
	}
	
	public void SetItemTime(int iHour, int iMinutes, boolean bShowMinutesOnly, boolean b24HourMode, int iSpaceWidthTime, int iSpaceWidthMinutes, int iSpaceWidthUSTimeMark)
	{
		this.iHour = iHour;
		this.iMinutes = iMinutes;		
		this.b24HourMode = b24HourMode;
		this.bShowMinutesOnly = bShowMinutesOnly;		
		this.iSpaceWidthUSTimeMark = (b24HourMode?0:(iFrame + iSpaceWidthUSTimeMark));
		if (bShowMinutesOnly)
		{
			this.iSpaceWidthTime = iSpaceWidthMinutes;			
		} else {
			this.iSpaceWidthTime = iSpaceWidthTime + (b24HourMode?0:(iFrame + iSpaceWidthUSTimeMark));			
		}
		this.iSpaceWidthTimeArea = iSpace + this.iSpaceWidthTime + iSpace;
	}
	
	public static int GetSpaceWidthTime(Paint mpt)
	{
    mpt.setTypeface(tfMono);
		mpt.setTextSize(ViewTodayItem.fTextSize);
    mpt.setFakeBoldText(true);
		return (int)mpt.measureText(sStrTime);		
	}

	public static int GetSpaceWidthMinutes(Paint mpt)
	{
    mpt.setTypeface(tfMono);
		mpt.setTextSize(ViewTodayItem.fTextSize);
    mpt.setFakeBoldText(true);
		return (int)mpt.measureText(sStrMinutes);		
	}
	
	public static int GetSpaceWidthUSTimeMark(Paint mpt)
	{
    mpt.setTypeface(tfMono);
		mpt.setTextSize(ViewTodayItem.fTextSize);		
    mpt.setFakeBoldText(false);
		return (int)mpt.measureText(sStrUSTimeMark);		
	}	
	
	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect)
	{
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		
		//refresh parent view for Day View
		ViewParent parent = getParent(); 
		if (parent != null)
		{
			if (parent instanceof ViewDayHourItem)
			{
				try
				{
					ViewDayHourItem item = (ViewDayHourItem)parent;
					if (item != null)
						item.invalidate();
				} catch (Exception e) {					
				}
			}
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);		
		
		mpt.setAntiAlias(true);
		mpt.setTextSize(fTextSize);		
    mpt.setTypeface(tfMono);
    mpt.setFakeBoldText(true);
		mpt.setShader(null);
		mpt.setUnderlineText(false);

		final boolean bIsFocused = hasFocus();
  	final int iHeight = getHeight() - iFrame;
  	final int iWidth = getWidth();  	
		final int iTimePosY = iMargin - (int)mpt.ascent();

		//draw time
		DrawTime(canvas, iTimePosY, iWidth, iHeight, bIsFocused);
		
		//draw text
    int iTextClipWidth = getWidth() - iSpace;
		if (bAlarm)
			iTextClipWidth -= iIconW;
		if (bRepeat)
			iTextClipWidth -= iIconW;

		final int iHeaderOffsetX = (bShowMinutesOnly?0:ViewTodayItemHeader.GetTextPosX());		
    final int iTextPosX = iHeaderOffsetX + iSpaceWidthTimeArea;
		
    mpt.setTypeface(null);
		DrawItemText(canvas, iTextPosX, iTimePosY, iTextClipWidth, iColorTextActive_enabled);
		
		//draw icons
		int iIconX = getWidth();
		int iIconY = (this.getHeight() >> 1) - (iIconH >> 1);

		//draw icon repeat
		if (bRepeat)
		{	
			iIconX -= iIconW;
			iconRepeat.setBounds(iIconX, iIconY, iIconX + iIconW, iIconY + iIconH);									
			iconRepeat.draw(canvas);
		}
		//draw icon alarm
		if (bAlarm)
		{
			iIconX -= iIconW;
			iconAlarm.setBounds(iIconX, iIconY, iIconX + iIconW, iIconY + iIconH);
			iconAlarm.draw(canvas);
		}		
	}
	
	private String GetTimeString()
	{				
		if (bShowMinutesOnly)
		{
			return GetMinutesString();
		} else {			
			if (b24HourMode)
			{
				return Integer.toString(iHour) + GetMinutesString();
			} else {
				int iDisplayHour = iHour;
				if (iDisplayHour == 0)
					iDisplayHour = 12;			
				if (iDisplayHour > 12)
					iDisplayHour -= 12;
				return Integer.toString(iDisplayHour) + GetMinutesString();
			}
		}		
	}
	
	private String GetMinutesString()
	{		
		if (iMinutes > 9)
			return ":" + Integer.toString(iMinutes);
		return ":0" + Integer.toString(iMinutes);
	}

	private String GetUSTimeMark()
	{
		if (iHour >= 12)
			return sStrUSTimePM;
		return sStrUSTimeAM;
	}
	
	private void DrawTime(Canvas canvas, int iTimePosY, int iWidth, int iHeight, boolean bIsFocused)
	{
		mpt.setAntiAlias(true);
		mpt.setTextSize(fTextSize);		
    mpt.setTypeface(tfMono);
    mpt.setFakeBoldText(true);
		mpt.setShader(null);
		mpt.setUnderlineText(false);
				
		//draw time
		final String sTime = GetTimeString();		
		final int iStrTimeWidth = (int)mpt.measureText(sTime);
				
		int iTimePosX = 0;			
		if (bShowMinutesOnly)
		{
			iTimePosX = iSpace;
		} else {
			iTimePosX = iSpaceWidthTimeArea - iStrTimeWidth - iSpaceWidthUSTimeMark - iSpace;			
			iTimePosX += ViewTodayItemHeader.GetTextPosX(); 
		}
		
		mpt.setColor(iColorTime);
		if (bIsFocused)
			mpt.setColor(iColorTimeFocused);
				
		canvas.drawText(sTime, iTimePosX, iTimePosY, mpt);
				
		//draw minutes
		int iMarkPosX = iTimePosX + iStrTimeWidth + iFrame;
		
		//draw us time mark
		if ((!b24HourMode) && (!bShowMinutesOnly))
		{						
	    mpt.setFakeBoldText(false);
			mpt.setColor(iColorMark);
			if (bIsFocused)
				mpt.setColor(iColorTimeFocused);					
			canvas.drawText(GetUSTimeMark(), iMarkPosX, iTimePosY, mpt);
		}
	}
	

}
