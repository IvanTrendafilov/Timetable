
package uk.ac.ed.timetable.views;


import java.util.Calendar;
import uk.ac.ed.timetable.Utils;
import android.content.Context;
import android.graphics.*;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;


public class ViewMonthWeekItem extends View
{
	//types
	public interface OnItemClick
	{
		public void OnClick(ViewMonthWeekItem item);
	}
	
	//fields
  private final static Typeface tfMono = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);
  
	//fields
	private RectF rectDayHeaderFrame = new RectF();  
	private RectF rectDayView = new RectF();
	
	//fields
	private static final int iMargin = 1;
	private final static int iSpace = 4;
	private final static int iWeekNrFontSize = 12;
	private final static int iDayHeaderFontSize = 12;
	private final static int iAlphaInactiveMonth = 0x40;

	//fields
	protected Paint mpt = null;
	protected OnItemClick itemClick = null;
	private boolean bEnableDaysHeader = false;
	private LayoutParams lparamsItem = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

	//fields
	private String sWeekNr = "";
	private int[] vecWeekDaysId = new int[7];
	private int[] vecDayNumbers = new int[7];	
	private byte[] vecDayState = new byte[7];
	
	//fields
	private int iHeaderHeight = 0; 
	private int iWeekNrMarginWidth = 0;
	private int iWeekDayWidth = 0;
	private boolean bTouchedDown = false;
	private Calendar calWeekStart = Calendar.getInstance();

	private int dayOfWeek;
		
	//methods
	public ViewMonthWeekItem(Context context, int iHeaderHeight, int iWeekNrMarginWidth)
	{
		super(context);
		this.iHeaderHeight = iHeaderHeight;
		this.iWeekNrMarginWidth = iWeekNrMarginWidth;		
		mpt = new Paint();
		setFocusable(true);
		setLayoutParams(lparamsItem);
	}
	
	public static int GetSpaceWidthWeekNr(Paint mpt)
	{
    mpt.setTypeface(tfMono);
		mpt.setTextSize(iWeekNrFontSize);
    mpt.setFakeBoldText(true);
    return iSpace + (int)mpt.measureText("00") + iSpace;		
	}
	
	public static int GetSpaceHeightHeader(Paint mpt)
	{
    mpt.setTypeface(null);
		mpt.setTextSize(iDayHeaderFontSize);
    mpt.setFakeBoldText(true);		
		return ((int)(-mpt.ascent() + mpt.descent())) + iSpace + iSpace + iMargin + iMargin;
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
	
	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect)
	{
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		invalidate();		
	}
	
	public void setDayData(int iDay, boolean bDayHasData)
	{
		vecDayState[iDay] |= (bDayHasData)?0x08:0x00;
	}	

	public void SetWeekStartDate(boolean bEnableDaysHeader, Calendar dateStartDay, int iActiveMonth, Calendar dateToday)
	{
		this.bEnableDaysHeader = bEnableDaysHeader;
		this.calWeekStart.setTimeInMillis(dateStartDay.getTimeInMillis());
		this.sWeekNr = Integer.toString(Utils.getIso8601Calendar(calWeekStart).get(Calendar.WEEK_OF_YEAR));
		
		final int iTodayDateYear = dateToday.get(Calendar.YEAR);
		final int iTodayDateMonth = dateToday.get(Calendar.MONTH);
		final int iTodayDateDay = dateToday.get(Calendar.DAY_OF_MONTH);		
		
		//iterate week days
		for (int iDay = 0; iDay < 7; iDay++)
		{			
			//get date values
			final int iDateYear = calWeekStart.get(Calendar.YEAR);
			final int iDateMonth = calWeekStart.get(Calendar.MONTH);
			final int iDateDay = calWeekStart.get(Calendar.DAY_OF_MONTH);
			final int iDateDayOfWeek = calWeekStart.get(Calendar.DAY_OF_WEEK);
					
			final boolean bToday = ((iDateYear == iTodayDateYear) && (iDateMonth == iTodayDateMonth) && (iDateDay == iTodayDateDay));				
			
			boolean bHoliday = false;
			if (iDateDayOfWeek == Calendar.SATURDAY)
				bHoliday = true;
			if (iDateDayOfWeek == Calendar.SUNDAY)
				bHoliday = true;
			if ((iDateMonth == Calendar.JANUARY) && (iDateDay == 1))
				bHoliday = true;

			final boolean bActiveMonth = (iDateMonth == iActiveMonth);
			
			byte byteDayState = 0;
			
			byteDayState |= (bToday)?0x01:0x00;
			byteDayState |= (bHoliday)?0x02:0x00;
			byteDayState |= (bActiveMonth)?0x04:0x00;
			
			vecDayState[iDay] = byteDayState; 			
			
			vecDayNumbers[iDay] = iDateDay;
			
			if (this.bEnableDaysHeader)
				vecWeekDaysId[iDay] = iDateDayOfWeek;
			
			calWeekStart.add(Calendar.DAY_OF_YEAR, 1);
		}

		//store week start date
		this.calWeekStart.setTimeInMillis(dateStartDay.getTimeInMillis());
	}

	public Calendar getWeekStartDate()
	{
		return calWeekStart;
	}
	
	public float getTextHeight()
	{
		return (-mpt.ascent() + mpt.descent());
	}
	
	private void drawWeekNr(Canvas canvas)
	{
		mpt.setAntiAlias(true);
		mpt.setShader(null);
		mpt.setTypeface(tfMono);
		mpt.setTextSize(iWeekNrFontSize);
	  mpt.setFakeBoldText(true);
		mpt.setColor(dayStyle.iColorTextWeek);

  	final int iPosX = iWeekNrMarginWidth - (int)mpt.measureText(sWeekNr) - iSpace;
  	
	  canvas.drawText(sWeekNr, iPosX, getTextHeight() + iHeaderHeight, mpt);
	}
	
	private void initRectangles()
	{
		//int iTotalHeaderHeight = iHeaderHeight;
		iWeekDayWidth = (this.getWidth() - iWeekNrMarginWidth - iMargin - iMargin) / 7; 
					
		//init header rectangle
		if (iHeaderHeight != 0)
		{
			rectDayHeaderFrame.top = iMargin;
			rectDayHeaderFrame.left = iWeekNrMarginWidth + iMargin;
			rectDayHeaderFrame.right = this.getWidth() - iMargin;
			rectDayHeaderFrame.bottom = rectDayHeaderFrame.top + iHeaderHeight - iMargin - iMargin;			
		}
		
		//init day view rectangles
		rectDayView.set(iWeekNrMarginWidth + iMargin, iMargin + iHeaderHeight, this.getWidth() - iMargin, this.getHeight() - iMargin);		
	}

	private void drawDayHeader(Canvas canvas, int iDay, boolean bToday, boolean bHoliday)
	{		
		if (this.bEnableDaysHeader)
		{
			//background
			mpt.setColor(dayStyle.getColorFrame(bHoliday, bToday));
			canvas.drawRoundRect(rectDayHeaderFrame, 2, 2, mpt);	

			//text
			mpt.setTypeface(null);
			mpt.setTextSize(iDayHeaderFontSize);
			mpt.setAntiAlias(true);
			mpt.setFakeBoldText(true);
			mpt.setColor(dayStyle.getColorTextHeader(bHoliday, bToday));					

			final int iTextPosY = (int)getTextHeight();			
			final String sDayName = dayStyle.getWeekDayName(vecWeekDaysId[iDay]);
			
			//draw day name
			final int iDayNamePosX = (int)rectDayHeaderFrame.left + ((int)rectDayHeaderFrame.width() >> 1) - ((int)mpt.measureText(sDayName) >> 1);			
			canvas.drawText(sDayName, iDayNamePosX, rectDayHeaderFrame.top + iTextPosY + 2, mpt);
		}
	}
	
	private void drawDayView(Canvas canvas, boolean bActiveMonth, boolean bFocused, boolean bToday, boolean bHoliday)
	{
		mpt.setAlpha(0xff);
		
		if (bFocused)
		{
			
			LinearGradient lGradBkg = new LinearGradient(rectDayView.left, 0, rectDayView.right, 0,
					dayStyle.iColorBkgFocusDark, dayStyle.iColorBkgFocusLight, Shader.TileMode.CLAMP);
			mpt.setShader(lGradBkg);
			canvas.drawRoundRect(rectDayView, 2, 2, mpt);
			mpt.setShader(null);
			
		} else {
			
			mpt.setColor(dayStyle.getColorBkg(bHoliday, bToday));
			if (!bActiveMonth)
				mpt.setAlpha(iAlphaInactiveMonth);			
			canvas.drawRoundRect(rectDayView, 2, 2, mpt);
			
		}
	}
	
	public void drawDayNumber(Canvas canvas, int iDayNumber, boolean bActiveMonth, boolean bFocused, boolean bToday, boolean bHoliday)
	{
		final String sDate = Integer.toString(iDayNumber);

		//draw day number
		mpt.setTypeface(null);
		mpt.setAntiAlias(true);
		mpt.setShader(null);
		mpt.setFakeBoldText(true);

		mpt.setAlpha(0xff);
		
		mpt.setUnderlineText(false);
		if (bToday)
			mpt.setUnderlineText(true);			

		float iFontSize = (int)rectDayView.height() / 1.1F;
		if (iFontSize > 20)
			iFontSize = 20;		
		mpt.setTextSize(iFontSize);
		
		final int iTextPosX = (int)rectDayView.right - (int)mpt.measureText(sDate) - iSpace - (iSpace >> 1);
		final int iTextPosY = (int)rectDayView.bottom + (int)(-mpt.ascent()) - (int)getTextHeight() - iSpace; 		

		//draw text
	  if (bFocused)
	  	mpt.setColor(dayStyle.iColorTextFocused);
	  else
			mpt.setColor(dayStyle.getColorText(bHoliday, bToday));	  	  
	  if (!bActiveMonth)
			mpt.setAlpha(iAlphaInactiveMonth);
		
 		canvas.drawText(sDate, iTextPosX, iTextPosY + iMargin, mpt);
 		
		mpt.setUnderlineText(false); 		
	}
	
	private void initDayRectangle(RectF src, int iDay)
	{
		src.left = iWeekNrMarginWidth + iMargin + (iDay * iWeekDayWidth);						
		src.right = src.left + iWeekDayWidth - iMargin - iMargin;
	}
		
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		initRectangles();
	
		drawWeekNr(canvas);
				
		for (int iDay = 0; iDay < 7; iDay++)
		{
			final int iDayNumber = vecDayNumbers[iDay];
			final boolean bFocused = IsViewFocused();

			final byte byteDayState = vecDayState[iDay]; 			
			
			final boolean bToday = ((byteDayState & 0x01) != 0);
			final boolean bHoliday = ((byteDayState & 0x02) != 0);
			final boolean bActiveMonth = ((byteDayState & 0x04) != 0); 
			final boolean bDayHasData = ((byteDayState & 0x08) != 0);

			initDayRectangle(rectDayHeaderFrame, iDay);
			drawDayHeader(canvas, iDay, bToday, bHoliday);
						
			initDayRectangle(rectDayView, iDay);
			drawDayView(canvas, bActiveMonth, bFocused, bToday, bHoliday);			
			drawDayNumber(canvas, iDayNumber, bActiveMonth, bFocused, bToday, bHoliday);
			
			//draw data icon
			if (bDayHasData)
			{
				mpt.setShader(null);
				mpt.setAntiAlias(true);				
				mpt.setAlpha(0xff);

			  if (bFocused)
			  	mpt.setColor(dayStyle.iColorTextFocused);
			  else
					mpt.setColor(dayStyle.getColorText(bHoliday, bToday));
			  if (!bActiveMonth)
					mpt.setAlpha(iAlphaInactiveMonth);

				canvas.drawCircle(rectDayView.left + 9, rectDayView.top + 9, 3, mpt);
			}			
		}
		
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
  		this.dayOfWeek = calculateDayOfWeek(event.getRawX());
  		Utils.startAlphaAnimIn(ViewMonthWeekItem.this);
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

	/**
   * @param rawX - the X-axis screen tap offset
   * @return - an index between 0 and 6, indicating which week day was selected
   */
  private int calculateDayOfWeek(Float rawX) {
		int result = (int) ((rawX - iWeekNrMarginWidth -2 * iMargin) / iWeekDayWidth);
		return result;
  }

  public int getDayOfWeek()
	{
		return dayOfWeek;
	}
}