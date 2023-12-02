package com.example.barta_a_messenger_app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class friendRequestFragment extends Fragment implements FriendRequestAdapter.FriendRequestActionListener{

    private RecyclerView recyclerView;
    private FriendRequestAdapter adapter;
    FirebaseAuth mAuth ;
    String uid;
    DatabaseReference friendRequestRef;
    private ArrayList<Request> friendRequest;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_request, container, false);

        recyclerView=view.findViewById(R.id.recyclerView);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        friendRequest = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new FriendRequestAdapter(requireContext(), friendRequest,this);
        loadFriendRequests();
        recyclerView.setAdapter(adapter);



        return view;
    }

    private void loadFriendRequests() {
        DatabaseReference friendRequestsRef = FirebaseDatabase.getInstance().getReference("FriendRequestPending").child(uid);

        friendRequestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    friendRequest.clear();
                    Request request = dataSnapshot.getValue(Request.class);

                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user").child(request.getSenderUid());
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String username = snapshot.child("username").getValue(String.class);
                            request.setName(username);
                            friendRequest.add(request);
                            adapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });

    }

    @Override
    public void onAcceptClicked(Request request) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user").child(request.getSenderUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.child("username").getValue(String.class);
                String phone = snapshot.child("phone").getValue(String.class);
                request.setName(username);
                request.setPhone(phone);
                friendRequest.add(request);
                DatabaseReference contactsRef = FirebaseDatabase.getInstance().getReference("Contacts")
                        .child(request.getReceiverUid())
                        .child(request.getSenderUid());

                contactsRef.setValue(new Contact(request.getName(), request.getPhone(), request.getSenderUid(), "","","",new Date().getTime(),"",""));


                adapter.notifyDataSetChanged();
                friendRequest.clear();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference userRef2 = FirebaseDatabase.getInstance().getReference("user").child(request.getReceiverUid());
        userRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.child("username").getValue(String.class);
                String phone = snapshot.child("phone").getValue(String.class);
                request.setName(username);
                request.setPhone(phone);
                friendRequest.add(request);
                DatabaseReference contactsRef = FirebaseDatabase.getInstance().getReference("Contacts")
                        .child(request.getSenderUid())
                        .child(request.getReceiverUid());

                contactsRef.setValue(new Contact(request.getName(), request.getPhone(), request.getReceiverUid(), "","","",new Date().getTime(),"",""));

                adapter.notifyDataSetChanged();
                friendRequest.clear();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        DatabaseReference friendRequestsRef = FirebaseDatabase.getInstance().getReference("FriendRequestPending")
                .child(request.getReceiverUid())
                .child(request.getSenderUid());

        friendRequestsRef.removeValue();

    }

    @Override
    public void onRejectClicked(Request request) {
        DatabaseReference friendRequestsRef = FirebaseDatabase.getInstance().getReference("FriendRequestPending")
                .child(request.getReceiverUid())
                .child(request.getSenderUid());

        friendRequestsRef.removeValue();
        adapter.notifyDataSetChanged();
        friendRequest.clear();

    }
}