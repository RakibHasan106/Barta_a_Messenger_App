package com.example.barta_a_messenger_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barta_a_messenger_app.R;
import com.example.barta_a_messenger_app.models.Request;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.MyViewHolder> {
    Context context;
    ArrayList<Request> list;
    FriendRequestActionListener actionListener;

    public interface FriendRequestActionListener {
        void onAcceptClicked(Request request);
        void onRejectClicked(Request request);
    }

    public FriendRequestAdapter(Context context, ArrayList<Request> list, FriendRequestActionListener actionListener) {
        this.context = context;
        this.list = list;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use contact_item.xml layout (or make sure request.xml has same structure)
        View v = LayoutInflater.from(context).inflate(R.layout.request, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestAdapter.MyViewHolder holder, int position) {
        Request request = list.get(position);

        // Set name and phone
        holder.userNameTextView.setText(request.getName() != null ? request.getName() : "Unknown User");
        holder.contact_phone.setText(request.getPhone() != null ? request.getPhone() : "");

        // Load profile picture using Picasso
        if (request.getProfilePic() != null && !request.getProfilePic().isEmpty()) {
            Picasso.get()
                    .load(request.getProfilePic())
                    .placeholder(R.drawable.profile_icon) // fallback while loading
                    .error(R.drawable.profile_icon)       // fallback if failed
                    .into(holder.profile_pic);
        } else {
            // Set default profile icon if no profile picture
            holder.profile_pic.setImageResource(R.drawable.profile_icon);
        }

        // Set click listeners for accept and reject buttons
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
        return (list != null) ? list.size() : 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView, contact_phone;
        CircleImageView profile_pic;
        CircleImageView acceptButton, rejectButton;

        public MyViewHolder(View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            contact_phone = itemView.findViewById(R.id.contact_number);
            profile_pic = itemView.findViewById(R.id.contact_image);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
        }
    }
}