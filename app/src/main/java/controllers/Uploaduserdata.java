package controllers;

import com.example.firebase.Auth_screen;
import com.example.firebase.profilepage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.util.Executors;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.chromium.base.task.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;

public class Uploaduserdata extends AsyncTask<Exception> {
   private FirebaseUser user;
   private Auth_screen authScreen;
   private profilepage profilepage;
   private Semaphore semaphore=new Semaphore(0);
    public Uploaduserdata(FirebaseUser user, com.example.firebase.profilepage profilepage, byte[] imagesbytes, String username, Executor executor) {
        this.user = user;
        this.profilepage = profilepage;
        this.imagesbytes = imagesbytes;
        this.username = username;
        this.executor = executor;
    }

    private byte[] imagesbytes;
   private  String username;
   private Exception exception;
   private String imageurl= "";
   private Executor executor;
    private boolean still_running=true;
    public Uploaduserdata(FirebaseUser user, Auth_screen authScreen, byte[] imagesbytes, String username, Executor executor) {
        this.user = user;
        this.authScreen = authScreen;
        this.imagesbytes = imagesbytes;
        this.username = username;
        this.executor = executor;
    }



    @Override
    protected Exception doInBackground() {
        if(imagesbytes!=null) {
            StorageReference sr = FirebaseStorage.getInstance().getReference().child("userimages");
            sr = sr.child(user.getUid() + ".jpg");
            UploadTask uploadTask = sr.putBytes(imagesbytes);
            uploadTask.addOnFailureListener(e -> {
            });
            uploadTask.addOnSuccessListener(executor, taskSnapshot -> {
                taskSnapshot.getStorage().getDownloadUrl().onSuccessTask(executor,
                        uri -> {
                            imageurl = uri.toString();
                            semaphore.release();
                            return null;
                        });
            });
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

                Map<String, Object> docData = new HashMap<>();
                docData.put("username", username);
                if(imagesbytes!=null)
                docData.put("imageurl", imageurl);
            if(authScreen!=null) {
                docData.put("email", user.getEmail());
                docData.put("users", new ArrayList<String>());
                docData.put("channels", new ArrayList<String>());
                FirebaseFirestore.getInstance().collection("users").document(user.getUid()).
                        set(docData).addOnCompleteListener(executor, task -> {
                    if (task.getException() != null)
                        exception = task.getException();
                  semaphore.release();
                });
            }
            else{
                FirebaseFirestore.getInstance().collection("users").document(user.getUid()).
                        update(docData).addOnCompleteListener(executor, task->{
                    if (task.getException() != null)
                        exception = task.getException();
                    semaphore.release();
                });
            }
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return exception;
    }
    @Override
    protected void onPostExecute(Exception exception) {
        if(authScreen!=null)
        authScreen.finishloading(exception);
        else
            profilepage.finishloading();
    }

}
