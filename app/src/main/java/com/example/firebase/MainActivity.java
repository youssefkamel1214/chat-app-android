package com.example.firebase;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.firebase.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;

import java.util.ArrayList;

import controllers.BackExecutor;
import models.User;

public class MainActivity extends AppCompatActivity  {
    private ActivityMainBinding binding;
    private  FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore =FirebaseFirestore.getInstance();
    private User user;
    private ListenerRegistration ls;
    private UsersFragment usersFragment;
    private ChannelsFragment channelsFragment;
    private boolean searchmod=false;
    private TextWatcher textWatcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_splash_screen);
        usersFragment=(UsersFragment)
        getSupportFragmentManager().findFragmentById(R.id.users_frag);
        channelsFragment=(ChannelsFragment)
        getSupportFragmentManager().findFragmentById(R.id.channel_frag);
        popupmenuinalize();
        handling_listened_user_update();
        Listhen_searchbar_clicked();
        getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager()
                .findFragmentById(R.id.users_frag)).commit();
        handleimageclick();
    }

    private void handleimageclick() {
        binding.appbar.userimageappbar.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivity.this,profilepage.class);
            intent.putExtra("username", user.getUserName());
            intent.putExtra("email", user.getEmail());
            intent.putExtra("imageurl", user.getImageUrl());
            startActivity(intent);
        });
    }

    private void popupmenuinalize() {
        binding.appbar.menumore.setOnClickListener(view -> {
                  PopupMenu popupMenu=new PopupMenu(MainActivity.this, view);
                  popupMenu.inflate(R.menu.popmenu);
                  popupMenu.setOnMenuItemClickListener(menuItem -> {
                        if(menuItem.getItemId()==R.id.logout){
                            Intent intent=new Intent(MainActivity.this,Auth_screen.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                            startActivity(intent);
                            finish();
                            firebaseAuth.signOut();
                        }
                        else if(menuItem.getItemId()==R.id.group)
                        {
                            Intent intent=new Intent(MainActivity.this,CreateGroup.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                            startActivity(intent);
                        }
                        return false;
                  });
                  popupMenu.setForceShowIcon(true);
                  popupMenu.show();

        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        searchmod^=true;
        changetitlebar();
    }

    private void Listhen_searchbar_clicked() {
        binding.appbar.searchBar.setOnClickListener(view -> {
            FragmentManager manager=this.getSupportFragmentManager();
            if(!manager.findFragmentById(R.id.channel_frag).isHidden())
                manager.beginTransaction().hide(manager.findFragmentById(R.id.channel_frag)).show(manager.findFragmentById(R.id.users_frag)).
                        addToBackStack(null).commit();
            else
                manager.popBackStack();
            searchmod^=true;
            changetitlebar();
        });
    }

    private void changetitlebar() {
            if(searchmod) {
                binding.appbar.appbartitle.setText("");
                binding.appbar.appbartitle.setInputType(InputType.TYPE_CLASS_TEXT);
                binding.appbar.appbartitle.setEnabled(true);
                textWatcher=new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        usersFragment.setSearcher(editable.toString());
                    }
                };
                binding.appbar.appbartitle.addTextChangedListener(textWatcher);

            }else {

                binding.appbar.appbartitle.setText(user.getUserName());
                binding.appbar.appbartitle.setEnabled(false);
                binding.appbar.appbartitle.setInputType(InputType.TYPE_NULL);
                binding.appbar.appbartitle.removeTextChangedListener(textWatcher);
                usersFragment.setSearcher("");
            }
    }

    private void handling_listened_user_update() {
         ls= firebaseFirestore.collection("users").
            document(firebaseAuth.getCurrentUser().getUid()).
                addSnapshotListener(new BackExecutor(), MetadataChanges.INCLUDE,
                        (value, error) -> {
                               if (error!=null){
                                   runOnUiThread(() -> Toast.makeText(MainActivity.this,
                                           error.getMessage(), Toast.LENGTH_LONG).show());
                                   return;
                               }
                               else {
                                   runOnUiThread(() -> {
                                       setContentView(binding.getRoot());
                                       user=new User(value.getId(), (String) value.get("email"),
                                               (String) value.get("username"),
                                               (String) value.get("imageurl"),
                                               (ArrayList<String>)value.get("users"),
                                               (ArrayList<String>)value.get("channels") );
                                       if(usersFragment!=null)
                                       usersFragment.setUser(user);
                                       Glide.with(this).load(user.getImageUrl()).placeholder(R.drawable.chat).into(binding.appbar.userimageappbar);
                                       if(searchmod==false)
                                       binding.appbar.appbartitle.
                                               setText(user.getUserName());

                                   });
                               }
                        });

    }

    @Override
    protected void onDestroy() {
        usersFragment=null;
        channelsFragment=null;
        ls.remove();
        ls=null;
        firebaseAuth=null;
        super.onDestroy();
    }

}