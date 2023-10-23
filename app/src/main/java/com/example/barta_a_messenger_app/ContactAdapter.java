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

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView profile_pic;
        TextView contact_name;
        TextView contact_phone;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_pic=itemView.findViewById(R.id.contact_image);
            contact_name=itemView.findViewById(R.id.contact_name);
            contact_phone=itemView.findViewById(R.id.contact_number);

            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {

        }
    }
//    public interface ClickListener{
//        void onItemClick(int position);
//    }

//    public void setOnItemClickListener(ClickListener clickListener){
//        this.clickListener = clickListener;
//    }

}
