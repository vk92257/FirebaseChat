package com.lynhill.firebasechat.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lynhill.firebasechat.Chats;
import com.lynhill.firebasechat.R;
import com.lynhill.firebasechat.model.MessageDetails;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MessageChatAdapter extends RecyclerView.Adapter<MessageChatAdapter.MyViewHolder> {
    private Context context;
    private int lastPosition = -1;
    private ArrayList<MessageDetails> strings ;
    private String TAG="MessageCahtAdapter";
    // Current playback position (in milliseconds).
    private int mCurrentPosition = 0;
    // Tag for the instance state bundle.
    private static final String PLAYBACK_TIME = "play_time";
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_chat,parent,false);
        return new MyViewHolder(view);
    }
    public MessageChatAdapter(Context context, ArrayList<MessageDetails> strings) {
        setHasStableIds(true);
        this.context = context;
        this.strings = strings;
    }
    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            @SuppressLint("ResourceType")
            Animation animation = AnimationUtils.loadAnimation(context, R.transition.bounce);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        @SuppressLint("ResourceType")
//        Animation animation = AnimationUtils.loadAnimation(context, R.transition.bounce);
        MediaController controller = new MediaController(context);

        MessageDetails  userData = strings.get(position);
//           Log.i("TAG", "onBindViewHolder: "+userData.getId()+"------------------"+Chats.user.getUid()+"_"+Chats.uid);
         String id = Chats.user.getUid()+"_"+Chats.uid;
         if (userData.getId()!=null){
             if (userData.getId().equals(id)){
                 // Set up the media controller widget and attach it to the video view.
                 controller.setMediaPlayer(holder.myVideoView);
                 holder.myVideoView.setMediaController(controller);

                 holder.senderTextTime.setVisibility(View.GONE);
                 holder.sender.setVisibility(View.GONE);
                 holder.senderVideoView.setVisibility(View.GONE);
                 holder.senderTextImage.setVisibility(View.GONE);
                 holder.myTextTime.setText(userData.getTime());
                 if (userData.getImage_uri() != null){
                     holder.myText.setVisibility(View.GONE);
                     holder.my.setVisibility(View.GONE);
                     holder.myVideoView.setVisibility(View.GONE);
                     holder.myAvi.setVisibility(View.VISIBLE);
                     holder.myAvi.show();


                     Picasso.get()
                             .load(userData.getImage_uri())
                             .into(holder.myTextImage, new Callback() {
                                 @Override
                                 public void onSuccess() {
                                     holder.myAvi.hide();
                                 }

                                 @Override
                                 public void onError(Exception e) {
                                    holder.myAvi.hide();
                                 }
                             });
//                     Log.i(TAG, "onBindViewHolder: "+userData.getImage_uri());



                 }

                 else if (userData.getVideo_uri() != null){
                     holder.myDownload.setVisibility(View.VISIBLE);
                        holder.myDownload.bringToFront();
                        holder.myDownload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                holder.myVideoView.setVideoPath(userData.getVideo_uri());
                                holder.myDownload.setVisibility(View.GONE);
                                holder.myAviVideo.show();
                                holder.myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        mp.start();
                                        mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                                            @Override
                                            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

                                                holder.myProgress.setText(""+i);
                                                Log.i(TAG, "onBufferingUpdate: "+i);
                                            }
                                        });

                                        mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {

                                            @Override
                                            public void onVideoSizeChanged(MediaPlayer mp, int arg1, int arg2) {
                                                // TODO Auto-generated method stub
                                                Log.e(TAG, "Changed");
                                                holder.myProgress.setVisibility(View.GONE);
                                                holder.myProgress.setVisibility(View.GONE);
                                                holder.myAviVideo.hide();
                                                mp.start();
                                            }
                                        });


                                    }
                                });

                            }
                        });


                     holder.myText.setVisibility(View.GONE);
                     holder.my.setVisibility(View.GONE);
                     holder.myTextImage.setVisibility(View.GONE);

                 }

                 else{
//                        setAnimation(holder.my,position);
                     holder.myTextImage.setVisibility(View.GONE);
                     holder.myVideoView.setVisibility(View.GONE);
                     holder.myText.setText(userData.getMessage());

                 }



             }
             else {

                 // Set up the media controller widget and attach it to the video view.
                 controller.setMediaPlayer(holder.senderVideoView);
                 holder.senderVideoView.setMediaController(controller);


                 holder.my.setVisibility(View.GONE);
                 holder.myTextTime.setVisibility(View.GONE);
                 holder.myVideoView.setVisibility(View.GONE);
                 holder.myTextImage.setVisibility(View.GONE);
                 holder.senderTextTime.setText(userData.getTime());
                 if (userData.getImage_uri() != null){
                     holder.senderText.setVisibility(View.GONE);
                     holder.senderVideoView.setVisibility(View.GONE);
                     holder.sender.setVisibility(View.GONE);
                     holder.senderAvi.setVisibility(View.VISIBLE);
                     holder.senderAvi.show();
                     Picasso.get().load(userData.getImage_uri())
                             .into(holder.senderTextImage, new Callback() {
                                 @Override
                                 public void onSuccess() {
                                        holder.senderAvi.hide();
                                 }

                                 @Override
                                 public void onError(Exception e) {
                                     holder.senderAvi.hide();
                                 }
                             });
//                     Log.i(TAG, "onBindViewHolder: "+userData.getImage_uri());
                 } else if (userData.getVideo_uri() != null){
                    holder.senderDownload.setVisibility(View.VISIBLE);
                    holder.senderDownload.bringToFront();
                    holder.senderDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            holder.senderVideoView.setVideoPath(userData.getVideo_uri());
                            holder.senderDownload.setVisibility(View.GONE);
                            holder.senderAviVideo.setVisibility(View.VISIBLE);
                            holder.senderAviVideo.show();
                            holder.senderVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {



                                    mp.start();
                                    mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                                        @Override
                                        public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

                                            holder.senderProgress.setText(""+i);
                                            Log.i(TAG, "onBufferingUpdate: "+i);
                                        }
                                    });

                                    mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {

                                        @Override
                                        public void onVideoSizeChanged(MediaPlayer mp, int arg1, int arg2) {
                                            // TODO Auto-generated method stub
                                            Log.e(TAG, "Changed");
                                            holder.senderProgress.setVisibility(View.GONE);
                                            holder.senderProgress.setVisibility(View.GONE);
                                            holder.senderAviVideo.hide();
                                            mp.start();
                                        }
                                    });


                                }
                            });
                        }
                    });

                     holder.senderText.setVisibility(View.GONE);
                     holder.sender.setVisibility(View.GONE);
                     holder.senderTextImage.setVisibility(View.GONE);

                 }else{

                     holder.senderTextImage.setVisibility(View.GONE);
                     holder.senderVideoView.setVisibility(View.GONE);
                     holder.senderText.setText(userData.getMessage());
                 }

             }
         }
         else {
             holder.my.setVisibility(View.GONE);
             holder.sender.setVisibility(View.GONE);
             holder.myTextTime.setVisibility(View.GONE);
             holder.senderTextTime.setVisibility(View.GONE);
            holder.myVideoView.setVisibility(View.GONE);
            holder.senderVideoView.setVisibility(View.GONE);
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

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

//    @Override
//    public void onViewAttachedToWindow(@NonNull MyViewHolder holder) {
//        super.onViewAttachedToWindow(holder);
//        @SuppressLint("ResourceType")
//        Animation animation = AnimationUtils.loadAnimation(context,R.transition.bounce);
//        holder.sender.setAnimation(animation);
//        holder.my.setAnimation(animation);
//    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView myText,senderText,myTextTime,senderTextTime,myProgress,senderProgress;
        ImageView myTextImage,senderTextImage,myDownload,senderDownload;
        FrameLayout my,sender;
        AVLoadingIndicatorView myAvi, senderAvi,myAviVideo, senderAviVideo;
        private VideoView myVideoView,senderVideoView;
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
            myAvi = itemView.findViewById(R.id.my_avi);
            senderAvi = itemView.findViewById(R.id.sender_avi);
            senderVideoView = itemView.findViewById(R.id.sender_videoview);
            myVideoView = itemView.findViewById(R.id.my_videoview);
            myAviVideo = itemView.findViewById(R.id.my_avi_video);
            senderAviVideo = itemView.findViewById(R.id.sender_avi_video);
            myProgress = itemView.findViewById(R.id.my_buffer);
            senderProgress =itemView.findViewById(R.id.sender_buffer);
            myDownload = itemView.findViewById(R.id.my_download);
            senderDownload = itemView.findViewById(R.id.sender_download);
        }

    }


}
