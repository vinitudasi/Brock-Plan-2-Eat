package com.example.foodplaceviewer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
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

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


// Initialize buttons and links
        final Button registerButton = findViewById(R.id.registerBtn);
        final TextView backToSignInBtn = findViewById(R.id.backToSignInBtn);

        // Initialize variables with new user's details for account registration
        final EditText username = findViewById(R.id.usernameInput);
        final EditText password = findViewById(R.id.passwordInput);
        final EditText fullName = findViewById(R.id.fullNameInput);
        final EditText email = findViewById(R.id.emailInput);
        final EditText confirmPassword = findViewById(R.id.confirmPasswordInput);

        // OnClickListeners
        backToSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // regex for email format
                final String emailFormat = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                //final String emailFormat = "^(.+)@(.+)$";

                // store new user account details in strings
                final String nameText = fullName.getText().toString().trim();
                final String usernameText = username.getText().toString().trim();
                final String passwordText = password.getText().toString();
                final String confirmPasswordText = confirmPassword.getText().toString();
                final String emailText = email.getText().toString().trim();

                // use DatabaseReference object to access our Firebase Realtime database
                DatabaseReference dbReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://brockp2e-places-default-rtdb.firebaseio.com/");

                // check if user enters invalid email format and notify if false
                if(!emailText.matches(emailFormat)){
                    Toast.makeText(Register.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                }

                // check if password matches required format
                else if(!validatePassword(passwordText)){
                    String passwordConditions = "Password must contain: \nAtleast one uppercase letter\nAtleast " +
                            "one lowercase letter\nAtleast one number\nand be atleast 8 digits long";
                    Toast.makeText(Register.this, passwordConditions, Toast.LENGTH_LONG).show();
                }

                // check if any fields are empty. If so, let user know
                else if(nameText.isEmpty() || usernameText.isEmpty() || passwordText.isEmpty() || confirmPasswordText.isEmpty() || emailText.isEmpty()){
                    Toast.makeText(Register.this, "Please fill out all required fields", Toast.LENGTH_SHORT).show();
                }
                // check if password and confirmPassword are not matching
                else if(!passwordText.equals(confirmPasswordText)){
                    Toast.makeText(Register.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
                // user details are valid, so insert new user data to firebase with username as primary key
                else{

                    dbReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // check if username already exists with a registered account
                            if(snapshot.hasChild(usernameText)){
                                Toast.makeText(Register.this, "Username already exists", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String hashedPassword = getHashedPW(passwordText);
                                // parent: username, name of column: users
                                dbReference.child("users").child(usernameText).child("email").setValue(emailText);
                                dbReference.child("users").child(usernameText).child("password").setValue(hashedPassword);
                                dbReference.child("users").child(usernameText).child("fullName").setValue(nameText);
                                dbReference.child("users").child(usernameText).child("favorites").setValue("");

                                // message to confirm user registration
                                Toast.makeText(Register.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                                finish();   //end activity after successful registration
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
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