package htwg.trackingapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


public class AddRating extends Activity {

    private static final int MIN_WORDLENGTH = 1;

    boolean entriesCorrect = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rating);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Insert the back Button in the Action bar
        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.drawable.ic_action_star);

        EditText text = (EditText) findViewById(R.id.newRatingMultilineEditText);
        text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_rating, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.e("onOptionsItemSelected", "bin da" );
        int id = item.getItemId();
        if (id == R.id.action_save) {
            if(finishEditingNewUserRating())
                startActivity(new Intent(getApplicationContext(), MainMenu.class));
            else {
                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                Toast.makeText(AddRating.this, R.string.incorrectInput, Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }


    //--------------------------------------------------------------------
    // Save user rating in SQLite Database
    //--------------------------------------------------------------------
    public boolean finishEditingNewUserRating() {
        EditText userRatingTitle = (EditText) findViewById(R.id.newRatingTitleEditText);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.newRatingRatingBar);
        EditText userRatingText = (EditText) findViewById(R.id.newRatingMultilineEditText);

        if (userRatingTitle.getText().toString().length() > MIN_WORDLENGTH && userRatingText.getText().toString().length() > MIN_WORDLENGTH) {
            UserRatingSQLiteHelper db = new UserRatingSQLiteHelper(this);
            db.addUserRating(new UserRating(userRatingTitle.getText().toString(), userRatingText.getText().toString(), (float)ratingBar.getRating()));
            return true;
        }
        return false;
    }
}