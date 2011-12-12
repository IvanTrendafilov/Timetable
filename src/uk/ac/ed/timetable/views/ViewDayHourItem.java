
package uk.ac.ed.timetable.views;


import android.content.*;
import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import uk.ac.ed.timetable.*;

public class ViewDayHourItem extends LinearLayout
{
	//inner invisible view for touch mode
	public class InvisibleView extends View
	{
		//fields
		private final int iFrame = 2;
		
		//methods
		public InvisibleView(Context context)
		{
			super(context);
	    setFocusable(false);
	    LinearLayout.LayoutParams layParams = 
	    	new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
	    this.setLayoutParams(layParams);
		}
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
		{
	    setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));				 
		}		
	  private int measureWidth(int measureSpec)
	  {
	    return MeasureSpec.getSize(measureSpec);
	  }		 
	  private int measureHeight(int measureSpec)
	  {
		  return iFrame + iItemTextHeight + iFrame + iFrame;	  	
	  }		
	}

	//types
	public interface OnItemClick
	{
		public void OnClick(ViewDayHourItem item);
	}
	
	//fields
	private static final String sStrHour = "00";
	private static final String sStrTime = "00:00";
	private static final String sStrUSTimeMark = "mm"; //am/pm
	private static final String sStrUSTimeAM = "am";
	private static final String sStrUSTimePM = "pm";
	
	//fields
	private static final int iSpace = 4;
	private static final int iFrame = 2;
  private static final Typeface tfMono = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);
	private static final float fTextSize = 20;

	//fields
	private boolean b24HourMode = false;
	private int iHour = 0;
	private int iCurrentHour = -1;
	protected OnItemClick itemClick = null;
	
	//default hour width, full time width, am/pm width
	private int iSpaceWidthUSTimeMark = 0;
	private int iSpaceWidthHourWithUSTimeMark = 0;	
	private int iSpaceWidthTimeWithUSTimeMark = 0;
	private int iSpaceWidthHourArea = 0;
	private int iSpaceWidthTimeArea = 0;
	private int iItemTextHeight = 0;
	
	//colors
	private final static int iColorTime = 0xFFFF8800;
	private final static int iColorMark = 0xFFAA5500;
	private final static int iColorTimeFocused = 0xFFFFCC66;
	private final static int iColorTimeTouch = 0xFF222222;
	private final int iTouchSliderColor = 0xBBFF8800;
	private final int iTouchPatternColor = 0xFF995500;
	
	//fields
	protected Paint mpt = null;

	//touch mode fields
	private static InvisibleView invView = null;
	private boolean bTouchedDown = false;
	private RectF rtTouchRect = new RectF();
	private RectF rtTimeTouchRect = new RectF();
	private RectF rtTouchSliderRect = new RectF();
	private RectF rectSliderArrow = new RectF();
	private int iTouchPosX = 0;
	private int iLastTimeMinutes = 0;

	//methods
	public ViewDayHourItem(Context context, int iHour, int iItemTextHeight)
	{
		super(context);

		this.iHour = iHour;
		this.iItemTextHeight = iItemTextHeight;
		
		mpt = new Paint();
		mpt.setAntiAlias(true);
		mpt.setTextSize(fTextSize);
		
		setWillNotDraw(false);
		
		setOrientation(LinearLayout.VERTICAL);
		LayoutParams lparams = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		setLayoutParams(lparams);

		//invisible view
		if (invView == null)
		{
			invView = new InvisibleView(context);
			invView.setVisibility(View.INVISIBLE);
		}
	}
  	
	public void SetItemClick(OnItemClick itemClick)	
	{
		this.itemClick = itemClick;	
	}

	public void SetItemData(boolean b24HourMode, int iSpaceWidthHour, int iSpaceWidthTime, int iSpaceWidthUSTimeMark)
	{
		this.b24HourMode = b24HourMode;
  	UpdateTimeFormat(b24HourMode, iSpaceWidthHour, iSpaceWidthTime, iSpaceWidthUSTimeMark);
		UpdateHeight();
	}
	
	public void SetCurrentHour(int iCurrentHour)
	{
		this.iCurrentHour = iCurrentHour; 		
	}
	
	public void UpdateTimeFormat(boolean b24HourMode, int iSpaceWidthHour, int iSpaceWidthTime, int iSpaceWidthUSTimeMark)
	{
		this.iSpaceWidthUSTimeMark = (b24HourMode?0:iSpaceWidthUSTimeMark);
		this.iSpaceWidthHourWithUSTimeMark = iSpaceWidthHour + (b24HourMode?0:(iSpaceWidthUSTimeMark + iFrame));
		this.iSpaceWidthTimeWithUSTimeMark = iSpaceWidthTime + (b24HourMode?0:(iSpaceWidthUSTimeMark + iFrame));
		this.iSpaceWidthHourArea = iSpace + iSpaceWidthHourWithUSTimeMark + iSpace;
		this.iSpaceWidthTimeArea = iSpace + iSpaceWidthTimeWithUSTimeMark + iSpace;
  	setPadding(iSpaceWidthHourArea, iFrame + iFrame, iFrame, iFrame + iFrame);
	}
	
	
	public int GetHour()
	{
		return iHour;
	}
	
	public int GetMinutes()
	{
		return iLastTimeMinutes;
	}

	public static int GetTextHeight(Paint mpt)
	{
    mpt.setTypeface(null);
		mpt.setTextSize(fTextSize);		
    mpt.setFakeBoldText(true);
		return (int)(-mpt.ascent() + mpt.descent());
	}
	
	public static int GetSpaceWidthHour(Paint mpt)
	{
    mpt.setTypeface(tfMono);
		mpt.setTextSize(fTextSize);		
    mpt.setFakeBoldText(true);
		return (int)mpt.measureText(sStrHour);
	}
	
	public static int GetSpaceWidthTime(Paint mpt)
	{
    mpt.setTypeface(tfMono);
		mpt.setTextSize(fTextSize);		
    mpt.setFakeBoldText(true);
		return (int)mpt.measureText(sStrTime);		
	}

	public static int GetSpaceWidthUSTimeMark(Paint mpt)
	{
    mpt.setTypeface(tfMono);
		mpt.setTextSize(fTextSize);		
    mpt.setFakeBoldText(false);
		return (int)mpt.measureText(sStrUSTimeMark);		
	}	
		
  private int GetMinHeight()
  {
	  return getPaddingTop() + iFrame + iItemTextHeight + iFrame + getPaddingBottom() - 1;
  }

  public void UpdateHeight()
  {
  	this.getLayoutParams().height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
  	if (getChildCount() == 0)
  	{
  		if ((iHour >= 6) && (iHour <= 22))
  		{
  	  	this.getLayoutParams().height = GetMinHeight(); 
  		} else {
  	  	this.getLayoutParams().height = 0; 
  		}
  	}
  }
   
  public boolean IsMinimized()
  {
  	return getHeight() == 0;
  }
  
	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect)
	{
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		invalidate();
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
		final int iTimePosY = (- (int)mpt.ascent()) + iFrame + iFrame + 1;

    if (!IsMinimized())
    {
    	GetTouchRectangle();
    	
  		DrawBackground(canvas, iWidth, iHeight, bIsFocused);
    	
  		DrawTime(canvas, iTimePosY, iWidth, iHeight, bIsFocused);
  		
  		if (bTouchedDown)
  		{
  			GetTouchSliderRectangle();
  			DrawTouchSlider(canvas);
  			DrawTouchBar(canvas);
  		}
    }
 	}

	private void DrawBackground(Canvas canvas, int iWidth, int iHeight, boolean bIsFocused)
	{    	
    mpt.setTypeface(null); 		
		mpt.setShader(null);		

		//draw touch slider background
		if (bTouchedDown)
    {
			mpt.setColor(iColorTime);
			canvas.drawRoundRect(rtTouchRect, 3, 3, mpt);
    }

		//draw gradient line 
		LinearGradient lGrad = new LinearGradient((getWidth() >> 2), 0, getWidth(), 0,
				0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);		
		mpt.setShader(lGrad);		
		canvas.drawLine(0, 0, getWidth(), 0, mpt);
		
		mpt.setShader(null);
	}
	
	private String GetHourString()
	{				
		if (b24HourMode)
		{
			return Integer.toString(iHour);
		} else {
			int iDisplayHour = iHour;			
			if (iDisplayHour == 0)
				iDisplayHour = 12;			
			if (iDisplayHour > 12)
				iDisplayHour -= 12;
			return Integer.toString(iDisplayHour);
		}
	}
	
	private String GetMinutesString()
	{		
		if (iLastTimeMinutes > 9)
			return ":" + Integer.toString(iLastTimeMinutes);
		return ":0" + Integer.toString(iLastTimeMinutes);
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
		if (iCurrentHour == iHour)
			mpt.setUnderlineText(true);
		
		//draw hour
		final String sHour = GetHourString();
		final int iStrHourWidth = (int)mpt.measureText(sHour);		
		final int iTimePosX = iSpaceWidthHourArea - iStrHourWidth - iFrame - iSpaceWidthUSTimeMark - iSpace;
		mpt.setColor((bTouchedDown)?iColorTimeTouch:iColorTime);
		if (bIsFocused)
			mpt.setColor(iColorTimeFocused);					
		canvas.drawText(sHour, iTimePosX, iTimePosY, mpt);
				
		//draw minutes
		int iMarkPosX = iTimePosX + iStrHourWidth + iFrame;
		if (bTouchedDown)
		{
			final String sMinutes = GetMinutesString();
			final int iStrMinutesWidth = (int)mpt.measureText(sMinutes);		
			final int iMinutesPosX = iTimePosX + iStrHourWidth;
			iMarkPosX = iMinutesPosX + iStrMinutesWidth + iFrame;
			canvas.drawText(sMinutes, iMinutesPosX, iTimePosY, mpt);
		}
		
		//draw us time mark
		if (!b24HourMode)
		{						
	    mpt.setFakeBoldText(false);
			mpt.setColor(iColorMark);
			if (bIsFocused)
				mpt.setColor(iColorTimeFocused);					
			canvas.drawText(GetUSTimeMark(), iMarkPosX, iTimePosY, mpt);
		}
		
		mpt.setUnderlineText(false);
	}
	
	private void DrawTouchSlider(Canvas canvas)
	{
		//draw inner background
		mpt.setShader(null);
		mpt.setColor(iColorTimeTouch);
		canvas.drawRoundRect(rtTouchSliderRect, 3, 3, mpt);
		
		//draw slider pattern
		mpt.setColor(iTouchPatternColor);
		mpt.setStrokeWidth(4);
		mpt.setStrokeCap(Paint.Cap.ROUND);
		
		rectSliderArrow.set(rtTouchSliderRect);		
		final int iArrowWidth = 6;
		final int iArrowSpace = 8;
		final int iArrowCount = (int)(rtTouchSliderRect.width() / (iArrowWidth + iArrowSpace));

		final int iMargin = (int)rtTouchSliderRect.width() - (iArrowCount * (iArrowWidth + iArrowSpace));
		rectSliderArrow.left += iMargin;
		
		for (int i = 0; i < iArrowCount; i++)
		{		
			DrawTouchSliderArrow(canvas, rectSliderArrow, iArrowWidth);			
			rectSliderArrow.left += iArrowWidth + iArrowSpace;
		}
						
		mpt.setStrokeWidth(0);
	}
	
	private void DrawTouchSliderArrow(Canvas canvas, RectF rect, int iArrowWidth)
	{
		int istartX = (int)(rect.left);
		int istopX = (int)(rect.left + iArrowWidth);		
		int iCenter = (int)(rect.top + (rect.height() / 2));		
		canvas.drawLine(istartX, rect.top + 5, istopX, iCenter, mpt);
		canvas.drawLine(istartX, rect.bottom - 5, istopX, iCenter, mpt);		
	}

	private void DrawTouchBar(Canvas canvas)
	{
		//draw inner background
		mpt.setShader(null);
		mpt.setColor(iTouchSliderColor);

		//control ranges
		float fMaxRight = rtTouchSliderRect.right;	
		rtTouchSliderRect.right = iTouchPosX;
		if (rtTouchSliderRect.right > fMaxRight)
			rtTouchSliderRect.right = fMaxRight;
		if (rtTouchSliderRect.right < rtTouchSliderRect.left)
			rtTouchSliderRect.right = rtTouchSliderRect.left;

		canvas.drawRoundRect(rtTouchSliderRect, 2, 2, mpt);
	}	
		
	public int CalculateTimeMinutes()
	{
		RectF rectSlider = GetTouchSliderRectangle();		
		if (iTouchPosX > 0)
		{
			if ((iTouchPosX > rectSlider.left) && (iTouchPosX < rectSlider.right))
			{
				float fRange = rectSlider.right - rectSlider.left;
				float fPos = iTouchPosX - rectSlider.left;				
				float fStep = (fRange / 60);
				int iMinutes = (int)(fPos / fStep);				
				if (iMinutes < 0)
					iMinutes = 0;
				if (iMinutes > 59)
					iMinutes = 59;
								
				int iModulo = iMinutes % 5;				
				iMinutes = iMinutes - iModulo; 
																
				return iMinutes;
			}
		}	
		return 0;
	}	
	
	public void doItemClick()
	{
		if (itemClick != null)
				itemClick.OnClick(this);	
	}
	
	private RectF GetTouchRectangle()
	{
 		rtTouchRect.set(0, 3, getWidth() - 1, GetMinHeight() - 2);  		
		return rtTouchRect;
	}
	
	private RectF GetTimeTouchRectangle()
	{
		rtTimeTouchRect.set(iFrame, 2, iSpaceWidthTimeArea - iFrame, GetMinHeight() - 2);
		return rtTimeTouchRect;
	}
	
	private RectF GetTouchSliderRectangle()
	{
		rtTouchSliderRect.set(GetTouchRectangle());		
		rtTouchSliderRect.inset(2, 2);
		rtTouchSliderRect.left = iSpaceWidthTimeArea + iSpace + iSpace;
		rtTouchSliderRect.right = rtTouchSliderRect.right - iSpace - iSpace - iSpace;
		return rtTouchSliderRect;
	}	
	
	private boolean TouchInTimeArea(MotionEvent event)
	{
		RectF rect = GetTimeTouchRectangle();
		if (rect.contains(event.getX(), event.getY()))
			return true;		
		return false;
	}
	
  @Override
	public boolean onTouchEvent(MotionEvent event)
  {
  	boolean bHandled = false;
  	if (event.getAction() == MotionEvent.ACTION_DOWN)
  	{  		
  		if (TouchInTimeArea(event))
  		{  				  		
    		bHandled = true;
  			bTouchedDown = true;
  			iLastTimeMinutes = 0;
  			iTouchPosX = 0;
  			EnableInnerViewSpace(true);
  			invalidate();
  			Utils.startAlphaAnimIn(ViewDayHourItem.this);
  		}
  	}
  	if (event.getAction() == MotionEvent.ACTION_CANCEL)
  	{
  		bHandled = true;
  		bTouchedDown = false;
			iTouchPosX = 0;
			EnableInnerViewSpace(false);
  		invalidate();
  	}
  	if (event.getAction() == MotionEvent.ACTION_UP)
  	{
 			bHandled = true;
 			bTouchedDown = false;
			EnableInnerViewSpace(false);
 			invalidate(); 			 			
  		doItemClick();
  	}
  	if (event.getAction() == MotionEvent.ACTION_MOVE)
  	{
  		bHandled = true;
			iTouchPosX = (int)event.getX();
 			iLastTimeMinutes = CalculateTimeMinutes(); 			
  		invalidate();
  	}
  	return bHandled;  	  	  	
  }
  
  private void EnableInnerViewSpace(boolean bEnable)
  {
		if (bEnable)
  	{
  		this.addView(invView, 0);  		
  	} else {
  		this.removeView(invView);
  	}
  }
    
}
