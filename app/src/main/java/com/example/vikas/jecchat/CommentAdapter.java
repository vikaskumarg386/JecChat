package com.example.vikas.jecchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by vikas on 8/1/18.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>{


    private List<Comments> list;
    private DatabaseReference mRootRef;
    private  int noOflikes;

    public CommentAdapter(List<Comments> list) {
        this.list = list;
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        public TextView displayPostName;
        public TextView postTime;
        public TextView postMessage;
        public CircleImageView postUserImage;
        public View itemView;
        public ImageButton like;
        public ImageButton comment;
        public String st = "not_liked";
        public TextView no_like;
        public TextView no_comments;


        public CommentViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            displayPostName = (TextView) itemView.findViewById(R.id.timelinePostName);
            postMessage = (TextView) itemView.findViewById(R.id.timelinePostMessage);
            postTime = (TextView) itemView.findViewById(R.id.timeLinePostTime);
            postUserImage = (CircleImageView) itemView.findViewById(R.id.timelinePostImage);
            like = (ImageButton) itemView.findViewById(R.id.like);
            comment = (ImageButton) itemView.findViewById(R.id.comment);
            no_comments = (TextView) itemView.findViewById(R.id.no_comments);
            no_like = (TextView) itemView.findViewById(R.id.no_likes);

            mRootRef = FirebaseDatabase.getInstance().getReference();

        }
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_line_post_layout, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommentViewHolder holder, int position) {
        final Comments c = list.get(position);


        holder.no_comments.setVisibility(View.INVISIBLE);
        holder.comment.setVisibility(View.INVISIBLE);
        holder.postMessage.setText(c.getComment_Text());
        holder.postTime.setText(c.getTime());
        holder.no_like.setText(c.getLikes()+" likes");
        String from = c.getFrom();
        noOflikes=Integer.parseInt(c.getLikes());

        mRootRef.child("users").child(from).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("thumbImage").getValue().toString();
                holder.displayPostName.setText(name);
                Picasso.with(holder.postUserImage.getContext()).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(holder.postUserImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(holder.postUserImage.getContext()).load(image).placeholder(R.drawable.user).into(holder.postUserImage);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userUid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                noOflikes=Integer.parseInt(c.getLikes());

                mRootRef.child("timeLinePost").child("timelinePostAll").child(c.getPush_key()).child("userComments").child(c.getcPush_key()).child("userLikes").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(userUid))
                        {
                             holder.st=dataSnapshot.child(userUid).child("like").getValue().toString();

                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                if(holder.st.equals("liked")){

                    mRootRef.child("timeLinePost").child("timelinePostAll").child(c.getPush_key()).child("userComments").child(c.getcPush_key()).child("userLikes").child(userUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                mRootRef.child("timeLinePost").child("timelinePostAll").child(c.getPush_key()).child("userComments").child(c.getcPush_key()).child("likes").setValue(String.valueOf(noOflikes-1)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            holder.no_like.setText(String.valueOf(noOflikes-1)+" likes");
                                            noOflikes--;
                                            holder.st="not_liked";
                                        }
                                    }
                                });

                            }
                        }
                    });


                }


                else
                {


                        mRootRef.child("timeLinePost").child("timelinePostAll").child(c.getPush_key()).child("userComments").child(c.getcPush_key()).child("userLikes").child(userUid).child("like").setValue("liked").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    mRootRef.child("timeLinePost").child("timelinePostAll").child(c.getPush_key()).child("userComments").child(c.getcPush_key()).child("likes").setValue(String.valueOf(noOflikes+1)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                holder.no_like.setText(String.valueOf(noOflikes+1)+" likes");
                                                noOflikes++;
                                                holder.st="liked";
                                            }
                                        }
                                    });


                                }
                            }
                        });


                }








            }
        });


    }

    @Override
    public int getItemCount() {


        if(list==null){

            return 0;
        }

        else
        return list.size();
    }


}
