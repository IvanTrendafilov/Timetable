
package uk.ac.ed.widgets;


import java.util.ArrayList;

import android.view.*;


public class KeyItemLayout
{
	//alpha layout
	public final KeyItem[] alphaRow1 = {
		new KeyItem("Q", KeyEvent.KEYCODE_Q, KeyItem.kDefault, false),
		new KeyItem("W", KeyEvent.KEYCODE_W, KeyItem.kDefault, false),
		new KeyItem("E", KeyEvent.KEYCODE_E, KeyItem.kDefault, false),
		new KeyItem("R", KeyEvent.KEYCODE_R, KeyItem.kDefault, false),
		new KeyItem("T", KeyEvent.KEYCODE_T, KeyItem.kDefault, false),
		new KeyItem("Y", KeyEvent.KEYCODE_Y, KeyItem.kDefault, false),
		new KeyItem("U", KeyEvent.KEYCODE_U, KeyItem.kDefault, false),
		new KeyItem("I", KeyEvent.KEYCODE_I, KeyItem.kDefault, false),
		new KeyItem("O", KeyEvent.KEYCODE_O, KeyItem.kDefault, false),
		new KeyItem("P", KeyEvent.KEYCODE_P, KeyItem.kDefault, false),
	};

	public final KeyItem[] alphaRow2 = {
		new KeyItem("A", KeyEvent.KEYCODE_A, KeyItem.kDefault, false),
		new KeyItem("S", KeyEvent.KEYCODE_S, KeyItem.kDefault, false),
		new KeyItem("D", KeyEvent.KEYCODE_D, KeyItem.kDefault, false),
		new KeyItem("F", KeyEvent.KEYCODE_F, KeyItem.kDefault, false),
		new KeyItem("G", KeyEvent.KEYCODE_G, KeyItem.kDefault, false),
		new KeyItem("H", KeyEvent.KEYCODE_H, KeyItem.kDefault, false),
		new KeyItem("J", KeyEvent.KEYCODE_J, KeyItem.kDefault, false),
		new KeyItem("K", KeyEvent.KEYCODE_K, KeyItem.kDefault, false),
		new KeyItem("L", KeyEvent.KEYCODE_L, KeyItem.kDefault, false),
	};

	public final KeyItem[] alphaRow3 = {
		new KeyItem("Z", KeyEvent.KEYCODE_Z, KeyItem.kDefault, false),
		new KeyItem("X", KeyEvent.KEYCODE_X, KeyItem.kDefault, false),
		new KeyItem("C", KeyEvent.KEYCODE_C, KeyItem.kDefault, false),
		new KeyItem("V", KeyEvent.KEYCODE_V, KeyItem.kDefault, false),
		new KeyItem("B", KeyEvent.KEYCODE_B, KeyItem.kDefault, false),
		new KeyItem("N", KeyEvent.KEYCODE_N, KeyItem.kDefault, false),
		new KeyItem("M", KeyEvent.KEYCODE_M, KeyItem.kDefault, false),
	};
	
	//digit layout
	public final KeyItem[] digitRow1 = {
		new KeyItem("1", KeyEvent.KEYCODE_1, KeyItem.kDefault, false),
		new KeyItem("2", KeyEvent.KEYCODE_2, KeyItem.kDefault, false),
		new KeyItem("3", KeyEvent.KEYCODE_3, KeyItem.kDefault, false),
		new KeyItem("4", KeyEvent.KEYCODE_4, KeyItem.kDefault, false),
		new KeyItem("5", KeyEvent.KEYCODE_5, KeyItem.kDefault, false),
		new KeyItem("6", KeyEvent.KEYCODE_6, KeyItem.kDefault, false),
		new KeyItem("7", KeyEvent.KEYCODE_7, KeyItem.kDefault, false),
		new KeyItem("8", KeyEvent.KEYCODE_8, KeyItem.kDefault, false),
		new KeyItem("9", KeyEvent.KEYCODE_9, KeyItem.kDefault, false),
		new KeyItem("0", KeyEvent.KEYCODE_0, KeyItem.kDefault, false),
	};

	public final KeyItem[] digitRow2 = {
		new KeyItem("!", KeyEvent.KEYCODE_1, KeyItem.kDefault, true),
		new KeyItem("@", KeyEvent.KEYCODE_AT, KeyItem.kDefault, false),
		new KeyItem("#", KeyEvent.KEYCODE_3, KeyItem.kDefault, true),
		new KeyItem("$", KeyEvent.KEYCODE_4, KeyItem.kDefault, true),
		new KeyItem("%", KeyEvent.KEYCODE_5, KeyItem.kDefault, true),
		new KeyItem("&", KeyEvent.KEYCODE_7, KeyItem.kDefault, true),
		new KeyItem("*", KeyEvent.KEYCODE_STAR, KeyItem.kDefault, false),
		new KeyItem("(", KeyEvent.KEYCODE_9, KeyItem.kDefault, true),
		new KeyItem(")", KeyEvent.KEYCODE_0, KeyItem.kDefault, true),
	};

	public final KeyItem[] digitRow3 = {
		new KeyItem("-", KeyEvent.KEYCODE_I, KeyItem.kDefault, true),
		new KeyItem("+", KeyEvent.KEYCODE_O, KeyItem.kDefault, true),
		new KeyItem("=", KeyEvent.KEYCODE_P, KeyItem.kDefault, true),
		new KeyItem("/", KeyEvent.KEYCODE_SLASH, KeyItem.kDefault, false),
		new KeyItem("\\", KeyEvent.KEYCODE_S, KeyItem.kDefault, true),
		new KeyItem("\"", KeyEvent.KEYCODE_E, KeyItem.kDefault, true),
		new KeyItem(":", KeyEvent.KEYCODE_L, KeyItem.kDefault, true),
	};

	//alpha cycle keys chain
	public final KeyItem[] cycleKeysAlpha = {
		new KeyItem(".", KeyEvent.KEYCODE_PERIOD, KeyItem.kDefault, false),
		new KeyItem(",", KeyEvent.KEYCODE_COMMA, KeyItem.kDefault, false),
		new KeyItem("?", KeyEvent.KEYCODE_SLASH, KeyItem.kDefault, true),
		new KeyItem(";", KeyEvent.KEYCODE_K, KeyItem.kDefault, true),
	};
	
	//special keys indexes
	public final static int iSpecKeyMode = 0;
	public final static int iSpecKeyDel = 1;
	public final static int iSpecKeyReturn = 2;
	public final static int iSpecKeySpace = 3;
	public final static int iSpecKeyCycle = 4;
	
	//special keys
	public final KeyItem[] specialKeys = {
		new KeyItem("ABC", 0, KeyItem.kMode, false),
		new KeyItem("del", KeyEvent.KEYCODE_DEL, KeyItem.kDelete, false),
		new KeyItem("return", KeyEvent.KEYCODE_ENTER, KeyItem.kReturn, false),
		new KeyItem("space", KeyEvent.KEYCODE_SPACE, KeyItem.kDefaultSpace, false),
		new KeyItem(".,?;", cycleKeysAlpha),
	};	
		
	//fields
	private ArrayList<KeyItem> layoutAlpha = new ArrayList<KeyItem>();
	private ArrayList<KeyItem> layoutDigit = new ArrayList<KeyItem>();
	
	//fields
	private int iCurrentLayoutMode = 0;
	
	//methods
	public KeyItemLayout()
	{
		//generate alpha layout
		generateLayout(alphaRow1, layoutAlpha);
		generateLayout(alphaRow2, layoutAlpha);
		generateLayout(alphaRow3, layoutAlpha);
		generateLayout(specialKeys, layoutAlpha);
		
		//generate alpha layout
		generateLayout(digitRow1, layoutDigit);
		generateLayout(digitRow2, layoutDigit);
		generateLayout(digitRow3, layoutDigit);
		generateLayout(specialKeys, layoutDigit);
		
		//current layout mode
		setLayoutMode(0);
	}
	
	private void generateLayout(KeyItem[] srcItems, ArrayList<KeyItem> destLayout)
	{
		for (int i = 0; i < srcItems.length; i++)		
			destLayout.add(srcItems[i]);		
	}
	
	public ArrayList<KeyItem> getLayout()
	{
		switch (iCurrentLayoutMode)
		{
			case 0: return layoutAlpha;
			case 1: return layoutAlpha;
			case 2: return layoutDigit;
		}
		return null;
	}	
	
	public String getLayoutModeSymbol()
	{
		switch (iCurrentLayoutMode)
		{
			case 0: return "ABC";
			case 1: return "abc";
			case 2: return "123";
		}
		return "";
	}
		
	//values: 0,1,2 (ABC/abc/123)
	public void setLayoutMode(int iModeIndex)
	{
		iCurrentLayoutMode = iModeIndex;		
	}

	public void setLayoutBigCaps()
	{
		iCurrentLayoutMode = 0;		
	}

	public void setLayoutSmallCaps()
	{
		iCurrentLayoutMode = 1;
	}

	public void setLayoutDigits()
	{
		iCurrentLayoutMode = 2;
	}
	
	public void cycleLayoutMode()
	{
		iCurrentLayoutMode++;
		if (iCurrentLayoutMode > 2)
			iCurrentLayoutMode = 0;			
	}
		
	public int getLayoutMode()
	{
		return iCurrentLayoutMode;
	}
	
	public boolean isCapitalMode()
	{
		return (iCurrentLayoutMode == 0);
	}
	
	public String getKeySymbol(KeyItem key)
	{
		if (key.iType == KeyItem.kMode)			
			return getLayoutModeSymbol();
		if (key.iType == KeyItem.kDefault)
		{
			if (iCurrentLayoutMode == 0)
				return key.sChar;
			if (iCurrentLayoutMode == 1)
				return key.sCharSmall;
			if (iCurrentLayoutMode == 2)
				return key.sChar;
		}
		if (key.iType == KeyItem.kDefaultSpace)
			return key.sCharSmall;
		if (key.iType == KeyItem.kDelete)
			return key.sCharSmall;
		if (key.iType == KeyItem.kReturn)
			return key.sCharSmall;
		if (key.iType == KeyItem.kAlphaCycle)
			return key.sCharSmall;
		return "";
	}
	
}

