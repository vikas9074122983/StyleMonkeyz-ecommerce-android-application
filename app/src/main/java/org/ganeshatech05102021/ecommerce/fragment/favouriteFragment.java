package org.ganeshatech05102021.ecommerce.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import org.ganeshatech05102021.ecommerce.LoginActivity;
import org.ganeshatech05102021.ecommerce.R;
import org.ganeshatech05102021.ecommerce.model.cartModel;
import org.ganeshatech05102021.ecommerce.model.productModel;
import org.ganeshatech05102021.ecommerce.productpage;
import org.jetbrains.annotations.NotNull;

public class favouriteFragment extends Fragment {
    RecyclerView favitemRV,favreccoRV;
    FirestoreRecyclerAdapter favAdapter,favrecoAdapter;
    TextView empty;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        favitemRV = view.findViewById(R.id.favitemRV);
        favitemRV.setLayoutManager(new LinearLayoutManager(getContext()));
        favreccoRV = view.findViewById(R.id.favreccoRV);
        favreccoRV.setLayoutManager(new LinearLayoutManager(getContext()));
        empty = view.findViewById(R.id.fav_empty);

        Query query = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("my product").whereEqualTo("type","favourite");
        FirestoreRecyclerOptions<cartModel> option = new FirestoreRecyclerOptions.Builder<cartModel>()
                .setQuery(query,cartModel.class)
                .build();
        favAdapter = new FirestoreRecyclerAdapter<cartModel, favViewHolder>(option){

            @NonNull
            @NotNull
            @Override
            public favViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View vie = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_card,parent,false);
                return new favViewHolder(vie);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull favouriteFragment.favViewHolder holder, int position, @NonNull @NotNull cartModel model) {
                Picasso.get().load(model.getImg()).into(holder.cpimg);
                holder.cpname.setText(model.getName());
                holder.cpdes.setText(model.getDescription());
                holder.cpprice.setText(String.valueOf(model.getPrice())+" Rs.");
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), productpage.class);
                        intent.putExtra("id",model.getId());
                        startActivity(intent);
                    }
                });
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DocumentReference dref = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .collection("my product").document(model.getId());
                        dref.delete().addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(), "Item Deleted", Toast.LENGTH_SHORT).show();
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
                    favitemRV.setVisibility(View.GONE);
                } else {
                    empty.setVisibility(View.GONE);
                    favitemRV.setVisibility(View.VISIBLE);
                }
            }
        };
        favitemRV.setAdapter(favAdapter);
        favAdapter.startListening();
        favAdapter.notifyDataSetChanged();


        return view;
    }
    @Override
    public void onStart() {
        super.onStart();

        Query query = FirebaseFirestore.getInstance().collection("product").limit(15);
        FirestoreRecyclerOptions<productModel> options = new FirestoreRecyclerOptions.Builder<productModel>()
                .setQuery(query,productModel.class)
                .build();
        favrecoAdapter = new FirestoreRecyclerAdapter<productModel, frViewHolder>(options) {

            @NonNull
            @NotNull
            @Override
            public frViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dash_list,parent,false);
                return new frViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull frViewHolder holder, int position, @NonNull @NotNull productModel model) {
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
                        Intent intent = new Intent(getContext(), productpage.class);
                        intent.putExtra("id",model.getId());
                        startActivity(intent);
                    }
                });
            }
        };
        favreccoRV.setAdapter(favrecoAdapter);
        favrecoAdapter.startListening();
    }

    private class favViewHolder extends RecyclerView.ViewHolder {
        TextView cpname,cpdes,cpprice;
        ImageView cpimg;
        ImageButton delete;
        public favViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cpname = itemView.findViewById(R.id.fav_name_txt);
            cpdes = itemView.findViewById(R.id.fav_desc_txt);
            cpprice = itemView.findViewById(R.id.fav_pro_price);
            cpimg = itemView.findViewById(R.id.fav_img);
            delete = itemView.findViewById(R.id.delete_fav_btn);
        }
    }

    private class frViewHolder extends RecyclerView.ViewHolder  {
        ImageView dproImage;
        TextView dproName,dproPrice;
        public frViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            dproImage = itemView.findViewById(R.id.dproduct_img);
            dproName = itemView.findViewById(R.id.dproduct_name);
            dproPrice = itemView.findViewById(R.id.dproduct_price);
        }
    }
}