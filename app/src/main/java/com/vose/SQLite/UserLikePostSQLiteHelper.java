package com.vose.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by jimmyhou on 2014/9/1.
 */
public class UserLikePostSQLiteHelper extends SQLiteOpenHelper{
    public static final String TABLE_USER_LIKE_POST = "user_like_post";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_POST_ID = "post_id";
    public static final String COLUMN_CREATED = "created";

    private static final String DATABASE_NAME = "UserLikePost.db";
    private static final int DATABASE_VERSION = 1;

    //Database creation sql statement
    private static final String TABLE_CREATE = "create table "
            + TABLE_USER_LIKE_POST + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_USER_ID
            + " text not null, " + COLUMN_POST_ID + " text not null, "+COLUMN_CREATED+ " TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";

    public UserLikePostSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(UserLikePostSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_LIKE_POST);
        onCreate(db);
    }

}
