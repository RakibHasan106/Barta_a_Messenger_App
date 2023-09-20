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
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class SendOTPActivity extends AppCompatActivity {

    EditText phoneNumber;
    Button getOtpButton;

    CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_otpactivity);

        phoneNumber = findViewById(R.id.inputMobile);
        getOtpButton = findViewById(R.id.getotpbutton);
        countryCodePicker = findViewById(R.id.countrypicker);

        String phone,name,password;

        phone = getIntent().getStringExtra("phone");
        name = getIntent().getStringExtra("name");
        password = getIntent().getStringExtra("password");

        phoneNumber.setText(phone);

        countryCodePicker.registerCarrierNumberEditText(phoneNumber);
        getOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!countryCodePicker.isValidFullNumber()){
                    phoneNumber.setError("Invalid phone number");
                }

                else{

                    Intent intent = new Intent(SendOTPActivity.this, VerifyOTPActivity.class);
                    intent.putExtra("phone",countryCodePicker.getFullNumberWithPlus());
                    intent.putExtra("name",name);
                    intent.putExtra("password",password);
                    startActivity(intent);

//                    PhoneAuthOptions options =
//                            PhoneAuthOptions.newBuilder()
//                                    .setPhoneNumber("+15555555555")
//                                    .setTimeout(60L, TimeUnit.SECONDS)
//                                    .setActivity(SendOTPActivity.this)
//                                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                                        @Override
//                                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                                            Toast.makeText(SendOTPActivity.this,"OTP verification successfull!",Toast.LENGTH_SHORT).show();
//                                        }
//
//                                        @Override
//                                        public void onVerificationFailed(@NonNull FirebaseException e) {
//                                            Toast.makeText(SendOTPActivity.this,"OTP verification not successfull!",Toast.LENGTH_SHORT).show();
//                                            e.printStackTrace();
//                                        }
//
//                                        @Override
//                                        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                                            super.onCodeSent(verificationId,forceResendingToken);
//                                            Toast.makeText(SendOTPActivity.this,"OTP sent successfully",Toast.LENGTH_SHORT).show();
//                                            Intent intent = new Intent(SendOTPActivity.this, VerifyOTPActivity.class);
//                                            intent.putExtra("phone",countryCodePicker.getFullNumberWithPlus());
//                                            intent.putExtra("name",name);
//                                            intent.putExtra("password",password);
//                                            startActivity(intent);
//                                        }
//                                    })
//                                    .build();
//
//                    PhoneAuthProvider.verifyPhoneNumber(options);

                }
            }
        });
    }
}