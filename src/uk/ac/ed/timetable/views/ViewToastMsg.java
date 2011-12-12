
package uk.ac.ed.timetable.views;


import uk.ac.ed.timetable.*;
import android.content.Context;
import android.widget.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;


public class ViewToastMsg extends LinearLayout
{
	//fields
	private static final int iMaxWidth = 260;
	private static final int iDefaultPadding = 6;
	private static final int iIconW = 19;
	private static final int iIconH = 19;
	private static final float fTextSize = 16;
	private Drawable icon = null;
	private RectF rect = new RectF();
	private Paint pt = new Paint(); 		
	private Handler handlerUpdateAnim = new Handler();
	private int iIconAlphaValue = 255;
	
	//fields	
	private LinearLayout layHeader = null;
	private LinearLayout layContent = null;
	private TextView textHeader = null;
	private TextView textContent = null;
	
	//methods
	public ViewToastMsg(Context context, String sHeaderText, String sContentText)
	{
		super(context);
		//get text size
		pt.setTextSize(fTextSize);
		final int iTextWidth = (int)pt.measureText(sContentText);		
		//set max text contener width
		int iMainWidth = android.view.ViewGroup.LayoutParams.FILL_PARENT;
		if ((iTextWidth + (iDefaultPadding + iDefaultPadding)) > getMaxWidth())						
			iMainWidth = getMaxWidth();
		//get icon
		icon = getResources().getDrawable(R.drawable.iconnotifyalarm);		
		//header
		layHeader = new LinearLayout(context);		
		layHeader.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		layHeader.setOrientation(LinearLayout.HORIZONTAL);
		//header text
		textHeader = new TextView(context);
		textHeader.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		textHeader.setText(sHeaderText);
		textHeader.setSingleLine();
		textHeader.setTextSize(fTextSize);		
		textHeader.setPadding(iIconW + 4, 0, 0, iDefaultPadding);
		layHeader.addView(textHeader);
		//content
		layContent = new LinearLayout(context);
		layContent.setLayoutParams(new LayoutParams(iMainWidth, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));	
		layContent.setOrientation(LinearLayout.HORIZONTAL);
		//content text
		textContent = new TextView(context);
		textContent.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		textContent.setText(sContentText);		
		textContent.setTextSize(fTextSize);
		textContent.setTextColor(0xFFFFFFFF);
		layContent.addView(textContent);
		//update layout
		this.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		this.setOrientation(LinearLayout.VERTICAL);
		this.setPadding(iDefaultPadding, iDefaultPadding, iDefaultPadding, iDefaultPadding);
		this.setBackgroundColor(0x00000000); //sets drawable surface !!!		
		this.addView(layHeader);
		this.addView(layContent);		
		//start anim
		handlerUpdateAnim.removeCallbacks(handlerUpdateAnimAssignment);
		handlerUpdateAnim.postDelayed(handlerUpdateAnimAssignment, 60);		
	}
	
  private Runnable handlerUpdateAnimAssignment = new Runnable()
  {
    public void run()
    {
    	try
    	{
    		iIconAlphaValue -= 20;
    		if (iIconAlphaValue < 0)
    			iIconAlphaValue = 255;
    		textHeader.invalidate();
    	} finally {
    		handlerUpdateAnim.postDelayed(this, 60);
    	}      
    }
  };

  private int getMaxWidth()
  {
  	return iMaxWidth;
  }
  
  @Override
	protected void onDraw(Canvas canvas)
  {
  	super.onDraw(canvas);

  	pt.setAntiAlias(true);
		rect.set(0, 0, this.getWidth(), this.getHeight());
	 
		//draw outer frame
		pt.setColor(0x99AACCAA);		
		canvas.drawRoundRect(rect, 4, 4, pt);
 
		//draw inner background
		pt.setColor(0x77000000);
		rect.inset(2, 2);
		canvas.drawRoundRect(rect, 3, 3, pt);
 
		final int iLineLeft = 2;
		final int iLineTop = textHeader.getBottom() + 3; 
		final int iLineWidth = getWidth() - (2 + 2);
 
		//draw base line
		LinearGradient lGrad = new LinearGradient((iLineWidth >> 2), 0, iLineWidth, 0,
				0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);
		pt.setShader(lGrad);		 		 		 
		canvas.drawLine(iLineLeft, iLineTop, iLineWidth, iLineTop, pt);
		pt.setShader(null);		 		 		 
 
		//draw icon
		final int iIconX = iDefaultPadding;
		final int iIconY = textHeader.getTop() + ((textHeader.getHeight() >> 1) - (iIconH >> 1)) + 2; 
		icon.setBounds(iIconX, iIconY, iIconX + iIconW, iIconY + iIconH);
		icon.setAlpha(iIconAlphaValue);
		icon.draw(canvas);
  }
	
}
