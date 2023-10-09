package com.example.barta_a_messenger_app;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class ForgetPasswordActivity extends AppCompatActivity {

    EditText email;
    Button sendResetMail , backButton;

    FirebaseAuth mAuth;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        email = findViewById(R.id.inputmail);
        sendResetMail = findViewById(R.id.resetpassbutton);
        backButton = findViewById(R.id.backbutton);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");

        mAuth = FirebaseAuth.getInstance();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(ForgetPasswordActivity.this, LoginPageActivity.class);
                startActivity(intent);
            }
        });

        sendResetMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                if(email.getText().toString().isEmpty()){
                    progressDialog.cancel();
                    email.setError("type your email");
                }
                else{
                    mAuth.sendPasswordResetEmail(email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent intent = new Intent(ForgetPasswordActivity.this,PasswordResetLinkSentActivity.class);
                                        progressDialog.cancel();
                                        startActivity(intent);
                                    }
                                    else{
                                        try{
                                            throw task.getException();
                                        }
                                        catch (Exception e) {
                                            progressDialog.cancel();
                                            Toast.makeText(ForgetPasswordActivity.this,"Authentication Failed",Toast.LENGTH_LONG).show();
                                        }

                                        Log.w(TAG,"user creation failed",task.getException());
                                    }
                                }
                            });
                }
            }
        });

    }
}