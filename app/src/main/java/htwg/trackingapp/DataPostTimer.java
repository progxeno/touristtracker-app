package htwg.trackingapp;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import android.app.Service;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by miosko on 13.11.14.
 */

public class DataPostTimer extends Service
{
    private static Timer timer = new Timer();


    public void onCreate()
    {
        super.onCreate();
        startService();
    }

    private void startService()
    {
            timer.scheduleAtFixedRate(new mainTask(), 0, 1000 * 60 * 60 * 3);
    }

    private class mainTask extends TimerTask
    {
        public void run()
        { postHandler.sendEmptyMessage(0);
        }
    }

    public void onDestroy()
    {
        super.onDestroy();
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public IBinder onBind(Intent arg0)
    {
        return null;
    }
    private final Handler postHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if(isConnected()) {
                JsonPost.gpsDataPOST(getApplicationContext());
                JsonPost.userRatingsDataPOST(getApplicationContext());
               // Toast.makeText(getApplicationContext(), "Data Send!!!!!!", Toast.LENGTH_SHORT).show();
            }
        }
    };
}