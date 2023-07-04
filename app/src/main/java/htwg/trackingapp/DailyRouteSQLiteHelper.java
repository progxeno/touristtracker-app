package htwg.trackingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Alex on 25.06.14.
 */

/**
 * @description     This class handles the dailyRoutes database, provides getter & setter,
 *                  and dedicated search and return functions
 */
public class DailyRouteSQLiteHelper extends SQLiteOpenHelper {
    // DailyRoutes table name
    private static final String TABLE_DAILYROUTES = "dailyRoutes";

    // DailyRoutes Table Columns names
    private static final String KEY_ID = AppPreferences.ID;
    private static final String KEY_DATE = "date";
    private static final String KEY_WALK_DISTANCE = "walkdistance";
    private static final String KEY_CAR_DISTANCE = "cardistance";
    private static final String KEY_TRAIN_DISTANCE = "traindistance";
    private static final String KEY_FERRY_DISTANCE = "ferrydistance";
    private static final String KEY_TOTAL_DISTANCE = "totaldistance";

    private static final String[] COLUMNS = {KEY_ID,KEY_DATE,KEY_WALK_DISTANCE,KEY_CAR_DISTANCE,KEY_TRAIN_DISTANCE,KEY_FERRY_DISTANCE,KEY_TOTAL_DISTANCE};

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = AppPreferences.DATABASE_DAILYROUTES_NAME;

    public DailyRouteSQLiteHelper(Context context) {
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

        // SQL statement to create dailyRoute table
        String CREATE_DAILYROUTES_TABLE = "CREATE TABLE dailyRoutes ( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_DATE + " TEXT, "+
                KEY_WALK_DISTANCE + " DOUBLE, "+
                KEY_CAR_DISTANCE + " DOUBLE, "+
                KEY_TRAIN_DISTANCE + " DOUBLE, "+
                KEY_FERRY_DISTANCE + " DOUBLE, "+
                KEY_TOTAL_DISTANCE + " DOUBLE)";

        // create dailyRoutes table
        db.execSQL(CREATE_DAILYROUTES_TABLE);
    }

    /**
     * @description     This method upgrades a database
     *
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older dailyRoutes table if existed
        db.execSQL("DROP TABLE IF EXISTS dailyRoutes");

        // create fresh dailyRoutes table
        this.onCreate(db);
    }

    /**
     * @description     This method calculates the distance between the old GPS position
     *                  and the current one and inserts one single item in GPSlogsDb
     *
     * @param           dailyRoute to be inserted
     *
     */
    public void addDailyRoute(DailyRoute dailyRoute){
        //for logging
        Log.d("addDailyRoute", dailyRoute.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, dailyRoute.getDate());
        values.put(KEY_WALK_DISTANCE, dailyRoute.getWalkDistance());
        values.put(KEY_CAR_DISTANCE, dailyRoute.getCarDistance());
        values.put(KEY_TRAIN_DISTANCE, dailyRoute.getTrainDistance());
        values.put(KEY_FERRY_DISTANCE, dailyRoute.getFerryDistance());
        values.put(KEY_TOTAL_DISTANCE, dailyRoute.getTotalDistance());

        // 3. insert
        db.insert(TABLE_DAILYROUTES, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // Log
        Log.e("addDailyRoute()", "values = " + values.toString());

        // 4. close
        db.close();
    }

    /**
     * @description     This method returns a list with all items in DailyRouteDb
     *
     * @param
     *
     * @return          list with all items in DailyRouteDb
     *
     */
    public List<DailyRoute> getAllDailyRoutes() {
        List<DailyRoute> dailyRouteList = new LinkedList<DailyRoute>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_DAILYROUTES;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build dailyRoute and add it to list
        DailyRoute dailyRoute = null;
        if (cursor.moveToLast()) {
            do {
                dailyRoute = new DailyRoute();
                dailyRoute.setId(cursor.getInt(0));
                dailyRoute.setDate(cursor.getString(1));
                dailyRoute.setWalkDistance(cursor.getDouble(2));
                dailyRoute.setCarDistance(cursor.getDouble(3));
                dailyRoute.setTrainDistance(cursor.getDouble(4));
                dailyRoute.setFerryDistance(cursor.getDouble(5));
                dailyRoute.setTotalDistance(cursor.getDouble(6));

                // Add dailyRoute to dailyRouteList
                dailyRouteList.add(dailyRoute);
            } while (cursor.moveToPrevious());
        }

        Log.i("getAllDailyRoutes()", dailyRouteList.toString());

        db.close();

        // return dailyRouteList
        return dailyRouteList;
    }

    /**
     * @description     This method returns the item in DailyRouteDb with the given id
     *
     * @param           id of item to be returned
     *
     * @return          returns item with given id or null
     */
    public DailyRoute getDailyRoute(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_DAILYROUTES, // a. table
                        COLUMNS, // b. column names
                        " " + KEY_ID + " = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();
        else return null;

        // 4. build dailyRoute object
        DailyRoute dailyRoute = new DailyRoute();
        dailyRoute.setId(cursor.getInt(0));
        dailyRoute.setDate(cursor.getString(1));
        dailyRoute.setWalkDistance(cursor.getDouble(2));
        dailyRoute.setCarDistance(cursor.getDouble(3));
        dailyRoute.setTrainDistance(cursor.getDouble(4));
        dailyRoute.setFerryDistance(cursor.getDouble(5));
        dailyRoute.setTotalDistance(cursor.getDouble(6));

        Log.d("getDailyRoute("+id+")", dailyRoute.toString());

        // 5. return dailyRoute
        db.close();
        return dailyRoute;
    }

    /**
     * @description     This method updates one single item in DailyRouteDb
     *
     * @param           dailyRoute to be updated
     *
     *@return           id of updated item
     */
    public int updateDailyRoute(DailyRoute dailyRoute) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, dailyRoute.getDate());
        values.put(KEY_WALK_DISTANCE, dailyRoute.getWalkDistance());
        values.put(KEY_CAR_DISTANCE, dailyRoute.getCarDistance());
        values.put(KEY_TRAIN_DISTANCE, dailyRoute.getTrainDistance());
        values.put(KEY_FERRY_DISTANCE, dailyRoute.getFerryDistance());
        values.put(KEY_TOTAL_DISTANCE, dailyRoute.getTotalDistance());

        // 3. updating row
        int i = db.update(TABLE_DAILYROUTES, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(dailyRoute.getId()) }); //selection args

        // 4. close
        db.close();
        Log.e("updateDailyRoute()", "values = " + values.toString());
        return i;

    }

    /**
     * @description     This method deletes one single item in DailyRouteDb
     *
     * @param           dailyRoute to be deleted
     *
     */
    public void deleteDailyRoute(DailyRoute dailyRoute) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_DAILYROUTES,
                KEY_ID+" = ?",
                new String[] { String.valueOf(dailyRoute.getId()) });

        // 3. close
        db.close();

        Log.e("deleteDailyRoute", dailyRoute.toString());

    }

    /**
     * @description     This method returns one single item which contains
     *                  the given date in DailyRouteDb
     *
     * @param           date    the item with this date
     *
     * @return          item with given date or null
     *
     */
    public DailyRoute searchDailyRouteByDate(String date){

        //Log.d("searchDailyRouteByDate", "in Funktion:");
        String query = "SELECT  * FROM " + TABLE_DAILYROUTES + " WHERE " + KEY_DATE + " = '"+ date + "'";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. if we got results get the first one
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
        }
        else {
            //Log.d("in Funktion: searchDailyRouteByDate(): ", "return mit null ");
            return null;
        }

        Log.d("in Funktion: searchDailyRouteByDate() ", "cursor " + cursor.getCount() + " und " + cursor.getPosition());

        // 4. build dailyRoute object
        DailyRoute dailyRoute = new DailyRoute();
        dailyRoute.setId(cursor.getInt(0));
        dailyRoute.setDate(cursor.getString(1));
        dailyRoute.setWalkDistance(cursor.getDouble(2));
        dailyRoute.setCarDistance(cursor.getDouble(3));
        dailyRoute.setTrainDistance(cursor.getDouble(4));
        dailyRoute.setFerryDistance(cursor.getDouble(5));
        dailyRoute.setTotalDistance(cursor.getDouble(6));

        Log.d("searchDailyRouteByDate("+cursor.getInt(0)+")", dailyRoute.toString());

        db.close();
        // 5. return dailyRoute
        return dailyRoute;
    }

}
