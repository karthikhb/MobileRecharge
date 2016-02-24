package com.example.karthik.mobilerecharge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.List;

/**
 * Created by karthik on 18-Feb-16.
 */
public class ReciverClass extends BroadcastReceiver {
    private Firebase firebase;

    @Override
    public void onReceive(Context context, Intent intent) {
        Firebase.setAndroidContext(context);
        firebase = new Firebase("https://glowing-fire-1131.firebaseio.com/RechargeHistroy");

        Bundle bundle = intent.getExtras();
        SmsMessage[] recievedMsgs = null;
        String str = "";
        Object[] pdus;
        if (bundle != null) {
             pdus = (Object[]) bundle.get("pdus");
            recievedMsgs = new SmsMessage[pdus.length];
            for (int i = 0; i < pdus.length; i++) {
                recievedMsgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                str +=recievedMsgs[i].getMessageBody().toString();

                Firebase newFirebase = firebase.push();


                newFirebase.child(recievedMsgs[i].getPseudoSubject()).setValue(str);
            }
        }
    }
}
