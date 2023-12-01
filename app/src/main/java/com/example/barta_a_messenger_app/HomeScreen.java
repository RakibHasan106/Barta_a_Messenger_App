package com.example.barta_a_messenger_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;

import android.os.Bundle;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeScreen extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private FloatingActionButton fab;
    BottomNavigationView bottomNavigationView;

    RecyclerView recyclerView;

    TextView header_user;
    ImageView header_img;

    FirebaseAuth mAuth ;
    FirebaseUser user;

    DatabaseReference databaseReference;
    String uid;

    ValueEventListener notificationListener;
    FirebaseDatabase database;

    ArrayList<Contact> contactList;
    String sendername;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);


        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);

        tabLayout.setupWithViewPager(viewPager);

        viewPagerAdapter vpAdapter = new viewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment(new chatFragment(),"CHATS");
        vpAdapter.addFragment(new profileFragment(),"PROFILE");
        vpAdapter.addFragment(new friendRequestFragment(),"REQUEST");
        vpAdapter.addFragment(new settingsFragment(),"SETTINGS");

        viewPager.setAdapter(vpAdapter);


        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab_button);

        header_user = findViewById(R.id.head_textView);
        header_img = findViewById(R.id.headImageView);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user!=null){
            uid = user.getUid();
        }

        database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("user").child(uid);



        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("username").getValue(String.class);
                    String profilePictureUrl = dataSnapshot.child("profilePicture").getValue(String.class);

                    header_user.setText(name);
                    if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                        Picasso.get().load(profilePictureUrl).into(header_img);
                    }
                    else {

                    }

                } else {
                    // Handle the case where the user data doesn't exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }

        });

        notificationListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                    for(DataSnapshot dataSnapshot2 : datasnapshot.getChildren()){
                        MessageModel message = dataSnapshot2.getValue(MessageModel.class);

                        if(message.getIsNotified().equals("no")){

                            database.getReference().child("user")
                                            .child(message.getUid()).get()
                                    .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            if(task.isSuccessful()){
                                                DataSnapshot ds = task.getResult();
                                                if(ds.exists()){
                                                    sendername = ds.child("username").getValue(String.class);
                                                    NotificationHelper.notificationDialog(HomeScreen.this,message.getMessage(),sendername);
                                                    //Log.d("senderName",sendername);

                                                }
                                            }
                                        }
                                    });
                            //Toast.makeText(HomeScreen.this,sendername, Toast.LENGTH_SHORT).show();


                            database.getReference().child("chats")
                                    .child(uid).child(datasnapshot.getKey())
                                    .child(dataSnapshot2.getKey())
                                    .child("isNotified").setValue("yes");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        database.getReference().child("chats")
                .child(uid)
                .addValueEventListener(notificationListener);


    }

    @Override
    protected void onStop() {
        super.onStop();
        database.getReference().child("chats").child(uid).removeEventListener(notificationListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        database.getReference().child("chats").child(uid).removeEventListener(notificationListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.getReference().child("chats").child(uid).removeEventListener(notificationListener);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        database.getReference().child("chats").child(uid).addValueEventListener(notificationListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        database.getReference().child("chats").child(uid).addValueEventListener(notificationListener);
    }
}