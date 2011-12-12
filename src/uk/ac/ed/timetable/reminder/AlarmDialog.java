
package uk.ac.ed.timetable.reminder;


import uk.ac.ed.timetable.*;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import java.util.*;
import android.content.ComponentName;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.*;
import android.widget.*;


public class AlarmDialog extends CommonActivity
{	
	//list adapter for alarm items
	//---------------------------------------------------------
	public class AlarmsAdapter implements ListAdapter
	{		
		//fields
		private ArrayList<AlarmDialogDataItem> vecAlarmItems = null;
		
		//methods
		AlarmsAdapter(ArrayList<AlarmDialogDataItem> vecAlarmItems)
		{
			this.vecAlarmItems = vecAlarmItems; 
		}
		
		public int getCount()
		{
			return vecAlarmItems.size();
		}
		
		public Object getItem(int position)
		{
			return vecAlarmItems.get(position);
		}
		
		public long getItemId(int position)
		{
			return position;
		}
				
		public View getView(int position, View convertView, ViewGroup parent)
		{
      return vecAlarmItems.get(position).getView();
		}
		
		public void registerDataSetObserver(DataSetObserver observer)
		{
		}
		
		public void unregisterDataSetObserver(DataSetObserver arg0)
		{
		}
		
		public boolean areAllItemsEnabled()
		{
			return true;
		}

		public boolean isEnabled(int position)
		{
			return true;
		}

		public int getItemViewType(int position)
		{
			return 0;
		}

		public int getViewTypeCount()
		{
			return 1;
		}

		public boolean hasStableIds()
		{
			return true;
		}

		public boolean isEmpty()
		{
			return (vecAlarmItems.size() == 0);
		}
	}
	
	
	//fields
	ArrayList<AlarmDialogDataItem> vecAlarmItems = new ArrayList<AlarmDialogDataItem>();
	
	//views
	private TextView labAlarmReminderHeader = null;
	private ListView listAlarmReminderContent = null;
	private ImageView imgAlarmReminderHeader = null;

	//views
	private Button btnAlarmReminderClear = null;
	private Button btnAlarmReminderSnooze = null;
	private Button btnAlarmReminderOpen = null;
	
	//fields
	private Bundle data = null;
	private Handler handlerCloseDialog = new Handler();	
	private AlarmsManager alarmsManager = null;
	
	//fields
  private String msgYouHaveASTS = "";
  private String msgYouHaveTS = "";
  private String msgYouHaveAS = "";
  private String msgYouHaveNoASTS = ""; 
	
	//methods
  @Override
  public void onCreate(Bundle icicle)
  {
      super.onCreate(icicle);      
      setContentView(R.layout.alarmdialog);

      //create alarsm manager instance
    	alarmsManager = new AlarmsManager(this.userdb, this.prefs);

      //localize strings
      msgYouHaveASTS = utils.GetResStr(R.string.msgYouHaveASTS);
      msgYouHaveTS = utils.GetResStr(R.string.msgYouHaveTS);
      msgYouHaveAS = utils.GetResStr(R.string.msgYouHaveAS);
      msgYouHaveNoASTS = utils.GetResStr(R.string.msgYouHaveNoASTS);
  }

  @Override
  public void onStart()
  {
  	super.onStart();
  	
    //get data from intent bundle
    data = this.getIntent().getExtras();
    GetIntentDataFromBundle();
    
    //initialize views
    InitViews();
    InitState();     
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
  
  @Override
  public void onStop()
  {
  	super.onStop();
  	
		//process alarms
		alarmsManager.processAll();
		
		//send update service for refresh data
		Timetable.UpdateReminderService(this, this.prefs, CommonActivity.bundleTableUpdated);
  }
  
	public String GetPopupMsg(int iCountAppts, int iCountAssignments)
	{
		String msg = String.format(msgYouHaveASTS, iCountAppts, iCountAssignments);		
		if ((iCountAppts == 0) && (iCountAssignments > 0))
			msg = String.format(msgYouHaveTS, iCountAssignments);
		if ((iCountAssignments == 0) && (iCountAppts > 0))
			msg = String.format(msgYouHaveAS, iCountAppts);
		if ((iCountAppts == 0) && (iCountAssignments == 0))
			msg = msgYouHaveNoASTS;
		return msg;
	}
	
	private void UpdateHeaderInfo()
	{
		int iCountAppts = 0;
		int iCountAssignments = 0;
		
		for (int i = 0; i < vecAlarmItems.size(); i++)
		{
			AlarmDialogDataItem item = vecAlarmItems.get(i);
			if (item != null)
			{
				if (item.getOrderFilter() == AlarmDataViewItem.iOrderAppts)
					iCountAppts++;
				if (item.getOrderFilter() == AlarmDataViewItem.iOrderAssignments)
					iCountAssignments++;				
			}			
		}
		
		String sTitle = GetPopupMsg(iCountAppts, iCountAssignments);
  	labAlarmReminderHeader.setText(sTitle);
  	
  	//update icon
  	imgAlarmReminderHeader.setBackgroundResource(R.drawable.iconnotifyalarm);
	}
  
  private void GetIntentDataFromBundle()
  {
  	if (data != null)
  	{
  		vecAlarmItems.clear();
  		DecodeBundleData(vecAlarmItems, AlarmDataViewItem.iOrderAppts);
  		DecodeBundleData(vecAlarmItems, AlarmDataViewItem.iOrderAssignments);  		  
  	}
	}
  
  private int DecodeBundleData(ArrayList<AlarmDialogDataItem> items, int iOrderFilter)
  {
  	int iTotalCount = 0; 
		String sTotalKey = "typetotal_" + Integer.toString(iOrderFilter);
		if (data.containsKey(sTotalKey))
		{
			iTotalCount = data.getInt(sTotalKey);
			for (int i = 0; i < iTotalCount; i++)
			{
				String sKey = "_" + Integer.toString(iOrderFilter) + "_" + Integer.toString(i);

				long lID = data.getLong("rowid" + sKey); 
				String sText = data.getString("text" + sKey);

				AlarmDialogDataItem item = new AlarmDialogDataItem(this, iOrderFilter, lID, sText);						  				
				items.add(item);
			}  			
		}
		return iTotalCount;
  }  
	
  private void InitViews()
  {
  	//get views
  	labAlarmReminderHeader = (TextView)findViewById(R.id.labAlarmReminderHeader);
  	listAlarmReminderContent = (ListView)findViewById(R.id.listAlarmReminderContent);
  	imgAlarmReminderHeader = (ImageView)findViewById(R.id.imgAlarmReminderHeader);
  	
  	//buttons
  	btnAlarmReminderClear = (Button)findViewById(R.id.btnAlarmReminderClear);
  	btnAlarmReminderClear.setOnClickListener(mBtnOnClick_CLEAR);
  	
  	btnAlarmReminderSnooze = (Button)findViewById(R.id.btnAlarmReminderSnooze);
  	btnAlarmReminderSnooze.setOnClickListener(mBtnOnClick_SNOOZE);
  	
  	btnAlarmReminderOpen = (Button)findViewById(R.id.btnAlarmReminderOpen);  	
  	btnAlarmReminderOpen.setOnClickListener(mBtnOnClick_OPEN);
  }
  
  private void InitState()
  {
  	//title
  	String sSubTitle = utils.GetResStr(R.string.titleDefaultAlarmDialog);
  	SetActivityTitle(sSubTitle);
  	  	
  	UpdateHeaderInfo();

  	//fill list
  	SetListWithDataAdapter();
  	
  	alarmsManager.clear();
  	
  	btnAlarmReminderClear.setFocusable(true);
		btnAlarmReminderClear.setFocusableInTouchMode(true);
  	  	
  	listAlarmReminderContent.setOnItemClickListener(mListViewOnItemClick);
  	listAlarmReminderContent.setOnItemSelectedListener(mListViewOnItemSelected);
  	
  	SelectFirstListItem();
  	
  	UpdateBottomButtonsState();
  	
  	btnAlarmReminderClear.requestFocus();
  }
  
  private void SetListWithDataAdapter()
  {
  	listAlarmReminderContent.setAdapter(new AlarmsAdapter(vecAlarmItems));  	
  }
  
  public void SelectFirstListItem()
  {
  	ClearAllSelection();
  	if (vecAlarmItems.size() > 0)
  		vecAlarmItems.get(0).setSelected(true);  	
  }
  
  //android.widget.AdapterView.OnItemClickListener
	@SuppressWarnings("all")
  private AdapterView.OnItemClickListener mListViewOnItemClick = new AdapterView.OnItemClickListener()
  {
  	public void onItemClick(AdapterView parent, View v, int position, long id)
  	{
  		if (v != null)
  		{  			 
  			AlarmDialogDataItem.ViewItem item = (AlarmDialogDataItem.ViewItem)v;
  			if (item != null)
  			{
  				ClearAllSelection();
  				item.getDataItem().setSelected(true);
  				btnAlarmReminderClear.requestFocus();
  			}
  		}
  		UpdateBottomButtonsState();
  	}
  };  
	
  //android.widget.AdapterView.OnItemSelectedListener
	@SuppressWarnings("all")
  private AdapterView.OnItemSelectedListener mListViewOnItemSelected = new AdapterView.OnItemSelectedListener()
  {
		public void onItemSelected(AdapterView parent, View v, int position, long id)
		{
			UpdateBottomButtonsState();
		}
		public void onNothingSelected(AdapterView arg0)
		{
			UpdateBottomButtonsState();
		}  	
  };
  
	public void ClearAllSelection()
	{
		for (int i = 0; i < vecAlarmItems.size(); i++)
		{
			AlarmDialogDataItem item = vecAlarmItems.get(i);
			if (item != null)
				item.setSelected(false);
		}
	}

	public int GetAlarmItemSelectedCount()
	{
		int iCount = 0;
		for (int i = 0; i < vecAlarmItems.size(); i++)
		{
			AlarmDialogDataItem item = vecAlarmItems.get(i);
			if ((item != null) && (item.isSelected()))
				iCount++;
		}
		return iCount;
	}

	public AlarmDialogDataItem GetSelectedItem()
	{
		for (int i = 0; i < vecAlarmItems.size(); i++)
		{
			AlarmDialogDataItem item = vecAlarmItems.get(i);
			if ((item != null) && (item.isSelected()))
				return item;
		}
		return null;
	}

	public void UpdateBottomButtonsState()
	{
		int iSelectedCount = GetAlarmItemSelectedCount();
		boolean bEnabled = (iSelectedCount == 1);
		btnAlarmReminderClear.setEnabled(bEnabled);
		btnAlarmReminderSnooze.setEnabled(bEnabled);
		btnAlarmReminderOpen.setEnabled(bEnabled);
	}
	
	public void removeAlarmItem(AlarmDialogDataItem item)
	{
		vecAlarmItems.remove(item);
		SetListWithDataAdapter();
		SelectFirstListItem();
		UpdateHeaderInfo();
		UpdateBottomButtonsState();		
	}
	
	public AlarmDialogDataItem getSelectedAlarmItemToProcess()
	{		
		AlarmDialogDataItem item = GetSelectedItem();
		if (item != null)
		{
			return item; 
		}
		return null;
	}
	
	public boolean NoAlarmItems()
	{
		return (vecAlarmItems.size() == 0);
	}	
	
	private View.OnClickListener mBtnOnClick_CLEAR = new View.OnClickListener()
	{
		public void onClick(View arg0)
		{
			AlarmDialogDataItem item = getSelectedAlarmItemToProcess();
			if (item != null)
			{
				alarmsManager.putAlarmToProcess(item, AlarmsManager.iAction_CLEAR);
				removeAlarmItem(item);
			}
			CloseDialogIfEmpty();
		}
	};

	private View.OnClickListener mBtnOnClick_SNOOZE = new View.OnClickListener()
	{
		public void onClick(View arg0)
		{
			AlarmDialogDataItem item = getSelectedAlarmItemToProcess();
			if (item != null)
			{
				alarmsManager.putAlarmToProcess(item, AlarmsManager.iAction_SNOOZE);
				removeAlarmItem(item);
			}
			CloseDialogIfEmpty();
		}
	};
	
  private void CloseDialogIfEmpty()
  {
  	if (NoAlarmItems())
  	{
  		//post delay assignment
  		handlerCloseDialog.removeCallbacks(handlerUpdateDialogAssignment);
  		handlerCloseDialog.postDelayed(handlerUpdateDialogAssignment, 1000);
  	}
  }
  
  private Runnable handlerUpdateDialogAssignment = new Runnable()
  {
    public void run()
    {
   		AlarmDialog.this.finish();
    }
  };

	public void OpenDataForEdit(AlarmDialogDataItem item)
	{
  	bundleOtherDataStartup.clear();  	
  	if (item.getOrderFilter() == AlarmDataViewItem.iOrderAppts)
  		OpenActivity(0, "android.intent.action.Timetable.ACTION_MODE_EDIT_COURSE", item.getID());
  	if (item.getOrderFilter() == AlarmDataViewItem.iOrderAssignments)
  		OpenActivity(0, "android.intent.action.Timetable.ACTION_MODE_EDIT_ASSIGNMENT", item.getID());
	}
	
	private View.OnClickListener mBtnOnClick_OPEN = new View.OnClickListener()
	{
		public void onClick(View arg0)
		{
			AlarmDialogDataItem item = getSelectedAlarmItemToProcess();
			if (item != null)
			{
				OpenDataForEdit(item);
				AlarmDialog.this.finish();
			}												
		}		
	};

	public boolean UpdateReminderService(String sKey)
	{
    Bundle args = new Bundle();
    
    args.putBoolean(sKey, true);
    //put additional prefs
    args.putBoolean("b24HourMode", prefs.b24HourMode);
    args.putInt("iFirstDayOfWeek", prefs.iFirstDayOfWeek);
    
    //update service
    Intent intent = new Intent(this, AlarmService.class);
    intent.putExtras(args);
    ComponentName cpn = startService(intent);    
    return (cpn != null);		
	}

	@Override
	protected void restoreStateFromFreeze()
	{
		
	}
	
}


