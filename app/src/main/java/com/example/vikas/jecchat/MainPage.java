package com.example.vikas.jecchat;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class MainPage extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private ViewPager mViewPager;
    private SectionPagerAdapter mSectionPagerAdapter;
    private TabLayout mTabLayout;
    private DatabaseReference userDataRef;

    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();


        if(mAuth.getCurrentUser()!=null)
        userDataRef= FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        toolbar=(Toolbar)findViewById(R.id.main_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("JecChat");

        mViewPager =(ViewPager)findViewById(R.id.viewPager);

        mSectionPagerAdapter =new SectionPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionPagerAdapter);

        mTabLayout=(TabLayout)findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=mAuth.getCurrentUser();
        if(user==null)
        {
            login_page();
        }
        else
        userDataRef.child("online").setValue("true");

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        userDataRef.child("online").setValue(ServerValue.TIMESTAMP);
    }

    void login_page()
    {
        Intent intent=new Intent(MainPage.this,WelcomePage.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_bar,menu);



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch(item.getItemId()){


            case R.id.action_notifications:{

               Intent intent=new Intent(this,Notification_Activity.class);
               startActivity(intent);
               break;


            }

            case R.id.userPost:{
                Intent intent =new Intent(MainPage.this,Post_Activity.class);
                startActivity(intent);
                break;


            }
            case R.id.logout:{
                userDataRef.child("online").setValue(ServerValue.TIMESTAMP);
                FirebaseAuth.getInstance().signOut();
                login_page();break;}

            case R.id.setting:
                Intent intent=new Intent(MainPage.this,Settings_Activity.class);
                startActivity(intent);break;
            case R.id.all_users:
                Intent intent2=new Intent(MainPage.this,AllUsers_Activity.class);
                startActivity(intent2);
            default:return true;
        }

       /* if(item.getItemId()==R.id.logout)
        {
            FirebaseAuth.getInstance().signOut();
            login_page();

        }
        if(item.getItemId()==R.id.setting);
        {
            Intent intent=new Intent(MainPage.this,Settings_Activity.class);
            startActivity(intent);
        }*/
        return true;
    }
}
