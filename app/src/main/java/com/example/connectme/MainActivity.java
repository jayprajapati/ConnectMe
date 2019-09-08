package com.example.connectme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {


    private FirebaseDatabase firebaseDatabase;
    private EditText InputData;
    private Button btnSync;
    private FirebaseFirestore firebaseFirestore;
    private String otp;
    private DatabaseReference dbRef;
    private String id;
    private TextView textViewOTP;
    private ImageView imAdd;
    private final static  String TAG = "ConnectMe";
    private final String  CHANNEL_ID = "personal_noti";
    private final int NOTIFICATION_ID = 001;
    private ImageView imDownload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseDatabase = FirebaseDatabase.getInstance();
        dbRef= firebaseDatabase.getReference();
        InputData = findViewById(R.id.InputData);
        btnSync = findViewById(R.id.btnSync);
        firebaseFirestore = FirebaseFirestore.getInstance();
        textViewOTP = findViewById(R.id.tvOTP);
        imAdd = findViewById(R.id.ivAdd);
        imAdd.setVisibility(View.INVISIBLE);
        imDownload = findViewById(R.id.imageView2);


        generateOTP();
        textViewOTP.setText(id);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_0_1)
        {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,"Name of Channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("DESC OF CHANNEL");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);

        }
        findViewById(R.id.imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Started");
                //displayNotification();
                Log.d(TAG, "onClick: stopped");
                generateOTP();
                textViewOTP.setText(id);
            }
        });

        imAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSync.setVisibility(View.VISIBLE);
                InputData.setVisibility(View.VISIBLE);
                findViewById(R.id.imageView).setVisibility(View.VISIBLE);
                generateOTP();
                textViewOTP.setText(id);
            }
        });


        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if(InputData.getText().toString()=="" || TextUtils.isEmpty(InputData.getText().toString()))
                    {
                        InputData.setError("Cant be null");
                    }else
                    {
                        dbRef.child(id).setValue(InputData.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(MainActivity.this, "Check Notification for OTP.", Toast.LENGTH_SHORT).show();
                                    displayNotification();
                                    InputData.setText("");
                                    //generateOTP();
                                    //textViewOTP.setText(id);
                                    imAdd.setVisibility(View.VISIBLE);
                                    btnSync.setVisibility(View.INVISIBLE);
                                    InputData.setVisibility(View.INVISIBLE);
                                    findViewById(R.id.imageView).setVisibility(View.INVISIBLE);

                                }else
                                {
                                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

            }
        });

        ValueEventListener postListner = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        dbRef.addValueEventListener(postListner);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void generateOTP()
    {
        id  = String.valueOf((int)(Math.random()*900)+100);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.hasChild(id))
                    {
                        generateOTP();
                        Log.d(TAG, "onDataChange: OTP conflict with firebase server");

                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void displayNotification()
    {
        Log.d(TAG, "displayNotification: Function Started");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID);
        builder.setSmallIcon(R.drawable.googleg_standard_color_18);
        builder.setContentTitle("ConnectMe");
        builder.setContentText("Your OTP is " + id);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
        Log.d(TAG, "displayNotification: complete");
    }




}
