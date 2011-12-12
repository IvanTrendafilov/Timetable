
package uk.ac.ed.timetable.activities;


import uk.ac.ed.timetable.CommonActivity;
import uk.ac.ed.timetable.R;
import uk.ac.ed.timetable.database.DataRowNote;
import uk.ac.ed.timetable.database.DataTable;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageButton;
import uk.ac.ed.widgets.*;


//New Note activity
public class ActivityNote extends CommonActivity
{
	//private fields
	private DataRowNote dataRow = null;	
	private DataTable dataTable = null;
	
	//views
	private TouchEdit edSubject = null;
	private ImageButton btnDelete = null;
	private ImageButton btnSave = null;
		
  //methods
  @Override
  public void onCreate(Bundle icicle)
  {
    super.onCreate(icicle);      
    setContentView(R.layout.note);

    //initialize objects
    dataRow = new DataRowNote(userdb);
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
  	edSubject = (TouchEdit)findViewById(R.id.edNoteSubject);  	
  	edSubject.setOnOpenKeyboard(new TouchEdit.OnOpenKeyboard()
    {
			public void OnOpenKeyboardEvent()
			{
        KeyboardWidget.Open(ActivityNote.this, edSubject.getText().toString());						
			}        	        	
    });  	

  	btnSave = (ImageButton)findViewById(R.id.btnNoteSave);
  	btnSave.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SaveData();
			}
  	});
  	
  	btnDelete = (ImageButton)findViewById(R.id.btnNoteDelete);
  	btnDelete.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				DeleteData();
			}		
  	});
  }
  
  private void InitState()
  {
  	//title
  	String sSubTitle = utils.GetResStr(R.string.titleDefaultNote); 

  	//INSERT MODE
  	if (GetStartMode() == StartMode.NEW)
  	{
  		sSubTitle = utils.GetResStr(R.string.titleNewNote);
      btnDelete.setVisibility(View.INVISIBLE);
  	}
  	
  	//EDIT MODE
  	if (GetStartMode() == StartMode.EDIT)
  	{
  		sSubTitle = utils.GetResStr(R.string.titleEditNote);
  		edSubject.setText(dataRow.GetSubject());  		
      btnDelete.setVisibility(View.VISIBLE);      
  	}

  	restoreStateFromFreezeIfRequired();
  	
  	SetActivityTitle(sSubTitle);

  	//set focus to subject
  	edSubject.requestFocus();
  	if (GetStartMode() == StartMode.EDIT)
  		edSubject.setSelection(edSubject.length());  	
  }
   
  public void SaveData()
  {  
  	dataRow.SetSubject(edSubject.getText().toString());
  	
  	if (SaveDataToTable(dataTable))
			CloseActivity(dataTable);
  }  
	
  public void DeleteData()
  {  	
		if (DeleteDataFromTable(dataTable))
			CloseActivity(dataTable);
  }
  
  @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
  	super.onActivityResult(requestCode, resultCode, data);
  	
  	Bundle extras = CommonActivity.getIntentExtras(data);
  	if (extras != null)
  	{
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
  }
  
	@Override
	protected void restoreStateFromFreeze()
	{
 		edSubject.setText(freeze.getString("subject"));
	}    
  
}
