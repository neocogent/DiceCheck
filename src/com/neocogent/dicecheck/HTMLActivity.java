package com.neocogent.dicecheck;

import com.neocogent.dicecheck.R;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class HTMLActivity extends Activity {
	
	private WebView htmlView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html);
        
        htmlView = (WebView)findViewById(R.id.htmlhelp);
		htmlView.getSettings().setJavaScriptEnabled(true);
        htmlView.setWebViewClient(new WebViewClient() {
        	
        	@Override
        	public boolean shouldOverrideUrlLoading(WebView view, String url) {
        	    if(url.contains("bitcoin://")) {
        	        String addr = url.substring("bitcoin://".length());
        	        ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        	        ClipData clip = ClipData.newPlainText("simple text", addr);
        	        clipboard.setPrimaryClip(clip);
        	        return true;
        	    }
        	    //handle the "normal" links...
        	    return super.shouldOverrideUrlLoading(view, url);
        	}
        });
        htmlView.loadUrl("file:///android_res/raw/help.html");
        
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if ((keyCode == KeyEvent.KEYCODE_BACK) && htmlView.canGoBack()) {
    		htmlView.goBack();
    		return true;
    	}
    	return super.onKeyDown(keyCode, event);
    }
  
}
