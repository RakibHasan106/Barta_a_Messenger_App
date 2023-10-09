package com.example.barta_a_messenger_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;
import com.hbb20.CountryCodePicker;

public class AddFriendActivity extends AppCompatActivity {

    String fname,phone,uid;
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

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

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
                    fname = name.getText().toString().trim();
                    phone = countryCodePicker.getFullNumberWithPlus();

                    databaseReference.child("All Accounts").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                saveContact();
                            }
                            else {
                                Toast.makeText(AddFriendActivity.this, "Phone number doesn't exists", Toast.LENGTH_SHORT).show();


                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }

    private void saveContact() {
        Contact contact = new Contact(fname,phone);
        databaseReference.child("Contacts").child(uid).child(phone).setValue(contact);
        name.setText("");
        phone_number.setText("");

    }


}