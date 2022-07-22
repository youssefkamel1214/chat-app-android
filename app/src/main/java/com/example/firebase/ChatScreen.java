package com.example.firebase;

import static com.google.firebase.firestore.DocumentChange.Type.ADDED;
import static com.google.firebase.firestore.DocumentChange.Type.MODIFIED;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.firebase.databinding.ActivityChatScreenBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.util.Executors;

import org.chromium.base.Log;
import org.chromium.base.task.TaskTraits;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import controllers.BackExecutor;
import controllers.MessegeAdapter;
import controllers.NewMessege;
import models.Channel;
import models.Messege;

public class ChatScreen extends AppCompatActivity {

    ActivityChatScreenBinding binding;
    private ListenerRegistration lsmesseges,lschannel;
    private  FirebaseFirestore fs=  FirebaseFirestore.getInstance();
    private  String channelid,channelname,userid,channelimage,stringmessege,username;
    MessegeAdapter messegeAdapter;
    private  Set<String>hashset=new HashSet<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getdata();
        setdataveiw();
        handleclickonnewmss();
        getdatafromfirstore();
    }

    private void handleclickonnewmss() {
     binding.textmess.addTextChangedListener(new TextWatcher() {
         @Override
         public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

         }

         @Override
         public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

         }

         @Override
         public void afterTextChanged(Editable editable) {
          stringmessege=editable.toString();
          if(!stringmessege.isEmpty())
          {
              binding.camraimage.setVisibility(View.GONE);
              binding.record.setVisibility(View.GONE);
              binding.attachfile.setVisibility(View.GONE);
              binding.sendbutton.setEnabled(true);
          }
          else{
              binding.camraimage.setVisibility(View.VISIBLE);
              binding.record.setVisibility(View.VISIBLE);
              binding.attachfile.setVisibility(View.VISIBLE);
              binding.sendbutton.setEnabled(false);
          }
         }
     });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fs=null;
        lschannel.remove();
        lsmesseges.remove();
        lsmesseges=null;
        lschannel=null;
    }

    private void setdataveiw() {
        binding.appbar.appbartitle.setText(channelname);
        Glide.with(this).load(channelimage).placeholder(R.drawable.chat).into(binding.appbar.userimageappbar);
        binding.sendbutton.setEnabled(false);
        binding.sendbutton.setOnClickListener(view -> sendmessege());

    }

    private void sendmessege() {
           binding.sendbutton.setVisibility(View.GONE);
           binding.loadingsend.setVisibility(View.VISIBLE);
           if(!stringmessege.isEmpty())
           {
               BackExecutor baExecutors=new BackExecutor();
               NewMessege newMessege=new NewMessege(stringmessege,userid,channelid,username,baExecutors,this);
               newMessege.executeOnExecutor(baExecutors);
           }
               binding.textmess.setText("");
               stringmessege="";
               binding.camraimage.setVisibility(View.VISIBLE);
               binding.record.setVisibility(View.VISIBLE);
               binding.attachfile.setVisibility(View.VISIBLE);
               binding.sendbutton.setEnabled(false);

    }

    private void getdatafromfirstore() {


        fs.collection("chat_channels/"+channelid+"/messeges").orderBy("timesent", Query.Direction.DESCENDING).get().addOnCompleteListener( task -> {
            ArrayList<Messege>messeges=new ArrayList<Messege>();
            Set<String>set=new HashSet<String>();
               if(!task.getResult().isEmpty()){
                   for(DocumentSnapshot dc:task.getResult().getDocuments()) {
                       Timestamp timestamp = (Timestamp) dc.get("timesent");
                       Calendar calendar=Calendar.getInstance();
                       calendar.setTime(timestamp.toDate());
                       String senderid=(String) dc.get("senderid");
                       String username=(String) dc.get("username");
                       String id=(String) dc.getId();
                       set.add(id);
                       String type=(String) dc.get("type");
                       Messege messege=new Messege(type,id,senderid,channelid,calendar,username);
                       if(type.equals("text")){
                           messege.setContent_txt((String) dc.get("content_txt"));
                       }
                       else {
                           messege.setContent_url((String) dc.get("content_url"));
                       }
                       messeges.add(messege);
                   }
               }
            messegeAdapter=new MessegeAdapter(messeges,userid,set);
            binding.loading.setVisibility(View.GONE);
            binding.recyler.setVisibility(View.VISIBLE);
            binding.recyler.setAdapter(messegeAdapter);
            inilazelisthners();
        });

    }

    private void getdata() {
        channelid=getIntent().getStringExtra("channelid");
        channelname=getIntent().getStringExtra("channelname");

        userid=getIntent().getStringExtra("userid");
        fs.collection("users").document(userid).get().addOnCompleteListener(task -> {
            username=(String) task.getResult().get("username");
        });
        channelimage=getIntent().getStringExtra("channelimage");
    }

    private void inilazelisthners() {

       lschannel= fs.collection("chat_channels").document(channelid).addSnapshotListener(Executors.BACKGROUND_EXECUTOR,
               MetadataChanges.INCLUDE, (value, error) -> {
            if (error==null)
            {
                    runOnUiThread(() -> {
                        Map<String,Object> dc = value.getData();
                        Timestamp timestamp = (Timestamp) dc.get("lasttime");
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(timestamp.toDate());
                        Channel c = new Channel(value.getId(), (String) dc.get("lastmessege"), (String) dc.get("messagetybe"), calendar,
                                (ArrayList<String>) dc.get("users"), (ArrayList<String>) dc.get("usersnames"),
                                (ArrayList<String>) dc.get("imagesurls"));
                        if (dc.containsKey("name")) {
                            c.setName((String) dc.get("name"));
                            c.setImageurl((String) dc.get("ImageUrl"));
                            c.setAdminid((String) dc.get("Adminid"));
                        }
                        runOnUiThread(() -> {
                        channelname=c.getName(userid);
                        channelimage=c.getImageurl(userid);
                        setdataveiw();
                        });
                    });

            }
       });

       lsmesseges=fs.collection("chat_channels/"+channelid+"/messeges").orderBy("timesent", Query.Direction.DESCENDING).
               addSnapshotListener(Executors.BACKGROUND_EXECUTOR,(value, error) -> {
           if(error==null){

               runOnUiThread(() -> {
                   ArrayList<Messege>messeges=new ArrayList<Messege>();
                for (DocumentChange dcc:value.getDocumentChanges())
                {
                    if(dcc.getType()== ADDED||dcc.getType()== MODIFIED)
                    {
                        DocumentSnapshot  dc= dcc.getDocument();
                        Timestamp timestamp = (Timestamp) dc.get("timesent");
                        Calendar calendar=Calendar.getInstance();
                        calendar.setTime(timestamp.toDate());
                        String senderid=(String) dc.get("senderid");
                        String username=(String) dc.get("username");
                        String id=(String) dc.getId();
                        String type=(String) dc.get("type");
                        Messege messege=new Messege(type,id,senderid,channelid,calendar,username);
                        if(type.equals("text")){
                            messege.setContent_txt((String) dc.get("content_txt"));
                        }
                        else {
                            messege.setContent_url((String) dc.get("content_url"));
                        }
                        messeges.add(messege);
                    }
                }
                        messegeAdapter.addmesseges(messeges);
               });
           }
       });
    }

    public void finishloading(String type, Exception exception) {
        Log.d("inchatscreen","finshed here\n\n\n\n");
        binding.sendbutton.setVisibility(View.VISIBLE);
       binding.loadingsend.setVisibility(View.GONE);

    }
}