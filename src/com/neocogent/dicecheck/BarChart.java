package com.neocogent.dicecheck;

import java.util.ArrayList;

import com.neocogent.dicecheck.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class BarChart extends View {
	
	Paint pt;
    double scaleFactor;
    ArrayList<Integer> data;
    ArrayList<String> labels;
    String strTitle;
    
    static final int BADBAR = 0xFFCF0000;
    static final int GOODBAR = 0xFF0000CF;

    public BarChart(Context context) {
        super(context);
        initialise();
    }
    public BarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise();
    }
    public BarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialise();
    }
    void initialise() {
    	
    	DisplayMetrics metrics = getResources().getDisplayMetrics();
        scaleFactor = metrics.density;
        data = new  ArrayList<Integer>();
        labels = new ArrayList<String>();
        pt = new Paint();
        strTitle = "";
    }
    
    public void setData (ArrayList<Integer> iL) {
        data = iL;
        labels.clear();
        for(int n = 0; n < data.size(); n++)
        	labels.add(Integer.toString(n+1));
        invalidate();
    }
    public void setLabels (ArrayList<String> sL) {
        labels = sL;
        invalidate();
    }
    public void setTitle(String str){
    	strTitle = str;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if(data.size() < 2)
        	return;
        int fullWidth = getWidth();
        int fullHeight = getHeight();
        int txtSize = (int) (14*scaleFactor);  
        int BarHeight = fullHeight-txtSize*3-6;
        int BarWidth = (fullWidth-8)/data.size();
        int HalfBarWidth = BarWidth/2;
        int off = (fullWidth-8-data.size()*BarWidth)/2+4;
        int gap = 2;
        int MaxBar = 0;
        
        
        for(int n = 0; n < data.size(); n++)
        	if(data.get(n) > MaxBar)
        		MaxBar = data.get(n);
        
        pt.setColor(Color.GRAY);
    	pt.setTextAlign(Paint.Align.CENTER);
        pt.setTextSize(txtSize);
        canvas.drawText(strTitle, fullWidth/2, txtSize+2, pt);

        if(MaxBar == 0) {
            canvas.drawText(getResources().getString(R.string.nochartdata), fullWidth/2, fullHeight/2, pt);
            return;
        }
        pt.setTextSize(data.size() > 14 ? txtSize*3/4 : txtSize);
        for(int n = 0; n < data.size(); n++) {
        	pt.setColor(GOODBAR);
        	int barH = fullHeight-txtSize-2-BarHeight*data.get(n)/MaxBar;
        	canvas.drawRect(off+n*BarWidth, barH, off+(n+1)*BarWidth-gap , fullHeight-txtSize , pt);
        	pt.setColor(Color.GRAY);
        	if(data.get(n) > 0)
        		canvas.drawText(Integer.toString(data.get(n)), off+n*BarWidth+HalfBarWidth, barH-2, pt);
            canvas.drawText(labels.get(n), off+n*BarWidth+HalfBarWidth, fullHeight-3, pt);
        }
        
    }
    
}
