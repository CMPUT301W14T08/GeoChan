package ca.ualberta.cmput301w14t08.geochan.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TextView;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.interfaces.UpdateDialogListenerInterface;

/**
 * Creates a simple dialog that is displayed when internet connection
 * has been regained after a period of no connectivity,
 * which prompts the user whether they would like to
 * refresh the current ThreadComment or list of ThreadComments.
 * 
 * @author Artem Herasymchuk
 *
 */
public class UpdateDialogFragment extends DialogFragment {
	
	public UpdateDialogFragment() {
		// Empty constructor required for DialogFragment
	}

	/**
	 * Creates the Update dialog.
	 * @param savedInstanceState  the saved instance state
	 * @return the dialog
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
		final ThreadViewFragment threadViewFragment = (ThreadViewFragment) fragmentManager.findFragmentByTag("thread_view_fragment");
		final ThreadListFragment threadListFragment = (ThreadListFragment) fragmentManager.findFragmentByTag("threadListFrag");
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
		alertDialogBuilder.setTitle(R.string.update_dialog_title);
		TextView textView = new TextView(getActivity());
		textView.setText(R.string.update_dialog_body);
		textView.setPadding(30, 30, 30, 30);
		textView.setTextSize(18);
		alertDialogBuilder.setView(textView);
		
		alertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				UpdateDialogListenerInterface listener = null;
				if (threadListFragment != null && threadListFragment.isVisible()) {
					listener = (UpdateDialogListenerInterface) threadListFragment;
				} else if (threadViewFragment != null && threadViewFragment.isVisible()) {
					listener = (UpdateDialogListenerInterface) threadViewFragment;
				}
				if (listener != null) {
					listener.reload();
				}
				dialog.dismiss();
			}
		});
		alertDialogBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		return alertDialogBuilder.create();
	}
	
}
