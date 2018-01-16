package com.example.vikas.jecchat;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class chatF extends Fragment {


    private RecyclerView recyclerView;
    private View mview;
    private DatabaseReference mRef;
    private DatabaseReference mRootref;


    public chatF() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mview = inflater.inflate(R.layout.fragment_chat, container, false);

        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            Intent intent=new Intent(getContext(),WelcomePage.class);
            startActivity(intent);
        }
        else{
        recyclerView = (RecyclerView) mview.findViewById(R.id.recyclerview_chat_fragment);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRef = FirebaseDatabase.getInstance().getReference().child("chat").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mRootref = FirebaseDatabase.getInstance().getReference();}


        return mview;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            Intent intent=new Intent(getContext(),WelcomePage.class);
            startActivity(intent);
        }
        else {

            Query chatQuery = mRef.orderByChild("timeStamp");

            FirebaseRecyclerAdapter<chat, chatListViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<chat, chatListViewHolder>(

                    chat.class,
                    R.layout.users_layout,
                    chatListViewHolder.class,
                    chatQuery

            ) {
                @Override
                protected void populateViewHolder(final chatListViewHolder viewHolder, chat model, int position) {
                    final String chatFriendId = getRef(position).getKey();
                    Log.i("group key",chatFriendId);
                    if(!(chatFriendId.equals("me")||chatFriendId.equals("ce")||chatFriendId.equals("ee")||chatFriendId.equals("ec")||chatFriendId.equals("cse")||chatFriendId.equals("ip"))) {

                        mRootref.child("users").child(chatFriendId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                final String name = dataSnapshot.child("name").getValue().toString();
                                viewHolder.setName(name);
                                final String image = dataSnapshot.child("thumbImage").getValue().toString();
                                viewHolder.setImage(image, getContext());

                                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                        chatIntent.putExtra("name", name);
                                        chatIntent.putExtra("thumbImage", image);
                                        chatIntent.putExtra("ID", chatFriendId);
                                        startActivity(chatIntent);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }


                        });
                        viewHolder.setStatus();

                    }

                    else {

                        mRootref.child("branch").child(chatFriendId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final String name=dataSnapshot.child("name").getValue().toString();
                                final String image=dataSnapshot.child("thumbImage").getValue().toString();
                                viewHolder.setName(name);
                                viewHolder.setImage(image,getContext());

                                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                        chatIntent.putExtra("name", name);
                                        chatIntent.putExtra("thumbImage", image);
                                        chatIntent.putExtra("ID", chatFriendId);
                                        startActivity(chatIntent);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });





                    }


                }
            };

            recyclerView.setAdapter(firebaseRecyclerAdapter);
        }
    }

    public static class chatListViewHolder extends RecyclerView.ViewHolder {

        View view;

        public chatListViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
        }

        public void setName(String name) {
            TextView chatUserName = (TextView) view.findViewById(R.id.displayUserName);
            chatUserName.setText(name);
        }

        public void setImage(String image, Context context) {
            CircleImageView chatFriendImage = (CircleImageView) view.findViewById(R.id.circleImageView2);
            Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(chatFriendImage);
        }

        public void setStatus(){
            TextView status=(TextView)view.findViewById(R.id.displayUserStatus);
            status.setVisibility(View.INVISIBLE);
        }
    }
}

