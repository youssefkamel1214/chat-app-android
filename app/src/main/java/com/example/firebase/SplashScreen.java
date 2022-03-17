package com.example.firebase;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.firebase.databinding.ActivitySplashScreenBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {
    ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

            FirebaseApp.initializeApp(SplashScreen.this);
            FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
             if(firebaseAuth.getCurrentUser()==null){
                 Intent intent=new Intent(SplashScreen.this,Auth_screen.class);
                 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                 startActivity(intent);
                 SplashScreen.this.finish();
             }
             else {
                 Intent intent=new Intent(SplashScreen.this,MainActivity.class);
                 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                 startActivity(intent);
                 SplashScreen.this.finish();
             }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}