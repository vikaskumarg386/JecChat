package com.example.vikas.jecchat;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class Post_Activity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText timelineUserMessage;

    private DatabaseReference mrootRef;
    private ImageButton addButton;
    private ImageView imageView;
    private StorageReference mImageRef;
    private  int GALLARY_PIC=1;
    private String pushKey;
    private String imageUrl="null";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_);

        toolbar=(Toolbar)findViewById(R.id.postToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("post message");
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        timelineUserMessage=(EditText) findViewById(R.id.timelineUserMessage);

        addButton=(ImageButton)findViewById(R.id.addButton);
        imageView=(ImageView)findViewById(R.id.postImage);
        mrootRef= FirebaseDatabase.getInstance().getReference();
        mImageRef= FirebaseStorage.getInstance().getReference();

        DatabaseReference pushRef=mrootRef.child("timeLinePost").child("timelinePostUser").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push();
        pushKey=pushRef.getKey();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent=new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),GALLARY_PIC);


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
           final String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (requestCode == GALLARY_PIC && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            StorageReference ref = mImageRef.child("imagePost").child(pushKey).child(ServerValue.TIMESTAMP + "jpg");

            ref.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {

                        imageUrl=task.getResult().getDownloadUrl().toString();

                        Picasso.with(Post_Activity.this).load(imageUrl).into(imageView);

                    } else {
                        Toast.makeText(Post_Activity.this, "faild to upload in storage", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.post_menu_bar,menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {


            case R.id.post: {
                String userMessage = timelineUserMessage.getText().toString();
                if (userMessage == null||userMessage.equals("")) {
                    Toast.makeText(Post_Activity.this, "message is empty", Toast.LENGTH_SHORT).show();
                }
               else if(imageUrl.equals("null"))
                {

                    DatabaseReference pushRef = mrootRef.child("timeLinePost").child("timelinePostUser").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push();
                    pushKey = pushRef.getKey();
                    final Map<String, String> map = new HashMap<>();
                    map.put("message", userMessage);
                    map.put("image","null");
                    map.put("time", DateFormat.getDateTimeInstance().format(new Date()));
                    map.put("from", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    map.put("type", "text");
                    map.put("likes", "0");
                    map.put("comments", "0");
                    map.put("push_key", pushKey);
                    pushRef.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mrootRef.child("timeLinePost").child("timelinePostAll").child(pushKey).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Toast.makeText(Post_Activity.this, "Posted", Toast.LENGTH_LONG).show();
                                        timelineUserMessage.setText("");

                                        Intent intent = new Intent(Post_Activity.this, MainPage.class);
                                        startActivity(intent);

                                    }
                                }
                            });
                        }
                    });
                }

                else {


                    DatabaseReference pushRef = mrootRef.child("timeLinePost").child("timelinePostUser").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push();
                    pushKey = pushRef.getKey();
                    final Map<String, String> map = new HashMap<>();
                    map.put("message", userMessage);
                    map.put("image",imageUrl);
                    map.put("time", DateFormat.getDateTimeInstance().format(new Date()));
                    map.put("from", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    map.put("type", "text");
                    map.put("likes", "0");
                    map.put("comments", "0");
                    map.put("push_key", pushKey);
                    pushRef.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mrootRef.child("timeLinePost").child("timelinePostAll").child(pushKey).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Toast.makeText(Post_Activity.this, "Posted", Toast.LENGTH_LONG).show();
                                        timelineUserMessage.setText("");

                                        Intent intent = new Intent(Post_Activity.this, MainPage.class);
                                        startActivity(intent);

                                    }
                                }
                            });
                        }
                    });

                }
            }
        }
        return true;
    }
}
