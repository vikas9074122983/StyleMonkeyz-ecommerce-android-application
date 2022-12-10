package org.ganeshatech05102021.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import org.ganeshatech05102021.ecommerce.fragment.cartFragment;
import org.ganeshatech05102021.ecommerce.model.cartModel;
import org.jetbrains.annotations.NotNull;

public class orderspage extends AppCompatActivity {

    RecyclerView ordersRV;
    FirestoreRecyclerAdapter orderadapter;
    TextView empty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderspage);

        ordersRV = findViewById(R.id.myorders_recy);
        empty = findViewById(R.id.orders_empty);

        Query query = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("my product").whereEqualTo("type","ordered").orderBy("priority", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<cartModel> option = new FirestoreRecyclerOptions.Builder<cartModel>()
                .setQuery(query,cartModel.class)
                .build();
        orderadapter = new FirestoreRecyclerAdapter<cartModel, orderViewHolder>(option){
            @NonNull
            @NotNull
            @Override
            public orderViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View vie = LayoutInflater.from(parent.getContext()).inflate(R.layout.myorder_card,parent,false);
                return new orderViewHolder(vie);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull orderspage.orderViewHolder holder, int position, @NonNull @NotNull cartModel model) {
                Picasso.get().load(model.getImg()).into(holder.img);
                holder.name.setText(model.getName());
                holder.des.setText(model.getDescription());
                holder.price.setText(String.valueOf(model.getPrice()));
                holder.quantity.setText(String.valueOf(model.getQuantity()));
                holder.status.setText(model.getStatus());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(orderspage.this,MOrderPage.class);
                        intent.putExtra("id",model.getPid());
                        startActivity(intent);
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
    }

    private class orderViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name,des,quantity,price,status;
        public orderViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.order_img);
            name = itemView.findViewById(R.id.order_name_txt);
            des = itemView.findViewById(R.id.order_desc_txt);
            quantity = itemView.findViewById(R.id.oquantity);
            price = itemView.findViewById(R.id.order_price);
            status = itemView.findViewById(R.id.order_status);
        }
    }
}