
package uk.ac.ed.widgets;


import android.content.Context;
import android.widget.EditText;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.graphics.*;


public class HintEdit extends EditText
{
	//fields
	private static final int iColorLightFrame = 0x88888888;
	private static final int iColorLightBkg = 0xccffffff;
	private static final int iColorLightText = 0xff000000;

	//fields
	private static final int iColorDarkFrame = 0x886688aa;
	private static final int iColorDarkBkg = 0xcc6688aa;
	private static final int iColorDarkText = 0xffffffff;
	
	//fields
  private final static Typeface tfMono = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);
	private static final int iMarginVert = 6;
	private static final int iMarginVertBottom = 8;
	private static final int iPaddingHorz = 26;
	
	//fields
	private Path pthTriangle = new Path();
	private Path pthTriangleBkg = new Path();
	private Paint pt = new Paint();
	private RectF rect = new RectF();
	private RectF rectBkg = new RectF();
	private boolean bHint = false;
	private boolean bHintSmall = false;
	private boolean bLight = false;	
	private String sHintText = "";

	//fields
	private float fTextSize = 32; 
	private float fTextSizeSmall = 14; 
	private int iSymbolWidth = 0;
	private int iSymbolWidthSmall = 0;
	private int iTextHeight = 0; 
	private int iTextHeightSmall = 0; 	
	private int iTextAscent = 0;
	private int iTextAscentSmall = 0;
	
	//fields
	private int iHintWidth = 0;
	private int iHintHeight = 0;

	//fields
	private int iHintWidthSmall = 0;
	private int iHintHeightSmall = 0;

	//methods
	public HintEdit(Context context)
	{
		super(context);
		initialize();
	}

	@SuppressWarnings("all")
	public HintEdit(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initialize();
	}

	private void initialize()
	{		
		pt.setUnderlineText(false);
		pt.setFakeBoldText(true);
		pt.setTypeface(tfMono);
		pt.setTextScaleX(1.3F);
		
		pt.setTextSize(fTextSize);		
		iSymbolWidth = (int)pt.measureText("W");
		iTextHeight = (int)(-pt.ascent() + pt.descent());		
		iTextAscent = (int)pt.ascent();		
		iHintHeight =  iMarginVert + iTextHeight + iMarginVert;
				
		pt.setTextSize(fTextSizeSmall);
		iSymbolWidthSmall = (int)pt.measureText("W");
		iTextHeightSmall = (int)(-pt.ascent() + pt.descent());
		iTextAscentSmall = (int)pt.ascent();		
		iHintHeightSmall =  iMarginVert + iTextHeightSmall + iMarginVert;
		
		//set triangle
		final int iTriEdge = 10;
		final int iTriHeight = 12;
		
		//frame
		pthTriangle.moveTo(0, 0);
		pthTriangle.lineTo(iTriEdge, 0);
		pthTriangle.lineTo(0, iTriHeight);
		pthTriangle.lineTo(-iTriEdge, 0);
		pthTriangle.lineTo(0, 0);
		pthTriangle.close();
		
		//bkg
		pthTriangleBkg.moveTo(0, -2);
		pthTriangleBkg.lineTo(iTriEdge - 1, -2);
		pthTriangleBkg.lineTo(0, iTriHeight - 3);
		pthTriangleBkg.lineTo(-iTriEdge + 1, -2);
		pthTriangleBkg.lineTo(0, -2);
		pthTriangleBkg.close();		
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		if (bHint)
		{
			final int iOffsetY = this.getScrollY();
		
			pt.setUnderlineText(false);
			pt.setFakeBoldText(true);
			pt.setTypeface(tfMono);
			pt.setTextScaleX(1.3F);
			pt.setAntiAlias(true);			
			
			//init pos and sizes			
			int iTextWidth = 0;			
			int iPosX = 0;
			int iPosY = 0;
			
			if (bHintSmall)
			{
				iTextWidth = iSymbolWidthSmall * sHintText.length();
				iHintWidthSmall = iPaddingHorz + iTextWidth + iPaddingHorz;				
				iPosX = (this.getWidth() >> 1) - (iHintWidthSmall >> 1);
				iPosY = (this.getHeight() >> 1) - (iHintHeightSmall >> 1) + iOffsetY;
				rect.set(iPosX, iPosY, iPosX + iHintWidthSmall, iPosY + iHintHeightSmall);
			} else {
				iTextWidth = iSymbolWidth;
				iHintWidth = iPaddingHorz + iTextWidth + iPaddingHorz;				
				iPosX = (this.getWidth() >> 1) - (iHintWidth >> 1);
				iPosY = (this.getHeight() >> 1) - (iHintHeight >> 1) + iOffsetY;
				rect.set(iPosX, iPosY, iPosX + iHintWidth, iPosY + iHintHeight);
				
				//add bottom margin to hint 
				rect.offset(0, - iMarginVertBottom);
			}
						
			rectBkg.set(rect);
			rectBkg.inset(2, 2);			
		
			//draw hint
			pt.setColor(bLight?iColorLightFrame:iColorDarkFrame);
			canvas.drawRoundRect(rect, 4, 4, pt);

			pt.setColor(bLight?iColorLightBkg:iColorDarkBkg);
			canvas.drawRoundRect(rectBkg, 3, 3, pt);
			
			//draw bottom triangle
			canvas.save();
			canvas.translate(rect.centerX(), rect.bottom);

			pt.setColor(bLight?iColorLightFrame:iColorDarkFrame);
			canvas.drawPath(pthTriangle, pt);
			
			pt.setColor(bLight?iColorLightBkg:iColorDarkBkg);
			canvas.drawPath(pthTriangleBkg, pt);
			
			canvas.restore();
			
			//draw text
			final int iTextX = (int)rect.centerX() - (iTextWidth >> 1);			
			final int iTextY = (int)rect.centerY() - ((bHintSmall?iTextHeightSmall:iTextHeight) >> 1) - (bHintSmall?iTextAscentSmall:iTextAscent);			
			pt.setColor(bLight?iColorLightText:iColorDarkText);
			pt.setTextSize(bHintSmall?fTextSizeSmall:fTextSize);
			canvas.drawText(sHintText, iTextX, iTextY + 1, pt);
		}
	}
	
	public void showSymbolHint(String sText, boolean bLight)
	{
		bHint = true;
		this.bHintSmall = (sText.length() > 1);
		this.sHintText = sText;
		this.bLight = bLight;
		invalidate();
	}
	
	public void hideSymbolHint()
	{
		bHint = false;
		invalidate();		
	}
	
}

