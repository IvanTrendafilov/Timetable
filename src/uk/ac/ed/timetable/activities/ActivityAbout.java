
package uk.ac.ed.timetable.activities;


import uk.ac.ed.timetable.CommonActivity;
import uk.ac.ed.timetable.R;
import uk.ac.ed.timetable.Utils;
import android.os.Bundle;
import android.widget.TextView;


//New About activity
public class ActivityAbout extends CommonActivity
{
	//views
	private TextView txtView = null;
	private TextView txtVersionLabel = null;
	
  //methods
  @Override
  public void onCreate(Bundle icicle)
  {
      super.onCreate(icicle);
      setContentView(R.layout.about);

      //initialize views
      InitViews();    	
  }
  
  @Override
  public void onStart()
  {
  	super.onStart();
  	    
    InitState();
  }
	
  private void InitViews()
  {
  	txtView = (TextView)findViewById(R.id.txtViewAbout);
  	txtVersionLabel = (TextView)findViewById(R.id.txtVersionLabel);
  }
  
  private void InitState()
  {
  	String sSubTitle = utils.GetResStr(R.string.titleDefaultAbout); 
		SetActivityTitle(sSubTitle);

		txtVersionLabel.setText(Utils.getAppVersionName(this));
  	
  	txtView.requestFocus();
  }

	@Override
	protected void restoreStateFromFreeze()
	{
		
	}
   
}
