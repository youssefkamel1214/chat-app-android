package com.example.firebase;
import androidx.annotation.NonNull;
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
import android.widget.Switch;
import android.widget.Toast;
import com.example.firebase.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;

import java.util.ArrayList;

import controllers.BackExecutor;
import controllers.NetImage;
import models.User;

public class MainActivity extends AppCompatActivity  {
    private ActivityMainBinding binding;
    private  FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore =FirebaseFirestore.getInstance();
    private NetImage netImage=null;
    private User user;
    private ListenerRegistration ls;
    private UsersFragment usersFragment;
    private boolean searchmod=false;
    private TextWatcher textWatcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_splash_screen);
        usersFragment=(UsersFragment)
        getSupportFragmentManager().findFragmentById(R.id.users_frag);
        binding.appbar.menumore.setOnClickListener(view -> {
                  PopupMenu popupMenu=new PopupMenu(MainActivity.this, view);
                  popupMenu.inflate(R.menu.popmenu);
                  popupMenu.show();

        });
        handling_listened_user_update();
        Listhen_searchbar_clicked();
        getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager()
                .findFragmentById(R.id.users_frag)).commit();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.logout){
            Intent intent=new Intent(MainActivity.this,Auth_screen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            startActivity(intent);
            this.finish();
            firebaseAuth.signOut();
        }
        return super.onOptionsItemSelected(item);
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
            if(searchmod) {
                binding.appbar.appbartitle.setText("");
                binding.appbar.appbartitle.setInputType(InputType.TYPE_CLASS_TEXT);
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
                binding.appbar.appbartitle.setInputType(InputType.TYPE_NULL);
                binding.appbar.appbartitle.removeTextChangedListener(textWatcher);
                usersFragment.setSearcher("");
            }
        });
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
                                       usersFragment.setUser(user);
                                       if(netImage!=null)
                                           return;
                                       netImage = new NetImage(user.getImageUrl(),
                                               binding.appbar.userimageappbar);
                                       netImage.executeOnExecutor(new BackExecutor());
                                       binding.appbar.appbartitle.
                                               setText(user.getUserName());
                                   });
                               }
                        });

    }

    @Override
    protected void onDestroy() {
        ls.remove();
        ls=null;
        super.onDestroy();
    }

}