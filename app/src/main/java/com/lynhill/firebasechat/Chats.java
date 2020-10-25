package com.lynhill.firebasechat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lynhill.firebasechat.adapter.MessageChatAdapter;
import com.lynhill.firebasechat.model.MessageDetails;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Chats extends AppCompatActivity {
    private ImageView send ;
    private EditText input;
        private TextView mTextView;
    private RecyclerView recyclerView;
    private MessageDetails messageDetails;
    private ArrayList<MessageDetails> list ;
    private ArrayList<MessageDetails> recieve ;
    private FirebaseAuth firebaseAuth ;
    private String s;
    private StorageReference mStorageRef;
    private ImageView attachment;
    private static int GALLRY_CODE = 1;
    private  MessageChatAdapter messageChatAdapter;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
  private   DatabaseReference myRef = database.getReference("all_messages");
  public static FirebaseUser user;
  public  static String uid;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        mTextView = (TextView) findViewById(R.id.text);
        if (getIntent()!= null) {
            Intent intent = getIntent();
                uid = intent.getStringExtra("uid");
                String userName = intent.getStringExtra("userName");
        mTextView.setText(userName);
        }
        messageRecieve();
        findViews();

        myRef.child(uid+"_"+user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d("TAG", "Value is: " + dataSnapshot.getChildren() );
                   Iterable<DataSnapshot> data =  dataSnapshot.getChildren();
                MessageDetails messageDetails =  new MessageDetails();
               for (DataSnapshot snapshot : data)
                   messageDetails = snapshot.getValue(MessageDetails.class);
                Log.d("TAG", "Value is: " + messageDetails.getMessage() );
//                Log.d("TAG", "Value is: " + snapshot.getValue() );
                list.add(messageDetails);
               if(messageChatAdapter != null){
                messageChatAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }

    private void messageRecieve() {


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("TAG", "onActivityResult: "+"false");
            if (requestCode == GALLRY_CODE && resultCode == RESULT_OK){
                Log.i("TAG", "onActivityResult: "+"true");
                if (data != null){
                    imageUri = data.getData();
                   saveImage();
                    Log.i("TAG", "onActivityResult: "+imageUri);
                }
            }
    }

    private void saveImage() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef=  mStorageRef.child("images")
                .child("images_"+String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()))+".jpg");
//                .child("images_"+String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()))+".jpg");
        riversRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        MessageDetails messageDetails = new MessageDetails();
                        int time = (int) (System.currentTimeMillis());
                        Timestamp tsTemp = new Timestamp(time);
                        String ts =  tsTemp.toString();
                        messageDetails.setImage_uri(uri.toString());
                        messageDetails.setTime(ts);
//                        Log.i(TAG, "onSuccess: +"+user.getUid()+"_"+uid);
                        messageDetails.setId(user.getUid()+"_"+uid);
                        myRef.child(user.getUid()+"_"+uid).push().setValue(messageDetails);
                        list.add(messageDetails);
                    messageChatAdapter.notifyDataSetChanged();
                    }
                });

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("TAG", "onFailure: "+e.toString());
                    }
                });

    }

    private void findViews() {
        attachment = findViewById(R.id.attachment);
        attachment.bringToFront();
        attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,GALLRY_CODE);
            }
        });
        recyclerView = findViewById(R.id.message_recycler);
       LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
       recyclerView.setLayoutManager(linearLayoutManager);

//        MessageDetails messageDetails = new MessageDetails();
//        messageDetails.setMessage("hello");
//        MessageDetails messageDetails1 = new MessageDetails();
//        messageDetails.setMessage("how ");
//        MessageDetails messageDetails2 = new MessageDetails();
////        messageDetails.setMessage("are");
        list = new ArrayList<>();
        recieve = new ArrayList<>();
        iniilizeAdapter();
//        list.add(messageDetails);
//        list.add(messageDetails1);
//        list.add(messageDetails2);
//        recyclerView.setHasFixedSize(true);
//        messageChatAdapter = new MessageChatAdapter(this,list);
//        recyclerView.setAdapter(messageChatAdapter);
//        messageChatAdapter.notifyDataSetChanged();
        sendMessage();

    }

    private void sendMessage() {
        input = findViewById(R.id.input);
        send = findViewById(R.id.messag_send);
        s = input.getText().toString();
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                s = editable.toString();
            }
        });
        send.bringToFront();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("TAG", "onClick: "+s+uid);
            if (s != null && uid != null){
                sendProcess();

            }
            }
        });

    }
    private void sendProcess() {
        int time = (int) (System.currentTimeMillis());
        Timestamp tsTemp = new Timestamp(time);
        String ts =  tsTemp.toString();
        MessageDetails messageDetails = new MessageDetails();
        messageDetails.setMessage(s);
        messageDetails.setTime(ts);
        messageDetails.setId(user.getUid()+"_"+uid);
        myRef.child(user.getUid()+"_"+uid).push().setValue(messageDetails);
        list.add(messageDetails);
       iniilizeAdapter();
        input.setText("");
//        Toast.makeText(Chats.this, ""+s, Toast.LENGTH_SHORT).show();
         Log.i("TAG", "sendProcess: "+messageDetails.getMessage()+" "+messageDetails.getTime());
             messageChatAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
    }
    private void iniilizeAdapter() {
        messageChatAdapter = new MessageChatAdapter(this,list);
        recyclerView.setAdapter(messageChatAdapter);
        recyclerView.setHasFixedSize(true);
    }
}