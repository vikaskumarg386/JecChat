package com.example.vikas.jecchat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by vikas on 4/1/18.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{

private DatabaseReference mrootRef;
private  String userId;
private List<post> postList;
private int likes;
private int no_of_comments;


    public PostAdapter(int no_of_comments ){
        this.no_of_comments=no_of_comments;
    }
    public PostAdapter(List<post> postList) {
        this.postList = postList;
    }

    public class PostViewHolder extends RecyclerView.ViewHolder{

        public TextView displayPostName;
        public TextView postTime;
        public TextView postMessage;
        public ImageView imagePost;
        public CircleImageView postUserImage;
        public View itemView;
        public ImageButton like;
        public ImageButton comment;
        public String st="not_liked" ;
        public TextView no_like;
        public TextView no_comments;


        public PostViewHolder(View itemView) {
            super(itemView);
            this.itemView=itemView;

            displayPostName=(TextView)itemView.findViewById(R.id.timelinePostName);
            postMessage=(TextView)itemView.findViewById(R.id.timelinePostMessage);
            imagePost=(ImageView)itemView.findViewById(R.id.imageView2);
            postTime=(TextView)itemView.findViewById(R.id.timeLinePostTime);
            postUserImage=(CircleImageView)itemView.findViewById(R.id.timelinePostImage);
            like=(ImageButton)itemView.findViewById(R.id.like);
            comment=(ImageButton) itemView.findViewById(R.id.comment);
            no_comments=(TextView)itemView.findViewById(R.id.no_comments);
            no_like=(TextView)itemView.findViewById(R.id.no_likes);

            mrootRef= FirebaseDatabase.getInstance().getReference();
            userId= FirebaseAuth.getInstance().getCurrentUser().getUid();

        }


    }


    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.time_line_post_layout,parent,false);

        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PostViewHolder holder, final int position) {

        final post p=postList.get(position);
        String from=p.getFrom();


        holder.postTime.setText(p.getTime());
        holder.postMessage.setText(p.getMessage());
        holder.no_comments.setText(p.getComments()+" comments");
        final String imagePostUrl=p.getImage();
        if(!imagePostUrl.equals("null")){
             // Toast.makeText(holder.postUserImage.getContext(),imagePostUrl,Toast.LENGTH_SHORT).show();
            Picasso.with(holder.imagePost.getContext()).load(imagePostUrl).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(holder.imagePost, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(holder.imagePost.getContext()).load(imagePostUrl).placeholder(R.drawable.user).into(holder.imagePost);
                }
            });


        }
        final String push_key=p.getPush_key();
        likes=Integer.parseInt(p.getLikes());

        mrootRef.child("timeLinePost").child("timelinePostAll").child(push_key).child("userLiked").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(userId))
                { holder.st=dataSnapshot.child(userId).child("liked").getValue().toString();
                    holder.no_like.setText(p.getLikes()+" likes");
                    holder.like.setAlpha((float)1);
                }
                else
                {
                    holder.no_like.setText(p.getLikes()+" likes");
                    holder.like.setAlpha((float)0.7);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  likes=Integer.parseInt(p.getLikes());

                mrootRef.child("timeLinePost").child("timelinePostAll").child(push_key).child("userLiked").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(userId))
                            { holder.st=dataSnapshot.child(userId).child("liked").getValue().toString();
                            }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                if(holder.st.equals("liked"))
                {
                    mrootRef.child("timeLinePost").child("timelinePostAll").child(push_key).child("userLiked").child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                mrootRef.child("timeLinePost").child("timelinePostAll").child(push_key).child("likes").setValue(String.valueOf(likes-1)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){
                                            holder.like.setAlpha((float)0.7);
                                        holder.no_like.setText(String.valueOf(likes-1)+" likes");
                                        likes--;
                                        holder.st="not_liked";}



                                    }
                                });


                            }

                        }
                    });





                }

                else{
                    mrootRef.child("timeLinePost").child("timelinePostAll").child(push_key).child("userLiked").child(userId).child("liked").setValue("liked").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                mrootRef.child("timeLinePost").child("timelinePostAll").child(push_key).child("likes").setValue(String.valueOf(likes+1)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){
                                            holder.like.setAlpha((float)1);
                                        holder.no_like.setText(String.valueOf(likes+1)+" likes");
                                        likes++;
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

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context=holder.comment.getContext();
                Intent intent=new Intent(context,Comment_Activity.class);
                intent.putExtra("push_key",push_key);
                context.startActivity(intent);




            }
        });


        mrootRef.child("users").child(from).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("name").getValue().toString();
                holder.displayPostName.setText(name);
                final String imageUrl=dataSnapshot.child("thumbImage").getValue().toString();
                Picasso.with(holder.postUserImage.getContext()).load(imageUrl).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(holder.postUserImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Picasso.with(holder.postUserImage.getContext()).load(imageUrl).placeholder(R.drawable.user).into(holder.postUserImage);

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {

        if(postList==null)
            return 0;
        else
            return postList.size();
    }




}
