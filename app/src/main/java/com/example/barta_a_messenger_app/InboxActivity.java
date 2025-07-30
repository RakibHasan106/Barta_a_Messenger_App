package com.example.barta_a_messenger_app;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class InboxActivity extends AppCompatActivity{

    TextView userName;

    ImageView DP;
    AppCompatImageView backButton;

    RecyclerView chatRecyclerView;

    FirebaseAuth mAuth;

    AppCompatImageView sendButton;
    EditText inputMessage;

    FirebaseDatabase database;
    ImageButton imageSendButton;

    String checker="",myUrl="";
    Uri imagePath,fileUri;
    String imageUrl,fileUrl;

    String senderRoom,receiverRoom,senderId,receiverId;

    ArrayList<MessageModel> localMessageModel;

    private DBHelper dbHelper;
    SQLiteDatabase db;
    ValueEventListener chatListener,otherChatListener;
    ChatAdapter chatAdapter;
    String messageSenderName,senderName;

    String decryptedmessage,decryptedmessagenotification,encryptedMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        userName = findViewById(R.id.userName);
        userName.setText(getIntent().getStringExtra("Name").toString());
        DP = findViewById(R.id.headImageView);

        String profilePictureUrl = getIntent().getStringExtra("profile_pic");

        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
            Picasso.get().load(profilePictureUrl).into(DP);
        }

        backButton = findViewById(R.id.imageBack);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);

        sendButton = findViewById(R.id.send);
        inputMessage = findViewById(R.id.inputMessage);

        imageSendButton = findViewById(R.id.image_send_button);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();



        senderId = mAuth.getCurrentUser().getUid();
        receiverId = getIntent().getStringExtra("contact_uid").toString();

        database.getReference().child("user").child(senderId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        senderName = snapshot.child("username").getValue(String.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        senderRoom = senderId + receiverId;
        receiverRoom = receiverId + senderId;

        dbHelper.chat_table_name="t_"+senderRoom;

        dbHelper = new DBHelper(this);


        db = dbHelper.getWritableDatabase();

        localMessageModel = new ArrayList<>();
        localMessageModel = getAllMessages();


        chatAdapter = new ChatAdapter(localMessageModel,this,receiverId);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);

        chatRecyclerView.scrollToPosition(localMessageModel.size()-1);


        chatListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        MessageModel message = snapshot1.getValue(MessageModel.class);
                        message.setMessageId(snapshot1.getKey());
                        message.setIsNotified("yes");

                        try {
                            // STEP: Hybrid decryption
                            String decryptedMessage = EncryptionHelper.decryptMessage(
                                    message.getMessage(),
                                    message.getEncryptedAESKey(),
                                    message.getIv()
                            );
                            message.setMessage(decryptedMessage);

                        } catch (Exception e) {
                            Log.e("DECRYPTION", "Failed to decrypt message: " + e.getMessage(), e);
                            message.setMessage("[decryption failed]");
                        }

                        localMessageModel.add(message);
                        updateLocalDatabase(message);
                        chatAdapter.notifyDataSetChanged();
                        chatRecyclerView.scrollToPosition(localMessageModel.size() - 1);
                    }

                    database.getReference().child("Contacts").child(senderId)
                            .child(receiverId).child("last_message_seen")
                            .setValue("true");

                    database.getReference().child("chats").child(senderId).child(receiverId).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CHAT", "Chat listener cancelled: " + error.getMessage());
            }
        };


        otherChatListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                    if (!datasnapshot.getKey().equals(receiverId)) {
                        for (DataSnapshot dataSnapshot2 : datasnapshot.getChildren()) {
                            MessageModel message = dataSnapshot2.getValue(MessageModel.class);

                            if (message != null && "no".equals(message.getIsNotified())) {

                                // STEP 1: Get sender's name
                                database.getReference().child("user")
                                        .child(message.getUid()).get()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                DataSnapshot ds = task.getResult();
                                                if (ds.exists()) {
                                                    messageSenderName = ds.child("username").getValue(String.class);

                                                    try {
                                                        // STEP 2: Hybrid decryption
                                                        decryptedmessagenotification = EncryptionHelper.decryptMessage(
                                                                message.getMessage(),
                                                                message.getEncryptedAESKey(),
                                                                message.getIv()
                                                        );

                                                    } catch (Exception e) {
                                                        Log.e("NOTIF DECRYPTION", "Failed to decrypt message: " + e.getMessage(), e);
                                                        decryptedmessagenotification = "[decryption failed]";
                                                    }

                                                    // STEP 3: Show notification dialog
                                                    NotificationHelper.notificationDialog(
                                                            InboxActivity.this,
                                                            decryptedmessagenotification,
                                                            messageSenderName
                                                    );

                                                    // STEP 4: Mark message as notified
                                                    database.getReference().child("chats")
                                                            .child(senderId)
                                                            .child(datasnapshot.getKey())
                                                            .child(dataSnapshot2.getKey())
                                                            .child("isNotified")
                                                            .setValue("yes");
                                                }
                                            } else {
                                                Log.e("USER LOOKUP", "Failed to retrieve sender info.");
                                            }
                                        });
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("otherChatListener", "Cancelled: " + error.getMessage());
            }
        };


        database.getReference().child("chats")
                .child(senderId)
                .child(receiverId)
                .addValueEventListener(chatListener);

        database.getReference().child("chats")
                .child(senderId).addValueEventListener(otherChatListener);



        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = inputMessage.getText().toString().trim();
                if (!message.isEmpty()) {

                    // STEP 1: Get receiver's public key from Firebase
                    DatabaseReference receiverKeyRef = FirebaseDatabase.getInstance()
                            .getReference("Contacts")
                            .child(senderId)
                            .child(receiverId)
                            .child("publicKey");

                    // STEP 1.5: Get sender's public key from KeyStoreHelper
                    String senderPublicKey = KeyStoreHelper.getPublicKeyBase64(); // You need this method implemented

                    receiverKeyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String receiverPublicKey = snapshot.getValue(String.class);

                                try {
                                    // STEP 2: Encrypt using hybrid method with dual encryption
                                    EncryptionHelper.EncryptedPayload payload =
                                            EncryptionHelper.encryptMessage(message, receiverPublicKey, senderPublicKey);

                                    MessageModel model = new MessageModel(senderId, payload.encryptedMessage);
                                    model.setEncryptedAESKey(payload.encryptedAESKey);                // Receiver encrypted AES key
                                    model.setIv(payload.iv);
                                    model.setTimestamp(new Date().getTime());
                                    model.setMessageType("msg");
                                    model.setIsNotified("no");
                                    // Optionally store sender-side encrypted AES key in the message model if needed
                                    // model.setEncryptedAESKeyForSender(payload.encryptedAESKeyForSender);

                                    inputMessage.setText("");

                                    // STEP 3: Push to Firebase
                                    String key = database.getReference().child("chats")
                                            .child(receiverId)
                                            .child(senderId)
                                            .push().getKey();

                                    model.setMessageId(key);

                                    database.getReference().child("chats")
                                            .child(receiverId)
                                            .child(senderId)
                                            .child(key)
                                            .setValue(model)
                                            .addOnSuccessListener(unused -> {
                                                // Store original message locally
                                                model.setMessage(message);
                                                updateLocalDatabase(model);
                                                localMessageModel.add(model);
                                                chatAdapter.notifyDataSetChanged();
                                                chatRecyclerView.scrollToPosition(localMessageModel.size() - 1);

                                                // Update Contact metadata for RECEIVER side
                                                DatabaseReference receiverContactRef = database.getReference()
                                                        .child("Contacts").child(receiverId).child(senderId);
                                                receiverContactRef.child("last_message").setValue(payload.encryptedMessage);
                                                receiverContactRef.child("last_sender_name").setValue("");
                                                receiverContactRef.child("message_time").setValue(model.getTimestamp());
                                                receiverContactRef.child("last_message_seen").setValue("false");
                                                receiverContactRef.child("encryptedAESKey").setValue(payload.encryptedAESKey);
                                                receiverContactRef.child("iv").setValue(payload.iv);

                                                // Update Contact metadata for SENDER side
                                                DatabaseReference senderContactRef = database.getReference()
                                                        .child("Contacts").child(senderId).child(receiverId);
                                                senderContactRef.child("last_message").setValue(payload.encryptedMessage);
                                                senderContactRef.child("last_sender_name").setValue("You");
                                                senderContactRef.child("message_time").setValue(model.getTimestamp());
                                                senderContactRef.child("last_message_seen").setValue("true");
                                                senderContactRef.child("encryptedAESKey").setValue(payload.encryptedAESKeyForSender); // Sender side encrypted AES key
                                                senderContactRef.child("iv").setValue(payload.iv);
                                            });

                                } catch (Exception e) {
                                    Log.e("ENCRYPTION", "Encryption failed: " + e.getMessage(), e);
                                }

                            } else {
                                Log.e("KEY ERROR", "Receiver public key not found in Contacts node.");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("KEY ERROR", "Error retrieving public key: " + error.getMessage());
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

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/pdf");
                            startActivityForResult(intent.createChooser(intent,"Select Pdf File"),123);
                        }
                        else{
                            checker="doc";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/msword");
                            startActivityForResult(Intent.createChooser(intent,"Select Doc File"),123);
                        }
                    }
                });
                builder.show();
            }
        });


        inputMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    chatRecyclerView.scrollToPosition(localMessageModel.size()-1);
                }
            }
        });

        chatRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                chatRecyclerView.scrollToPosition(localMessageModel.size()-1);
                // when the edittext is pressed , the onscreen keyboard is displayed. so the recyclerview is hidden.
                // setonclicklistener will also not work for first click.
                // cause after first click the recylerview will go to the botton position , that's right,
                // but then the on screen keyboard will pop up
                // and the recylerview's bottom portion will be hidden again.
                //so i've used the scrollToPosition method when the on screen keyboard appears

            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InboxActivity.this,HomeScreen.class);

                startActivity(intent);
                finish();
            }
        });

        chatRecyclerView.setAdapter(chatAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        database.getReference().child("chats")
                .child(senderId)
                .child(receiverId).removeEventListener(chatListener);

        database.getReference().child("chats")
                .child(senderId).removeEventListener(otherChatListener);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.getReference().child("chats")
                .child(senderId)
                .child(receiverId).removeEventListener(chatListener);

        database.getReference().child("chats")
                .child(senderId).removeEventListener(otherChatListener);
    }

    private void updateLocalDatabase(MessageModel message) {
        ContentValues values = new ContentValues();

        values.put("MESSAGEID", message.getMessageId());
        values.put("MESSAGE", message.getMessage());
        values.put("MESSAGETYPE",message.getMessageType());
        values.put("ISNOTIFIED",message.getIsNotified());
        values.put("TIMESTAMP", message.getTimestamp());
        values.put("SENDER_ID",message.getUid());

        db.insert(dbHelper.chat_table_name, null, values);

    }


    @Override
    protected void onActivityResult(int requestCode,int resultCode,@Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode==123 && resultCode==RESULT_OK && data!=null && data.getData()!=null){

            if(checker.equals("image")){
                imagePath = data.getData();
                uploadImage();
            }
            else if(checker.equals("pdf")){
                fileUri = data.getData();
                uploadFile("pdf");
            }
            else if(checker.equals("doc")){
                fileUri = data.getData();
                uploadFile("doc");
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

        FirebaseStorage.getInstance()
                .getReference("chat_images/" + UUID.randomUUID().toString())
                .putFile(imagePath)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        task.getResult().getStorage().getDownloadUrl()
                                .addOnCompleteListener(urlTask -> {
                                    if (urlTask.isSuccessful()) {
                                        imageUrl = urlTask.getResult().toString();

                                        // 1) Get receiver's public key from Contacts
                                        DatabaseReference receiverKeyRef = FirebaseDatabase.getInstance()
                                                .getReference("Contacts")
                                                .child(senderId)
                                                .child(receiverId)
                                                .child("publicKey");

                                        receiverKeyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (!snapshot.exists()) {
                                                    Log.e("KEY ERROR", "Receiver public key not found in Contacts node.");
                                                    progressDialog.dismiss();
                                                    return;
                                                }

                                                String receiverPublicKey = snapshot.getValue(String.class);

                                                try {
                                                    // 2) Get sender public key from keystore (Base64)
                                                    String senderPublicKey = KeyStoreHelper.getPublicKeyBase64();

                                                    // 3) Encrypt imageUrl with EncryptionHelper (dual encryption)
                                                    EncryptionHelper.EncryptedPayload payload =
                                                            EncryptionHelper.encryptMessage(imageUrl, receiverPublicKey, senderPublicKey);

                                                    // 4) Build message model
                                                    MessageModel model = new MessageModel(senderId, payload.encryptedMessage, "img");
                                                    model.setEncryptedAESKey(payload.encryptedAESKey); // for receiver
                                                    // if your MessageModel has this field, set it (recommended)
                                                    // model.setEncryptedAESKeyForSender(payload.encryptedAESKeyForSender);
                                                    model.setIv(payload.iv);
                                                    model.setTimestamp(new Date().getTime());
                                                    model.setIsNotified("no");

                                                    // 5) Push to chats/receiverId/senderId
                                                    String key = database.getReference().child("chats")
                                                            .child(receiverId)
                                                            .child(senderId)
                                                            .push().getKey();

                                                    model.setMessageId(key);

                                                    database.getReference().child("chats")
                                                            .child(receiverId)
                                                            .child(senderId)
                                                            .child(key)
                                                            .setValue(model)
                                                            .addOnSuccessListener(unused -> {
                                                                // store original url locally only
                                                                model.setMessage(imageUrl);
                                                                updateLocalDatabase(model);
                                                                localMessageModel.add(model);
                                                                chatAdapter.notifyDataSetChanged();
                                                                chatRecyclerView.scrollToPosition(localMessageModel.size() - 1);

                                                                long ts = model.getTimestamp();

                                                                // 6) Update Contacts (RECEIVER)
                                                                DatabaseReference receiverContactRef = database.getReference()
                                                                        .child("Contacts").child(receiverId).child(senderId);
                                                                receiverContactRef.child("last_message").setValue("sent an image");
                                                                receiverContactRef.child("last_sender_name").setValue("");
                                                                receiverContactRef.child("message_time").setValue(ts);
                                                                receiverContactRef.child("last_message_seen").setValue("false");
                                                                receiverContactRef.child("encryptedAESKey").setValue(payload.encryptedAESKey);
                                                                receiverContactRef.child("iv").setValue(payload.iv);

                                                                // 7) Update Contacts (SENDER)
                                                                DatabaseReference senderContactRef = database.getReference()
                                                                        .child("Contacts").child(senderId).child(receiverId);
                                                                senderContactRef.child("last_message").setValue("sent an image");
                                                                senderContactRef.child("last_sender_name").setValue("You");
                                                                senderContactRef.child("message_time").setValue(ts);
                                                                senderContactRef.child("last_message_seen").setValue("true");
                                                                // if you saved sender-side AES key separately, use it here. If not, you can still store payload.encryptedAESKey.
                                                                // Prefer: payload.encryptedAESKeyForSender (if your MessageModel/DB supports it)
                                                                // senderContactRef.child("encryptedAESKey").setValue(payload.encryptedAESKeyForSender);
                                                                senderContactRef.child("encryptedAESKey").setValue(payload.encryptedAESKey);
                                                                senderContactRef.child("iv").setValue(payload.iv);

                                                                progressDialog.dismiss();
                                                            });

                                                } catch (Exception e) {
                                                    Log.e("ENCRYPTION", "Encryption failed: " + e.getMessage(), e);
                                                    progressDialog.dismiss();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Log.e("KEY ERROR", "Error retrieving public key: " + error.getMessage());
                                                progressDialog.dismiss();
                                            }
                                        });

                                    } else {
                                        Log.e("UPLOAD", "Failed to get image download URL: " + urlTask.getException());
                                        progressDialog.dismiss();
                                    }
                                });
                    } else {
                        Log.e("UPLOAD", "Image upload failed: " + task.getException());
                        progressDialog.dismiss();
                    }
                });
    }




    private void uploadFile(String fileType) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading....");
        progressDialog.show();

        // Upload file to Firebase Storage
        FirebaseStorage.getInstance()
                .getReference("files/" + getFileNameFromUri(fileUri))
                .putFile(fileUri)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        task.getResult().getStorage().getDownloadUrl()
                                .addOnCompleteListener(urlTask -> {
                                    if (urlTask.isSuccessful()) {
                                        String fileUrl = urlTask.getResult().toString();

                                        // Create MessageModel with unencrypted file URL
                                        MessageModel model = new MessageModel(senderId, fileUrl, fileType);
                                        model.setTimestamp(new Date().getTime());
                                        model.setIsNotified("no");

                                        // Push message to Firebase Database under chats/receiverId/senderId
                                        String key = database.getReference().child("chats")
                                                .child(receiverId)
                                                .child(senderId)
                                                .push().getKey();

                                        model.setMessageId(key);

                                        database.getReference().child("chats")
                                                .child(receiverId)
                                                .child(senderId)
                                                .child(key)
                                                .setValue(model)
                                                .addOnSuccessListener(unused -> {
                                                    // Store file URL locally for sender
                                                    updateLocalDatabase(model);
                                                    localMessageModel.add(model);
                                                    chatAdapter.notifyDataSetChanged();
                                                    chatRecyclerView.scrollToPosition(localMessageModel.size() - 1);

                                                    long ts = model.getTimestamp();

                                                    // Update Contacts metadata for receiver
                                                    DatabaseReference receiverContactRef = database.getReference()
                                                            .child("Contacts").child(receiverId).child(senderId);
                                                    receiverContactRef.child("last_message").setValue("sent a file");
                                                    receiverContactRef.child("last_sender_name").setValue("");
                                                    receiverContactRef.child("message_time").setValue(ts);
                                                    receiverContactRef.child("last_message_seen").setValue("false");

                                                    // Update Contacts metadata for sender
                                                    DatabaseReference senderContactRef = database.getReference()
                                                            .child("Contacts").child(senderId).child(receiverId);
                                                    senderContactRef.child("last_message").setValue("sent a file");
                                                    senderContactRef.child("last_sender_name").setValue("You");
                                                    senderContactRef.child("message_time").setValue(ts);
                                                    senderContactRef.child("last_message_seen").setValue("true");

                                                    progressDialog.dismiss();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("UPLOAD", "Failed to save message to database: " + e.getMessage());
                                                    progressDialog.dismiss();
                                                });

                                    } else {
                                        Log.e("UPLOAD", "Failed to get file download URL: " + urlTask.getException());
                                        progressDialog.dismiss();
                                    }
                                });
                    } else {
                        Log.e("UPLOAD", "File upload failed: " + task.getException());
                        progressDialog.dismiss();
                    }
                });
    }






    public ArrayList<MessageModel> getAllMessages() {
        ArrayList<MessageModel> messages = new ArrayList<>();

        db = dbHelper.getWritableDatabase();
        String createTableQuery = "CREATE TABLE IF NOT EXISTS "+dbHelper.chat_table_name+
                " (MESSAGEID TEXT PRIMARY KEY," +
                " MESSAGE TEXT, MESSAGETYPE TEXT ," +
                "ISNOTIFIED TEXT,"+
                "TIMESTAMP INTEGER, SENDER_ID TEXT);";

        db.execSQL(createTableQuery);

        db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                dbHelper.chat_table_name,
                null,  // projection: null means all columns
                null,  // selection
                null,  // selectionArgs
                null,  // groupBy
                null,  // having
                "TIMESTAMP" + " ASC"  // orderBy
        );

        while (cursor.moveToNext()) {
            MessageModel message = new MessageModel();

            message.setMessageId(cursor.getString(cursor.getColumnIndexOrThrow("MESSAGEID"))); //new added
            message.setMessage(cursor.getString(cursor.getColumnIndexOrThrow("MESSAGE")));
            message.setMessageType(cursor.getString(cursor.getColumnIndexOrThrow("MESSAGETYPE")));
            message.setIsNotified(cursor.getString(cursor.getColumnIndexOrThrow("ISNOTIFIED")));
            message.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow("TIMESTAMP")));
            message.setUid(cursor.getString(cursor.getColumnIndexOrThrow("SENDER_ID")));

            messages.add(message);
        }

        cursor.close();

        //db.close();

        //Log.d("message",messages.toString());

        return messages;
    }

    private String getFileNameFromUri(Uri uri) {
        DocumentFile documentFile = DocumentFile.fromSingleUri(this, uri);
        return documentFile.getName();
    }
}