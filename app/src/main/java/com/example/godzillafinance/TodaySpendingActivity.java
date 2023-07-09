package com.example.godzillafinance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class TodaySpendingActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private FloatingActionButton fb;
    private RecyclerView recyclerView3;
    private TextView TodaySpending_Tv;
    private DatabaseReference ExpenseRef;

    private FirebaseAuth mAuth;
    private ProgressDialog loader;
    private String user_id="";

    private TodayItemAdapter todayItemAdapter;
    private List<Data_Added> myDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_spending);

        TodaySpending_Tv = findViewById(R.id.TodaySpending_Tv);
        progressBar = findViewById(R.id.progressBar2);
        fb = findViewById(R.id.floatingActionButton22);
        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        ExpenseRef = FirebaseDatabase.getInstance()
                .getReference().child("expenses").child(user_id);

        // Adding items in the firebase database with json structure of budget then child is the uuid of the
        // current user.
        recyclerView3 = findViewById(R.id.RecyclerView3);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView3.setHasFixedSize(true);
        recyclerView3.setLayoutManager(linearLayoutManager);

        loader = new ProgressDialog(this);

        myDataList = new ArrayList<>();
        todayItemAdapter = new TodayItemAdapter(TodaySpendingActivity.this,myDataList);
        recyclerView3.setAdapter(todayItemAdapter);
        readItems();

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Insert_Item();
            }
        });
    }

    private void readItems() {
        DateFormat df = new SimpleDateFormat("dd-mm-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = df.format(cal.getTime());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(user_id);
        Query query = reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataList.clear();
                for(DataSnapshot snap:snapshot.getChildren()){
                    Data_Added data = snap.getValue(Data_Added.class);
                    myDataList.add(data);
                }
                todayItemAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                int total_amt = 0;
                for(DataSnapshot snap: snapshot.getChildren()){
                    Map<String,Object> map = (Map<String, Object>)snap.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    total_amt += pTotal;
                    TodaySpending_Tv.setText("Today's Spending is:- "+total_amt);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void Insert_Item() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.activity_input_layout, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        Spinner sp = myView.findViewById(R.id.spinner_items);
        EditText amount_et = myView.findViewById(R.id.Amount_Et_Input);
        Button Save_Btn = myView.findViewById(R.id.Save_button);
        Button Cancel_Btn = myView.findViewById(R.id.Cancel_button);

        Save_Btn.setOnClickListener(new View.OnClickListener() {

            // Adding data in the form of an object to firebase Database.

            @Override
            public void onClick(View view) {

                // Adding the items to the firebase database
                String Budget_Item;
                if (amount_et.getText().toString().isEmpty()) {
                    amount_et.setError("Amount cannot be empty");
                    return;
                }

                String Budget_Amount = amount_et.getText().toString();

                if (sp.getSelectedItem().toString() == "Select item") {
                    Toast.makeText(TodaySpendingActivity.this, "Item needs to be selected", Toast.LENGTH_LONG).show();
                    Budget_Item = "Other";
                } else {
                    loader.setMessage("Inserting an item in budget");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    Budget_Item = sp.getSelectedItem().toString();
                    // Adding all the json values of an item in the firebase database.

                    String id = ExpenseRef.push().getKey();
                    // Adding the date
                    DateFormat df = new SimpleDateFormat("dd-mm-yyyy");
                    Calendar cal = Calendar.getInstance();
                    String date = df.format(cal.getTime());

                    MutableDateTime epoch = new MutableDateTime();
                    // epoch to set at start of january 1990 and calculate accordingly
                    epoch.setDate(0);
                    DateTime now = new DateTime();
                    Months month = Months.monthsBetween(epoch, now);
                    Data_Added data_added = new Data_Added(id, Budget_Item,
                            date, month.getMonths(), Integer.parseInt(Budget_Amount));
                    // Add the data of type datacreated using ExpenseRef using firebasedatabase.
                    Log.d("Ameya2", "onClick: Ameya" + Budget_Amount + Budget_Item);

                    ExpenseRef.child(id).setValue(data_added).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(TodaySpendingActivity.this, "Item added.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(TodaySpendingActivity.this, "Item Failed to add to database.", Toast.LENGTH_LONG).show();
                            }
                            loader.dismiss();
                        }
                    });
                }

                dialog.dismiss();
            }
        });

        Cancel_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cancelling the dialog box.
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}