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
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

import org.ganeshatech05102021.ecommerce.ConfirmOrderPage;
import org.ganeshatech05102021.ecommerce.R;
import org.ganeshatech05102021.ecommerce.model.cartModel;
import org.ganeshatech05102021.ecommerce.model.productModel;
import org.ganeshatech05102021.ecommerce.productpage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class cartFragment extends Fragment {

    RecyclerView cartitemRV,reccoRV;
    FirestoreRecyclerAdapter cartAdapter,recoAdapter;
    TextView empty;
    Button placeoreder;

    ArrayList<String> ids = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        cartitemRV = view.findViewById(R.id.cart_recy);
        cartitemRV.setLayoutManager(new LinearLayoutManager(getContext()));
        reccoRV = view.findViewById(R.id.cart_recco_recy);
        reccoRV.setLayoutManager(new LinearLayoutManager(getContext()));
        empty = view.findViewById(R.id.cart_empty);
        placeoreder = view.findViewById(R.id.placeorder_btn);

        Query cquery = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("my product").whereEqualTo("type","cart");
        FirestoreRecyclerOptions<cartModel> option = new FirestoreRecyclerOptions.Builder<cartModel>()
                .setQuery(cquery,cartModel.class)
                .build();
        cartAdapter = new FirestoreRecyclerAdapter<cartModel, cartViewHolder>(option){
            @NonNull
            @NotNull
            @Override
            public cartViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View vie = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_card,parent,false);
                return new cartViewHolder(vie);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull cartFragment.cartViewHolder holder, int position, @NonNull @NotNull cartModel model) {
                Picasso.get().load(model.getImg()).into(holder.cpimg);
                holder.cpname.setText(model.getName());
                holder.cpdes.setText(model.getDescription());
                holder.cpquantity.setText(String.valueOf(model.getQuantity()));
                Long tempPrice = model.getPrice();
                Long tempItem = model.getQuantity();
                Long price = tempPrice*tempItem;
                holder.cpprice.setText(String.valueOf(price)+" Rs.");

//                if (ids.isEmpty()){
//                    ids.add(model.getPid());
//                }
//                if (!ids.get(ids.size()-1).equals(model.getPid())){
//                    ids.add(model.getPid());
//                }

                placeoreder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Log.d("TAG", "onBindViewHolder: "+ids);
                        Intent intent = new Intent(getContext(), ConfirmOrderPage.class);
//                        intent.putExtra("id", ids);
                        startActivity(intent);
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(),productpage.class);
                        intent.putExtra("id",model.getId());
                        startActivity(intent);
                    }
                });

                holder.plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DocumentReference dref = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .collection("my product").document(model.getPid());
                        dref.update("quantity", FieldValue.increment(1));
                    }
                });
                holder.minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DocumentReference dref = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .collection("my product").document(model.getPid());
                        dref.update("quantity", FieldValue.increment(-1));
                    }
                });
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DocumentReference dref = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .collection("my product").document(model.getPid());
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
                    cartitemRV.setVisibility(View.GONE);
                    placeoreder.setClickable(false);
                } else {
                    empty.setVisibility(View.GONE);
                    cartitemRV.setVisibility(View.VISIBLE);
                    placeoreder.setClickable(true);
                }
            }

        };
        cartitemRV.setAdapter(cartAdapter);
        cartAdapter.startListening();
        cartAdapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = FirebaseFirestore.getInstance().collection("product").limit(15);
        FirestoreRecyclerOptions<productModel> options = new FirestoreRecyclerOptions.Builder<productModel>()
                .setQuery(query,productModel.class)
                .build();
        recoAdapter = new FirestoreRecyclerAdapter<productModel, rppViewHolder>(options) {

            @NonNull
            @NotNull
            @Override
            public rppViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dash_list,parent,false);
                return new rppViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull cartFragment.rppViewHolder holder, int position, @NonNull @NotNull productModel model) {
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
        reccoRV.setAdapter(recoAdapter);
        recoAdapter.startListening();
    }

    private class rppViewHolder extends RecyclerView.ViewHolder {
        ImageView dproImage;
        TextView dproName,dproPrice;
        public rppViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            dproImage = itemView.findViewById(R.id.dproduct_img);
            dproName = itemView.findViewById(R.id.dproduct_name);
            dproPrice = itemView.findViewById(R.id.dproduct_price);
        }
    }

    private class cartViewHolder extends RecyclerView.ViewHolder {
        TextView cpname,cpdes,cpprice,cpquantity;
        ImageView cpimg;
        ImageButton delete,plus,minus;
        public cartViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cpname = itemView.findViewById(R.id.cart_name_txt);
            cpdes = itemView.findViewById(R.id.card_desc_txt);
            cpprice = itemView.findViewById(R.id.cart_pro_price);
            cpquantity = itemView.findViewById(R.id.quantity);
            cpimg = itemView.findViewById(R.id.cart_img);
            delete = itemView.findViewById(R.id.deletefromcart_btn);
            plus = itemView.findViewById(R.id.qinc);
            minus = itemView.findViewById(R.id.qdec);
        }
    }
}