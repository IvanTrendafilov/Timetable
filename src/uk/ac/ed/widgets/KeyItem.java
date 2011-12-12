
package uk.ac.ed.widgets;


import android.graphics.RectF;
import android.view.*;


public class KeyItem
{
	//key types
	protected final static int kDefault = 0;
	protected final static int kDefaultSpace = 1;
	protected final static int kMode = 2;
	protected final static int kDelete = 3;
	protected final static int kAlphaCycle = 4;
	protected final static int kReturn = 5;
	
	//fields
	public String sChar = null; 
	public String sCharSmall = null; 
	public int iCode = KeyEvent.KEYCODE_UNKNOWN;
	public boolean bAlt = false;
	public int iType = kDefault;
		
	//fields
	public RectF rectFocus = new RectF();
	public RectF rectFrame = new RectF();
	
	//fields
	public int iSymbolWidth = 0;
	public int iSymbolHeight = 0;
	public boolean bIsActive = false;
	
	//fields
	private KeyItem keyDelete = null;
	private KeyItem[] cycleKeys = null;
	private int iCycleIndex = 0;
	private long lClickTime = 0;
	
	//methods
	KeyItem(String sChar, int iCode, int iType, boolean bAlt)
	{
		this.sChar = sChar;
		this.sCharSmall = sChar.toLowerCase();
		this.iCode = iCode;
		this.iType = iType;
		this.bAlt = bAlt;			
	}

	KeyItem(String sCycleSequence, final KeyItem[] cycleKeys)
	{
		this(sCycleSequence, 0, kAlphaCycle, false);
		this.cycleKeys = cycleKeys;
		this.keyDelete = new KeyItem("del", KeyEvent.KEYCODE_DEL, KeyItem.kDelete, false);
	}
	
	public void setSymbolSize(int iWidth, int iHeight)
	{
		iSymbolWidth = iWidth;
		iSymbolHeight = iHeight;			
	}
	
	public void setRectangle(int iLeft, int iTop, int iWidth, int iHeight)
	{	
		rectFocus.set(iLeft, iTop, iLeft + iWidth, iTop + iHeight);
		rectFrame.set(rectFocus);
		rectFrame.inset(1, 1);
	}
	
	public boolean isDefaultKey()
	{
		return ((iType == kDefault) || (iType == kDefaultSpace));
	}
	
	public boolean isSpaceKey()
	{
		return (iType == kDefaultSpace);
	}	

	public boolean isModeKey()
	{
		return (iType == kMode);
	}

	public boolean isCycleKey()
	{
		return (iType == kAlphaCycle);
	}
	
	public void sendKeyEvent(View targetView, int iAction, int iKeyCode)
	{
		KeyEvent event = new KeyEvent(iAction, iKeyCode);
		targetView.dispatchKeyEvent(event);
	}

	public void sendKeyEventClick(View targetView)
	{
		sendKeyEvent(targetView, KeyEvent.ACTION_DOWN, iCode);
		sendKeyEvent(targetView, KeyEvent.ACTION_UP, iCode);
	}

	public void sendKeyEventModifier(View targetView, int iAction, boolean bCapital)
	{
		if (bCapital)
			sendKeyEvent(targetView, iAction, KeyEvent.KEYCODE_SHIFT_LEFT);
		if (bAlt)
			sendKeyEvent(targetView, iAction, KeyEvent.KEYCODE_ALT_LEFT);
	}
	
  public void sendKey(View targetView, boolean bCapital)
  {
  	if (iType == kDefault)
  	{
  		sendKeyEventModifier(targetView, KeyEvent.ACTION_DOWN, bCapital);    	
  		sendKeyEventClick(targetView);
  		sendKeyEventModifier(targetView, KeyEvent.ACTION_UP, bCapital);    	
  	} else {
  		if (iType == kAlphaCycle)
  		{  			
  			sendKeyCycled(targetView);  			
  		} else {
  			sendKeyEventClick(targetView);  			
  		}
  	}
  }

  private int getCycleIndex()
  {
  	if (iCycleIndex > (cycleKeys.length - 1))
  		iCycleIndex = 0;
  	int iout = iCycleIndex; 
  	iCycleIndex++;
  	return iout;
  }

  private void sendKeyCycled(View targetView)
  {
  	final long lDuration = System.currentTimeMillis() - lClickTime;

  	if (lDuration > 500)
  	{
  		iCycleIndex = 0;
  	} else {
    	keyDelete.sendKey(targetView, false);  		
  	}
  	
  	final int index = getCycleIndex();
  	
  	lClickTime = System.currentTimeMillis();
  	KeyItem key = cycleKeys[index];  	
  	key.sendKey(targetView, false);  	
  }
  
}
