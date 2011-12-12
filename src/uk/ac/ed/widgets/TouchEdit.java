
package uk.ac.ed.widgets;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.*;
import android.widget.EditText;
import android.graphics.*;


public class TouchEdit extends EditText
{
	//types
	public interface OnOpenKeyboard
	{
		public void OnOpenKeyboardEvent();
	}
	
	//fields
	protected OnOpenKeyboard mEventOnOpenKeyboard = null; 

	//fields
	private final static int iEditHeight = 54;	
	private final static int iArrowWidth = 20;
	private final static int iArrowHeight = 16;
	private final static int iMarginHorz = 3;
	private final static int iMarginVertTop = 4;	
	private final static int iMarginVertBottom = 8;	
	
	//fields
	private RectF rect = new RectF();
	private int iDelta = 0;
	private Paint pt = new Paint();
	private boolean bTouchedDown = false;
	
	//methods
	public TouchEdit(Context context)
	{
		super(context);
	}

	@SuppressWarnings("all")
	public TouchEdit(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public void setOnOpenKeyboard(OnOpenKeyboard openKeyboard)	
	{		
		//set event for touch slide
		this.mEventOnOpenKeyboard = openKeyboard;
		//and set event for long click
	  setOnLongClickListener(new EditText.OnLongClickListener()
	  {
			public boolean onLongClick(View arg0)
			{
				if (mEventOnOpenKeyboard != null)
					mEventOnOpenKeyboard.OnOpenKeyboardEvent();
				return true;
			}
	  });	  		
	}	
		
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		if (bTouchedDown)
		{
			final int iOffsetY = this.getScrollY();
			
			pt.setAntiAlias(true);
			pt.setStrokeCap(Paint.Cap.ROUND);
			
			pt.setColor(0x88ffffff);
			canvas.drawRect(iMarginHorz, iMarginVertTop + iOffsetY, getWidth() - iMarginHorz, getHeight() - iMarginVertBottom + iOffsetY, pt);
			
			int iLeft = this.getWidth() - iArrowWidth - 12;
			int iTop = (iEditHeight >> 1) - (iArrowHeight >> 1) + iOffsetY;
			
			rect.set(iLeft, iTop, iLeft + iArrowWidth, iTop + iArrowHeight);
			
			//draw points
			drawDottedLine(canvas);
			
			//draw frame
			pt.setStrokeWidth(7);
			pt.setColor(0xff3366bb);
			canvas.drawLine(rect.left, rect.centerY(), rect.right, rect.centerY(), pt);		
			canvas.drawLine(rect.right, rect.centerY(), rect.right - 6, rect.top, pt);
			canvas.drawLine(rect.right, rect.centerY(), rect.right - 6, rect.bottom, pt);
			
			//draw bkg
			pt.setStrokeWidth(4);
			pt.setColor(0xff66bbff);
			canvas.drawLine(rect.left, rect.centerY(), rect.right, rect.centerY(), pt);		
			canvas.drawLine(rect.right, rect.centerY(), rect.right - 6, rect.top, pt);
			canvas.drawLine(rect.right, rect.centerY(), rect.right - 6, rect.bottom, pt);
		}
	}

	private void drawDottedLine(Canvas canvas)
	{
		int iSpace = 12;
		int iPosX = 0;
		for (int i = 0; i < 3; i++)
		{
			iPosX += iSpace;
			pt.setStrokeWidth(7);
			pt.setColor(0xff3366bb);
			canvas.drawPoint(rect.left - iPosX, rect.centerY(), pt);
			pt.setStrokeWidth(4);
			pt.setColor(0xff66bbff);
			canvas.drawPoint(rect.left - iPosX, rect.centerY(), pt);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		super.onTouchEvent(event);

		boolean bHandled = false;
				
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			iDelta = (int)event.getX();
			bTouchedDown = true;
			invalidate();
			bHandled = true;
		}
		if (event.getAction() == MotionEvent.ACTION_CANCEL)
		{
			iDelta = 0;
			bTouchedDown = false;
			invalidate();
			bHandled = true;
		}
		if (event.getAction() == MotionEvent.ACTION_UP)
		{
			bTouchedDown = false;
			invalidate();
			int iRange = (this.getWidth() >> 2);
			if (((int)event.getX() - iDelta) > iRange)
			{
				if (mEventOnOpenKeyboard != null)
				{
					mEventOnOpenKeyboard.OnOpenKeyboardEvent();
					bHandled = true;
				}
			}
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE)
		{
			bHandled = true;
		}
		
		return bHandled;  	  	  	
	}    
	
}
