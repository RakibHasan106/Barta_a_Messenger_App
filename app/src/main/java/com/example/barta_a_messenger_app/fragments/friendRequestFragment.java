package com.example.barta_a_messenger_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barta_a_messenger_app.models.Contact;
import com.example.barta_a_messenger_app.R;
import com.example.barta_a_messenger_app.models.Request;
import com.example.barta_a_messenger_app.adapters.FriendRequestAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Date;

public class friendRequestFragment extends Fragment implements FriendRequestAdapter.FriendRequestActionListener {

    private RecyclerView recyclerView;
    private FriendRequestAdapter adapter;
    private FirebaseAuth mAuth;
    private String uid;
    private ArrayList<Request> friendRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(com.example.barta_a_messenger_app.R.layout.fragment_friend_request, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        friendRequest = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new FriendRequestAdapter(requireContext(), friendRequest, this);
        recyclerView.setAdapter(adapter);

        loadFriendRequests();
        return view;
    }

    private void loadFriendRequests() {
        DatabaseReference friendRequestsRef = FirebaseDatabase.getInstance()
                .getReference("FriendRequestPending")
                .child(uid);

        friendRequestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendRequest.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Request request = dataSnapshot.getValue(Request.class);
                    if (request == null) continue;

                    DatabaseReference userRef = FirebaseDatabase.getInstance()
                            .getReference("user")
                            .child(request.getSenderUid());

                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String username = snapshot.child("username").getValue(String.class);
                            request.setName(username);
                            friendRequest.add(request);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    @Override
    public void onAcceptClicked(Request request) {
        DatabaseReference senderUserRef = FirebaseDatabase.getInstance().getReference("user").child(request.getSenderUid());
        DatabaseReference receiverUserRef = FirebaseDatabase.getInstance().getReference("user").child(request.getReceiverUid());

        // Add sender to receiver's Contacts list
        senderUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot senderSnap) {
                String senderName = senderSnap.child("username").getValue(String.class);
                String senderPhone = senderSnap.child("phone").getValue(String.class);
                String senderProfilePic = senderSnap.child("profilePicture").getValue(String.class);
                String senderStatus = senderSnap.child("status").getValue(String.class);
                String senderPublicKey = senderSnap.child("publicKey").getValue(String.class);

                DatabaseReference contactRef = FirebaseDatabase.getInstance()
                        .getReference("Contacts")
                        .child(request.getReceiverUid())
                        .child(request.getSenderUid());

                contactRef.setValue(new Contact(
                        senderName != null ? senderName : "",
                        senderPhone != null ? senderPhone : "",
                        request.getSenderUid(),
                        senderProfilePic != null ? senderProfilePic : "",
                        senderStatus != null ? senderStatus : "",
                        "", new Date().getTime(), "", "", senderPublicKey != null ? senderPublicKey : "", "", ""
                ));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Add receiver to sender's Contacts list
        receiverUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot receiverSnap) {
                String receiverName = receiverSnap.child("username").getValue(String.class);
                String receiverPhone = receiverSnap.child("phone").getValue(String.class);
                String receiverProfilePic = receiverSnap.child("profilePicture").getValue(String.class);
                String receiverStatus = receiverSnap.child("status").getValue(String.class);
                String receiverPublicKey = receiverSnap.child("publicKey").getValue(String.class);

                DatabaseReference contactRef = FirebaseDatabase.getInstance()
                        .getReference("Contacts")
                        .child(request.getSenderUid())
                        .child(request.getReceiverUid());

                contactRef.setValue(new Contact(
                        receiverName != null ? receiverName : "",
                        receiverPhone != null ? receiverPhone : "",
                        request.getReceiverUid(),
                        receiverProfilePic != null ? receiverProfilePic : "",
                        receiverStatus != null ? receiverStatus : "",
                        "", new Date().getTime(), "", "", receiverPublicKey != null ? receiverPublicKey : "", "", ""
                ));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Remove from pending requests
        DatabaseReference requestRef = FirebaseDatabase.getInstance()
                .getReference("FriendRequestPending")
                .child(request.getReceiverUid())
                .child(request.getSenderUid());
        requestRef.removeValue();

        friendRequest.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRejectClicked(Request request) {
        DatabaseReference requestRef = FirebaseDatabase.getInstance()
                .getReference("FriendRequestPending")
                .child(request.getReceiverUid())
                .child(request.getSenderUid());

        requestRef.removeValue();
        friendRequest.clear();
        adapter.notifyDataSetChanged();
    }
}
