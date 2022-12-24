package com.example.foodplaceviewer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    Context context;
    ArrayList<FoodPlace> list;
    String CURRENT_USER;

    public Adapter(Context context, ArrayList<FoodPlace> list, String username) {
        this.context = context;
        this.list = list;
        this.CURRENT_USER = username;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.foodplace_entry, parent, false);
        return new ViewHolder(view);
    }

    // items contained in one specific card view in RecyclerViewer
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodPlace fp = list.get(position);

        long ID = fp.getId();
        String LOCATION = fp.getLocation();
        String NAME = fp.getName();
        String WEBSITE = fp.getWebsiteURL();
        String IMAGE = fp.getImageURL();
        String TAGS = fp.getTags();
        String IMAGE2 = fp.getImageURL2();
        int FAVORITE = fp.getFavorite();

        // setting text on the recyclerview card
        holder.name.setText(NAME);
        holder.tags.setText(TAGS);
        holder.location.setText(LOCATION);

        Glide.with(context).load(IMAGE).into(holder.imageView);

//        try {
//            URL url = new URL(IMAGE);
//            Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//            Drawable image = new BitmapDrawable(context.getResources(), bitmap);
//            holder.imageView.setBackground(image);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // make the recycleview cards clickable
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, FoodPlaceView.class);

                i.putExtra("ID", ID);
                i.putExtra("CURRENT_USER", CURRENT_USER);
                i.putExtra("LOCATION", LOCATION);
                i.putExtra("PLACE_NAME", NAME);
                i.putExtra("WEBSITE_URL", WEBSITE);
                i.putExtra("IMAGE_URL", IMAGE2);
                i.putExtra("TAGS", TAGS);
                i.putExtra("FAVORITE", FAVORITE);

                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // These are just elements for the individual elements for each of the cards in the recycle view
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, tags, location;
        ImageView imageView;

        // itemView refers to the items on the cardview
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.idName);
            tags = itemView.findViewById(R.id.idTags);
            location = itemView.findViewById(R.id.idLocation);
            imageView = itemView.findViewById(R.id.idImageView);
        }
    }
}
