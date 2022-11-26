package com.example.crypt.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.crypt.MessageActivity;
import com.example.crypt.Fragments.Model.User;
import com.example.crypt.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserSendFileAdapter extends RecyclerView.Adapter<UserSendFileAdapter.ViewHolder>{
    private Context mContext;
    private List<User> mUser;
    private String mFiles, uid;
    private boolean ischat;

    public UserSendFileAdapter(Context mContext, List<User> mUser, String mFiles, String uid, boolean ischat){
        this.mUser = mUser;
        this.mContext = mContext;
        this.ischat = ischat;
        this.mFiles = mFiles;
        this.uid = uid;
    }

    @NonNull
    @Override
    public UserSendFileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserSendFileAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserSendFileAdapter.ViewHolder holder, int position) {
        User user = mUser.get(position);
        holder.username.setText(user.getName());
        if(user.getImgURL().isEmpty()){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(mContext).load(user.getImgURL()).into(holder.profile_image);
        }
        if(ischat){
            if(user.getStatus().equals("online")){
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            }else{
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        }else{
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( mContext, MessageActivity.class);
                intent.putExtra("userid", user.getIdUser());
                intent.putExtra("fileId", mFiles);
                sendMessage(uid, user.getIdUser(), mFiles);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        public ViewHolder(View itemView){
            super(itemView);
            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.image_on);
            img_off = itemView.findViewById(R.id.image_off);
        }
    }

    private void sendMessage(String sender, String receiver, String message){
        DatabaseReference data = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("typeMss", 3);
        data.child("Chats").push().setValue(hashMap);
    }
}
