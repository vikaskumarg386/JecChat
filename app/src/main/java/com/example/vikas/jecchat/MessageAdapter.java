package com.example.vikas.jecchat;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by vikas on 30/12/17.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{



   private DatabaseReference mRootref;
    private List<Message> messageList;


    public MessageAdapter (List<Message> messageList){
        this.messageList=messageList;
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView messageText;
        public TextView timeView;
        public RelativeLayout rLayout;
        public ImageView imageMessage;
        public View mView;
        public TextView nameofSender;
        public CircleImageView circleImageView;


        public MessageViewHolder(View itemView) {
            super(itemView);
            this.mView=itemView;

            messageText=(TextView)itemView.findViewById(R.id.message_text);
            timeView=(TextView) itemView.findViewById(R.id.time);
            rLayout=(RelativeLayout)itemView.findViewById(R.id.relative_layout);
            imageMessage=(ImageView)itemView.findViewById(R.id.imageMessage);
            mRootref= FirebaseDatabase.getInstance().getReference();
            nameofSender=(TextView)itemView.findViewById(R.id.name_text_layout);
            circleImageView=(CircleImageView)itemView.findViewById(R.id.message_profile_layout);

        }
    }


    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.message_chat_layout,parent,false);

        return new MessageViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {
        Message c=messageList.get(position);

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String fromUserId = c.getFrom();
        holder.timeView.setText(c.getTimestamp());

        mRootref.child("users").child(fromUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.nameofSender.setText(dataSnapshot.child("name").getValue().toString());
                Picasso.with(holder.circleImageView.getContext()).load(dataSnapshot.child("thumbImage").getValue().toString()).placeholder(R.drawable.user).into(holder.circleImageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        if(c.getType().equals("text")) {

            holder.imageMessage.setVisibility(View.INVISIBLE);
            holder.messageText.setVisibility(View.VISIBLE);

            holder.messageText.setText(c.getMessage());


            if (currentUserId.equals(fromUserId)) {

                holder.messageText.setTextColor(Color.WHITE);




            } else {
                holder.messageText.setTextColor(Color.BLACK);
            }


        }

        if(c.getType().equals("image")){

            holder.messageText.setVisibility(View.INVISIBLE);
            holder.imageMessage.setVisibility(View.VISIBLE);

            Picasso.with(holder.imageMessage.getContext()).load(c.getMessage()).into(holder.imageMessage);
            if (currentUserId.equals(fromUserId)) {

                holder.imageMessage.setBackgroundResource(R.drawable.chat_text_shap);


            } else {

                holder.imageMessage.setBackgroundResource(R.drawable.receive_message_shap);
            }


        }



    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }



}
