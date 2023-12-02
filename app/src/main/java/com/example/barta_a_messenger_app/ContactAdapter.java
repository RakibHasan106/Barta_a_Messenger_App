package com.example.barta_a_messenger_app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder>{
//    private ClickListener clickListener;

    Context context;
    static ArrayList<Contact> list;


    public ContactAdapter(Context context, ArrayList<Contact> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.contacts,parent,false);
        return new MyViewHolder(v);
    }


    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Contact contact = list.get(position);
        holder.contact_name.setText(contact.getFull_name());
        holder.contact_phone.setText(contact.getPhone_number());


        String profilePicUrl = contact.getProfilePic();
        String status = contact.getStatus();

        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
            Picasso.get().load(profilePicUrl).into(holder.profile_pic);
        }
        else
        {
            // Handle the case where the URL is empty or null
        }

        if (status.equals("active")) {
            holder.active_status.setVisibility(View.VISIBLE); // Set the online status indicator to visible
        } else {
            holder.active_status.setVisibility(View.INVISIBLE); // Set the online status indicator to invisible
        }


//        Picasso.get().load(contact.getProfilePic()).into(holder.profile_pic);

//        ImageView alertImageView = holder.itemView.findViewById(R.id.danger);
//
//        if (record.shouldShowAlert()) {
//            alertImageView.setVisibility(View.VISIBLE);
//        } else {
//            alertImageView.setVisibility(View.INVISIBLE);
//        }

    }

    public int getItemCount() {
        if(list!=null){
            return list.size();
        }
        else{
            return -1;
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView profile_pic,active_status;
        TextView contact_name;
        TextView contact_phone;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_pic=itemView.findViewById(R.id.contact_image);
            contact_name=itemView.findViewById(R.id.contact_name);
            contact_phone=itemView.findViewById(R.id.contact_number);
            active_status = itemView.findViewById(R.id.online_status);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context c = view.getContext();
                    Intent intent = new Intent(c,InboxActivity.class);
                    int position = getAdapterPosition();
                    Contact contact = list.get(position);
                    intent.putExtra("Name",contact.getFull_name());
                    intent.putExtra("phone_no",contact.getPhone_number());
                    intent.putExtra("contact_uid",contact.getUid());
                    intent.putExtra("profile_pic",contact.getProfilePic());

                    c.startActivity(intent);
                }
            });
        }
    }
//    public interface ClickListener{
//        void onItemClick(int position);
//    }

//    public void setOnItemClickListener(ClickListener clickListener){
//        this.clickListener = clickListener;
//    }

}
