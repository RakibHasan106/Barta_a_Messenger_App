package com.example.barta_a_messenger_app;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    Button sendOTPButton,loginButton;
    EditText email,name,password;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        sendOTPButton = findViewById(R.id.otpsendbutton);
        loginButton = findViewById(R.id.loginbutton);

        email = findViewById(R.id.email);
        name = findViewById(R.id.name);
        password = findViewById(R.id.password);

        mAuth = FirebaseAuth.getInstance();

        sendOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if the edittexts are empty then set error , else change the activity.

                if(email.getText().toString().isEmpty()==true){
                    email.setError("not filled");
                }
                if(name.getText().toString().isEmpty()==true){
                    name.setError("not filled");
                }
                if(password.getText().toString().isEmpty()==true){
                    password.setError("not filled");
                }
                if(!email.getText().toString().isEmpty() && !name.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
                    createNewUser(email.getText().toString(),password.getText().toString());
                    Intent intent = new Intent(SignUpActivity.this, SendOTPActivity.class);
                    intent.putExtra("email", email.getText().toString());
                    intent.putExtra("name",name.getText().toString());
                    intent.putExtra("password", password.getText().toString());
                    startActivity(intent);

                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginPageActivity.class);
                startActivity(intent);
            }
        });
    }

    void createNewUser(String mail,String pass){
        mAuth.createUserWithEmailAndPassword(mail,pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "New User Created Successfully!");
                            FirebaseUser user = mAuth.getCurrentUser();

                            ///user.delete();

                            Intent intent = new Intent(SignUpActivity.this, SendOTPActivity.class);
                            intent.putExtra("email",mail);
                            intent.putExtra("name",name.getText().toString());
                            intent.putExtra("password",pass);
                            startActivity(intent);
                        }
                        else{

                            try{
                                throw task.getException();
                            }
                            catch (FirebaseAuthWeakPasswordException weakPasswordException){
                                password.setError("password must be greater than 6 characters");
                                Toast.makeText(SignUpActivity.this,"Invalid Password",Toast.LENGTH_LONG).show();
                            }
                            catch(FirebaseAuthUserCollisionException userCollisionException){
                                email.setError("This Email is Already Registered");
                                Toast.makeText(SignUpActivity.this,"Email Already Registered",Toast.LENGTH_LONG).show();
                            }
                            catch (Exception e) {
                                Toast.makeText(SignUpActivity.this,"Authentication Failed",Toast.LENGTH_LONG).show();
                            }

                            Log.w(TAG,"user creation failed",task.getException());
//                            Toast.makeText(SignUpActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}