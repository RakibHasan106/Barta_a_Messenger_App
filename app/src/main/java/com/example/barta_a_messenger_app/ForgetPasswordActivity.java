package com.example.barta_a_messenger_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ForgetPasswordActivity extends AppCompatActivity {

    EditText email;
    Button sendResetMail , backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        email = findViewById(R.id.inputmail);
        sendResetMail = findViewById(R.id.resetpassbutton);
        backButton = findViewById(R.id.backbutton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(ForgetPasswordActivity.this, LoginPageActivity.class);
                startActivity(intent);
            }
        });

    }
}