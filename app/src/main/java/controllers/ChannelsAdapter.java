package controllers;
import static java.util.Collections.swap;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.firebase.ChatScreen;
import com.example.firebase.R;
import com.example.firebase.databinding.ChannelItemBinding;
import com.google.firebase.firestore.util.Executors;

import org.chromium.base.task.AsyncTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import models.Channel;

public class ChannelsAdapter extends RecyclerView.Adapter<ChannelsAdapter.ViewHolder> {
      private ArrayList<Channel> channels;
      private String Userid;
      private   Map<String,Integer>indexes;
    public ChannelsAdapter(ArrayList<Channel> channels,String Userid,Map<String,Integer>indexes) {
        this.channels = channels;
        this.Userid=Userid;
        this.indexes=indexes;
    }
    public void add_Channel(Channel channel){
        if (indexes.containsKey(channel.getId()))
        {
            modify_channel(channel);
            return;
        }
        channels.add(0, channel);
        indexes.put(channel.getId(), 0);
        notifyItemInserted(0);
    }
   public void  delete_channel (Channel channel){
        if(indexes.containsKey(channel.getId())) {
            channels.remove(indexes.get(channel.getId()).intValue());
            notifyItemRemoved(indexes.get(channel.getId()));
            indexes.remove(channel.getId());
        }
   }
    public void  modify_channel (Channel channel){
            int old_pos=indexes.get(channel.getId());
            for (int i=old_pos;i>0;i--){
                swap(channels,i,i-1);
                indexes.put(channels.get(i).getId(), i);

            }
            indexes.put(channel.getId(),0);
            notifyItemRangeChanged(0, old_pos+1);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ChannelItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Channel channel=channels.get(position);
        holder.binding.name.setText(channel.getName(Userid));
        handlelastsentmessge(channel.getLastMessage(),channel.getMessegetybe(),holder.binding.imagetype,holder.binding.message);
       // holder.binding.message.setText(channel.getLastMessage());
        DateFormat dateFormat=new SimpleDateFormat("dd-MMM-yyyy");
        holder.binding.time.setText(dateFormat.format( channel.getLastTime().getTime()));
        if(!channel.getImageurl(Userid).isEmpty())
           Glide.with(holder.binding.channelimage).load(channel.getImageurl(Userid)).placeholder(R.drawable.chat).into(holder.binding.channelimage);
        holder.binding.chatbutton.setOnClickListener(view -> {
            Intent intent=new Intent(holder.binding.chatbutton.getContext(), ChatScreen.class);
            intent.putExtra("channelid",channel.getId());
            intent.putExtra("channelname",channel.getName(Userid));
            intent.putExtra("userid",Userid);
            intent.putExtra("channelimage",channel.getImageurl(Userid));
            holder.binding.chatbutton.getContext().startActivity(intent);
        });

    }

    private void handlelastsentmessge(String lastMessage, String messegetybe, ImageView imagetype, TextView message) {
        if(messegetybe.equals("None")||messegetybe.equals("text"))
            message.setText(lastMessage);
        else if (messegetybe.equals("audio"))
        {
            message.setText("audio was sent");
            imagetype.setVisibility(View.VISIBLE);
            imagetype.setImageResource(R.drawable.ic_baseline_audio_24);
        }
        else if(messegetybe.equals("image"))
        {
            message.setText("image was sent");
            imagetype.setVisibility(View.VISIBLE);
            imagetype.setImageResource(R.drawable.ic_baseline_image_24);
        }
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ChannelItemBinding binding;
        public ViewHolder(ChannelItemBinding binding) {
            super(binding.getRoot());
            this.binding=binding;

        }

    }
}