package controllers;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;


import org.chromium.base.task.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import models.User;

public class AddChannel extends AsyncTask<Exception> {
   private User u1,u2;
   private Context context;
   private  Exception exception;
   private  boolean loaded=false;
   private Executor executor;
   private  FirebaseFirestore firestore=FirebaseFirestore.getInstance();
    public AddChannel(User u1, User u2, Context context,Executor executor) {
        this.u1 = u1;
        this.u2 = u2;
        this.context = context;
        this.executor=executor;
    }

    @Override
    protected Exception doInBackground() {
        ArrayList<String>users=new ArrayList<String>();
        ArrayList<String>usersnames=new ArrayList<String>();
        ArrayList<String>imageurl=new ArrayList<String>();
        users.add(u1.getId());
        users.add(u2.getId());
        usersnames.add(u1.getUserName());
        usersnames.add(u2.getUserName());
        imageurl.add(u1.getImageUrl());
        imageurl.add(u2.getImageUrl());
        Map<String,Object>data=new HashMap<String,Object>();
        data.put("lasttime", Timestamp.now());
        data.put("lastmessege", "there is no messeges yet");
        data.put("messagetybe", "None");
        data.put("users",users);
        data.put("usersnames",usersnames);
        data.put("imagesurls",imageurl);
        firestore.collection("chat_channels").add(data).
                addOnCompleteListener(executor, task -> {
                    if (task.getException()==null){
                        String channel_id=task.getResult().getId();
                        Map<String ,Object> updatedata1=new HashMap<String,Object>();
                        ArrayList<String>users1=u1.getUsers();
                        ArrayList<String>users2= u2.getUsers();
                        ArrayList<String>channel1=u1.getChannels();
                        ArrayList<String>channel2=u2.getChannels();
                        users1.add(u2.getId());
                        users2.add(u1.getId());
                        channel1.add(channel_id);
                        channel2.add(channel_id);
                        updatedata1.put("users", users1);
                        updatedata1.put("channels", channel1);
                        Map<String ,Object> updatedata2=new HashMap<String,Object>();
                        updatedata2.put("users", users2);
                        updatedata2.put("channels", channel2);
                        firestore.collection("users").document(u1.getId()).
                                update(updatedata1 ).addOnCompleteListener(executor, Task->{
                                    if (task.getException()==null)
                                        firestore.collection("users").document(u2.getId()).
                                                update(updatedata2).
                                                addOnCompleteListener(executor, taska->{
                                                    if(task.getException()!=null)
                                                      exception=  taska.getException();
                                                    loaded=true;
                                                });
                                    else {
                                        exception = task.getException();
                                        loaded=true;
                                    }
                        } );
                   }
                    else {
                        exception=task.getException();
                        loaded=true;
                    }
                });
        while (!loaded);
        return exception;
    }

    @Override
    protected void onPostExecute(Exception exception) {
          if(exception!=null)
          {
              Toast.makeText(context, "there was exception="+exception.getMessage(),Toast.LENGTH_LONG).show();

          }
    }
}
