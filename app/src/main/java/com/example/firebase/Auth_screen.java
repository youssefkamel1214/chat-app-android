package com.example.firebase;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.firebase.databinding.ActivityAuthScreenBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.util.Executors;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

import controllers.BackExecutor;
import controllers.Uploaduserdata;

public class Auth_screen extends AppCompatActivity {
    private  FirebaseAuth mAuth;
    private ActivityAuthScreenBinding binding;
    private boolean login=true;
    private byte[] imagesbytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAuthScreenBinding.inflate(getLayoutInflater());
        mAuth = FirebaseAuth.getInstance();
        setContentView(binding.getRoot());
        Toolbar myToolbar =  binding.included.appbar;
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();
        binding.included.appbartitle.setText("wwf");
        binding.username.setVisibility(View.GONE);
        binding.authLoading.setVisibility(View.GONE);
        binding.imagegroup.setVisibility(View.GONE);
        make_listhen_to_icon();
        handle_switching();
        handle_image_pickng_button();
        binding.sumbitbutt.setOnClickListener(view -> {
            binding.authLoading.setVisibility(View.VISIBLE);
            binding.sumbitbutt.setVisibility(View.GONE);
           if (login==true)
                 handle_sumbiting_login();
           else
                handle_sumbiting_signup();

        });

    }

    private void handle_image_pickng_button() {
        binding.pickimagefromcamera.setOnClickListener(view -> {
       if(ContextCompat.checkSelfPermission(Auth_screen.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
       {
           ActivityCompat.requestPermissions(Auth_screen.this, new String[]{
                   Manifest.permission.CAMERA
           },100);
       }
       if(ContextCompat.checkSelfPermission(Auth_screen.this, Manifest.permission.CAMERA)!=
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {

            Bitmap bitmap = null;
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
            binding.userimage.setImageBitmap(resizedBitmap);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    public void finishloading(Exception exception){
        binding.authLoading.setVisibility(View.GONE);
        binding.sumbitbutt.setVisibility(View.VISIBLE);
        System.err.println("execptrion="+exception);
        if (exception==null) {
            Intent intent=new Intent(Auth_screen.this,MainActivity.class);
            startActivity(intent);
            this.finish();
        }else
            Toast.makeText(Auth_screen.this,exception.getMessage(), Toast.LENGTH_LONG).show();

    }
    private void handle_sumbiting_signup() {
        if(imagesbytes==null){
            Toast.makeText(Auth_screen.this, "you should pick image or poggy man will hurt you", Toast.LENGTH_LONG)
                    .show();
            binding.authLoading.setVisibility(View.GONE);
            binding.sumbitbutt.setVisibility(View.VISIBLE);
            return;
        }
        if(!check_inputs()){
            binding.authLoading.setVisibility(View.GONE);
            binding.sumbitbutt.setVisibility(View.VISIBLE);
            return;
        }
        mAuth.createUserWithEmailAndPassword(binding.Email.getText().toString(),
                binding.password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            BackExecutor backExecutor=new BackExecutor();
                            Uploaduserdata uploaduserdata= new
                                    Uploaduserdata(task.getResult().getUser(),Auth_screen.this,
                                    imagesbytes,binding.username.getText().toString(), backExecutor);
                            uploaduserdata.executeOnExecutor(backExecutor);
                      } else {
                            finishloading(task.getException());
                            if( task.getException().getMessage().indexOf("email")>-1)
                                binding.Email.setError(task.getException().getMessage());
                           else
                               binding.password.setError(task.getException().getMessage());
                        }
                    }
                });
    }


    private void handle_sumbiting_login() {
        if(!check_inputs()){
            binding.authLoading.setVisibility(View.GONE);
            binding.sumbitbutt.setVisibility(View.VISIBLE);
            return;
        }
        mAuth.signInWithEmailAndPassword(binding.Email.getText().toString(),
                binding.password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            finishloading(null);
                        } else {
                            finishloading(task.getException());
                            if(! (task.getException().getMessage().indexOf("password")>-1))
                                binding.Email.setError(task.getException().getMessage());
                            else
                                binding.password.setError(task.getException().getMessage());
                        }
                    }
                });
    }
    private boolean check_inputs() {
       String regex="^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
       Pattern p=Pattern.compile(regex);
        if(!p.matcher(binding.Email.getText()).matches()){
            binding.Email.setError("not good format");
            return  false;
        }
         regex="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,20}$";
         p=Pattern.compile(regex);
        if(!p.matcher(binding.password.getText()).matches()){
             binding.password.setError("sould has 1 big char and 1 small char and 1 dig and be 8 chars");
            return false;
        }
         regex="^[A-Za-z \\s]+$";
         p=Pattern.compile(regex);
        if(!login &&! p.matcher(binding.username.getText()).matches()) {
            binding.username.setError("should be only charaters");
            return false;
        }
        return true;
    }

    private void handle_switching() {
        binding.swithcState.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (login==true){
                            binding.username.setVisibility(View.VISIBLE);
                            binding.swithcState.setText("change to Log in");
                            binding.sumbitbutt.setText("Sign up");
                            binding.imagegroup.setVisibility(View.VISIBLE);
                        }
                        else {
                            binding.username.setVisibility(View.GONE);
                            binding.imagegroup.setVisibility(View.GONE);
                            binding.swithcState.setText("change to Sign up");
                            binding.sumbitbutt.setText("Log in");
                        }
                        login^=true;
                    }
                }
        );
    }

    @SuppressLint("ClickableViewAccessibility")
    private void make_listhen_to_icon() {
        binding.password.setOnTouchListener((view, event) -> {
            final int DRAWABLE_LEFT = 0;
            if(event.getAction() == MotionEvent.ACTION_UP) {
                int[] location = new int[2];
                binding.password.getLocationOnScreen(location);

                if(event.getRawX()>location[0]+10&&
                        event.getRawX() <= (location[0]+20+binding.password.
                                getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width()))
                {
                    if( binding.password.getInputType()==129) {
                        binding.password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                       binding.password.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_baseline_visibility_off_24, 0, 0, 0);
                    }
                    else {
                        binding.password.setInputType(129);
                        binding.password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_visibility_24,0,0,0);
                    }
                    return true;
                }
            }
            return false;

        });
    }
}
