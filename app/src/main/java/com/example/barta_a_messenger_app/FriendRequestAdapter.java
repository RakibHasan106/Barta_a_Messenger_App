package com.example.barta_a_messenger_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.MyViewHolder> {
    Context context;

    static ArrayList<Request> list;
    FriendRequestActionListener actionListener;

    public interface FriendRequestActionListener {
        void onAcceptClicked(Request request);
        void onRejectClicked(Request request);
    }

    public  FriendRequestAdapter(Context context, ArrayList<Request> list, FriendRequestActionListener actionListener){
        this.context = context;
        this.list = list;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.request,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestAdapter.MyViewHolder holder, int position) {
        Request request = list.get(position);
        holder.userNameTextView.setText(request.getName());
        holder.contact_phone.setText(request.getPhone());

        holder.acceptButton.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onAcceptClicked(request);
            }
        });

        holder.rejectButton.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onRejectClicked(request);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(list!=null){
            return list.size();
        }
        else{
            return -1;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;

        TextView contact_phone;
        ImageView acceptButton;
        ImageView rejectButton;

        public MyViewHolder(View itemView){
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            contact_phone = itemView.findViewById(R.id.contact_number);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
        }
    }
}
