package com.example.vikas.jecchat;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class requestF extends Fragment {

    private DatabaseReference mRequestRef;
    private DatabaseReference mRootRef;
    private RecyclerView recyclerView;

    public requestF() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mMainView;

        mMainView=inflater.inflate(R.layout.fragment_request, container, false);
        mRootRef=FirebaseDatabase.getInstance().getReference();
        mRequestRef= mRootRef.child("friendRequest").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        recyclerView=(RecyclerView)mMainView.findViewById(R.id.request_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }


    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<request,requestViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<request, requestViewHolder>(

                request.class,
                R.layout.users_layout,
                requestViewHolder.class,
                mRequestRef


        ) {
            @Override
            protected void populateViewHolder(final requestViewHolder viewHolder, request model, int position) {

                final String requestUserId=getRef(position).getKey();
                String reqType=model.getRequest_type();
                if(reqType.equals("received")) {
                    mRootRef.child("users").child(requestUserId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String name = dataSnapshot.child("name").getValue().toString();
                            String image = dataSnapshot.child("thumbImage").getValue().toString();
                            viewHolder.setName(name);
                            viewHolder.setImage(image, getContext());
                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent profileIntet=new Intent(getContext(),UserProfile_Activity.class);
                                    profileIntet.putExtra("Id",requestUserId);
                                    startActivity(profileIntet);
                                }
                            });


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                viewHolder.setStatus();

            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class requestViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public requestViewHolder(View itemView) {
            super(itemView);

            this.mView=itemView;
        }

        public void setName(String name){
            TextView sname=(TextView)mView.findViewById(R.id.displayUserName);
            sname.setText(name);
        }

        public void setImage(String image, Context context){
            CircleImageView img=(CircleImageView)mView.findViewById(R.id.circleImageView2);
            Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(img);
        }

        public void setStatus(){
            TextView status=(TextView)mView.findViewById(R.id.displayUserStatus);
            status.setVisibility(View.INVISIBLE);
        }
    }
}
