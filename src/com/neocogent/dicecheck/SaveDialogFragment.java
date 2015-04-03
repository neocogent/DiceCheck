package com.neocogent.dicecheck;

import java.io.IOException;

import com.neocogent.dicecheck.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class SaveDialogFragment extends DialogFragment {
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		final EditText filename = new EditText(getActivity());
		filename.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
		filename.setOnEditorActionListener(new OnEditorActionListener() {
        	@Override
        	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        	    if (actionId == EditorInfo.IME_ACTION_DONE) {
        	    	String str = v.getText().toString();
                	if(!(str.length() != 0))
                		return true;
                	if(str.length() != str.replaceAll("[/\n\r\t\0\f`?*\\<>|\"\':]", "").length()) {
                		Toast.makeText(getActivity(), getResources().getString(R.string.err_invalid_name), Toast.LENGTH_LONG).show();
                		return true;
                		}
                	}
        	    return false;
        	    }
        	});
		
        final AlertDialog dlg = new AlertDialog.Builder(getActivity())
        	.setTitle(R.string.dialog_save_dice_title)
        	.setView(filename)
        	.setPositiveButton(R.string.dialog_save, null)
        	.setNegativeButton(R.string.dialog_cancel, null)
        	.create();
        dlg.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btn = dlg.getButton(AlertDialog.BUTTON_POSITIVE);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
	        			String str = filename.getText().toString();
	        			if(str.length() != str.replaceAll("[/\n\r\t\0\f`?*\\<>|\"\':]", "").length()) {
		           			Toast.makeText(getActivity(), getResources().getString(R.string.err_invalid_name), Toast.LENGTH_LONG).show();
		           			return;
		           			}
		        	    DiceApp app = (DiceApp)getActivity().getApplication();
		           		try {
		           			app.save(str);
		           		} catch (IOException e) {
		           			Toast.makeText(getActivity(), getResources().getString(R.string.err_save_dice), Toast.LENGTH_LONG).show();
		           			return;
		           		}
		           		((MainActivity)getActivity()).UpdateAll();
		           		Toast.makeText(getActivity(), getResources().getString(R.string.msg_dice_saved), Toast.LENGTH_SHORT).show();
		           		dlg.dismiss();
	                }
	            });
            }
		});
        
        return dlg;
    }

}
