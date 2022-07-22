package com.example.firebase;
import static org.chromium.base.ThreadUtils.runOnUiThread;

import android.nfc.Tag;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.firebase.databinding.FragmentUsersBinding;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.util.Executors;
import com.google.type.DateTime;

import org.chromium.base.Log;
import org.chromium.base.task.TaskExecutor;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;

import controllers.BackExecutor;
import controllers.UsersAdapter;
import models.User;

public class UsersFragment extends Fragment {

    private FragmentUsersBinding binding;
    private  User user=null;
    private String searcher="";
    private ListenerRegistration ls;
    private  FirebaseFirestore fs=  FirebaseFirestore.getInstance();
    private UsersAdapter usersAdapter;

    public UsersFragment() {
        // Required empty public constructor
    }

    public void setUser(User user) {
        this.user = user;
        get_allusersdata();
        if(usersAdapter!=null)
            usersAdapter.set_user(user);

    }
    public void setSearcher(String searcher) {
        this.searcher = searcher;
        usersAdapter.setSearcher(searcher);
    }

    @Override
    public void onDestroyView() {
        System.out.println("here");
        if(ls!=null) {
            ls.remove();
        }
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ls=null;
        fs=null;
        super.onDestroyView();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding=FragmentUsersBinding.inflate(getLayoutInflater());
       binding.loadingUf.setVisibility(View.VISIBLE);
       binding.text.setVisibility(View.GONE);
       binding.usersRecl.setVisibility(View.GONE);
       return binding.getRoot();
    }

    private void inialaize_listhners() {
        ls=fs.collection("users").addSnapshotListener(Executors.BACKGROUND_EXECUTOR, MetadataChanges.INCLUDE,
                (value, error) -> {
                    if (error==null){
                        for(DocumentChange dcc:value.getDocumentChanges()){
                            System.out.println(Calendar.getInstance().getTime());
                            if(dcc.getDocument().getId().equals(user.getId())&&ls!=null)
                                continue;
                            switch (dcc.getType()) {
                                case ADDED:
                                    runOnUiThread(() -> {
                                                QueryDocumentSnapshot dc= dcc.getDocument();
                                                User   u=new User(dc.getId(),(String) dc.get("email"),(String) dc.get("username"),
                                                    (String) dc.get("imageurl"),(ArrayList<String>)dc.get("users"),
                                                    (ArrayList<String>) dc.get("channels"));
                                                usersAdapter.add_User(u);
                                            }
                                    );
                                    break;
                                case MODIFIED:
                                    runOnUiThread(() -> {
                                                QueryDocumentSnapshot dc= dcc.getDocument();
                                                User   u=new User(dc.getId(),(String) dc.get("email"),(String) dc.get("username"),
                                                    (String) dc.get("imageurl"),(ArrayList<String>)dc.get("users"),
                                                    (ArrayList<String>) dc.get("channels"));
                                                usersAdapter.modify_User(u);
                                            }
                                    );                            break;
                                case REMOVED:
                                    runOnUiThread(() -> {
                                                QueryDocumentSnapshot dc= dcc.getDocument();
                                                User u=new User(dc.getId(),(String) dc.get("email"),(String) dc.get("username"),
                                                    (String) dc.get("imageurl"),(ArrayList<String>)dc.get("users"),
                                                    (ArrayList<String>) dc.get("channels"));
                                                usersAdapter.delete_User(u);
                                            }
                                    );                            break;
                                default:
                                    break;
                            }
                            set_visbality(usersAdapter.getItemCount()==0);
                        }
                    }
                });
    }

    private void get_allusersdata() {
        ArrayList<User>users=new ArrayList<User>();
        Map<String,Integer>index=new HashMap<String,Integer>();
        fs.collection("users").get().addOnCompleteListener(task -> {
            if(task.getException()==null){
                Log.d("on complete", "called" );
                int i=0;
                if(task.getResult().isEmpty())
                    return;
                for (DocumentSnapshot dc:task.getResult().getDocuments()){
                    if(user.getId().equals(dc.getId()))
                        continue;
                    users.add(new User(dc.getId(), (String)dc.get("email"), (String)dc.get("username"),
                            (String)dc.get("imageurl") ,(ArrayList<String>) dc.get("users") ,
                            (ArrayList<String>)dc.get("channels") ));
                    index.put(dc.getId(), i);
                    i++;
                }

                    binding.loadingUf.setVisibility(View.GONE);
                    usersAdapter=new UsersAdapter(users,user,searcher,index);
                    binding.usersRecl.setAdapter(usersAdapter);
                    set_visbality(usersAdapter.getItemCount()==0);
                    inialaize_listhners();
                }
        });
    }

    private void set_visbality(boolean empty) {
        if (empty){
            binding.text.setVisibility(View.VISIBLE);
            binding.usersRecl.setVisibility(View.GONE);
        }
        else {
            binding.text.setVisibility(View.GONE);
            binding.usersRecl.setVisibility(View.VISIBLE);
        }
    }
}