package ca.ualberta.cmput301w14t08.geochan;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ThreadListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_thread_list);
	}

	@Override 
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.thread_list, menu);
		return true;
	}

}
