package com.neocogent.dicecheck;

import com.neocogent.dicecheck.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class LEDMeter extends View {
	
	Paint pt;
    double scaleFactor;
    int Leds = 20;
    int gLeds = 8;
    int yLeds = 5;
    int rLeds = 7;
    int value;    
    boolean enabled = false;
    
    static final int REDLED = 0xFFCF0000;
    static final int GREENLED = 0xFF1FB800;
    static final int YELLOWLED = 0xFFE3E300;

    public LEDMeter(Context context) {
        super(context);
        initialise();
    }
    public LEDMeter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise();
    }
    public LEDMeter(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialise();
    }
    void initialise() {
    	
    	DisplayMetrics metrics = getResources().getDisplayMetrics();
        scaleFactor = metrics.density;
        pt = new Paint();        
    }
    
    public void setValue (int v, boolean e) {
        value = v/5;
        enabled = e;
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int fullWidth = getWidth();
        int fullHeight = getHeight();
        int padding = (int) (5*scaleFactor);
        int txtSize = (int) (14*scaleFactor); 
        int LedHeight = fullHeight-txtSize-2;
        int LedWidth = fullWidth/Leds;
        int off = (fullWidth-Leds*LedWidth)/2;
        int gap = 2;
        int level = enabled ? value : 0;
        
        for(int n = 0; n < Leds; n++) {
        	pt.setColor(n < level ? (n < rLeds ? REDLED : (n < (rLeds+yLeds) ? YELLOWLED : GREENLED)) : Color.LTGRAY);
        	canvas.drawRect(off+n*LedWidth, 0, off+(n+1)*LedWidth-gap , LedHeight , pt);
        }
        if(!enabled) {
        	pt.setColor(REDLED);
        	pt.setTextAlign(Paint.Align.CENTER);
        	pt.setTextSkewX((float)-0.25);
            canvas.drawText(getResources().getString(R.string.disabled), fullWidth/2, (LedHeight+txtSize)/2, pt);
            pt.setTextSkewX((float)0);
        }
        pt.setTextSize(txtSize);
        pt.setTextAlign(Paint.Align.LEFT);
        pt.setColor(REDLED);
        canvas.drawText(getResources().getString(R.string.biased), padding, fullHeight, pt);
        pt.setTextAlign(Paint.Align.RIGHT);
        pt.setColor(GREENLED);
        canvas.drawText(getResources().getString(R.string.fair), fullWidth-padding, fullHeight, pt);
    }

}
