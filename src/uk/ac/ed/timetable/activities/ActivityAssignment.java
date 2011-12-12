
package uk.ac.ed.timetable.activities;


import java.util.*;
import uk.ac.ed.widgets.*;
import uk.ac.ed.timetable.CommonActivity;
import uk.ac.ed.timetable.R;
import uk.ac.ed.timetable.database.DataRowAssignment;
import uk.ac.ed.timetable.database.DataTable;
import uk.ac.ed.widgets.DateWidget;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;


//New assignment activity
public class ActivityAssignment extends CommonActivity
{
	//private fields
	private Calendar dateDue = null;	
	private DataRowAssignment dataRow = null;
	private DataTable dataTable = null;	
  private int iAssignmentPriority = 2;
  
	//views
	private TouchEdit edSubject = null;
	private CheckBox chkDone = null;
	private CheckBox chkAlarm = null;
	private Button btnDueDate = null;
	private ImageButton btnDelete = null;
	private ImageButton btnSave = null;
	
	//priority views
	private Button btnAssignmentPriorityLow = null;
	private Button btnAssignmentPriorityDefault = null;
	private Button btnAssignmentPriorityHigh = null;	
		
  //methods
  @Override
  public void onCreate(Bundle icicle)
  {
    super.onCreate(icicle);      
    setContentView(R.layout.assignment);
    
    //initialize objects
    dataRow = new DataRowAssignment(userdb);
  	dataTable = new DataTable(dataRow);      

    //check startup mode and open data
  	if (GetStartMode() == StartMode.EDIT)
  		if (!OpenDataForEdit(dataTable))
  			finish();
        	
    //initialize views
    InitViews();    	
    InitState();
  }

  @Override
  public void onStart()
  {
  	super.onStart();
  	    
  }
  
  @Override
  public void onResume()
  {
  	super.onResume();

  }
    
  @Override
  public void onPause()
  {
  	super.onPause();
  	
  }
	
  private void InitViews()
  {
  	edSubject = (TouchEdit)findViewById(R.id.edAssignmentSubject);
  	edSubject.setOnOpenKeyboard(new TouchEdit.OnOpenKeyboard()
    {
			public void OnOpenKeyboardEvent()
			{
        KeyboardWidget.Open(ActivityAssignment.this, edSubject.getText().toString());						
			}        	        	
    });  	
  	
  	chkDone = (CheckBox)findViewById(R.id.chkAssignmentDone);   
  	
    chkAlarm = (CheckBox)findViewById(R.id.chkAssignmentAlarm);  	
  	btnDueDate = (Button)findViewById(R.id.btnAssignmentDueDate);

  	btnSave = (ImageButton)findViewById(R.id.btnAssignmentSave);
  	btnSave.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SaveData();
			}
  	});
  	
  	btnDelete = (ImageButton)findViewById(R.id.btnAssignmentDelete);
  	btnDelete.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				DeleteData();
			}		
  	});
  	
  	btnDueDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				DateWidget.Open(ActivityAssignment.this, true, dateDue, prefs.iFirstDayOfWeek);
				
			}
		});
  	
  	//priority
  	btnAssignmentPriorityLow = (Button)findViewById(R.id.btnAssignmentPriorityLow);
  	btnAssignmentPriorityLow.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SetPriority(3);
			}
  	});
  	btnAssignmentPriorityDefault = (Button)findViewById(R.id.btnAssignmentPriorityDefault);
  	btnAssignmentPriorityDefault.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SetPriority(2);
			}
  	});
  	btnAssignmentPriorityHigh = (Button)findViewById(R.id.btnAssignmentPriorityHigh);
  	btnAssignmentPriorityHigh.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SetPriority(1);
			}
  	});
  	
  }
  
  public void SetPriority(int iPriority)
  {
  	//update global value
  	this.iAssignmentPriority = iPriority;
  	//enable buttons
  	btnAssignmentPriorityHigh.setEnabled((iPriority != 1));
  	btnAssignmentPriorityDefault.setEnabled((iPriority != 2));
  	btnAssignmentPriorityLow.setEnabled((iPriority != 3));
  	Button btn = null;
  	if (iPriority == 1)
  		btn = btnAssignmentPriorityHigh;
  	// 1: Daily
  	if (iPriority == 2)
  		btn = btnAssignmentPriorityDefault;
  	// 2: Weekly
  	if (iPriority == 3)
  		btn = btnAssignmentPriorityLow;
  	//set focus
  	if ((btn != null) && (!btn.isFocused())) 
  		btn.requestFocus();  	  	
  }  
  
  private void InitState()
  {
  	//title
  	String sSubTitle = utils.GetResStr(R.string.titleDefaultCourse); 

  	dateDue = Calendar.getInstance();

  	//INSERT MODE
  	if (GetStartMode() == StartMode.NEW)
  	{
  		dateDue.setTimeInMillis(0);
  		sSubTitle = utils.GetResStr(R.string.titleNewAssignment);
      btnDelete.setVisibility(View.INVISIBLE);
      chkDone.setChecked(false);      
      chkDone.setVisibility(View.GONE);
      SetPriority(2);
    	chkAlarm.setChecked(false);
  	}
  	
  	//EDIT MODE
  	if (GetStartMode() == StartMode.EDIT)
  	{  		
  		dateDue.setTimeInMillis(dataRow.GetDueDate().getTimeInMillis());      
  		sSubTitle = utils.GetResStr(R.string.titleEditAssignment);
  		edSubject.setText(dataRow.GetSubject());
      btnDelete.setVisibility(View.VISIBLE);      
      chkDone.setChecked(dataRow.GetDone());
      chkDone.setVisibility(View.VISIBLE);      
      SetPriority(dataRow.GetPriority());
    	chkAlarm.setChecked(dataRow.GetAlarm());
  	}

  	restoreStateFromFreezeIfRequired();
  	
  	SetActivityTitle(sSubTitle);
    UpdateDueDateView();

  	//set focus to subject
  	edSubject.requestFocus();
  	if (GetStartMode() == StartMode.EDIT)
  		edSubject.setSelection(edSubject.length());  	
  }
   
  private void UpdateDueDateView()
  {
  	if (dateDue.getTimeInMillis() != 0)
		{
	  	btnDueDate.setText(utils.GetLongDate(dateDue));
		} else {
			btnDueDate.setText(utils.GetResStr(R.string.labNoDate));
		}	
  }  

  public void SaveData()
  {
  	if (dateDue.getTimeInMillis() != 0)
  		if (DateBeforeNow(dateDue))
  			return;
  	
  	dataRow.SetSubject(edSubject.getText().toString());
  	dataRow.SetDone(chkDone.isChecked());
  	dataRow.SetPriority(this.iAssignmentPriority);
  	dataRow.SetAlarm(chkAlarm.isChecked());
  	
  	if (dateDue.getTimeInMillis() != 0)
  	{
    	dataRow.SetDueDate(dateDue);
  	} else {
    	dataRow.SetDueDate(null);
  	}
  	
  	if (SaveDataToTable(dataTable))
			CloseActivity(dataTable);
  }  
	
  public void DeleteData()
  {  	
		if (DeleteDataFromTable(dataTable))
			CloseActivity(dataTable);
  }
  
  @Override
  public void SaveDateValuesBeforeChange(Bundle data)
  {
  	super.SaveDateValuesBeforeChange(data);
  	if (GetStartMode() == StartMode.EDIT)
  	{
  		if (dataRow.UsingDueDate())
  			data.putLong("dateDue", dataRow.GetDueDate().getTimeInMillis());
  	}
  }  
  
  @Override
  public boolean DateValuesChanged(Bundle data)
  {
  	super.DateValuesChanged(data);
  	if (GetStartMode() == StartMode.EDIT)
  	{
  		if (dataRow.UsingDueDate())
  			if (dateDue.getTimeInMillis() != data.getLong("dateDue"))
  				return true;
  	}
  	return false;
  }

  @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
  	super.onActivityResult(requestCode, resultCode, data);
		
  	Bundle extras = CommonActivity.getIntentExtras(data);
  	if (extras != null)
  	{
  	
	  	//check for date widget edit request code
	  	if (requestCode == DateWidget.SELECT_DATE_REQUEST)
	  	{
	    	final long lDate = DateWidget.GetSelectedDateOnActivityResult(requestCode, resultCode, extras, dateDue);
	    	if (lDate != -1)
	    	{
	    		UpdateDueDateView();
	    	}
	  	}
	
	  	//get KeyboardWidget result
	  	if ((requestCode == KeyboardWidget.EDIT_TEXT_REQUEST) && (resultCode == RESULT_OK)) 
	  	{
				String sText = KeyboardWidget.GetTextOnActivityResult(requestCode, resultCode, extras);    			
				edSubject.setText(sText);
	  	}
	  	
  	}  	
  }

  @Override
  protected void onSaveInstanceState(Bundle outState)
  {
  	super.onSaveInstanceState(outState);
  	//save controls state
  	outState.putString("subject", edSubject.getText().toString());
  	outState.putBoolean("alarm", chkAlarm.isChecked());  	
  	outState.putInt("priority", this.iAssignmentPriority);
  	outState.putLong("dateDue", dateDue.getTimeInMillis());
  }  
  
	@Override
	protected void restoreStateFromFreeze()
	{
 		edSubject.setText(freeze.getString("subject"));  	
 		chkAlarm.setChecked(freeze.getBoolean("alarm"));
 		SetPriority(freeze.getInt("priority"));
 		dateDue.setTimeInMillis(freeze.getLong("dateDue"));
	}  
  
}
