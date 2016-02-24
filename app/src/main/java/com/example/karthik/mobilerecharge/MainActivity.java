package com.example.karthik.mobilerecharge;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.InputFilter;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText mobNumb,  amount, accountNumb;
   // EditText network;
    String sMobNumb, sNetwork, sAmount, sAccountNumb;
    private Button recharge;
    private final static String SMS_REQUEST_FORMAT_ICICI = "MTOPUP %s %s %s %s";
    Context parentActivity=MainActivity.this;
    Spinner network;
    private final static String SMS_SENT_INTENT = "in.ireff.android.SMS_SENT_INTENT";
    private final static String SMS_DEST_ICICI = "7795867230";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        network=(Spinner)findViewById(R.id.spinner);

        mobNumb = (EditText) findViewById(R.id.mobile_number);
       // network = (EditText) findViewById(R.id.network);
        amount = (EditText) findViewById(R.id.amount);
        accountNumb = (EditText) findViewById(R.id.account_number);
        spinnerCode();
         recharge = (Button) findViewById(R.id.recharge);
        recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendMessage();
                oldCode();

            }
        });

        Intent recIntent = new Intent(MainActivity.this, ReciverClass.class);
        getApplicationContext().sendBroadcast(recIntent);
    }

   private void  spinnerCode(){

       // Spinner Drop down elements
       List<String> categories = new ArrayList<String>();
       categories.add("AIRTEL");
       categories.add("AIRCEL");
       categories.add("VODOFONE");
       categories.add("IDEA");
       categories.add("BSNL");
       categories.add("TATADOCOMO");

       // Creating adapter for spinner
       ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

       // Drop down layout style - list view with radio button
       dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

       // attaching data adapter to spinner
       network.setAdapter(dataAdapter);
   }




   public  void oldCode(){
        if (!validateMobileNumber(mobNumb)) {
            return;
        }
        if (!validatePin(accountNumb)) {
            return;
        }

        final ProgressDialog requestProgress = new ProgressDialog(parentActivity);
        requestProgress.setIndeterminate(true);
        parentActivity.registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                parentActivity.unregisterReceiver(this);
                requestProgress.dismiss();
                String message = null;
                if (getResultCode() == Activity.RESULT_OK) {
                    message = "Recharge request sent to bank successfully. Please await SMS reply from bank.";
                } else {
                    message = "Failed to send SMS request to bank. Please try again later";
                }
                Toast.makeText(parentActivity, message, Toast.LENGTH_SHORT).show();
            }
        }, new IntentFilter(SMS_SENT_INTENT));
        // create SMS request
        String smsRequest = String.format(SMS_REQUEST_FORMAT_ICICI,
                mobNumb.getText().toString(), // mobile number
                network.getSelectedItem().toString(), // operator

                amount.getText().toString(), // amount
                accountNumb.getText().toString() // security code
        );
        SmsManager.getDefault().sendTextMessage(SMS_DEST_ICICI, null, smsRequest, PendingIntent.getBroadcast(parentActivity, 0, new Intent(SMS_SENT_INTENT), 0), null);
        requestProgress.setMessage("Sending recharge request via SMS to bank ...");
        requestProgress.show();
    }





    private void sendMessage() {


        sMobNumb = mobNumb.getText().toString();
        sNetwork = network.getSelectedItem().toString();
        sAmount = amount.getText().toString();


        sAccountNumb = accountNumb.getText().toString();

        String finalMessage = "MTOPUP"+" "+sMobNumb + " " + sNetwork + " " + sAmount + " " + sAccountNumb;
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("9222208888", null, finalMessage, null, null);

    }

    private boolean validateMobileNumber(EditText mobileNumber) {
        String mobileNumberStr = mobNumb.getText().toString();
        if (mobileNumberStr.length() != 10
                || mobileNumberStr.startsWith("0")
                || !(mobileNumberStr.startsWith("7") || mobileNumberStr.startsWith("8") || mobileNumberStr.startsWith("9"))) {
            mobNumb.setError("Please enter valid 10 digit mobile number");
            return false;
        } else {
            return true;
        }
    }

    private boolean validatePin(EditText pin) {
        if (accountNumb.getText().toString().length() != 6) {
            accountNumb.setError("Please enter last 6 digits of account number");
            return false;
        } else {
            return true;
        }
    }
}
