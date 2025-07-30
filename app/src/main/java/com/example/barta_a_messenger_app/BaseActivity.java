package com.example.barta_a_messenger_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BaseActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    DatabaseReference contactsRef;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        contactsRef = FirebaseDatabase.getInstance().getReference("Contacts").child(uid);
    }

    protected void onStart(){
        super.onStart();
        updateUserStatus("active");
    }

    protected void onPause() {
        super.onPause();
        // Handle logic for onPause
        updateUserStatus("inactive");
    }

    protected void onStop() {
        super.onStop();
        // Handle logic for onStop
        updateUserStatus("inactive");
    }

    private void updateUserStatus(String status) {
        DatabaseReference userStatusRef = FirebaseDatabase.getInstance().getReference("user").child(uid).child("status");
        userStatusRef.setValue(status);

        contactsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot contactSnapshot : snapshot.getChildren()) {
                    Contact contact = contactSnapshot.getValue(Contact.class);
                    if (contact != null && contact.getUid()!=null && contact.getUid().equals(uid)) {
                        contactsRef.child(contactSnapshot.getKey()).child("status").setValue(status);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}