package com.example.barta_a_messenger_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

public class AddContactActivity extends AppCompatActivity {

    String fname,phone,uid;
    EditText name, phone_number;
    Button save;
    AppCompatImageView backButton;
    private FirebaseAuth mAuth;
    CountryCodePicker countryCodePicker;

    DatabaseReference databaseReference,reference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

//        name = findViewById(R.id.contact_name);
        phone_number = findViewById(R.id.contact_number);
        save = findViewById(R.id.save_button);
        countryCodePicker = findViewById(R.id.countrypicker);
        backButton = findViewById(R.id.imageBack);
        countryCodePicker.registerCarrierNumberEditText(phone_number);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        reference = FirebaseDatabase.getInstance().getReference();

        if(currentUser != null){
            uid = currentUser.getUid();
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact();
            }

            private void addContact() {
                if(!countryCodePicker.isValidFullNumber()){
                    phone_number.setError("Invalid phone number");
                }

                else {
//                    fname = name.getText().toString().trim();
                    phone = countryCodePicker.getFullNumberWithPlus();

                    reference.child("user").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String phone_numb = snapshot.child("phone").getValue().toString();
                            if (!phone_numb.equals(phone)){
                                databaseReference.child("All Accounts").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){

                                            saveContact(snapshot.child("uid").getValue().toString());
                                        }
                                        else {
                                            Toast.makeText(AddContactActivity.this, "Phone number doesn't exists", Toast.LENGTH_SHORT).show();


                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            else {
                                phone_number.setError("Invalid phone number");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });




                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddContactActivity.this,HomeScreen.class);

                startActivity(intent);
                finish();
            }
        });
    }



    private void saveContact(String contact_uid) {
        // Step 1: Retrieve the sender's profile picture from the 'user' node
        reference.child("user").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String senderProfilePic = "";
                if (snapshot.child("profilePicture").exists()) {
                    senderProfilePic = snapshot.child("profilePicture").getValue(String.class);
                }

                // Step 2: Create the Request object with profile picture
                Request request = new Request("", phone, uid, contact_uid, "pending", senderProfilePic);

                // Step 3: Save to Firebase
                databaseReference.child("FriendRequestPending")
                        .child(contact_uid)
                        .child(uid)
                        .setValue(request);

                phone_number.setText("");

                Toast.makeText(AddContactActivity.this, "Friend request sent", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddContactActivity.this, "Failed to fetch sender's profile picture", Toast.LENGTH_SHORT).show();
            }
        });
    }



}