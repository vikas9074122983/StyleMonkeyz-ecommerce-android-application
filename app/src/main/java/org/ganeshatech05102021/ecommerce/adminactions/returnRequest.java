package org.ganeshatech05102021.ecommerce.adminactions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.ganeshatech05102021.ecommerce.R;
import org.ganeshatech05102021.ecommerce.model.adminSerchModel;
import org.jetbrains.annotations.NotNull;

public class returnRequest extends AppCompatActivity {
    RecyclerView ordersRV;
    TextView empty;
    FirestoreRecyclerAdapter orderadapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_request);
        ordersRV = findViewById(R.id.returnorder_RV);
        empty = findViewById(R.id.return_empty);

        FirebaseFirestore fstore = FirebaseFirestore.getInstance();
        Query query = fstore.collection("return").whereEqualTo("status","return");

        FirestoreRecyclerOptions<adminSerchModel> option = new FirestoreRecyclerOptions.Builder<adminSerchModel>()
                .setQuery(query,adminSerchModel.class)
                .build();
        orderadapter = new FirestoreRecyclerAdapter<adminSerchModel, rerderViewHolder>(option){
            @NonNull
            @NotNull
            @Override
            public rerderViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View vie = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_search_card,parent,false);
                return new rerderViewHolder(vie);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull rerderViewHolder holder, int position, @NonNull @NotNull adminSerchModel model) {
                holder.name.setText(model.getName());
                holder.id.setText(model.getId());
                Log.d("TAG", "onBindViewHolder: "+model.getId());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(returnRequest.this, returnRequestAction.class);
                        intent.putExtra("id",model.getId());
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
        orderadapter.notifyDataSetChanged();
    }

    private class rerderViewHolder extends RecyclerView.ViewHolder{
        TextView name ,id;
        public rerderViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.user_name);
            id = itemView.findViewById(R.id.user_id);
        }
    }
}