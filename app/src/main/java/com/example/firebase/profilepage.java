package com.example.firebase;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.firebase.databinding.ActivityProfilepageBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import controllers.BackExecutor;
import controllers.Uploaduserdata;

public class profilepage extends AppCompatActivity {
ActivityProfilepageBinding binding;
    private FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
    String name,email,imageurl;
    private byte[] imagesbytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityProfilepageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setvisbandusrname();
        getdata();
        setdata();
        binding.usernamebutton.setOnClickListener(view -> {
            binding.username.setEnabled(true);
            binding.username.requestFocus();
        });
        handle_image_pickng_button();
    }
    private void handle_image_pickng_button() {
        binding.pickimagefromcamera.setOnClickListener(view -> {
            if(ContextCompat.checkSelfPermission(profilepage.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(profilepage.this, new String[]{
                        Manifest.permission.CAMERA
                },100);
            }
            if(ContextCompat.checkSelfPermission(profilepage.this, Manifest.permission.CAMERA)!=
                    PackageManager.PERMISSION_GRANTED)
                return;
            Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 100);
        });
        binding.pickimagefromgallery.setOnClickListener(view->{
            Intent intent= new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent,
                    "Select Picture"),  1);

        });
        binding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.loading.setVisibility(View.VISIBLE);
                BackExecutor backExecutor=new BackExecutor();
                Uploaduserdata uploaduserdata=new Uploaduserdata(firebaseAuth.getCurrentUser(),profilepage.this,imagesbytes,binding.username.getText().toString(),backExecutor);
                uploaduserdata.executeOnExecutor(backExecutor);
            }
        });
    }

    private void setdata() {
        Glide.with(this).load(imageurl).placeholder(R.drawable.chat).into(binding.profileimage);
        binding.username.setText(name);
        binding.email.setText(email);
    }

    private void getdata() {
        name=getIntent().getStringExtra("username");
        email=getIntent().getStringExtra("email");
        imageurl=getIntent().getStringExtra("imageurl");
    }

    private void setvisbandusrname() {
        binding.appbar.circleCenter.setVisibility(View.GONE);
        binding.appbar.menumore.setVisibility(View.GONE);
        binding.appbar.searchBar.setVisibility(View.GONE);
        binding.update.setVisibility(View.GONE);
        binding.loading.setVisibility(View.GONE);
        binding.appbar.appbartitle.setText("Profile");
        binding.username.setEnabled(false);
        binding.username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                  if(!editable.toString().equals(email))
                      binding.update.setVisibility(View.VISIBLE);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            binding.update.setVisibility(View.VISIBLE);
            Bitmap bitmap = null;
            Log.d(TAG, "anahena"+String.valueOf( requestCode));
            if(requestCode==1&&data.getData()!=null) {
                try {
                    bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(requestCode==100){
                bitmap=(Bitmap) data.getExtras().get("data");

            }
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float scaleWidth = ((float) 150) / width;
            float scaleHeight = ((float) 150) / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);

            // "RECREATE" THE NEW BITMAP
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
            bitmap.recycle();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int qauility=50;
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            while (baos.toByteArray().length>9*1024){
                baos=new ByteArrayOutputStream();
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, qauility, baos);
                qauility-=5;
                if(qauility<=5)break;
            }
            imagesbytes = baos.toByteArray();
            binding.profileimage.setImageBitmap(resizedBitmap);
        }
    }

    public void finishloading() {
        finish();
    }
}