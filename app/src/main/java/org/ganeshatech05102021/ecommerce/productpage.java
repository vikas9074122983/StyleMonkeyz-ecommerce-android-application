package org.ganeshatech05102021.ecommerce;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import org.ganeshatech05102021.ecommerce.fragment.HomeFragment;
import org.ganeshatech05102021.ecommerce.model.productModel;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class productpage extends AppCompatActivity {
    String id,url1,url2,url3,url4,url5,url6,category;
    ImageView imgv1,imgv2,imgv3,imgv4,imgv5,imgv6;
    private TextView name,price,desc;
    private Button addtocart,addtofav;
    private RecyclerView recyclerView;
    FirestoreRecyclerAdapter adapter;
    ProgressDialog progressDialog;
    String currentTime, DateToday,timedate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productpage);

        id = getIntent().getStringExtra("id");
        Log.d("TAG", "onCreate: "+id);
        imgv1 = findViewById(R.id.imgv1);
        imgv2 = findViewById(R.id.imgv2);
        imgv3 = findViewById(R.id.imgv3);
        imgv4 = findViewById(R.id.imgv4);
        imgv5 = findViewById(R.id.imgv5);
        imgv6 = findViewById(R.id.imgv6);
        name  = findViewById(R.id.ppname);
        price = findViewById(R.id.ppprice);
        desc = findViewById(R.id.pdescri);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
        currentTime = mdformat.format(calendar.getTime());
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        DateToday = day + "-" + (month + 1) + "-" + year;
        timedate = currentTime+"-"+DateToday;



        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait ....");

        DocumentReference dref = FirebaseFirestore.getInstance().collection("product").document(id);
        dref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){
                    url1 = task.getResult().getString("img1");
                    url2 = task.getResult().getString("img2");
                    url3 = task.getResult().getString("img3");
                    url4 = task.getResult().getString("img4");
                    url5 = task.getResult().getString("img5");
                    url6 = task.getResult().getString("img6");
                    String pn = task.getResult().getString("name");
                    String pd = task.getResult().getString("description");
                    category = task.getResult().getString("category");
                    name.setText(pn);
                    long pr = task.getResult().getLong("price");
                    String prset = String.valueOf(pr)+" Rs.";
                    price.setText(prset);
                    desc.setText(pd);
                    if (url1 != null){
                        imgv1.setVisibility(View.VISIBLE);
                        Picasso.get().load(url1).into(imgv1);
                    }
                    if (url2 != null){
                        imgv2.setVisibility(View.VISIBLE);
                        Picasso.get().load(url2).into(imgv2);
                    }
                    if (url3 != null){
                        imgv3.setVisibility(View.VISIBLE);
                        Picasso.get().load(url3).into(imgv3);
                    }
                    if (url4 != null){
                        imgv4.setVisibility(View.VISIBLE);
                        Picasso.get().load(url4).into(imgv4);
                    }
                    if (url5 != null){
                        imgv5.setVisibility(View.VISIBLE);
                        Picasso.get().load(url5).into(imgv5);
                    }
                    if (url6 != null){
                        imgv6.setVisibility(View.VISIBLE);
                        Picasso.get().load(url6).into(imgv6);
                    }
                    if (category != null){
                        loadrecy();
                    }

                    DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .collection("my product").document(id+"-"+timedate);
                    addtocart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            progressDialog.show();
                            Map<String,Object> addcart = new HashMap<>();
                            addcart.put("name",pn);
                            addcart.put("description",pd);
                            addcart.put("price",pr);
                            addcart.put("id",id);
                            addcart.put("pid",id+"-"+timedate);
                            addcart.put("type","cart");
                            addcart.put("quantity",1);
                            if (url1 != null){
                                addcart.put("img",url1);
                            } else if (url2 != null){
                                addcart.put("img",url2);
                            } else if (url3 != null){
                                addcart.put("img",url3);
                            } else if (url4 != null){
                                addcart.put("img",url4);
                            } else if (url5 != null){
                                addcart.put("img",url5);
                            } else if (url6 != null){
                                addcart.put("img",url6);
                            }
                            addcart.put("payment",null);
                            addcart.put("date",null);
                            addcart.put("time",null);
                            addcart.put("status",null);
                            addcart.put("address",null);
                            addcart.put("payment_id",null);
                            documentReference.set(addcart).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
                                    Toast.makeText(productpage.this, "Item Added to cart..", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    progressDialog.dismiss();
                                    Log.d("TAG", "onFailure: "+e.getMessage());
                                    Toast.makeText(productpage.this, "Error..", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    addtofav.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DocumentReference dreff = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .collection("my product").document(id);
                            progressDialog.show();
                            Map<String,Object> addcart = new HashMap<>();
                            addcart.put("name",pn);
                            addcart.put("description",pd);
                            addcart.put("price",pr);
                            addcart.put("id",id);
                            addcart.put("type","favourite");
                            addcart.put("quantity",1);
                            if (url1 != null){
                                addcart.put("img",url1);
                            } else if (url2 != null){
                                addcart.put("img",url2);
                            } else if (url3 != null){
                                addcart.put("img",url3);
                            } else if (url4 != null){
                                addcart.put("img",url4);
                            } else if (url5 != null){
                                addcart.put("img",url5);
                            } else if (url6 != null){
                                addcart.put("img",url6);
                            }
                            dreff.set(addcart).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
                                    Toast.makeText(productpage.this, "Item Added to Favourites..", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    progressDialog.dismiss();
                                    Log.d("TAG", "onFailure: "+e.getMessage());
                                    Toast.makeText(productpage.this, "Error..", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        });


        addtocart = findViewById(R.id.addtocartbtn);
        addtofav = findViewById(R.id.addtofavouritebtn);
        recyclerView = findViewById(R.id.PPrecomenddRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }

    private void loadrecy() {
        Log.d("TAG", "loadrecy: got "+category);
        Query query = FirebaseFirestore.getInstance().collection("product").whereEqualTo("category",category);
        FirestoreRecyclerOptions<productModel> options = new FirestoreRecyclerOptions.Builder<productModel>()
                .setQuery(query,productModel.class)
                .build();
       adapter = new FirestoreRecyclerAdapter<productModel,ppViewHolder>(options) {
           @NonNull
           @NotNull
           @Override
           public ppViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
               View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dash_list,parent,false);
               return new ppViewHolder(view);
           }

           @Override
           protected void onBindViewHolder(@NonNull @NotNull productpage.ppViewHolder holder, int position, @NonNull @NotNull productModel model) {
               if (model.getImg1() != null){
                   Picasso.get().load(model.getImg1()).into(holder.dproImage);
               } else if (model.getImg2() != null){
                   Picasso.get().load(model.getImg2()).into(holder.dproImage);
               } else if (model.getImg3() != null){
                   Picasso.get().load(model.getImg3()).into(holder.dproImage);
               } else if (model.getImg4() != null){
                   Picasso.get().load(model.getImg4()).into(holder.dproImage);
               } else if (model.getImg5() != null){
                   Picasso.get().load(model.getImg5()).into(holder.dproImage);
               } else {
                   Picasso.get().load(model.getImg6()).into(holder.dproImage);
               }
               holder.dproName.setText(model.getName());
               holder.dproPrice.setText(String.valueOf(model.getPrice()+" Rs."));
               holder.itemView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       Intent intent = new Intent(productpage.this,productpage.class);
                       intent.putExtra("id",model.getId());
                       startActivity(intent);
                   }
               });
           }
       };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private class ppViewHolder extends RecyclerView.ViewHolder {
        ImageView dproImage;
        TextView dproName,dproPrice;
        public ppViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            dproImage = itemView.findViewById(R.id.dproduct_img);
            dproName = itemView.findViewById(R.id.dproduct_name);
            dproPrice = itemView.findViewById(R.id.dproduct_price);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(productpage.this,MainActivity.class));
        finish();
    }
}