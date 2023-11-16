package com.example.barta_a_messenger_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class InboxActivity extends AppCompatActivity {

    TextView userName;
    AppCompatImageView backButton;

    RecyclerView chatRecyclerView;

    FirebaseAuth mAuth;

    AppCompatImageView sendButton;
    EditText inputMessage;

    FirebaseDatabase database;
    ImageButton imageSendButton;

    String checker="",myUrl="";
    Uri imagePath;
    String imageUrl;

    String senderRoom,receiverRoom,senderId,receiverId;

    ArrayList<MessageModel> messageModels;

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

        imageSendButton = findViewById(R.id.image_send_button);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        senderId = mAuth.getCurrentUser().getUid();
        receiverId = getIntent().getStringExtra("contact_uid").toString();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InboxActivity.this,HomeScreen.class);
                startActivity(intent);
            }
        });

        messageModels = new ArrayList<>();
        final ChatAdapter chatAdapter = new ChatAdapter(messageModels,this,receiverId);

        chatRecyclerView.setAdapter(chatAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        chatRecyclerView.setLayoutManager(layoutManager);

        senderRoom = senderId + receiverId;
        receiverRoom = receiverId + senderId;

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
                                        chatRecyclerView.scrollToPosition(messageModels.size()-1);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

        RecyclerView.ItemAnimator animator = chatRecyclerView.getItemAnimator();
        if (animator instanceof DefaultItemAnimator){
            DefaultItemAnimator defaultItemAnimator = (DefaultItemAnimator) animator;
            defaultItemAnimator.setAddDuration(1000);
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = inputMessage.getText().toString();
                if(!message.isEmpty()){
                    final MessageModel model = new MessageModel(senderId,message);
                    model.setTimestamp(new Date().getTime());
                    inputMessage.setText("");


                    database.getReference().child("chats")
                            .child(senderRoom)
                            .push()
                            .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
//                                chatRecyclerView.refreshDrawableState();
                                    chatRecyclerView.scrollToPosition(messageModels.size()-1);
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

            }
        });

        imageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence options[] = new CharSequence[]
                        {
                                "Images",
                                "PDF Files",
                                "MS Word Files"
                        };

                AlertDialog.Builder builder = new AlertDialog.Builder(InboxActivity.this);
                builder.setTitle("Select the File");

                builder.setItems(options, new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            checker="image";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent,"Select Image"),123);
                        }
                        else if(i==1){
                            checker="pdf";
                        }
                        else{
                            checker="doc";
                        }
                    }
                });
                builder.show();
            }
        });

//        chatRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                chatRecyclerView.scrollToPosition(messageModels.size()-1);
//                chatRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//            }
//        });

        inputMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    chatRecyclerView.scrollToPosition(messageModels.size()-1);
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,@Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode==123 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imagePath = data.getData();
            if(!checker.equals("image")){
                
            }
            else if(checker.equals("image")){
                uploadImage();



            }
            else if(checker.equals("pdf")){

            }
            else if(checker.equals("doc")){

            }
            else{
                Toast.makeText(this,"Nothing Selected,Error",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImage() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading....");
        progressDialog.show();

        FirebaseStorage.getInstance().getReference("chat_images/"+ UUID.randomUUID().toString()).putFile(imagePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                imageUrl = task.getResult().toString();
//                                Toast.makeText(InboxActivity.this, imageUrl, Toast.LENGTH_SHORT).show();

                                MessageModel model = new MessageModel(senderId,imageUrl,true);
                                model.setTimestamp(new Date().getTime());

                                database.getReference().child("chats")
                                        .child(senderRoom)
                                        .push()
                                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
//                                chatRecyclerView.refreshDrawableState();
                                                chatRecyclerView.scrollToPosition(messageModels.size()-1);
                                                database.getReference().child("chats")
                                                        .child(receiverRoom)
                                                        .push()
                                                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                progressDialog.dismiss();
                                                            }
                                                        });
                                            }
                                        });

//                                updateProfilePicture(task.getResult().toString());
                            }

                        }

                    });
//                    Toast.makeText(ProfileActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                }
                else {
//                    Toast.makeText(ProfileActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        });


    }

    private void getImageInImageView() {

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imagePath);
        }
        catch (IOException e){
            e.printStackTrace();
        }

//        imgProfile.setImageBitmap(bitmap);
    }
}