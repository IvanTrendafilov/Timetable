
package uk.ac.ed.widgets;


import java.util.ArrayList;
import android.content.Context;
import android.view.*;
import android.widget.LinearLayout.LayoutParams;
import android.graphics.*;
import android.graphics.drawable.*;
import uk.ac.ed.timetable.*;


public class KeyboardWidgetView extends View
{
	//types
	public interface OnKeyClick
	{
		public void OnKeyClicked(KeyItem key, boolean bCapital);
	}
	
	//fields
	protected OnKeyClick mEventOnKeyClick = null; 
	
	//fields
	private Drawable bkgKeyboard = null;
	
	//fields
	private static final int iKeyboardWidth = 284;
	private static final int iKeyboardHeight = 214;

	//fields
	public final static int iMarginTB = 3;
	public final static int iKeyHeight = 48;
	
	//fields
  private final static Typeface tfMono = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);
	private final static float fTextSize = 20;
	private final static float fTextSizeSmall = 12;
	
	//fields
	private int iDialogWidth = 0;
	private int iKeyWidth = 0;
	private int iKeyModeWidth = 0;
	private int iKeySpecialWidth = 0;
	private int iDefaultSymbolWidth = 0;
	private int iDefaultSymbolHeight = 0;
	private int iDefaultSmallSymbolHeight = 0;
		
	//fields
	private KeyItem activeKey = null;
	private KeyItemLayout keyLayout = new KeyItemLayout();
	public HintEdit editText = null;

	//fields
	private Paint pt = new Paint();
	
	//methods
	public KeyboardWidgetView(Context context, int iDialogWidth)
	{
		super(context);
		bkgKeyboard = getResources().getDrawable(R.drawable.keyboard);

		//calculate sizes
		this.iDialogWidth = iDialogWidth;
		this.iKeyWidth = (iDialogWidth / 10);
		this.iKeyModeWidth = (int)(iKeyWidth * 1.4F);		
		this.iKeySpecialWidth = (iKeyWidth * 2);
		
		//get default symbol size
		pt.setUnderlineText(false);
		pt.setFakeBoldText(true);
		pt.setTypeface(tfMono);
		
		pt.setTextSize(fTextSize);			
		iDefaultSymbolWidth = (int)pt.measureText("W");
		iDefaultSymbolHeight = (int)(-pt.ascent() + pt.descent());
				
		pt.setTextSize(fTextSizeSmall);		
		iDefaultSmallSymbolHeight = (int)(-pt.ascent() + pt.descent());

		setLayoutParams(new LayoutParams(getKeyboardWidth(), getKeyboardHeight()));	
		
		initializeLayouts(pt);		
	}
	
	public int getKeyboardWidth()
	{
		return (10 * iKeyWidth);
	}

	public int getKeyboardHeight()
	{
		return 4 * (iMarginTB + iKeyHeight + iMarginTB);
	}
	
	public int getTotalHeight()
	{
		return getKeyboardHeight();
	}

	public void setKeyClickEvent(OnKeyClick keyClickEvent)	
	{
		this.mEventOnKeyClick = keyClickEvent;
	}	
	
	public void initializeLayoutSpecKey(Paint pt, int iSpecKeyIndex, int iLeft, int iTop, int iKeyWidth)
	{
		KeyItem item = keyLayout.specialKeys[iSpecKeyIndex];
		int iSymbolWidth = (int)pt.measureText(item.sChar);
		item.setRectangle(iLeft, iTop, iKeyWidth, iKeyHeight);
		item.setSymbolSize(iSymbolWidth, iDefaultSmallSymbolHeight);
	}
			
	private void initializeLayoutSpecKeys(Paint pt, int iTop)
	{
		final int iSpace = ((iDialogWidth - (10 * iKeyWidth)) >> 1);
		
		//get symbol size
		pt.setUnderlineText(false);
		pt.setFakeBoldText(true);
		pt.setTypeface(tfMono);
		pt.setTextSize(fTextSizeSmall);

		//third row
		initializeLayoutSpecKey(pt, KeyItemLayout.iSpecKeyMode, iSpace, iTop, iKeyModeWidth);
		initializeLayoutSpecKey(pt, KeyItemLayout.iSpecKeyDel, iDialogWidth - iSpace - iKeyModeWidth, iTop, iKeyModeWidth);
		
		//fourth row
		iTop += iKeyHeight + iMarginTB + iMarginTB;

		initializeLayoutSpecKey(pt, KeyItemLayout.iSpecKeyCycle, iSpace, iTop, iKeySpecialWidth);
		initializeLayoutSpecKey(pt, KeyItemLayout.iSpecKeyReturn, iDialogWidth - iSpace - iKeySpecialWidth, iTop, iKeySpecialWidth);
		
		//space key
		int iLeftX = iSpace + iKeySpecialWidth + 5;
		int iRightX = iDialogWidth - iSpace - iKeySpecialWidth - 5;		
		initializeLayoutSpecKey(pt, KeyItemLayout.iSpecKeySpace, iLeftX, iTop, iRightX - iLeftX);
	}	
			
	public void initializeLayoutRow(KeyItem[] map, int iTop)
	{
		int iSpace = ((iDialogWidth - (map.length * iKeyWidth)) >> 1);
		for (int index = 0; index < map.length; index++)
		{
			KeyItem mapItem = map[index];
			mapItem.setRectangle(iSpace, iTop, iKeyWidth, iKeyHeight);
			mapItem.setSymbolSize(iDefaultSymbolWidth, iDefaultSymbolHeight);
			iSpace += iKeyWidth;
		}		
	}
	
	public void initializeLayouts(Paint pt)
	{
		//misc sizes
		int iTop = iMarginTB;
		
		//first row
		initializeLayoutRow(keyLayout.alphaRow1, iTop);
		initializeLayoutRow(keyLayout.digitRow1, iTop);
		
		//second row
		iTop += iKeyHeight + iMarginTB + iMarginTB;
		initializeLayoutRow(keyLayout.alphaRow2, iTop);
		initializeLayoutRow(keyLayout.digitRow2, iTop);
		
		//third row
		iTop += iKeyHeight + iMarginTB + iMarginTB;
		initializeLayoutRow(keyLayout.alphaRow3, iTop);
		initializeLayoutRow(keyLayout.digitRow3, iTop);

		//spec keys
		initializeLayoutSpecKeys(pt, iTop);
	}
	
	private void drawKey(Canvas canvas, Paint pt, KeyItem key)
	{
		final boolean bDefaultKey = key.isDefaultKey();
		final boolean bSpaceKey = key.isSpaceKey();

		//draw active key over keyboard background
		if (key.bIsActive)
		{
			//draw focus frame
			pt.setColor(bDefaultKey?0xffffbb77:0xff77bbff);									
			canvas.drawRoundRect(key.rectFocus, 3, 3, pt);
			//draw background
			pt.setColor(bDefaultKey?0xffffffff:0xff5a78a0);
			canvas.drawRoundRect(key.rectFrame, 2, 2, pt);
		}
		
		//draw symbol
		pt.setColor(bDefaultKey?0xff000000:0xffddeeff);
		pt.setTextSize(bDefaultKey?fTextSize:fTextSizeSmall);
		if (bSpaceKey)
			pt.setTextSize(fTextSizeSmall);
		
		final int iSymbolPosX = (int)key.rectFrame.left + (((int)key.rectFrame.width() >> 1) - (key.iSymbolWidth >> 1));
		final int iSymbolPosY = (int)key.rectFrame.top + (((int)key.rectFrame.height() >> 1) - (key.iSymbolHeight >> 1)) - (int)pt.ascent();

 		canvas.drawText(keyLayout.getKeySymbol(key), iSymbolPosX, iSymbolPosY, pt);
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		//draw keyboard background
		final int iPosX = -2;
		final int iPosY = 1;
		bkgKeyboard.setBounds(iPosX, iPosY, iPosX + iKeyboardWidth, iPosY + iKeyboardHeight);
		bkgKeyboard.draw(canvas);		

		//draw keyboard layout
		final ArrayList<KeyItem> layout = keyLayout.getLayout();
		
		pt.setAntiAlias(true);
		pt.setShader(null);		
		pt.setUnderlineText(false);
		pt.setFakeBoldText(true);
		pt.setTypeface(tfMono);
		
		//iterate keys
		for (int i = 0; i < layout.size(); i++)
		{
			final KeyItem key = layout.get(i);
			drawKey(canvas, pt, key);
		}
	}
	
	private KeyItem getActiveKey(MotionEvent event)
	{		
		final ArrayList<KeyItem> layout = keyLayout.getLayout();
		
		//iterate keys
		for (int i = 0; i < layout.size(); i++)
		{
			final KeyItem key = layout.get(i);
			if (key.rectFrame.contains((int)event.getX(), (int)event.getY()))
				return key;
		}
		return null;
	}

	private void setKeyActive(boolean bIsActive)
	{
		if (activeKey != null)
		{
			activeKey.bIsActive = bIsActive;
		}
	}

	private void showKeyHint()
	{
		if ((editText != null) && (activeKey != null))
		{
			final String sKey = keyLayout.getKeySymbol(activeKey);
			editText.showSymbolHint(sKey, activeKey.isDefaultKey());
		}
	}

	private void hideKeyHint()
	{
		if ((editText != null) && (activeKey != null))
			editText.hideSymbolHint();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
  {
  	boolean bHandled = false;
  	
  	if (event.getAction() == MotionEvent.ACTION_DOWN)
  	{  		
  		activeKey = getActiveKey(event);
  		setKeyActive(true);  		  		
  		bHandled = true;
  		showKeyHint();
  	}
  	if (event.getAction() == MotionEvent.ACTION_CANCEL)
  	{
  		setKeyActive(false);
  		bHandled = true;
  		hideKeyHint();
  	}
  	if (event.getAction() == MotionEvent.ACTION_UP)
  	{
 			doButtonClick();			
 			setKeyActive(false);
			bHandled = true;			
  		hideKeyHint();
  	}
  	if (event.getAction() == MotionEvent.ACTION_MOVE)
  	{
  		setKeyActive(false);
  		activeKey = getActiveKey(event);
  		setKeyActive(true);
  		bHandled = true;
  		if (activeKey == null)
  		{
  			if (editText != null)
  				editText.hideSymbolHint();  			
  		} else {
    		showKeyHint();  			
  		}
  	}
  	
  	if (bHandled)
  		invalidate();
  	
  	return bHandled;  	  	  	
  }
	
  private void doButtonClick()
  {
  	if ((activeKey != null) && (activeKey.bIsActive))
  	{
  		
  		//mode key: switch layout
  		if (activeKey.isModeKey())
  		{
  			keyLayout.cycleLayoutMode();
  			return;
  		}
  		
  		//call event
  		if (mEventOnKeyClick != null)
  		{  			
  			mEventOnKeyClick.OnKeyClicked(activeKey, keyLayout.isCapitalMode());
  		}
  		
  	}  	
  }
  
  public KeyItemLayout getKeyLayout()
  {
  	return keyLayout;
  }
			
}
