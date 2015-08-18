package com.vose.core.data.dao.userlikepost;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.parse.ParseUser;
import com.vose.SQLite.UserLikePostSQLiteHelper;
import com.vose.data.model.post.Post;
import com.vose.data.model.post.UserLikePost;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jimmyhou on 2014/9/6.
 */
public class UserLikePostSQLiteDAOSQLiteImpl implements UserLikePostSQLiteDAO {

    private SQLiteDatabase database;
    private UserLikePostSQLiteHelper dbHelper;
    private String[] allColumns = {UserLikePostSQLiteHelper.COLUMN_ID, UserLikePostSQLiteHelper.COLUMN_USER_ID, UserLikePostSQLiteHelper.COLUMN_POST_ID, UserLikePostSQLiteHelper.COLUMN_CREATED};
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Cursor cursor;
    public UserLikePostSQLiteDAOSQLiteImpl(Context context){
        dbHelper = new UserLikePostSQLiteHelper(context);

    }

    @Override
    public void openSQLiteDB(){
        database = dbHelper.getWritableDatabase();
    }

    @Override
    public void closeSQLiteDB(){
        dbHelper.close();
    }

    @Override
    //save at local , acted like a cache, need to kick out the old one, only maintain 30 userlikepost?
    public UserLikePost createUserLikePost(Post post) throws ParseException {
        dbHelper.onUpgrade(database, database.getVersion(), database.getVersion()+1);
        ContentValues values = new ContentValues();
        //values order in query
        values.put(UserLikePostSQLiteHelper.COLUMN_USER_ID, ParseUser.getCurrentUser().getObjectId());
        values.put(UserLikePostSQLiteHelper.COLUMN_POST_ID, post.getObjectId());
        values.put(UserLikePostSQLiteHelper.COLUMN_CREATED, df.format(new Date()));




        //insert into db
        long insertId = database.insert(UserLikePostSQLiteHelper.TABLE_USER_LIKE_POST, null, values);
        cursor = database.query(UserLikePostSQLiteHelper.TABLE_USER_LIKE_POST, allColumns,  UserLikePostSQLiteHelper.COLUMN_ID+ " = "+ insertId, null, null,null,null);
        cursor.moveToFirst();
        //cursor.close();
        UserLikePost userLikePost = cursorToUserLikePost(cursor);

        return userLikePost;
    }

    @Override
    public List<String> getMostRecentlyLikedPostIds(String userId, int numberOfRetunredRows) throws ParseException {
        List<String> likedPostIds = new ArrayList<String>();
        //useful code to get DB schema
       // Cursor cursorColumns = database.rawQuery("SELECT * FROM " +UserLikePostSQLiteHelper.TABLE_USER_LIKE_POST  + " LIMIT 1", null);
       // String[] colNames = cursorColumns.getColumnNames();

        //Cursor cursorRaw = database.rawQuery(queryString, null);


        //UserLikePostSQLiteHelper.COLUMN_CREATED+" ASC"
        //+" LIMIT "+ numberOfRetunredRows
//        Cursor cursor = database.query(UserLikePostSQLiteHelper.TABLE_USER_LIKE_POST,
//                new String[]{UserLikePostSQLiteHelper.COLUMN_POST_ID}, UserLikePostSQLiteHelper.COLUMN_USER_ID+" = '"+ userId+"'", null, null, null, UserLikePostSQLiteHelper.COLUMN_CREATED+" ASC");

        //user rawQuery since we can only put limit behind order
        String queryString = "SELECT post_id FROM " + UserLikePostSQLiteHelper.TABLE_USER_LIKE_POST+"  WHERE "+ UserLikePostSQLiteHelper.COLUMN_USER_ID+" = '" + userId+"' ORDER BY "+UserLikePostSQLiteHelper.COLUMN_CREATED+" DESC LIMIT "+numberOfRetunredRows;
        cursor = database.rawQuery(queryString,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            // only return one column
            String postId = cursor.getString(0);
            likedPostIds.add(postId);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        //cursor.close();
        return likedPostIds;

    }

    private UserLikePost cursorToUserLikePost(Cursor cursor) throws ParseException {
        UserLikePost userLikePost = new UserLikePost();
//        userLikePost.setUserId(cursor.getString(1));
//        userLikePost.setPostId(cursor.getString(2));
//        userLikePost.setCreated(df.parse(cursor.getString(3)));

        return  userLikePost;
    }

    public void closeCursor(){
        cursor.close();
    }
}
