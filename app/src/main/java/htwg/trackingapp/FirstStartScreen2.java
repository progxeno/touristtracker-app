package htwg.trackingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

public class FirstStartScreen2 extends Activity {

    private static final int MIN_WORDLENGTH = 3;

    Toast toast;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_start_screen_2);

        toast = new Toast(getApplicationContext());
        context = getApplicationContext();

        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        numberPicker.setMaxValue(2014);
        numberPicker.setMinValue(1900);
        numberPicker.setValue(1980);

        if(AppPreferences.getZipcode(context) != null) {
            setSavedUserData();
        }
    }

    //---------------------------------------------------------------------
    // Set navigation buttons on click
    //---------------------------------------------------------------------
    public void buttonOnClick(View v) {
        switch (v.getId()) {
            case R.id.forwardButton:
                if (saveValues(0)) {
                    startActivity(new Intent(getApplicationContext(), FirstStartScreen3.class));
                }
                break;
            case R.id.backButton:
                saveValues(1);
                startActivity(new Intent(getApplicationContext(), FirstStartScreen1.class));
                break;
            default:
                break;
        }
    }

    //---------------------------------------------------------------------
    // Get country spinner String
    //---------------------------------------------------------------------
    private String getSpinnerString(Spinner countrySpinner){
        switch (countrySpinner.getSelectedItemPosition()){
            case 0:
                return "D";
            case 1:
                return "A";
            case 2:
                return "CH";
            case 3:
                return "F";
            case 4:
                return "I";
            default:
                return "other";
        }
    }

    //---------------------------------------------------------------------
    // Get country spinner Int
    //---------------------------------------------------------------------
    private int getSpinnerInt(){
        if (AppPreferences.getCountry(context).equals("D"))
            return 0;
        else if (AppPreferences.getCountry(context).equals("A"))
            return 1;
        else if (AppPreferences.getCountry(context).equals("CH"))
            return 2;
        else if (AppPreferences.getCountry(context).equals("F"))
            return 3;
        else if (AppPreferences.getCountry(context).equals("I"))
            return 4;
        else
            return 5;
    }

    //---------------------------------------------------------------------
    // Save values in preferences
    //---------------------------------------------------------------------
    private boolean saveValues(int i){
        EditText eMailEditText = (EditText) findViewById(R.id.eMailEditText);
        EditText zipCodeEditText = (EditText) findViewById(R.id.zipCodeEditText);
        NumberPicker yearOfBirthNumberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        Spinner countrySpinner = (Spinner) findViewById(R.id.countrySpinner);

        if(eMailEditText.getText().toString().length() > MIN_WORDLENGTH && zipCodeEditText.getText().toString().length() > MIN_WORDLENGTH ) {
            AppPreferences.setEmail(eMailEditText.getText().toString(), context);
            AppPreferences.setZipcode(zipCodeEditText.getText().toString(), context);
            AppPreferences.setYear(yearOfBirthNumberPicker.getValue(), context);
            AppPreferences.setCountry(getSpinnerString(countrySpinner),context);
            AppPreferences.setId(Integer.toString(eMailEditText.hashCode()),context);
            return true;
        }
        else if (i == 0){
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
            Toast.makeText(FirstStartScreen2.this, R.string.insertNotComplete, Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (i == 1){
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
            Toast.makeText(FirstStartScreen2.this, R.string.dataNotSaved, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    //---------------------------------------------------------------------
    // Set saved spinner, datePicker and textEdit on return
    //---------------------------------------------------------------------
    private void setSavedUserData(){
        EditText eMailEditText = (EditText) findViewById(R.id.eMailEditText);
        EditText zipCodeEditText = (EditText) findViewById(R.id.zipCodeEditText);
        NumberPicker yearOfBirthNumberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        Spinner countrySpinner = (Spinner) findViewById(R.id.countrySpinner);

        eMailEditText.setText(AppPreferences.getEmail(context));
        zipCodeEditText.setText(AppPreferences.getZipcode(context));
        yearOfBirthNumberPicker.setValue(AppPreferences.getYear(context));
        countrySpinner.setSelection(getSpinnerInt());
    }
}
