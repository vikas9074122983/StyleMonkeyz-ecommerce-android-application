package org.ganeshatech05102021.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MOrderPage extends AppCompatActivity {
    ImageView img;
    TextView name,des,price,add,date,time,status,paytype,payid,oquan;
    LinearLayout paylay;
    Button cancel,returnbtn;
    String productid;
    ProgressDialog progressDialog;
    String clientName;
    private long priority = -1 * new Date().getTime();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_morder_page);

        img = findViewById(R.id.img_orde);
        name = findViewById(R.id.pponame);
        des = findViewById(R.id.podescri);
        add = findViewById(R.id.oaddr);
        date = findViewById(R.id.odate);
        time = findViewById(R.id.otime);
        status = findViewById(R.id.ostatus);
        paytype = findViewById(R.id.opaytype);
        payid = findViewById(R.id.opayid);
        paylay = findViewById(R.id.payidll);
        cancel = findViewById(R.id.cancel_btn);
        returnbtn = findViewById(R.id.return_order);
        price = findViewById(R.id.ppoprice);
        oquan = findViewById(R.id.oquan);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait ");

        productid = getIntent().getStringExtra("id");
        Log.d("TAG", "onCreate: "+productid);
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("my product").document(productid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){
                    String imag = task.getResult().getString("img");
                    String nam = task.getResult().getString("name");
                    String de = task.getResult().getString("description");
                    String ad = task.getResult().getString("address");
                    String da = task.getResult().getString("date");
                    String ti = task.getResult().getString("time");
                    String st = task.getResult().getString("status");
                    String pty = task.getResult().getString("payment");
                    String pid = task.getResult().getString("payment_id");
                    long pri = task.getResult().getLong("price");
                    long qua = task.getResult().getLong("quantity");

                    if (imag != null){
                        Picasso.get().load(imag).into(img);
                    }
                    name.setText(nam);
                    des.setText(de);
                    add.setText(ad);
                    date.setText(da);
                    time.setText(ti);
                    status.setText(st);
                    paytype.setText(pty);
                    if (pid == null){
                        paylay.setVisibility(View.GONE);
                    }else {
                        payid.setText(pid);
                    }
                    price.setText(String.valueOf(pri));
                    oquan.setText(String.valueOf(qua));
                    if (st.equals("delivered")){
                        cancel.setVisibility(View.GONE);
                    }
                    if ((st.equals("return declined")) || (st.equals("return completed")) || (st.equals("return requested"))){
                        cancel.setVisibility(View.GONE);
                        returnbtn.setVisibility(View.GONE);
                    }
                    if (st.equals("ordered")){
                        returnbtn.setVisibility(View.GONE);
                    }

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(MOrderPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        DocumentReference drefff = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        drefff.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){
                    clientName = task.getResult().getString("name");
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "onClick: "+clientName);

                AlertDialog.Builder builder = new AlertDialog.Builder(MOrderPage.this);
                builder.setTitle("Cancel Order");
                builder.setMessage("Do you want to cancel this order ? ");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        progressDialog.show();
                        DocumentReference dref = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .collection("my product").document(productid);
                        Log.d("TAG", "onCreate: "+productid);
                        dref.update("status","cancelled").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                if (progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                                Toast.makeText(MOrderPage.this, "Order Cancelled..", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MOrderPage.this,MainActivity.class));
                                finish();
                            }
                        });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                builder.show();
            }
        });
        returnbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "onClick: "+clientName);

                AlertDialog.Builder builder = new AlertDialog.Builder(MOrderPage.this);
                builder.setTitle("Return Order");
                builder.setMessage("Do you want to return this order ? ");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        progressDialog.show();
                        DocumentReference dref = FirebaseFirestore.getInstance().collection("return").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        Map<String,Object> codmap = new HashMap<>();
                        codmap.put("name",clientName);
                        codmap.put("id",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        codmap.put("status","return");
                        codmap.put("priority",priority);
                        dref.set(codmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                DocumentReference dref = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .collection("my product").document(productid);
                                Log.d("TAG", "onCreate: "+productid);
                                dref.update("status","return requested");
                                if (progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                                Toast.makeText(MOrderPage.this, "Return Requested..", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MOrderPage.this,MainActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Toast.makeText(MOrderPage.this, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                if (progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                builder.show();
            }
        });
    }

}