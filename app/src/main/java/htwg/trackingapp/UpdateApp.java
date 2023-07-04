package htwg.trackingapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by miosko on 20.11.14.
 */
public class UpdateApp extends AsyncTask<String,Void,Void> {
    private Context context;
    public void setContext(Context contextf){
        context = contextf;
    }

    @Override
    protected Void doInBackground(String... arg0) {

        String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                +"/app-debug.apk";
        try {
            URL url = new URL(arg0[0]);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(false);
            c.connect();

            String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            File file = new File(PATH);
            file.mkdirs();
            File outputFile = new File(PATH, "app-debug.apk");
            if(outputFile.exists()){
                outputFile.delete();
            }else  // Add Else part, it is missing in your code
            {
                outputFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(outputFile);


            InputStream is = c.getInputStream();


            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
            }
            fos.close();
            is.close();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
            context.startActivity(intent);


        } catch (Exception e) {


            UpdateApp atualizaApp = new UpdateApp();
            atualizaApp.setContext(context);
            atualizaApp.execute("http://htwg-tourist-tracker.herokuapp.com/assets/AppRelease/app-debug.apk");

            Log.i("UpdateAPP", "Update error! " + e.getMessage());
        }
        return null;
    }}
