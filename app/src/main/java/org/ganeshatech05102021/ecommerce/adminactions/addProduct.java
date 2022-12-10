package org.ganeshatech05102021.ecommerce.adminactions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.ganeshatech05102021.ecommerce.AdminActivity;
import org.ganeshatech05102021.ecommerce.R;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class addProduct extends AppCompatActivity {

    private Long counter;
    Spinner spinner;
    EditText pname,pdes,price;
    Button save;
    ImageView imone,imtwo,imthree,imfour,imfive,imsix;
    String categoryname;
    private int imval;
    Uri oneuri,twouri,threeuri,foururi,fiveuri,sixuri;
    UploadTask uploadTask;
    String urlone,urltwo,urlthree,urlfour,urlfive,urlsix;
    ProgressDialog uploadingD,savingD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        uploadingD = new ProgressDialog(this);
        uploadingD.setCancelable(false);
        uploadingD.setMessage("Please Wait Uploading image....");

        savingD = new ProgressDialog(this);
        savingD.setCancelable(false);
        savingD.setMessage("Saving data please wait..");

        pname = findViewById(R.id.product_name);
        pdes = findViewById(R.id.product_description);
        price = findViewById(R.id.product_price);
        save = findViewById(R.id.product_savebtn);
        imone = findViewById(R.id.imone);
        imtwo = findViewById(R.id.imtwo);
        imthree = findViewById(R.id.imthree);
        imfour = findViewById(R.id.imfour);
        imfive = findViewById(R.id.imfive);
        imsix = findViewById(R.id.imsix);

        spinner = findViewById(R.id.category_spin);
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
        imfour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imval = 4;
                takepic();
            }
        });
        imfive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imval = 5;
                takepic();
            }
        });
        imsix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imval = 6;
                takepic();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "uploadimg: "+urlone+" "+urltwo+" "+urlthree+" "+urlfour+" "+urlfive+" "+urlsix);
                saveproduct();
            }
        });
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
        } if (urlone == null && urltwo == null && urlthree == null && urlfour == null && urlfive == null && urlsix == null){
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
        product.put("type","product");
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
        if (urlfour != null){
            product.put("img4",urlfour);
        }
        if (urlfive != null){
            product.put("img5",urlfive);
        }
        if (urlsix != null){
            product.put("img6",urlsix);
        }
        product.put("Count",counter);
        documentReference.set(product).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                DocumentReference countref = FirebaseFirestore.getInstance().collection("Count").document("Count");
                countref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()){
                            countref.update("Count", FieldValue.increment(1));
                        }
                    }
                });
                Toast.makeText(addProduct.this, "Product Successfully Added", Toast.LENGTH_SHORT).show();
                if (savingD.isShowing())
                    savingD.dismiss();
                startActivity(new Intent(addProduct.this, AdminActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(addProduct.this, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                if (savingD.isShowing())
                    savingD.dismiss();
            }
        });
    }

    private void takepic() {
        ImagePicker.Companion.with(addProduct.this)
                .crop()
                .maxResultSize(512, 512)
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
        } else if (imval == 4){
            foururi = data.getData();
            if (foururi != null){
                Picasso.get().load(foururi).into(imfour);
                uploadimg();
            }
        } else if (imval == 5){
            fiveuri = data.getData();
            if (fiveuri != null){
                Picasso.get().load(fiveuri).into(imfive);
                uploadimg();
            }
        } else if (imval == 6){
            sixuri = data.getData();
            if (sixuri != null){
                Picasso.get().load(sixuri).into(imsix);
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
                    Toast.makeText(addProduct.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(addProduct.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(addProduct.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else if (imval == 4){
            uploadingD.show();
            uploadTask = reference.putFile(foururi);
            Log.d("TAG", "uploadimg: "+foururi);
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
                        urlfour = task.getResult().toString();
                        Log.d("TAG", "uploadimg:sucess "+urlfour);
                        uploadingD.dismiss();
                    } else {
                        uploadingD.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    uploadingD.dismiss();
                    Toast.makeText(addProduct.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else if (imval == 5){
            uploadingD.show();
            uploadTask = reference.putFile(fiveuri);
            Log.d("TAG", "uploadimg: "+fiveuri);
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
                        urlfive = task.getResult().toString();
                        Log.d("TAG", "uploadimg:sucess "+urlfive);
                        uploadingD.dismiss();
                    } else {
                        uploadingD.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    uploadingD.dismiss();
                    Toast.makeText(addProduct.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else if (imval == 6){
            uploadingD.show();
            uploadTask = reference.putFile(sixuri);
            Log.d("TAG", "uploadimg: "+sixuri);
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
                        urlsix = task.getResult().toString();
                        Log.d("TAG", "uploadimg:sucess "+urlsix);
                        uploadingD.dismiss();
                    } else {
                        uploadingD.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    uploadingD.dismiss();
                    Toast.makeText(addProduct.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        DocumentReference countref = FirebaseFirestore.getInstance().collection("Count").document("Count");
        countref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    counter = task.getResult().getLong("Count");
                    Log.d("TAG", "onComplete: onstart "+counter);
                } else {
                    Map<String,Object> counter = new HashMap<>();
                    counter.put("Count",1);
                    countref.set(counter).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("TAG", "onComplete: onstart counter set");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(addProduct.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}