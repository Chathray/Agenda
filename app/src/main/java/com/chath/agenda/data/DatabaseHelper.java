package com.chath.agenda.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database
    private static final String DATABASE_NAME = "agenda_db";
    private static final Integer DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_SUBJECT = "subject";
    private static final String TABLE_CONTENT = "content";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_DESCRIPTION = "description";

    // Todo: Subject table create statement
    private static final String CREATE_TABLE_SUBJECT =
            "CREATE TABLE " + TABLE_SUBJECT + "("
                    + KEY_ID + " INTEGER PRIMARY KEY,"
                    + KEY_TITLE + " TEXT UNIQUE,"
                    + KEY_CREATED_AT + " DATETIME" + ")";

    // Todo: Subject table create statement
    private static final String CREATE_TABLE_CONTENT =
            "CREATE TABLE " + TABLE_CONTENT + "("
                    + KEY_ID + " INTEGER,"
                    + KEY_TITLE + " TEXT,"
                    + KEY_DESCRIPTION + " TEXT,"
                    + KEY_CREATED_AT + " DATETIME,"
                    + " FOREIGN KEY (" + KEY_ID + ") REFERENCES " + TABLE_SUBJECT + "(" + KEY_ID + "));";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * get datetime
     */
    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // creating required tables
        sqLiteDatabase.execSQL(CREATE_TABLE_SUBJECT);
        sqLiteDatabase.execSQL(CREATE_TABLE_CONTENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // on upgrade drop older tables
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTENT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECT);

        // create new tables
        onCreate(sqLiteDatabase);
    }

    /*---------------------------------------------------------------------------------*/

    /**
     * Creating a subject
     */
    public long createSubject(SubjectModel subject) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, subject.getTitle());
        values.put(KEY_CREATED_AT, getDateTime());

        return db.insert(TABLE_SUBJECT, null, values);
    }

    /**
     * Updating a subject
     */
    public int updateSubject(int sid, String newTitle) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, newTitle);

        // updating row
        return db.update(TABLE_SUBJECT, values, KEY_ID + " = ?",
                new String[]{String.valueOf(sid)});
    }

    /**
     * Deleting a subject
     */
    public boolean deleteSubject(int sid) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Define 'where' part of query.
        String selection = KEY_ID + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {sid + ""};
        // Issue SQL statement.
        db.delete(TABLE_CONTENT, selection, selectionArgs);
        int a = db.delete(TABLE_SUBJECT, selection, selectionArgs);
        db.close();

        return a != 0;
    }

    /**
     * getting all subjects
     */
    public ArrayList<SubjectModel> getAllSubject() {
        ArrayList<SubjectModel> subjects = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_SUBJECT;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                int id = c.getInt((c.getColumnIndex(KEY_ID)));
                String tl = c.getString((c.getColumnIndex(KEY_TITLE)));
                String ca = c.getString((c.getColumnIndex(KEY_CREATED_AT)));

                subjects.add(new SubjectModel(id, tl, ca));
            } while (c.moveToNext());
        }
        return subjects;
    }

    /**
     * getting all subject titles
     */
    public ArrayList<String> getAllSubjectTitle(int sort) {
        ArrayList<String> subjectTitles = new ArrayList<>();


        String selectQuery = "SELECT " + KEY_TITLE + " FROM " + TABLE_SUBJECT;
        switch (sort) {
            case 2:
                selectQuery += " ORDER BY " + KEY_TITLE + " DESC";
                break;
            case 3:
                selectQuery += " ORDER BY " + KEY_CREATED_AT + " DESC";
                break;
            case 4:
                selectQuery += " ORDER BY " + KEY_CREATED_AT + " ASC";
                break;
            default:
                selectQuery += " ORDER BY " + KEY_TITLE + " ASC";
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do subjectTitles.add(c.getString(0));
            while (c.moveToNext());
        }
        c.close();
        return subjectTitles;
    }

    public String getSubjectTitle(int content_key) {
        String selectQuery = "SELECT " + KEY_TITLE + " FROM " + TABLE_SUBJECT + " WHERE id = '" + content_key + "'";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        c.moveToFirst();
        return c.getString(0);
    }

    public int getSubjectKey(CharSequence title) {
        String selectQuery = "SELECT * FROM " + TABLE_SUBJECT + " WHERE title = '" + title + "'";

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        c.moveToFirst();
        int re =  c.getInt((c.getColumnIndex(KEY_ID)));
        c.close();
        return re;
    }

    /*---------------------------------------------------------------------------------*/

    /**
     * Creating a content
     */
    public long createContent(ContentModel content) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, content.getID());
        values.put(KEY_TITLE, content.getTitle());
        values.put(KEY_DESCRIPTION, content.getDescription());
        values.put(KEY_CREATED_AT, getDateTime());

        return db.insert(TABLE_CONTENT, null, values);
    }

    /**
     * Updating a content
     */
    public int updateContent(String oldTitle, ContentModel content) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, content.getTitle());
        values.put(KEY_DESCRIPTION, content.getDescription());

        // updating row
        return db.update(TABLE_CONTENT, values, KEY_ID + " = ? AND " + KEY_TITLE +
                " = ?", new String[]{String.valueOf(content.getID()), oldTitle});
    }

    /**
     * Deleting a content
     */
    public boolean deleteContent(ContentModel content) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Define 'where' part of query.
        String selection = KEY_ID + " LIKE ? AND " + KEY_TITLE + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {content.getID() + "", content.getTitle()};
        // Issue SQL statement.
        int a = db.delete(TABLE_CONTENT, selection, selectionArgs);
        db.close();

        return a != 0;
    }

    /**
     * getting all contents
     */
    public ArrayList<ContentModel> getAllContent(int sid, int sort) {
        ArrayList<ContentModel> contents = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_CONTENT + " WHERE " + KEY_ID + " = '" + sid;
        switch (sort) {
            case 2:
                selectQuery += "' ORDER BY " + KEY_TITLE + " DESC";
                break;
            case 3:
                selectQuery += "' ORDER BY " + KEY_CREATED_AT + " DESC";
                break;
            case 4:
                selectQuery += "' ORDER BY " + KEY_CREATED_AT + " ASC";
                break;
            default:
                selectQuery += "' ORDER BY " + KEY_TITLE + " ASC";
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                int id = c.getInt((c.getColumnIndex(KEY_ID)));
                String tl = c.getString((c.getColumnIndex(KEY_TITLE)));
                String ds = c.getString((c.getColumnIndex(KEY_DESCRIPTION)));
                String ca = c.getString((c.getColumnIndex(KEY_CREATED_AT)));

                contents.add(new ContentModel(id, tl, ds, ca));
            } while (c.moveToNext());
        }
        return contents;
    }
}