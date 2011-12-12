
package uk.ac.ed.timetable.views;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.graphics.*;
import android.graphics.drawable.Drawable;


public class ViewImgButton extends Button
{
	protected Drawable icon = null;
	private int iMarginX = 0;

	public ViewImgButton(Context context)
	{
		super(context);
	}

	@SuppressWarnings("all")
	public ViewImgButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		if (icon != null)
		{
			int iW = icon.getMinimumWidth();
			int iH = icon.getMinimumHeight();
			
			int iX = (getWidth() >> 1) - (iW >> 1) + iMarginX;
			int iY = ((getHeight() - getPaddingTop()) >> 1) - (iH >> 1);
							
			icon.setBounds(iX, iY, iX + iW, iY + iH);
			icon.draw(canvas);			
		}
	}
	
	public void SetButtonIcon(int iResId, int iMarginX)
	{
		icon = getResources().getDrawable(iResId);
		this.iMarginX = iMarginX;
	}

}
