
package uk.ac.ed.widgets;


import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;


public class KeyboardWidget extends Activity
{
	//fields
	public static final int EDIT_TEXT_REQUEST = 113;
	private static final int iSmallButtonWidth = 100;	
	
	//margin from left/right to window frame: 8px. window frame: 2px
	public static final int iMaxWidth = 320 - (20 * 2);
	public static final int iMaxHeight = 480;
	
	//fields
	private static String sStrTitle = "Enter text";
	private static String sStrSet = "set";
	private static String sStrCancel= "cancel";
	
	//fields
	private LinearLayout layContent = null;
	private KeyboardWidgetView keyboard = null;
	private HintEdit edit = null;
	private Button btnCancel = null;
	private Button btnSet = null;
	
  //methods
  @Override
  public void onCreate(Bundle icicle)
  {
  	super.onCreate(icicle);
  	
		setTitle(sStrTitle);
  	  	
  	//get startup data
  	String sText = "";
  	Bundle data = this.getIntent().getExtras();
  	if (data != null)
  	{
  		if (data.containsKey("text"))
  			sText = data.getString("text");
  	}  	
  	
  	//restore data from freeze state
  	if (icicle != null)
  	{
  		sText = icicle.getString("freezeText");
  	}
  	
  	setContentView(generateContentView());
  	
  	initializeEditing(sText);
  }
  
  @Override
  protected void onSaveInstanceState(Bundle outState)
  {
  	super.onSaveInstanceState(outState);
  	//save controls state
  	outState.putString("freezeText", edit.getText().toString().trim());
  }
  
  @Override
  public void onStart()
  {
  	super.onStart();
  	
  }
  
	public static void setStrings(String sStrTitle)
  {  	
		KeyboardWidget.sStrTitle = new String(sStrTitle);
  }  
	
	public static void setStrings(String sStrTitle, String sStrCancel, String strSet)
  {  	
		sStrTitle = new String(sStrTitle);
		sStrCancel = new String(sStrCancel);
		strSet = new String(strSet);
  }  	
  
  public static void Open(Activity parentActivity, String sText)
  {
    Intent it = new Intent("android.intent.action.Timetable.ACTION_MODE_EDIT_TEXT");      
    Bundle data = new Bundle();
    data.putString("text", sText);
    it.putExtras(data);
    parentActivity.startActivityForResult(it, EDIT_TEXT_REQUEST);
  }  

  public static String GetTextOnActivityResult(int requestCode, int resultCode, Bundle extras)
  {
		if ((requestCode == KeyboardWidget.EDIT_TEXT_REQUEST) && (resultCode == RESULT_OK)) 
		{
    	if (extras.containsKey("text"))
    	{
    		return extras.getString("text");
    	}
		}
		return "";
  }
  
  public LinearLayout createLayout(int iOrientation)
  {
		LinearLayout lay = new LinearLayout(this);		
		lay.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		lay.setOrientation(iOrientation);
		return lay;
  }
  
  public Button createButton(String sText, int iWidth, int iHeight)
  {
  	Button btn = new Button(this);
  	btn.setText(sText);
  	btn.setLayoutParams(new LayoutParams(iWidth, iHeight));
  	return btn;
  }  

  public TextView createLabel(String sText, int iWidth, int iHeight)
  {
  	TextView label = new TextView(this);
  	label.setText(sText);
  	label.setLayoutParams(new LayoutParams(iWidth, iHeight));
  	return label;
  }
  
  public void generateBottomButtons(LinearLayout layBottomControls)
  {
  	TextView labMargin = createLabel("", 8, android.view.ViewGroup.LayoutParams.FILL_PARENT);
  	
  	btnCancel = createButton(sStrCancel, iSmallButtonWidth, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);  	
  	btnCancel.setBackgroundResource(android.R.drawable.btn_default_small);  	
  	
  	btnSet = createButton(sStrSet, iSmallButtonWidth, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);  	
  	btnSet.setBackgroundResource(android.R.drawable.btn_default_small);
  	
  	//set events
  	btnCancel.setOnClickListener(new Button.OnClickListener()
  	{
			public void onClick(View arg0) {
				OnClose(false);
		}});
  	btnSet.setOnClickListener(new Button.OnClickListener()
  	{
			public void onClick(View arg0) {				
				OnClose(true);
		}});
  	
  	layBottomControls.setGravity(Gravity.CENTER_HORIZONTAL);
  	layBottomControls.addView(btnCancel);
		layBottomControls.addView(labMargin);
  	layBottomControls.addView(btnSet);  	
  }
  
	public void OnClose(boolean bOK)
	{
    Bundle data = new Bundle();    
    data.putString("text", edit.getText().toString().trim());    
		
		Intent intentData = new Intent("");
		intentData.putExtras(data);
		setResult(bOK?RESULT_OK:RESULT_CANCELED, intentData);
		
		this.finish();
	}
  
  public HintEdit createEditBox()
  {
  	edit = new HintEdit(this);
		edit.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		edit.setLines(3);
		edit.setGravity(Gravity.TOP);
		edit.setFocusable(true);
				
		return edit;
  }

  private View generateContentView()
  {
  	LinearLayout layMain = createLayout(LinearLayout.VERTICAL);
  	layMain.setPadding(8, 8, 8, 8);
  	
  	LinearLayout layTopControls = createLayout(LinearLayout.HORIZONTAL);
  	LinearLayout layMargin = createLayout(LinearLayout.HORIZONTAL);
		layMargin.getLayoutParams().height = 12;

  	LinearLayout layContentBottom = createLayout(LinearLayout.HORIZONTAL);
  	layContentBottom.getLayoutParams().height = 18;
  	
		LinearLayout layBottomControls = createLayout(LinearLayout.HORIZONTAL);
  	
  	//top edit box
  	edit = createEditBox();
		layTopControls.addView(edit);
  	
  	//content
  	layContent = createLayout(LinearLayout.VERTICAL);
  	  	
  	keyboard = new KeyboardWidgetView(this, iMaxWidth);  
  	
  	layContent.getLayoutParams().width = iMaxWidth;
  	layContent.getLayoutParams().height = keyboard.getTotalHeight();
  	
  	layContent.addView(keyboard);
  	
  	generateBottomButtons(layBottomControls);
  	
  	layMain.addView(layTopControls);
  	layMain.addView(layMargin);
  	layMain.addView(layContent);
  	layMain.addView(layContentBottom);
  	layMain.addView(layBottomControls);
  	
  	return layMain;
  }

  public void initializeEditing(String sText)
  {
  	keyboard.setKeyClickEvent(mOnKeyClick);
  	keyboard.editText = edit;  	
		edit.setText(sText);
  	edit.setSelectAllOnFocus(true);  	
  } 
  
	public KeyboardWidgetView.OnKeyClick mOnKeyClick = new KeyboardWidgetView.OnKeyClick()	
	{
		public void OnKeyClicked(KeyItem key, boolean bCapital)
		{
			key.sendKey(edit, bCapital);
			autoChangeLayout(key);
		}
	};
	
	public void autoChangeLayout(KeyItem key)
	{
		KeyItemLayout keys = keyboard.getKeyLayout();
		String sText = edit.getText().toString();
		
		//reset layout
		if (sText.length() == 0)
		{
			keys.setLayoutBigCaps();
			keyboard.invalidate();
			return;
		}
		
		//change layout to small caps after first letter
		if (sText.length() == 1)
		{
			keys.setLayoutSmallCaps();
			keyboard.invalidate();
			return;
		}
		
		//change layout to big caps after period
		if ((sText.length() > 0) && (key.iType == KeyItem.kAlphaCycle))
		{
			if (sText.charAt(sText.length() - 1) == '.')
			{
				keys.setLayoutBigCaps();
				keyboard.invalidate();
				return;
			}
			if (sText.charAt(sText.length() - 1) == ',')
			{
				keys.setLayoutSmallCaps();
				keyboard.invalidate();
				return;
			}			
		}
		
		//change layout to big caps after period and big letter		
		int index = sText.lastIndexOf(".");
		if ((index != -1) && (sText.length() > 0))
		{
			sText = sText.substring(index + 1).trim();		
			if (sText.length() == 1)
			{
				if (Character.isUpperCase(sText.charAt(0)))
				{
					keys.setLayoutSmallCaps();
					keyboard.invalidate();
					return;					
				}			
			}
		}		
		
	}

}
