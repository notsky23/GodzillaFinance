package com.example.godzillafinance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class TodayItemAdapter extends RecyclerView.Adapter<TodayItemAdapter.ViewHolder>{

    private String post_key = "";
    private String item = "";
    private int amount = 0;
    private String ameya = "Ameya";
    private Context mcontext;
    private List<Data_Added> myDataList;

    public TodayItemAdapter(Context mcontext, List<Data_Added> myDataList) {
        this.mcontext = mcontext;
        this.myDataList = myDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.retrieve_layout,parent,false);
        return new TodayItemAdapter.ViewHolder(view);
    }

    // For each Item in the recycler View.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final Data_Added data = myDataList.get(position);
            holder.item.setText("Item: "+data.getItem_name());
            holder.amount.setText("Amount: "+data.getAmount());
            holder.date.setText("Date: "+data.getDate());
        switch(data.getItem_name()) {
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
        holder.itemView.setOnClickListener((view )-> {
            post_key = data.getId();
            item = data.getItem_name();
            amount = data.getAmount();
            update_Item();
        });
    }

    private void update_Item() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(mcontext);
        LayoutInflater inflater = LayoutInflater.from(mcontext);
        View mView = inflater.inflate(R.layout.update_resource,null);
        myDialog.setView(mView);
        final AlertDialog dialog = myDialog.create();
        final TextView mItem = mView.findViewById(R.id.TodaySpending);
        final EditText mAmount = mView.findViewById(R.id.EnterAmount);
        final Button Save_btn = mView.findViewById(R.id.Update_Btn_1);
        final Button Delete_btn = mView.findViewById(R.id.Cancel_button_1);
        mItem.setText(item);
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
                DatabaseReference refernece = FirebaseDatabase.getInstance().getReference("expenses").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                refernece.child(post_key).setValue(data_added).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(mcontext,"Item updated.",Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(mcontext,"Item updation failed.",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });
        Delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference refernece = FirebaseDatabase.getInstance().getReference("expenses").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                refernece.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(mcontext,"Item Removed.",Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(mcontext,"Item removal failed.",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return myDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView item,amount,date;
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.Name_tv);
            amount = itemView.findViewById(R.id.Amount_tv);
            date = itemView.findViewById(R.id.Date_tv);
            imageView = itemView.findViewById(R.id.img_tv);

        }
    }
}
