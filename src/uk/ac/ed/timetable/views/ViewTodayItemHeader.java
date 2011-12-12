
package uk.ac.ed.timetable.views;


import java.util.Map;
import uk.ac.ed.timetable.Utils;
import android.content.*;
import android.util.AttributeSet;
import android.view.View;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.graphics.*;


public class ViewTodayItemHeader extends View
{
	//types
	public enum ViewType { Courses, Assignments, Notes };

	//types
	public interface OnHeaderItemClick
	{
		public void OnClick(View v, ViewTodayItemHeader.ViewType type);
	}
		
	//fields
	private Paint mpt = null;
	private ViewType viewType = ViewType.Courses;
	private String sText = "";
	private String sTextInfo = "";
	private OnHeaderItemClick itemClick = null;
	private boolean bTouchedDown = false;
	
	//fields
	private final int iBottomLineSpace = 4;
	private static final int iconW = 4;
	private static final int iconH = 4;
	private static final int iIconLeft = 6;
	private final float fTextSize = 20;
	
	//fields	
	private final int iFocusedTextColor = 0xFF222222;
	private final int iTitleTextColor = 0xFF778899;
	private final int iTitleTextShadowColor = 0x88AAAAAA;
	private final int iInfoTextColor = 0xFF777777;
	
	//methods
	public ViewTodayItemHeader(Context context)
	{
		super(context);
		Init(context);
	}

  @SuppressWarnings("all")
	public ViewTodayItemHeader(Context context, AttributeSet attrs, Map inflateParams)
	{
		super(context, attrs);
		Init(context);
	}

	private void Init(Context context)
	{
		mpt = new Paint();
		mpt.setAntiAlias(true);
		mpt.setTextSize(fTextSize);
		mpt.setColor(0xFF000000);
		
		SetType(ViewType.Courses);
				
    setFocusable(true);
	}

	public void SetType(ViewType viewType)
	{
		this.viewType = viewType;
	}	
  
	public void SetItemClick(OnHeaderItemClick itemClick)	
	{
		this.itemClick = itemClick;		
	}
		
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
    setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));				 
	}
	
	public int TextHeight()
	{
		return (int)(-mpt.ascent() + mpt.descent());
	}
	
  private int measureWidth(int measureSpec)
  {
    int result = 0;
    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);
    
    if (specMode == MeasureSpec.EXACTLY)
    {
        result = specSize;
    } else {
        result = (int) mpt.measureText(sText) + getPaddingLeft() + getPaddingRight();
        if (specMode == MeasureSpec.AT_MOST)
        {
            result = Math.min(result, specSize);
        }
    }
    return result;
  }	
	 
  private int measureHeight(int measureSpec)
  {
    int result = 0;
    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);
    if (specMode == MeasureSpec.EXACTLY)
    {
        result = specSize;
    } else {
    		float iTextSize = TextHeight();
    		float iHeight = Math.max(iTextSize, iconH);    		    		
        result = getPaddingTop() + (int)iHeight + iBottomLineSpace + 1 + getPaddingBottom();
        if (specMode == MeasureSpec.AT_MOST)
        {
            result = Math.min(result, specSize);
        }
    }
    return result;
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
				itemClick.OnClick(this, viewType);	
	}
	
	public void DrawTestRect(Canvas canvas)
	{
		Rect rt = new Rect(0, 0, getWidth(), getHeight());
		mpt.setColor(0x22000000);
		canvas.drawRect(rt, mpt);		
	}
		
	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect)
	{
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		invalidate();		
	}
	
	protected void DrawBackground(Canvas canvas)
	{
    if (IsViewFocused())
    {    	
			LinearGradient lGradBkg = new LinearGradient(0, 0, 0, getHeight(),
					0xFFDDDDDD, 0xFF444444, Shader.TileMode.CLAMP);						
			mpt.setShader(lGradBkg);
			RectF rt = new RectF(0F, 0F, getWidth(), getHeight());				
			canvas.drawRoundRect(rt, 2, 2, mpt);
    }
	}
	
	public static int GetTextPosX()
	{
		return iIconLeft + (iconW + (iconW >> 1)) + iIconLeft;
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		DrawBackground(canvas);

		final int iTextPosX = GetTextPosX();		
		int iposLineY = getHeight() - 1 - getPaddingBottom();

		mpt.setStrokeWidth(0);
		mpt.setStyle(Paint.Style.FILL);		
		
		//draw base line
    if (!IsViewFocused())
    {
			LinearGradient lGrad = new LinearGradient((getWidth() >> 2), 0, getWidth(), 0,
					0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);		
			mpt.setShader(lGrad);		
			canvas.drawLine(iTextPosX, iposLineY, getWidth(), iposLineY, mpt);
    }
		mpt.setShader(null);

		//draw icon border
		mpt.setColor(0xFF222222);
		canvas.drawCircle(iIconLeft + iconW, (getHeight() >> 1), iconH + 1, mpt);		
		//draw icon center shaded
		RadialGradient radGrad = new RadialGradient(iIconLeft + iconW, (getHeight() >> 1), iconH,
				0xFF888888, 0xFF666666, Shader.TileMode.CLAMP);
		mpt.setShader(radGrad);
		//mpt.setColor(0xFF666666);
		canvas.drawCircle(iIconLeft + iconW, (getHeight() >> 1), iconH, mpt);				
		mpt.setShader(null);		

    //set text
    mpt.setLinearText(true);
    mpt.setFakeBoldText(true);
		//int iposX = iIconLeft + iconW + iIconLeft;
		final int iTextPosY = TextHeight();
    
    //draw title text shadow
    if (IsViewFocused())
    {
  		mpt.setColor(iTitleTextShadowColor);
    	canvas.drawText(sText, iTextPosX + 1, iTextPosY + 1, mpt);
    }		
    
    //draw title text
    if (IsViewFocused())
    {
  		mpt.setColor(iFocusedTextColor);
    } else {
  		mpt.setColor(iTitleTextColor);
    }    
		canvas.drawText(sText, iTextPosX, iTextPosY, mpt);
		
		//draw additional info text
    final int iposX = getWidth() - (int)mpt.measureText(sTextInfo) - iIconLeft;
		mpt.setUnderlineText(false);
		
    //draw info text shadow
    if (IsViewFocused())
    {
  		mpt.setColor(iTitleTextShadowColor);
  		canvas.drawText(sTextInfo, iposX + 1, iTextPosY + 1, mpt);
    }	
		
		//right info color
    if (IsViewFocused())
    {
    	mpt.setColor(iFocusedTextColor);
    } else {
    	mpt.setColor(iInfoTextColor);
    }    
		canvas.drawText(sTextInfo, iposX, iTextPosY, mpt);		
	}
		
  public void SetText(String value)
  {
  	sText = value;
    requestLayout();
    invalidate();
  }

  public void SetInfoText(String value)
  {
  	sTextInfo = value;
    requestLayout();
    invalidate();
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
  		Utils.startAlphaAnimIn(ViewTodayItemHeader.this);
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
