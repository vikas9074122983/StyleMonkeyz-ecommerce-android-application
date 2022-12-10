package org.ganeshatech05102021.ecommerce.adminactions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.ganeshatech05102021.ecommerce.R;
import org.ganeshatech05102021.ecommerce.model.CategoryModel;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class addCategory extends AppCompatActivity {

    private ImageView addimg;
    private EditText name;
    Button save;
    RecyclerView recyclerView;
    private ProgressDialog progressDialog,dialog;
    Uri imageUri;
    UploadTask uploadTask;
    private FirebaseFirestore fStore;
    private StorageReference storageReference;
    FirestoreRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        addimg = findViewById(R.id.addcategory_image);
        name = findViewById(R.id.category_name);
        save = findViewById(R.id.category_savebtn);
        recyclerView = findViewById(R.id.category_RV);

        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("Category Images");

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait Uploading data....");

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Deleting please wait..");



        addimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(addCategory.this)
                        .cropSquare()
                        .maxResultSize(300, 300)
                        .start();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploaddata();
            }
        });

        Query query = fStore.collection("category");
        FirestoreRecyclerOptions<CategoryModel> options = new FirestoreRecyclerOptions.Builder<CategoryModel>()
                .setQuery(query,CategoryModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<CategoryModel,catViewHolder>(options) {
            @NonNull
            @NotNull
            @Override
            public catViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_card,parent,false);
                return new catViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull addCategory.catViewHolder holder, int position, @NonNull @NotNull CategoryModel model) {
                holder.name.setText(model.getName());
                Picasso.get().load(model.getUrl()).into(holder.img);

                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.show();
                        DocumentReference dref = fStore.collection("category").document(model.getName());
                        dref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                dialog.dismiss();
                                Toast.makeText(addCategory.this, "Category Deleted..", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(addCategory.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter.startListening();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageUri = data.getData();
        if (imageUri != null){
            Picasso.get().load(imageUri).into(addimg);
        }
    }

    private void uploaddata() {
        String cname = name.getText().toString();
        if (cname.isEmpty()){
            name.setError("Name Required");
            return;
        }
        if (imageUri == null){
            Toast.makeText(this, "Choose category Image", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show();
        StorageReference reference = storageReference.child(System.currentTimeMillis()+".jpg");
        uploadTask = reference.putFile(imageUri);
        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw task.getException();
                }
                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    DocumentReference documentReference = fStore.collection("category").document(cname);
                    Map<String,Object> user = new HashMap<>();
                    user.put("name",cname);
                    user.put("url",downloadUri.toString());
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(addCategory.this, "Category Successfully Added", Toast.LENGTH_SHORT).show();
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            name.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(addCategory.this, "Error", Toast.LENGTH_SHORT).show();
                            Log.d("TAG", "onFailure: "+e.getMessage());
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    });
            } else {

                }
            }
        });

    }

    private static class catViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name;
        Button delete;
        public catViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.card_category_img);
            name = itemView.findViewById(R.id.card_category_txt);
            delete = itemView.findViewById(R.id.delete_catbtnnn);
        }
    }

}