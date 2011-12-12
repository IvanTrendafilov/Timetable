
package uk.ac.ed.widgets;


import android.content.*;
import android.view.*;
import android.widget.LinearLayout.LayoutParams;
import android.graphics.*;


public class TimeCaption extends View
{
	//fields
	private final static float fTextSize = 26;
	private final static int iBorder = 4;

	//fields
	private Paint pt = new Paint();	
	private RectF rect = new RectF();	
	private RectF rectFrame = new RectF();	
	private boolean b24HourMode = false;
	private String sTime = "";
	private String sTimeSign = "";

	//methods
	public TimeCaption(Context context, boolean b24HourMode, int iWidth)
	{
		super(context);
		this.b24HourMode = b24HourMode;
    setFocusable(false);
    final int iHeight = getTotalHeight();
		setLayoutParams(new LayoutParams(iWidth, iHeight));
		initRectangles(iWidth, iHeight);
	}

	private void initRectangles(int iWidth, int iHeight)
	{
		rect.set(iBorder, iBorder, iWidth - iBorder, iHeight - iBorder);
		rectFrame.set(rect);
		rectFrame.inset(-2, -2);		
	}
	
	private int getTotalHeight()
	{
		pt.setTextSize(fTextSize);
		return iBorder + iBorder + getTextHeight() + iBorder + iBorder;
	}

	private int getTextHeight()
	{		
		return (int)(-pt.ascent() + pt.descent());
	}	
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		drawBackground(canvas);
		drawText(canvas);
	}
	
	private void drawBackground(Canvas canvas)
	{
		pt.setAntiAlias(true);
		
		pt.setColor(0xff335577);
		canvas.drawRoundRect(rectFrame, 5, 5, pt);		
		
		pt.setColor(0xff557799);
		canvas.drawRoundRect(rect, 4, 4, pt);
	}
	
	private void drawText(Canvas canvas)
	{
		pt.setAntiAlias(true);
		pt.setFakeBoldText(true);	
		pt.setTypeface(null);
		pt.setTextSize(fTextSize);
		pt.setUnderlineText(false);
		
		final int iSpace = 4;
		final int iTimeWidth = (int)pt.measureText(sTime);
		final int iTimeSignWidth = (int)pt.measureText(sTimeSign);
		final int iTimeTotalWidth = iTimeWidth + (b24HourMode?0:(iSpace + iTimeSignWidth));

		int iTextPosY = (int)rect.bottom + (int)(-pt.ascent()) - getTextHeight();
		iTextPosY -=  ((int)rect.height() >> 1) - (getTextHeight() >> 1);

		final int iTextPosX = (int)rect.left + (((int)rect.width() >> 1) - (iTimeTotalWidth >> 1));
	
		pt.setColor(0xffffffff);
		canvas.drawText(sTime, iTextPosX, iTextPosY, pt);
				
		if (!b24HourMode)
		{
			pt.setColor(0xffaaccee);
			canvas.drawText(sTimeSign, iTextPosX + iTimeWidth + iSpace, iTextPosY, pt);
		}
	}
	
	private String GetMinutesString(int iMinutes)
	{		
		if (iMinutes > 9)
			return ":" + Integer.toString(iMinutes);
		return ":0" + Integer.toString(iMinutes);
	}

	private String GetUSTimeMark(int iHour)
	{
		if (iHour >= 12)
			return "pm";
		return "am";
	}

	public void setTime(int iHour, int iMinutes)
	{
		if ((iHour == -1) || (iMinutes == -1))
		{
			sTime = "";
			sTimeSign = "";			
		} else {
			if (b24HourMode)
			{
				sTime = Integer.toString(iHour) + GetMinutesString(iMinutes);
				sTimeSign = "";
			} else {
				int iDisplayHour = iHour;
				if (iDisplayHour == 0)
					iDisplayHour = 12;
				if (iDisplayHour > 12)
					iDisplayHour -= 12;
				sTime = Integer.toString(iDisplayHour) + GetMinutesString(iMinutes);
				sTimeSign = GetUSTimeMark(iHour);
			}
		}
		this.invalidate();
	}
	
}
