package com.lynhill.firebasechat.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lynhill.firebasechat.Chats;
import com.lynhill.firebasechat.R;
import com.lynhill.firebasechat.model.MessageDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessageChatAdapter extends RecyclerView.Adapter<MessageChatAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<MessageDetails> strings ;
    private String TAG="MessageCahtAdapter";

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_chat,parent,false);
        return new MyViewHolder(view);
    }
    public MessageChatAdapter(Context context, ArrayList<MessageDetails> strings) {
        this.context = context;
        this.strings = strings;
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MessageDetails  userData = strings.get(position);
           Log.i("TAG", "onBindViewHolder: "+userData.getId()+"------------------"+Chats.user.getUid()+"_"+Chats.uid);
         String id = Chats.user.getUid()+"_"+Chats.uid;
         if (userData.getId()!=null){
             if (userData.getId().equals(id)){
                 holder.senderTextTime.setVisibility(View.GONE);
                 holder.sender.setVisibility(View.GONE);
                 holder.senderTextImage.setVisibility(View.GONE);
                 holder.myTextTime.setText(userData.getTime());
                 if (userData.getImage_uri() != null){
                     holder.myText.setVisibility(View.GONE);
                     holder.my.setVisibility(View.GONE);
                     Picasso.get()
                             .load(userData.getImage_uri())
                             .into(holder.myTextImage);
                     Log.i(TAG, "onBindViewHolder: "+userData.getImage_uri());
                 }else{
                     holder.myTextImage.setVisibility(View.GONE);
                     holder.myText.setText(userData.getMessage());
                 }

//              Log.i("TAG", "onBindViewHolder: condition true");
//              if ( userData.getImage_uri()!= null){
//                  holder.sender.setVisibility(View.GONE);
//                 holder.myTextImage.setVisibility(View.VISIBLE);
//                  holder.myText.setVisibility(View.GONE);
//                 Log.i(TAG, "onBindViewHolder: "+userData.getImage_uri());
//
//             }else{
//                 holder.myTextImage.setVisibility(View.GONE);
//                 holder.myText.setText(userData.getMessage());
//                 holder.myText.setVisibility(View.VISIBLE);
//             }
//              holder.myTextTime.setVisibility(View.VISIBLE);
//             holder.myTextTime.setText(userData.getTime());
//            holder.senderTextImage.setVisibility(View.GONE);
//            holder.senderText.setVisibility(View.GONE);
//          holder.senderTextTime.setVisibility(View.GONE);
             }
             else {
                 Log.i("TAG", "onBindViewHolder: condition false");
                 holder.my.setVisibility(View.GONE);
                 holder.myTextTime.setVisibility(View.GONE);
                 holder.myTextImage.setVisibility(View.GONE);
                 holder.senderTextTime.setText(userData.getTime());
                 if (userData.getImage_uri() != null){
                     holder.senderText.setVisibility(View.GONE);
                     holder.sender.setVisibility(View.GONE);
                     Picasso.get()
                             .load(userData.getImage_uri())
                             .into(holder.senderTextImage);
                     Log.i(TAG, "onBindViewHolder: "+userData.getImage_uri());
                 }else{
                     holder.senderTextImage.setVisibility(View.GONE);
                     holder.senderText.setText(userData.getMessage());
                 }

//              if ( userData.getImage_uri()!= null){
//                  holder.senderTextImage.setVisibility(View.VISIBLE);
//                  holder.senderText.setVisibility(View.GONE);
//                  Log.i(TAG, "onBindViewHolder: "+userData.getImage_uri());
//
//              }else{
//                  holder.senderTextImage.setVisibility(View.GONE);
//                  holder.senderText.setVisibility(View.VISIBLE);
//                  holder.senderText.setText(userData.getMessage());
//              }
//              holder.senderTextTime.setVisibility(View.VISIBLE);
//              holder.senderTextTime.setText(userData.getTime());
//              holder.myText.setVisibility(View.GONE);
//              holder.myTextTime.setVisibility(View.GONE);
             }
         }
         else {
             holder.my.setVisibility(View.GONE);
             holder.sender.setVisibility(View.GONE);
             holder.myTextTime.setVisibility(View.GONE);
             holder.senderTextTime.setVisibility(View.GONE);
             holder.myTextImage.setVisibility(View.GONE);
             holder.senderTextImage.setVisibility(View.GONE);

         }
    }

    @Override
    public int getItemCount()
    {
        Log.i("TAG", "onBindViewHolder: condition false"+strings.size());
        return strings.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView myText,senderText,myTextTime,senderTextTime;
        ImageView myTextImage,senderTextImage;
        FrameLayout my,sender;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myText = itemView.findViewById(R.id.my_text);
            senderText = itemView.findViewById(R.id.sender_text);
            myTextTime = itemView.findViewById(R.id.my_text_time);
            senderTextTime = itemView.findViewById(R.id.sender_text_time);
            myTextImage = itemView.findViewById(R.id.my_text_image);
            senderTextImage = itemView.findViewById(R.id.semder_text_image);
            my = itemView.findViewById(R.id.me);
            sender = itemView.findViewById(R.id.sender);

        }
    }

}
