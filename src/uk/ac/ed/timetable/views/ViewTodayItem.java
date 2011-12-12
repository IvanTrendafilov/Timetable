
package uk.ac.ed.timetable.views;


import java.util.Map;
import uk.ac.ed.timetable.R;
import uk.ac.ed.timetable.Utils;
import android.content.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.KeyEvent;
import android.graphics.*;
import android.graphics.drawable.Drawable;


public class ViewTodayItem extends View
{
	//types
	public interface OnItemClick
	{
		public void OnClick(ViewTodayItem item);
	}
		
	//fields
	protected static final int iIconW = 22;
	protected static final int iIconH = 22;	
	protected static final int iMargin = 1;
	protected static final int iSpace = 4;
	protected static final int iFrame = 2;
	protected static final float fTextSize = 20;
	protected final static int iColorTextActive_enabled = 0xFFEEEEEE;
	protected final static int iColorTextActive_disabled = 0xFF999999;
	
	private long lRowId = -1;
	protected Paint mpt = null;
  private Rect rectClipText = new Rect();	
	private String sText = "";
	protected OnItemClick itemClick = null; 
	private boolean bTouchedDown = false;	
	protected static Drawable iconAlarm = null;
	protected static Drawable iconRepeat = null;
	protected static Drawable iconDone = null;
	protected static Drawable iconUnDone = null;			
	
	//methods
	public ViewTodayItem(Context context)
	{
		super(context);
		Init(context);
	}

  @SuppressWarnings("all")
	public ViewTodayItem(Context context, AttributeSet attrs, Map inflateParams)
	{
		super(context, attrs);
		Init(context);
	}
	
	private void Init(Context context)
	{
		mpt = new Paint();
		mpt.setAntiAlias(true);		
		mpt.setTextSize(fTextSize);
    setFocusable(true);
		//get icons
		if (iconAlarm == null)
			iconAlarm = getResources().getDrawable(R.drawable.iconitemalarm);
		if (iconRepeat == null)
			iconRepeat = getResources().getDrawable(R.drawable.iconitemrepeat);
		if (iconDone == null)
			iconDone = getResources().getDrawable(R.drawable.iconitemdone);
		if (iconUnDone == null)
			iconUnDone = getResources().getDrawable(R.drawable.iconitemundone);		
	}
  
	public void SetItemClick(OnItemClick itemClick)	
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
	
	public static int GetMinItemHeight(int iPaddingTop, int iPaddingBottom)
	{
		Paint pt = new Paint();
		pt.setAntiAlias(true);
		pt.setTextSize(fTextSize);
		float iHeight = (int)(-pt.ascent() + pt.descent());
    return iPaddingTop + iMargin + (int)iHeight + iMargin + iMargin + iPaddingBottom;		
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
    		float iHeight = TextHeight();
        result = getPaddingTop() + iMargin + (int)iHeight + iMargin + iMargin + getPaddingBottom();
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
				itemClick.OnClick(this);	
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
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);				
	}
	
  public void SetText(String value)
  {  	
  	sText = value.replace("\n", " ");
  }
  
  public void SetRowId(long lRowId)
  {  
  	this.lRowId = lRowId;
  }

  public long GetRowId()
  {  
  	return lRowId;
  }
  
  public void Update()
  {
    requestLayout();
    invalidate();  	
  }
  
	public void DrawItemText(Canvas canvas, int iposX, int iposY, int iWidth, int iActiveTextColor)
	{
	  mpt.setFakeBoldText(false);
	  	  
	  //text background
    if (IsViewFocused())
    {
    	int iHeight = getHeight();
  		mpt.setShader(null);  		
			LinearGradient lGradBkg = new LinearGradient(0, 0, 0, iHeight,
				0xFFDDDDDD, 0xFF444444, Shader.TileMode.CLAMP);						
			
			mpt.setShader(lGradBkg);
			RectF rt = new RectF(iposX, 0, iWidth, iHeight);	
			canvas.drawRoundRect(rt, 2, 2, mpt);
    }

    //init colors
	  int iTextColor = 0;
	  int iTextColorEnd = 0; 

    //draw text shadow
    if (IsViewFocused())
    {
      //set gradient text shader
			iTextColor = 0xFF888888;
	  	iTextColorEnd = iTextColor & 0x00FFFFFF;
  		LinearGradient lGrad = new LinearGradient(iWidth - 16, 0, iWidth - 6, 0,
  				iTextColor, iTextColorEnd, Shader.TileMode.CLAMP);		
  		mpt.setShader(lGrad);
  		
    	canvas.drawText(sText, iposX + 1, iposY + 1, mpt);
    }		

    //set gradient text shader
		iTextColor = (IsViewFocused())?0xFF222222:iActiveTextColor;		
	  iTextColorEnd = iTextColor & 0x00FFFFFF;
		LinearGradient lGrad = new LinearGradient(iWidth - 16, 0, iWidth - 6, 0,
				iTextColor, iTextColorEnd, Shader.TileMode.CLAMP);		
		mpt.setShader(lGrad);

    //draw text
	  rectClipText.set(0, 0, iWidth, getHeight());
	  canvas.save();
	  canvas.clipRect(rectClipText);
	  canvas.drawText(sText, iposX + 2, iposY, mpt);	  
	  canvas.restore();
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
  		Utils.startAlphaAnimIn(ViewTodayItem.this);
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
