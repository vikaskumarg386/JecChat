package com.example.vikas.jecchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class CreateUserPage extends AppCompatActivity {

    FirebaseAuth mAuth;
    Button button;

    TextInputLayout t1;
    TextInputLayout t2;
    TextInputLayout t3;
    TextInputLayout t4;

    FirebaseDatabase db=FirebaseDatabase.getInstance();
    DatabaseReference root=db.getReference();






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        mAuth=FirebaseAuth.getInstance();
        button=(Button)findViewById(R.id.button2);

        Toolbar toolbar=(Toolbar)findViewById(R.id.createToolbar);


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Account");


        t1=(TextInputLayout)findViewById(R.id.name);
        t2=(TextInputLayout)findViewById(R.id.enroll);
        t3=(TextInputLayout)findViewById(R.id.branch);
        t4=(TextInputLayout)findViewById(R.id.pass);




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               final String enroll=t2.getEditText().getText().toString()+"@gmail.com";
                String pass=t4.getEditText().getText().toString();
                final String display_name=t1.getEditText().getText().toString();
                final String user_branch=t3.getEditText().getText().toString();


                mAuth.createUserWithEmailAndPassword(enroll, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.i("TAG", "createUserWithEmail:onComplete:" + task.isSuccessful());


                                if (!task.isSuccessful()) {
                                    Toast.makeText(CreateUserPage.this, "Auth fail",
                                            Toast.LENGTH_SHORT).show();
                                }

                                else {
                                    String id = mAuth.getCurrentUser().getUid();
                                    String tokenId = FirebaseInstanceId.getInstance().getToken();

                                    HashMap<String, String> hm = new HashMap<>();

                                    hm.put("name", display_name);
                                    hm.put("enrollment", enroll);
                                    hm.put("branch", user_branch);
                                    hm.put("image", "default");
                                    hm.put("thumbImage", "default");
                                    hm.put("status", "Hii there i am using JecChat");
                                    hm.put("tokenId", tokenId);
                                    root.child("users").child(id).setValue(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Intent intent = new Intent(CreateUserPage.this, MainPage.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            }
                                        }
                                    });

                                }


                            }
                          });
                     }
                  }
        );
    }
}
