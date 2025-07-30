package com.example.barta_a_messenger_app;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import java.util.concurrent.TimeUnit;

public class VerifyOTPActivity extends AppCompatActivity {

    Button verifyButton, resendButton;
    EditText editText1,editText2,editText3,editText4,editText5,editText6;
    TextView timer,errorView;


    ProgressDialog progressDialog;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;


    String phoneNumber,name,password,email;

    String mVerificationId;
    String typedOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otpactivity);

        email = getIntent().getStringExtra("email");
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

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Verifying...");
        progressDialog.setCancelable(false);

        resendButton.setEnabled(false);



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


        sendOtp(phoneNumber);

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                if(editText1.getText().toString().isEmpty()||editText2.getText().toString().isEmpty()||editText3.getText().toString().isEmpty()||editText4.getText().toString().isEmpty()||
                        editText5.getText().toString().isEmpty()||editText6.getText().toString().isEmpty() ){
                    progressDialog.cancel();
                    errorView.setVisibility(View.VISIBLE);
                }
                else{
                    typedOTP = editText1.getText().toString()+editText2.getText().toString()
                            +editText3.getText().toString()+editText4.getText().toString()
                            +editText5.getText().toString()+editText6.getText().toString();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,typedOTP);
                    linkWithCurrentUser(credential);
                }
            }
        });

        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.setVisibility(View.VISIBLE);
                resendButton.setEnabled(false);
                sendOtp(phoneNumber);
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


        editText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>=1){
                    editText2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>=1){
                    editText3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>=1){
                    editText4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editText4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>=1){
                    editText5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editText5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>=1){
                    editText6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editText6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>=1){
                    if(editText1.getText().toString().isEmpty() && editText2.getText().toString().isEmpty() && editText3.getText().toString().isEmpty()
                            && editText4.getText().toString().isEmpty()
                            && editText5.getText().toString().isEmpty() ){

                        typedOTP = editText1.getText().toString()+editText2.getText().toString()
                                +editText3.getText().toString()+editText4.getText().toString()
                                +editText5.getText().toString()+editText6.getText().toString();

                        progressDialog.show();

                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,typedOTP);
                        linkWithCurrentUser(credential);

                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }


    void sendOtp(String phoneNumber){
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(VerifyOTPActivity.this,"OTP verification successfull!",Toast.LENGTH_SHORT).show();
                linkWithCurrentUser(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                progressDialog.cancel();
                Toast.makeText(VerifyOTPActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                mVerificationId = verificationId;
                mResendToken = forceResendingToken;
                Toast.makeText(VerifyOTPActivity.this,"OTP sent successfully",Toast.LENGTH_SHORT).show();
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
    }


    void linkWithCurrentUser(PhoneAuthCredential credential){
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = task.getResult().getUser();
                            if(user!=null){
                                user.linkWithCredential(credential)
                                        .addOnCompleteListener(VerifyOTPActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if(task.isSuccessful()){
                                                    Log.d(TAG, "linkWithCredential:Success");
                                                    FirebaseUser user = task.getResult().getUser();

                                                    Intent intent = new Intent(VerifyOTPActivity.this,HomeScreen.class);
                                                    progressDialog.cancel();
                                                    finish();
                                                    startActivity(intent);
                                                }
                                                else{
                                                    progressDialog.cancel();
                                                    Log.w(TAG, "linkWithCredential:failure",task.getException());
                                                    Toast.makeText(VerifyOTPActivity.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();
                                                    user.delete();
                                                }
                                            }
                                        });
                            }
                        }
                        else{
                            progressDialog.cancel();
                            Log.w(TAG, "linkWithCredential: Failure", task.getException());
                            Toast.makeText(VerifyOTPActivity.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

}