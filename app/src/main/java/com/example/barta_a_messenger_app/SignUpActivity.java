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
                Intent intent = new Intent(SignUpActivity.this, SendOTPActivity.class);
                intent.putExtra("phone",phone.getText().toString());
                startActivity(intent);
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