package com.example.passwordkeeper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class PasswordAdapter {

    static final String KEY_TITLE = "title";
    static final String KEY_USERNAME = "username";
    static final String KEY_PASSWORD = "password";
    static final String KEY_EXTRA = "extra";
    static final String KEY_ROWID = "_id";
    static final String KEY_OLDTITLE = "oldtitle";
    private static final String TAG = "NotesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    static boolean dbStatus = true;
    /**
     * Database creation sql statement
     */

    private static final String DATABASE_CREATE =
            "create table notes (_id integer primary key autoincrement, "
                    + "title text not null unique, username text not null, password text not null, extra);";
    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "notes";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    PasswordAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     * initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    PasswordAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     *
     * @param title the title of the note
     * @return rowId or -1 if failed
     */
    boolean createNote(String title, String username, String password, String extra) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_USERNAME, username);
        initialValues.put(KEY_PASSWORD, password);
        initialValues.put(KEY_EXTRA, extra);
        try {
            mDb.insertOrThrow(DATABASE_TABLE, null, initialValues);
        } catch (SQLiteConstraintException e) {
            dbStatus = false;
        }
        return dbStatus;
        //return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    void createALLNote(String[] title, String[] username, String[] password, String[] extra) {
        int ii = title.length;
        int i;
        for (i = 0; i < ii; i++) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_TITLE, title[i]);
            initialValues.put(KEY_USERNAME, username[i]);
            initialValues.put(KEY_PASSWORD, password[i]);
            initialValues.put(KEY_EXTRA, extra[i]);
            try {
                mDb.insertOrThrow(DATABASE_TABLE, null, initialValues);
            } catch (SQLiteConstraintException e) {
            }
        }
    }

    /**
     * Delete the note with the given rowId
     *
     * @return true if deleted, false otherwise
     */
    boolean deleteNote(String oldtitle) {
        String[] oldTitle = {oldtitle};
//    	return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + oldtitle, null) > 0;
        return mDb.delete(DATABASE_TABLE, KEY_TITLE + "=?", oldTitle) > 0;
    }

    boolean deleteALL() {
        return mDb.delete(DATABASE_TABLE, null, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     *
     * @return Cursor over all notes
     */
    Cursor fetchAllNotes() {
        Cursor pp = mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_TITLE,
                KEY_USERNAME, KEY_PASSWORD, KEY_EXTRA}, null, null, null, null, KEY_TITLE + " COLLATE NOCASE ASC");
        return pp;
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     *
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchNote(long rowId) throws SQLException {
        Cursor mCursor =
                mDb.query(true, DATABASE_TABLE, new String[]{KEY_ROWID,
                                KEY_TITLE, KEY_USERNAME, KEY_PASSWORD, KEY_EXTRA}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     *
     * @param rowId id of note to update
     * @param title value to set note title to
     * @return true if the note was successfully updated, false otherwise
     */
    boolean updateNote(long rowId, String title, String username, String password, String extra, String oldtitle) {
//    	open();
        dbStatus = true;
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_USERNAME, username);
        args.put(KEY_PASSWORD, password);
        args.put(KEY_EXTRA, extra);
        String[] blaa = {oldtitle};
        try {
            mDb.update(DATABASE_TABLE, args, KEY_TITLE + "= ?", blaa);
        } catch (SQLiteConstraintException e) {
            dbStatus = false;
        }
        return dbStatus;
    }
}