package htwg.trackingapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

/**
 * Created by Alex on 27.06.14.
 */

/**
 * @description     This class stores application specific data persistent in a .dat file
 *                  and provides getter and setter functions
 */
public class AppPreferences {


    public static final String APP_PREFERENCES = "app_preferences";
    public static final int MODE_PRIVATE = 0;

    public static final String USER_ID = "userid";
    public static final String USER_EMAIL = "email";
    public static final String USER_YEAR_OF_BIRTH = "year";
    public static final String USER_ZIP_CODE = "zipcode";
    public static final String USER_COUNTRY = "country";
    public static final String USER_IS_MALE_GENDER = "gender";
    public static final String USER_IS_RETURNER = "returner";
    public static final String USER_CURRENT_VEHICLE = "currentvehicle";

    public static final String APP_IS_SET_GENDER = "issetgender";
    public static final String APP_IS_SET_RETURNER = "issetreturner";
    public static final String APP_FIRST_APP_LAUNCH = "firstapplaunch";
    public static final String APP_LAST_SAVE_DATE = "lastsavedate";

    public static final String ID = "id";

    public static final String DATABASE_GPS_NAME = "GPSLogsDB";
    public static final String DATABASE_DAILYROUTES_NAME = "dailyRoutesDB";
    public static final String DATABASE_USERRATINGS_NAME = "userRatingsDB";

    //public static final Integer MILLISECONDS_PER_DAY = 1000 * 60 * 60 * 24;

    //App Release Version
    public static final int CURRENT_RELEASE_VERSION = 5;

    public AppPreferences() {
    }

    public static void setId(String id, Context context) {
        SharedPreferences pref =  context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(USER_ID, id);
        editor.commit();
    }

    public static void setEmail(String email, Context context) {
        SharedPreferences pref =  context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(USER_EMAIL, email);
        editor.putString(USER_ID, Integer.toString(email.hashCode()));
        editor.commit();
    }

    public static void setYear(int year, Context context) {
        SharedPreferences pref =  context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(USER_YEAR_OF_BIRTH, year);
        editor.commit();
    }

    public static void setZipcode(String zipcode, Context context) {
        SharedPreferences pref =  context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(USER_ZIP_CODE, zipcode);
        editor.commit();
    }

    public static void setCountry(String country, Context context) {
        SharedPreferences pref =  context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(USER_COUNTRY, country);
        editor.commit();
    }

    public static void setMaleGender(boolean maleGender, Context context) {
        SharedPreferences pref =  context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(USER_IS_MALE_GENDER, maleGender);
        editor.commit();
    }

    public static void setReturner(boolean returner, Context context) {
        SharedPreferences pref =  context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(USER_IS_RETURNER, returner);
        editor.commit();
    }

    public static void setCurrentVehicle(int vehicle, Context context) {
        SharedPreferences pref =  context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(USER_CURRENT_VEHICLE, vehicle);
        editor.commit();
    }

    public static void setIsSetGender(boolean isSetGender, Context context) {
        SharedPreferences pref =  context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(APP_IS_SET_GENDER, isSetGender);
        editor.commit();
    }

    public static void setIsSetReturner(boolean isSetReturner, Context context) {
        SharedPreferences pref =  context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(APP_IS_SET_RETURNER, isSetReturner);
        editor.commit();
    }

    public static void setFirstAppLaunch(boolean firstAppLaunch, Context context) {
        SharedPreferences pref =  context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(APP_FIRST_APP_LAUNCH, firstAppLaunch);
        editor.commit();
    }

    public static void setLastSaveDate(Context context) {
        SharedPreferences pref =  context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Date date = new Date();

        editor.putLong(APP_LAST_SAVE_DATE, date.getTime()/1000);
        editor.commit();
    }

    public static String getId(Context context) {
        SharedPreferences pref =  context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        return pref.getString(USER_ID, null);
    }

    public static String getEmail(Context context) {
        SharedPreferences pref =  context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        return pref.getString(USER_EMAIL, null);
    }

    public static int getYear(Context context) {
        SharedPreferences pref =  context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        return pref.getInt(USER_YEAR_OF_BIRTH, 0);
    }

    public static String getZipcode(Context context) {
        SharedPreferences pref =  context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        return pref.getString(USER_ZIP_CODE, null);
    }

    public static String getCountry(Context context) {
        SharedPreferences pref =  context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        return pref.getString(USER_COUNTRY, null);
    }

    public static boolean isMaleGender(Context context) {
        SharedPreferences pref =  context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        return pref.getBoolean(USER_IS_MALE_GENDER, false);
    }

    public static boolean isReturner(Context context) {
        SharedPreferences pref =  context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        return pref.getBoolean(USER_IS_RETURNER, false);
    }

    public static int getCurrentVehicle (Context context) {
        SharedPreferences pref =  context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        return pref.getInt(USER_CURRENT_VEHICLE, 0);
    }

    public static boolean isSetGender(Context context) {
        SharedPreferences pref =  context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        return pref.getBoolean(APP_IS_SET_GENDER, false);
    }

    public static boolean isSetReturner(Context context) {
        SharedPreferences pref =  context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        return pref.getBoolean(APP_IS_SET_RETURNER, false);
    }

    public static boolean isFirstAppLaunch(Context context) {
        SharedPreferences pref =  context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        return pref.getBoolean(APP_FIRST_APP_LAUNCH, true);
    }

    public static long getLastSaveDate(Context context) {
        SharedPreferences pref =  context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        return pref.getLong(APP_LAST_SAVE_DATE, 0);
    }

}