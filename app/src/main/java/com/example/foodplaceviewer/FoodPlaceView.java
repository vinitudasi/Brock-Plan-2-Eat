package com.example.foodplaceviewer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// When you click on a food place, it brings you to this class
public class FoodPlaceView extends AppCompatActivity {

    private long id;
    private String currentUser;
    private String name;
    private String location;
    private String websiteURL;
    private String imageURL;
    private String tags;
    private String favorite;

    TextView tvName, tvLocation, tvTags;
    Button btnFavorite, btnWebsite;
    ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodplace_view);

        // UI Elements
        tvName = findViewById(R.id.idViewName);
        tvLocation = findViewById(R.id.idViewLocation);
        tvTags = findViewById(R.id.idViewTags);
        btnFavorite = findViewById(R.id.idViewFavorite);
        btnWebsite = findViewById(R.id.idViewWebsite);
        ivImage = findViewById(R.id.idViewImage);

        // unbundle everything with getExtras
        id = getIntent().getLongExtra("ID",0);
        //id = getIntent().getLongExtra("ID");
        currentUser = getIntent().getStringExtra("CURRENT_USER");
        name = getIntent().getStringExtra("PLACE_NAME");
        location = getIntent().getStringExtra("LOCATION");
        websiteURL = getIntent().getStringExtra("WEBSITE_URL");
        imageURL = getIntent().getStringExtra("IMAGE_URL");
        tags = getIntent().getStringExtra("TAGS");
        favorite = getIntent().getStringExtra("FAVORITE");

        // set values
        tvName.setText(name);
        tvLocation.setText("Location: "+location);
        tvTags.setText(tags);
        Glide.with(this).load(imageURL).into(ivImage);

    }

    // redirect user to food place's official website
    public void clickWebsite(View view) {

        if(!websiteURL.isEmpty()) {
            Uri uri = Uri.parse(websiteURL);
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(i);
        } else {
            Toast.makeText(FoodPlaceView.this, "No website available.", Toast.LENGTH_SHORT).show();
        }
    }


    // when user adds a place to their list of favorites
    public void clickFavorite(View view) {

        // use DatabaseReference object to access our Firebase Realtime database
        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://brockp2e-places-default-rtdb.firebaseio.com/").child("users");
        DatabaseReference dbReferenceFp = FirebaseDatabase.getInstance().getReferenceFromUrl("https://brockp2e-places-default-rtdb.firebaseio.com/").child("foodplaces");

        dbReference.child(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {

            // Update user's favorites list in database
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<String> currentFavorites;

                // get all current favorites
                String currentFavString = snapshot.child("favorites").getValue(String.class);


                // guest users can't have saved favorites
                if (currentUser.equals("guest")) {
                    Toast.makeText(FoodPlaceView.this, "Sign in to add this to your favorites", Toast.LENGTH_SHORT).show();
                }
                else {

                    // if list of favorites is not empty
                    if (!(currentFavString.equals(""))) {

                        // make arraylist of foodplace ids
                        currentFavorites = Arrays.asList(currentFavString.split(" "));
                        ArrayList<String> currentFavsAL = new ArrayList<>(currentFavorites);

                        // if the selected foodplace is already in the list of favorites
                        if (currentFavorites.contains(String.valueOf(id).trim())) {

                            // remove place from user's favorites list
                            int index = currentFavsAL.indexOf(String.valueOf(id).trim());

                            currentFavsAL.remove(index);
                            String newFavorites="";
                            String leadingZeros = "^\\s+";

                            for(String fpID : currentFavsAL){
                                //concatenate each place id to new favorites string with " " delimiter
                                String temp = newFavorites + " " + fpID;
                                String temp2 = temp.replaceAll(leadingZeros,"");
                                newFavorites = " " +temp2;

                            }
                            dbReference.child(currentUser).child("favorites").setValue(newFavorites);
                            Toast.makeText(FoodPlaceView.this, "Removed from favorites", Toast.LENGTH_SHORT).show();

                        }
                        // add the foodplace to the list of favorites
                        else {

                            String leadingZeros = "^\\s+";
                            String temp = currentFavString + " " + id;
                            String temp2 = temp.replaceAll(leadingZeros,"");
                            String updatedFavourites = " "+temp2;

                            dbReference.child(currentUser).child("favorites").setValue(updatedFavourites);
                            Toast.makeText(FoodPlaceView.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                        }

                    }
                    // if adding the first place to favorites
                    else {

                        String addFirstFav = " " +id;
                        dbReference.child(currentUser).child("favorites").setValue(addFirstFav);
                        Toast.makeText(FoodPlaceView.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}