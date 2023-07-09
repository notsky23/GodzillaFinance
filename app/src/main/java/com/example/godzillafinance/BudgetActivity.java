package com.example.godzillafinance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BudgetActivity extends AppCompatActivity {

    private FloatingActionButton fb;
    private DatabaseReference budgetRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loader;
    private RecyclerView recyclerView;
    private TextView Budget_Tv_;

    private String post_key = "";
    private String item = "";
    private int amount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);
        mAuth = FirebaseAuth.getInstance();
        // Adding items in the firebase database with json structure of budget then child is the uuid of the
        // current user.
        budgetRef = FirebaseDatabase.getInstance()
                .getReference().child("budget").child(mAuth.getCurrentUser().getUid());
        loader = new ProgressDialog(this);
        fb = findViewById(R.id.floatingActionButton);

        // Displaying the items in the recycler view.
        recyclerView = findViewById(R.id.RecyclerView);
        Budget_Tv_ = findViewById(R.id.Budget_Tv_);
        // To display items in a recycler view you will need a linear layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        // Calculating the total value of the items and displaying them.
        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total = 0;
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Data_Added data = snapshot1.getValue(Data_Added.class);
                    total += data.getAmount();
                    Budget_Tv_.setText("Total spending is $:- "+String.valueOf(total));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity_insert();
            }
        });
    }
    public void gotoActivity_insert(){
        // Allowing user to input using floating action button
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.activity_input_layout,null);
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
                if(amount_et.getText().toString().isEmpty()){
                    amount_et.setError("Amount cannot be empty");
                    return;
                }

                String Budget_Amount = amount_et.getText().toString();

                if(sp.getSelectedItem().toString() == "Select item"){
                    Toast.makeText(BudgetActivity.this,"Item needs to be selected",Toast.LENGTH_LONG).show();
                    Budget_Item = "Other";
                }else{
                    loader.setMessage("Inserting an item in budget");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    Budget_Item = sp.getSelectedItem().toString();
                    // Adding all the json values of an item in the firebase database.

                    String id = budgetRef.push().getKey();
                    // Adding the date
                    DateFormat df = new SimpleDateFormat("dd-mm-yyyy");
                    Calendar cal = Calendar.getInstance();
                    String date = df.format(cal.getTime());

                    MutableDateTime epoch =new MutableDateTime();
                    // epoch to set at start of january 1990 and calculate accordingly
                    epoch.setDate(0);
                    DateTime now = new DateTime();
                    Months month = Months.monthsBetween(epoch,now);
                    Data_Added data_added = new Data_Added(id,Budget_Item,
                            date,month.getMonths(), Integer.parseInt(Budget_Amount));
                    // Add the data of type datacreated using budgetRef using firebasedatabase.
                    Log.d("Ameya2", "onClick: Ameya"+Budget_Amount+Budget_Item);

                    budgetRef.child(id).setValue(data_added).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(BudgetActivity.this,"Item added.",Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(BudgetActivity.this,"Item Failed to add to database.",Toast.LENGTH_LONG).show();
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

    // Displaying the fetched items in the recycler view.

    @Override
    protected void onStart() {
        super.onStart();
        // For recycler view you need to create a view holder,Builder,linearLayout and a view.

        FirebaseRecyclerOptions<Data_Added> options = new FirebaseRecyclerOptions.Builder<Data_Added>()
                .setQuery(budgetRef,Data_Added.class).build();
        // take the data then set it to the item values.
        // recycler Adapter pass the data and the viewHolder(has all the text views which need to be set to their repective values.
        FirebaseRecyclerAdapter<Data_Added,MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data_Added, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Data_Added model) {
                holder.setAmount_tv("Allocated amount: $ "+ model.getAmount());
                holder.setDate_tv("On: "+model.getDate());
                holder.setName_tv("BudgetItem: "+model.getItem_name());
                switch(model.getItem_name()) {
                    case "Transport":
                        holder.imageView.setImageResource(R.drawable.ic_transport);
                        break;
                    case "Food":
                        holder.imageView.setImageResource(R.drawable.ic_food);
                        break;
                    case "Education":
                        holder.imageView.setImageResource(R.drawable.ic_education);
                        break;
                    case "Medicine":
                        holder.imageView.setImageResource(R.drawable.ic_health);
                        break;

                    case "Shopping":
                        holder.imageView.setImageResource(R.drawable.ic_shirt);
                        break;

                    case "House":
                        holder.imageView.setImageResource(R.drawable.ic_house);
                        break;

                    case "Other":
                        holder.imageView.setImageResource(R.drawable.ic_other);
                        break;
                }
//                when any iteme is clicked in the holder
                holder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        post_key = getRef(position).getKey();
                        item = model.getItem_name();
                        amount = model.getAmount();
                        // Amount and item name are taken from the item clicked and
                        // then to update them we call update_item() function
                        update_item();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_layout,parent,false);
                return new MyViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        View myView;
        public ImageView imageView;
        public TextView Date_tv;
        public TextView amount_tv;
        public TextView Name_tv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize the view and the items in the view.
            // use setters and getters for the same.
            myView = itemView;
            imageView = itemView.findViewById(R.id.img_tv) ;
            Date_tv = itemView.findViewById(R.id.Date_tv);
            amount_tv = itemView.findViewById(R.id.Amount_tv);
            Name_tv = itemView.findViewById(R.id.Name_tv);

        }

        public void setAmount_tv(String amt) {
            TextView amount_tv = itemView.findViewById(R.id.Amount_tv);
            amount_tv.setText(amt);
        }
        public void setDate_tv(String Date) {
            TextView Date_tv = itemView.findViewById(R.id.Date_tv);
            Date_tv.setText(Date);
        }
        public void setName_tv(String Name) {
            TextView Name_tv = itemView.findViewById(R.id.Name_tv);
            Name_tv.setText(Name);
        }
    }

    private void update_item(){
      // We need to create a alert dialog box to add or delete item amount
      AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
      LayoutInflater inflater = LayoutInflater.from(this);
      View mView = inflater.inflate(R.layout.update_resource,null);
      myDialog.setView(mView);
      final AlertDialog dialog = myDialog.create();
      final TextView mItem = mView.findViewById(R.id.TodaySpending);
      final EditText mAmount = mView.findViewById(R.id.EnterAmount);
      final Button Save_btn = mView.findViewById(R.id.Update_Btn_1);
      final Button Delete_btn = mView.findViewById(R.id.Cancel_button_1);

      mAmount.setText(String.valueOf(amount));
      mAmount.setSelection(String.valueOf(amount).length());
      // set selection to set the cursor to a particular position.

      Save_btn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              amount = Integer.parseInt(mAmount.getText().toString());
              // Adding the date
              DateFormat df = new SimpleDateFormat("dd-mm-yyyy");
              Calendar cal = Calendar.getInstance();
              String date = df.format(cal.getTime());

              MutableDateTime epoch =new MutableDateTime();
              // epoch to set at start of january 1990 and calculate accordingly
              epoch.setDate(0);
              DateTime now = new DateTime();
              Months month = Months.monthsBetween(epoch,now);
              Data_Added data_added = new Data_Added(post_key,item,
                      date,month.getMonths(), amount);
              // Add the data of type datacreated using budgetRef using firebasedatabase.

              budgetRef.child(post_key).setValue(data_added).addOnCompleteListener(new OnCompleteListener<Void>() {
                  @Override
                  public void onComplete(@NonNull Task<Void> task) {
                      if(task.isSuccessful()){
                          Toast.makeText(BudgetActivity.this,"Item updated.",Toast.LENGTH_LONG).show();
                      }
                      else{
                          Toast.makeText(BudgetActivity.this,"Item updation failed.",Toast.LENGTH_LONG).show();
                      }
                  }
              });
              dialog.dismiss();
          }
      });
      Delete_btn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              budgetRef.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                  @Override
                  public void onComplete(@NonNull Task<Void> task) {
                      if(task.isSuccessful()){
                          Toast.makeText(BudgetActivity.this,"Item Removed.",Toast.LENGTH_LONG).show();
                      }
                      else{
                          Toast.makeText(BudgetActivity.this,"Item removal failed.",Toast.LENGTH_LONG).show();
                      }
                  }
              });
              dialog.dismiss();
          }
      });

      dialog.show();
    }

}