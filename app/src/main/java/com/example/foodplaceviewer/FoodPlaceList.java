package com.example.foodplaceviewer;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class FoodPlaceList extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    RecyclerView recycleView;
    ArrayList<FoodPlace> list;
    DatabaseReference db, databaseRef;  // db->foodplaces, databaseRef->users
    Adapter adapter;
    Button tempBtn, filterBtn;

    private String retrieveIsGuest, currentUser, currentFavString;
    private int[] favoriteList;
    private int index = 0;
    //String retrieveIsGuest = getIntent().getStringExtra("isGuest");

    @Override
    public void onBackPressed() {

        // check if the current user is a guest. If yes, enable the back button, else disable it
        // this prevents the user from backing into the login screen after logging in
        String retrieveIsGuest = getIntent().getStringExtra("isGuest");
        if(retrieveIsGuest.equals("true")){

            super.onBackPressed(); // enable back button for current screen

            // send user back to Sign in page
            startActivity(new Intent(FoodPlaceList.this, SignIn.class));
            finish();
        }else{
            // do nothing (back button disabled)
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodplace_list);

        // initializing variables
        databaseRef = FirebaseDatabase.getInstance().getReference("users");
        String[] filterNames = getResources().getStringArray(R.array.filters);

        // temporary (spacing)
        tempBtn = findViewById(R.id.idTempButton);
        tempBtn.setVisibility(View.INVISIBLE);

        // obtain bundle information
        String filter = getIntent().getStringExtra("FILTER");
        int position = getIntent().getIntExtra("POSITION",0);

        filterBtn = findViewById(R.id.idFilter);
        filterBtn.setText(filterNames[position]);

        // setting up recycleviewer
        recycleView = findViewById(R.id.idRecyclerView);
        list = new ArrayList<>();
        db = FirebaseDatabase.getInstance().getReference("foodplaces");
        recycleView.setLayoutManager(new LinearLayoutManager(this));

        currentUser = getIntent().getStringExtra("CURRENT_USER");
        adapter = new Adapter(this, list, currentUser);
        recycleView.setAdapter(adapter);

        // reading from database for retrieving favorites
        // returns null for guests
        databaseRef.child(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // dataSnapshot keeps updated per loading of an activity
                if(retrieveIsGuest.equals("false")) {
                    currentFavString = numSort(dataSnapshot.child("favorites").getValue(String.class));
//                    System.out.println("======"+"currentFavString: "+currentFavString+"======");
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        // add data
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    FoodPlace fp = ds.getValue(FoodPlace.class);

                    if(position != 1) { // not position 1
                        if(filter == null || filter.equals("")) {   // Show all
                            list.add(fp);
                        }
                        else {    // All other locations
                            if(fp.getLocation().equals(filter)) {
                                list.add(fp);
                            }
                        }
                    }
                    else {    // position is 1 --> showing favorites
                        String temp = getIntent().getStringExtra("CURRENT_FAV_STRING");
                        if(temp != null) {
                            favoriteList = stringToInt(temp);

                            // example: length is 4 for "Jero"
                            if((int)fp.getId() == favoriteList[index]) {
                                list.add(fp);
                                if(index+1 != favoriteList.length) index++;
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged(); // important; keep
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    // display message for action bar option
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        retrieveIsGuest = getIntent().getStringExtra("isGuest");

        // display "sign out" or "sign in" on action bar depending on whether user is signed in
        if(retrieveIsGuest.equals("true")){
            getMenuInflater().inflate(R.menu.signin_menu, menu);
        }else{
            getMenuInflater().inflate(R.menu.signout_menu, menu);
        }
        return true;
    }

    @Override   // Functionality for Action Bar button
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // if user is logged in, make menu option sign out
        if(item.getItemId()==R.id.signout) {
            Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(FoodPlaceList.this, SignIn.class));
            finish();

            // if user is logged out, make menu option sign in
        }else if(item.getItemId()==R.id.signin){
            startActivity(new Intent(FoodPlaceList.this, SignIn.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void btnFilter(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        Intent i = new Intent(this, FoodPlaceList.class);
        i.putExtra("isGuest", retrieveIsGuest);
        i.putExtra("CURRENT_USER", currentUser);
        i.putExtra("CURRENT_FAV_STRING", currentFavString);

        switch(menuItem.getItemId()) {
            case R.id.item0:    // show all
                i.putExtra("POSITION", 0);
                this.startActivity(i);
                break;
            case R.id.item1:    // favorites
                i.putExtra("POSITION", 1);
                this.startActivity(i);
                break;
            case R.id.item2:    // market
                i.putExtra("POSITION", 2);
                i.putExtra("FILTER", "Market");
                this.startActivity(i);
                break;
            case R.id.item3:    // hungry badger
                i.putExtra("POSITION", 3);
                i.putExtra("FILTER", "Hungry Badger");
                this.startActivity(i);
                break;
            case R.id.item4:    // union station
                i.putExtra("POSITION", 4);
                i.putExtra("FILTER", "Union Station");
                this.startActivity(i);
                break;
            case R.id.item5:    // library
                i.putExtra("POSITION", 5);
                i.putExtra("FILTER", "Library");
                this.startActivity(i);
                break;
            case R.id.item6:    // welch hall
                i.putExtra("POSITION", 6);
                i.putExtra("FILTER", "Welch Hall");
                this.startActivity(i);
                break;
            case R.id.item7:    // schmon tower
                i.putExtra("POSITION", 7);
                i.putExtra("FILTER", "Schmon Tower");
                this.startActivity(i);
                break;
        }
        return false;
    }
    public void callBot(View view){
        startActivity(new Intent(this,BotActivity.class));
    }

    // sorts the given unsorted string of favorites, then returns them as a string (that is sorted)
    public String numSort(String arr) {
        arr = arr.trim(); // removes the space in the beginning
        String[] temp = arr.split(" ");

        int[] numbers = new int[temp.length];

        for (int i = 0; i < temp.length; i++)
            numbers[i] = Integer.parseInt(temp[i]);

        Arrays.sort(numbers);

        // converting integer array to string which doesnt include the square brackets
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numbers.length; i++) {
            sb.append(numbers[i]);
            if (i < numbers.length-1) {
                sb.append(" ");
            }
        }

        String result = sb.toString();

//        Toast.makeText(this, "result: \""+result+"\"", Toast.LENGTH_SHORT).show();

        return result;
    }

    // simply converts a string into an integer array
    public int[] stringToInt(String arr) {
        arr = arr.trim();
        String[] temp = arr.split(" ");

        int[] numbers = new int[temp.length];

        for (int i = 0; i < temp.length; i++)
            numbers[i] = Integer.parseInt(temp[i]);

        return numbers;
    }
}