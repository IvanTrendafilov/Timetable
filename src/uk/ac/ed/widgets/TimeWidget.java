
package uk.ac.ed.widgets;


import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;


public class TimeWidget extends Activity
{
	//fields
	private static String sStrSelect = "Select time";
	private static String sStrSet = "set";
	private static String sStrNone = "none";
	
	//fields
	public static final int SELECT_TIME_REQUEST = 112;
	public static final int iSliderViewWidth = 276;
	private static final int iSmallButtonWidth = 100;	
	
	//fields
	private LinearLayout layContent = null;
	private Button btnNone = null;
	private Button btnOK = null;
	
	//fields
	private boolean bNoneButton = true;
	private boolean b24HourMode = false;
	private int iHour = -1;
	private int iMinutes = -1;
	
	//fields
	TimeCaption labCaption = null;
	TimeWidgetSlider timeSliderHour = null;
	TimeWidgetSlider timeSliderMinutes = null;

	
  //methods
  @Override
  public void onCreate(Bundle icicle)
  {
  	super.onCreate(icicle);
  	
		setTitle(sStrSelect);

  	//init to defaults
  	bNoneButton = true;
  	b24HourMode = false;
  	iHour = -1;
  	iMinutes = -1;

  	//get startup data
  	Bundle data = this.getIntent().getExtras();
  	if (data != null)
  	{
  		if (data.containsKey("noneButton"))
  			bNoneButton = data.getBoolean("noneButton");
  		if (data.containsKey("24HourMode"))
  			b24HourMode = data.getBoolean("24HourMode");
  		if (data.containsKey("Hour"))
  			iHour = data.getInt("Hour");
  		if (data.containsKey("Minute"))
  			iMinutes = data.getInt("Minute");
  	}  	

  	setContentView(generateContentView());
  	
  	timeSliderHour.setValue(iHour, false);
  	timeSliderMinutes.setValue(iMinutes, false);
  	
  	timeSliderHour.requestFocus();
  }
  
  @Override
  protected void onSaveInstanceState(Bundle outState)
  {
  	super.onSaveInstanceState(outState);
  	
  	//cancel time edit on activity change
		this.finish();
  }
  
  @Override
  public void onStart()
  {
  	super.onStart();
  	
  }
  
	public static void setStrings(String strSelect, String strNone, String strSet)
  {  	
		sStrSelect = new String(strSelect);
  	sStrNone = new String(strNone);
  	sStrSet = new String(strSet);
  }  
  
  public static void Open(Activity parentActivity, boolean bNoneButton, boolean b24HourMode, int iHour, int iMinute)
  {
    Intent it = new Intent("android.intent.action.Timetable.ACTION_MODE_EDIT_SELECT_TIME");      
    Bundle data = new Bundle();
    data.putInt("Hour", iHour);
    data.putInt("Minute", iMinute);    
    data.putBoolean("24HourMode", b24HourMode);
    data.putBoolean("noneButton", bNoneButton);
    it.putExtras(data);    
    parentActivity.startActivityForResult(it, SELECT_TIME_REQUEST);
  }
  
  public static int GetSelectedTimeHourOnActivityResult(int requestCode, int resultCode, Bundle extras)
  {
		if ((requestCode == TimeWidget.SELECT_TIME_REQUEST) && (resultCode == RESULT_OK))
    	if (extras.containsKey("Hour"))
    		return extras.getInt("Hour");
		return -1;
  }
  
  public static int GetSelectedTimeMinuteOnActivityResult(int requestCode, int resultCode, Bundle extras)
  {
		if ((requestCode == TimeWidget.SELECT_TIME_REQUEST) && (resultCode == RESULT_OK))
    	if (extras.containsKey("Minute"))
    		return extras.getInt("Minute");
		return -1;
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

  private void getDataFromSliders()
  {
  	iHour = timeSliderHour.getValue();
  	iMinutes = timeSliderMinutes.getValue();
  }
  
  public void generateBottomButtons(LinearLayout layBottomControls)
  {
  	TextView labMargin = createLabel("", 8, android.view.ViewGroup.LayoutParams.FILL_PARENT);

  	btnNone = createButton(sStrNone, iSmallButtonWidth, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);  	
  	btnNone.setBackgroundResource(android.R.drawable.btn_default_small);
  	
  	btnOK = createButton(sStrSet, iSmallButtonWidth, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);  	
  	btnOK.setBackgroundResource(android.R.drawable.btn_default_small);
  	
  	//set events
  	btnNone.setOnClickListener(new Button.OnClickListener()
  	{
			public void onClick(View arg0) {
				clearTime();
				OnClose();
		}});

  	btnOK.setOnClickListener(new Button.OnClickListener()
  	{
			public void onClick(View arg0) {
				getDataFromSliders();
				OnClose();
		}});
  	
  	layBottomControls.setGravity(Gravity.CENTER_HORIZONTAL);  	
  	if (bNoneButton)
  	{
  		layBottomControls.addView(btnNone);
  		layBottomControls.addView(labMargin);
  	}
  	layBottomControls.addView(btnOK);
  }
  
  private View createLabelCaption()
  {  	
  	labCaption = new TimeCaption(this, b24HourMode, iSliderViewWidth - 120);  	
  	return labCaption;
  }

  private View generateContentView()
  {
  	LinearLayout layMain = createLayout(LinearLayout.VERTICAL);  	
  	layMain.setPadding(8, 8, 8, 8);

  	LinearLayout layTopControls = createLayout(LinearLayout.HORIZONTAL);
  	LinearLayout layContentTop = createLayout(LinearLayout.HORIZONTAL);
  	LinearLayout layContentBottom = createLayout(LinearLayout.HORIZONTAL);
  	LinearLayout layBottomControls = createLayout(LinearLayout.HORIZONTAL);
  	LinearLayout layMargin = createLayout(LinearLayout.HORIZONTAL);
  	layContent = createLayout(LinearLayout.VERTICAL);
  	
  	layTopControls.addView(createLabelCaption());  	
  	layTopControls.setGravity(Gravity.CENTER_HORIZONTAL);
  	
  	generateBottomButtons(layBottomControls);

  	layContentTop.getLayoutParams().height = 16;
  	layContentBottom.getLayoutParams().height = 18;
  	layMargin.getLayoutParams().height = 18;

		timeSliderHour = new TimeWidgetSlider(this, b24HourMode, TimeWidgetSlider.STYPE_HOURS, iSliderViewWidth);
		timeSliderMinutes = new TimeWidgetSlider(this, b24HourMode, TimeWidgetSlider.STYPE_MINUTES, iSliderViewWidth);

		timeSliderHour.setTimeChangeEvent(mOnTimeChangeEvent);
		timeSliderMinutes.setTimeChangeEvent(mOnTimeChangeEvent);		

		layContent.addView(timeSliderHour);
		layContent.addView(layMargin);
		layContent.addView(timeSliderMinutes);
		
  	layMain.addView(layTopControls);
  	layMain.addView(layContentTop);
  	layMain.addView(layContent);
  	layMain.addView(layContentBottom);
  	layMain.addView(layBottomControls);  	

  	return layMain;
  }

	public void OnClose()
	{
    Bundle data = new Bundle();
    data.putInt("Hour", iHour);
    data.putInt("Minute", iMinutes);
    
    Intent intentData = new Intent("");
		intentData.putExtras(data);
		setResult(RESULT_OK, intentData);
		
		this.finish();
	}

	public void clearTime()
	{
		iHour = -1;
		iMinutes = -1;
	}
	
	public void updateLabelCaption()
	{				
		labCaption.setTime(timeSliderHour.getValue(), timeSliderMinutes.getValue());
	}
	
	private TimeWidgetSlider.OnTimeChange mOnTimeChangeEvent = new TimeWidgetSlider.OnTimeChange()
	{
		public void OnChange(TimeWidgetSlider slider)
		{
			updateLabelCaption();
		}		
	};
	
}
