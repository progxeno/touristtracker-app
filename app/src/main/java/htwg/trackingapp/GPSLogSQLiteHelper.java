package htwg.trackingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateFormat;
import android.util.Log;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Alex on 25.06.14.
 */

/**
 * @description     This class handles the GPSlog database, provides getter & setter,
 *                  and search and return functions
 */
public class GPSLogSQLiteHelper extends SQLiteOpenHelper {
    // GPSLogs table name
    private static final String TABLE_GPSLOGS = "GPSlogs";

    // GPSLogs Table Columns names
    private static final String KEY_ID = AppPreferences.ID;
    private static final String KEY_VEHICLE = "vehicle";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_DISTANCE = "distance";
    private static final String KEY_SENT_TO_SERVER = "senttoserver";

    // some constants included from AppPreferences
    //private static final Integer MILLISECONDS_PER_DAY = AppPreferences.MILLISECONDS_PER_DAY;

    private static final String[] COLUMNS = {KEY_ID,KEY_TIMESTAMP,KEY_LATITUDE,KEY_LONGITUDE,KEY_DISTANCE,KEY_SENT_TO_SERVER};

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = AppPreferences.DATABASE_GPS_NAME;

    public GPSLogSQLiteHelper(Context context) {
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

        // SQL statement to create userRating table
        String CREATE_GPSLOGS_TABLE = "CREATE TABLE " + TABLE_GPSLOGS + " ( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_VEHICLE + " TEXT, " +
                KEY_TIMESTAMP + " LONG, " +
                KEY_LATITUDE + " DOUBLE, " +
                KEY_LONGITUDE + " DOUBLE, " +
                KEY_DISTANCE + " DOUBLE, "+
                KEY_SENT_TO_SERVER + " BOOLEAN)";

        // create userRatings table
        db.execSQL(CREATE_GPSLOGS_TABLE);
    }

    /**
     * @description     This method upgrades a database
     *
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older userRatings table if existed
        db.execSQL("DROP TABLE IF EXISTS GPSLogs");

        // create fresh userRatings table
        this.onCreate(db);
    }

    /**
     * @description     This method calculates the distance between the old GPS position
     *                  and the current one and inserts one single item in GPSlogsDb
     *
     * @param           gpsLog to be inserted
     *
     */
    public void addGPSLog(GPSLog gpsLog){
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Log.i("GPSLOG:  ", gpsLog.toString());
        //calc distance
        String query = "SELECT  * FROM " + TABLE_GPSLOGS;

        // 2. get reference to writable DB
        Cursor cursor = db.rawQuery(query, null);
        double tmpDistance = 0;

        if (cursor.moveToLast()) {
            tmpDistance = gpsLog.calculateDistanceTwoPoints(cursor.getDouble(3),cursor.getDouble(4));
        }

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_VEHICLE, gpsLog.getVehicle()); // get title
        values.put(KEY_TIMESTAMP, gpsLog.getTimeStamp()); // get text
        values.put(KEY_LATITUDE, gpsLog.getLatitude()); // get stars
        values.put(KEY_LONGITUDE, gpsLog.getLongitude()); // get text
        values.put(KEY_DISTANCE, tmpDistance); // get stars
        values.put(KEY_SENT_TO_SERVER, gpsLog.isSentToServer());

        // 3. insert
        db.insert(TABLE_GPSLOGS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // Log
        //Log.e("addGPSLog()", "values = " + values.toString());
        // 4. close
        db.close();
    }

    /**
     * @description     This method returns a list with all items in GPSlogsDb
     *
     * @param
     *
     * @return          list with all items in GPSlogs
     *
     */
    public List<GPSLog> getAllGPSLogs() {
        List<GPSLog> gpsLogs = new LinkedList<GPSLog>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_GPSLOGS;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build userRating and add it to list
        GPSLog gpsLog = null;
        if (cursor.moveToLast()) {
            do {
                gpsLog = new GPSLog();
                gpsLog.setId(cursor.getInt(0));
                gpsLog.setVehicle(cursor.getInt(1));
                gpsLog.setTimeStamp(cursor.getLong(2));
                gpsLog.setLatitude(cursor.getDouble(3));
                gpsLog.setLongitude(cursor.getDouble(4));
                gpsLog.setDistance(cursor.getDouble(5));
                gpsLog.setSentToServer((cursor.getInt(6) != 0));
                gpsLogs.add(gpsLog);
            } while (cursor.moveToPrevious());
        }

        //Log.d("getAllGPSLogs()", gpsLogs.toString());

        db.close();

        // return gpsLogs
        return gpsLogs;
    }

    /**
     * @description     This method returns the item in GPSLogs with the given id
     *
     * @param           id of item to be returned
     *
     * @return          returns item with given id or null
     */
    public GPSLog getGPSLog(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_GPSLOGS, // a. table
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

        // 4. build userRating object
        GPSLog gpsLog = new GPSLog();
        gpsLog.setId(cursor.getInt(0));
        gpsLog.setVehicle(cursor.getInt(1));
        gpsLog.setTimeStamp(cursor.getLong(2));
        gpsLog.setLatitude(cursor.getDouble(3));
        gpsLog.setLongitude(cursor.getDouble(4));
        gpsLog.setDistance(cursor.getDouble(5));
        gpsLog.setSentToServer((cursor.getInt(6) != 0));

        Log.d("getGPSLog("+id+")", gpsLog.toString());

        db.close();

        // 5. return gpsLog
        return gpsLog;
    }


    /**
     * @description     This method updates one single item in GPSlogsDb
     *
     * @param           gpsLog to be updated
     *
     *@return           id of updated item
     */
    public int updateGPSLog(GPSLog gpsLog) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_VEHICLE, gpsLog.getVehicle()); // get title
        values.put(KEY_TIMESTAMP, gpsLog.getTimeStamp()); // get text
        values.put(KEY_LATITUDE, gpsLog.getLatitude()); // get stars
        values.put(KEY_LONGITUDE, gpsLog.getLongitude()); // get text
        values.put(KEY_DISTANCE, gpsLog.getDistance()); // get stars
        values.put(KEY_SENT_TO_SERVER, gpsLog.isSentToServer());

        // 3. updating row
        int i = db.update(TABLE_GPSLOGS, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(gpsLog.getId()) }); //selection args

        // 4. close
        db.close();

        return i;

    }

    /**
     * @description     This method deletes one single item in GPSlogsDb
     *
     * @param           gpsLog to be deleted
     *
     */
    public void deleteGPSLog(GPSLog gpsLog) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_GPSLOGS,
                KEY_ID + " = ?",
                new String[] { String.valueOf(gpsLog.getId()) });

        // 3. close
        db.close();

        Log.d("deleteGPSLog", gpsLog.toString());
    }

    /**
     * @description     This method calculates the distance for each vehicle group
     *                  per day.
     *                  - select every item in GPSlogs which hasn't been added to a route yet
     *                  - sum all distances in GPSlogs item for each day
     *                  - update existing dailyRoute item or insert new one
     *                  - update lastSaveDate
     *
     * @param           context     application Context
     */
    public void calculateDailyDistancesAndInsertInDailyRoutes(Context context) {

        long lastSaveDay = AppPreferences.getLastSaveDate(context);

        // iterate over each day till now
        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_GPSLOGS + " WHERE timestamp >= " + String.valueOf(lastSaveDay);//!!!!!!!!!
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Cursor cursor2 = db.rawQuery(query, null);

        // for each day new entry in dailyRoutes
        if (cursor.moveToFirst() && cursor2.moveToFirst()) {

            double tmpWalkDistance = 0;
            double tmpCarDistance = 0;
            double tmpTrainDistance = 0;
            double tmpFerryDistance = 0;
            double tmpTotalDistance = 0;

            // get current day
            Date currentDate = new Date();
            long currentDay = currentDate.getTime();

            // open dailyRoutesDb
            DailyRouteSQLiteHelper dailyRouteDb = new DailyRouteSQLiteHelper(context);
            do {
                // switch case for vehicle
                switch (cursor.getInt(1)) {
                    case 0:
                        // walk
                        tmpWalkDistance += cursor.getDouble(5);
                        break;
                    case 1:
                        // car
                        tmpCarDistance += cursor.getDouble(5);
                        break;
                    case 2:
                        // train
                        tmpTrainDistance += cursor.getDouble(5);
                        break;
                    case 3:
                        // ferry
                        tmpFerryDistance += cursor.getDouble(5);
                        break;
                }
                tmpTotalDistance += cursor.getDouble(5);

                // if current item is last item of this day or last item in list,
                // save data to dailyRouteDb
                if (cursor2.moveToNext()) {
                    CharSequence s1  = DateFormat.format("dd.MM.yyyy", cursor.getLong(2));
                    String date1  = s1.toString();
                    CharSequence s2  = DateFormat.format("dd.MM.yyyy", cursor2.getLong(2));
                    String date2  = s2.toString();

                    // calc day nummer since 01.01.1970 till today and compare
                    if (date1.equalsIgnoreCase(date2)) {
                        continue;
                    }
                }

                // convert date to string and store all data in new dailyRoute instance
                CharSequence s  = DateFormat.format("dd.MM.yyyy", cursor.getLong(2) * 1000L);
                String date  = s.toString();

                // new istance of dayilyRoute
                DailyRoute dailyRoute = new DailyRoute(date,tmpWalkDistance,
                                                            tmpCarDistance,
                                                            tmpTrainDistance,
                                                            tmpFerryDistance,
                                                            tmpTotalDistance);

                // search id of current days dailyRoute object
                DailyRoute dailyRouteSearchResult = dailyRouteDb.searchDailyRouteByDate(date);
                if (dailyRouteSearchResult != null) {
                    // item found

                    // update dailyRoute (from today)
                    dailyRoute.setId(dailyRouteSearchResult.getId());
                    dailyRoute.setWalkDistance(dailyRouteSearchResult.getWalkDistance() + tmpWalkDistance);
                    dailyRoute.setCarDistance(dailyRouteSearchResult.getCarDistance() + tmpCarDistance);
                    dailyRoute.setTrainDistance(dailyRouteSearchResult.getTrainDistance() + tmpTrainDistance);
                    dailyRoute.setFerryDistance(dailyRouteSearchResult.getFerryDistance() + tmpFerryDistance);
                    dailyRoute.setTotalDistance(dailyRouteSearchResult.getTotalDistance() + tmpTotalDistance);
                    dailyRouteDb.updateDailyRoute(dailyRoute);
                }
                else {
                    // insert dailyRoute in dailyRouteDb
                    dailyRouteDb.addDailyRoute(dailyRoute);
                }

                // reset tmpVariables for next day calculation
                tmpWalkDistance = 0;
                tmpCarDistance = 0;
                tmpTrainDistance = 0;
                tmpFerryDistance = 0;
                tmpTotalDistance = 0;

            } while (cursor.moveToNext());

            dailyRouteDb.close();
        }

        db.close();

        // update last save date
        AppPreferences.setLastSaveDate(context);

        // call delete function to save memory
        deleteGPSLogSent(context);
    }

    /**
     * @description     This method returns all GPSlogs that hadn't been sent to server yet
     *                  [senToServer = false]
     *
     * @param
     * @return          list with all GPSlogs that need to be sent to server
     */
    public List<GPSLog> getAllGPSLogsToSend() {
        List<GPSLog> gpsLogs = new LinkedList<GPSLog>();

        // get timestamp
        Date date = new Date();
        String timeStamp = String.valueOf(date.getTime());

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_GPSLOGS + " WHERE " + KEY_SENT_TO_SERVER + "=0";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build userRating and add it to list
        GPSLog gpsLog = null;
        if (cursor.moveToLast()) {
            do {
                gpsLog = new GPSLog();
                gpsLog.setId(cursor.getInt(0));
                gpsLog.setVehicle(cursor.getInt(1));
                gpsLog.setTimeStamp(cursor.getLong(2));
                gpsLog.setLatitude(cursor.getDouble(3));
                gpsLog.setLongitude(cursor.getDouble(4));
                gpsLog.setDistance(cursor.getDouble(5));
                gpsLog.setSentToServer((cursor.getInt(6) != 0));

                // Add userRating to userRatings
                gpsLogs.add(gpsLog);
            } while (cursor.moveToPrevious());
        }
        db.close();

        Log.d("getAllGPSLogsToSend(): ", gpsLogs.toString());
        return gpsLogs;
    }


    /**
     * @description     This method deletes all entrys in GPSlogsDb if their values
     *                  [senToServer = true] and [timestamp <= lastSaveDate]
     *
     * @param           context     application Context
     */
    public void deleteGPSLogSent(Context context) {

        // 1. build the query
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_GPSLOGS,
                KEY_SENT_TO_SERVER + " = 1" + " and " +
                KEY_TIMESTAMP + " <= " + Long.toString(AppPreferences.getLastSaveDate(context)),
                null);

        // 3. close
        db.close();

        Log.d("deleteGPSLogSent: ", "delete executed");
    }
}
