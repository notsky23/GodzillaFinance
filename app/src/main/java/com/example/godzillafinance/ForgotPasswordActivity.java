package com.example.godzillafinance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText Reset_Et;
    private Button ResetEmailId_Btn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        progressBar = findViewById(R.id.ProgressBar);
        Reset_Et = findViewById(R.id.ResetEmail);
        ResetEmailId_Btn = findViewById(R.id.ResetEmailId);
        mAuth = FirebaseAuth.getInstance();

        ResetEmailId_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goto_ResetPassword();
            }
        });
    }
    public void goto_ResetPassword(){
        progressBar.setVisibility(View.VISIBLE);
        String Email_val = Reset_Et.getText().toString();

        if(Email_val.isEmpty()){
            Reset_Et.setError("Email cannot be empty.");
            Reset_Et.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(Email_val).matches()){
            Reset_Et.setError("Email not in proper format.");
            Reset_Et.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(Email_val).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Reset Email sent Successfully. check your email.", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
                else{
                    Toast.makeText(ForgotPasswordActivity.this,"Check if email exists",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}