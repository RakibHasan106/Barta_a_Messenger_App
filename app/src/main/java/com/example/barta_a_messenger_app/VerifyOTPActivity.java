package com.example.barta_a_messenger_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class VerifyOTPActivity extends AppCompatActivity {

    Button verifyButton, resendButton;
    EditText editText1,editText2,editText3,editText4,editText5,editText6;
    TextView timer,errorView;

    ProgressBar progressBar;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;


    String phoneNumber,name,password;

    String mVerificationId;
    String typedOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otpactivity);

        phoneNumber = getIntent().getStringExtra("phone");
        name = getIntent().getStringExtra("name");
        password = getIntent().getStringExtra("password");

        errorView = findViewById(R.id.error_text);


        editText1 = findViewById(R.id.inputCode1);
        editText2 = findViewById(R.id.inputCode2);
        editText3 = findViewById(R.id.inputCode3);
        editText4 = findViewById(R.id.inputCode4);
        editText5 = findViewById(R.id.inputCode5);
        editText6 = findViewById(R.id.inputCode6);

        timer = findViewById(R.id.timer);

        verifyButton = findViewById(R.id.verifybutton);
        resendButton = findViewById(R.id.resendbutton);

        resendButton.setEnabled(false);

        progressBar = findViewById(R.id.progressbar);


        //timer for otp send
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                // This method will be called every second until the timer is finished
                long secondsRemaining = millisUntilFinished / 1000;
                if(secondsRemaining>=10){
                    timer.setText("00:"+secondsRemaining);
                }
                else{
                    timer.setText("00:0"+secondsRemaining);
                }

            }

            public void onFinish() {
                // This method will be called when the timer finishes
                // Perform actions when the timer is done
                resendButton.setEnabled(true);
                timer.setVisibility(View.GONE);
            }
        }.start();


        sendOtp(phoneNumber,true);

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText1.getText().toString().isEmpty()||editText2.getText().toString().isEmpty()||editText3.getText().toString().isEmpty()||editText4.getText().toString().isEmpty()||
                        editText5.getText().toString().isEmpty()||editText6.getText().toString().isEmpty() ){
                    errorView.setVisibility(View.VISIBLE);
                }
                else{
                    typedOTP = editText1.getText().toString()+editText2.getText().toString()
                            +editText3.getText().toString()+editText4.getText().toString()
                            +editText5.getText().toString()+editText6.getText().toString();
                    verifyotp();
                }
            }
        });

        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.setVisibility(View.VISIBLE);
                resendButton.setEnabled(false);
                new CountDownTimer(60000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        // This method will be called every second until the timer is finished
                        long secondsRemaining = millisUntilFinished / 1000;
                        if(secondsRemaining>=10){
                            timer.setText("00:"+secondsRemaining);
                        }
                        else{
                            timer.setText("00:0"+secondsRemaining);
                        }
                    }

                    public void onFinish() {
                        // This method will be called when the timer finishes
                        // Perform actions when the timer is done
                        resendButton.setEnabled(true);
                        timer.setVisibility(View.GONE);
                    }
                }.start();
            }
        });


    }
    void sendOtp(String phoneNumber,boolean isResend){
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(VerifyOTPActivity.this,"OTP verification successfull!",Toast.LENGTH_SHORT);
                signin(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(VerifyOTPActivity.this,"OTP verification not successfull!",Toast.LENGTH_SHORT);
                e.printStackTrace();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                mVerificationId = verificationId;
                mResendToken = forceResendingToken;
                Toast.makeText(VerifyOTPActivity.this,"OTP sent successfully",Toast.LENGTH_SHORT);
            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L,TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);


        //startResendTimer();
//        setInProgress(true);
//        PhoneAuthOptions.Builder builder =
//                PhoneAuthOptions.newBuilder(mAuth)
//                        .setPhoneNumber(phoneNumber)
//                        .setTimeout(60L, TimeUnit.SECONDS)
//                        .setActivity(this)
//                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                            @Override
//                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                                signIn(phoneAuthCredential);
//                                setInProgress(false);
//                            }
//
//                            @Override
//                            public void onVerificationFailed(@NonNull FirebaseException e) {
//                                Toast.makeText(VerifyOTPActivity.this,"OTP verification not successfull!",Toast.LENGTH_SHORT);
//                                setInProgress(false);
//                            }
//
//                            @Override
//                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                                super.onCodeSent(s, forceResendingToken);
//                                verificationCode = s;
//                                resendingToken = forceResendingToken;
//                                Toast.makeText(VerifyOTPActivity.this,"OTP sent successfully",Toast.LENGTH_SHORT);
//                                setInProgress(false);
//                            }
//                        });
//        if(isResend){
//            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
//        }else{
//            PhoneAuthProvider.verifyPhoneNumber(builder.build());
//        }

    }
//    void setInProgress(boolean inProgress){
//        if(inProgress){
//            progressBar.setVisibility(View.VISIBLE);
//
//        }else{
//            progressBar.setVisibility(View.GONE);
//        }
//    }

//    void signIn(PhoneAuthCredential phoneAuthCredential){
//        //login and go to next activity
//        setInProgress(true);
//    }

 //   void startResendTimer(){
//        resendButton.setActivated(false);
//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                timeoutSeconds--;
//                resendButton.setText("Resend OTP in "+timeoutSeconds +" seconds");
//                if(timeoutSeconds<=0){
//                    timeoutSeconds =60L;
//                    timer.cancel();
//                    runOnUiThread(() -> {
//                        resendButton.setActivated(true);
//                    });
//                }
//            }
//        },0,1000);

    void verifyotp(){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,typedOTP);
        signin(credential);

    }

    void signin(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //Successful Verification
                            FirebaseUser user = task.getResult().getUser();

                            //User is signed in
                        }
                        else{
                            Exception e = task.getException();
                        }
                    }
                });
    }

}