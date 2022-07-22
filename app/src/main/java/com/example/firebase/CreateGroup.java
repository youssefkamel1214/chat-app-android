package com.example.firebase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.firebase.databinding.ActivityCreateGroupBinding;

public class CreateGroup extends AppCompatActivity {
ActivityCreateGroupBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCreateGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}