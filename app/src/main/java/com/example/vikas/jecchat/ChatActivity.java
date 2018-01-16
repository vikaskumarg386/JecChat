package com.example.vikas.jecchat;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatActivity extends AppCompatActivity {

    private Toolbar chatToolbar1;
    private TextView displayName;
    private TextView lastSeen;
    private CircleImageView icon;
    private EditText chatText;
    private ImageButton sendButton;
    private ImageButton addButton;
    private DatabaseReference mrootRef;
    private String userId;
    private RecyclerView messageList;
    private String id;

    private static List<Message> mlist=new ArrayList<Message>();
    private LinearLayoutManager linearLayout;
    private MessageAdapter adapter;

    private SwipeRefreshLayout refreshLayout;
    private int GALLARY_PIC=1;
    private StorageReference mImageRef;


    private static int total_message_show_on_sreen=10;
    private int mCurrentPage=1;


    private int itemPos = 0;

    private String mLastKey = "";
    private String mPrevKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatToolbar1=(Toolbar)findViewById(R.id.layout1);
        setSupportActionBar(chatToolbar1);

        mrootRef= FirebaseDatabase.getInstance().getReference();

        ActionBar actionBar=getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        String name=getIntent().getStringExtra("name");
        String image=getIntent().getStringExtra("thumbImage");
        id=getIntent().getStringExtra("ID");
        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        getSupportActionBar().setTitle(name);

       LayoutInflater inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbarView=inflater.inflate(R.layout.chat_layout_for_img_icon,null);

        actionBar.setCustomView(actionbarView);

        displayName=(TextView)findViewById(R.id.displayName);
        displayName.setText(name);
        lastSeen=(TextView)findViewById(R.id.last_Seen);

        icon=(CircleImageView)findViewById(R.id.chatIcon);

        Picasso.with(this).load(image).placeholder(R.drawable.user).into(icon);
        if(!(id.equals("cse")||id.equals("me")||id.equals("ec")||id.equals("ce")||id.equals("ee")||id.equals("ip"))) {
           mrootRef.child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {
                   String online = dataSnapshot.child("online").getValue().toString();

                   if (online.equals("true"))
                       lastSeen.setText("online");
                   else {

                       // TimeAgo timeAgo=new TimeAgo();
                       long seenTime = Long.parseLong(online);
                       String lastSeenTime = TimeAgo.getTimeAgo(seenTime, getApplicationContext());
                       lastSeen.setText(lastSeenTime);
                   }

               }

               @Override
               public void onCancelled(DatabaseError databaseError) {

               }
           });
       }


        //saving messages

        sendButton=(ImageButton)findViewById(R.id.chat_send_btn);
        chatText=(EditText)findViewById(R.id.chat_message_view);
        messageList=(RecyclerView)findViewById(R.id.chatRecyclerView);
        refreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        mImageRef= FirebaseStorage.getInstance().getReference();
        addButton=(ImageButton)findViewById(R.id.chat_add_btn);

        adapter=new MessageAdapter(mlist);
        linearLayout=new LinearLayoutManager(this);
        messageList.setLayoutManager(linearLayout);
        messageList.setHasFixedSize(true);
        messageList.setAdapter(adapter);
        mlist.clear();
        loadMessages();



        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String text=chatText.getText().toString();
                if(!TextUtils.isEmpty(text)) {


                    Map saveChat=new HashMap();
                    saveChat.put("timeStamp",DateFormat.getDateTimeInstance().format(new Date()));
                    saveChat.put("seen",false);
                    mrootRef.child("chat").child(userId).child(id).setValue(saveChat);
                    mrootRef.child("chat").child(id).child(userId).setValue(saveChat);

                    DatabaseReference message_push_ref=FirebaseDatabase.getInstance().getReference().child("messages").child(userId).child(id).push();
                    String message_push_key=message_push_ref.getKey();

                    final Map saveText = new HashMap();
                    saveText.put("message", text);
                    saveText.put("seen", false);
                    saveText.put("timestamp", DateFormat.getDateTimeInstance().format(new Date()));
                    saveText.put("type","text");
                    saveText.put("from",userId);

                    if(!(id.equals("cse")||id.equals("me")||id.equals("ec")||id.equals("ce")||id.equals("ee")||id.equals("ip"))) {
                        Map map = new HashMap();
                        map.put("messages/" + userId + "/" + id + "/" + message_push_key, saveText);
                        map.put("messages/" + id + "/" + userId + "/" + message_push_key, saveText);

                        mrootRef.updateChildren(map, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null)
                                    Log.i("error sendingText", databaseError.getMessage());
                            }
                        });

                        chatText.setText("");
                        mrootRef.child("chat").child(userId).child(id).child("seen").setValue(true);
                        mrootRef.child("chat").child(userId).child(id).child("timeStamp").setValue(DateFormat.getDateTimeInstance().format(new Date()));
                        mrootRef.child("chat").child(id).child(userId).child("seen").setValue(false);
                        mrootRef.child("chat").child(id).child(userId).child("timeStamp").setValue(DateFormat.getDateTimeInstance().format(new Date()));

                    }

                    else{

                        Map map = new HashMap();
                        map.put("messages/" + userId + "/" + id + "/" + message_push_key, saveText);
                       // map.put("messages/" + id + "/" + userId + "/" + message_push_key, saveText);

                        mrootRef.child("branch").child(id).child("users").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot childSnapshot:dataSnapshot.getChildren()){
                                    if(!childSnapshot.getKey().equals(userId)) {
                                        mrootRef.child("messages").child(childSnapshot.getKey()).child(id).push().setValue(saveText);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        mrootRef.updateChildren(map, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null)
                                    Log.i("error sendingText", databaseError.getMessage());
                            }
                        });

                        chatText.setText("");
                        mrootRef.child("chat").child(userId).child(id).child("seen").setValue(true);
                        mrootRef.child("chat").child(userId).child(id).child("timeStamp").setValue(DateFormat.getDateTimeInstance().format(new Date()));
                       // mrootRef.child("chat").child(id).child(userId).child("seen").setValue(false);
                       // mrootRef.child("chat").child(id).child(userId).child("timeStamp").setValue(DateFormat.getDateTimeInstance().format(new Date()));


                    }
                }
            }
        });


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent=new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),GALLARY_PIC);


            }
        });




        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;

                itemPos = 0;mlist.clear();

                loadMoreMessages();
            }
        });



    }

    private void loadMoreMessages() {

        DatabaseReference dataQueryRef=mrootRef.child("messages").child(userId).child(id);

        Query messageQuery=dataQueryRef.orderByKey().endAt(mLastKey).limitToLast(mCurrentPage*total_message_show_on_sreen);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message m=dataSnapshot.getValue(Message.class);

                String messageKey = dataSnapshot.getKey();

                if(!mPrevKey.equals(messageKey)){

                    mlist.add(itemPos++, m);

                } else {

                    mPrevKey = mLastKey;

                }


                if(itemPos == 1) {

                    mLastKey = messageKey;

                }


                Log.d("TOTALKEYS", "Last Key : " + mLastKey + " | Prev Key : " + mPrevKey + " | Message Key : " + messageKey);

                adapter.notifyDataSetChanged();

                refreshLayout.setRefreshing(false);

                linearLayout.scrollToPositionWithOffset(10, 0);
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

    private void loadMessages() {

        DatabaseReference dataQueryRef=mrootRef.child("messages").child(userId).child(id);

        //Query messageQuery=dataQueryRef.limitToLast(mCurrentPage*total_message_show_on_sreen);

        dataQueryRef.addChildEventListener(new ChildEventListener() {
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        Message message=dataSnapshot.getValue(Message.class);

       // itemPos++;
        refreshLayout.setEnabled(false);

       // if(itemPos == 1){

       //     String messageKey = dataSnapshot.getKey();

       //     mLastKey = messageKey;
       //     mPrevKey = messageKey;

      //  }

        mlist.add(message);
        adapter.notifyDataSetChanged();

        messageList.scrollToPosition(mlist.size() - 1);

        refreshLayout.setRefreshing(false);



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

    //sending images


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALLARY_PIC && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            StorageReference ref = mImageRef.child("imageMessage").child(userId).child(id).child(ServerValue.TIMESTAMP + "jpg");

            ref.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {

                        String imageUrl=task.getResult().getDownloadUrl().toString();
                        DatabaseReference keyRef=mrootRef.child("messages").child(userId).child(id).push();
                        String pushKey=keyRef.getKey();
                        Map saveMessage=new HashMap();
                        saveMessage.put("message",imageUrl );
                        saveMessage.put("seen", false);
                        saveMessage.put("timestamp", DateFormat.getDateTimeInstance().format(new Date()));
                        saveMessage.put("type","image");
                        saveMessage.put("from",userId);
                        Map imageMap=new HashMap();
                        imageMap.put("messages/"+userId+"/"+id+"/"+pushKey,saveMessage);
                        imageMap.put("messages/"+id+"/"+userId+"/"+pushKey,saveMessage);
                        mrootRef.updateChildren(imageMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null)
                                    Log.i("error sendingImage", databaseError.getMessage());
                            }
                        });
                        mrootRef.child("chat").child(userId).child(id).child("seen").setValue(true);
                        mrootRef.child("chat").child(userId).child(id).child("timeStamp").setValue(DateFormat.getDateTimeInstance().format(new Date()));
                        mrootRef.child("chat").child(id).child(userId).child("seen").setValue(false);
                        mrootRef.child("chat").child(id).child(userId).child("timeStamp").setValue(DateFormat.getDateTimeInstance().format(new Date()));




                    } else {
                        Toast.makeText(ChatActivity.this, "faild to upload in storage", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }

    }




    @Override
    protected void onStart() {
        super.onStart();
        mlist.clear();
        loadMessages();
        mrootRef.child("users").child(userId).child("online").setValue("true");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mrootRef.child("users").child(userId).child("online").setValue(ServerValue.TIMESTAMP);
    }
}
