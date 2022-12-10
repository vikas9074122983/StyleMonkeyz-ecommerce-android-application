package org.ganeshatech05102021.ecommerce.adminactions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.ganeshatech05102021.ecommerce.R;
import org.ganeshatech05102021.ecommerce.model.orderModel;
import org.jetbrains.annotations.NotNull;

public class returnRequestAction extends AppCompatActivity {
    String id;
    RecyclerView ordersRV;
    FirestoreRecyclerAdapter orderadapter;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_request_action);
        id = getIntent().getStringExtra("id");
        Log.d("TAG", "onCreate: "+id);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait ....");

        ordersRV = findViewById(R.id.readorder_RV);

        Query query = FirebaseFirestore.getInstance().collection("users").document(id)
                .collection("my product")
                .orderBy("priority",Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<orderModel> option = new FirestoreRecyclerOptions.Builder<orderModel>()
                .setQuery(query,orderModel.class)
                .build();
        orderadapter = new FirestoreRecyclerAdapter<orderModel, reaViewHolder>(option){
            @NonNull
            @NotNull
            @Override
            public reaViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View vie = LayoutInflater.from(parent.getContext()).inflate(R.layout.receive_order_card,parent,false);
                return new reaViewHolder(vie);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull reaViewHolder holder, int position, @NonNull @NotNull orderModel model) {
                Picasso.get().load(model.getImg()).into(holder.img);
                holder.name.setText(model.getName());
                holder.des.setText(model.getDescription());
                holder.qua.setText(String.valueOf(model.getQuantity()));
                holder.price.setText(String.valueOf(model.getPrice()));
                holder.status.setText(model.getStatus());
                holder.address.setText(model.getAddress());
                holder.date.setText(model.getDate());
                holder.time.setText(model.getTime());
                holder.ptype.setText(model.getPayment());
                holder.payid.setText(model.getPayment_id());
                if (model.getStatus().equals("return requested")){
                    holder.acceptReturn.setVisibility(View.VISIBLE);
                    holder.declineReturn.setVisibility(View.VISIBLE);
                }
                if (model.getStatus().equals("return accepted")){
                    holder.returnCompleted.setVisibility(View.VISIBLE);
                    holder.acceptReturn.setVisibility(View.GONE);
                    holder.declineReturn.setVisibility(View.GONE);
                }
                DocumentReference dref = FirebaseFirestore.getInstance().collection("users").document(id)
                        .collection("my product").document(model.getPid());
                holder.acceptReturn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressDialog.show();
                        dref.update("status","return accepted").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                progressDialog.dismiss();
                                Toast.makeText(returnRequestAction.this, "Status changed to return accepted", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(returnRequestAction.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                holder.declineReturn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressDialog.show();
                        dref.update("status","return declined").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                DocumentReference dreff = FirebaseFirestore.getInstance().collection("return").document(id);
                                dreff.update("status","returned").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                        Toast.makeText(returnRequestAction.this, "Status changed to return declined", Toast.LENGTH_SHORT).show();
                                        CollectionReference cref = FirebaseFirestore.getInstance().collection("users").document(id)
                                                .collection("my product");
                                        cref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()){
                                                    for (QueryDocumentSnapshot document : task.getResult()){
                                                        String temp = document.getString("status");
                                                        if ((temp.equals("return accepted")) || (temp.equals("return requested"))){
                                                            dreff.update("status","return");
                                                            Intent intent = new Intent(returnRequestAction.this,returnRequestAction.class);
                                                            intent.putExtra("id",id);
                                                            startActivity(intent);
                                                            finish();
                                                        }else {
                                                            Log.d("TAG", "onComplete: ");
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(returnRequestAction.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(returnRequestAction.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                holder.returnCompleted.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressDialog.show();
                        dref.update("status","return completed").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                DocumentReference dreff = FirebaseFirestore.getInstance().collection("return").document(id);
                                dreff.update("status","returned").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                        Toast.makeText(returnRequestAction.this, "Status changed to return completed", Toast.LENGTH_SHORT).show();
                                        CollectionReference cref = FirebaseFirestore.getInstance().collection("users").document(id)
                                                .collection("my product");
                                        cref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()){
                                                    for (QueryDocumentSnapshot document : task.getResult()){
                                                        String temp = document.getString("status");
                                                        if ((temp.equals("return accepted")) || (temp.equals("return requested"))){
                                                            dreff.update("status","return");
                                                            Intent intent = new Intent(returnRequestAction.this,returnRequestAction.class);
                                                            intent.putExtra("id",id);
                                                            startActivity(intent);
                                                            finish();
                                                        }else {
                                                            Log.d("TAG", "onComplete: ");
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(returnRequestAction.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(returnRequestAction.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });


            }
        };
        ordersRV.setAdapter(orderadapter);
        ordersRV.setLayoutManager(new LinearLayoutManager(this));
        orderadapter.startListening();
        orderadapter.notifyDataSetChanged();
    }

    private class reaViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name,des,qua,price,status,address,date,time,ptype,payid;
        Button acceptReturn,declineReturn,returnCompleted;
        public reaViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.roorder_img);
            name = itemView.findViewById(R.id.roorder_name_txt);
            des = itemView.findViewById(R.id.roorder_desc_txt);
            qua = itemView.findViewById(R.id.roquantity);
            price = itemView.findViewById(R.id.roorder_price);
            status = itemView.findViewById(R.id.roorder_status);
            address = itemView.findViewById(R.id.roaddr);
            date = itemView.findViewById(R.id.rodate);
            time = itemView.findViewById(R.id.rotime);
            ptype = itemView.findViewById(R.id.ropaytype);
            payid = itemView.findViewById(R.id.ropayid);
            acceptReturn = itemView.findViewById(R.id.return_rec_accept);
            declineReturn = itemView.findViewById(R.id.return_rec_decline);
            returnCompleted = itemView.findViewById(R.id.return_rec_completed);
        }
    }
}