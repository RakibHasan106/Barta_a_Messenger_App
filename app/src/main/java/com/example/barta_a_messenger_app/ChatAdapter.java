package com.example.barta_a_messenger_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import kotlinx.coroutines.channels.Send;

public class ChatAdapter extends RecyclerView.Adapter{

    ArrayList<MessageModel> messageModels;
    Context context;
    String recId;



    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context) {
        this.messageModels = messageModels;
        this.context = context;
    }

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context, String recId) {
        this.messageModels = messageModels;
        this.context = context;
        this.recId = recId;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == SENDER_VIEW_TYPE){
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender,parent,false);
            return new SenderViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.sample_receiver,parent,false);
            return new RecieverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position){
        if(messageModels.get(position).getUid().equals(FirebaseAuth.getInstance().getUid())){
            return SENDER_VIEW_TYPE;
        }
        else{
            return RECEIVER_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel = messageModels.get(position);

        if(messageModel.isItImage==false){
            if(holder.getClass() == SenderViewHolder.class){
                ((SenderViewHolder)holder).sentImage.setVisibility(View.GONE);
                ((SenderViewHolder)holder).senderMsg.setVisibility(View.VISIBLE);
                ((SenderViewHolder)holder).senderMsg.setText(messageModel.getMessage());
                ((SenderViewHolder)holder).senderTime.setText(new SimpleDateFormat("HH:mm").format(new Date(messageModel.getTimestamp())));
            }
            else{
                ((RecieverViewHolder)holder).receivedImage.setVisibility(View.GONE);
                ((RecieverViewHolder)holder).receiverMsg.setVisibility(View.VISIBLE);
                ((RecieverViewHolder)holder).receiverMsg.setText(messageModel.getMessage());
                ((RecieverViewHolder)holder).receiverMsg.setText(new SimpleDateFormat("HH:mm").format(new Date(messageModel.getTimestamp())));
            }
        }
        else{
            if(holder.getClass() == SenderViewHolder.class){
                ((SenderViewHolder)holder).senderMsg.setVisibility(View.GONE);
                ((SenderViewHolder)holder).sentImage.setVisibility(View.VISIBLE);
                Picasso.get().load(messageModel.getMessage()).into(((SenderViewHolder)holder).sentImage);
                ((SenderViewHolder)holder).senderTime.setText(new SimpleDateFormat("HH:mm").format(new Date(messageModel.getTimestamp())));
            }
            else{
                ((RecieverViewHolder)holder).receiverMsg.setVisibility(View.GONE);
                ((RecieverViewHolder)holder).receivedImage.setVisibility(View.VISIBLE);
                Picasso.get().load(messageModel.getMessage()).into(((RecieverViewHolder)holder).receivedImage);
                ((RecieverViewHolder)holder).receiverMsg.setText(new SimpleDateFormat("HH:mm").format(new Date(messageModel.getTimestamp())));
            }
        }

    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public class RecieverViewHolder extends RecyclerView.ViewHolder{
        TextView receiverMsg, receiverTime;

        ImageView receivedImage;

        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);

            receiverMsg = itemView.findViewById(R.id.receiverText);
            receiverTime = itemView.findViewById(R.id.receiverTime);
            receivedImage = itemView.findViewById(R.id.received_image);
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMsg,senderTime;
        ImageView sentImage;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMsg = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);
            sentImage = itemView.findViewById(R.id.sent_image);
        }
    }
}
