package com.example.barta_a_messenger_app.activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barta_a_messenger_app.R;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginPageActivity extends AppCompatActivity{

    Button loginButton, signupButton;
    ImageView googleButton;

    EditText email,password;

    TextView forgetPass;

    ProgressDialog progressDialog;

    FirebaseAuth mAuth;

    DatabaseReference databaseReference;

    SignInClient oneTapClient;
    BeginSignInRequest signUpRequest;

    ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        loginButton = findViewById(R.id.loginbutton);
        signupButton = findViewById(R.id.signupbutton);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        forgetPass = findViewById(R.id.forgetpasstext);




        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPageActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });

        oneTapClient = Identity.getSignInClient(this);
        signUpRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.web_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();

        mAuth = FirebaseAuth.getInstance();

        activityResultLauncher =
                registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            try {
                                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                                String idToken = credential.getGoogleIdToken();
                                if (idToken !=  null) {
                                    navigateToSecondActivity();
                                }
                            } catch (ApiException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                if(email.getText().toString().isEmpty()==true){
                    progressDialog.cancel();
                    email.setError("required");
                }
                if(password.getText().toString().isEmpty()==true){
                    progressDialog.cancel();
                    password.setError("password empty");
                }
                if(!email.getText().toString().isEmpty() &&  !password.getText().toString().isEmpty()){
                    signInwithEmailPassword(email.getText().toString(),password.getText().toString());
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPageActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        Log.d("AUTH_DEBUG_CRITICAL", "=== onStart() Debug ===");
        Log.d("AUTH_DEBUG_CRITICAL", "Current user in onStart: " + (currentUser != null ? currentUser.getUid() : "NULL"));
        Log.d("AUTH_DEBUG_CRITICAL", "Is anonymous: " + (currentUser != null ? currentUser.isAnonymous() : "N/A"));
        Log.d("AUTH_DEBUG_CRITICAL", "Email: " + (currentUser != null ? currentUser.getEmail() : "N/A"));
        Log.d("AUTH_DEBUG_CRITICAL", "Creation timestamp: " + (currentUser != null ? currentUser.getMetadata().getCreationTimestamp() : "N/A"));
        Log.d("AUTH_DEBUG_CRITICAL", "Last sign in timestamp: " + (currentUser != null ? currentUser.getMetadata().getLastSignInTimestamp() : "N/A"));
        Log.d("AUTH_DEBUG_CRITICAL", "========================");

        if (currentUser != null && !currentUser.isAnonymous()) {
            Log.d("AUTH_DEBUG_CRITICAL", "NAVIGATING TO HOMESCREEN - User found!");
            Intent intent = new Intent(LoginPageActivity.this, HomeScreenActivity.class);
            startActivity(intent);
        } else if (currentUser != null && currentUser.isAnonymous()) {
            Log.d("AUTH_DEBUG_CRITICAL", "ANONYMOUS USER FOUND - Signing out");
            mAuth.signOut();
        } else {
            Log.d("AUTH_DEBUG_CRITICAL", "NO USER FOUND - Staying on login screen");
        }
    }





    private void signOut() {
        // Implement sign out logic here
    }

    private void signInwithGoogle() {
        oneTapClient.beginSignIn(signUpRequest)
                .addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        IntentSenderRequest intentSenderRequest =
                                new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build();

                        activityResultLauncher.launch(intentSenderRequest);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.getLocalizedMessage());
                    }
                });
    }


    void signInwithEmailPassword(String mail,String pass){
        mAuth.signInWithEmailAndPassword(mail,pass).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG,"signInWithEmail:success");

                    mAuth = FirebaseAuth.getInstance();
                    String uid = mAuth.getCurrentUser().getUid();

                    databaseReference = FirebaseDatabase.getInstance().getReference().child("user").child(uid);

                    databaseReference.child("status").setValue("active");

                    Intent intent = new Intent(LoginPageActivity.this, HomeScreenActivity.class);
                    progressDialog.cancel();

                    startActivity(intent);
                }
                else{
                    progressDialog.cancel();
                    Log.w(TAG,"signInWithEmail:Failed",task.getException());
                    Toast.makeText(LoginPageActivity.this,"Email Or Password is Wrong",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void navigateToSecondActivity() {
        finish();
        Intent intent = new Intent(LoginPageActivity.this, HomeScreenActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

    }
}