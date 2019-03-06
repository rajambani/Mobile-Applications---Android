package com.example.temperatureconverter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private EditText inputValue;
    private RadioGroup selectedRadioGroup;
    private RadioButton selectedRadioButton;
    private TextView outputValue;
    private TextView historyTextView;
    private StringBuffer resultHistory;
    private int selectedRadioId;
    private TextView inputTextView;
    private TextView outputTextView;
    private String inputVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultHistory = new StringBuffer();

        selectedRadioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        selectedRadioId = selectedRadioGroup.getCheckedRadioButtonId();
        selectedRadioButton = (RadioButton) findViewById(selectedRadioId);

        inputTextView = (TextView) findViewById(R.id.inputTextView);
        outputTextView = (TextView) findViewById(R.id.outputTextView);

        inputValue = (EditText) findViewById(R.id.inputEditText);
        outputValue = (TextView) findViewById(R.id.ResultTextView);

        historyTextView = (TextView) findViewById(R.id.historyValuesTextView);


    }

    //This method is to take care of changes required when the radio button is clicked.
    public void onRadioClick(View v)
    {
        //Toast.makeText(this, "Test 1", Toast.LENGTH_SHORT).show();

        selectedRadioId = selectedRadioGroup.getCheckedRadioButtonId();
        selectedRadioButton = (RadioButton) findViewById(selectedRadioId);

        if(selectedRadioButton.getText().toString().equalsIgnoreCase("Fahrenheit to Celsius")) //for Fahrenheit to Celsius
        {
            inputTextView.setText("Fahrenheit Degrees: ");
            outputTextView.setText("Celsius Degrees: ");
        }
        else
        {
            inputTextView.setText("Celsius Degrees: ");
            outputTextView.setText("Fahrenheit Degrees: ");
        }
    }

    public void onClickConvert(View v)
    {
        inputVal = inputValue.getText().toString();
        selectedRadioId = selectedRadioGroup.getCheckedRadioButtonId();
        selectedRadioButton = (RadioButton) findViewById(selectedRadioId);

        //set input field to empty string.
        inputValue.setText("");

        if(inputVal.equalsIgnoreCase("") || inputVal.equalsIgnoreCase("-"))
        {
            return;
        }
        Double input = Double.valueOf(inputVal).doubleValue();
        Double result;

        Log.d(TAG, "onClickConvert: " + selectedRadioId);
        if(selectedRadioButton.getText().toString().equalsIgnoreCase("Fahrenheit to Celsius")) //for Fahrenheit to Celsius
        {
            result = (input - 32) / 1.8;
            //convert result to 1 decimal point
            result = Math.round(result * 10.0) / 10.0;
            input = Math.round(input * 10.0) / 10.0;
            resultHistory.insert(0,input.toString() + " °F --> " + result.toString() + " °C" + "\n");
        }
        else
        {
            result = (input * 1.8) + 32;
            //convert result to 1 decimal point
            result = Math.round(result*10.0)/10.0;
            input = Math.round(input * 10.0) / 10.0;
            resultHistory.insert(0,input.toString() + " °C --> " + result.toString() + " °F" + "\n");
        }

        //round result
        //DecimalFormat df = new DecimalFormat("###.##");
        //df.format(result);
        outputValue.setText(result.toString());

        //This will enable scrolling in the history text view.
        historyTextView.setMovementMethod(new ScrollingMovementMethod());
        historyTextView.setText(resultHistory.toString());
    }

    public void onClickClear(View v)
    {
        historyTextView.setText("");
        resultHistory = new StringBuffer();
        outputValue.setText("");
    }

    //Below method is to save instance of the program to preserve data while rotation

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Log.d(TAG, "onSaveInstanceState: Starting saving data...");
        //if(!(inputValue.getText().toString().equalsIgnoreCase("")))
        //outState.putString("selectedRadio", Integer.valueOf(selectedRadioId + "").toString());
        outState.putString("input",  inputVal = inputValue.getText().toString());
        outState.putString("output", outputValue.getText().toString());
        outState.putString("history", historyTextView.getText().toString());

        //set text view
        outState.putString("inputTextView", inputTextView.getText().toString());
        outState.putString("outputTextView", outputTextView.getText().toString());

        super.onSaveInstanceState(outState);

        Log.d(TAG, "onSaveInstanceState: Successfully saved session");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.d(TAG, "onRestoreInstanceState: Retrieving session data");
        super.onRestoreInstanceState(savedInstanceState);

        //outState.putString("selectedRadio", Integer.valueOf(selectedRadioId + "").toString());
        inputValue.setText(savedInstanceState.getString("input"));
        outputValue.setText(savedInstanceState.getString("output"));
        historyTextView.setText(savedInstanceState.getString("history"));

        //set the history string buffer to appropriate values.
        resultHistory.insert(0,historyTextView.getText().toString());
        //outState.putString("history", historyTextView.getText().toString());

        //retrieve text view values for input and output
        inputTextView.setText(savedInstanceState.getString("inputTextView"));
        outputTextView.setText(savedInstanceState.getString("outputTextView"));


        Log.d(TAG, "onRestoreInstanceState: successfully retrieved data");
    }
}
