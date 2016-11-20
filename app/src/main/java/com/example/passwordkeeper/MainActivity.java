package com.example.passwordkeeper;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.content.Intent;
import android.content.SharedPreferences;

public class MainActivity extends Activity implements View.OnClickListener {
	EditText passwordEntered;
	static String oldPass ;
	public static final String PREFS_NAME = "MyPasswordsFile";
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences passwords = getSharedPreferences(PREFS_NAME,0);
        oldPass = passwords.getString("password", "g");
        // returns g if the passowrd does not exist
    }
    
    public void onCLick(View v){}
    
    public void georgiB (View v){
    	Intent intent = new Intent(this, DisplayMessageActivity.class);
    	passwordEntered = (EditText) findViewById(R.id.inputPWD);
    	String password = passwordEntered.getText().toString();
    	intent.putExtra(EXTRA_MESSAGE, password);
    	
    	if (password.equals(oldPass))
    	{
        	startActivity(intent);
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
	}
    
}
