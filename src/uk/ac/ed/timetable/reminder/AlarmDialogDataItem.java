
package uk.ac.ed.timetable.reminder;


import uk.ac.ed.timetable.R;
import android.content.Context;
import android.widget.*;


public class AlarmDialogDataItem
{
	
	//ListView item
	//---------------------------------------------------------
	public class ViewItem extends LinearLayout
	{
		//fields
		private AlarmDialogDataItem item;
	  private TextView mText;
	  private ImageView mImage;
	  
	  //methods
	  public ViewItem(Context context, AlarmDialogDataItem item, String sText)
	  {
	      super(context);
	      this.setOrientation(HORIZONTAL);
	      this.item = item;
	      //text view
	      mText = new TextView(context);
	      mText.setTextSize(20);
	      mText.setTextColor(0xFFBBBBBB);
	      mText.setText(sText);
	      //image view
	      mImage = new ImageView(context);
	      mImage.setImageResource(R.drawable.arrowright);
	      mImage.setPadding(2, 2, 2, 0);
	      //add view
	      addView(mImage, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
	      addView(mText, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
	      //set state
	      setAlarmItemSelected(false);
	  }
	  
	  private void setAlarmItemSelected(boolean bSelected)
	  {
	  	mImage.setVisibility((bSelected)?VISIBLE:INVISIBLE);
	    mText.setTextColor((bSelected)?0xFFFFFFFF:0xFFBBBBBB);        	    	
	  }
	  
	  public AlarmDialogDataItem getDataItem()
	  {
	  	return item;
	  }
	}
		
	
	//fields
	private boolean bSelected = false;
	private int iOrderFilter = -1;
	private long ID = -1;
	private String sText = null;
	private ViewItem listViewItem = null;
	private int iAction = 0;
	
	//methods
	AlarmDialogDataItem(Context context, int iOrderFilter, long ID, String sText)
	{
		this.iOrderFilter = iOrderFilter;
		this.ID = ID;
		this.sText = sText;
		this.listViewItem = new ViewItem(context, this, this.sText);
	}
	
	public boolean isSelected()
	{
		return bSelected;
	}

	public int getOrderFilter()
	{
		return iOrderFilter;
	}
	
	public long getID()
	{
		return ID;
	}
	
	public String getText()
	{
		return sText;
	}
	
	public void setSelected(boolean bSelected)
	{
		this.bSelected = bSelected;
		this.listViewItem.setAlarmItemSelected(bSelected);
	}
	
	public ViewItem getView()
	{
		return listViewItem;
	}
	
	public void setAction(int iAction)
	{
		this.iAction = iAction;
	}
	
	public int getAction()
	{
		return iAction;
	}
		
}
