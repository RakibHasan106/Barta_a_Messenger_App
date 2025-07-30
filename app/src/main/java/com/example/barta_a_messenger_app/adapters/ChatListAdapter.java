package com.example.barta_a_messenger_app.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barta_a_messenger_app.models.Contact;
import com.example.barta_a_messenger_app.helpers.EncryptionHelper;
import com.example.barta_a_messenger_app.R;
import com.example.barta_a_messenger_app.activities.InboxActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> {
    Context context;
    static ArrayList<Contact> list;

    public ChatListAdapter(Context context, ArrayList<Contact> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.contacts, parent, false);
        return new ChatListAdapter.MyViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.MyViewHolder holder, int position) {
        Contact contact = list.get(position);
        holder.contact_name.setText(contact.getFull_name());

        String encryptedAESKey = contact.getEncryptedAESKey();
        String iv = contact.getIv();
        String encryptedMessage = contact.getLast_message();

        Log.d("ChatListAdapter", "Contact: " + contact.getFull_name());
        Log.d("ChatListAdapter", "Encrypted Message: " + (encryptedMessage != null ? encryptedMessage : "Null"));
        Log.d("ChatListAdapter", "Encrypted AES Key: " + (encryptedAESKey != null ? "Present" : "Null"));
        Log.d("ChatListAdapter", "IV: " + (iv != null ? "Present" : "Null"));

        String decryptedmessage = "";

        // ✅ Check for file/image message and skip decryption
        if ("sent a file".equals(encryptedMessage) || "sent an image".equals(encryptedMessage)) {
            decryptedmessage = encryptedMessage;
            Log.d("ChatListAdapter", "No decryption needed for file/image message.");
        }
        else if (encryptedMessage == null || encryptedMessage.isEmpty()) {
            decryptedmessage = "";
            Log.d("ChatListAdapter", "No message to decrypt");
        }
        else if (encryptedAESKey == null || encryptedAESKey.isEmpty() || iv == null || iv.isEmpty()) {
            decryptedmessage = encryptedMessage;
            Log.w("ChatListAdapter", "Missing encryption data - showing raw message");
        }
        else {
            try {
                decryptedmessage = EncryptionHelper.decryptMessage(encryptedMessage, encryptedAESKey, iv);
                Log.d("ChatListAdapter", "Successfully decrypted message");
            } catch (Exception e) {
                Log.e("ChatListAdapter", "Decryption failed: " + e.getMessage(), e);
                decryptedmessage = null;
            }
        }

        if (encryptedMessage == null || encryptedMessage.isEmpty()) {
            holder.contact_phone.setText("");
        } else {
            if(decryptedmessage!=null){
                if ("You".equals(contact.getLast_sender_name())) {
                    holder.contact_phone.setText(contact.getLast_sender_name() + " : " + decryptedmessage);
                }
                else {
                    holder.contact_phone.setText(decryptedmessage);
                }
            }
            else {
                holder.contact_phone.setText(decryptedmessage);
            }
        }

        if ("false".equals(contact.getLast_message_seen())) {
            holder.contact_phone.setTypeface(null, Typeface.BOLD);
        } else {
            holder.contact_phone.setTypeface(null, Typeface.NORMAL);
        }

        String profilePicUrl = contact.getProfilePic();
        String status = contact.getStatus();

        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
            Picasso.get()
                    .load(profilePicUrl)
                    .placeholder(R.drawable.profile_icon) // fallback while loading
                    .error(R.drawable.profile_icon)       // fallback if URL fails
                    .into(holder.profile_pic);
        } else {
            // Clear any previously set image to avoid incorrect recycling
            Picasso.get().cancelRequest(holder.profile_pic);
            holder.profile_pic.setImageResource(R.drawable.profile_icon);
        }


        if ("active".equals(status)) {
            holder.active_status.setVisibility(View.VISIBLE);
        } else {
            holder.active_status.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView profile_pic, active_status;
        TextView contact_name;
        TextView contact_phone;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_pic = itemView.findViewById(R.id.contact_image);
            contact_name = itemView.findViewById(R.id.contact_name);
            contact_phone = itemView.findViewById(R.id.contact_number);
            active_status = itemView.findViewById(R.id.online_status);

            itemView.setOnClickListener(view -> {
                Context c = view.getContext();
                Intent intent = new Intent(c, InboxActivity.class);
                int position = getAdapterPosition();
                Contact contact = list.get(position);
                intent.putExtra("Name", contact.getFull_name());
                intent.putExtra("phone_no", contact.getPhone_number());
                intent.putExtra("contact_uid", contact.getUid());
                intent.putExtra("profile_pic", contact.getProfilePic());
                contact_phone.setTypeface(null, Typeface.NORMAL);
                c.startActivity(intent);
            });
        }
    }
}
