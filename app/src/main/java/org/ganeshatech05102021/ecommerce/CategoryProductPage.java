package org.ganeshatech05102021.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import org.ganeshatech05102021.ecommerce.fragment.HomeFragment;
import org.ganeshatech05102021.ecommerce.model.productModel;
import org.jetbrains.annotations.NotNull;

public class CategoryProductPage extends AppCompatActivity {
    RecyclerView  categoryRv,searchList;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirestoreRecyclerAdapter categoryAdapter,searchAdapter;
    private Button searchBtn;
    private EditText inputText;
    private String searchInput;
    TextView emptyserch;
    String category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_product_page);

        category = getIntent().getStringExtra("category");
        categoryRv = findViewById(R.id.ca_productRV);
        searchBtn = findViewById(R.id.capr_search_btn);
        inputText = findViewById(R.id.search_product_ca);
        emptyserch = findViewById(R.id.caser_empty);
        searchList = findViewById(R.id.casearch_list);
        searchList.setLayoutManager(new LinearLayoutManager(CategoryProductPage.this));
        categoryRv.setLayoutManager(new LinearLayoutManager(CategoryProductPage.this));


        Query fpquery = fStore.collection("product").whereEqualTo("category",category);
        FirestoreRecyclerOptions<productModel> fpoption = new FirestoreRecyclerOptions.Builder<productModel>()
                .setQuery(fpquery,productModel.class)
                .build();
        categoryAdapter = new FirestoreRecyclerAdapter<productModel,caproductViewHolder>(fpoption) {
            @NonNull
            @NotNull
            @Override
            public caproductViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View vv = LayoutInflater.from(parent.getContext()).inflate(R.layout.dash_list,parent,false);
                return new caproductViewHolder(vv);
            }
            @Override
            protected void onBindViewHolder(@NonNull @NotNull caproductViewHolder holder, int position, @NonNull @NotNull productModel model) {
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
                        Intent intent = new Intent(getApplicationContext(),productpage.class);
                        intent.putExtra("id",model.getId());
                        startActivity(intent);
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
        Query squery = reference.orderBy("name").startAt(searchInput).whereEqualTo("category",category);
        FirestoreRecyclerOptions<productModel> poptions = new FirestoreRecyclerOptions.Builder<productModel>()
                .setQuery(squery,productModel.class)
                .build();
        searchAdapter = new FirestoreRecyclerAdapter<productModel,srViewHolder>(poptions) {
            @NonNull
            @NotNull
            @Override
            public srViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_card,parent,false);
                return new srViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull CategoryProductPage.srViewHolder holder, int position, @NonNull @NotNull productModel model) {
                holder.serchres.setText(model.getName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(),productpage.class);
                        intent.putExtra("id",model.getId());
                        startActivity(intent);
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

    private void serchaction() {
        CollectionReference reference = fStore.collection("product");
        Query squery = reference.orderBy("name").startAt(searchInput).whereEqualTo("category",category);
        FirestoreRecyclerOptions<productModel> poptions = new FirestoreRecyclerOptions.Builder<productModel>()
                .setQuery(squery,productModel.class)
                .build();
        searchAdapter = new FirestoreRecyclerAdapter<productModel, srViewHolder>(poptions) {
            @NonNull
            @NotNull
            @Override
            public srViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_card,parent,false);
                return new srViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull srViewHolder holder, int position, @NonNull @NotNull productModel model) {
                holder.serchres.setText(model.getName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(),productpage.class);
                        intent.putExtra("id",model.getId());
                        startActivity(intent);
                    }
                });
            }
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (getItemCount() == 0){
                    emptyserch.setVisibility(View.VISIBLE);
                    searchList.setVisibility(View.GONE);
                } else {
                    emptyserch.setVisibility(View.GONE);
                    searchList.setVisibility(View.VISIBLE);
                }
            }
        };
        searchList.setAdapter(searchAdapter);
        searchAdapter.startListening();
    }

    private class caproductViewHolder extends RecyclerView.ViewHolder {
        ImageView dproImage;
        TextView dproName,dproPrice;
        public caproductViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            dproImage = itemView.findViewById(R.id.dproduct_img);
            dproName = itemView.findViewById(R.id.dproduct_name);
            dproPrice = itemView.findViewById(R.id.dproduct_price);
        }
    }

    private class srViewHolder extends RecyclerView.ViewHolder {
        TextView serchres;
        public srViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            serchres = itemView.findViewById(R.id.serch_result_text);
        }
    }
}