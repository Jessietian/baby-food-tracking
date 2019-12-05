package com.example.babyfoodtracking;

import java.util.HashMap;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class BabyFood extends ContentProvider {
    static final String PROVIDER_NAME = "com.example.babyfoodtracking.BabyFood";
    static final String URL = "content://" + PROVIDER_NAME + "/baby_food";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String _ID = "_id";
    static final String NAME = "name";
    static final String CATEGORY = "category";
    static final String DATE = "date";
    static final String AMOUNT = "amount";
    static final String NOTE = "note";

    private static HashMap<String, String> STUDENTS_PROJECTION_MAP;

    static final int BABY_FOOD = 1;
    static final int FOOD_ID = 2;
    static final int FOOD_CATEGORY = 3;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "baby_food", BABY_FOOD);
        uriMatcher.addURI(PROVIDER_NAME, "baby_food/#", FOOD_ID);
    }

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "Food";
    static final String TABLE_NAME = "baby_food";
    static final int DATABASE_VERSION = 1;
    static String CREATE_DB_TABLE =
            " CREATE TABLE " + TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + " name TEXT NOT NULL, " +
                    " category TEXT NOT NULL, " + " date TEXT NOT NULL, " + " amount TEXT NOT NULL, " + " note TEXT NOT NULL);";


    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +  TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return (db == null)? false:true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = db.insert(	TABLE_NAME, "", values);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME);
        switch (uriMatcher.match(uri)) {
            case BABY_FOOD:
                qb.setProjectionMap(STUDENTS_PROJECTION_MAP);
                break;
            case FOOD_ID:
                qb.appendWhere( _ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
        }
        if (sortOrder == null || sortOrder == ""){
            sortOrder = NAME;
        }
        Cursor c = qb.query(db,	projection,	selection,
                selectionArgs,null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)){
            case BABY_FOOD:
                count = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case FOOD_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(TABLE_NAME, _ID +  " = " + id +
                                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case BABY_FOOD:
                count = db.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            case FOOD_ID:
                count = db.update(TABLE_NAME, values,
                        _ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case BABY_FOOD:
                return "vnd.android.cursor.dir/vnd.example.baby_food";
            case FOOD_ID:
                return "vnd.android.cursor.item/vnd.example.baby_food";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}