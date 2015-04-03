package com.neocogent.dicecheck;

import java.io.File;

import com.neocogent.dicecheck.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;
import android.widget.Toast;

public class RenameDialogFragment extends DialogFragment {
	
	String strFileName;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		final EditText filename = new EditText(getActivity());
		
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_save_dice_title)
        	   .setView(filename)
               .setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   String dicename = filename.getText().toString();
        			   File from = new File(String.format("%s/%s.dice", getActivity().getFilesDir().getPath(), strFileName));
        			   File to = new File(String.format("%s/%s.dice", getActivity().getFilesDir().getPath(), dicename));
        			   if(from.renameTo(to)) {
        				    ((MainActivity)getActivity()).UpdateAll();
        			   		Toast.makeText(getActivity(), getResources().getString(R.string.msg_dice_renamed), Toast.LENGTH_SHORT).show();
        			   }
                   }
               })
               .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   dialog.cancel();
                   }
               });
        
        return builder.create();
    }

	public void setFileName(String str) {
		strFileName = str;
	}
}
