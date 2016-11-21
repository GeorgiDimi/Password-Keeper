package com.example.passwordkeeper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class NoteEditActivity extends Activity {
    private EditText mTitleText;
    private EditText mUsernameText;
    private EditText mPasswordText;
    private EditText mExtraText;
    private Long mRowId;
    String title;
    String username;
    String password;
    String extra;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_message, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_edit2);
//	        setTitle(R.string.edit_note);
        mTitleText = (EditText) findViewById(R.id.et_title);
        mUsernameText = (EditText) findViewById(R.id.et_username);
        mPasswordText = (EditText) findViewById(R.id.et_password);
        mExtraText = (EditText) findViewById(R.id.et_extra);
        mRowId = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            title = extras.getString(PasswordAdapter.KEY_TITLE);
            username = extras.getString(PasswordAdapter.KEY_USERNAME);
            password = extras.getString(PasswordAdapter.KEY_PASSWORD);
            extra = extras.getString(PasswordAdapter.KEY_EXTRA);
            mRowId = extras.getLong(PasswordAdapter.KEY_ROWID);
            if (title != null) {
                mTitleText.setText(title);
            }
            if (username != null) {
                mUsernameText.setText(username);
            }
            if (password != null) {
                mPasswordText.setText(password);
            }
            if (extra != null) {
                mExtraText.setText(extra);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Bundle bundle = new Bundle();
        Intent mIntent = new Intent();
        bundle.putString(PasswordAdapter.KEY_TITLE, title);
        bundle.putString(PasswordAdapter.KEY_USERNAME, username);
        bundle.putString(PasswordAdapter.KEY_PASSWORD, password);
        bundle.putString(PasswordAdapter.KEY_EXTRA, extra);
        bundle.putString(PasswordAdapter.KEY_OLDTITLE, title);
        if (mRowId != null) {
            bundle.putLong(PasswordAdapter.KEY_ROWID, mRowId);
        }
        mIntent.putExtras(bundle);
        setResult(3, mIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.confirm:
                Bundle bundle = new Bundle();
                if (mTitleText.getText().toString().matches("")) {
                    Toast.makeText(this, "Please enter a valid title", Toast.LENGTH_LONG).show();
                }
                if (mTitleText.getText().toString().matches(" ") || mTitleText.getText().toString().matches("  ")) {
                    Toast.makeText(this, "Please enter a valid title", Toast.LENGTH_LONG).show();
                    return false;
                }
                bundle.putString(PasswordAdapter.KEY_TITLE, mTitleText.getText().toString());
                bundle.putString(PasswordAdapter.KEY_USERNAME, mUsernameText.getText().toString());
                bundle.putString(PasswordAdapter.KEY_PASSWORD, mPasswordText.getText().toString());
                bundle.putString(PasswordAdapter.KEY_EXTRA, mExtraText.getText().toString());
                bundle.putString(PasswordAdapter.KEY_OLDTITLE, title);
                if (mRowId != null) {
                    bundle.putLong(PasswordAdapter.KEY_ROWID, mRowId);
                }
                if (!(mTitleText.getText().toString().matches(""))) {
                    Intent mIntent = new Intent();
                    mIntent.putExtras(bundle);
                    setResult(RESULT_OK, mIntent);
                    finish();
                    return true;
                }
        }
        return true;
    }
}
