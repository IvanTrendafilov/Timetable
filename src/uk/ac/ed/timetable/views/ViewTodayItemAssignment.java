
package uk.ac.ed.timetable.views;


import android.content.Context;
import android.graphics.Canvas;


public class ViewTodayItemAssignment extends ViewTodayItem
{
	//fields
	private boolean bDone = false;
	private boolean bAlarm = false;

	//methods
	public ViewTodayItemAssignment(Context context)
	{
		super(context);
	}
	
	public void SetItemData(boolean bDone, String sText, boolean bAlarm)
	{
		this.bDone = bDone;
		this.bAlarm = bAlarm;
		SetText(sText);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);		
		//DrawTestRect(canvas);

		final int iposX = ViewTodayItemHeader.GetTextPosX();

		//draw icon done/undone
		int iIconX = iposX;
		int iIconY = (this.getHeight() >> 1) - (iIconH >> 1);

		if (bDone)
		{
			iconDone.setBounds(iIconX, iIconY, iIconX + iIconW, iIconY + iIconH);
			iconDone.draw(canvas);
		} else {
			iconUnDone.setBounds(iIconX, iIconY, iIconX + iIconW, iIconY + iIconH);
			iconUnDone.draw(canvas);
		}
		
		int iposY = iMargin - (int)mpt.ascent();

    int iTextClipWidth = getWidth() - iSpace;
		if (bAlarm)
			iTextClipWidth -= iIconW;
		
    final int iTextPosX = iposX + iIconW + iSpace;
    
    mpt.setStrikeThruText(bDone);
    
		DrawItemText(canvas, iTextPosX, iposY, iTextClipWidth, bDone?iColorTextActive_disabled:iColorTextActive_enabled);
		
		//draw icon alarm
		if (bAlarm)
		{
			iIconX = getWidth() - iIconW;
			iconAlarm.setBounds(iIconX, iIconY, iIconX + iIconW, iIconY + iIconH);
			iconAlarm.draw(canvas);
		}
	}
}
