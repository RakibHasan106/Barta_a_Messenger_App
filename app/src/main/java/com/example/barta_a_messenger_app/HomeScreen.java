package com.example.barta_a_messenger_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeScreen extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private FloatingActionButton fab;
    BottomNavigationView bottomNavigationView;

    RecyclerView recyclerView;

    FirebaseAuth mAuth ;
    FirebaseUser user;

    DatabaseReference database;
    String uid;

    ArrayList<Contact> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactList = new ArrayList<>();


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user!=null){
            uid = user.getUid();
        }

        database = FirebaseDatabase.getInstance().getReference("Contacts").child(uid);
        ContactAdapter adapter = new ContactAdapter(this,contactList);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactList.clear();
                for(DataSnapshot datasnapshot:snapshot.getChildren()){
                    Contact contact = datasnapshot.getValue(Contact.class);
                    contactList.add(contact);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, AddFriendActivity.class);
                startActivity(intent);
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_chat) {
            return true;
        }
        else if (id == R.id.menu_profile) {
            startActivity(new Intent(HomeScreen.this,ProfileActivity.class));
            return true;
        }
        else if (id == R.id.menu_settings) {
            startActivity(new Intent(HomeScreen.this,SettingsActivity.class));
            return true;
        }
        else {
            return false;
        }
    }
}