package org.ganeshatech05102021.ecommerce.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import org.ganeshatech05102021.ecommerce.CategoryProductPage;
import org.ganeshatech05102021.ecommerce.R;
import org.ganeshatech05102021.ecommerce.adminactions.addCategory;
import org.ganeshatech05102021.ecommerce.adminactions.addProduct;
import org.ganeshatech05102021.ecommerce.model.CategoryModel;
import org.ganeshatech05102021.ecommerce.model.productModel;
import org.ganeshatech05102021.ecommerce.productpage;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    RecyclerView categoryRv,dealsRV, dashboardRv,searchList,recomenddRV;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirestoreRecyclerAdapter categoryAdapter,dealsAdapter,searchAdapter,dashAdapter,recomendAdapter;
    private Button searchBtn;
    private EditText inputText;
    private String searchInput,recomendcategory;
    TextView emptyserch;
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        categoryRv = view.findViewById(R.id.category_homeRV);
        dashboardRv = view.findViewById(R.id.dashboardRV);
        searchBtn = view.findViewById(R.id.pr_search_btn);
        inputText = view.findViewById(R.id.search_product_name);
        emptyserch = view.findViewById(R.id.ser_empty);
        searchList = view.findViewById(R.id.search_list);
        dealsRV = view.findViewById(R.id.dealsRV);
        recomenddRV = view.findViewById(R.id.recomenddRV);
        searchList.setLayoutManager(new LinearLayoutManager(getContext()));

        Query query = fStore.collection("category");
        FirestoreRecyclerOptions<CategoryModel> CAoptions = new FirestoreRecyclerOptions.Builder<CategoryModel>()
                .setQuery(query,CategoryModel.class)
                .build();
        categoryAdapter = new FirestoreRecyclerAdapter<CategoryModel, caViewHolder>(CAoptions){
            @NonNull
            @NotNull
            @Override
            public caViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_main,parent,false);
                return new caViewHolder(v);
            }
            @Override
            protected void onBindViewHolder(@NonNull @NotNull HomeFragment.caViewHolder holder, int position, @NonNull @NotNull CategoryModel model) {
                holder.ctxt.setText(model.getName());
                Picasso.get().load(model.getUrl()).into(holder.cimg);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(uid);
                        documentReference.update("cat1",model.getName());
                        Intent intent = new Intent(getContext(), CategoryProductPage.class);
                        intent.putExtra("category",model.getName());
                        startActivity(intent);
                    }
                });
            }
        };
        categoryRv.setAdapter(categoryAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        categoryRv.setLayoutManager(linearLayoutManager);
        categoryRv.setNestedScrollingEnabled(false);
        categoryAdapter.startListening();

        Query dealquery = fStore.collection("product").whereEqualTo("type","deal");
        FirestoreRecyclerOptions<productModel> Dealoptions = new FirestoreRecyclerOptions.Builder<productModel>()
                .setQuery(dealquery,productModel.class)
                .build();
        dealsAdapter = new FirestoreRecyclerAdapter<productModel,dViewHolder>(Dealoptions) {
            @NonNull
            @NotNull
            @Override
            public dViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View vi = LayoutInflater.from(parent.getContext()).inflate(R.layout.deals_card,parent,false);
                return new dViewHolder(vi);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull HomeFragment.dViewHolder holder, int position, @NonNull @NotNull productModel model) {
                holder.dname.setText(model.getName());
                holder.dprice.setText(String.valueOf(model.getPrice()+" Rs."));
                Log.d("TAG", "onBindViewHolder: got deals "+model.getName());
                if (model.getImg1() != null){
                    Picasso.get().load(model.getImg1()).into(holder.dimg);
                } else if (model.getImg2() != null){
                    Picasso.get().load(model.getImg2()).into(holder.dimg);
                } else {
                    Picasso.get().load(model.getImg3()).into(holder.dimg);
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(),productpage.class);
                        intent.putExtra("id",model.getId());
                        startActivity(intent);
                    }
                });
            }
        };
        dealsRV.setAdapter(dealsAdapter);
        LinearLayoutManager dealamanger = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        dealsRV.setLayoutManager(dealamanger);
        dealsRV.setNestedScrollingEnabled(false);
        dealsAdapter.startListening();

        DocumentReference recomendref = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        recomendref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                    if (task.getResult().getString("cat1") != null){
                        recomendcategory = task.getResult().getString("cat1");
                        onStart();
                        Log.d("TAG", "onComplete: onstart "+recomendcategory);
                    }
            }
        });


        Query fpquery = fStore.collection("product").whereEqualTo("type","product");
        FirestoreRecyclerOptions<productModel> fpoption = new FirestoreRecyclerOptions.Builder<productModel>()
                .setQuery(fpquery,productModel.class)
                .build();
        dashAdapter = new FirestoreRecyclerAdapter<productModel,dashproductViewHolder>(fpoption) {
            @NonNull
            @NotNull
            @Override
            public dashproductViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View vv = LayoutInflater.from(parent.getContext()).inflate(R.layout.dash_list,parent,false);
                return new dashproductViewHolder(vv);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull HomeFragment.dashproductViewHolder holder, int position, @NonNull @NotNull productModel model) {
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
                        Intent intent = new Intent(getContext(),productpage.class);
                        intent.putExtra("id",model.getId());
                        startActivity(intent);
                    }
                });
            }
        };
        dashboardRv.setAdapter(dashAdapter);
        dashboardRv.setLayoutManager(new LinearLayoutManager(getContext()));
        dashAdapter.startListening();



        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchInput = inputText.getText().toString();
                if (searchInput.isEmpty()){
                    searchList.setVisibility(View.GONE);
                } else {
                    searchList.setVisibility(View.VISIBLE);
                    serchaction();
                }
            }
        });
        return view;
    }

    private void serchaction() {
        CollectionReference reference = fStore.collection("product");
        Query squery = reference.orderBy("name").startAt(searchInput);
        FirestoreRecyclerOptions<productModel> poptions = new FirestoreRecyclerOptions.Builder<productModel>()
                .setQuery(squery,productModel.class)
                .build();
        searchAdapter = new FirestoreRecyclerAdapter<productModel,pViewHolder>(poptions) {
            @NonNull
            @NotNull
            @Override
            public pViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_card,parent,false);
                return new pViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull HomeFragment.pViewHolder holder, int position, @NonNull @NotNull productModel model) {
                holder.serchres.setText(model.getName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(),productpage.class);
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

    private class caViewHolder extends RecyclerView.ViewHolder {
        ImageView cimg;
        TextView ctxt;
        public caViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cimg = itemView.findViewById(R.id.category_img);
            ctxt = itemView.findViewById(R.id.category_txt);
        }
    }

    private class pViewHolder extends RecyclerView.ViewHolder {
        TextView serchres;
        public pViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            serchres = itemView.findViewById(R.id.serch_result_text);
        }
    }

    private class dViewHolder extends RecyclerView.ViewHolder {
        ImageView dimg;
        TextView dname,dprice;
        public dViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            dimg = itemView.findViewById(R.id.deals_img);
            dname = itemView.findViewById(R.id.deal_name);
            dprice = itemView.findViewById(R.id.deal_price);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("TAG", "onCreateView: requery "+recomendcategory);
        Query requery = fStore.collection("product").whereEqualTo("category",recomendcategory).limit(6);
        FirestoreRecyclerOptions<productModel> recoption = new FirestoreRecyclerOptions.Builder<productModel>()
                .setQuery(requery,productModel.class)
                .build();
        recomendAdapter = new FirestoreRecyclerAdapter<productModel,productViewHolder>(recoption) {
            @NonNull
            @NotNull
            @Override
            public productViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View vie = LayoutInflater.from(parent.getContext()).inflate(R.layout.dash_list,parent,false);
                return new productViewHolder(vie);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull HomeFragment.productViewHolder holder, int position, @NonNull @NotNull productModel model) {
                if (model.getImg1() != null){
                    Picasso.get().load(model.getImg1()).into(holder.proImage);
                } else if (model.getImg2() != null){
                    Picasso.get().load(model.getImg2()).into(holder.proImage);
                } else if (model.getImg3() != null){
                    Picasso.get().load(model.getImg3()).into(holder.proImage);
                } else if (model.getImg4() != null){
                    Picasso.get().load(model.getImg4()).into(holder.proImage);
                } else if (model.getImg5() != null){
                    Picasso.get().load(model.getImg5()).into(holder.proImage);
                } else {
                    Picasso.get().load(model.getImg6()).into(holder.proImage);
                }
                holder.proName.setText(model.getName());
                holder.proPrice.setText(String.valueOf(model.getPrice()+" Rs."));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(),productpage.class);
                        intent.putExtra("id",model.getId());
                        startActivity(intent);
                    }
                });
            }
        };
        recomenddRV.setAdapter(recomendAdapter);
        recomenddRV.setLayoutManager(new LinearLayoutManager(getContext()));
        recomendAdapter.startListening();
    }

    private class productViewHolder extends RecyclerView.ViewHolder {
        ImageView proImage;
        TextView proName,proPrice;
        public productViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            proImage = itemView.findViewById(R.id.dproduct_img);
            proName = itemView.findViewById(R.id.dproduct_name);
            proPrice = itemView.findViewById(R.id.dproduct_price);
        }
    }

    private class dashproductViewHolder extends RecyclerView.ViewHolder {
        ImageView dproImage;
        TextView dproName,dproPrice;
        public dashproductViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            dproImage = itemView.findViewById(R.id.dproduct_img);
            dproName = itemView.findViewById(R.id.dproduct_name);
            dproPrice = itemView.findViewById(R.id.dproduct_price);
        }
    }
}