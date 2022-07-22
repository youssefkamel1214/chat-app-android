package com.example.firebase;

import static org.chromium.base.ThreadUtils.runOnUiThread;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.firebase.databinding.FragmentChannelsBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.util.Executors;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import controllers.ChannelsAdapter;
import models.Channel;

/**
 * A fragment representing a list of Items.
 */
public class ChannelsFragment extends Fragment {

  private  FragmentChannelsBinding binding;
  private ChannelsAdapter channelsAdapter;
  private ListenerRegistration ls;
  private final  FirebaseFirestore fs=  FirebaseFirestore.getInstance();
  private final   String Userid=FirebaseAuth.getInstance().getCurrentUser().getUid();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentChannelsBinding.inflate(getLayoutInflater());
        binding.channelsRecl.setVisibility(View.GONE);
        binding.text.setVisibility(View.GONE);
        binding.progressCircular.setVisibility(View.VISIBLE);
        get_channels_data();
        return binding.getRoot();
    }
    @Override
    public void onDestroy() {
        ls.remove();
        ls=null;
        super.onDestroy();
    }
    private void make_initialize_listeners() {
        ls= fs.collection("chat_channels").whereArrayContains("users", Userid).addSnapshotListener(Executors.BACKGROUND_EXECUTOR, MetadataChanges.INCLUDE, (value, error) -> {
            if (error==null){
                for(DocumentChange dcc:value.getDocumentChanges()){
                    switch (dcc.getType()) {
                        case ADDED:
                            runOnUiThread(() -> {
                                        QueryDocumentSnapshot dc= dcc.getDocument();
                                        Timestamp timestamp=(Timestamp) dc.get("lasttime");
                                        Calendar calendar=Calendar.getInstance();
                                        calendar.setTime(timestamp.toDate());
                                        Channel c=new Channel(dc.getId(),(String) dc.get("lastmessege"),(String) dc.get("messagetybe"),calendar,
                                                (ArrayList<String>) dc.get("users"),(ArrayList<String>)dc.get("usersnames"),
                                                (ArrayList<String>) dc.get("imagesurls"));
                                        if(dc.contains("name")){
                                            c.setName((String) dc.get("name"));
                                            c.setImageurl((String) dc.get("ImageUrl"));
                                            c.setAdminid((String) dc.get("Adminid"));
                                        }
                                        channelsAdapter.add_Channel(c);
                                        set_visbality(channelsAdapter.getItemCount()==0);
                                    }
                            );
                            break;
                        case MODIFIED:
                            runOnUiThread(() -> {
                                        QueryDocumentSnapshot dc= dcc.getDocument();
                                        Timestamp timestamp=(Timestamp) dc.get("lasttime");
                                        Calendar calendar=Calendar.getInstance();
                                        calendar.setTime(timestamp.toDate());
                                        Channel c=new Channel(dc.getId(),(String) dc.get("lastmessege"),(String) dc.get("messagetybe"),calendar,
                                                (ArrayList<String>) dc.get("users"),(ArrayList<String>)dc.get("usersnames"),
                                                (ArrayList<String>) dc.get("imagesurls"));

                                        if(dc.contains("name")){
                                            c.setName((String) dc.get("name"));
                                            c.setImageurl((String) dc.get("imageurl"));
                                            c.setAdminid((String) dc.get("Adminid"));

                                        }
                                        channelsAdapter.modify_channel(c);
                                        set_visbality(channelsAdapter.getItemCount()==0);
                                    }
                            );                            break;
                        case REMOVED:
                            runOnUiThread(() -> {
                                        QueryDocumentSnapshot dc= dcc.getDocument();
                                        Timestamp timestamp=(Timestamp) dc.get("lasttime");
                                        Calendar calendar=Calendar.getInstance();
                                        calendar.setTime(timestamp.toDate());
                                        Channel c=new Channel(dc.getId(),(String) dc.get("lastmessege"),(String) dc.get("messagetybe"),calendar,
                                                (ArrayList<String>) dc.get("users"),(ArrayList<String>)dc.get("usersnames"),
                                                (ArrayList<String>) dc.get("imagesurls"));
                                        if(dc.contains("name")){
                                            c.setName((String) dc.get("name"));
                                            c.setImageurl((String) dc.get("imageurl"));
                                            c.setAdminid((String) dc.get("Adminid"));

                                        }
                                        channelsAdapter.delete_channel(c);
                                        set_visbality(channelsAdapter.getItemCount()==0);

                                    }
                            );                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

    private void get_channels_data() {
        ArrayList<Channel>channels=new ArrayList<Channel>();
        Map<String,Integer> indexes=new HashMap<String,Integer>();
      fs.collection("chat_channels").whereArrayContains("users", Userid).get().
                addOnCompleteListener(task -> {
            if(task.getException()==null){
                if(task.getResult().isEmpty()){
                    channelsAdapter=new ChannelsAdapter(channels,Userid,indexes);
                }
                else {
                    for(DocumentSnapshot dc: task.getResult().getDocuments()){
                        Timestamp timestamp=(Timestamp) dc.get("lasttime");
                        Calendar calendar=Calendar.getInstance();
                        calendar.setTime(timestamp.toDate());
                        Channel c=new Channel(dc.getId(),(String) dc.get("lastmessege"),(String) dc.get("messagetybe"),calendar,
                                (ArrayList<String>) dc.get("users"),(ArrayList<String>)dc.get("usersnames"),
                                (ArrayList<String>) dc.get("imagesurls"));
                        if(dc.contains("name")){
                            c.setName((String) dc.get("name"));
                            c.setImageurl((String) dc.get("imageurl"));
                        }
                        channels.add(0, c);
                    }
                    channels.sort((channel, t1) -> {
                        if (channel.getLastTime().after(t1.getLastTime()))
                            return  1;
                        else if(channel.getLastTime().before(t1.getLastTime()))
                            return  -1;
                        else
                            return  0;
                    });
                   for (int i=0;i<channels.size();i++)
                   {
                       indexes.put(channels.get(i).getId(), i);
                   }
                }
            }
            set_visbality(channels.isEmpty());
            make_initialize_listeners();
                });

        channelsAdapter=new ChannelsAdapter(channels, Userid,indexes);
        binding.progressCircular.setVisibility(View.GONE);
        binding.channelsRecl.setAdapter(channelsAdapter);
    }
    private void set_visbality(boolean empty) {
        if (empty){
            binding.text.setVisibility(View.VISIBLE);
            binding.channelsRecl.setVisibility(View.GONE);
        }
        else {
            binding.text.setVisibility(View.GONE);
            binding.channelsRecl.setVisibility(View.VISIBLE);
        }
    }
}
