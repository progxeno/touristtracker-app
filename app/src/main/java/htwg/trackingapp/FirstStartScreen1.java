package htwg.trackingapp;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;


public class FirstStartScreen1 extends Activity {

    Toast toast;
    ImageButton imageButton;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_start_screen_1);


        toast = new Toast(getApplicationContext());
        imageButton = (ImageButton) findViewById(R.id.returnersCheck);
        context = getApplicationContext();

        if(AppPreferences.isSetReturner(context) && AppPreferences.isSetGender(context))
            setSavedButtons();
    }

    //---------------------------------------------------------------------
    // Set navigation buttons on click
    // Set gender and returner button on click
    //---------------------------------------------------------------------
    public void buttonOnClick(View v) {
        switch (v.getId()) {

            case R.id.forwardButton:
                if (AppPreferences.isSetReturner(context) && AppPreferences.isSetGender(context))
                    startActivity(new Intent(getApplicationContext(), FirstStartScreen2.class));
                else {
                    toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                    Toast.makeText(FirstStartScreen1.this, R.string.notPossible, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.returnersCheck:
                AppPreferences.setIsSetReturner(true, context);
                AppPreferences.setReturner(true, context);

                v.setBackgroundResource(R.drawable.image_button_clicked);
                imageButton = (ImageButton) findViewById(R.id.returnersCancel);
                imageButton.setBackgroundResource(R.drawable.image_button_style);
                break;

            case R.id.returnersCancel:
                AppPreferences.setIsSetReturner(true, context);
                AppPreferences.setReturner(false, context);

                v.setBackgroundResource(R.drawable.image_button_clicked);
                imageButton = (ImageButton) findViewById(R.id.returnersCheck);
                imageButton.setBackgroundResource(R.drawable.image_button_style);
                break;

            case R.id.genderWomen:
                AppPreferences.setIsSetGender(true, context);
                AppPreferences.setMaleGender(false, context);

                v.setBackgroundResource(R.drawable.image_button_clicked);
                imageButton = (ImageButton) findViewById(R.id.genderMen);
                imageButton.setBackgroundResource(R.drawable.image_button_style);
                break;

            case R.id.genderMen:
                AppPreferences.setIsSetGender(true, context);
                AppPreferences.setMaleGender(true, context);

                v.setBackgroundResource(R.drawable.image_button_clicked);
                imageButton = (ImageButton) findViewById(R.id.genderWomen);
                imageButton.setBackgroundResource(R.drawable.image_button_style);
                break;

            default:
                break;
        }
    }

    //---------------------------------------------------------------------
    // Disables back button
    //---------------------------------------------------------------------
    @Override
    public void onBackPressed() {
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        Toast.makeText(FirstStartScreen1.this, R.string.notPossible, Toast.LENGTH_SHORT).show();
    }

    //---------------------------------------------------------------------
    // Set saved buttons on return
    //---------------------------------------------------------------------
    private void setSavedButtons(){
        if(AppPreferences.isReturner(context)){
            imageButton = (ImageButton) findViewById(R.id.returnersCheck);
            imageButton.setBackgroundResource(R.drawable.image_button_clicked);
            imageButton = (ImageButton) findViewById(R.id.returnersCancel);
            imageButton.setBackgroundResource(R.drawable.image_button_style);
        }
        else{
            imageButton = (ImageButton) findViewById(R.id.returnersCheck);
            imageButton.setBackgroundResource(R.drawable.image_button_style);
            imageButton = (ImageButton) findViewById(R.id.returnersCancel);
            imageButton.setBackgroundResource(R.drawable.image_button_clicked);
        }
        if(AppPreferences.isMaleGender(context)){
            imageButton = (ImageButton) findViewById(R.id.genderWomen);
            imageButton.setBackgroundResource(R.drawable.image_button_style);
            imageButton = (ImageButton) findViewById(R.id.genderMen);
            imageButton.setBackgroundResource(R.drawable.image_button_clicked);
        }
        else{
            imageButton = (ImageButton) findViewById(R.id.genderWomen);
            imageButton.setBackgroundResource(R.drawable.image_button_clicked);
            imageButton = (ImageButton) findViewById(R.id.genderMen);
            imageButton.setBackgroundResource(R.drawable.image_button_style);
        }
    }
}
