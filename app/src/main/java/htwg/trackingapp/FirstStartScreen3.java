package htwg.trackingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


public class FirstStartScreen3 extends Activity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_start_screen_3);

        context = getApplicationContext();
    }

    //---------------------------------------------------------------------
    // Set navigation buttons on click
    //---------------------------------------------------------------------
    public void buttonOnClick(View v) {
        switch (v.getId()) {
            case R.id.forwardButton:
                if (!isConnected()) {
                    Toast.makeText(context, "Internet connection required", Toast.LENGTH_LONG).show();
                    break;
                }
                //Set first Lunch false;
                AppPreferences.setFirstAppLaunch(false, context);
                if (JsonPost.userDataPOST(context)){
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainMenu.class));
                } else {
                    Toast.makeText(context, "Server failure", Toast.LENGTH_LONG).show();}
                break;
            case R.id.backButton:
                startActivity(new Intent(getApplicationContext(), FirstStartScreen2.class));
                break;
            case R.id.dbackButton:
                startActivity(new Intent(getApplicationContext(), FirstStartScreen1.class));
                break;
            default:
                break;
        }
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
}
