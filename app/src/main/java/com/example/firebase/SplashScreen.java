package com.example.firebase;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.firebase.databinding.ActivitySplashScreenBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {
    ActivitySplashScreenBinding binding;
    int good=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Animation animation= AnimationUtils.loadAnimation(this,R.anim.splashanimation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                     movetopage();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        binding.middleimage.startAnimation(animation);
    }

    private void movetopage() {
        FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            Intent intent=new Intent(SplashScreen.this,Auth_screen.class);
            startActivity(intent);
            this.finish();
        }
        else {
            Intent intent=new Intent(SplashScreen.this,MainActivity.class);
            startActivity(intent);
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}