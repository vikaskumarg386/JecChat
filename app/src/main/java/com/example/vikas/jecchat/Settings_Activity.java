package com.example.vikas.jecchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class Settings_Activity extends AppCompatActivity {


    private CircleImageView mImage;
    private Button changeStatus;
    private Button changeImage;
    private TextView mName;
    private TextView mStatus;
    private int GALLARY_PIC=1;
    private StorageReference mImageRef;

    private FirebaseUser firebaseUser;
    private DatabaseReference user;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_);

        mImageRef= FirebaseStorage.getInstance().getReference();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        mName=(TextView)findViewById(R.id.displayName);
        mStatus=(TextView)findViewById(R.id.s_tatus);
        mImage=(CircleImageView)findViewById(R.id.circleImageView);
        String userId;

        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();

        user=FirebaseDatabase.getInstance().getReference().child("users").child(userId);
               user.keepSynced(true);
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nam=dataSnapshot.child("name").getValue().toString();
                String sts=dataSnapshot.child("status").getValue().toString();
                final String img=dataSnapshot.child("image").getValue().toString();

                mName.setText(nam);
                mStatus.setText(sts);

                if(!img.equals("default"))
                Picasso.with(Settings_Activity.this).load(img).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(mImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(Settings_Activity.this).load(img).placeholder(R.drawable.user).into(mImage);

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        changeStatus =(Button)findViewById(R.id.change_status);
        changeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String st=mStatus.getText().toString();
                Intent intent=new Intent(Settings_Activity.this,Status_setting.class);

                intent.putExtra("status_val",st);
                startActivity(intent);
            }
        });


         changeImage=(Button)findViewById(R.id.change_image);
        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent galleryIntent=new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),GALLARY_PIC);
               /* CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(Settings_Activity.this);*/


            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

        user.child("online").setValue("true");
    }

    @Override
    protected void onStop() {
        super.onStop();
        user.child("online").setValue(ServerValue.TIMESTAMP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==GALLARY_PIC&&resultCode==RESULT_OK) {
            Uri imageUri=data.getData();
            CropImage.activity(imageUri).setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                progressDialog=new ProgressDialog(Settings_Activity.this);
                progressDialog.setTitle("Uploading Image...");
                progressDialog.setMessage("Please wait until image upload");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                Uri resultUri = result.getUri();


                File filePath=new File(resultUri.getPath());
                Bitmap thumbBitmap;


                try {
                    thumbBitmap = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(60)
                            .compressToBitmap(filePath);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] thumbData = baos.toByteArray();
                    StorageReference thumbStorage=mImageRef.child("image_profiles").child("thumb").child(firebaseUser.getUid()+"jpg");

                    UploadTask uploadTask = thumbStorage.putBytes(thumbData);
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            String thumbUrl=task.getResult().getDownloadUrl().toString();
                            user.child("thumbImage").setValue(thumbUrl);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                StorageReference ref=mImageRef.child("image_profiles").child(firebaseUser.getUid()+"jpg");


                ref.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            String imageUrl=task.getResult().getDownloadUrl().toString();

                            user.child("image").setValue(imageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressDialog.dismiss();
                                    }
                                    else
                                    {
                                        Toast.makeText(Settings_Activity.this,"faild to upload in database",Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(Settings_Activity.this,"faild to upload in storage",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

}
