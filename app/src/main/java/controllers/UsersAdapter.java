package controllers;

import static java.util.Collections.swap;

import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.IconCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firebase.databinding.ChannelItemBinding;
import com.example.firebase.databinding.UsersItemBinding;
import com.google.firebase.firestore.util.Executors;
import java.util.ArrayList;
import java.util.Map;
import models.User;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder>{
   private ArrayList<User>users;
   private  User user;
   private String searcher;
   private Map<String,Integer> indexes;

    public UsersAdapter(ArrayList<User> users, User user, String searcher,Map<String,Integer> indexes) {
        this.users = users;
        this.user = user;
        this.searcher = searcher;
        this.indexes=indexes;
    }
    public void add_User(User user){
        if(indexes.containsKey(user.getId()))
            return;
        users.add( user);
        indexes.put(user.getId(), users.size()-1);
        notifyItemInserted(users.size()-1);
    }
    public void  delete_User (User user){
        if(indexes.containsKey(user.getId())) {
            users.remove(indexes.get(user.getId()).intValue());
            notifyItemRemoved(indexes.get(user.getId()));
            indexes.remove(user.getId());
        }
    }
    public void  modify_User (User user){

        users.set(indexes.get(user.getId()).intValue(), user);
        notifyDataSetChanged();
    }
    public void setSearcher(String searcher) {
        this.searcher = searcher;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UsersItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        User another_user=users.get(position);
        if(another_user.getEmail().contains(searcher)||another_user.getUserName().contains(searcher)) {
            holder.binding.MainCard.setVisibility(View.VISIBLE);
          if( ((BitmapDrawable)holder.binding.userimage.getDrawable())==null||
                 !holder.binding.email.getText().equals(another_user.getEmail())){
                  Glide.with(holder.binding.userimage).load(another_user.getImageUrl()).into(holder.binding.userimage);
          }
            holder.binding.email.setText(another_user.getEmail());
            holder.binding.username.setText(another_user.getUserName());
            holder.binding.addChannel.setOnClickListener(another_user.getUsers().contains(user.getId()) ? null
                    : (View.OnClickListener) view -> {
                         BackExecutor backExecutor=new BackExecutor();
                        AddChannel addChannel=new AddChannel(user, another_user,
                                holder.binding.addChannel.getContext(),backExecutor );
                        addChannel.executeOnExecutor(backExecutor);
                    });
            holder.binding.chat.setOnClickListener(another_user.getUsers().contains(user.getId())? view -> {
                System.out.println("i am with you");
            }:null);
        }
        else {
            holder.binding.MainCard.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void set_user(User user) {
        this.user=user;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final UsersItemBinding binding;
        public ViewHolder(UsersItemBinding binding) {
            super(binding.getRoot());
            this.binding=binding;

        }
    }
}
