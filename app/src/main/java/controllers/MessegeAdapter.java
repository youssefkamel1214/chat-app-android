package controllers;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.firebase.R;
import com.example.firebase.databinding.ChannelItemBinding;
import com.example.firebase.databinding.MessegebubbleBinding;
import com.example.firebase.databinding.MessegecontentBinding;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import models.Messege;

public class MessegeAdapter extends RecyclerView.Adapter<MessegeAdapter.ViewHolder> {
    private  ArrayList<Messege>messeges;
    private String Userid;
    private Set<String> indexes;

    public MessegeAdapter(ArrayList<Messege> messeges, String userid, Set<String>  indexes) {
        this.messeges = messeges;
        Userid = userid;
        this.indexes = indexes;
    }
public void addmessege(Messege messege){
        if(indexes.contains(messege.getId()))
            return;
        else {
               messeges.add(0, messege);
               indexes.add(messege.getId());
               notifyDataSetChanged();
            }
}
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessegeAdapter.ViewHolder(MessegebubbleBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Messege messege=messeges.get(position);
            int color=R.color.white;
            boolean me=Userid.equals(messege.getSenderid());
        if(messege.getContent_txt().equals("af"))
            System.out.println("fa");
            MessegecontentBinding conbid=MessegecontentBinding.inflate(LayoutInflater.from(holder.binding.left.getContext()));
            makedir(me,holder.binding,conbid);
          if(!me)
                color=R.color.black;
            conbid.username.setText(messege.getUsername());
            conbid.username.setTextColor(conbid.image.getContext().getResources().getColor(color));
            if(messege.getType().equals("text"))
            {
                conbid.messegetext.setVisibility(View.VISIBLE);
                conbid.messegetext.setText(messege.getContent_txt());
                conbid.messegetext.setTextColor(conbid.image.getContext().getResources().getColor(color));
            }
            else if(messege.getType().equals("image"))
            {
                conbid.image.setVisibility(View.VISIBLE);
                Glide.with(conbid.image.getContext()).load(messege.getContent_url()).placeholder(R.drawable.chat).into(conbid.image);
            }
    }

    private void makedir(boolean me, MessegebubbleBinding bid1,MessegecontentBinding bid2) {
      if(me) {
            bid1.right.setVisibility(View.GONE);
            bid1.left.setVisibility(View.VISIBLE);
            bid1.left.removeAllViews();
            bid1.left.addView(bid2.getRoot());
        }
        else {
            bid1.left.setVisibility(View.GONE);
            bid1.right.setVisibility(View.VISIBLE);
            bid1.right.removeAllViews();
            bid1.right.addView(bid2.getRoot());
      }
    }

    @Override
    public int getItemCount() {
        return messeges.size();
    }

    public void addmesseges(ArrayList<Messege> messegess) {
        boolean changed=false;
        for (Messege messege:messegess)
        {
            if(indexes.contains(messege.getId()))
                continue;
            else {
                changed=true;
                messeges.add(0,messege);
                indexes.add(messege.getId());
            }
        }
        if (changed)
            notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final MessegebubbleBinding binding;
        public ViewHolder(MessegebubbleBinding binding) {
            super(binding.getRoot());
            this.binding=binding;

        }

    }
}
