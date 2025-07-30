package com.example.barta_a_messenger_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.barta_a_messenger_app.helpers.KeyStoreHelper;
import com.example.barta_a_messenger_app.R;
import com.example.barta_a_messenger_app.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

public class SendOTPActivity extends AppCompatActivity {

    EditText phoneNumber;
    Button getOtpButton;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    CountryCodePicker countryCodePicker;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_otpactivity);

        phoneNumber = findViewById(R.id.inputMobile);
        getOtpButton = findViewById(R.id.getotpbutton);
        countryCodePicker = findViewById(R.id.countrypicker);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        String email = getIntent().getStringExtra("email");
        String name = getIntent().getStringExtra("name");
        String password = getIntent().getStringExtra("password");

        String userUid = mAuth.getCurrentUser().getUid();

        countryCodePicker.registerCarrierNumberEditText(phoneNumber);

        getOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!countryCodePicker.isValidFullNumber()) {
                    phoneNumber.setError("Invalid phone number");
                } else {
                    String phone = countryCodePicker.getFullNumberWithPlus();

                    databaseReference.child("All Accounts").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.child(phone).exists()) {
                                phoneNumber.setError("Phone Number Already Registered");
                            } else {
                                try {
                                    // ✅ Generate KeyPair if not already
                                    KeyStoreHelper.generateKeyPairIfNotExists();

                                    // ✅ Get encoded public key
                                    String publicKeyString = KeyStoreHelper.getEncodedPublicKey();

                                    // Save phone and uid in "All Accounts"
                                    databaseReference.child("All Accounts").child(phone).child("phone_no").setValue(phone);
                                    databaseReference.child("All Accounts").child(phone).child("uid").setValue(userUid);

                                    // Save full user data including public key
                                    User newUser = new User(name, email, phone, publicKeyString, "active");
                                    databaseReference.child("user").child(userUid).setValue(newUser);

                                    // Navigate to home screen
                                    Intent intent = new Intent(SendOTPActivity.this, HomeScreenActivity.class);
                                    intent.putExtra("email", email);
                                    intent.putExtra("phone", phone);
                                    intent.putExtra("name", name);
                                    intent.putExtra("password", password);
                                    startActivity(intent);
                                    finish();

                                } catch (Exception e) {
                                    Toast.makeText(SendOTPActivity.this, "Key generation failed", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(SendOTPActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
