package com.neocogent.dicecheck;

import java.io.File;
import java.io.IOException;

import com.neocogent.dicecheck.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

public class ToolsDialogFragment extends DialogFragment {
	
	String strFileName;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_dice_tools_title)
               .setItems(R.array.dice_tools, new DialogInterface.OnClickListener() {
            	   public void onClick(DialogInterface dlg, int which) {
            		   switch(which) {
            		   case 0: // rename
            			    RenameDialogFragment dlgR = new RenameDialogFragment();
                       		dlgR.setFileName(strFileName);
                       		dlgR.show(getActivity().getSupportFragmentManager(), "rename");
            			   break;
            		   case 1: // merge
            			   DiceApp app = (DiceApp)getActivity().getApplication();
            			   try {
            				   Toast.makeText(getActivity(), getResources().getString(
                   					app.merge(strFileName) ? R.string.msg_dice_merged : R.string.msg_cannot_merge), Toast.LENGTH_SHORT).show();
                      		} catch (IOException e) {
                      			Toast.makeText(getActivity(), getResources().getString(R.string.err_merge_dice), Toast.LENGTH_LONG).show();
                      			return;
                      		}
            			   ((MainActivity)getActivity()).UpdateAll();
            			   ((MainActivity)getActivity()).mViewPager.setCurrentItem(1);
            			   break;
            		   case 2: // delete
            			   File file = new File(String.format("%s/%s.dice", getActivity().getFilesDir().getPath(), strFileName));
            			   if(file.delete()) {
            				   ((MainActivity)getActivity()).UpdateAll();
            				   Toast.makeText(getActivity(), getResources().getString(R.string.msg_dice_deleted), Toast.LENGTH_SHORT).show();
            			   }
            			   break;
            		   }
            	   }
               });
        
        return builder.create();
    }

	public void setFileName(String str) {
		strFileName = str;
	}
}
