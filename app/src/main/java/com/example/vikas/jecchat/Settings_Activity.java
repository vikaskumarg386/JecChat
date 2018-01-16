package com.example.vikas.jecchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class Settings_Activity extends AppCompatActivity {


    private CircleImageView mImage;
    private ImageView mImageCover;
    private EditText mEnrollment;
    private EditText mBranch;
    private EditText mSem;
    private EditText mEmail;
    private EditText mName;
    private EditText mStatus;
    private Button mSave;
    private int GALLARY_PIC=1;
    private StorageReference mImageRef;

    private FirebaseUser firebaseUser;
    private DatabaseReference user;
    private ProgressDialog progressDialog;
    private DatabaseReference mRootFef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_);

        mImageRef= FirebaseStorage.getInstance().getReference();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        mName=(EditText) findViewById(R.id.displayName);
        mStatus=(EditText) findViewById(R.id.s_tatus);
        mEnrollment=(EditText)findViewById(R.id.userEnroll);
        mBranch=(EditText)findViewById(R.id.userBranch);
        mSem=(EditText)findViewById(R.id.userSemester);
        mEmail=(EditText)findViewById(R.id.userEmailid);
        mImage=(CircleImageView)findViewById(R.id.circleImageView);
        mImageCover=(ImageView)findViewById(R.id.display_cover_image);
        final String userId;

        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();

        user=FirebaseDatabase.getInstance().getReference().child("users").child(userId);
               user.keepSynced(true);
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nam=dataSnapshot.child("name").getValue().toString();
                String sts=dataSnapshot.child("status").getValue().toString();
                final String img=dataSnapshot.child("image").getValue().toString();
                String enrl=dataSnapshot.child("enrollment").getValue().toString();
                String branch=dataSnapshot.child("branch").getValue().toString();
                String sem=dataSnapshot.child("sem").getValue().toString();
                String emailId=dataSnapshot.child("email").getValue().toString();
                final String imageCover=dataSnapshot.child("imageCover").getValue().toString();


                mEnrollment.setText(enrl);
                mBranch.setText(branch);
                mSem.setText(sem);
                mEmail.setText(emailId);
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

                if(!imageCover.equals("default"))
                    Picasso.with(Settings_Activity.this).load(imageCover).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(mImageCover, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(Settings_Activity.this).load(imageCover).placeholder(R.drawable.user).into(mImageCover);

                        }
                    });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mRootFef=FirebaseDatabase.getInstance().getReference();
        mSave=(Button)findViewById(R.id.saveSetting);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                user.child("name").setValue(mName.getText().toString());
                user.child("status").setValue(mStatus.getText().toString());
                user.child("enrollment").setValue(mEnrollment.getText().toString());
                user.child("branch").setValue(mBranch.getText().toString());
                user.child("sem").setValue(mSem.getText().toString());
                user.child("email").setValue(mEmail.getText().toString());

                Toast.makeText(Settings_Activity.this,"changes saved",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(Settings_Activity.this,MainPage.class);
                startActivity(intent);

            }
        });



        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent galleryIntent=new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),1);
               /* CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(Settings_Activity.this);*/


            }
        });

        mImageCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),2);
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

      // switch (requestCode) {

          // case 1: {



               if (requestCode==1&&resultCode == RESULT_OK) {
                   Uri imageUri = data.getData();
                   CropImage.activity(imageUri).setAspectRatio(1, 1)
                           .start(this);
               }

               if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                   CropImage.ActivityResult result = CropImage.getActivityResult(data);
                   if (resultCode == RESULT_OK) {

                       progressDialog = new ProgressDialog(Settings_Activity.this);
                       progressDialog.setTitle("Uploading Image...");
                       progressDialog.setMessage("Please wait until image upload");
                       progressDialog.setCanceledOnTouchOutside(false);
                       progressDialog.show();

                       Uri resultUri = result.getUri();


                       File filePath = new File(resultUri.getPath());
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
                           StorageReference thumbStorage = mImageRef.child("image_profiles").child("thumb").child(firebaseUser.getUid() + "jpg");

                           UploadTask uploadTask = thumbStorage.putBytes(thumbData);
                           uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                               @Override
                               public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                   String thumbUrl = task.getResult().getDownloadUrl().toString();
                                   user.child("thumbImage").setValue(thumbUrl);
                               }
                           });
                       } catch (IOException e) {
                           e.printStackTrace();
                       }

                       StorageReference ref = mImageRef.child("image_profiles").child(firebaseUser.getUid() + "jpg");


                       ref.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                           @Override
                           public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                               if (task.isSuccessful()) {
                                   String imageUrl = task.getResult().getDownloadUrl().toString();

                                   user.child("image").setValue(imageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           if (task.isSuccessful()) {
                                               progressDialog.dismiss();
                                           } else {
                                               Toast.makeText(Settings_Activity.this, "faild to upload in database", Toast.LENGTH_SHORT).show();
                                               progressDialog.dismiss();
                                           }
                                       }
                                   });
                               } else {
                                   Toast.makeText(Settings_Activity.this, "faild to upload in storage", Toast.LENGTH_SHORT).show();
                                   progressDialog.dismiss();
                               }
                           }
                       });
                   } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                       Exception error = result.getError();
                   }
               }
               //break;
          // }
          // case 2:{


               if (requestCode==2&&resultCode == RESULT_OK) {
                   Uri imageUri = data.getData();
                  // CropImage.activity(imageUri).setAspectRatio(1, 1)
                         //  .start(this);
              // }

             //  if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                  // CropImage.ActivityResult result = CropImage.getActivityResult(data);
                  // if (resultCode == RESULT_OK) {

                       progressDialog = new ProgressDialog(Settings_Activity.this);
                       progressDialog.setTitle("Uploading Image...");
                       progressDialog.setMessage("Please wait until image upload");
                       progressDialog.setCanceledOnTouchOutside(false);
                       progressDialog.show();

                      // Uri resultUri = result.getUri();


                       File filePath = new File(imageUri.getPath());
                       Bitmap thumbBitmap;


                      /* try {
                           thumbBitmap = new Compressor(this)
                                   .setMaxHeight(200)
                                   .setMaxWidth(200)
                                   .setQuality(60)
                                   .compressToBitmap(filePath);

                           ByteArrayOutputStream baos = new ByteArrayOutputStream();
                           thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                           byte[] thumbData = baos.toByteArray();
                           StorageReference thumbStorage = mImageRef.child("image_profiles_cover").child("thumb_cover").child(firebaseUser.getUid() + "jpg");

                           UploadTask uploadTask = thumbStorage.putBytes(thumbData);
                           uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                               @Override
                               public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                   if(task.isSuccessful()){
                                   String thumbUrl = task.getResult().getDownloadUrl().toString();
                                   user.child("thumbImageCover").setValue(thumbUrl);}
                               }
                           });
                       } catch (IOException e) {
                           e.printStackTrace();
                       }*/

                       StorageReference ref = mImageRef.child("image_profiles_cover").child(firebaseUser.getUid() + "jpg");


                       ref.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                           @Override
                           public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                               if (task.isSuccessful()) {
                                   String imageUrl = task.getResult().getDownloadUrl().toString();

                                   user.child("imageCover").setValue(imageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           if (task.isSuccessful()) {
                                               progressDialog.dismiss();
                                           } else {
                                               Toast.makeText(Settings_Activity.this, "faild to upload in database", Toast.LENGTH_SHORT).show();
                                               progressDialog.dismiss();
                                           }
                                       }
                                   });
                               } else {
                                   Toast.makeText(Settings_Activity.this, "faild to upload in storage", Toast.LENGTH_SHORT).show();
                                   progressDialog.dismiss();
                               }
                           }
                       });
                   //} //else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                      // Exception error = result.getError();
                  // }
               }

              //break;



          // }
          // default:
      // }
    }


}
