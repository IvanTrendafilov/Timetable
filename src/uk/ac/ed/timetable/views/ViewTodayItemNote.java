
package uk.ac.ed.timetable.views;


import android.content.Context;
import android.graphics.Canvas;


public class ViewTodayItemNote extends ViewTodayItem
{
	//methods
	public ViewTodayItemNote(Context context)
	{
		super(context);
	}
	
	public void SetItemData(String sText)
	{
		SetText(sText);
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);		
		//DrawTestRect(canvas);

		final int iposX = ViewTodayItemHeader.GetTextPosX();
		int iposY = iMargin - (int)mpt.ascent();
		
		DrawItemText(canvas, iposX, iposY, getWidth() - iSpace, iColorTextActive_enabled);		
	}

}
