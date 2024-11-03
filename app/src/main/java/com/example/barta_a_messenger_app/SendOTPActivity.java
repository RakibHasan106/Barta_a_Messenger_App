package com.example.barta_a_messenger_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class SendOTPActivity extends AppCompatActivity {

    EditText phoneNumber;
    Button getOtpButton;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    CountryCodePicker countryCodePicker;

    DatabaseReference databaseReference ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_otpactivity);

        phoneNumber = findViewById(R.id.inputMobile);
        getOtpButton = findViewById(R.id.getotpbutton);
        countryCodePicker = findViewById(R.id.countrypicker);

        databaseReference =  FirebaseDatabase.getInstance().getReference();

        String email,name,password;

        String user = mAuth.getCurrentUser().getUid();

        email = getIntent().getStringExtra("email");
        name = getIntent().getStringExtra("name");
        password = getIntent().getStringExtra("password");


        countryCodePicker.registerCarrierNumberEditText(phoneNumber);
        getOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!countryCodePicker.isValidFullNumber()){
                    phoneNumber.setError("Invalid phone number");
                }

                else{
                    String phone = countryCodePicker.getFullNumberWithPlus();
//                    databaseReference.child("All Accounts").child(phone).setValue(phone);
                    databaseReference.child("All Accounts").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.child(phone).exists()){
                                phoneNumber.setError("Phone Number Already Registered");
                            }
                            else{
                                databaseReference.child("All Accounts").child(phone).child("phone_no").setValue(phone);
                                databaseReference.child("All Accounts").child(phone).child("uid").setValue(FirebaseAuth.getInstance().getUid());
                                databaseReference.child("user").child(user).setValue(new User(name,email,phone,"","active"));
                                Intent intent = new Intent(SendOTPActivity.this, HomeScreen.class);
                                intent.putExtra("email",email);
                                intent.putExtra("phone",countryCodePicker.getFullNumberWithPlus());
                                intent.putExtra("name",name);
                                intent.putExtra("password",password);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                            Toast.makeText(SendOTPActivity.this,"error",Toast.LENGTH_SHORT).show();

                        }
                    });


                }
            }
        });
    }
}