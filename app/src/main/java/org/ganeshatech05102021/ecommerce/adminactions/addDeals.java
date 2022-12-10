package org.ganeshatech05102021.ecommerce.adminactions;

import androidx.annotation.NonNull;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.ganeshatech05102021.ecommerce.AdminActivity;
import org.ganeshatech05102021.ecommerce.R;
import org.ganeshatech05102021.ecommerce.model.CategoryModel;
import org.ganeshatech05102021.ecommerce.model.productModel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class addDeals extends AppCompatActivity {

    Spinner spinner;
    EditText pname,pdes,price;
    Button save;
    ImageView imone,imtwo,imthree;
    String categoryname;
    private int imval;
    Uri oneuri,twouri,threeuri;
    UploadTask uploadTask;
    String urlone,urltwo,urlthree;
    ProgressDialog uploadingD,savingD,dialog;
    FirestoreRecyclerAdapter fadapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deals);
        uploadingD = new ProgressDialog(this);
        uploadingD.setCancelable(false);
        uploadingD.setMessage("Please Wait Uploading image....");

        savingD = new ProgressDialog(this);
        savingD.setCancelable(false);
        savingD.setMessage("Saving data please wait..");

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Deleting please wait..");

        pname = findViewById(R.id.hdproduct_name);
        pdes = findViewById(R.id.hdproduct_description);
        price = findViewById(R.id.hdproduct_price);
        save = findViewById(R.id.hdproduct_savebtn);
        imone = findViewById(R.id.hdimone);
        imtwo = findViewById(R.id.hdimtwo);
        imthree = findViewById(R.id.hdimthree);
        recyclerView = findViewById(R.id.deal_RV);

        spinner = findViewById(R.id.hdcategory_spin);
        FirebaseFirestore ref = FirebaseFirestore.getInstance();
        CollectionReference ceteref = ref.collection("category");
        List<String> category =new ArrayList<>();
        category.add(0,"Select Category");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,category);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        ceteref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String cname = document.getString("name");
                        category.add(cname);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).equals("Select Category")){
                    categoryname = "";
                } else {
                    categoryname = adapterView.getItemAtPosition(i).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getApplicationContext(), "Please Select Category", Toast.LENGTH_SHORT).show();
            }
        });

        imone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imval = 1;
                takepic();
            }
        });
        imtwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imval = 2;
                takepic();
            }
        });
        imthree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imval = 3;
                takepic();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "uploadimg: "+urlone+" "+urltwo+" "+urlthree);
                saveproduct();
            }
        });

        Query query = FirebaseFirestore.getInstance().collection("product").whereEqualTo("type","deal");
        FirestoreRecyclerOptions<productModel> options = new FirestoreRecyclerOptions.Builder<productModel>()
                .setQuery(query,productModel.class)
                .build();
        fadapter = new FirestoreRecyclerAdapter<productModel, dealViewHolder>(options) {
            @NonNull
            @NotNull
            @Override
            public dealViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_card,parent,false);
                return new dealViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull addDeals.dealViewHolder holder, int position, @NonNull @NotNull productModel model) {
                holder.name.setText(model.getName());
                if (model.getImg1() != null){
                    Picasso.get().load(model.getImg1()).into(holder.img);
                } else if (model.getImg2() != null){
                    Picasso.get().load(model.getImg2()).into(holder.img);
                } else {
                    Picasso.get().load(model.getImg3()).into(holder.img);
                }
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.show();
                        DocumentReference dref = FirebaseFirestore.getInstance().collection("product").document(model.getId());
                        dref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                dialog.dismiss();
                                Toast.makeText(addDeals.this, "Deal Deleted..", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(addDeals.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            }
        };
        recyclerView.setAdapter(fadapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        fadapter.startListening();
    }

    private void saveproduct() {
        String pn = pname.getText().toString();
        String pd = pdes.getText().toString();
        String pp = price.getText().toString();

        if (categoryname.isEmpty()){
            Toast.makeText(this, "Select Category", Toast.LENGTH_SHORT).show();
            return;
        } if (pn.isEmpty()){
            pname.setError("Product name Required");
            return;
        } if (pd.isEmpty()){
            pdes.setError("Product Description Required");
            return;
        } if (pp.isEmpty()){
            price.setError("Product price Required");
            return;
        } if (urlone == null && urltwo == null && urlthree == null){
            Toast.makeText(this, "Please Select Atleast one Image", Toast.LENGTH_SHORT).show();
            return;
        }

        savingD.show();
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("product").document();
        Map<String,Object> product = new HashMap<>();
        product.put("name",pn);
        product.put("description",pd);
        product.put("price",Double.parseDouble(price.getText().toString()));
        product.put("category",categoryname);
        product.put("type","deal");
        product.put("id",documentReference.getId());
        if (urlone != null){
            product.put("img1",urlone);
        }
        if (urltwo != null){
            product.put("img2",urltwo);
        }
        if (urlthree != null){
            product.put("img3",urlthree);
        }
        documentReference.set(product).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(addDeals.this, "Product Successfully Added", Toast.LENGTH_SHORT).show();
                if (savingD.isShowing())
                    savingD.dismiss();
                startActivity(new Intent(addDeals.this, AdminActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(addDeals.this, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                if (savingD.isShowing())
                    savingD.dismiss();
            }
        });
    }

    private void takepic() {
        ImagePicker.Companion.with(addDeals.this)
                .crop()
                .maxResultSize(400, 400)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (imval == 1){
            oneuri = data.getData();
            if (oneuri != null){
                Picasso.get().load(oneuri).into(imone);
                uploadimg();
            }
        } else if (imval == 2){
            twouri = data.getData();
            if (twouri != null){
                Picasso.get().load(twouri).into(imtwo);
                uploadimg();
            }
        } else if (imval == 3){
            threeuri = data.getData();
            if (threeuri != null){
                Picasso.get().load(threeuri).into(imthree);
                uploadimg();
            }
        }
    }

    private void uploadimg() {
        StorageReference reference = FirebaseStorage.getInstance().getReference("Product Images").child(System.currentTimeMillis()+".jpg");

        if (imval == 1){
            uploadingD.show();
            uploadTask = reference.putFile(oneuri);
            Log.d("TAG", "uploadimg: "+oneuri);
            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        uploadingD.dismiss();
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        urlone = task.getResult().toString();
                        Log.d("TAG", "uploadimg:sucess "+urlone);
                        uploadingD.dismiss();
                    } else {
                        uploadingD.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    uploadingD.dismiss();
                    Toast.makeText(addDeals.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else if (imval == 2){
            uploadingD.show();
            uploadTask = reference.putFile(twouri);
            Log.d("TAG", "uploadimg: "+twouri);
            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        uploadingD.dismiss();
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        urltwo = task.getResult().toString();
                        Log.d("TAG", "uploadimg:sucess "+urltwo);
                        uploadingD.dismiss();
                    } else {
                        uploadingD.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    uploadingD.dismiss();
                    Toast.makeText(addDeals.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else if (imval == 3){
            uploadingD.show();
            uploadTask = reference.putFile(threeuri);
            Log.d("TAG", "uploadimg: "+oneuri);
            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        uploadingD.dismiss();
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        urlthree = task.getResult().toString();
                        Log.d("TAG", "uploadimg:sucess "+urlthree);
                        uploadingD.dismiss();
                    } else {
                        uploadingD.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    uploadingD.dismiss();
                    Toast.makeText(addDeals.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private class dealViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name;
        Button delete;
        public dealViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.card_category_img);
            name = itemView.findViewById(R.id.card_category_txt);
            delete = itemView.findViewById(R.id.delete_catbtnnn);
        }
    }
}