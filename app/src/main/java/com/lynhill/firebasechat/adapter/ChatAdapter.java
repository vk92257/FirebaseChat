package com.lynhill.firebasechat.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.lynhill.firebasechat.Chats;
import com.lynhill.firebasechat.R;
import com.lynhill.firebasechat.model.User;

import java.util.ArrayList;


public class ChatAdapter extends RecyclerView.Adapter<com.lynhill.firebasechat.adapter.ChatAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<User> strings ;
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_user,parent,false);
        return new MyViewHolder(view);
    }
    public ChatAdapter(Context context, ArrayList<User> strings) {
        this.context = context;
        this.strings = strings;
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = strings.get(position);
        holder.myText.setText(user.getUserName());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Chats.class);
                    intent.putExtra("uid",user.getuId());
                    intent.putExtra("userName",user.getUserName());
                    context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

       TextView myText,senderText;
       CardView cardView ;
       public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myText = itemView.findViewById(R.id.textView);
            cardView = itemView.findViewById(R.id.all_user);
        }
    }

}
