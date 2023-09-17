package com.example.barta_a_messenger_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class VerifyOTPActivity extends AppCompatActivity {

    Button verifyButton, resendButton;
    EditText editText1,editText2,editText3,editText4,editText5;

    ProgressBar progressBar;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    String verificationCode;
    Long timeoutSeconds = 60L;
    PhoneAuthProvider.ForceResendingToken resendingToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otpactivity);

        String phoneNumber = getIntent().getStringExtra("phone");
        String name = getIntent().getStringExtra("name");
        String password = getIntent().getStringExtra("password");

        Button verifyButton = findViewById(R.id.verifybutton);
        Button resendButton = findViewById(R.id.resendbutton);

        progressBar = findViewById(R.id.progressbar);

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        sendOtp(phoneNumber,true);


    }
    void sendOtp(String phoneNumber,boolean isResend){
        //startResendTimer();
        setInProgress(true);
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                                setInProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(VerifyOTPActivity.this,"OTP verification not successfull!",Toast.LENGTH_SHORT);
                                setInProgress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                resendingToken = forceResendingToken;
                                Toast.makeText(VerifyOTPActivity.this,"OTP sent successfully",Toast.LENGTH_SHORT);
                                setInProgress(false);
                            }
                        });
        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        }else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }

    }
    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);

        }else{
            progressBar.setVisibility(View.GONE);
        }
    }

    void signIn(PhoneAuthCredential phoneAuthCredential){
        //login and go to next activity
        setInProgress(true);
    }

//    void startResendTimer(){
////        resendButton.setActivated(false);
////        Timer timer = new Timer();
////        timer.scheduleAtFixedRate(new TimerTask() {
////            @Override
////            public void run() {
////                timeoutSeconds--;
////                resendButton.setText("Resend OTP in "+timeoutSeconds +" seconds");
////                if(timeoutSeconds<=0){
////                    timeoutSeconds =60L;
////                    timer.cancel();
////                    runOnUiThread(() -> {
////                        resendButton.setActivated(true);
////                    });
////                }
////            }
////        },0,1000);
//    }


}