package htwg.trackingapp;

import android.net.Uri;
import android.os.Build;
import android.util.Log;

import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * Created by miosko on 18.11.14.
 */
public class LocalFileSystemErrorSender implements ReportSender {
    private static String BASE_DIR_Name ="";

    public LocalFileSystemErrorSender(String formUri) {
        BASE_DIR_Name = Uri.parse(formUri).toString();
    }

    @Override
    public void send(CrashReportData report) throws ReportSenderException{
        String log = createCrashLog(report);

    }

    private String createCrashLog(CrashReportData report){

        // 1. create HttpClient
        HttpClient httpclient = new DefaultHttpClient();

        // 2. make POST request to the given URL
        HttpPost httpPost = new HttpPost(BASE_DIR_Name);

        Date now = new Date();
        StringBuilder log = new StringBuilder();
        try {
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("Package", report.get(ReportField.PACKAGE_NAME));
        jsonObject.accumulate("Version", report.get(ReportField.APP_VERSION_CODE));
        jsonObject.accumulate("Android",report.get(ReportField.ANDROID_VERSION));
        jsonObject.accumulate("Manufacturer", Build.MANUFACTURER);
        jsonObject.accumulate("Model", report.get(ReportField.PHONE_MODEL));
        jsonObject.accumulate("Date", now);
        jsonObject.accumulate("StackTrace", report.get(ReportField.STACK_TRACE));

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(jsonObject.toString());

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);
            Log.i("http post", "for execute " + httpPost.toString());

            BufferedReader rd = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            String response = "";
            String iter ="";
            while((iter = rd.readLine()) != null) {
                response += iter;
            }
            Log.i("Response:     ", response);

    } catch (Exception e) {
        BASE_DIR_Name = Uri.parse("http://htwg-tourist-tracker.herokuapp.com/reportcrash").toString();
            createCrashLog(report);
    }
        return log.toString();
    }
}
