package com.example.vikas.jecchat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Comment_Activity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private EditText commentText;
    private ImageButton sendComment;
    private DatabaseReference mRootRef;
    private CommentAdapter adapter;
    private List<Comments> list=new ArrayList<>();
    private String push_key;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_);
        toolbar=(Toolbar)findViewById(R.id.commentToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("comments");
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        commentText=(EditText)findViewById(R.id.commentText);
        sendComment=(ImageButton)findViewById(R.id.sendComment);

        push_key=getIntent().getStringExtra("push_key");

        mRootRef= FirebaseDatabase.getInstance().getReference();

        adapter=new CommentAdapter(list);
        recyclerView=(RecyclerView)findViewById(R.id.commentRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        loadComments();

        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(commentText.getText()!=null)
                {   DatabaseReference cPush_ref=mRootRef.child("timeLinePost").child("timelinePostAll").child(push_key).child("userComments").push();
                    String cPush_key=cPush_ref.getKey();
                    String cText=commentText.getText().toString();
                    Map<String,String> map=new HashMap<>();
                    map.put("comment_Text",cText);
                    map.put("time", DateFormat.getDateTimeInstance().format(new Date()));
                    map.put("from", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    map.put("likes","0");
                    map.put("type","text");
                    map.put("cPush_key",cPush_key);
                    map.put("push_key",push_key);
                    mRootRef.child("timeLinePost").child("timelinePostAll").child(push_key).child("userComments").child(cPush_key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {


                                mRootRef.child("timeLinePost").child("timelinePostAll").child(push_key).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String no=dataSnapshot.child("comments").getValue().toString();
                                        int k=Integer.parseInt(no);
                                        mRootRef.child("timeLinePost").child("timelinePostAll").child(push_key).child("comments").setValue(String.valueOf(k+1)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(getApplicationContext(), "Comment Posted", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }

                        }
                    });




                }


            }
        });


    }

    private void loadComments() {

      DatabaseReference  ref= mRootRef.child("timeLinePost").child("timelinePostAll").child(push_key).child("userComments");
      ref.addChildEventListener(new ChildEventListener() {
           @Override
           public void onChildAdded(DataSnapshot dataSnapshot, String s) {

               Comments c=dataSnapshot.getValue(Comments.class);
               list.add(c);
               adapter.notifyDataSetChanged();


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
