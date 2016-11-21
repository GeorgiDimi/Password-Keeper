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

import static com.example.passwordkeeper.Constants.BUNDLE_EXTRA_OLD_PASSWORD;
import static com.example.passwordkeeper.Constants.PREFS_NAME;

public class ChangePassActivity extends Activity {

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
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.change_pass, menu);
        return true;
    }

    public void changePass(View v) {
        String oldPass, newPass;
        EditText oldP, newP;
        SharedPreferences passwords = getSharedPreferences(PREFS_NAME, 0);
        String oldRPass = passwords.getString("password", "g");
        oldP = (EditText) findViewById(R.id.et_old_password);
        oldPass = oldP.getText().toString();
        newP = (EditText) findViewById(R.id.et_new_password);
        newPass = newP.getText().toString();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_EXTRA_OLD_PASSWORD, oldPass);
        Intent i = new Intent();
        i.putExtras(bundle);
        if (oldPass.equals(oldRPass) && !newPass.equals("")) {
            SharedPreferences.Editor editor = passwords.edit();
            editor.putString("password", newPass);
            editor.apply();
            Toast.makeText(this, "Password Changed", Toast.LENGTH_LONG).show();
            setResult(4, i);
            finish();
        } else {
            Toast.makeText(this, "Invalid old Password", Toast.LENGTH_LONG).show();
            setResult(5, i);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent mIntent = new Intent();
        setResult(5, mIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
