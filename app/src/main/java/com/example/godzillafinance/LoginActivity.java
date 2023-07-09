package com.example.godzillafinance;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText Email_Et,Password_Et;
    private Button Login_Btn;
    private TextView SignUp_Tv;
    private TextView Forgot_Tv;
    private ProgressBar ProgressBar;
    private FirebaseAuth mAuth;
    private ImageView Call_Img_View;
    private ImageView Email_Img_View;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Email_Et = findViewById(R.id.EmailId);
        Password_Et = findViewById(R.id.Password);
        Login_Btn = findViewById(R.id.LoginButton);
        SignUp_Tv = findViewById(R.id.SignUpTextView);
        Forgot_Tv = findViewById(R.id.Forgot);
        ProgressBar = findViewById(R.id.ProgressBar);
        Call_Img_View = findViewById(R.id.Call_image_view);
        Email_Img_View = findViewById(R.id.Email_Image_View);
        mAuth = FirebaseAuth.getInstance();

        Call_Img_View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call_Img_Update();
            }
        });

        Email_Img_View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Email_Img_Update();
            }
        });
        Forgot_Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity_ForgotPassword();
            }
        });

        Login_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity_Login();
            }
        });
        SignUp_Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity_RegisterHere();
            }
        });
    }

    public void gotoActivity_Login() {
        String Password_val =  Password_Et.getText().toString().trim();
        String Email_val =  Email_Et.getText().toString().trim();

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

        mAuth.signInWithEmailAndPassword(Email_val,Password_val)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // User DashBoard
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if(user.isEmailVerified()){
                                gotoActivity_UserProfile();
                            }
                           else{
                               ProgressBar.setVisibility(View.GONE);
                               user.sendEmailVerification();
                                Toast.makeText(LoginActivity.this,"Check your email to verify your account.",Toast.LENGTH_LONG);
                            }
                        }
                        else{
                            Toast.makeText(LoginActivity.this,"Failed to login user",Toast.LENGTH_LONG);

                        }
                    }
                });

    }
    public void gotoActivity_UserProfile() {
        Intent i = new Intent(this,ProfileActivity.class);
        startActivity(i);
    }
    public void gotoActivity_RegisterHere() {
        Intent i = new Intent(this,RegistrationActivity.class);
        startActivity(i);
    }

    public void gotoActivity_ForgotPassword() {
        Intent i = new Intent(this,ForgotPasswordActivity.class);
        startActivity(i);
    }
   public void Call_Img_Update(){
       Uri number = Uri.parse("tel:8574379760");
       Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
       startActivity(callIntent);
   }
   public void Email_Img_Update(){
       String recepientList = "Team1@gmail.com";
       String [] recepients = recepientList.split(",");
       String subject = "Help";
       String message = "I have a query";

       Intent i = new Intent(Intent.ACTION_SEND);
       i.putExtra(i.EXTRA_EMAIL,recepients);
       i.putExtra(i.EXTRA_SUBJECT,subject);
       i.putExtra(i.EXTRA_TEXT,message);
       i.setType("message/rfc822");
       startActivity(i.createChooser(i,"choose an email client:- "));
   }
}