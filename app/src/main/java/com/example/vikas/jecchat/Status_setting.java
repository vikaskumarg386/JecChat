package com.example.vikas.jecchat;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class Status_setting extends AppCompatActivity {

    private Toolbar toolbar;
    private Button mSave;
    private TextInputLayout mStatus;
    private String id;


    private DatabaseReference mRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_setting);

        mRoot= FirebaseDatabase.getInstance().getReference().child("users");
         id= FirebaseAuth.getInstance().getCurrentUser().getUid();

        mStatus=(TextInputLayout)findViewById(R.id.statusss) ;

        toolbar=(Toolbar)findViewById(R.id.toolBarrr);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Setting Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String st=getIntent().getStringExtra("status_val");
        mStatus.getEditText().setText(st);

        mSave=(Button)findViewById(R.id.saveee);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String status=mStatus.getEditText().getText().toString();



                mRoot.child(id).child("status").setValue(status);


            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        mRoot.child(id).child("online").setValue("true");

    }

    @Override
    protected void onStop() {
        super.onStop();
        mRoot.child(id).child("online").setValue(ServerValue.TIMESTAMP);

    }
}
