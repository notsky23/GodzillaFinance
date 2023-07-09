package com.example.godzillafinance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;

public class RegistrationActivity extends AppCompatActivity {

    private EditText FullName_Et;
    private EditText Password_Et;
    private EditText Email_Et;
    private EditText Age_Et;
    private Button SignUp_btn;
    private TextView LoginBack_btn;
    private ProgressBar ProgressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        FullName_Et = findViewById(R.id.FullName);
        Password_Et = findViewById(R.id.password);
        Email_Et = findViewById(R.id.Email);
        Age_Et = findViewById(R.id.Age);
        SignUp_btn = findViewById(R.id.SignUp);
        LoginBack_btn = findViewById(R.id.LoginBack);
        ProgressBar = findViewById(R.id.ProgressBar);


        mAuth = FirebaseAuth.getInstance();
        LoginBack_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotToActivity_LoginHere();
            }
        });

        SignUp_btn.setOnClickListener(new View.OnClickListener() {
            // getting the string values from the edit text.
            @Override
            public void onClick(View view) {
                String FullName_val =  FullName_Et.getText().toString().trim();
                String Password_val =  Password_Et.getText().toString().trim();
                String Email_val =  Email_Et.getText().toString().trim();
                String Age_val =  Age_Et.getText().toString().trim();

                if(FullName_val.isEmpty()){
                    FullName_Et.setError("Full Name cannot be empty.");
                    FullName_Et.requestFocus();
                    return;
                }

                if(Age_val.isEmpty()){
                    Age_Et.setError("Age cannot be empty.");
                    Age_Et.requestFocus();
                    return;
                }

                if(Email_val.isEmpty()){
                    Email_Et.setError("Email cannot be empty.");
                    Email_Et.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(Email_val).matches()){
                    Email_Et.setError("Email not in proper format.");
                    Email_Et.requestFocus();
                    return;
                }

                if(Password_val.isEmpty()){
                    Password_Et.setError("Password cannot be empty.");
                    Password_Et.requestFocus();
                    return;
                }
                if(Password_val.length() < 6){
                    Password_Et.setError("Length of the password cannot be less than 6.");
                    Password_Et.requestFocus();
                    return;
                }
                ProgressBar.setVisibility(View.VISIBLE);

                // Add the user to firebase database.
                mAuth.createUserWithEmailAndPassword(Email_val,Password_val)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    UserCreated user = new UserCreated(FullName_val,Age_val,Email_val);
                                    Log.d("Ameya", "onComplete: qopq");
                                    // Add the user into firebase now
                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                               // User added at a particular UUid value of the current user.
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(RegistrationActivity.this,"User Created Successfully check email to verify the email address.",Toast.LENGTH_LONG).show();
                                                        ProgressBar.setVisibility(View.GONE);
                                                        // User directed to login.
                                                        gotToActivity_LoginHere();
                                                    }
                                                    else{
                                                        Toast.makeText(RegistrationActivity.this,"Failed to create a User",Toast.LENGTH_LONG).show();
                                                        ProgressBar.setVisibility(View.GONE);
                                                    }

                                                }
                                            });
                                }
                                else{
                                    Toast.makeText(RegistrationActivity.this,"Failed to create a User",Toast.LENGTH_LONG).show();
                                    ProgressBar.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        });


    }

    public void gotToActivity_LoginHere(){
        // Already a user.
        Intent i = new Intent(RegistrationActivity.this,LoginActivity.class);
        startActivity(i);
    }
}