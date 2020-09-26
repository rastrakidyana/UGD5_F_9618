package com.maderastra.ugd5_f_9618;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText emailI, passI;
    private Button registerBtn, loginBtn;
    private FirebaseAuth mAuth;
    private String CHANNEL_ID = "Channel 2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailI = findViewById(R.id.emailInp);
        passI = findViewById(R.id.passInp);
        registerBtn = findViewById(R.id.btn_register);
        loginBtn = findViewById(R.id.btn_login);
        mAuth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate(emailI, passI) ){
                    String email = emailI.getText().toString().trim();
                    String pass = passI.getText().toString().trim();
                    mAuth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Toast.makeText(MainActivity.this, "Authentication Success", Toast.LENGTH_SHORT).show();
                                        emailI.setText("");
                                        passI.setText("");
                                        findViewById(R.id.mainLayout).requestFocus();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Authentication failed",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    Toast.makeText(MainActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate(emailI, passI) ){
                    String email = emailI.getText().toString().trim();
                    String pass = passI.getText().toString().trim();
                    mAuth.signInWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Toast.makeText(MainActivity.this, "Login Success",
                                                Toast.LENGTH_SHORT).show();
                                        createNotificationChannel();
                                        addNotification();
                                        Intent goAfter = new Intent(MainActivity.this, AfterLogin.class);
                                        startActivity(goAfter);
                                        finish();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Login Failed",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean validate(TextInputEditText emailI, TextInputEditText passI){
        String email = emailI.getText().toString().trim();
        String pass = passI.getText().toString().trim();

        if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches() && !pass.isEmpty() && (pass.length() > 5) ){
            return true;
        }else{
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Toast.makeText(this, "Email Invalid", Toast.LENGTH_SHORT).show();
            }

            if(pass.isEmpty()){
                Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();
            }else if(pass.length() < 6){
                Toast.makeText(this, "Password Too Short", Toast.LENGTH_SHORT).show();
            }

            return false;
        }
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel 2";
            String description = "This is Channel 2";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }

    private void addNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Hello")
                .setContentText("Welcome Back!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent notificationIntent = new Intent(this, AfterLogin.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

}