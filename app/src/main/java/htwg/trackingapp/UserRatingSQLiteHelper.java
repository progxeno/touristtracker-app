package htwg.trackingapp;

/**
 * Created by Alex on 25.06.14.
 */

/**
 * @description     This class handles the userRatings database, provides getter & setter,
 *                  and dedicated search and return functions
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class UserRatingSQLiteHelper extends SQLiteOpenHelper {
    // UserRatings table name
    private static final String TABLE_USERRATINGS = "userRatings";

    // UserRatings Table Columns names
    private static final String KEY_ID = AppPreferences.ID;
    private static final String KEY_TITLE = "title";
    private static final String KEY_TEXT = "text";
    private static final String KEY_STARS = "stars";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_SENT_TO_SERVER = "senttoserver";


    // SQL statement to create userRating table
    private static final String CREATE_USERRATINGS_TABLE = "CREATE TABLE userRatings ( " +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_TITLE + " TEXT, "+
            KEY_TEXT + " TEXT, "+
            KEY_STARS + " FLOAT, "+
            KEY_TIMESTAMP + " LONG, "+
            KEY_SENT_TO_SERVER + " BOOLEAN)";

    private static final String[] COLUMNS = {KEY_ID,KEY_TITLE,KEY_TEXT,KEY_STARS,KEY_TIMESTAMP,KEY_SENT_TO_SERVER};

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = AppPreferences.DATABASE_USERRATINGS_NAME;

    public UserRatingSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * @description     This method creates the database
     *
     * @param           db database to be created
     *
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create userRatings table
        db.execSQL(CREATE_USERRATINGS_TABLE);
    }

    /**
     * @description     This method upgrades a database
     *
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older userRatings table if existed
        db.execSQL("DROP TABLE IF EXISTS userRatings");

        // create fresh userRatings table
        this.onCreate(db);
    }

    /**
     * @description     This method inserts one single item in UserRatingsDb
     *
     * @param           userRating to be inserted
     *
     */
    public void addUserRating(UserRating userRating){
        //for logging
        Log.d("addUserRating", userRating.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, userRating.getTitle());
        values.put(KEY_TEXT, userRating.getText());
        values.put(KEY_STARS, userRating.getStars());
        values.put(KEY_TIMESTAMP, userRating.getTimeStamp());
        values.put(KEY_SENT_TO_SERVER, userRating.isSentToServer());

        // 3. insert
        db.insert(TABLE_USERRATINGS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // Log
        Log.e("addUserRating()", "values = " + values.toString());

        // 4. close
        db.close();
    }

    /**
     * @description     This method returns a list with all items in UserRatingsDb
     *
     * @param
     *
     * @return          list with all items in UserRatingsDb
     *
     */
    public List<UserRating> getAllUserRatings() {
        List<UserRating> userRatings = new LinkedList<UserRating>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_USERRATINGS;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build userRating and add it to list
        UserRating userRating = null;
        if (cursor.moveToLast()) {
            do {
                userRating = new UserRating();
                userRating.setId(cursor.getInt(0));
                userRating.setTitle(cursor.getString(1));
                userRating.setText(cursor.getString(2));
                userRating.setStars(cursor.getFloat(3));
                userRating.setTimeStamp(cursor.getLong(4));
                userRating.setSentToServer((cursor.getInt(5) != 0));

                // Add userRating to userRatings
                userRatings.add(userRating);
            } while (cursor.moveToPrevious());
        }

        Log.d("getAllUserRatings()", userRatings.toString());

        db.close();

        // return userRatings
        return userRatings;
    }

    /**
     * @description     This method returns the item in UserRatingsDb with the given id
     *
     * @param           id of item to be returned
     *
     * @return          returns item with given id or null
     */
    public UserRating getUserRating(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_USERRATINGS, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();
        else return null;

        // 4. build userRating object
        UserRating userRating = new UserRating();
        userRating.setId(cursor.getInt(0));
        userRating.setTitle(cursor.getString(1));
        userRating.setText(cursor.getString(2));
        userRating.setStars(cursor.getFloat(3));
        userRating.setTimeStamp(cursor.getLong(4));
        userRating.setSentToServer((cursor.getInt(5) != 0));

        Log.d("getUserRating("+id+")", userRating.toString());

        db.close();
        // 5. return userRating
        return userRating;
    }

    /**
     * @description     This method updates one single item in UserRatingsDb
     *
     * @param           userRating to be updated
     *
     *@return           id of updated item
     */
    public int updateUserRating(UserRating userRating) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, userRating.getTitle());
        values.put(KEY_TEXT, userRating.getText());
        values.put(KEY_STARS, userRating.getStars());
        values.put(KEY_TIMESTAMP, userRating.getTimeStamp());
        values.put(KEY_SENT_TO_SERVER, userRating.isSentToServer());

        // 3. updating row
        int i = db.update(TABLE_USERRATINGS, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(userRating.getId()) }); //selection args

        // 4. close
        db.close();

        return i;

    }

    /**
     * @description     This method deletes one single item in UserRatingsDb
     *
     * @param           userRating to be deleted
     *
     */
    public void deleteUserRating(UserRating userRating) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_USERRATINGS,
                KEY_ID+" = ?",
                new String[] { String.valueOf(userRating.getId()) });

        // 3. close
        db.close();

        Log.e("deleteUserRating", userRating.toString());

    }

    /**
     * @description     This method returns all UserRatingsDb that hadn't been sent to server yet
     *                  [senToServer = false]
     *
     * @param
     * @return          list with all UserRatings that need to be sent to server
     */
    public List<UserRating> getAllUserRatingsToSend() {
        List<UserRating> userRatings = new LinkedList<UserRating>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_USERRATINGS + " WHERE " + KEY_SENT_TO_SERVER + "=0";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build userRating and add it to list
        UserRating userRating = null;
        if (cursor.moveToLast()) {
            do {
                userRating = new UserRating();
                userRating.setId(cursor.getInt(0));
                userRating.setTitle(cursor.getString(1));
                userRating.setText(cursor.getString(2));
                userRating.setStars(cursor.getFloat(3));
                userRating.setTimeStamp(cursor.getLong(4));
                userRating.setSentToServer((cursor.getInt(5) != 0));

                // Add userRating to userRatings
                userRatings.add(userRating);
            } while (cursor.moveToPrevious());
        }

        Log.d("getAllUserRatingsToSend()", userRatings.toString());

        db.close();

        // return userRatings
        return userRatings;
    }

    /**
     * @description     This method updates one item in UserRatingsDb
     *                  [senToServer = true]
     *
     * @param           ids
     * @return          true if succeeded
     */
    public boolean updateUserRatingSent(String ids) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_SENT_TO_SERVER, 1);

        // 3. updating row
        int i = db.update(TABLE_USERRATINGS, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[]{ids}); //selection args
        Log.i("updateUserRatingSent(): ", "updated rows = " + i);

        // 4. close
        db.close();

        return true;

    }

}
