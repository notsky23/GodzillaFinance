package com.example.godzillafinance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.graphics.Color;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

public class ProfileActivity extends AppCompatActivity implements ExampleDialog.ExampleDialogListener{

    private ImageView LogOut_Btn;
    private TextView FullName_Et;
    private TextView Age_Et;
    private TextView Email_Et;
    private FirebaseAuth mAuth;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userId;
    private Button Menu_btn;

    private Button click;
    private PieChart chart;
    public static int i1 = 15;
    public static int i2 = 25;
    public static int i3 = 35;

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FullName_Et = findViewById(R.id.FullName);
        Age_Et = findViewById(R.id.age);
        Email_Et = findViewById(R.id.EmailAddress);

        LogOut_Btn = findViewById(R.id.LogOut);
        mAuth = FirebaseAuth.getInstance();

        click = findViewById(R.id.btn_click);
        chart = findViewById(R.id.pie_chart);

        button = (Button) findViewById(R.id.Enter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToPieChart();
            }
        });


        Menu_btn = findViewById(R.id.Menu);

        Menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotToActivity_Menu();
            }
        });

        LogOut_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                gotoActivity_Logout();
            }
        });

        // Showing user information.
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userId = user.getUid();

        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserCreated userProfile = snapshot.getValue(UserCreated.class);
                if(userProfile != null){
                    String FullName = userProfile.fullName;
                    String age = userProfile.age;
                    String email = userProfile.email;

                    // Setting the user information on the userprofile.
                    FullName_Et.setText(FullName);
                    Age_Et.setText(age);
                    Email_Et.setText(email);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this,"Unable to fetch User Data.",Toast.LENGTH_LONG).show();
            }
        });
    }
    public void gotoActivity_Logout(){
        Intent i = new Intent(ProfileActivity.this,LoginActivity.class);
        startActivity(i);

    }

    public void gotToActivity_Menu(){
        Intent intent = new Intent(ProfileActivity.this,MenuActivity.class);
        startActivity(intent);
    }
    private void addToPieChart() {
        // add to pie chart

        chart.addPieSlice(new PieModel("Spendings", i2, Color.parseColor("#ff0000")));
        chart.addPieSlice(new PieModel("Earnings", i1, Color.parseColor("#FFFF00")));
        chart.addPieSlice(new PieModel("Savings", i3, Color.parseColor("#00FF00")));
        chart.startAnimation();
        click.setClickable(false);
    }

    public void openDialog() {
        ExampleDialog exampleDialog = new ExampleDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    public void applyTexts(String username, String password, String savings) {
//        textViewUsername.setText(username);
//        textViewPassword.setText(password);
        i1 = Integer.valueOf(password);
        i2 = Integer.valueOf(username);
        i3 = Integer.valueOf(savings);
    }
}