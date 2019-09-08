package com.example.connectme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class NumberReceiver extends BroadcastReceiver {
    private final static String TAG = "ConnectMe";
    private DatabaseReference Ref;
    @Override
    public void onReceive(Context context, Intent intent) {

        Ref = FirebaseDatabase.getInstance().getReference();
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if(state.equals(TelephonyManager.EXTRA_STATE_RINGING))
        {
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            String name = getContactName(number,context);
            Toast.makeText(context, "RINGIN INCOMING CALL from "+ name +"(" + number +")", Toast.LENGTH_LONG).show();
            Log.d(TAG, "onReceive:ringing ");
            Map<String,String > contact = new HashMap<>();
            contact.put("name" , name);
            contact.put("number" , number);
            Ref.child("IncomingNumber").setValue(contact).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful())
                    {
                        Log.d(TAG, "onComplete: Error");
                    }else
                    {
                        Log.d(TAG, "onComplete: Complete");
                    }
                }
            });

        }

    }

    public String getContactName(final String phoneNumber, Context context)
    {
        Uri uri= Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName="";

        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);

        if (cursor != null) {
            if(cursor.moveToFirst()) {
                contactName=cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }

}
