package com.example.foodplaceviewer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Get user SignIn credentials
        final EditText password = findViewById(R.id.password);
        final EditText username = findViewById(R.id.username);

        // Get SignIn, Register and Guest opions
        final TextView registerButton = findViewById(R.id.registerButton);
        final Button signInButton = findViewById(R.id.signInButton);
        final Button guestButton = findViewById(R.id.guestButton);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);   // disables the keyboard from popping up on launch of activity


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get username and password as strings
                final String usernameString = username.getText().toString().trim();
                final String passwordString = password.getText().toString();

                // use DatabaseReference object to access our Firebase Realtime database
                DatabaseReference dbReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://brockp2e-places-default-rtdb.firebaseio.com/");

                // Check if user signs in without either password or username
                if (passwordString.isEmpty() || usernameString.isEmpty())
                {
                    Toast.makeText(SignIn.this, "Please enter both username and password to sign in", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    dbReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            // see if a user with same username already exists in the database
                            if(snapshot.hasChild(usernameString)){

                                // username exists in database, check if user entered correct password
//                                String existingPassword = snapshot.child(usernameString).child("password").getValue(String.class);
                                String inputPassword = getHashedPW(passwordString);
                                String existingPassword = snapshot.child(usernameString).child("password").getValue(String.class);
                                if(existingPassword.equals(inputPassword)){
                                    // if passwords match, tell user they have signed in successfully and send them to the main activity
                                    Toast.makeText(SignIn.this, "Signed In as "+ usernameString, Toast.LENGTH_SHORT).show();

                                    Intent afterSignIn = new Intent(SignIn.this, FoodPlaceList.class);
                                    afterSignIn.putExtra("isGuest", "false");
                                    afterSignIn.putExtra("CURRENT_USER", usernameString);
                                    startActivity(afterSignIn);
                                    finish(); // end this activity
                                }
                                else{
                                    // if passwords don't match, display incorrect credentials message
                                    Toast.makeText(SignIn.this, "Incorrect username or password. Please review your credentials and try again", Toast.LENGTH_LONG).show();

                                }
                            }
                            else{
                                // if username doesn't exist in database, display incorrect credentials message
                                Toast.makeText(SignIn.this, "Incorrect username or password. Please review your credentials and try again", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }


            }
        });

        // OnClickListener for sending user to registration
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(SignIn.this, Register.class));   // TRY THIS IF SEPARATE DECLARATION DOESN'T WORK
                Intent intent = new Intent(SignIn.this, Register.class);
                startActivity(intent);
            }
        });

        // OnClickListener for continuing as guest
        guestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SignIn.this, FoodPlaceList.class);
                intent.putExtra("isGuest", "true");
                intent.putExtra("CURRENT_USER", "guest");
                startActivity(intent);
            }
        });


    }
    // helper function for hashing strings for encrypting user passwords
    private String getHashedPW(String unhashedPW){
        StringBuilder hashedPW = new StringBuilder();

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(unhashedPW.getBytes());

            byte[] pwToBytes = md.digest(); // convert password string to byte array

            for (byte b: pwToBytes){
                hashedPW.append(String.format("%02x", b));
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashedPW.toString();
    }

    // helper function for validating whether password matches the required format
    private Boolean validatePassword(String password){

        //String passwordConditions = "Password must contain: \nAtleast one uppercase letter\nAtleast " + "one lowercase letter\nAtleast one number\nand atleast 8 digits long";
        boolean validPassword = true;

        // regexes for each required condition
        String checkUpperCase = "(.*[A-Z].*)";
        String checkLowerCase = "(.*[a-z].*)";
        String containsNum = "(.*[0-9].*)";

        if(password.length() < 8||!password.matches(checkUpperCase)||!password.matches(checkLowerCase)||!password.matches(containsNum)){
            //Toast.makeText(Register.this, passwordConditions, Toast.LENGTH_SHORT).show();
            validPassword = false;
        }

        /*if(password.length() < 8){

        }

        if(!password.matches(checkUpperCase)){

            Toast.makeText(Register.this, passwordConditions, Toast.LENGTH_SHORT).show();
            validPassword = false;
        }
        if(!password.matches(checkLowerCase)){
            validPassword = false;
        }
        if(!password.matches(containsNum)){
            validPassword = false;
        }*/
        return validPassword;
    }
}