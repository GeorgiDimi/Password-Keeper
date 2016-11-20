package com.example.passwordkeeper;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class ChangePassActivity extends Activity {
	public static final String PREFS_NAME = "MyPasswordsFile";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_pass);
		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.change_pass, menu);
		return true;
	}
	public void changePass(View v){
			String oldPass, newPass;
			EditText oldP,newP;
			SharedPreferences passwords = getSharedPreferences(PREFS_NAME,0);
	        String oldRPass = passwords.getString("password", "g");
			oldP = (EditText) findViewById(R.id.oldPassfield);
	    	oldPass = oldP.getText().toString();
	    	newP= (EditText) findViewById(R.id.newPassfield);
	    	newPass = newP.getText().toString();
	    	Bundle bundle = new Bundle();
	    	bundle.putString("OLD_PASS",oldPass);
	    	Intent i = new Intent();
	    	i.putExtras(bundle);
	    	if (oldPass.equals(oldRPass)&&!newPass.equals("")){
	        	SharedPreferences.Editor editor = passwords.edit();
	        	editor.putString("password",newPass);
	        	editor.commit();
	        	Toast.makeText(this, "Password Changed" , Toast.LENGTH_LONG).show();
	        	setResult(4,i);
	        	finish();
	    	}
	    	else{
	    		Toast.makeText(this, "Invalid old Password" , Toast.LENGTH_LONG).show();
	    		setResult(5,i);
		    	finish();
	    	}
	}
	@Override public void onBackPressed(){
        Intent mIntent = new Intent();
        setResult(5, mIntent);
        finish();
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
