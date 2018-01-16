package com.example.vikas.jecchat;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class timeLineF extends Fragment {


    private RecyclerView recyclerView;

    private DatabaseReference mrootRef;
    private PostAdapter adapter;
    private List<post> list=new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference mRef;

    public timeLineF() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mMainView;
        mMainView=inflater.inflate(R.layout.fragment_time_line, container, false);


        mrootRef= FirebaseDatabase.getInstance().getReference();
        mRef=mrootRef.child("timeLinePost").child("timelinePostAll");
        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            Intent mainIntent=new Intent(getContext(),WelcomePage.class);
            startActivity(mainIntent);
        }
        else {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


            recyclerView = (RecyclerView) mMainView.findViewById(R.id.timelineRecyclerView);
            recyclerView.setHasFixedSize(true);
            linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setReverseLayout(true);
            recyclerView.setLayoutManager(linearLayoutManager);
            adapter = new PostAdapter(list);
           recyclerView.setAdapter(adapter);
           loadPost();




        }

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();



      /*  FirebaseRecyclerAdapter<post,postViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<post, postViewHolder>(

                post.class,
                R.layout.time_line_post_layout,
                postViewHolder.class,
                mRef

        ) {
            @Override
            protected void populateViewHolder(final postViewHolder viewHolder, post model, int position) {

                String from=model.getFrom();
                viewHolder.setTime(model.getTime());
                viewHolder.setMessage(model.getMessage());
                viewHolder.setLikes(model.getLikes());
                viewHolder.setComments(model.getComments());

                mrootRef.child("users").child(from).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.setName(dataSnapshot.child("name").getValue().toString());
                        viewHolder.setImage(dataSnapshot.child("thumbImage").getValue().toString(),getContext());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        int count = 0;
        if (firebaseRecyclerAdapter != null) {
            count = firebaseRecyclerAdapter.getItemCount();
        }

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        linearLayoutManager.scrollToPositionWithOffset(count-1,count);*/
        


    }


   /* public static class postViewHolder extends RecyclerView.ViewHolder {

        View view;
        public postViewHolder(View itemView) {
            super(itemView);
            this.view=itemView;
        }


        public void setImage(final String thumbImage, final Context context) {
            final CircleImageView image=(CircleImageView)view.findViewById(R.id.timelinePostImage);
            Picasso.with(context).load(thumbImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
             Picasso.with(context).load(thumbImage).placeholder(R.drawable.user).into(image);
                }
            });

        }

        public void setName(String name) {
            TextView pname=(TextView)view.findViewById(R.id.timelinePostName);
            pname.setText(name);

        }

        public void setComments(String comments) {
            Button cmnt=(Button) view.findViewById(R.id.comment);
            cmnt.setText(comments+" comments");
        }

        public void setLikes(String likes) {
            Button lk=(Button)view.findViewById(R.id.like);
            lk.setText(likes+" likes");
        }

        public void setMessage(String message) {
            TextView msg=(TextView)view.findViewById(R.id.timelinePostMessage);
            msg.setText(message);
        }

        public void setTime(String time) {
            TextView tm=(TextView)view.findViewById(R.id.timeLinePostTime);
            tm.setText(time);

        }
    }*/


      private void loadPost() {

        DatabaseReference ref= mrootRef.child("timeLinePost").child("timelinePostAll");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                post p=dataSnapshot.getValue(post.class);

                list.add(p);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(list.size()-1);




            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
