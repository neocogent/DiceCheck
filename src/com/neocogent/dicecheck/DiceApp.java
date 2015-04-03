package com.neocogent.dicecheck;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Application;

import com.neocogent.dicecheck.R;
import com.google.gson.Gson;

public class DiceApp extends Application {

	private class DiceData {
	  int sides = 6;
	  int rolls = 0;
	  int last = 0;
	  int same = 0;
	  int[] freq = new int[20];
	  int[] cons = new int[20];
	  int confidence = 1;
	  int minrolls = 10;
	  int fairness = 0;
	}
		
	private static final double[][] X2p = new double[][]{
		{ 2.71, 4.60, 6.25, 7.78, 9.24, 10.64, 12.02, 13.36, 14.68, 15.99, 17.28, 18.55, 19.81, 21.06, 22.31, 23.54, 24.77, 25.99, 27.2 },
		{ 3.84, 5.99, 7.82, 9.49, 11.07, 12.59, 14.07, 15.51, 16.92, 18.31, 19.68, 21.03, 22.36, 23.69, 25.00, 26.30, 27.59, 28.87, 30.14 },
		{ 6.64, 9.21, 11.34, 13.28, 15.09, 16.81, 18.48, 20.09, 21.67, 23.21, 24.73, 26.22, 27.69, 29.14, 30.58, 32.00, 33.41, 34.81, 36.19 }
	}; 

	DiceData data;
	String strName;
	
	public DiceApp() {
		data = new DiceData();
		strName = new String("");
	}
	public String getName() {
		return strName;
	}
	public int getSides(){
	    return data.sides;
	}
	public void setSides(int n){
	    data.sides = n;
	}
	public void setMinRolls(int n){
	    data.minrolls = n;
	}
	public int getMinRolls(){
	    return data.minrolls;
	}
	public int getRolls(){
	    return data.rolls;
	}	
	public void addRoll(int roll){
	    data.freq[roll]++;
	    data.rolls++;
	    if(data.last == roll) {
	    	data.same++;
	    	if(data.same > data.cons[roll])
	    		data.cons[roll] = data.same;
	    	}
	    else
	    	data.same = 0;
	    data.last = roll;
	}
	public void resetRolls() {
	    for(int n = 0; n < 20; n++)
	    	data.freq[n] = data.cons[n] = 0;
	    data.rolls = data.same = data.last = 0;
	    strName = "";
	}
	public double getSSE() {
		double expfreq = (double)data.rolls/data.sides;
		double sse = 0.0;
		for(int n = 0; n < data.sides; n++) {
			double err = (data.freq[n]-expfreq);
			sse += err*err;
		}
		return sse;
	}
	public void setConfidence(int n) {
		data.confidence = n;
	}
	public int getConfidence() {
		return data.confidence;
	}
	public double getMaxSSE() {
		return X2p[data.confidence][data.sides-2];  
	}
	public void setFairness(int n) {
		data.fairness = n;
	}
	public ArrayList<Integer> getFreqData() {
		ArrayList<Integer> dataList = new ArrayList<Integer>();
		for(int n = 0; n < data.sides; n++)
			dataList.add(data.freq[n]);
		return dataList;
	}
	public ArrayList<Integer> getConsData() {
		ArrayList<Integer> dataList = new ArrayList<Integer>();
		for(int n = 0; n < data.sides; n++)
			dataList.add(data.cons[n] > 0 ? data.cons[n]+1 : 0);
		return dataList;
	}
	public boolean save() throws IOException {
		if(strName.length() == 0)
			return false;
		else
			save(strName);
		return true;
	}
	public void save(String dicename) throws IOException {
		if(strName.length() == 0)
			strName = dicename;
		Gson gson = new Gson();
		String json = gson.toJson(data);
		FileWriter writer = new FileWriter(String.format("%s/%s.dice", getFilesDir().getPath(), strName));
        writer.append(json);
        writer.flush();
        writer.close();
		
	}
	public void load(String dicename) throws IOException {
		strName = dicename;
		data = read(strName);
	}
	public boolean merge(String dicename) throws IOException {
		DiceData src = read(dicename);
		if(src.sides == data.sides) {
			data.rolls += src.rolls;
			for(int n = 0; n < data.sides; n++) {
				data.freq[n] += src.freq[n];
				data.cons[n] += src.cons[n];
			}
			return true;
		}
		return false;
	}
	public String info(String dicename) {
		DiceData chk;
		try {
			chk = read(dicename);
		} catch (IOException e) {
			return getResources().getString(R.string.err_read_dice);
		}
		if(chk.fairness > 0)
			return String.format(getResources().getString(R.string.dice_info_fmt1), chk.sides, chk.rolls, chk.fairness);
		else
			return String.format(getResources().getString(R.string.dice_info_fmt2), chk.sides, chk.rolls);
	}
	
	private DiceData read(String name) throws IOException {		
		BufferedReader bfr = new BufferedReader(new FileReader(String.format("%s/%s.dice", getFilesDir().getPath(), name)));
        StringBuilder sb = new StringBuilder();
        char[] buf = new char[2048];
        int n;
        while ((n = bfr.read(buf, 0, buf.length)) != -1)
            sb.append(buf, 0, n);
        bfr.close();
        String json = sb.toString();
        Gson gson = new Gson();
		return gson.fromJson(json, DiceData.class);
	}	
}

