package com.example.passwordkeeper;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import static com.example.passwordkeeper.Constants.BUNDLE_EXTRA_PASSWORD;
import static com.example.passwordkeeper.Constants.PREFS_NAME;

public class LoginActivity extends Activity implements View.OnClickListener {
    EditText passwordEntered;
    static String oldPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences passwords = getSharedPreferences(PREFS_NAME, 0);
        oldPass = passwords.getString("password", "g");
        // returns g if the passowrd does not exist
    }

    public void onCLick(View v) {
    }

    public void login(View v) {
        Intent intent = new Intent(this, PasswordListActivity.class);
        passwordEntered = (EditText) findViewById(R.id.et_password);
        String password = passwordEntered.getText().toString();
        intent.putExtra(BUNDLE_EXTRA_PASSWORD, password);

        if (password.equals(oldPass)) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Invalid Password", Toast.LENGTH_SHORT).show();
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
