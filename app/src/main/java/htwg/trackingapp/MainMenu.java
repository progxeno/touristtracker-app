package htwg.trackingapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


import htwg.trackingapp.UpdateHandler.ReleaseVersionListener;

public class MainMenu extends Activity implements ReleaseVersionListener{

    private List<UserRating> myUserRatings = new ArrayList<UserRating>();
    private List<DailyRoute> myDailyRoutes = new ArrayList<DailyRoute>();

    private ImageButton imageButton;
    private TextView textView;
    private Menu menuMain;
    private TabHost tabHost;
    GPSLogSQLiteHelper gpsLogsDb;
    Context context;

    /**
     * Just to test
     */
    //private static final int CURRENT_RELEASE_VERSION = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        MainMenu.this.startService(new Intent(MainMenu.this, DataPostTimer.class));

        UserRatingSQLiteHelper userRatingsDb = new UserRatingSQLiteHelper(this);
        final DailyRouteSQLiteHelper dailyRoutesDb = new DailyRouteSQLiteHelper(this);
        gpsLogsDb = new GPSLogSQLiteHelper(this);

        context = getApplicationContext();
        myUserRatings = userRatingsDb.getAllUserRatings();
        myDailyRoutes = dailyRoutesDb.getAllDailyRoutes();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        if (AppPreferences.isFirstAppLaunch(context)) {
            startActivity(new Intent(context, FirstStartScreen1.class));
        }


        if (isConnected()) {
            new UpdateHandler("http://touristtracking.in.htwg-konstanz.de/release", this).checkReleaseVersion();
        }


        final ActionBar actionBar = getActionBar();

        actionBar.setTitle("Tracking App");
        actionBar.setSubtitle("HTWG Konstanz");

        tabCreate(dailyRoutesDb);
        setVehicleButton();
    }



    @Override
    public void onReleaseVersionReceived(int releaseVersion) {
        String message;
        if(AppPreferences.CURRENT_RELEASE_VERSION < releaseVersion) {
            /**
             * Dialog mit Link zur Downloadpage srv/download
             */

            UpdateApp atualizaApp = new UpdateApp();
            atualizaApp.setContext(getApplicationContext());
            atualizaApp.execute("http://touristtracking.in.htwg-konstanz.de/assets/AppRelease/app-debug.apk");


            message = "A new version is available";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        } else if (releaseVersion != 0){
            String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    +"/app-debug.apk";
            File file = new File(fileName);
            file.delete();
            message = "App is up-to-date";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onReleaseVersionError(Exception e) {
        Toast.makeText(this, "An Error occured: " + e.getMessage(), Toast.LENGTH_LONG).show();
    }


    //--------------------------------------------------------------------
    // Create Tab Menu
    //--------------------------------------------------------------------
    private void tabCreate(final DailyRouteSQLiteHelper dailyRoutesDb){


        //--------------------------------------------------------------------
        // Create Tabs
        //--------------------------------------------------------------------
        tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();
        //Create Taps
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("Home");
        tabSpec.setContent(R.id.mainHomeTab);
        tabSpec.setIndicator("Home");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("Raitings");
        tabSpec.setContent(R.id.raitingTab);
        tabSpec.setIndicator(getResources().getString(R.string.ratingsTap));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("Daily Routes");
        tabSpec.setContent(R.id.goGreenTab);
        tabSpec.setIndicator("Daily Routes");
        tabHost.addTab(tabSpec);


        //--------------------------------------------------------------------
        // On Tabs Clicked
        //--------------------------------------------------------------------
        //TODO Help option
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String arg0) {
                switch (tabHost.getCurrentTab()) {
                    case 0:
                        //menuMain.clear();
                        menuMain.removeItem(R.id.addRating);
                        menuMain.removeItem(R.id.help);
                        getMenuInflater().inflate(R.menu.main_home_resume_tab_home, menuMain);
                        break;
                    case 1:
                        populateUserRatingsListView();
                        //menuMain.clear();
                        menuMain.removeItem(R.id.addRating);
                        menuMain.removeItem(R.id.help);
                        getMenuInflater().inflate(R.menu.main_home_tab_ratings, menuMain);
                        break;
                    case 2:
                        gpsLogsDb.calculateDailyDistancesAndInsertInDailyRoutes(getApplicationContext());

                        myDailyRoutes = dailyRoutesDb.getAllDailyRoutes();
                        populateDailyRoutesListView();

                       // Toast.makeText(MainMenu.this, myDailyRoutes.toString(), Toast.LENGTH_SHORT).show();
                        //menuMain.clear();

                        menuMain.removeItem(R.id.addRating);
                        menuMain.removeItem(R.id.help);
                        getMenuInflater().inflate(R.menu.main_home_tab_go_green, menuMain);
                        break;
                }
            }
        });
    }

    //--------------------------------------------------------------------
    // Set vehicle button on last value
    //--------------------------------------------------------------------
    private void setVehicleButton(){

        switch(AppPreferences.getCurrentVehicle(context)){
            case 0:
                imageButton = (ImageButton) findViewById(R.id.byFoot);
                imageButton.setBackgroundResource(R.drawable.transport_image_button_clicked);
                imageButton = (ImageButton) findViewById(R.id.byCar);
                imageButton.setBackgroundResource(R.drawable.transport_image_button_style);
                imageButton = (ImageButton) findViewById(R.id.byBus);
                imageButton.setBackgroundResource(R.drawable.transport_image_button_style);
                imageButton = (ImageButton) findViewById(R.id.byShip);
                imageButton.setBackgroundResource(R.drawable.transport_image_button_style);

                textView = (TextView) findViewById(R.id.transportChoiceText);
                textView.setText(R.string.byFootText);
                break;
            case 1:
                imageButton = (ImageButton) findViewById(R.id.byFoot);
                imageButton.setBackgroundResource(R.drawable.transport_image_button_style);
                imageButton = (ImageButton) findViewById(R.id.byCar);
                imageButton.setBackgroundResource(R.drawable.transport_image_button_clicked);
                imageButton = (ImageButton) findViewById(R.id.byBus);
                imageButton.setBackgroundResource(R.drawable.transport_image_button_style);
                imageButton = (ImageButton) findViewById(R.id.byShip);
                imageButton.setBackgroundResource(R.drawable.transport_image_button_style);

                textView = (TextView) findViewById(R.id.transportChoiceText);
                textView.setText(R.string.byCarText);
                break;

            case 2:
                imageButton = (ImageButton) findViewById(R.id.byFoot);
                imageButton.setBackgroundResource(R.drawable.transport_image_button_style);
                imageButton = (ImageButton) findViewById(R.id.byCar);
                imageButton.setBackgroundResource(R.drawable.transport_image_button_style);
                imageButton = (ImageButton) findViewById(R.id.byBus);
                imageButton.setBackgroundResource(R.drawable.transport_image_button_clicked);
                imageButton = (ImageButton) findViewById(R.id.byShip);
                imageButton.setBackgroundResource(R.drawable.transport_image_button_style);

                textView = (TextView) findViewById(R.id.transportChoiceText);
                textView.setText(R.string.byBusText);
                break;

            case 3:
                imageButton = (ImageButton) findViewById(R.id.byFoot);
                imageButton.setBackgroundResource(R.drawable.transport_image_button_style);
                imageButton = (ImageButton) findViewById(R.id.byCar);
                imageButton.setBackgroundResource(R.drawable.transport_image_button_style);
                imageButton = (ImageButton) findViewById(R.id.byBus);
                imageButton.setBackgroundResource(R.drawable.transport_image_button_style);
                imageButton = (ImageButton) findViewById(R.id.byShip);
                imageButton.setBackgroundResource(R.drawable.transport_image_button_clicked);

                textView = (TextView) findViewById(R.id.transportChoiceText);
                textView.setText(R.string.byShipText);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuMain = menu;
        getMenuInflater().inflate(R.menu.main_home_tab_home, menu);
        menu.removeItem(R.id.addRating);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        tabHost.findViewById(R.id.tabHost);
        if (tabHost.getCurrentTab() == 0) {
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
            Toast.makeText(MainMenu.this, R.string.notPossible, Toast.LENGTH_SHORT).show();
        }
        else
            tabHost.setCurrentTab(0);
    }

    //--------------------------------------------------------------------
    // Select transport vehicle and change background
    //--------------------------------------------------------------------
    public void transportVehicleButtonOnClick(View v) {
        MainMenu.this.startService(new Intent(MainMenu.this, SrvPositioning.class));

        MenuItem item = menuMain.findItem(R.id.tracking);
        item.setIcon(R.drawable.ic_action_stop);
        switch (v.getId()) {
            case R.id.byFoot:
                AppPreferences.setCurrentVehicle(0, context);
                v.setBackgroundResource(R.drawable.transport_image_button_clicked);
                imageButton = (ImageButton) findViewById(R.id.byCar);
                imageButton.setBackgroundResource(R.drawable.transport_image_button_style);
                imageButton = (ImageButton) findViewById(R.id.byBus);
                imageButton.setBackgroundResource(R.drawable.transport_image_button_style);
                imageButton = (ImageButton) findViewById(R.id.byShip);
                imageButton.setBackgroundResource(R.drawable.transport_image_button_style);

                textView = (TextView) findViewById(R.id.transportChoiceText);
                textView.setText(R.string.byFootText);
                break;
            case R.id.byCar:
                AppPreferences.setCurrentVehicle(1, context);
                v.setBackgroundResource(R.drawable.transport_image_button_clicked);
                imageButton = (ImageButton) findViewById(R.id.byFoot);
                imageButton.setBackgroundResource(R.drawable.transport_image_button_style);
                imageButton = (ImageButton) findViewById(R.id.byBus);
                imageButton.setBackgroundResource(R.drawable.transport_image_button_style);
                imageButton = (ImageButton) findViewById(R.id.byShip);
                imageButton.setBackgroundResource(R.drawable.transport_image_button_style);

                textView = (TextView) findViewById(R.id.transportChoiceText);
                textView.setText(R.string.byCarText);
                break;
            case R.id.byBus:
                AppPreferences.setCurrentVehicle(2, context);
                v.setBackgroundResource(R.drawable.transport_image_button_clicked);
                imageButton = (ImageButton) findViewById(R.id.byCar);
                imageButton.setBackgroundResource(R.drawable.transport_image_button_style);
                imageButton = (ImageButton) findViewById(R.id.byFoot);
                imageButton.setBackgroundResource(R.drawable.transport_image_button_style);
                imageButton = (ImageButton) findViewById(R.id.byShip);
                imageButton.setBackgroundResource(R.drawable.transport_image_button_style);

                textView = (TextView) findViewById(R.id.transportChoiceText);
                textView.setText(R.string.byBusText);
                break;
            case R.id.byShip:
                AppPreferences.setCurrentVehicle(3, context);
                v.setBackgroundResource(R.drawable.transport_image_button_clicked);
                imageButton = (ImageButton) findViewById(R.id.byCar);
                imageButton.setBackgroundResource(R.drawable.transport_image_button_style);
                imageButton = (ImageButton) findViewById(R.id.byBus);
                imageButton.setBackgroundResource(R.drawable.transport_image_button_style);
                imageButton = (ImageButton) findViewById(R.id.byFoot);
                imageButton.setBackgroundResource(R.drawable.transport_image_button_style);

                textView = (TextView) findViewById(R.id.transportChoiceText);
                textView.setText(R.string.byShipText);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.tracking:
                if (item.getIcon().getConstantState().equals(getResources().getDrawable(R.drawable.ic_action_go).getConstantState())) {
                    MainMenu.this.startService(new Intent(MainMenu.this, SrvPositioning.class));
                    item.setIcon(R.drawable.ic_action_stop);
                }
                else {
                    MainMenu.this.stopService(new Intent(MainMenu.this, SrvPositioning.class));
                    item.setIcon(R.drawable.ic_action_go);
                }
                break;
            case R.id.addRating:
                startActivity(new Intent(this, AddRating.class));
                break;
            case R.id.help:
                startActivity(new Intent(this, HelpMenu.class));
        }
        if (id == R.id.addRating) {

            return true;
        }
        return true;
    }

    //---------------------------------------------------------------------
    // RatingsListAdapter
    //---------------------------------------------------------------------
    private void populateUserRatingsListView() {
        // Create list of items
        //String[] myItems = {"blue", "green" ,"
        // black"};

        // build adapter
        ArrayAdapter<UserRating> adapter = new MyRatingsListAdapter();

        // configure the listView
        ListView listView = (ListView) findViewById(R.id.userRatingListView);

        listView.setAdapter(adapter);
    }

    private class MyRatingsListAdapter extends ArrayAdapter<UserRating> {
        public MyRatingsListAdapter() {
            super(MainMenu.this, R.layout.my_ratings_list_layout, myUserRatings);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.my_ratings_list_layout, parent, false);
            }

            // Find Rating we work with
            UserRating currentUserRating = myUserRatings.get(position); //<--- ???

            // convert date to string and store all data in nem UserRating instance
            CharSequence s = DateFormat.format("EEEE dd.MM.yyyy ", currentUserRating.getTimeStamp());
            String timeStamp = s.toString();

            // Title
            TextView titleTextView = (TextView) itemView.findViewById(R.id.ratingsTitleText);
            titleTextView.setMovementMethod(new ScrollingMovementMethod());
            titleTextView.setText(currentUserRating.getTitle());

            // Text
            TextView textTextView = (TextView) itemView.findViewById(R.id.ratingsMultilineText);
            textTextView.setMovementMethod(new ScrollingMovementMethod());
            textTextView.setText(currentUserRating.getText());

            // Timestamp
            TextView timeStampTextView = (TextView) itemView.findViewById(R.id.ratingsTimeStampText);
            timeStampTextView.setText(timeStamp);

            Float stars = Float.valueOf(currentUserRating.getStars());

            // rating
            RatingBar ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            ratingBar.setRating(stars);
            //Log.e("MyRoutesListAdapter.getView", "float stars=" + stars);
            ratingBar.setIsIndicator(true);


            // Fill the view
            return itemView;
        }
    }

    //---------------------------------------------------------------------
    // MyRoutesListAdapter
    //---------------------------------------------------------------------

    private void populateDailyRoutesListView() {
        // Create list of items
        //String[] myItems = {"blue", "green" ,"black"};

        // build adapter
        ArrayAdapter<DailyRoute> myDailyRoutesAdapter = new MyDailyRoutesListAdapter();

        // configure the listView
        ListView listView = (ListView) findViewById(R.id.dailyRoutesListView);

        listView.setAdapter(myDailyRoutesAdapter);
    }

    private class MyDailyRoutesListAdapter extends ArrayAdapter<DailyRoute> {
        public MyDailyRoutesListAdapter() {
            super(MainMenu.this, R.layout.my_daily_routes_list_layout, myDailyRoutes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.my_daily_routes_list_layout, parent, false);
            }

            // Find Rating we work with
            DailyRoute currentDailyRoute = myDailyRoutes.get(position); //<--- ???

            // Date
            TextView dateTextView = (TextView) itemView.findViewById(R.id.textVievMyRoutesDate);
            dateTextView.setText(currentDailyRoute.getDate());

            DecimalFormat formatDistance = new DecimalFormat("#0.00");

            // Walk distance
            TextView walkDistanceTextView = (TextView) itemView.findViewById(R.id.textVievMyRoutesDistanceWalk);
            walkDistanceTextView.setText(formatDistance.format(currentDailyRoute.getWalkDistance()) + " km");

            // ferry distance
            TextView ferryDistanceTextView = (TextView) itemView.findViewById(R.id.textVievMyRoutesDistanceFerry);
            ferryDistanceTextView.setText(formatDistance.format(currentDailyRoute.getFerryDistance()) + " km");

            // Car distance
            TextView carDistanceTextView = (TextView) itemView.findViewById(R.id.textVievMyRoutesDistanceCar);
            carDistanceTextView.setText(formatDistance.format(currentDailyRoute.getCarDistance()) + " km");

            // Train distance
            TextView trainDistanceTextView = (TextView) itemView.findViewById(R.id.textVievMyRoutesDistanceTrain);
            trainDistanceTextView.setText(formatDistance.format(currentDailyRoute.getTrainDistance()) + " km");

            // Progress Bar
            ProgressBar progressBar = (ProgressBar) itemView.findViewById(R.id.progressBarMyRoutes);
           // progressBar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP); //TODO Indicator
            progressBar.setProgress(currentDailyRoute.getSustainabilityIndicator());

            // Fill the view

            return itemView;
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


    @Override
    public void onDestroy() {

        stopService(new Intent(getApplicationContext(), SrvPositioning.class));
        stopService(new Intent(getApplicationContext(), DataPostTimer.class));
        super.onDestroy();

    }
}

