
package uk.ac.ed.widgets;


import java.util.*;
import android.content.*;
import android.view.*;
import android.widget.LinearLayout.LayoutParams;
import android.graphics.*;


public class TimeWidgetSlider extends View
{
	//types
	public interface OnTimeChange
	{
		public void OnChange(TimeWidgetSlider slider);
	}
	
	//types
	private class ValueBox
	{
		//fields
		public int iValue = 0;
		@SuppressWarnings("unused")
		public int iLeftOffset = 0;
		@SuppressWarnings("unused")
		public int iWidth = 0;
		public String strValue = null;
		public int iStrWidth = 0;
		public int iStrWidthSmall = 0;
		
		//methods
		public ValueBox(int iValue, int iLeftOffset, int iWidth, Paint pt)
		{
			this.iValue = iValue;
			this.iLeftOffset = iLeftOffset;
			this.iWidth = iWidth;
			strValue = getValueStr();
			calcValueStrWidths(pt);
		}
		private String getValueStr()
		{
			if (iSliderType == STYPE_MINUTES)
				return Integer.toString(iValue * 5);				
			if (iSliderType == STYPE_HOURS)
			{
				if (b24HourMode)
				{
					return Integer.toString(iValue);
				} else {
					int iDisplayHour = iValue;
					if (iDisplayHour == 0)
						iDisplayHour = 12;			
					if (iDisplayHour > 12)
						iDisplayHour -= 12;
					return Integer.toString(iDisplayHour);
				}
			}
			return "";
		}
		public void calcValueStrWidths(Paint pt)
		{		
			pt.setFakeBoldText(true);	
			pt.setTypeface(tfMono);
						
			pt.setTextSize(fTextSize);
			iStrWidth = (int)pt.measureText(strValue);
			
			pt.setTextSize(fTextSizeSmall);
			iStrWidthSmall = (int)pt.measureText(strValue);						
		}		
		@SuppressWarnings("unused")
		public boolean isPM()
		{
			if (iValue >= 12)
				return true;
			return false;
		}
	}		

	//fields
	private ArrayList<ValueBox> vecValues = new ArrayList<ValueBox>();
	
	//fields
	private final static int iColorBkg = 0xff777777;
	private final static int iColorLightBkg = 0xffcccccc;
	private final static int iColorSliderBkg = 0xff444444;	
	private final static int iColorButtonArrows = 0xff444444;
	private final static int iColorSliderCenterBox = 0xffffffff;	
	private final static int iColorValueBoxText = 0xff666666;
	private final static int iColorValueBoxBkg = 0xffbbbbbb;
	private final static int iColorValueBoxTextSelected = 0xff222222;

	//fields
  private final static Typeface tfMono = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);
	private final static float fTextSize = 28;
	private final static float fTextSizeSmall = 22;
	private final static int iFocusBorder = 3;
	private final static int iSliderSpace = 4;
	private final static int iSliderBorder = 4;
	private final static int iBoxBorder = 4;
	private final static int iSliderButtonWidth = 40;
	
	//fields
	protected OnTimeChange mOnTimeChange = null; 
	
	//fields
	private Paint pt = new Paint();	
	private RectF rectFocus = new RectF();
	private RectF rect = new RectF();
	private RectF rectBar = new RectF();
	private RectF rectBox = new RectF();
	private RectF rectCenterBox = new RectF();
	private RectF rectButtonLeft = new RectF();
	private RectF rectButtonRight = new RectF();
	private RectF rectSlider = new RectF();
	
	//fields
	private boolean b24HourMode = false;
	private int iSliderType = 0;
	private boolean bNoValue = false;
	private int iCurrentValue = -1;
	
	//fields
	private boolean bTouchedDownSlider = false;
	private boolean bTouchedDownBtnLeft = false;
	private boolean bTouchedDownBtnRight = false;
	private int iTouchedSliderPosX = 0;
	
	//fields
	public final static int STYPE_HOURS = 1;
	public final static int STYPE_MINUTES = 2;	

	//fields
	private int iBoxWidth = 0;
	private BlurMaskFilter filterBlur = new BlurMaskFilter(0.5F, BlurMaskFilter.Blur.NORMAL);

	//fields
	private int iAnimPosX = 0;
	private int iAnimDelta = 0;
	private long mLastTime = 0;
	private final static int iAnimStep = 12; 
			
	//methods
	public TimeWidgetSlider(Context context, boolean b24HourMode, int iSliderType, int iWidth)
	{
		super(context);
		this.b24HourMode = b24HourMode;		
		this.iSliderType = iSliderType;
    setFocusable(true);

    final int iHeight = getTotalHeight();
		setLayoutParams(new LayoutParams(iWidth, iHeight));
		
		//init sizes
		iBoxWidth = getBoxWidth();
		initRectangles(iWidth, iHeight);
		
		generateValues();
		setValue(getValue(), false);
	}

	public void setTimeChangeEvent(OnTimeChange timeChange)	
	{
		this.mOnTimeChange = timeChange;	
	}
	
	public void doTimeChanged()
	{
		if (mOnTimeChange != null)
			mOnTimeChange.OnChange(this);	
	}
	
	private int getBoxWidth()
	{
		pt.setTypeface(tfMono);
		pt.setTextSize(fTextSize);
		pt.setFakeBoldText(true);
		return iBoxBorder + (int)pt.measureText("00") + iBoxBorder;		
	}
	
	private void initRectangles(int iWidth, int iHeight)
	{
		rectFocus.set(0, 0, iWidth, iHeight);		
		
		rect.set(rectFocus);
		rect.inset(0, iFocusBorder);
		
		rectBar.set(iFocusBorder, 
				iFocusBorder + iSliderBorder + iSliderSpace,
				iWidth - iFocusBorder,
				iHeight - iSliderSpace - iSliderBorder - iFocusBorder);
		
		rectButtonLeft.set(rect);
		rectButtonLeft.right = iSliderButtonWidth;		
		rectButtonRight.set(rect);
		rectButtonRight.left = rectButtonRight.right - iSliderButtonWidth;
		
		rectSlider.set(rectBar);
		rectSlider.left = rectButtonLeft.right;
		rectSlider.right = rectButtonRight.left;
		
		rectCenterBox.set(rectSlider);
		rectCenterBox.top -= iSliderSpace;
		rectCenterBox.bottom += iSliderSpace;
		rectCenterBox.left += ((int)rectCenterBox.width() >> 1) - (iBoxWidth >> 1);
		rectCenterBox.right = rectCenterBox.left + iBoxWidth;
	}
	
	public void clearValue()
	{
		this.bNoValue = true;
		this.invalidate();
		doTimeChanged();
	}
	
	public void setValue(int iValue, boolean bAnimate)
	{
		this.bNoValue = false;
		int iMax = 0;	
		if (iSliderType == STYPE_HOURS)
			iMax = (vecValues.size() - 1);
 		if (iSliderType == STYPE_MINUTES)
			iMax = (vecValues.size() - 1) * 5;

		this.iAnimDelta = (iCurrentValue - iValue);		

		if (iValue < 0)
		{
			iValue = iMax;
		}
		if (iValue > iMax)
		{
			iValue = 0;
		}
	
		this.iCurrentValue = iValue;

		this.iAnimPosX = 0;
		if (iAnimDelta > 0)
			this.iAnimPosX = 0;		
		if (iAnimDelta < 0)
			this.iAnimPosX = iBoxWidth;
		
		if (!bAnimate)
		{
			this.iAnimPosX = 0;
			this.iAnimDelta = 0;
		}

		this.invalidate();		
		doTimeChanged();
	}
	
	public int getValue()
	{
		return this.iCurrentValue;
	}
	
	private int getTextHeight()
	{
		return (int)(-pt.ascent() + pt.descent());
	}
	
	private int getTotalHeight()
	{
		pt.setTextSize(fTextSize);
		return iSliderBorder + iSliderSpace + iBoxBorder + getTextHeight() + iBoxBorder + iSliderSpace + iSliderBorder;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		boolean bResult = super.onKeyDown(keyCode, event);
		if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER) || (keyCode == KeyEvent.KEYCODE_ENTER))
		{
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
		{
			bTouchedDownBtnLeft = true;
			doClickSliderButton();
			clearTouchButtonsFlags();
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
		{
			bTouchedDownBtnRight = true;
			doClickSliderButton();
			clearTouchButtonsFlags();
		}
		return bResult;
	}
 
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		boolean bResult = super.onKeyUp(keyCode, event);
		return bResult;
	}
		
	private void generateValues()
	{
		vecValues.clear();		
 		if (iSliderType == STYPE_HOURS)
 		{
 			generateValuesRange(24); //24 steps = 24 hours
 		}
 		if (iSliderType == STYPE_MINUTES)
 		{
 			generateValuesRange(12); //12 steps * 5 minutes = 60minutes
 		}				
	}
	
	private void generateValuesRange(int iRange)
	{
		int iLeftOffset = 0;
		for (int iValue = 0; iValue < iRange; iValue++)
		{
			ValueBox value = new ValueBox(iValue, iLeftOffset, iBoxWidth, pt);
			vecValues.add(value);
			iLeftOffset += iBoxWidth;
		}
	}
	
	private void updateAnimationState()
	{		
		//wait next frames for period of time
		if ((System.currentTimeMillis() - mLastTime) < 30)
			return;
		mLastTime = System.currentTimeMillis();

		//animate slider scrolling
		if (iAnimDelta < 0)
		{
			iAnimPosX -= iAnimStep;
			if (iAnimPosX < 0)				
				iAnimDelta = 0;
		}

		if (iAnimDelta > 0)
		{
			iAnimPosX += iAnimStep;
			if (iAnimPosX > iBoxWidth)
				iAnimDelta = 0;
		}
		
		if (iAnimDelta == 0)
		{
			iAnimPosX = 0;
		}		
	}		
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		updateAnimationState();
		
		drawBackground(canvas);
		
		if (!bNoValue)
		{
			drawSliderBars(canvas);			
		}
		
		drawFrames(canvas);
		
		drawSliderButtons(canvas);
		drawArrowLeft(canvas);
		drawArrowRight(canvas);
				
		//call next update
		this.invalidate();
	}
	
	private void drawSliderBars(Canvas canvas)
	{
		canvas.save();
		canvas.clipRect(rectSlider);				

		int index = iCurrentValue;
		if (iSliderType == STYPE_HOURS)
			index = iCurrentValue;
 		if (iSliderType == STYPE_MINUTES)
 			index = iCurrentValue / 5;

 		//draw center box
		final ValueBox value = vecValues.get(index);
		rectBox.set(rectSlider);
		rectBox.left = rectCenterBox.left + iAnimPosX;
		rectBox.right = rectBox.left + iBoxWidth;
		drawValueBox(canvas, rectBox, value);								

		//draw left boxes
		int iLeftIndex = index - 1;
		rectBox.set(rectSlider);
		rectBox.left = rectCenterBox.left - iBoxWidth + iAnimPosX;
		rectBox.right = rectBox.left + iBoxWidth;
		for (int i = 0; i < 4; i++)
		{
			if (iLeftIndex < 0)
				iLeftIndex = vecValues.size() - 1;			
			final ValueBox valueLeft = vecValues.get(iLeftIndex);
			drawValueBox(canvas, rectBox, valueLeft);
			rectBox.left -= iBoxWidth;
			rectBox.right = rectBox.left + iBoxWidth;
			iLeftIndex--;
		}

		//draw right boxes
		int iRightIndex = index + 1;
		rectBox.set(rectSlider);
		rectBox.left = rectCenterBox.right + iAnimPosX;
		rectBox.right = rectBox.left + iBoxWidth;
		for (int i = 0; i < 4; i++)
		{
			if (iRightIndex > (vecValues.size() - 1))
				iRightIndex = 0;
			final ValueBox valueRight = vecValues.get(iRightIndex);
			drawValueBox(canvas, rectBox, valueRight);
			rectBox.left += iBoxWidth;
			rectBox.right = rectBox.left + iBoxWidth;
			iRightIndex++;
		}				
		
		canvas.restore();
	}
			
	private void drawBackground(Canvas canvas)
	{
		pt.setShader(null);
		pt.setAntiAlias(true);

		//focus background
		if (this.isFocused())
		{
			pt.setColor(0xffff8800);
			canvas.drawRoundRect(rectFocus, 2, 2, pt);
		}
		
		//background
		pt.setColor(iColorBkg);
		final int iRoundCorner = this.isFocused()?0:2;		
		canvas.drawRoundRect(rect, iRoundCorner, iRoundCorner, pt);
		
		//draw slider background outer frame
		pt.setColor(iColorSliderBkg);
		canvas.drawRect(rectSlider, pt);
				
		//draw slider background inner frame
		pt.setColor(iColorValueBoxBkg);
		canvas.drawRect(rectSlider.left, rectSlider.top + 1, rectSlider.right, rectSlider.bottom -1, pt);
		
		//center box background
		if (!bNoValue)
		{
			pt.setColor(iColorSliderCenterBox);
			canvas.drawRoundRect(rectCenterBox, 2, 2, pt);
		}
	}
		
	private boolean isCurrentBoxSelected(RectF rect)
	{
		if ((rect.left >= rectCenterBox.left) && (rect.right <= rectCenterBox.right))
			return true;				
		return false;
	}
	
	private void drawValueBox(Canvas canvas, RectF rect, ValueBox value)
	{				
		final boolean bSelected = isCurrentBoxSelected(rect);

		//draw value number
		pt.setShader(null);
		pt.setAntiAlias(true);
		pt.setFakeBoldText(true);	
		pt.setTypeface(tfMono);
		pt.setUnderlineText(false);
		
		pt.setTextSize(fTextSizeSmall);
		if (bSelected)
			pt.setTextSize(fTextSize);
		
		if (iAnimPosX != 0)
			pt.setMaskFilter(filterBlur);
						
		final int iNumberWidth = (bSelected)?value.iStrWidth:value.iStrWidthSmall;

		int iTextPosX = (int)rect.right - iNumberWidth;
		int iTextPosY = (int)rect.bottom + (int)(-pt.ascent()) - getTextHeight();
		
		iTextPosX -= ((int)rect.width() >> 1) - (iNumberWidth >> 1);
		iTextPosY -=  ((int)rect.height() >> 1) - (getTextHeight() >> 1);
		
		iTextPosY += 1;			

		pt.setColor((bSelected)?iColorValueBoxTextSelected:iColorValueBoxText);		
 		canvas.drawText(value.strValue, iTextPosX, iTextPosY, pt);
 		
		pt.setMaskFilter(null);
	}

	private void drawSliderButtons(Canvas canvas)
	{
		pt.setShader(null);
		pt.setAntiAlias(true);
		pt.setFakeBoldText(true);	
		pt.setTypeface(tfMono);
		pt.setTextSize(fTextSize);
		pt.setUnderlineText(false);
		
		pt.setColor((bTouchedDownBtnLeft)?iColorLightBkg:iColorBkg);
		canvas.drawRoundRect(rectButtonLeft, 2, 2, pt);

		pt.setColor((bTouchedDownBtnRight)?iColorLightBkg:iColorBkg);
		canvas.drawRoundRect(rectButtonRight, 2, 2, pt);		
	}
	
	private void drawFrames(Canvas canvas)
	{
		pt.setColor(iColorSliderBkg);
		pt.setStrokeWidth(0);
		pt.setAntiAlias(false);
		
		//slider frame
		canvas.drawLine(rectSlider.left, rectSlider.top, rectSlider.left, rectSlider.bottom, pt);
		canvas.drawLine(rectSlider.right - 1, rectSlider.top, rectSlider.right - 1, rectSlider.bottom, pt);
		
		//center box frame
		canvas.drawLine(rectCenterBox.left - 1, rectSlider.top, rectCenterBox.left - 1, rectSlider.bottom, pt);
		canvas.drawLine(rectCenterBox.right, rectSlider.top, rectCenterBox.right, rectSlider.bottom, pt);
	}
	
	private void drawArrowLeft(Canvas canvas)
	{		
		final int iH = 8;
		final int iH2 = 4;
		final int iV = 14;
		final int iX = (int)rectButtonLeft.centerX();
		final int iY = (int)rectButtonLeft.centerY();
		pt.setAntiAlias(true);
		pt.setStrokeCap(Paint.Cap.ROUND);
		pt.setStrokeWidth(6);
		pt.setColor(iColorButtonArrows);		
		canvas.drawLine(iX - iH, iY, iX + iH2, rectButtonLeft.top + iV, pt);
		canvas.drawLine(iX - iH, iY, iX + iH2, rectButtonLeft.bottom - iV, pt);		
	}	

	private void drawArrowRight(Canvas canvas)
	{
		final int iH = 8;
		final int iH2 = 4;
		final int iV = 14;
		final int iX = (int)rectButtonRight.centerX();
		final int iY = (int)rectButtonRight.centerY();
		pt.setAntiAlias(true);
		pt.setStrokeCap(Paint.Cap.ROUND);		
		pt.setStrokeWidth(6);
		pt.setColor(iColorButtonArrows);
		canvas.drawLine(iX - iH2, rectButtonRight.top + iV, iX + iH, iY, pt);
		canvas.drawLine(iX - iH2, rectButtonRight.bottom - iV, iX + iH, iY, pt);
	}	
	
	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect)
	{
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		invalidate();
	}
	
	private void doClickSliderButton()
	{
		int iStep = 0;
		if (iSliderType == STYPE_MINUTES)
			iStep = 5;
		if (iSliderType == STYPE_HOURS)
			iStep = 1;		
		if (bTouchedDownBtnLeft)
		{
			int iValue = getValue() - iStep;
			setValue(iValue, true);
		}
		if (bTouchedDownBtnRight)
		{
			int iValue = getValue() + iStep;
			setValue(iValue, true);
		}
	}
	
	private boolean touchInSliderButtonsArea(MotionEvent event)
	{
		clearTouchButtonsFlags();
		if (rectButtonLeft.contains(event.getX(), event.getY()))
		{
			bTouchedDownBtnLeft = true;
			return true;
		}
		if (rectButtonRight.contains(event.getX(), event.getY()))
		{
			bTouchedDownBtnRight = true;
			return true;
		}
		return false;
	}

	private void clearTouchButtonsFlags()
	{
		bTouchedDownBtnLeft = false;
		bTouchedDownBtnRight = false;
	}
	
	private void clearTouchSliderFlags()
	{
		bTouchedDownSlider = false;
	}
	
	private boolean touchInSliderArea(MotionEvent event)
	{
		clearTouchSliderFlags();
		if (rectSlider.contains(event.getX(), event.getY()))
		{
			bTouchedDownSlider = true;
			iTouchedSliderPosX = (int)event.getX();
			return true;
		}
		return false;
	}	

	private void touchSliderMoved(MotionEvent event)
	{		
		if (bTouchedDownSlider)
		{
			final int iDelta = (iTouchedSliderPosX - (int)event.getX());
			
			if (Math.abs(iDelta) >= (iBoxWidth * 0.7))
			{  			
  			//move right
  			if (iDelta > 0)
  				bTouchedDownBtnRight = true;
  			//move left
  			if (iDelta < 0)
  				bTouchedDownBtnLeft = true;
  			
				iTouchedSliderPosX = (int)event.getX();
				invalidate();
				doClickSliderButton();
				clearTouchButtonsFlags();
			}
		}		
	}	
	
  @Override
	public boolean onTouchEvent(MotionEvent event)
  {
  	boolean bHandled = false;
  	if (event.getAction() == MotionEvent.ACTION_DOWN)
  	{  		
  		if (touchInSliderButtonsArea(event) || touchInSliderArea(event))
  		{
    		bHandled = true;
  			invalidate();
  		}
  	}
  	if (event.getAction() == MotionEvent.ACTION_CANCEL)
  	{
  		clearTouchButtonsFlags();
  		clearTouchSliderFlags();
  		bHandled = true;
  		invalidate();
  	}
  	if (event.getAction() == MotionEvent.ACTION_UP)
  	{
 			bHandled = true;
 			invalidate();
 			doClickSliderButton();
 			clearTouchButtonsFlags();
 			clearTouchSliderFlags();
 			invalidate(); 			
  	}
  	if (event.getAction() == MotionEvent.ACTION_MOVE)
  	{
  		touchSliderMoved(event);  		
  		bHandled = true;
  		invalidate();
  	}
  	return bHandled;  	  	  	
  }
	
}
