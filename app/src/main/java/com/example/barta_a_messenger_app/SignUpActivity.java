package com.example.barta_a_messenger_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignUpActivity extends AppCompatActivity {

    Button sendOTPButton,loginButton;
    EditText phone,name,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        sendOTPButton = findViewById(R.id.otpsendbutton);
        loginButton = findViewById(R.id.loginbutton);

        phone = findViewById(R.id.phone);
        name = findViewById(R.id.name);
        password = findViewById(R.id.password);

        sendOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if the edittexts are empty then set error , else change the activity.

                if(phone.getText().toString().isEmpty()==true){
                    phone.setError("not filled");
                }
                if(name.getText().toString().isEmpty()==true){
                    name.setError("not filled");
                }
                if(password.getText().toString().isEmpty()==true){
                    password.setError("not filled");
                }
                if(!phone.getText().toString().isEmpty() && !name.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
                    Intent intent = new Intent(SignUpActivity.this, SendOTPActivity.class);
                    intent.putExtra("phone",phone.getText().toString());
                    intent.putExtra("name",name.getText().toString());
                    intent.putExtra("password",password.getText().toString());
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
}