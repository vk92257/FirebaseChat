package com.lynhill.firebasechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lynhill.firebasechat.adapter.ChatAdapter;
import com.lynhill.firebasechat.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
        private RecyclerView  recyclerView;
        private ArrayList<User> strings;
        private ChatAdapter chatAdapter;
        private FirebaseDatabase database;
        private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        addViews();
    }

    private void addViews() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        strings = new ArrayList<>();
        fetchingData();
                recyclerView = findViewById(R.id.recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
            chatAdapter = new ChatAdapter(this,strings);
          recyclerView.setAdapter(chatAdapter);
            recyclerView.setHasFixedSize(true);
            chatAdapter.notifyDataSetChanged();
    }

    private void fetchingData() {
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Iterable<DataSnapshot> value = snapshot.getChildren();
                   for (DataSnapshot snapshot1 : value){
                       FirebaseUser currentUser = mAuth.getCurrentUser();
                       if (!currentUser.getUid().equals(snapshot1.getKey())){
                            User user = new User();
                            user.setUserName(snapshot1.getValue().toString());
                            user.setuId(snapshot1.getKey());
                            strings.add(user);
                       }
                   }
                    chatAdapter.notifyDataSetChanged();
                     }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.i("TAG", "onCancelled: "+error.toString());
                }
            });

    }
}