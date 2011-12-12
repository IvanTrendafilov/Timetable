
package uk.ac.ed.widgets;


import java.util.*;
import uk.ac.ed.timetable.Utils;
import android.content.*;
import android.view.*;
import android.widget.LinearLayout.LayoutParams;
import android.graphics.*;


public class DateWidgetDayCell extends View
{
	//types
	public interface OnItemClick
	{
		public void OnClick(DateWidgetDayCell item);
	}
		
	//fields
	private final static float fTextSize = 22;	
	private final static int iMargin = 1;
	private final static int iAlphaInactiveMonth = 0x88;
	
	//fields
	private int iDateYear = 0;
	private int iDateMonth = 0;
	private int iDateDay = 0;
		
	//fields
	private OnItemClick itemClick = null;
	private Paint pt = new Paint();
	private RectF rect = new RectF();
	private String sDate = "";
	
	//fields
	private boolean bSelected = false;	
	private boolean bIsActiveMonth = false;
	private boolean bToday = false;
	private boolean bHoliday = false;
	private boolean bTouchedDown = false;	
	
	
	//methods
	public DateWidgetDayCell(Context context, int iWidth, int iHeight)
	{
		super(context);
    setFocusable(true);    
		setLayoutParams(new LayoutParams(iWidth, iHeight));
	}
	
	public boolean getSelected()
	{
		return this.bSelected;
	}
	
	@Override
	public void setSelected(boolean bEnable)
	{
		if (this.bSelected != bEnable)
		{
			this.bSelected = bEnable;
			this.invalidate();
		}
	}
	
	public void setData(int iYear, int iMonth, int iDay, boolean bToday, boolean bHoliday, int iActiveMonth)
	{
		iDateYear = iYear;
		iDateMonth = iMonth;
		iDateDay = iDay;
		
		this.sDate = Integer.toString(iDateDay);
		this.bIsActiveMonth = (iDateMonth == iActiveMonth);
		this.bToday = bToday;
		this.bHoliday = bHoliday;
	}
			
	public void setItemClick(OnItemClick itemClick)	
	{
		this.itemClick = itemClick;	
	}
		
	private int getTextHeight()
	{
		return (int)(-pt.ascent() + pt.descent());
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
 
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		boolean bResult = super.onKeyUp(keyCode, event);
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
	
	public Calendar getDate()
	{
		Calendar calDate = Calendar.getInstance();
		calDate.clear();
		calDate.set(Calendar.YEAR, iDateYear);
		calDate.set(Calendar.MONTH, iDateMonth);
		calDate.set(Calendar.DAY_OF_MONTH, iDateDay);
		return calDate;
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		//init rectangles
		rect.set(0, 0, this.getWidth(), this.getHeight());
		rect.inset(1, 1);

		//drawing
		final boolean bFocused = IsViewFocused();
		
		drawDayView(canvas, bFocused);
		drawDayNumber(canvas, bFocused);
	}
	
	private void drawDayView(Canvas canvas, boolean bFocused)
	{
		if (bSelected || bFocused)
		{
			LinearGradient lGradBkg = null;

			if (bFocused)
			{
				lGradBkg = new LinearGradient(rect.left, 0, rect.right, 0,
						dayStyle.iColorBkgFocusDark, dayStyle.iColorBkgFocusLight, Shader.TileMode.CLAMP);
			}
			
			if (bSelected)
			{
				lGradBkg = new LinearGradient(rect.left, 0, rect.right, 0,
						dayStyle.iColorBkgSelectedDark, dayStyle.iColorBkgSelectedLight, Shader.TileMode.CLAMP);
			}
									
			if (lGradBkg != null)
			{
				pt.setShader(lGradBkg);
				canvas.drawRect(rect, pt);
			}
			
			pt.setShader(null);
			
		} else {
			
			pt.setColor(dayStyle.getColorBkg(bHoliday, bToday));
			if (!bIsActiveMonth)
				pt.setAlpha(iAlphaInactiveMonth);
			canvas.drawRect(rect, pt);
		}
	}
	
	public void drawDayNumber(Canvas canvas, boolean bFocused)
	{	
		//draw day number
		pt.setTypeface(null);
		pt.setAntiAlias(true);
		pt.setShader(null);
		pt.setFakeBoldText(true);	
		pt.setTextSize(fTextSize);
		
		pt.setUnderlineText(false);
		if (bToday)
			pt.setUnderlineText(true);

		int iTextPosX = (int)rect.right - (int)pt.measureText(sDate);
		int iTextPosY = (int)rect.bottom + (int)(-pt.ascent()) - getTextHeight();
		
		iTextPosX -= ((int)rect.width() >> 1) - ((int)pt.measureText(sDate) >> 1);
		iTextPosY -=  ((int)rect.height() >> 1) - (getTextHeight() >> 1);

		//draw text
	  if (bSelected || bFocused)
	  {
	  	if (bSelected)
	  		pt.setColor(dayStyle.iColorTextSelected);
	  	if (bFocused)
	  		pt.setColor(dayStyle.iColorTextFocused);
	  } else {
			pt.setColor(dayStyle.getColorText(bHoliday, bToday));
	  }

	  if (!bIsActiveMonth)
			pt.setAlpha(iAlphaInactiveMonth);
		
 		canvas.drawText(sDate, iTextPosX, iTextPosY + iMargin, pt);
 		
 		pt.setUnderlineText(false); 		
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
  		Utils.startAlphaAnimIn(DateWidgetDayCell.this);
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
