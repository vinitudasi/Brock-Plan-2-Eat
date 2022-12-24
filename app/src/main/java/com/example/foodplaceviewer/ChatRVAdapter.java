package com.example.foodplaceviewer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatRVAdapter extends RecyclerView.Adapter {

    //Adapter Sequences
    private ArrayList<ChatModel> chatsModelArrayList;
    private Context context;

    //Constructor
    /*
     * Adapter sets the layouts for subsequent messages
     *
     * */
    public ChatRVAdapter(ArrayList<ChatModel> chatsModelArrayList, Context context) {
        this.chatsModelArrayList = chatsModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch(viewType){
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_user_rv_item,parent,false);
                return new UserViewHolder(view);

            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_bot_rv_item,parent,false);
                return new BotViewHolder(view);
        }
        return null;
    }


    //Subsequent Textview Differentiator
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatModel chatModel = chatsModelArrayList.get(position);
        switch (chatModel.getSender()){
            case "user":
                ((UserViewHolder)holder).userTV.setText(chatModel.getMessage());
                break;
            case "bot":
                ((BotViewHolder)holder).botMsgTV.setText(chatModel.getMessage());
                break;
        }
    }


    @Override
    public int getItemViewType(int pos){
        switch(chatsModelArrayList.get(pos).getSender()){
            case "user":
                return 0;
            case "bot":
                return 1;
            default:
                return -1;
        }
    }


    @Override
    public int getItemCount() {
        return chatsModelArrayList.size();
    }


    //Class for Bot's textview(Bot Message)
    public static class BotViewHolder extends RecyclerView.ViewHolder{
        TextView botMsgTV;
        public BotViewHolder(@NonNull View itemView){
            super(itemView);
            botMsgTV = itemView.findViewById(R.id.idTVBot);
        }
    }


    //Class for User's textview
    public static class UserViewHolder extends RecyclerView.ViewHolder{
        TextView userTV;
        public UserViewHolder(@NonNull View itemView){
            super(itemView);
            userTV = itemView.findViewById(R.id.idTextViewUser);
        }
    }



}