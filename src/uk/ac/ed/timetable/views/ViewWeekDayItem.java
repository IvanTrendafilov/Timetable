
package uk.ac.ed.timetable.views;


import java.util.*;
import uk.ac.ed.timetable.Utils;
import android.content.*;
import android.graphics.*;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.view.*;


public class ViewWeekDayItem extends View
{
	//fields
	private final static int iStartHour = 3;
	private final static int iTotalHours = 24;
	
	//types
	private class TimeItem
	{
		public int iHour = -1;
		public int iMinute = -1;
		@SuppressWarnings("unused")
		public int iDurationInMinutes = 0;
	}

	//types
	public interface OnItemClick
	{
		public void OnClick(ViewWeekDayItem item);
	}

	//fields
	private ArrayList<TimeItem> vecTimeItems = new ArrayList<TimeItem>();	
	
	//fields
	private RectF rectDayView = new RectF();
	private RectF rectDayViewFrame = new RectF();

	//fields
	private int iHeaderHeight = 0; 
	private boolean b24HourMode = false;
	private boolean bTouchedDown = false;
	private int iTimeMarginWidth = 0;
	private int iCurrentHour = -1;
		
	private static final int iTimeFontSize = 12;
	private static final int iWeekNameFontSize = 12;
	private static final int iWeekDayFontSize = 18;
	private static final int iMargin = 1;
  private static final Typeface tfMono = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);  

	//fields
  private boolean bToday = false;
  private boolean bHoliday = false;
  
	//fields
	protected Paint mpt = null;
	protected OnItemClick itemClick = null;
	private Calendar calDate = Calendar.getInstance();
	private String sStrDayNr = "";
	private String sStrDayName = "";
	private LayoutParams lparamsItem = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);	
		
	//methods
	public ViewWeekDayItem(Context context, int iHeaderHeight)
	{
		super(context);
		this.iHeaderHeight = iHeaderHeight;
		mpt = new Paint();
		setFocusable(true);
		setLayoutParams(lparamsItem);		
	}
  
	public void SetSize(int iWidth, int iHeight)
	{
		lparamsItem.width = iWidth; 			
		lparamsItem.height = iHeight; 
	}	
	
	public void SetItemClick(OnItemClick itemClick)	
	{
		this.itemClick = itemClick;	
	}

	public void SetCurrentHour(int iCurrentHour)
	{
		this.iCurrentHour = iCurrentHour; 		
	}
	
	public static int GetSpaceWidthTime(Paint mpt, boolean b24HourMode)
	{
    mpt.setTypeface(tfMono);
		mpt.setTextSize(iTimeFontSize);
    mpt.setFakeBoldText(true);
		return 2 + (int)mpt.measureText((b24HourMode?"00":"00XX")) + 2;
	}
	
	public static int GetSpaceHeightHeader(Paint mpt)
	{
    mpt.setTypeface(null);
    mpt.setFakeBoldText(true);

    mpt.setTextSize(iWeekDayFontSize);
    int iTextHeight = (int)(-mpt.ascent() + mpt.descent()) + iMargin + iMargin + iMargin;
    
    mpt.setTextSize(iWeekNameFontSize);    
    iTextHeight += (int)(-mpt.ascent() + mpt.descent())  + iMargin;
        
		return iTextHeight + iMargin + iMargin + iMargin;
	}
	
	private String GetListShortTime(int iHour)
	{
		if (b24HourMode)
		{
			return Integer.toString(iHour);
		} else {
			String sm = "am";
			if (iHour >= 12)
				sm = "pm";
			int iDisplayHour = iHour;			
			if (iDisplayHour == 0)
				iDisplayHour = 12;			
			if (iDisplayHour > 12)
				iDisplayHour -= 12;
			return Integer.toString(iDisplayHour) + sm;
		}
	}	
	
	public void ClearTimeItems()
	{
		vecTimeItems.clear();
	}
	
	public void AddTimeItem(int iHour, int iMinute, int iDurationInMinutes)
	{
		TimeItem item = new TimeItem();
		item.iHour = iHour;
		item.iMinute = iMinute;
		item.iDurationInMinutes = iDurationInMinutes;
		vecTimeItems.add(item);
	}
	
	public void SetTimeMargin(int iTimeMarginWidth, boolean b24HourMode)
	{
		this.iTimeMarginWidth = iTimeMarginWidth;
		this.b24HourMode = b24HourMode;
	}
	
	public void SetDate(Calendar dateDay, Calendar dateToday)
	{
		this.calDate.setTimeInMillis(dateDay.getTimeInMillis());
		this.sStrDayNr = Integer.toString(calDate.get(Calendar.DAY_OF_MONTH));
		this.sStrDayName = dayStyle.getWeekDayName(calDate.get(Calendar.DAY_OF_WEEK));				
		this.bToday = IsToday(dateToday);
		this.bHoliday = IsHoliday();
	}
	
	public Calendar GetDate()
	{
		return calDate;
	}
	
	public int GetTextHeight()
	{
		return (int)(-mpt.ascent() + mpt.descent());
	}
		
	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect)
	{
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		invalidate();		
	}
		
	private boolean IsToday(Calendar calDateToday)
	{
		if (calDate.get(Calendar.YEAR) == calDateToday.get(Calendar.YEAR))
			if (calDate.get(Calendar.MONTH) == calDateToday.get(Calendar.MONTH))
				if (calDate.get(Calendar.DAY_OF_YEAR) == calDateToday.get(Calendar.DAY_OF_YEAR))
					return true;
		return false;
	}

	private boolean IsHoliday()
	{
		if (calDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
			return true;
		if (calDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
			return true;		
		if ((calDate.get(Calendar.MONTH) == Calendar.JANUARY) && ((calDate.get(Calendar.DAY_OF_MONTH) == 1)))
			return true;
		return false;
	}

	private void initRectangles()
	{
		//init day view rectangle frame
		rectDayViewFrame.set(iMargin, iMargin, this.getWidth() - iMargin, this.getBottom() - iMargin);						
		rectDayView.set(rectDayViewFrame);		
		rectDayView.top = iMargin + iHeaderHeight + iMargin + iMargin + iMargin;		
		//add left time margin
		rectDayViewFrame.left += iTimeMarginWidth;
		rectDayView.left += iTimeMarginWidth;
	}

	private void drawDayHeader(Canvas canvas)
	{		
		//text
		mpt.setTypeface(null);
		mpt.setAntiAlias(true);
		mpt.setFakeBoldText(true);

		//draw day number
		mpt.setUnderlineText(false);
		if (bToday)
			mpt.setUnderlineText(true);			

		mpt.setColor(dayStyle.getColorTextHeaderLight(bHoliday, bToday));					
		mpt.setTextSize(iWeekDayFontSize);		
		final int iDayTextHeight = - (int)mpt.ascent() + (int)mpt.descent();		
		final int iDayNrPosX = (int)rectDayViewFrame.left + ((int)rectDayViewFrame.width() >> 1) - ((int)mpt.measureText(sStrDayNr) >> 1);			
 		canvas.drawText(sStrDayNr, iDayNrPosX, rectDayViewFrame.top + iDayTextHeight, mpt); 		

		//draw day name
		mpt.setUnderlineText(false);
		mpt.setColor(dayStyle.getColorTextHeader(bHoliday, bToday));					
		mpt.setTextSize(iWeekNameFontSize);		
		final int iNameTextHeight = - (int)mpt.ascent() + (int)mpt.descent();
		final int iDayNamePosX = (int)rectDayViewFrame.left + ((int)rectDayViewFrame.width() >> 1) - ((int)mpt.measureText(sStrDayName) >> 1);			
 		canvas.drawText(sStrDayName, iDayNamePosX, iMargin + iDayTextHeight + iMargin + iMargin + iMargin + iNameTextHeight, mpt);
	}
	
	private void drawDayViewBackground(Canvas canvas, boolean bFocused)
	{
		//frame
		mpt.setColor(dayStyle.getColorFrame(bHoliday, false));		
		canvas.drawRoundRect(rectDayViewFrame, 2, 2, mpt);				
		
		//background
		if (bFocused)
		{
			
			LinearGradient lGradBkg = new LinearGradient(rectDayView.left, 0, rectDayView.right, 0,
					dayStyle.iColorBkgFocusDark, dayStyle.iColorBkgFocusLight, Shader.TileMode.CLAMP);
			mpt.setShader(lGradBkg);
			canvas.drawRoundRect(rectDayView, 2, 2, mpt);
			mpt.setShader(null);
			
		} else {
			
			mpt.setColor(dayStyle.getColorBkg(bHoliday, bToday));
			canvas.drawRoundRect(rectDayView, 2, 2, mpt);			
		}
	}
	
	private void drawHourItems(Canvas canvas, boolean bFocused)
	{
		mpt.setTextSize(iTimeFontSize);
		mpt.setAntiAlias(true);		
		mpt.setTypeface(tfMono);	
		mpt.setShader(null);
		mpt.setFakeBoldText(true);
		
		final float fHourItemHeight = (rectDayView.height() / (iTotalHours - iStartHour));
				
		//draw hours		
		for (int iHour = iStartHour; iHour < iTotalHours; iHour++)
		{						
			final float fItemPosY = rectDayView.top + (fHourItemHeight * (iHour - iStartHour));
									
			//draw hour
			if ((iTimeMarginWidth > 0) && ((iHour % 3) == 0))
			{
				final String sTime = GetListShortTime(iHour);				
				float fTextPosX = iTimeMarginWidth - mpt.measureText(sTime) - 3;				
				float fTextPosY = fItemPosY - mpt.ascent();				
				mpt.setColor(dayStyle.iColorTextHour);
				
				mpt.setUnderlineText(false);
				if (iCurrentHour == iHour)
					mpt.setUnderlineText(true);
				
		 		canvas.drawText(sTime, fTextPosX, fTextPosY, mpt);			
			}
		}
		
		//draw time items
		mpt.setAntiAlias(false);
		mpt.setShader(null);
		mpt.setUnderlineText(false);
		
		final int iViewTop = (int)rectDayView.top + 1;
		final int iViewBottom = (int)rectDayView.bottom - 1;		
		
		//iterate time items
		final int iTimeItemHeight = (int)(fHourItemHeight) >> 1;
		for (int index = 0; index < vecTimeItems.size(); index++)
		{
			TimeItem item = vecTimeItems.get(index);				

			//draw time item
			final float fItemPosY = iViewTop + (fHourItemHeight * (item.iHour - iStartHour));
			final float fMinuteOffset = (fHourItemHeight / 60) * item.iMinute;				
			int iTimeItemTop = (int)(fItemPosY + fMinuteOffset);

			//correct item position if out of time range
			if (iTimeItemTop + iTimeItemHeight > iViewBottom)
				iTimeItemTop = iViewBottom - iTimeItemHeight;
			
			if (iTimeItemTop + iTimeItemHeight < iViewTop)
				continue;
			
			//outer frame
			mpt.setColor(dayStyle.getColorTimeItem(bFocused));
			canvas.drawRect(rectDayView.left + 1, iTimeItemTop, rectDayView.right - 1, iTimeItemTop + iTimeItemHeight, mpt);
			
			//inner bkg
			mpt.setColor(dayStyle.getColorTimeItemBkg(bFocused));
 			canvas.drawRect(rectDayView.left + 1 + 1, iTimeItemTop + 1, rectDayView.right - 1 - 1, iTimeItemTop + iTimeItemHeight - 1, mpt);								
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		initRectangles();
		
		final boolean bFocused = IsViewFocused();
		
		drawDayViewBackground(canvas, bFocused);
		drawDayHeader(canvas);
		drawHourItems(canvas, bFocused);
	}
		
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		boolean bResult = super.onKeyDown(keyCode, event);	
		if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER) || (keyCode == KeyEvent.KEYCODE_ENTER))
		{
			doItemClick();
		}
		return bResult;
	}
	
	public void doItemClick()
	{
		if (itemClick != null)
				itemClick.OnClick(this);	
	}	
	
  public boolean IsViewFocused()
  {
  	return (this.isFocused() || bTouchedDown);
  }
	
  @Override
	public boolean onTouchEvent(MotionEvent event)
  {
  	boolean bHandled = false;
  	if (event.getAction() == MotionEvent.ACTION_DOWN)
  	{
  		bHandled = true;
  		bTouchedDown = true;
  		invalidate();
  		Utils.startAlphaAnimIn(ViewWeekDayItem.this);
  	}
  	if (event.getAction() == MotionEvent.ACTION_CANCEL)
  	{
  		bHandled = true;
  		bTouchedDown = false;
  		invalidate();
  	}
  	if (event.getAction() == MotionEvent.ACTION_UP)
  	{
  		bHandled = true;
  		bTouchedDown = false;
  		invalidate();
  		doItemClick();
  	}
  	return bHandled;
  }
	  
}
