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
import org.ganeshatech05102021.ecommerce.model.adminSerchModel;
import org.ganeshatech05102021.ecommerce.model.orderModel;
import org.jetbrains.annotations.NotNull;

public class receivedOrderAction extends AppCompatActivity {

    String id;
    RecyclerView ordersRV;
    TextView empty;
    FirestoreRecyclerAdapter orderadapter;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_order_action);

        id = getIntent().getStringExtra("id");
        Log.d("TAG", "onCreate: "+id);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait ....");

        ordersRV = findViewById(R.id.roreceivedorder_RV);
        empty = findViewById(R.id.roreceive_empty);

        Query query = FirebaseFirestore.getInstance().collection("users").document(id)
                .collection("my product")
                .orderBy("priority",Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<orderModel> option = new FirestoreRecyclerOptions.Builder<orderModel>()
                .setQuery(query,orderModel.class)
                .build();
        orderadapter = new FirestoreRecyclerAdapter<orderModel, ropViewHolder>(option){
            @NonNull
            @NotNull
            @Override
            public ropViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View vie = LayoutInflater.from(parent.getContext()).inflate(R.layout.receive_order_card,parent,false);
                return new ropViewHolder(vie);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull ropViewHolder holder, int position, @NonNull @NotNull orderModel model) {
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
                if (model.getStatus().equals("ordered")){
                    holder.shippedbtn.setVisibility(View.VISIBLE);
                }
                if (model.getStatus().equals("shipped")){
                    holder.delivered.setVisibility(View.VISIBLE);
                }
                DocumentReference dref = FirebaseFirestore.getInstance().collection("users").document(id)
                        .collection("my product").document(model.getPid());
                holder.shippedbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressDialog.show();
                        dref.update("status","shipped").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                progressDialog.dismiss();
                                Toast.makeText(receivedOrderAction.this, "Status changed to Shipped", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(receivedOrderAction.this,receivedOrderAction.class);
                                intent.putExtra("id",id);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(receivedOrderAction.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                holder.delivered.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressDialog.show();
                        dref.update("status","delivered").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                DocumentReference dreff = FirebaseFirestore.getInstance().collection("order").document(id);
                                dreff.update("status","delivered").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                        Toast.makeText(receivedOrderAction.this, "Status changed to Delivered", Toast.LENGTH_SHORT).show();
                                        CollectionReference cref = FirebaseFirestore.getInstance().collection("users").document(id)
                                                .collection("my product");
                                        cref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()){
                                                    for (QueryDocumentSnapshot document : task.getResult()){
                                                        String temp = document.getString("status");
                                                        if ((temp.equals("ordered")) || (temp.equals("shipped"))){
                                                            dreff.update("status","ordered");
                                                            Intent intent = new Intent(receivedOrderAction.this,receivedOrderAction.class);
                                                            intent.putExtra("id",id);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
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
                                        Toast.makeText(receivedOrderAction.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(receivedOrderAction.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (getItemCount() == 0){
                    empty.setVisibility(View.VISIBLE);
                    ordersRV.setVisibility(View.GONE);
                } else {
                    empty.setVisibility(View.GONE);
                    ordersRV.setVisibility(View.VISIBLE);
                }
            }
        };
        ordersRV.setAdapter(orderadapter);
        ordersRV.setLayoutManager(new LinearLayoutManager(this));
        orderadapter.startListening();
        orderadapter.notifyDataSetChanged();
    }

    private class ropViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name,des,qua,price,status,address,date,time,ptype,payid;
        Button shippedbtn,delivered;
        public ropViewHolder(@NonNull @NotNull View itemView) {
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
            shippedbtn = itemView.findViewById(R.id.shipped_order);
            delivered = itemView.findViewById(R.id.delivered_order);
        }
    }
}