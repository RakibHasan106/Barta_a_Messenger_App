package com.example.barta_a_messenger_app.activities;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.barta_a_messenger_app.models.Contact;
import com.example.barta_a_messenger_app.helpers.EncryptionHelper;
import com.example.barta_a_messenger_app.models.MessageModel;
import com.example.barta_a_messenger_app.helpers.NotificationHelper;
import com.example.barta_a_messenger_app.R;
import com.example.barta_a_messenger_app.fragments.chatFragment;
import com.example.barta_a_messenger_app.fragments.friendRequestFragment;
import com.example.barta_a_messenger_app.fragments.profileFragment;
import com.example.barta_a_messenger_app.adapters.viewPagerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeScreenActivity extends BaseActivity{

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private FloatingActionButton fab;
    BottomNavigationView bottomNavigationView;

    RecyclerView recyclerView;

    TextView header_user;
    ImageView header_img;

    FirebaseAuth mAuth ;
    FirebaseUser user;

    DatabaseReference databaseReference;
    String uid;

    ValueEventListener notificationListener;
    FirebaseDatabase database;

    ArrayList<Contact> contactList;
    String sendername;
    String decryptedmessage;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);

        tabLayout.setupWithViewPager(viewPager);

        viewPagerAdapter vpAdapter = new viewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment(new chatFragment(),"CHATS");
        vpAdapter.addFragment(new profileFragment(),"PROFILE");
        vpAdapter.addFragment(new friendRequestFragment(),"REQUEST");

        viewPager.setAdapter(vpAdapter);

        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab_button);

        header_user = findViewById(R.id.head_textView);
        header_img = findViewById(R.id.headImageView);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user!=null){
            uid = user.getUid();
        }

        database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("user").child(uid);


        if (!isNotificationPermissionGranted()) {
            // If not granted, prompt the user to grant permission
            showNotificationPermissionDialog();
            //promptForNotificationPermission();
        }
        else{
            Toast.makeText(HomeScreenActivity.this,"notification enabled",Toast.LENGTH_SHORT);
        }


        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("username").getValue(String.class);
                    String profilePictureUrl = dataSnapshot.child("profilePicture").getValue(String.class);

                    header_user.setText(name);
                    if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                        Picasso.get().load(profilePictureUrl).into(header_img);
                    }
                    else {

                     }

                } else {
                    // Handle the case where the user data doesn't exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });

        notificationListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                    for(DataSnapshot dataSnapshot2 : datasnapshot.getChildren()){
                        MessageModel message = dataSnapshot2.getValue(MessageModel.class);

                        if(message != null && "no".equals(message.getIsNotified())){

                            database.getReference().child("user")
                                    .child(message.getUid()).get()
                                    .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DataSnapshot ds = task.getResult();
                                                if (ds.exists()) {
                                                    sendername = ds.child("username").getValue(String.class);

                                                    // Handle different message types
                                                    String messageType = message.getMessageType();
                                                    String notificationMessage = "";

                                                    if ("msg".equals(messageType)) {
                                                        // Text message - decrypt it
                                                        try {
                                                            String encryptedAESKey = message.getEncryptedAESKey();
                                                            String iv = message.getIv();
                                                            String encryptedMsg = message.getMessage();

                                                            Log.d("DecryptionDebug", "EncryptedAESKey: " + encryptedAESKey);
                                                            Log.d("DecryptionDebug", "IV: " + iv);
                                                            Log.d("DecryptionDebug", "EncryptedMsg: " + encryptedMsg);

                                                            if (encryptedAESKey == null || iv == null || encryptedMsg == null) {
                                                                notificationMessage = encryptedMsg != null ? encryptedMsg : "";
                                                                Log.d("DecryptionDebug", "Missing encryption parameters, skipping decryption.");
                                                            } else {
                                                                notificationMessage = EncryptionHelper.decryptMessage(encryptedMsg, encryptedAESKey, iv);
                                                                Log.d("DecryptionDebug", "Decrypted message: " + notificationMessage);
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("DecryptionDebug", "Decryption failed", e);
                                                            notificationMessage = "";
                                                        }
                                                    }
                                                    else if ("img".equals(messageType)) {
                                                        // Image message - show generic notification
                                                        notificationMessage = "sent an image";
                                                    }
                                                    else if ("pdf".equals(messageType)) {
                                                        // PDF file - show generic notification
                                                        notificationMessage = "sent a file";
                                                    }
                                                    else if ("doc".equals(messageType)) {
                                                        // DOC file - show generic notification
                                                        notificationMessage = "sent a file";
                                                    }
                                                    else {
                                                        // Unknown message type
                                                        notificationMessage = "sent a message";
                                                    }

                                                    // Show notification
                                                    new NotificationHelper().notificationDialog(HomeScreenActivity.this, notificationMessage, sendername);
                                                }
                                            }
                                        }
                                    });

                            // Mark message as notified
                            database.getReference().child("chats")
                                    .child(uid).child(datasnapshot.getKey())
                                    .child(dataSnapshot2.getKey())
                                    .child("isNotified").setValue("yes");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("NotificationListener", "Cancelled: " + error.getMessage());
            }
        };

        database.getReference().child("chats")
                .child(uid)
                .addValueEventListener(notificationListener);


    }

    @Override
    public void onBackPressed() {


    }



    @Override
    protected void onStop() {
        super.onStop();
        database.getReference().child("chats").child(uid).removeEventListener(notificationListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        database.getReference().child("chats").child(uid).removeEventListener(notificationListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.getReference().child("chats").child(uid).removeEventListener(notificationListener);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        database.getReference().child("chats").child(uid).addValueEventListener(notificationListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        database.getReference().child("chats").child(uid).addValueEventListener(notificationListener);
    }

    private void showNotificationPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.notification_permission_dialog, null);
        builder.setView(dialogView);
        builder.setTitle("Please Give Notification Permission to Continue");

        Button btnYes = dialogView.findViewById(R.id.btnOk);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptForNotificationPermission();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


    }

    private boolean isNotificationPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // For Android Oreo and above, use NotificationManagerCompat
            return NotificationManagerCompat.from(this)
                    .areNotificationsEnabled();
        } else {
            // For versions below Oreo, check if the notification channel is enabled
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            return notificationManager != null &&
                    notificationManager.getImportance() != NotificationManager.IMPORTANCE_NONE;
        }
    }

    private void promptForNotificationPermission() {
        // You can show a dialog or navigate the user to the notification settings
        // For example, you can open the app settings page
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivity(intent);

        // You can also display a toast or a dialog to inform the user
        //Toast.makeText(this, "Please enable notification permission", Toast.LENGTH_LONG).show();
    }

}