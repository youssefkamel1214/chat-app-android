package controllers;
import static java.util.Collections.swap;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.example.firebase.databinding.ChannelItemBinding;
import com.google.firebase.firestore.util.Executors;
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
       channels.remove(indexes.get(channel.getId()).intValue());
       notifyItemRemoved(indexes.get(channel.getId()));
       indexes.remove(channel.getId());
   }
    public void  modify_channel (Channel channel){
            int old_pos=indexes.get(channel.getId());
            for (int i=old_pos;i>0;i--){
                swap(channels,i,i-1);
                indexes.put(channels.get(i).getId(), i);

            }
            indexes.put(channel.getId(),0);
            notifyItemRangeChanged(0, old_pos);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ChannelItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Channel channel=channels.get(position);
        System.out.println(channel.getImageurl(Userid));
        holder.binding.name.setText(channel.getName(Userid));
        holder.binding.message.setText(channel.getLastMessage());
        NetImage netImage=new NetImage(channel.getImageurl(Userid),holder.binding.channelimage);
        netImage.executeOnExecutor(new BackExecutor());

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