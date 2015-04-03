package com.neocogent.dicecheck;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import com.neocogent.dicecheck.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, OnSharedPreferenceChangeListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }
    
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        DiceApp app = (DiceApp)getApplication();
        app.setConfidence(Integer.valueOf(prefs.getString("prefConfidence", "1")));
        UpdateAll();
    }   
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        UpdateAll();
    }
    
    public void UpdateAll() {
    	RollFragment fg = (RollFragment)mSectionsPagerAdapter.getItem(0);
        if(fg != null)
        	fg.UpdateKeyPad();
        StatsFragment sfg = (StatsFragment)mSectionsPagerAdapter.getItem(1);
        if(sfg != null)
        	sfg.UpdateStats();      
        LogFragment lfg = (LogFragment)mSectionsPagerAdapter.getItem(2);
        if(lfg != null)
        	lfg.UpdateLog();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	DiceApp app = (DiceApp)getApplication();
        int itemId = item.getItemId();
		if (itemId == R.id.action_settings) {
			Intent iCfg = new Intent(this, SettingsActivity.class);
			startActivity(iCfg);
			return true;
		} else if (itemId == R.id.action_save) {
			try {
				UpdateAll();
				if(app.save())
					Toast.makeText(this, getResources().getString(R.string.msg_dice_saved), Toast.LENGTH_SHORT).show();
				else {
					SaveDialogFragment dlg = new SaveDialogFragment();
					dlg.show(getSupportFragmentManager(), "save");
				}
			} catch (IOException e) {
				Toast.makeText(this, getResources().getString(R.string.err_save_dice), Toast.LENGTH_LONG).show();
			}
			return true;
		} else if (itemId == R.id.action_reset) {
			app.resetRolls();
			UpdateAll();
			return true;
		} else if (itemId == R.id.action_help) {
			Intent iHtml = new Intent(this, HTMLActivity.class);
			startActivity(iHtml);
			return true;
		}
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

     public class SectionsPagerAdapter extends FragmentPagerAdapter {

    	Fragment[] frags = new Fragment[3];
    	
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
        	
        	if(frags[position] != null)
        		return frags[position];
        	switch(position) {
        		case 0: frags[0] = RollFragment.newInstance(); break;
        		case 1: frags[1] = StatsFragment.newInstance(); break;
        		case 2: frags[2] = LogFragment.newInstance(); break;
        	}
        	return frags[position];
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getResources().getStringArray(R.array.page_titles)[position].toUpperCase(Locale.getDefault());
        }
    }

    /**
     * Fragment that handles Roll page.
     */
    public static class RollFragment extends Fragment {

        public static RollFragment newInstance() {
            RollFragment fragment = new RollFragment();
            return fragment;
        }

        public RollFragment() {
        }
        
        private static View RollView;
        private static int rawClick;
        private static Vibrator vibe;
      
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        	RollView = inflater.inflate(R.layout.fragment_main, container, false);
            EditText mEdit = (EditText)RollView.findViewById(R.id.edit_sides);
            mEdit.setOnEditorActionListener(new OnEditorActionListener() {
            	@Override
            	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            	    if (actionId == EditorInfo.IME_ACTION_DONE) {
            	    	String str = v.getText().toString();
                    	if(!(str.length() != 0))
                    		return true;
                    	int sides = Integer.parseInt(str);
                    	if(sides < 2 || sides > 20)
                    		return true;
                    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    	prefs.edit().putInt("sidesLast", sides).commit();
            	    	}
            	    return false;
            	    }
            	});
            UpdateKeyPad();
            return RollView;
        }
                      
        public void UpdateKeyPad() {
        	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        	int sides = prefs.getInt("sidesLast", 6);
        	rawClick = getResources().getIdentifier(prefs.getString("prefSound", "click"), "raw", getActivity().getPackageName());
        	vibe = prefs.getBoolean("prefVibrate", false) ? (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE) : null;
        	((EditText)RollView.findViewById(R.id.edit_sides)).setText(Integer.toString(sides));
        	
        	DiceApp app = (DiceApp)getActivity().getApplication();
        	if(app.getSides() != sides) {
            	app.setSides(sides);
            	app.resetRolls();
        	}
       		((TextView)RollView.findViewById(R.id.dicename)).setText(app.getName());
        	int minRolls = Integer.valueOf(prefs.getString("prefMinRolls", "10"));
        	if(app.getMinRolls() != minRolls)
        		app.setMinRolls(minRolls);

        	ViewGroup keypad = (ViewGroup)RollView.findViewById(R.id.keypad);
        	keypad.removeAllViews();
        	
        	SetRolls(app, (TextView)RollView.findViewById(R.id.rolls));
        	int w = RollView.getContext().getResources().getDisplayMetrics().widthPixels-20;
        	int h = RollView.getContext().getResources().getDisplayMetrics().heightPixels-20;
    		int cols = 2;
    		while((h*2/3/(w/cols+5))*cols < sides) cols++;
    		int bs = w/cols-5;
    		for(int n = 0; n < sides; n++) {
    			Button btn = DieBtn(n);
    			btn.setId(n+1);
    			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(bs,bs);
    			if(n > 0) {
    				if(n % cols != 0)
    					params.addRule(RelativeLayout.RIGHT_OF, n);
    				params.addRule(RelativeLayout.BELOW, n-cols+1);          					
    			}
    			btn.setLayoutParams(params);
    			btn.setTag(Integer.valueOf(n));
    			btn.setOnClickListener(new Button.OnClickListener() {
    			    public void onClick(View v) {
    			    	if(rawClick != 0) {
    			    		MediaPlayer mp = MediaPlayer.create(getActivity().getApplicationContext(), rawClick);
    			    		mp.start();
    			    		mp.setOnCompletionListener(new OnCompletionListener() {
    			    	        public void onCompletion(MediaPlayer mp) {
    			    	        mp.release();
    			    	        }
    			    	    });
    			    	}
    			    	if(vibe != null)
    			    		vibe.vibrate(50);
    			        AlphaAnimation alphaDown = new AlphaAnimation(1.0f, 0.3f);
    			        AlphaAnimation alphaUp = new AlphaAnimation(0.3f, 1.0f);
    			        alphaDown.setDuration(100);
    			        alphaUp.setDuration(100);
    			        alphaDown.setFillAfter(true);
    			        alphaUp.setFillAfter(true);
    			        v.startAnimation(alphaUp);
    			        DiceApp app = (DiceApp)getActivity().getApplication();
    			    	app.addRoll(((Integer)v.getTag()).intValue());
    			        SetRolls(app, (TextView)RollView.findViewById(R.id.rolls));
    			    }
    			});
    			keypad.addView(btn);
    		}
        }
        
        void SetRolls(DiceApp app, TextView rolls) {
	    	String strRolls = Integer.toString(app.getRolls());
	    	int minRolls = app.getMinRolls()*app.getSides();	    	
	    	if(app.getRolls() < minRolls) {
	    		strRolls += String.format(" / %d", minRolls);
	    		rolls.setTextColor(0xFFCF0000);
	    	}
	    	else
	    		rolls.setTextColor(0xFF1FB800);
	    	rolls.setText(strRolls);        	
        }
        
        Button DieBtn(int n) {
        	int sides = ((DiceApp)getActivity().getApplication()).getSides();
        	Button btn = new Button(RollView.getContext());
        	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        	btn.setText(Integer.toString(n+1));
        	switch(sides) {
        		case 2: 
        			if(prefs.getBoolean("pref2sides", true)) {
        				btn.setText("");
        				try {
        					String path = getActivity().getExternalFilesDir(null).getPath() + ((n == 0) ? "/heads.png" : "/tails.png");
        					BitmapDrawable bm = new BitmapDrawable(RollView.getContext().getResources(), path);
        					if(bm.getBitmap() != null)
        						btn.setBackgroundDrawable(bm);
        					else
        						btn.setBackgroundResource(getActivity().getResources().getIdentifier((n == 0) ? "heads" : "tails", "drawable", getActivity().getPackageName()));
        				} catch (Exception e) {
        					btn.setBackgroundResource(getActivity().getResources().getIdentifier((n == 0) ? "heads" : "tails", "drawable", getActivity().getPackageName()));
        				}
        			}
        			break;
        		case 6: 
        			if(prefs.getBoolean("pref6sides", true)) {
        				btn.setText("");
        				btn.setBackgroundResource(getActivity().getResources().getIdentifier("die_"+(n+1), "drawable", getActivity().getPackageName()));
        			}
        			break;        			
        		case 16: 
        			if(prefs.getBoolean("pref16sides", true))
        				btn.setText(String.format("%X",n));
        			break;
        	}
        	return btn;
        }
    }
    
    /**
     * Fragment that handles Stats page.
     */
    public static class StatsFragment extends Fragment {
    	
    	double[] Probs = new double[]{ 0.1, 0.05, 0.01 }; 

        public static StatsFragment newInstance() {
        	StatsFragment fragment = new StatsFragment();
            return fragment;
        }

        public StatsFragment() {
        }
        
        @Override
        public void setMenuVisibility(final boolean visible) {
            super.setMenuVisibility(visible);
            if(visible) 
            	UpdateStats();
        }
        
        private static View StatsView;
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
           	StatsView = inflater.inflate(R.layout.fragment_stats, container, false);
            return StatsView;
        }
        
        public void UpdateStats() {
        	DiceApp app = (DiceApp)getActivity().getApplication();
        	int rolls = app.getRolls();
        	int sides = app.getSides();
        	double SSE = app.getSSE();
        	// normalize for sides, scale to percent reversed, adjust MaxSSE to 35% for red LED
        	double fairness = 100 - Math.min(100, SSE*sides/rolls/app.getMaxSSE()*65); 
        	((LEDMeter)StatsView.findViewById(R.id.fairMeter)).setValue((int)fairness, rolls >= app.getMinRolls()*sides);
        	BarChart freqBar = ((BarChart)StatsView.findViewById(R.id.freqBar));
        	BarChart consBar = ((BarChart)StatsView.findViewById(R.id.consBar));
        	freqBar.setData(app.getFreqData());
        	consBar.setData(app.getConsData());
        	
        	ViewGroup stats = (ViewGroup)StatsView.findViewById(R.id.stats);
        	stats.removeAllViews();

        	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        	if(rolls >= app.getMinRolls()*sides) {
        		app.setFairness((int)Math.round(fairness));
	        	int mode = Integer.valueOf(prefs.getString("prefConfidence", "1"));
	        	double [] values = new double[]{ rolls, sides, (double)rolls/sides, Probs[mode], SSE, SSE*sides/rolls, fairness };
	        	String[] formats = getResources().getStringArray(R.array.statFormats);
	        	int w = StatsView.getContext().getResources().getDisplayMetrics().widthPixels-40;
	        	int cols = w/110;
	        	for(int n = 0; n < formats.length; n++) {
	        		TextView v = new TextView(stats.getContext());
	        		v.setId(n+1);
	        		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w/cols-2,30);
	        		if(n > 0) {
	            		if(n % cols != 0)
	    					params.addRule(RelativeLayout.RIGHT_OF, n);
	    				params.addRule(RelativeLayout.BELOW, n-cols+1);
	        		}
	        		params.setMargins(2,7,2,2);
	        		v.setLayoutParams(params);
	        		v.setBackgroundColor(0xFFDDDDDD);
	        		v.setPadding(10, 2, 2, 5);
	        		v.setText(String.format(formats[n], values[n]));
	        		stats.addView(v);
	        	}
        	}
        	else
        		app.setFairness(0);
        	if(app.getSides() == 2 && prefs.getBoolean("pref2sides", true)) {
        		ArrayList<String> labels = new ArrayList<String>();
        		labels.add(getResources().getString(R.string.heads));
        		labels.add(getResources().getString(R.string.tails));
        		freqBar.setLabels(labels);
        		consBar.setLabels(labels);
        	}
        	if(app.getSides() == 16 && prefs.getBoolean("pref16sides", false)) {
        		ArrayList<String> labels = new ArrayList<String>();
        		for(int n = 0; n < 16; n++)
        			labels.add(String.format("%X",n));
        		freqBar.setLabels(labels);
        		consBar.setLabels(labels);
        	}
        	freqBar.setTitle(getResources().getString(R.string.rollfreq));
        	consBar.setTitle(getResources().getString(R.string.rollcons));
        }
    }    

    /**
     * Fragment that handles Log page.
     */
    public static class LogFragment extends Fragment {

        public static LogFragment newInstance() {
        	LogFragment fragment = new LogFragment();
            return fragment;
        }

        public LogFragment() {
        }
        
        private static ListView ListView;
        private static TextView MsgView;
        private static ArrayList<String> filelist = new ArrayList<String>();
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
           	View LogView = inflater.inflate(R.layout.fragment_log, container, false);
           	ListView = (ListView)LogView.findViewById(R.id.log);
           	MsgView = (TextView)LogView.findViewById(R.id.nodice);
            final LogArrayAdapter adapter = new LogArrayAdapter(LogView.getContext(), filelist);
            ListView.setAdapter(adapter);
            ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> lv, View v, int position, long id) {
                	String item = (String)lv.getItemAtPosition(position);
                	DiceApp app = (DiceApp)getActivity().getApplication();
                	if(item.length() != item.replaceAll("[/\n\r\t\0\f`?*\\<>|\"\':]", "").length()) {
                		Toast.makeText(getActivity(), getResources().getString(R.string.err_invalid_name), Toast.LENGTH_LONG).show();
                		return;
                	}
                	try {
						app.load(item);
					} catch (IOException e) {
						Toast.makeText(getActivity(), getResources().getString(R.string.err_load_dice), Toast.LENGTH_LONG).show();
						return;
					}
                	SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                	prefs.putInt("sidesLast", app.getSides());
                	prefs.putString("prefMinRolls", Integer.toString(app.getMinRolls()));
                	prefs.putString("prefConfidence", Integer.toString(app.getConfidence())).commit();
                	Toast.makeText(getActivity(), getResources().getString(R.string.msg_dice_loaded), Toast.LENGTH_SHORT).show();
                	((MainActivity)getActivity()).mViewPager.setCurrentItem(0);
                	((MainActivity)getActivity()).UpdateAll();
                }
            });
            ListView.setLongClickable(true);
            ListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> lv, View v, int position, long id) {
                	String item = (String)lv.getItemAtPosition(position);
                	ToolsDialogFragment dlg = new ToolsDialogFragment();
                	dlg.setFileName(item);
					dlg.show(getActivity().getSupportFragmentManager(), "tools");
                    return true;
                }
            });
           	UpdateLog();
            return LogView;
        }
        
        public void UpdateLog() {
            filelist.clear();
            File dir;
            try { 
            	dir = getActivity().getFilesDir();
            } catch (NullPointerException e) { return; }
        	if(dir != null) {
	        	String[] dicefiles = new File(dir.getPath()).list(new FilenameFilter() {
	        	    public boolean accept(File dir, String name) {
	        	        return(name.endsWith(".dice")); 
	        	    }
	        	});
	        	MsgView.setVisibility(dicefiles.length == 0 ? View.VISIBLE : View.GONE);
        		for(int i = 0; i < dicefiles.length; ++i)
        			filelist.add(dicefiles[i].substring(0, dicefiles[i].lastIndexOf(".")));
	            ((LogArrayAdapter)ListView.getAdapter()).notifyDataSetChanged();
        	}
        }
   
    public class LogArrayAdapter extends ArrayAdapter<String> {
    	private final Context context;
    	private final ArrayList<String> values;

    	public LogArrayAdapter(Context context, ArrayList<String> values) {
    	    super(context, R.layout.loglayout, values);
    	    this.context = context;
    	    this.values = values;
    	}

    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    	    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	    View rowView = inflater.inflate(R.layout.loglayout, parent, false);
    	    TextView filename = (TextView)rowView.findViewById(R.id.filename);
    	    TextView diceinfo = (TextView)rowView.findViewById(R.id.diceinfo);
    	    filename.setText(values.get(position));
    	    
    	    DiceApp app = (DiceApp)getActivity().getApplication();
    	    diceinfo.setText(app.info(values.get(position)));

    	    return rowView;
    	  }
    	} 
    }      
}


