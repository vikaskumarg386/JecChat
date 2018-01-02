package com.example.vikas.jecchat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class AllUsers_Activity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private RecyclerView mUserList;
    private DatabaseReference userDatabse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users_);

        mtoolbar =(android.support.v7.widget.Toolbar)findViewById(R.id.allUser_toolBar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userDatabse= FirebaseDatabase.getInstance().getReference().child("users");
        userDatabse.keepSynced(true);

        mUserList=(RecyclerView)findViewById(R.id.users_RecyclerView);
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        userDatabse.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue("true");

        FirebaseRecyclerAdapter<users,usersViewHolder> firebaseRecyclerAdapter=
        new FirebaseRecyclerAdapter<users, usersViewHolder>(
                users.class,
                R.layout.users_layout,
                usersViewHolder.class,
                userDatabse

        ) {
            @Override
            protected void populateViewHolder(usersViewHolder viewHolder, users model, int position) {

                viewHolder.setName(model.getName());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setImage(model.getThumbImage(),getApplicationContext());

                final String id=getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent=new Intent(AllUsers_Activity.this,UserProfile_Activity.class);
                        profileIntent.putExtra("Id",id);
                        startActivity(profileIntent);
                    }
                });

            }
        };

        mUserList.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        userDatabse.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP);


    }

    public static class usersViewHolder extends RecyclerView.ViewHolder{

         View mView;

        public usersViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setName(String name){
            TextView usersNameView=(TextView)mView.findViewById(R.id.displayUserName);
            usersNameView.setText(name);

        }
        public void setStatus(String mstatus){
            TextView usersStatusView=(TextView)mView.findViewById(R.id.displayUserStatus);
            usersStatusView.setText(mstatus);

        }
        public void setImage(final String imageUrl, final Context ctx){
            final CircleImageView image=(CircleImageView)mView.findViewById(R.id.circleImageView2);
            Picasso.with(ctx).load(imageUrl).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                    Picasso.with(ctx).load(imageUrl).placeholder(R.drawable.user).into(image);
                }
            });
        }

    }
}
