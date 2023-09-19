package com.example.barta_a_messenger_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hbb20.CountryCodePicker;

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
                }
            }
        });
    }
}