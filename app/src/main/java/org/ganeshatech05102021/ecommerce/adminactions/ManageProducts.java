package org.ganeshatech05102021.ecommerce.adminactions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import org.ganeshatech05102021.ecommerce.CategoryProductPage;
import org.ganeshatech05102021.ecommerce.MOrderPage;
import org.ganeshatech05102021.ecommerce.MainActivity;
import org.ganeshatech05102021.ecommerce.R;
import org.ganeshatech05102021.ecommerce.model.productModel;
import org.ganeshatech05102021.ecommerce.productpage;
import org.jetbrains.annotations.NotNull;

public class ManageProducts extends AppCompatActivity {
    RecyclerView categoryRv,searchList;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirestoreRecyclerAdapter categoryAdapter,searchAdapter;
    private Button searchBtn;
    private EditText inputText;
    private String searchInput;
    TextView emptyserch;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);

        categoryRv = findViewById(R.id.allproductRV);
        searchBtn = findViewById(R.id.prosearch_btn);
        inputText = findViewById(R.id.search_pro);
        emptyserch = findViewById(R.id.caasser_empty);
        searchList = findViewById(R.id.search_res);
        searchList.setLayoutManager(new LinearLayoutManager(ManageProducts.this));
        categoryRv.setLayoutManager(new LinearLayoutManager(ManageProducts.this));

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait ....");

        Query fpquery = fStore.collection("product");
        FirestoreRecyclerOptions<productModel> fpoption = new FirestoreRecyclerOptions.Builder<productModel>()
                .setQuery(fpquery,productModel.class)
                .build();
        categoryAdapter = new FirestoreRecyclerAdapter<productModel, prooViewHolder>(fpoption) {
            @NonNull
            @NotNull
            @Override
            public prooViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View vv = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_card,parent,false);
                return new prooViewHolder(vv);
            }
            @Override
            protected void onBindViewHolder(@NonNull @NotNull prooViewHolder holder, int position, @NonNull @NotNull productModel model) {
                if (model.getImg1() != null){
                    Picasso.get().load(model.getImg1()).into(holder.cpimg);
                } else if (model.getImg2() != null){
                    Picasso.get().load(model.getImg2()).into(holder.cpimg);
                } else if (model.getImg3() != null){
                    Picasso.get().load(model.getImg3()).into(holder.cpimg);
                } else if (model.getImg4() != null){
                    Picasso.get().load(model.getImg4()).into(holder.cpimg);
                } else if (model.getImg5() != null){
                    Picasso.get().load(model.getImg5()).into(holder.cpimg);
                } else {
                    Picasso.get().load(model.getImg6()).into(holder.cpimg);
                }
                holder.cpname.setText(model.getName());
                holder.cpdes.setText(model.getDescription());
                holder.cpprice.setText(String.valueOf(model.getPrice()+" Rs."));
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ManageProducts.this);
                        builder.setTitle("Delete Product");
                        builder.setMessage("Do you want to delete this product ? ");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                progressDialog.show();
                                DocumentReference dref = FirebaseFirestore.getInstance().collection("product").document(model.getId());
                                dref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
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
        };
        categoryRv.setAdapter(categoryAdapter);
        categoryAdapter.startListening();

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchInput = inputText.getText().toString();
                if (searchInput.isEmpty()){
                    searchList.setVisibility(View.GONE);
                } else {
                    searchList.setVisibility(View.VISIBLE);
                    search();
                }
            }
        });
    }

    private void search() {
        CollectionReference reference = fStore.collection("product");
        Query squery = reference.orderBy("name").startAt(searchInput);
        FirestoreRecyclerOptions<productModel> poptions = new FirestoreRecyclerOptions.Builder<productModel>()
                .setQuery(squery,productModel.class)
                .build();
        searchAdapter = new FirestoreRecyclerAdapter<productModel, proViewHolder>(poptions) {
            @NonNull
            @NotNull
            @Override
            public proViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_card,parent,false);
                return new proViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull proViewHolder holder, int position, @NonNull @NotNull productModel model) {
                if (model.getImg1() != null){
                    Picasso.get().load(model.getImg1()).into(holder.cpimg);
                } else if (model.getImg2() != null){
                    Picasso.get().load(model.getImg2()).into(holder.cpimg);
                } else if (model.getImg3() != null){
                    Picasso.get().load(model.getImg3()).into(holder.cpimg);
                } else if (model.getImg4() != null){
                    Picasso.get().load(model.getImg4()).into(holder.cpimg);
                } else if (model.getImg5() != null){
                    Picasso.get().load(model.getImg5()).into(holder.cpimg);
                } else {
                    Picasso.get().load(model.getImg6()).into(holder.cpimg);
                }
                holder.cpname.setText(model.getName());
                holder.cpdes.setText(model.getDescription());
                holder.cpprice.setText(String.valueOf(model.getPrice()+" Rs."));
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ManageProducts.this);
                        builder.setTitle("Delete Product");
                        builder.setMessage("Do you want to delete this product ? ");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                progressDialog.show();
                                DocumentReference dref = FirebaseFirestore.getInstance().collection("product").document(model.getId());
                                dref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
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
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (getItemCount() == 0){
                    searchList.setVisibility(View.GONE);
                    emptyserch.setVisibility(View.VISIBLE);
                } else {
                    searchList.setVisibility(View.VISIBLE);
                    emptyserch.setVisibility(View.GONE);
                }
            }
        };
        searchList.setAdapter(searchAdapter);
        searchAdapter.startListening();
    }

    private class proViewHolder extends RecyclerView.ViewHolder {
        TextView cpname,cpdes,cpprice;
        ImageView cpimg;
        ImageButton delete;
        public proViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cpname = itemView.findViewById(R.id.fav_name_txt);
            cpdes = itemView.findViewById(R.id.fav_desc_txt);
            cpprice = itemView.findViewById(R.id.fav_pro_price);
            cpimg = itemView.findViewById(R.id.fav_img);
            delete = itemView.findViewById(R.id.delete_fav_btn);
        }
    }
    private class prooViewHolder extends RecyclerView.ViewHolder {
        TextView cpname,cpdes,cpprice;
        ImageView cpimg;
        ImageButton delete;
        public prooViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cpname = itemView.findViewById(R.id.fav_name_txt);
            cpdes = itemView.findViewById(R.id.fav_desc_txt);
            cpprice = itemView.findViewById(R.id.fav_pro_price);
            cpimg = itemView.findViewById(R.id.fav_img);
            delete = itemView.findViewById(R.id.delete_fav_btn);
        }
    }
}