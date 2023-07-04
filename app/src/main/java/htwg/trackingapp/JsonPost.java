package htwg.trackingapp;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Alex on 27.06.14.
 */

/**
 * @description     This class handles the data transfer functions over http requests
 *                  and builds the json objects to send
 */
public class JsonPost {
//    private static final String JSON_PARSE_OK = ": ok";

    private static final String USER_ID = "userid";
    private static final String USER_EMAIL = "email";
    private static final String USER_YEAR_OF_BIRTH = "year";
    private static final String USER_ZIP_CODE = "zipcode";
    private static final String USER_COUNTRY = "country";
    private static final String USER_IS_MALE_GENDER = "gender";
    private static final String USER_IS_RETURNER = "returner";

    private static final String RATING_TITLE = "ratingtitle";
    private static final String RATING_TEXT = "ratingtext";
    private static final String RATING_STARS = "ratingstars";
    private static final String RAITING_COLLECTION = "ratingcollection";

    private static final String GPS_ID = "id";
    private static final String GPS_VEHICLE = "vehicle";
    private static final String GPS_TIMESTAMP = "timestamp";
    private static final String GPS_LATITUDE = "latitude";
    private static final String GPS_LONGITUDE = "longitude";
    private static final String GPS_DISTANCE = "distance";
    private static final String GPS_COLLECTION = "gpscollection";

    public static  String GPS_DATAPOST_URL = "http://touristtracking.in.htwg-konstanz.de/addJGPSlog";         // TODO url gps
    public static String USER_DATAPOST_URL = "http://touristtracking.in.htwg-konstanz.de/addJUser";
    public static  String RATINGS_DATAPOST_URL = "http://touristtracking.in.htwg-konstanz.de/addJUserRating";     // TODO url ratings
    public static Boolean serverValid = true;

    public JsonPost() {
    }

    /**
     * @description             This method creates a JSON object containing user information
     *                          and sends it to the given url
     *
     * @param                   context application context
     *
     * @return                  true if succeeded
     */
    public static boolean userDataPOST(Context context){
        InputStream inputStream = null;
        String result = null;
        try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(USER_DATAPOST_URL);

            String json = null;

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate(USER_ID, AppPreferences.getId(context));
            jsonObject.accumulate(USER_EMAIL, AppPreferences.getEmail(context));
            jsonObject.accumulate(USER_YEAR_OF_BIRTH, AppPreferences.getYear(context));
            jsonObject.accumulate(USER_ZIP_CODE, AppPreferences.getZipcode(context));
            jsonObject.accumulate(USER_COUNTRY, AppPreferences.getCountry(context));
            jsonObject.accumulate(USER_IS_MALE_GENDER, AppPreferences.isMaleGender(context));
            jsonObject.accumulate(USER_IS_RETURNER, AppPreferences.isReturner(context));
            jsonObject.accumulate("version", android.os.Build.VERSION.SDK);
            jsonObject.accumulate("appVersion", AppPreferences.CURRENT_RELEASE_VERSION);
            jsonObject.accumulate("manufacturer", Build.MANUFACTURER);
            jsonObject.accumulate("model", Build.MODEL);

            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            jsonObject.accumulate("screenSize", display.toString());
            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();
            Log.i("USERDATA to send in JSONObject jsonObject ", json);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);
            Log.d("http post", "for execute " + httpPost.toString());

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

            Log.i("http post to .../addJUser: ", result);

        } catch (Exception e) {
            USER_DATAPOST_URL = "http://htwg-tourist-tracker.herokuapp.com/addJUser";

            if(userDataPOST(context)) {
                return true;
            } else {
                return false;
            }


        }
        if (result.contains("ok"))
            return true;
        else
            return false;
    }

    /**
     * @description             This method creates a JSON object containing user ratings
     *                          and sends it to the given url
     *
     * @param                   context application context
     *
     * @return                  true if succeeded
     */
    public static boolean userRatingsDataPOST(Context context) {
        InputStream inputStream = null;
        String result = "";

        UserRating userRating;

        //find all entrys to send
        UserRatingSQLiteHelper db = new UserRatingSQLiteHelper(context);
        List<UserRating> myUserRatings;
        myUserRatings = db.getAllUserRatingsToSend();
        if (myUserRatings.isEmpty()) return true;
        Iterator<UserRating> iterator = myUserRatings.iterator();

        try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(RATINGS_DATAPOST_URL);

            String json = "";


            // 3. build jsonObject

            //db.close();

            JSONObject jsonObject = new JSONObject();


            try {
                JSONArray jsonArray = new JSONArray();

                while (iterator.hasNext()) {
                    userRating = iterator.next();
                    JSONObject styleJson = new JSONObject();
                    styleJson.put(USER_ID, AppPreferences.getId(context));
                    styleJson.put(RATING_TITLE, userRating.getTitle());
                    styleJson.put(RATING_TEXT, userRating.getText());
                    styleJson.put(RATING_STARS, String.valueOf(((int) userRating.getStars())));
                    jsonArray.put(styleJson);
                    Log.d("while (iterator.hasNext())", "Id i = " + userRating.getId());

                }
                jsonObject.put(RAITING_COLLECTION, jsonArray);
                Log.d("Raitings JSONObject jsonObject ", "jsonObject.toString() " + jsonObject.toString());
            } catch (Exception e) {
                Log.d("Raitings to send catch (Exception e) ", e.getLocalizedMessage());
            }


            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

            Log.i("http post to .../addJUserRating: ", result);





        } catch (Exception e) {
            RATINGS_DATAPOST_URL = "http://htwg-tourist-tracker.herokuapp.com/addJUserRating";
            if(userRatingsDataPOST(context)){
                return true;
            } else {
                return false;
            }

        }
        if (result.contains("ok")) {


            iterator = myUserRatings.iterator();
            while (iterator.hasNext()) {
                userRating = iterator.next();

                Log.d("while (iterator.hasNext())", "Id i = " + userRating.getId());
                db.updateUserRatingSent(Integer.toString(userRating.getId()));
            }
            return true;

        } else
            return false;
    }

    /**
     * @description             This method creates a JSON object containing user GPS logs
     *                          and sends it to the given url
     *
     * @param                   context application context
     *
     * @return                  true if succeeded
     */
    public static boolean gpsDataPOST(Context context){
        InputStream inputStream = null;
        String result = null;

        GPSLog gpsLog;

        GPSLogSQLiteHelper db = new GPSLogSQLiteHelper(context);
        List<GPSLog> gpsLogs = db.getAllGPSLogsToSend();

        if (gpsLogs.isEmpty()) return true;

        Iterator<GPSLog> iterator = gpsLogs.iterator();

        try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(GPS_DATAPOST_URL);

            String json = null;

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            try {
                JSONArray jsonArray = new JSONArray();
                while (iterator.hasNext()) {
                    gpsLog = iterator.next();
                    JSONObject styleJson = new JSONObject();
                    styleJson.put(USER_ID, AppPreferences.getId(context));
                    styleJson.put(GPS_VEHICLE, gpsLog.getVehicle());
                    styleJson.put(GPS_TIMESTAMP, gpsLog.getTimeStamp());
                    styleJson.put(GPS_LATITUDE, gpsLog.getLatitude());
                    styleJson.put(GPS_LONGITUDE, gpsLog.getLongitude());
                    styleJson.put(GPS_DISTANCE, gpsLog.getDistance());
                    jsonArray.put(styleJson);
                }
                jsonObject.put(GPS_COLLECTION, jsonArray);
                //Log.d("GPS_COLLECTION to send in JSONObject jsonObject ", jsonObject.toString());
            } catch (Exception e) {
                Log.e("GPS_COLLECTION to send in JSONObject jsonObject error:", e.getLocalizedMessage());
            }

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert input stream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            GPS_DATAPOST_URL = "http://htwg-tourist-tracker.herokuapp.com/addJGPSlog";
            if(gpsDataPOST(context)){
                return true;
            } else {
                return false;
            }

        }

        //Log.d("http post to .../addJGPSlog: ", result);

        if (result.contains("ok")) {
            // update sentToServer = true
            iterator = gpsLogs.iterator();

            while (iterator.hasNext()) {
                gpsLog = iterator.next();
                gpsLog.setSentToServer(true);
                db.updateGPSLog(gpsLog);
            }
            return true;
        }
        else
            return false;
    }

    /**
     * @description             This method converts a server response into a string
     *
     * @param                   inputStream to be converted into a string
     *
     * @return                  string containing the server response
     */
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

/*
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(jsonAction.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }


    @Override
    public void onClick(View view) {

        switch(view.getId()){
            case R.id.btnPost:
                if(!validate())
                    Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();
                // call AsynTask to perform network operation on separate thread
                new HttpAsyncTask().execute("http://hmkcode.appspot.com/jsonservlet"); <------------
                break;
        }

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return userDataPOST(context);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }
    }


    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }*/
}
