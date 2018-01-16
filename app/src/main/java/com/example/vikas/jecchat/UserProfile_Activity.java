package com.example.vikas.jecchat;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile_Activity extends AppCompatActivity {

    private CircleImageView profileImage;
    private TextView profileName;
    private TextView profileStatus;
    private TextView profileEnroll;
    private TextView profileBranch;
    private TextView profileSem;
    private TextView profileEmail;
    private Button frndReq;
    private Button declReq;

    private DatabaseReference profileRef;
    private DatabaseReference requestRef;
    private DatabaseReference friendsRef;
    private DatabaseReference notificationRef;
    private DatabaseReference mrootRef;

    private String userId;
    private int requestStatus=0;//send=0,cancel=1,accept=2,unfriend=3
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_);

        profileImage=(CircleImageView) findViewById(R.id.circleImageView);
        profileName=(TextView)findViewById(R.id.displayName);
        profileStatus=(TextView)findViewById(R.id.s_tatus);
        profileEnroll=(TextView)findViewById(R.id.profileEnroll);
        profileBranch=(TextView)findViewById(R.id.profileBranch);
        profileSem=(TextView)findViewById(R.id.userSemester);
        profileEmail=(TextView)findViewById(R.id.profileEmailid);
        frndReq=(Button)findViewById(R.id.send_request);
        declReq=(Button)findViewById(R.id.decline_request);
        declReq.setVisibility(View.INVISIBLE);
        declReq.setEnabled(false);


        progressDialog=new ProgressDialog(UserProfile_Activity.this);
        progressDialog.setTitle("Uploading..");
        progressDialog.setMessage("please wait while it uploads");
        progressDialog.show();
        final String id=getIntent().getStringExtra("Id");
        profileRef= FirebaseDatabase.getInstance().getReference().child("users").child(id);
        requestRef=FirebaseDatabase.getInstance().getReference().child("friendRequest");
        friendsRef=FirebaseDatabase.getInstance().getReference().child("friends");
        notificationRef=FirebaseDatabase.getInstance().getReference().child("notifications");
        mrootRef=FirebaseDatabase.getInstance().getReference();


        userId=FirebaseAuth.getInstance().getCurrentUser().getUid();

        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("name").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                final String image=dataSnapshot.child("image").getValue().toString();
                String enroll=dataSnapshot.child("enrollment").getValue().toString();
                String branch=dataSnapshot.child("branch").getValue().toString();
                String sem=dataSnapshot.child("sem").getValue().toString();
                String emailId=dataSnapshot.child("email").getValue().toString();

                profileEnroll.setText(enroll);
                profileBranch.setText(branch);
                profileEmail.setText(emailId);
                profileSem.setText(sem);
                profileName.setText(name);
                profileStatus.setText(status);
                Picasso.with(UserProfile_Activity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.images).into(profileImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(UserProfile_Activity.this).load(image).placeholder(R.drawable.images).into(profileImage);
                    }
                });

                //accepting friend request
                requestRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(id)) {
                            String requestType = dataSnapshot.child(id).child("request_type").getValue().toString();
                            if (requestType.equals("received")) {
                                frndReq.setText("Accept friend request");
                                requestStatus = 2;

                                Log.i("butoon", frndReq.getText().toString());
                                declReq.setVisibility(View.VISIBLE);
                                declReq.setEnabled(true);
                                progressDialog.dismiss();

                            } else if (requestType.equals("sent")) {
                                frndReq.setText("cancel the request");
                                requestStatus = 1;
                                Log.i("butoon", frndReq.getText().toString());
                                declReq.setVisibility(View.INVISIBLE);
                                declReq.setEnabled(false);
                                progressDialog.dismiss();
                            }

                        }


                            else {
                                friendsRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(id)) {
                                            frndReq.setText("unfriend this person");
                                            requestStatus = 3;
                                            Log.i("butoon", frndReq.getText().toString());
                                            declReq.setVisibility(View.INVISIBLE);
                                            declReq.setEnabled(false);
                                            progressDialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        progressDialog.dismiss();
                                    }
                                });

                            }
                        progressDialog.dismiss();


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        progressDialog.dismiss();
                    }
                });
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        progressDialog.dismiss();


        declReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestRef.child(userId).child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        requestRef.child(id).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                frndReq.setEnabled(true);
                                requestStatus=0;
                                frndReq.setText("send friend request");
                                declReq.setVisibility(View.INVISIBLE);
                                declReq.setEnabled(false);
                            }
                        });
                    }
                });
            }
        });


        frndReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                frndReq.setEnabled(false);

                if(requestStatus==0) {

                    DatabaseReference notiref=mrootRef.child("notifications").child(id).push();
                    String notirefid=notiref.getKey();

                    HashMap<String,String> notiHash=new HashMap<>();
                    notiHash.put("from",userId);
                    notiHash.put("type","request");

                    Map requestMap=new HashMap();
                    requestMap.put("friendRequest/"+userId+"/"+id+"/"+"request_type","sent");
                    requestMap.put("friendRequest/"+id+"/"+userId+"/"+"request_type","received");
                    requestMap.put("notifications/"+id+"/"+notirefid,notiHash);

                    mrootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError==null)
                            {
                                frndReq.setEnabled(true);
                                frndReq.setText("cancel the request");
                                requestStatus = 1;
                                declReq.setVisibility(View.INVISIBLE);
                                declReq.setEnabled(false);
                            }
                            else{
                                Toast.makeText(UserProfile_Activity.this,"request not sent",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }

                if(requestStatus==1){

                    requestRef.child(userId).child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            requestRef.child(id).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    frndReq.setEnabled(true);
                                    requestStatus=0;
                                    frndReq.setText("send friend request");
                                    declReq.setVisibility(View.INVISIBLE);
                                    declReq.setEnabled(false);
                                }
                            });
                        }
                    });

                }

                if(requestStatus==2){

                    final String currentDate= DateFormat.getDateTimeInstance().format(new Date());



                    Map friendsMap=new HashMap();
                    friendsMap.put("friends/"+userId+"/"+id+"/date",currentDate);
                    friendsMap.put("friends/"+id+"/"+userId+"/date",currentDate);

                    friendsMap.put("friendRequest/"+userId+"/"+id,null);
                    friendsMap.put("friendRequest/"+id+"/"+userId,null);
                    mrootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError==null)
                            {
                                frndReq.setEnabled(true);
                                requestStatus=3;
                                frndReq.setText("Unfriend this person");
                                frndReq.setEnabled(true);

                                declReq.setVisibility(View.INVISIBLE);
                                declReq.setEnabled(false);
                            }

                            else
                                Toast.makeText(UserProfile_Activity.this,"task is not successfull",Toast.LENGTH_SHORT).show();

                        }
                    });


                }

                if(requestStatus==3) {


                    Map unfriendMap = new HashMap();
                    unfriendMap.put("friends/" + userId + "/" + id, null);
                    unfriendMap.put("friends" + id + "/" + userId, null);

                    mrootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                frndReq.setEnabled(true);
                                requestStatus = 0;
                                frndReq.setText("send friend request");
                                declReq.setVisibility(View.INVISIBLE);
                                declReq.setEnabled(false);
                            } else
                                Toast.makeText(UserProfile_Activity.this, "task is not successful", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });




    }

    @Override
    protected void onStart() {
        super.onStart();
        mrootRef.child("users").child(userId).child("online").setValue("true");
    }


    @Override
    protected void onStop() {
        super.onStop();
        mrootRef.child("users").child(userId).child("online").setValue(ServerValue.TIMESTAMP);
    }
}
