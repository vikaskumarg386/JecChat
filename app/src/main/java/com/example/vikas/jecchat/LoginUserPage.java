package com.example.vikas.jecchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginUserPage extends AppCompatActivity {

   private FirebaseAuth mAuth;
   private TextInputLayout userEmail;
   private TextInputLayout pass;
   private Button login;
    private Toolbar toolbar;
private DatabaseReference dbref;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();

        toolbar=(Toolbar) findViewById(R.id.loginToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");



        dbref= FirebaseDatabase.getInstance().getReference().child("users");



        userEmail=(TextInputLayout)findViewById(R.id.enrollment);
        pass=(TextInputLayout)findViewById(R.id.password);
        login=(Button)findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String en=userEmail.getEditText().getText().toString();
                String pa=pass.getEditText().getText().toString();

                if(en!=null||pa!=null){

                    mAuth.signInWithEmailAndPassword(en,pa).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                               String deviceToken= FirebaseInstanceId.getInstance().getToken();
                               dbref.child(uid).child("tokenId").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {

                                       Intent intent=new Intent(LoginUserPage.this,MainPage.class);
                                       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                       startActivity(intent);
                                   }
                               });


                            }

                            else
                                Toast.makeText(LoginUserPage.this,"Enrollment and password are wrong",Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            }
        });
    }
}
