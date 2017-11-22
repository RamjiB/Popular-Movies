package com.rbuys.android.mdb.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavouriteDbHelper extends SQLiteOpenHelper{

    //the database name
    private static final String DATABASE_NAME = "favourite.db";

    //If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    //Constructor
    public FavouriteDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //Create a table to hold favourite movies data
        final String SQL_CREATE_FAVOURITE_TABLE = "CREATE TABLE "+
                FavouriteContract.FavouriteEntry.TABLE_NAME + " (" +
                FavouriteContract.FavouriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,  " +
                FavouriteContract.FavouriteEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavouriteContract.FavouriteEntry.COLUMN_IMAGE + " TEXT NOT NULL, " +
                FavouriteContract.FavouriteEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, " +
                FavouriteContract.FavouriteEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                FavouriteContract.FavouriteEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                FavouriteContract.FavouriteEntry.COLUMN_ID + " TEXT NOT NULL" + "); ";

        db.execSQL(SQL_CREATE_FAVOURITE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + FavouriteContract.FavouriteEntry.TABLE_NAME);
        onCreate(db);

    }
}
