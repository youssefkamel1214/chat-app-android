package controllers;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.example.firebase.ChatScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.util.Executors;

import org.chromium.base.Log;
import org.chromium.base.task.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;

//private String type,id,content_txt,senderid,channelid,content_url,username;
//    Calendar timesend;
public class NewMessege extends AsyncTask<Exception> {
    String content_txt,senderid,channelid,username,type;
    private byte[] imagesbytes;
    private FirebaseFirestore firestore=FirebaseFirestore.getInstance();
    private Executor executor;
    private Exception exception;
    private String id;
    private Semaphore semaphore=new Semaphore(0);
    WeakReference<Activity>weakReference;

    public NewMessege(String content_txt, String senderid, String channelid, String username, Executor executor, ChatScreen activity) {
        this.content_txt = content_txt;
        this.senderid = senderid;
        this.channelid = channelid;
        this.username = username;
        type="text";
        this.executor=executor;
        weakReference=new WeakReference<Activity>(activity);
    }
    public NewMessege(String senderid, String channelid, String username, byte[] imagesbytes,Executor executor,ChatScreen activity) {
        this.senderid = senderid;
        this.channelid = channelid;
        this.username = username;
        type = "image";
        this.imagesbytes = imagesbytes;
        this.executor=executor;
        weakReference=new WeakReference<Activity>(activity);
    }

    @Override
    protected Exception doInBackground() {
        if (type.equals("text"))
            sendtxtmessege();
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return exception;
    }

    private void sendtxtmessege() {
        Map<String,Object> data=new HashMap<String,Object>();
        data.put("timesent", Timestamp.now());
        data.put("senderid", senderid);
        data.put("username", username);
        data.put("type", "text");
        data.put("content_txt", content_txt);
        firestore.collection("chat_channels/"+channelid+"/messeges").add(data).addOnCompleteListener(executor, task -> {
            exception=task.getException();
            finishhere();
            semaphore.release();
        });
    }

    private void finishhere() {
        id= "afaf";
    }

    @Override
    protected void onPostExecute(Exception exception) {
        ChatScreen chatScreen=(ChatScreen) weakReference.get();
        chatScreen.finishloading(type,exception);
    }
}
