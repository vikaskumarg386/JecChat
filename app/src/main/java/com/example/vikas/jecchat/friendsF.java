package com.example.vikas.jecchat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class friendsF extends Fragment {


    private RecyclerView recyclerView;
    private View mMainView;
    private DatabaseReference friendsRef;
    private DatabaseReference usersRef;



    public friendsF() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mMainView =inflater.inflate(R.layout.fragment_friends, container, false);
        recyclerView=(RecyclerView)mMainView.findViewById(R.id.friendsRecylerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        String userId= FirebaseAuth.getInstance().getCurrentUser().getUid();

        friendsRef= FirebaseDatabase.getInstance().getReference().child("friends").child(userId);
        usersRef=FirebaseDatabase.getInstance().getReference().child("users");


        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<friends,friendsViewHolder> friendsFirebaseRecylerAdapter=new FirebaseRecyclerAdapter<friends, friendsViewHolder>(

                friends.class,
                R.layout.users_layout,
                friendsViewHolder.class,
                friendsRef
        ) {
            @Override
            protected void populateViewHolder(final friendsViewHolder viewHolder, friends model, int position) {

                final String list_userId=getRef(position).getKey();


                usersRef.child(list_userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String friendName=dataSnapshot.child("name").getValue().toString();
                        final String friendThumbImage =dataSnapshot.child("thumbImage").getValue().toString();

                        if(dataSnapshot.hasChild("online")){
                        String onlineStatus=dataSnapshot.child("online").getValue().toString();
                            viewHolder.setOnlineStatus(onlineStatus);}

                        viewHolder.setName(friendName);
                        viewHolder.setImage(friendThumbImage,getContext());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CharSequence[] items={"open profile","send message"};

                                AlertDialog.Builder builder= new AlertDialog.Builder(getContext());
                                builder.setTitle("select option");
                                builder.setItems(items, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if(which==0){

                                            Intent profileIntent=new Intent(getContext(),UserProfile_Activity.class);
                                            profileIntent.putExtra("Id",list_userId);
                                            startActivity(profileIntent);
                                        }

                                        if(which==1){

                                            Intent chatIntent=new Intent(getContext(),ChatActivity.class);
                                            chatIntent.putExtra("ID",list_userId);
                                            chatIntent.putExtra("name",friendName);
                                            chatIntent.putExtra("thumbImage",friendThumbImage);
                                            startActivity(chatIntent);

                                        }
                                    }
                                });
                                builder.show();

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.setDate(model.getDate());




            }
        };
        recyclerView.setAdapter(friendsFirebaseRecylerAdapter);
    }

    public static class friendsViewHolder extends RecyclerView.ViewHolder {

        public View mView;

        public friendsViewHolder(View itemView) {
            super(itemView);
            this.mView=itemView;
        }

        public void setDate(String date){
            TextView dateText=(TextView)mView.findViewById(R.id.displayUserStatus);
            dateText.setText(date);
        }

        public void setName(String name){
            TextView nameText=(TextView)mView.findViewById(R.id.displayUserName);
            nameText.setText(name);
        }

        public void setImage(String img,Context context){

            CircleImageView imgView=(CircleImageView)mView.findViewById(R.id.circleImageView2);
            Picasso.with(context).load(img).placeholder(R.drawable.user).into(imgView);
        }

        public void setOnlineStatus(String b){

            ImageView img=(ImageView)mView.findViewById(R.id.onlineIcon);

            if(b.equals("true"))
            img.setVisibility(View.VISIBLE);
            else
                img.setVisibility(View.INVISIBLE);
        }
    }
}
