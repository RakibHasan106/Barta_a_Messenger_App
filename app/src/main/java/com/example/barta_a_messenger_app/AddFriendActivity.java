package com.example.barta_a_messenger_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;
import com.hbb20.CountryCodePicker;

public class AddFriendActivity extends AppCompatActivity {

    EditText name, phone_number;
    Button save;

    private FirebaseAuth mAuth;

    CountryCodePicker countryCodePicker;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        name = findViewById(R.id.contact_name);
        phone_number = findViewById(R.id.contact_number);
        save = findViewById(R.id.save_button);
        countryCodePicker = findViewById(R.id.countrypicker);

        countryCodePicker.registerCarrierNumberEditText(phone_number);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact();
            }

            private void addContact() {
                String fname = name.getText().toString().trim();
                String phone = countryCodePicker.getFullNumberWithPlus();

                mAuth.getInstance().fetchSignInMethodsForEmail(phone)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                if (task.getResult().getSignInMethods().isEmpty()){
                                    Toast.makeText(AddFriendActivity.this, "User doesn't exists with this phone number", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    String user = mAuth.getCurrentUser().getUid();
                                    databaseReference.child(user).child("All Contacts").child(phone);

                                }
                            }
                            else {
                                Exception exception = task.getException();
                            }
                        });



//                databaseReference.child("All Accounts").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if(snapshot.hasChild(phone)){
//                            phone_number.setError("Contact already exists");
//                            phone_number.requestFocus();
//                        }
//                        else{
//                            databaseReference.child("All Accounts").child(phone).child("Name").setValue(fname);
//                            databaseReference.child("All Acconts").child(phone).child("Phone_number").setValue(phone);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
            }

        });
    }


}