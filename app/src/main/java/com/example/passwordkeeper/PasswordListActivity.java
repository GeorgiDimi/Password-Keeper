package com.example.passwordkeeper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;

import com.example.myapplication.R;

import static com.example.passwordkeeper.Constants.BUNDLE_EXTRA_PASSWORD;

public class PasswordListActivity extends Activity implements OnItemClickListener {
    private PasswordAdapter mDbHelper = new PasswordAdapter(this);
    ListView lv;
    ArrayAdapter<String> arrayAdapter;
    Cursor c;
    private static final int DELETE_ID = 5;
    private static final int CHANGE_ID = 6;
    private static final int DELETE_ID2 = Menu.FIRST + 1;
    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;
    private static final int ACTIVITY_EXIT = 3;
    private static final int ACTIVITY_CHANGE = 4;
    private static final int ACTIVITY_FAILED = 5;
    private static final int BACKUP_ID = Menu.FIRST + 1;
    private static final int READ_ID = 7;
    private Context context;

//    private Parcelable mListState = null;

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
//        mListState = state.getParcelable(LIST_STATE);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        fillData();        
//        if (mListState != null)
//            getListView().onRestoreInstanceState(mListState);
//        mListState = null;
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle state) {
//        super.onSaveInstanceState(state);
//        mListState = getListView().onSaveInstanceState();
//        state.putParcelable(LIST_STATE, mListState);
//    }

    public static Intent getIntent(final Context context, final String password) {
        Intent intent = new Intent(context, PasswordListActivity.class);
        intent.putExtra(BUNDLE_EXTRA_PASSWORD, password);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_list);
        mDbHelper.open();
        fillData();
        context = getApplicationContext();
        registerForContextMenu(findViewById(R.id.lv_passwords));
        // Show the Up button in the action bar.
        setupActionBar();
//		Intent intent = getIntent();
//		password = intent.getStringExtra(LoginActivity.EXTRA_PASSWORD);
        lv = (ListView) findViewById(R.id.lv_passwords);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long id) {
                begin(position, id);
            }
        });
    }

    private void hideKeyobard() {
        View view = this.getCurrentFocus();

        if (view == null) {
            return;
        }

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputMethodManager == null) {
            return;
        }

        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
    
    @Override
    protected void onResume() {
        super.onResume();

        hideKeyobard();
    }

    public void begin(int position, long id) {
        Intent i2 = new Intent(this, NoteEditActivity.class);
        Cursor c2 = c;
        c2.moveToPosition(position);
        i2.putExtra(PasswordAdapter.KEY_ROWID, id);
        i2.putExtra(PasswordAdapter.KEY_TITLE, c2.getString(
                c2.getColumnIndexOrThrow(PasswordAdapter.KEY_TITLE)));
        i2.putExtra(PasswordAdapter.KEY_USERNAME, c2.getString(
                c2.getColumnIndexOrThrow(PasswordAdapter.KEY_USERNAME)));
        i2.putExtra(PasswordAdapter.KEY_PASSWORD, c2.getString(
                c2.getColumnIndexOrThrow(PasswordAdapter.KEY_PASSWORD)));
        i2.putExtra(PasswordAdapter.KEY_EXTRA, c2.getString(
                c2.getColumnIndexOrThrow(PasswordAdapter.KEY_EXTRA)));
        startActivityForResult(i2, ACTIVITY_EDIT);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID2, 0, R.string.delete);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    // to add items when you press the menu key beside the home button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        boolean result = super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.display_message, menu);
        menu.add(0, DELETE_ID, 0, R.string.delete);
        menu.add(0, CHANGE_ID, 0, R.string.change);
        menu.add(0, BACKUP_ID, 0, R.string.backup);
        menu.add(0, READ_ID, 0, R.string.read);
        return result;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID2:
                String title = "blaaa";
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                Cursor c3 = c;
                c3.moveToPosition(info.position);
                title = c3.getString(
                        c3.getColumnIndexOrThrow(PasswordAdapter.KEY_TITLE));
                mDbHelper.deleteNote(title);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    // when you click my message at the top it uses this method to go somewhere
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this); // TOOD fix this
                return true;
            case R.id.item1:
                createNote();
                return true;
            //this is to "delete all" messages option which is disabled
            case DELETE_ID:
                mDbHelper.deleteALL();
                fillData();
                return true;
            case BACKUP_ID:
                backup();
                return true;
            case READ_ID:
                read();
                return true;
            case CHANGE_ID:
                Intent intent3 = new Intent(this, ChangePassActivity.class);
                startActivityForResult(intent3, ACTIVITY_CHANGE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
//        NavUtils.navigateUpFromSameTask(this); // TODO fix this
    }

    public void read() //reads from a txt file
    {
        int gg = 1;
        //External storage read
        File newF = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "PasswordKeeper.txt");
        StringBuilder text = new StringBuilder();
        StringBuilder word = new StringBuilder();
        StringBuilder extraw = new StringBuilder();
//		StringBuilder tempT = new StringBuilder();
        StringBuilder tempU = new StringBuilder();
        StringBuilder tempP = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(newF));
            int column = 1, index = 0, listL = 0, length;
            String freshString, lastString, line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                freshString = text.toString();
                length = freshString.length();
                if (length > 0 && freshString.charAt(0) == '|') {
                    listL++;
                }
                text = new StringBuilder();
            }
            br.close();
            br = new BufferedReader(new FileReader(newF));
            text = new StringBuilder();
            Toast.makeText(context, "number of records " + listL, Toast.LENGTH_SHORT).show();
//		    String[] title = new String[listL+1], username = new String[listL+1], password = new String[listL+1], extra = new String[listL+1];
            String[] title = new String[listL], username = new String[listL], password = new String[listL], extra = new String[listL];

            while ((line = br.readLine()) != null) {
                int s = 0, f = 0;
                text.append(line); // appends the line to "text" which is a string builder empty variable
                freshString = text.toString(); //
                length = freshString.length(); // gets the length of the string
                if (length > 0 && freshString.charAt(0) == '|') // means a new entry is found (they are separated by the | char)
                {
                    column = 1;
                    index++;
                    gg = 1;
                    extraw = new StringBuilder();
                }
                if (length > 0 && freshString.charAt(0) != '|')//&&freshString.charAt(0)!='\n')
                {
                    while (s < length && length != 0) {
                        while ((f + 1) < length && freshString.charAt(f + 1) != '\t') {
                            f++;
//		        			Log.v("incrementing f+1",freshString.charAt(f)+".");
                        }
                        for (; s <= f && s < length; s++) {
                            word.append(freshString.charAt(s));
                        }
                        if (column == 1) {
                            title[index] = word.toString();
//		        			Toast.makeText(context, "1  " + title[index], Toast.LENGTH_SHORT).show();
                            tempU = new StringBuilder();
                        } else if (column == 2) {
                            tempU.append(word);
                            tempU.append(' ');
                            username[index] = tempU.toString();
//		        			username[index] = word.toString();
//		        			Toast.makeText(context, "2  " +  username[index], Toast.LENGTH_SHORT).show();
                            tempP = new StringBuilder();
                        } else if (column == 3) {
                            tempP.append(word);
                            tempP.append(' ');
                            password[index] = tempP.toString();
//		        			password[index] = word.toString();
//		        			Toast.makeText(context, "3  " + password[index], Toast.LENGTH_SHORT).show();
                        } else if (column == 4) {
                            if (gg == 1) {
                                extraw = word;
//		        				extra[index] = word.toString();
                                gg++;
                            } else if (gg > 1) {
                                extraw.append('\n');
                                extraw.append(word);
//		        				extra[index] += word.toString();
                            }
                            extra[index] = extraw.toString();
//		        			Toast.makeText(context, "4  " + extra[index], Toast.LENGTH_SHORT).show();
                        }
                        if (column < 4 && f + 2 < length && freshString.charAt(f + 1) == '\t')
                            column++;
                        if (f + 2 < length) {
                            s = f + 2;
                            f = s;
                            Log.v("s is " + s, "length is " + length);
                        } else if (f + 2 == length) {
                            s++;
                            s++;
                        } else if (f + 1 == length)
                            s++;
                        word = new StringBuilder(); // causing allocation error!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    }
                }
                lastString = freshString;
                text = new StringBuilder(); // clears the sb
//		        text.append('\n'); // added to the end
            }
            br.close();
            Toast.makeText(context, "done processing", Toast.LENGTH_SHORT).show();
//		    Toast.makeText(context, "4  " + title[45], Toast.LENGTH_SHORT).show();
//		    Toast.makeText(context, "5  " + username[45], Toast.LENGTH_SHORT).show();
//		    Toast.makeText(context, "6  " + password[45], Toast.LENGTH_SHORT).show();
//		    Toast.makeText(context, "7  " + extra[45], Toast.LENGTH_SHORT).show();
//		    Log.v("the extra is " , extra[44]);
//		    String readString = text.toString();
//		    Toast.makeText(context, readString, Toast.LENGTH_LONG).show();
            mDbHelper.createALLNote(title, username, password, extra);
        } catch (IOException e) {
        }

        //Internal storage read
//		FileInputStream fis = null;
//		try {
//			fis = context.openFileInput("PasswordKeeper.txt");
//			InputStreamReader isr = new InputStreamReader(fis);
//			StringBuilder sb = new StringBuilder();
//			char[] inputBuffer = new char[2048];
//			int l;
//			while((l=isr.read(inputBuffer))!=-1){
//				sb.append(inputBuffer,0,l);
//			}
//			String readString = sb.toString();
//			fis.close();
//			Toast.makeText(context, readString, Toast.LENGTH_LONG).show();
//		}
//		catch (Exception e) {}
//		finally {
//			if (fis!=null)
//				fis=null;
//		}
//		if (deleteFile("PasswordKeeper.txt")==false)
//		{
//			Log.v("s","s");
//		}
//		deleteFile("my-file-name.txt");		
    }

    public void backup() {
        // this saves to a downloads directory
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "PasswordKeeper.txt");

        //this saves to a private directory that is NOT accesible by the user or by other apps
//		String path = context.getFilesDir().getAbsolutePath();
//		File file = new File(path + "/my-file-name.txt");

        FileOutputStream stream;
        try {
            stream = new FileOutputStream(file);
            c = mDbHelper.fetchAllNotes();
            startManagingCursor(c);
            if (c.moveToFirst()) {
                do {
                    stream.write((c.getString(1)).getBytes());
                    stream.write('\t');
                    stream.write((c.getString(2)).getBytes());
                    stream.write('\t');
                    stream.write((c.getString(3)).getBytes());
                    stream.write('\t');
                    if (c.getString(4) == null)
                        stream.write(' ');
                    else
                        stream.write((c.getString(4)).getBytes());
                    stream.write('\n');
                    stream.write("|\n".getBytes()); // denotes new entry
                } while (c.moveToNext());
            }
            stream.close();
        } catch (Exception e) {
        }
    }

    public void createNote() {
        Intent intent2 = new Intent(this, NoteEditActivity.class);
        startActivityForResult(intent2, ACTIVITY_CREATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Bundle extras = intent.getExtras();
        switch (requestCode) {
            case ACTIVITY_CREATE:
                String title = extras.getString(PasswordAdapter.KEY_TITLE);
                String username = extras.getString(PasswordAdapter.KEY_USERNAME);
                String password = extras.getString(PasswordAdapter.KEY_PASSWORD);
                String extra = extras.getString(PasswordAdapter.KEY_EXTRA);
                if (title != null)
                    mDbHelper.createNote(title, username, password, extra);
                if (!PasswordAdapter.dbStatus)
                    Toast.makeText(this, "Duplicate Entry", Toast.LENGTH_LONG).show();
//	                String[] title2 = {"apply2jobs.com","bell"};
//	                String[] title2 = {"apply2jobs.com","bell","bla","bold app world","bombardier","cgi","city of toronto","dead trigger","deloitte.com","dropbox","facebook","glo-bus","gmail","gmail2","gradience","great west life","hotmail","husky engergy","ibm","indeed","instagram","itunes","job logictics.com","jobvite.com","knight online","koodo","linked in","locks","LOL","lords and knights","microsoft dreamspark","monster.ca","msn","ontario colleges","osap","ouac","placepro","raidcall","robert Half","RS","ryerson","samsung","sick kids","sjobs.brassring","sjobs.brassring.com","skype","steam","stumbleUpon","sunlife Financial","telus","telus jobs","timeplay","Twitter1","Twitter2","Ultipro.com","Waterloo Quest","Web Assign","Wisdomjobs.ca","WoW","Yahoo","Youtube"};
//	                String[] username2 = {"georgi12dimitrov@gmail.com","b1mjba69","3076 m","georgibba6","geo222","georgi12dimitrov@gmail.com","georgi12dimitrov@gmail.com","bba647","georgi12dimitrov@gmail.com","georgi12dimitrov@gmail.com","georgi_rules@hotmail.com","georgi.dimitrov@ryerson.ca","georgi12dimitrov@gmail.com","georgi.d12@gmail.com","georgid123"," ","georgi_rules@hotmail.com","georgi12dimitrov@gmail.com","georgi12dimitrov@gmail.com","georgi12dimitrov@gmail.com","georgidimi","georgi_rules@hotmail.com","geo222","georgi12dimitrov@gmail.com","georgi4","dimitrova_mariana@yahoo.ca","georgi12dimitrov@gmail.com","42 7 17 black","assasin02","terrorsquack@yahoo.ca","georgi12dimitrov@gmail.com","georgi12dimitrov@gmail.com","georgi_rule@hotmail.com","georgi12dimitrov@gmail.com","osap access number: 812 497 806","2011 041 568","gdimitrov13","marack22","georgi12dimitrov@gmail.com","assasin g 93","500443549","georgi_rules@hotmail.com","georgi12dimitrov@gmail.com","g12d","georgi12dimitrov@gmail.com","georgi.dimitrov647","noob_killer_for_life","georgirules","georgi12dimitrov@gmail.com","diljan_dimitrov@yahoo.ca","georgi12dimitrov@gmail.com","georgi.d12@gmail.com","georgidimi","bulgarianbba","georgi12dimitrov@gmail.com","gdimitro","georgi.dimitrov","georgi12dimitrov@gmail.com","georgi_rules@hotmail.com","terrorsquack@yahoo.ca","mstruegamer"};
//	                
//	                String[] password2 = {"georgi123!","J3p8q973 / Ae362042","7630 d","258456456","georgi123","georgi222","258456g","12345fuck","georgi123","georgi12","po258654po!","258456g","258456ggg654","258456g6g5g4","258456g654"," ","123654963g","georgi123","georgi456","georgi123","gh258654gh","258456456g","georgi222","georgi123","gm3ft90lpqr","Dimitrova6","258georgi","42 32 13 blue","258456456g","258456g","258!456","258georgi","123654963g","19931021g","258456g654H","L9GR","258654gk","654321jb","georgi123","pantelis5","258456gG","georgi258g456g","georgi123","georgi123","georgi222","258456g654","148368789","258456","georgi123","19931021g","georgi123","1234512345g","258g456g","258456g","georgi123","BBA258g!","258456g","georgi222","258456456g","1234512345g","258456g6g5g4"};
//	                
//	                String[] extra2 = {"snakes, sales, bart","gm3ft9ol123 \nmarianadimitrova 19931021","3176 g","georgi_rules@hotmail.com"," "," ","brainhunter.com \n bba"," "," "," "," "," ","georgi_rules@hotmail.com\n647 217 8845\nbart","terrorsquack@yahoo.ca\nrocen"," ","110574530000000032101 \ndilyan dimitrov"," "," ","ford\nsofia\naltera jobs \ngeorgi12dimitrov@gmail.com\ncineplex\ngeorgi12dimitrov@gmail.com"," ","georgi_rules@hotmail.com"," "," ","georgi12dimitrov@gmail.com","pin: 19931021","","","","georgi_rules@gmail.com"," "," "," "," ","accoung#: 2100 1385 8314 \napplication #: 11 080 4067","maria\naudi\nnis: 542 404 587\napplication reference: 1123 75560"," ","rustud","horhei","beta\ntoronto\nbba","assasingr06\nterrirsquack@yahoo.ca    pantelis5\n johnbree89    5432154321jb blaa \n 654321jb    johnbree2 tw \n month.erey.54@facebook.com \n 543321jb","favourite color: purple \ndads b day: 1967 04 11\n username: georgi.dimitrov@ryerson.ca \n token: nccunyuyebadcqzh \n internet pass: EGGY1 \n scs.ryerson.ca \ncoopsci username: g4dimitr@scs.ryerson.ca \npass: 258741kK \n ssh moon.scs.ryerson.ca \n"," "," "," "," "," ","stefanova \n georgi_rules@hotmail.com\n "," "," ","sofia"," "," ","georgi_rules@hotmail.com"," "," ","audi\ndimitrova\ngamble\nbart\nmarshals\nleaside\nseptember15"," "," ","moltenwow: assasin02    258456456g"," "," "};
//	                mDbHelper.createALLNote(title2, username2, password2, extra2);
                fillData();
                break;
            case ACTIVITY_EDIT:
                Long rowId = extras.getLong(PasswordAdapter.KEY_ROWID);
                if (rowId != null) {
                    String editTitle = extras.getString(PasswordAdapter.KEY_TITLE);
                    String editUsername = extras.getString(PasswordAdapter.KEY_USERNAME);
                    String editPassword = extras.getString(PasswordAdapter.KEY_PASSWORD);
                    String editExtra = extras.getString(PasswordAdapter.KEY_EXTRA);
                    String oldTitle = extras.getString(PasswordAdapter.KEY_OLDTITLE);
                    mDbHelper.updateNote(rowId, editTitle, editUsername, editPassword, editExtra, oldTitle);
                    if (!PasswordAdapter.dbStatus)
                        Toast.makeText(this, "Duplicate Entry", Toast.LENGTH_LONG).show();
                    fillData();
                }
                fillData();
                break;
            case ACTIVITY_EXIT:
                fillData();
                break;
        }
        switch (resultCode) {
            case ACTIVITY_FAILED:
                fillData();
                break;
            case ACTIVITY_EXIT:
                fillData();
                break;
        }
    }

    public void fillData() {
        c = mDbHelper.fetchAllNotes();
        List<String> all = new ArrayList<String>();
        startManagingCursor(c);
        if (c.moveToFirst()) {
            do {
                all.add(c.getString(1));
            } while (c.moveToNext());
        }
        lv = (ListView) findViewById(R.id.lv_passwords);
        arrayAdapter = new ArrayAdapter<String>(PasswordListActivity.this, android.R.layout.simple_list_item_1, all);
        lv.setAdapter(arrayAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
        // TODO Auto-generated method stub

    }
}
