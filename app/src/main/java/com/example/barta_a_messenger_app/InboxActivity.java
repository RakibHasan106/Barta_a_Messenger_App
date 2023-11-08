package com.example.barta_a_messenger_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class InboxActivity extends AppCompatActivity {

    TextView userName;
    AppCompatImageView backButton;

    RecyclerView chatRecyclerView;

    FirebaseAuth mAuth;

    AppCompatImageView sendButton;
    EditText inputMessage;

    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        userName = findViewById(R.id.userName);
        userName.setText(getIntent().getStringExtra("Name").toString());


        backButton = findViewById(R.id.imageBack);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);

        sendButton = findViewById(R.id.send);
        inputMessage = findViewById(R.id.inputMessage);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        String senderId = mAuth.getCurrentUser().getUid();
        String receiverId = getIntent().getStringExtra("contact_uid").toString();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InboxActivity.this,HomeScreen.class);
                startActivity(intent);
            }
        });

        final ArrayList<MessageModel> messageModels = new ArrayList<>();
        final ChatAdapter chatAdapter = new ChatAdapter(messageModels,this,receiverId);

        chatRecyclerView.setAdapter(chatAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        chatRecyclerView.setLayoutManager(layoutManager);

        final String senderRoom = senderId + receiverId;
        final String receiverRoom = receiverId + senderId;

        database.getReference().child("chats")
                        .child(senderRoom)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        messageModels.clear();
                                        for(DataSnapshot snapshot1:snapshot.getChildren()){
                                            MessageModel model = snapshot1.getValue(MessageModel.class);
                                            model.setMessageId(snapshot1.getKey());
                                            messageModels.add(model);
                                        }
                                        chatAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = inputMessage.getText().toString();
                final MessageModel model = new MessageModel(senderId,message);
                model.setTimestamp(new Date().getTime());
                inputMessage.setText("");

                database.getReference().child("chats")
                        .child(senderRoom)
                        .push()
                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference().child("chats")
                                        .child(receiverRoom)
                                        .push()
                                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        });
                            }
                        });
            }
        });



    }
}