package org.ganeshatech05102021.ecommerce;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class usersetting extends AppCompatActivity {
    CircleImageView upprofileImageView;
    EditText upname,upphone,uplocation;
    TextView backbtn;
    Button savebtn;
    FloatingActionButton pickimgbtn;
    Uri imageUri;
    Uri newImgUri;
    Uri updateUri;
    boolean tfval = false ;
    String myUrl = "";
    StorageReference storageProfilePrictureRef;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    StorageReference fStorage;
    DocumentReference documentReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    String userID;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usersetting);
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        documentReference = fStore.collection("users").document(userID);
        storageReference = FirebaseStorage.getInstance().getReference("Profile Images");

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait updating data....");
        upprofileImageView = findViewById(R.id.up_profile_image);
        upname = findViewById(R.id.up_full_name);
        upphone = findViewById(R.id.up_phone);
        uplocation = findViewById(R.id.up_location);
        savebtn = findViewById(R.id.savebtn);
        pickimgbtn = findViewById(R.id.update_image_btn);
        backbtn = findViewById(R.id.upclose_settings_btn);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        pickimgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(usersetting.this)
                        .crop()
                        .maxResultSize(512, 512)
                        .start();
            }
        });
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateProfile();
            }
        });
    }

    private void UpdateProfile() {
        String name = upname.getText().toString();
        String phone = upphone.getText().toString();
        String location = uplocation.getText().toString();

        if (name.isEmpty()){
            upname.setError("Name Required");
            return;
        }
        if (phone.isEmpty()){
            upphone.setError("Phone Number Required");
            return;
        }
        if (location.isEmpty()){
            uplocation.setError("Address Required");
            return;
        }
        if (tfval == true){
            imageUri = newImgUri;
        }if (imageUri == null){
            Toast.makeText(usersetting.this, "Please select an Image", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show();
        userID = fAuth.getCurrentUser().getUid();
        final StorageReference reference = storageReference.child(System.currentTimeMillis()+".jpg");
        UploadTask uploadTask = reference.putFile(imageUri);
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
                    DocumentReference documentReference = fStore.collection("users").document(userID);
                    Map<String,Object> user = new HashMap<>();
                    user.put("name",name);
                    user.put("phone",phone);
                    user.put("address",location);
                    user.put("url",downloadUri.toString());
                    documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(usersetting.this, "User Profile Successfully Updated", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),userInfo.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(usersetting.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(usersetting.this, "Error :"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            newImgUri = data.getData();
            Picasso.get().load(newImgUri).into(upprofileImageView);
            tfval = true;
    }
    @Override
    protected void onStart() {
        super.onStart();
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){
                    String rname = task.getResult().getString("name");
                    String rphone = task.getResult().getString("phone");
                    String rlocation = task.getResult().getString("address");
                    String Url = task.getResult().getString("url");

                    if (tfval == false){
                        Picasso.get().load(Url).into(upprofileImageView);
                    }
                    upname.setText(rname);
                    upphone.setText(rphone);
                    uplocation.setText(rlocation);
                } else {
                    Toast.makeText(usersetting.this, "No Profile Exist", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(usersetting.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}