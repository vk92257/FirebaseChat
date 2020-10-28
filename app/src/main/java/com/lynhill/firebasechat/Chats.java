package com.lynhill.firebasechat;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.wang.avi.AVLoadingIndicatorView;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Chats extends AppCompatActivity {
    private ImageView send,back,gallery , video;
    private EditText input;
    LinearLayout attachmentConten ;
    private AVLoadingIndicatorView indicatorView;
    private boolean flag = true;
        private TextView mTextView;
    private RecyclerView recyclerView;
    private ArrayList<MessageDetails> list  ;
    private FirebaseAuth firebaseAuth ;
    private String s="";
    private StorageReference mStorageRef;
    private ImageView attachment;
    private static int GALLRY_CODE = 1;
    private  MessageChatAdapter messageChatAdapter;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
  private   DatabaseReference myRef = database.getReference("all_messages");
  private   DatabaseReference chatHistory = database.getReference("chat_history");

    public static FirebaseUser user;
  public  static String uid;
  private ValueEventListener valueEventListener;
  private ValueEventListener chatHistoryListenre;

  private ArrayList<Uri> imageUri;
    private int VIDEO_CODE=2;
    private ArrayList<Uri> videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        list = new ArrayList<>();
        mTextView = (TextView) findViewById(R.id.text);
        if (getIntent()!= null) {
            Intent intent = getIntent();
                uid = intent.getStringExtra("uid");
                String userName = intent.getStringExtra("userName");
        mTextView.setText(userName);
        }
        findViews();
        messageRecieve();

    }

    private void chatHistory() {
        chatHistory.child(user.getUid()+"_"+uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Log.i("TAG", "onDataChange: "+dataSnapshot.getKey()+" value"+dataSnapshot.getValue());
                    MessageDetails messageDetails = new MessageDetails();
                    messageDetails = dataSnapshot.getValue(MessageDetails.class);
                    list.add(messageDetails);
                }
                indicatorView.setVisibility(View.INVISIBLE);
                indicatorView.hide();
                messageChatAdapter.notifyDataSetChanged();
//                chatHistory.removeEventListener(chatHistoryListenre);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });


//        chatHistory.child(user.getUid()+"_"+uid).addValueEventListener(chatHistoryListenre = new ValueEventListener() {
//             @Override
//             public void onDataChange(@NonNull DataSnapshot snapshot) {
//                 for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
//                     Log.i("TAG", "onDataChange: "+dataSnapshot.getKey()+" value"+dataSnapshot.getValue());
//                    MessageDetails messageDetails = new MessageDetails();
//                    messageDetails = dataSnapshot.getValue(MessageDetails.class);
//                  list.add(messageDetails);
//                 }
//                 messageChatAdapter.notifyDataSetChanged();
//             chatHistory.removeEventListener(chatHistoryListenre);
//             }
//             @Override
//             public void onCancelled(@NonNull DatabaseError error) {
//
//             }
//         });

    }

    private void messageRecieve() {
        myRef.child(uid+"_"+user.getUid()).addValueEventListener( valueEventListener = new ValueEventListener() {
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
               if(!flag) list.add(messageDetails);
                if (messageChatAdapter == null) iniilizeAdapter();
                messageChatAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
             flag = false;
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == GALLRY_CODE && resultCode == RESULT_OK){
                    imageUri = new ArrayList<>();
                    if(data.getClipData() != null) {
                    int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                    for(int i = 0; i < count; i++) {
                       Uri uri = data.getClipData().getItemAt(i).getUri();
                        Log.i("TAG", "onActivityResult: " + "false" + count);
                        Log.i("TAG", "onActivityResult: " + uri.toString());
                            imageUri.add(uri);
                            //do something with the image (save it to some directory or whatever you need to do with it here)
                    }
                    saveImage();
                    }
                else if (data.getData() != null){
                    Log.i("TAG", "onActivityResult: "+"true");
                    Uri uri = data.getData();
                    Log.i("TAG", "onActivityResult: "+uri);
                imageUri.add(uri);
                    saveImage();
                }
            }
        if (requestCode == VIDEO_CODE && resultCode == RESULT_OK){
            videoUri = new ArrayList<>();
            if(data.getClipData() != null) {
                int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                for(int i = 0; i < count; i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
//                    Log.i("TAG", "onActivityResult: " + uri.toString());
                    videoUri.add(uri);
                    //do something with the image (save it to some directory or whatever you need to do with it here)
                }
                saveVideo();
            }
            else if (data.getData() != null){
                Uri uri = data.getData();
//                Log.i("TAG", "onActivityResult: "+uri);
                videoUri.add(uri);
                saveVideo();
            }
        }
    }

    private void saveVideo() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        if (!videoUri.isEmpty()&& videoUri != null){
            for (Uri url : videoUri) {
                Log.i("TAG", "saveVideo: "+url.toString());
                File destinationPath = new File("/storage/emulated/0/DCIM/myvideo");
                destinationPath.mkdir();
                File file = new File(destinationPath.getAbsolutePath());
                Toast.makeText(Chats.this, "folder: " + file, Toast.LENGTH_SHORT).show();
//                VideoCompressor.start(url.getPath().toString(), destinationPath.getAbsolutePath(), new CompressionListener() {
//                    @Override
//                    public void onStart() {
//                        // Compression start
//                        // indicatorView.show();
//                        Log.i("TAG", "onStart: ");
//                    }
//                    @Override
//                    public void onSuccess() {
//                        // On Compression success
//                   indicatorView.hide();
//                        Log.i("TAG", "onStart: ");
//                    }
//
//                    @Override
//                    public void onFailure(String failureMessage) {
//                        Log.i("TAG", "onStart: "+failureMessage.toString());
//                        // On Failure
//                    }
//
//                    @Override
//                    public void onProgress(float v) {
//                        // Update UI with progress value
//                        runOnUiThread(new Runnable() {
//                            public void run() {
//                                Log.i("TAG", "onStart: ");
//                                indicatorView.show();
//                                 }
//                        });
//                    }
//                    @Override
//                    public void onCancelled() {
//                        // On Cancelled
//                    }
//                }, VideoQuality.MEDIUM, false, false);


                StorageReference riversRef = mStorageRef.child("videos")
                        .child("video_"+String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()))+".mp4");
                riversRef.putFile(url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                MessageDetails messageDetails = new MessageDetails();
                                messageDetails.setVideo_uri(uri.toString());
                                messageDetails.setTime(java.text.DateFormat.getDateTimeInstance().format(new Date()));
//                            Log.i(TAG, "onSuccess: +"+user.getUid()+"_"+uid);
                                messageDetails.setId(user.getUid()+"_"+uid);
                                myRef.child(user.getUid()+"_"+uid).push().setValue(messageDetails);
                                chatHistory.child(user.getUid()+"_"+uid).push().setValue(messageDetails);
                                chatHistory.child(uid+"_"+user.getUid()).push().setValue(messageDetails);
                                list.add(messageDetails);
                                messageChatAdapter.notifyDataSetChanged();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("TAG", "onFailure: "+e.toString());
                        indicatorView.hide();
                    }
                });



            }
        }
    }

    public static String getRealPathFromURI_API19(Context context, Uri uri) {
        String filePath = "";

        Pattern p = Pattern.compile("(\\d+)$");
        Matcher m = p.matcher(uri.toString());
        if (!m.find()) {
//            Log.e(ImageConverter.class.getSimpleName(), "ID for requested image not found: " + uri.toString());
            return filePath;
        }
        String imgId = m.group();
        String[] column = { MediaStore.Images.Media.DATA };
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ imgId }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();

        return filePath;
        }


   
    private void saveImage() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
       if (!imageUri.isEmpty()&& imageUri != null){

           for (Uri url : imageUri) {
               Log.i("TAG", "saveImage: inside loop "+url.toString());
//               final File file = new File(Environment.getExternalStorageDirectory(), "read.me");
//               Uri uri = Uri.fromFile(file);
//               File auxFile = new File(uri.toString());
//               File l =  saveBitmapToFile(auxFile);
//               Log.i("TAG", "saveImage: "+l.getAbsolutePath());
               StorageReference riversRef=  mStorageRef.child("images")
                       .child("images_"+String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()))+".jpg");
               riversRef.putFile(url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                           @Override
                           public void onSuccess(Uri uri) {
                               MessageDetails messageDetails = new MessageDetails();
                               messageDetails.setImage_uri(uri.toString());
                               messageDetails.setTime(java.text.DateFormat.getDateTimeInstance().format(new Date()));
//                        Log.i(TAG, "onSuccess: +"+user.getUid()+"_"+uid);
                               messageDetails.setId(user.getUid()+"_"+uid);
                               myRef.child(user.getUid()+"_"+uid).push().setValue(messageDetails);

                               chatHistory.child(user.getUid()+"_"+uid).push().setValue(messageDetails);
                               chatHistory.child(uid+"_"+user.getUid()).push().setValue(messageDetails);

                               list.add(messageDetails);

                               messageChatAdapter.notifyDataSetChanged();

                           }
                       });

                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Log.i("TAG", "onFailure: "+e.toString());
                       indicatorView.hide();
                   }
               });
           }
       }else {
           Log.i("TAG", "saveImage cause of error : image uri array list is empty or null");
       }

    }
    @Override
    protected void onStart() {
        super.onStart();
       if (flag)chatHistory();

    }

    private void findViews() {
        indicatorView = findViewById(R.id.avi);
        attachment = findViewById(R.id.attachment);
        indicatorView.setVisibility(View.VISIBLE);
        indicatorView.show(); attachment.bringToFront();
        back = findViewById(R.id.back_button);
        attachmentConten = findViewById(R.id.attachment_content);
        gallery = findViewById(R.id.camera);
        video = findViewById(R.id.video);
        gallery.bringToFront();
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(intent,GALLRY_CODE);
                Toast.makeText(Chats.this, "gallery", Toast.LENGTH_SHORT).show();
                attachmentConten.setVisibility(View.GONE);
            }
        });
        video.bringToFront();
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("video/*");
                startActivityForResult(intent,VIDEO_CODE);
                attachmentConten.setVisibility(View.GONE);
                Toast.makeText(Chats.this, "video", Toast.LENGTH_SHORT).show();
            }
        });

       back.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivity(new Intent(getBaseContext(),ChatActivity.class));
                finish();
           }
       });
       attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               if (attachmentConten.getVisibility() == View.GONE)attachmentConten.setVisibility(View.VISIBLE);
                    else attachmentConten.setVisibility(View.GONE);
            }
        });
        recyclerView = findViewById(R.id.message_recycler);
       LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
       recyclerView.setLayoutManager(linearLayoutManager);
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

                send.setImageResource(R.drawable.send_message_blue);
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
            if (!s.isEmpty() && uid != null){
                sendProcess();
                send.setImageDrawable(getResources().getDrawable(R.drawable.send_message));
            }
            }
        });

    }

    private void sendProcess() {
        MessageDetails messageDetails = new MessageDetails();
        messageDetails.setMessage(s);
        messageDetails.setTime(java.text.DateFormat.getDateTimeInstance().format(new Date()));
        messageDetails.setId(user.getUid()+"_"+uid);
        myRef.child(user.getUid()+"_"+uid).push().setValue(messageDetails);

        chatHistory.child(user.getUid()+"_"+uid).push().setValue(messageDetails);
        chatHistory.child(uid+"_"+user.getUid()).push().setValue(messageDetails);
        list.add(messageDetails);
        iniilizeAdapter();
        messageChatAdapter.notifyDataSetChanged();
        input.setText("");
//        Toast.makeText(Chats.this, ""+s, Toast.LENGTH_SHORT).show();
         Log.i("TAG", "sendProcess: "+messageDetails.getMessage()+" "+messageDetails.getTime());
         recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());

    }
    private void iniilizeAdapter() {
        messageChatAdapter = new MessageChatAdapter(this,list);
        recyclerView.setAdapter(messageChatAdapter);
        recyclerView.setHasFixedSize(true);

    }

    public  boolean  isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag = true;
        imageUri = null;
        myRef.removeEventListener(valueEventListener);
    }
}