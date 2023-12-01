package com.example.barta_a_messenger_app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

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
            return new ReceiverViewHolder(view);
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

        if(messageModel.getMessageType().equals("msg")){
            if(holder.getClass() == SenderViewHolder.class){
                ((SenderViewHolder)holder).sentImage.setVisibility(View.GONE);
                ((SenderViewHolder)holder).sentFile.setVisibility(View.GONE);
                ((SenderViewHolder)holder).senderMsg.setVisibility(View.VISIBLE);
                ((SenderViewHolder)holder).senderMsg.setText(messageModel.getMessage());
                ((SenderViewHolder)holder).senderTime.setText(new SimpleDateFormat("HH:mm a").format(new Date(messageModel.getTimestamp())));
            }
            else{
                ((ReceiverViewHolder)holder).receivedImage.setVisibility(View.GONE);
                ((ReceiverViewHolder)holder).receivedFile.setVisibility(View.GONE);
                ((ReceiverViewHolder)holder).receiverMsg.setVisibility(View.VISIBLE);
                ((ReceiverViewHolder)holder).receiverMsg.setText(messageModel.getMessage());
                ((ReceiverViewHolder)holder).receiverTime.setText(new SimpleDateFormat("HH:mm a").format(new Date(messageModel.getTimestamp())));
            }
        }
        else if(messageModel.getMessageType().equals( "img")){
            if(holder.getClass() == SenderViewHolder.class){
                ((SenderViewHolder)holder).senderMsg.setVisibility(View.GONE);
                ((SenderViewHolder)holder).sentFile.setVisibility(View.GONE);
                ((SenderViewHolder)holder).sentImage.setVisibility(View.VISIBLE);
                Picasso.get().load(messageModel.getMessage()).into(((SenderViewHolder)holder).sentImage);
                ((SenderViewHolder)holder).senderTime.setText(new SimpleDateFormat("HH:mm a").format(new Date(messageModel.getTimestamp())));
            }
            else{
                ((ReceiverViewHolder)holder).receiverMsg.setVisibility(View.GONE);
                ((ReceiverViewHolder)holder).receivedFile.setVisibility(View.GONE);
                ((ReceiverViewHolder)holder).receivedImage.setVisibility(View.VISIBLE);
                Picasso.get().load(messageModel.getMessage()).into(((ReceiverViewHolder)holder).receivedImage);
                ((ReceiverViewHolder)holder).receiverTime.setText(new SimpleDateFormat("HH:mm a").format(new Date(messageModel.getTimestamp())));
            }
        }
        else{
            if(holder.getClass() == SenderViewHolder.class){
                ((SenderViewHolder)holder).sentFile.setVisibility(View.VISIBLE);
                ((SenderViewHolder)holder).senderMsg.setVisibility(View.GONE);
                ((SenderViewHolder)holder).sentImage.setVisibility(View.GONE);
                if(messageModel.getMessageType().equals("pdf")){
                    ((SenderViewHolder)holder).sentFile.setImageResource(R.drawable.pdf_icon);
                }
                else{
                    ((SenderViewHolder)holder).sentFile.setImageResource(R.drawable.word_icon);
                }
                ((SenderViewHolder)holder).senderTime.setText(new SimpleDateFormat("HH:mm a").format(new Date(messageModel.getTimestamp())));
            }
            else{
                ((ReceiverViewHolder)holder).receivedFile.setVisibility(View.VISIBLE);
                ((ReceiverViewHolder)holder).receiverMsg.setVisibility(View.GONE);
                ((ReceiverViewHolder)holder).receivedImage.setVisibility(View.GONE);
                if(messageModel.getMessageType().equals("pdf")){
                    ((ReceiverViewHolder)holder).receivedFile.setImageResource(R.drawable.pdf_icon);
                }
                else{
                    ((ReceiverViewHolder)holder).receivedFile.setImageResource(R.drawable.word_icon);
                }
                ((ReceiverViewHolder)holder).receiverTime.setText(new SimpleDateFormat("HH:mm a").format(new Date(messageModel.getTimestamp())));
            }
        }

    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder{
        TextView receiverMsg, receiverTime;

        ImageView receivedImage;
        ImageButton receivedFile;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);

            receiverMsg = itemView.findViewById(R.id.receiverText);
            receiverTime = itemView.findViewById(R.id.receiverTime);
            receivedImage = itemView.findViewById(R.id.received_image);
            receivedFile = itemView.findViewById(R.id.received_file);
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMsg,senderTime;
        ImageView sentImage;
        ImageButton sentFile;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMsg = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);
            sentImage = itemView.findViewById(R.id.sent_image);
            sentFile = itemView.findViewById(R.id.sent_file);

            sentFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context c = view.getContext();
                    int position = getAdapterPosition();
                    MessageModel messageModel = messageModels.get(position);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(messageModel.getMessage()));
                    c.startActivity(intent);
                }
            });
        }
    }
}
