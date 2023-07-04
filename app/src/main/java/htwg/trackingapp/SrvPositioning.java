package htwg.trackingapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;

/**
 * Created by Alex on 04.07.14.
 */

/**
 * @description     This class extends service class and handles a service to get gps data
 */
public class SrvPositioning extends Service {


    // An alarm for rising in special times to fire the
    // pendingIntentPositioning
    private AlarmManager alarmManagerPositioning;
    // A PendingIntent for calling a receiver in special times
    public PendingIntent pendingIntentPositioning;

    @Override
    public void onCreate() {
        super.onCreate();
        alarmManagerPositioning = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intentToFire = new Intent(ReceiverPositioningAlarm.ACTION_REFRESH_SCHEDULE_ALARM);
        intentToFire.putExtra(ReceiverPositioningAlarm.COMMAND, ReceiverPositioningAlarm.SENDER_SRV_POSITIONING);
        pendingIntentPositioning = PendingIntent.getBroadcast(this, 0, intentToFire, 0);
    };

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        try {
            long interval = 1000 * 15;                                                     //TODO Interval
            int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
            long timeToRefresh = SystemClock.elapsedRealtime();
            alarmManagerPositioning.setInexactRepeating(alarmType, timeToRefresh, interval, pendingIntentPositioning);
        } catch (NumberFormatException e) {
            Toast.makeText(this,
                    "error running service: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this,
                    "error running service: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
       // getPointTriggerRoutine();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        this.alarmManagerPositioning.cancel(pendingIntentPositioning);
        ReceiverPositioningAlarm.stopLocationListener();
        super.onDestroy();
    }

    protected void getPointTriggerRoutine() {

        ReceiverPositioningAlarm.OnNewLocationListener onNewLocationListener = new ReceiverPositioningAlarm.OnNewLocationListener() {

            @Override
            public void onNewLocationReceived(Location location) {

                GPSLogSQLiteHelper gpsLogsDb = new GPSLogSQLiteHelper(getApplicationContext());
                gpsLogsDb.addGPSLog(new GPSLog(AppPreferences.getCurrentVehicle(getApplicationContext()), location));
                gpsLogsDb.close();
               // Toast.makeText(getApplicationContext(), "new GPS data saved" + location.toString(), Toast.LENGTH_SHORT).show();           //TODO Delete

                ReceiverPositioningAlarm.clearOnNewLocationListener(this);
            }
        };
        // start listening for new location
        ReceiverPositioningAlarm.setOnNewLocationListener(onNewLocationListener);
    }

}