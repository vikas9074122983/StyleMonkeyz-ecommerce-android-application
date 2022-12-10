package org.ganeshatech05102021.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfirmOrderPage extends AppCompatActivity implements PaymentResultListener {

    CheckBox pay,cod;
    long total = 0, totalquantity = 0;
    int i;
    TextView itemqau,itemtotal;
    EditText name,mail,phone,addr;
    Button confirmbtn;
    ProgressDialog progressDialog;
    String currentTime, DateToday,username,orderid,address;
    ArrayList<String> idList = new ArrayList<>();
    List<String> newidList;
    private String idl;
    private long priority = -1 * new Date().getTime();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order_page);

        pay = findViewById(R.id.pay_online);
        cod = findViewById(R.id.cod);
        itemqau = findViewById(R.id.item_quantity);
        itemtotal = findViewById(R.id.total_item_price);
        name = findViewById(R.id.apa_name);
        mail = findViewById(R.id.apa_email);
        phone = findViewById(R.id.apa_phone);
        addr = findViewById(R.id.apa_add);
        confirmbtn = findViewById(R.id.confirmorder_btn);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
        currentTime = mdformat.format(calendar.getTime());
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        DateToday = day + "-" + (month + 1) + "-" + year;

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait Completing your order....");
        

        CollectionReference cref = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("my product");
        cref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        if (document.getString("type").equals("cart")){
                            long price = document.getLong("price");
                            long qua = document.getLong("quantity");
                            long tot = price*qua;
                            total = total+tot;
                            totalquantity = totalquantity+qua;
                            Log.d("TAG", "onCreate: "+total+" "+totalquantity);
                            itemqau.setText(String.valueOf(totalquantity));
                            itemtotal.setText(String.valueOf(total));
                            String id = document.getString("pid");
                            if (id != null){
                                idList.add(id);
                            }
                        }
                    }
                    Log.d("TAG", "onComplete: "+idList);
                }
            }
        });


        pay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()){
                    cod.setChecked(false);
                }
            }
        });
        cod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()){
                    pay.setChecked(false);
                }
            }
        });

        confirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmorder();
            }
        });
    }

    private void confirmorder() {
        String nn = name.getText().toString();
        String ee = mail.getText().toString();
        String pp = phone.getText().toString();
        address = addr.getText().toString();
        String pri = itemtotal.getText().toString();
        String it = itemqau.getText().toString();


        if (nn.isEmpty()){
            name.setError("Name required");
            return;
        }
        if (ee.isEmpty()){
            mail.setError("Email Required");
            return;
        }
        if (pp.isEmpty()){
            phone.setError("Phone number Required");
            return;
        }
        if (address.isEmpty()){
            addr.setError("Address Required");
            return;
        }

        if (pay.isChecked()){
            Checkout checkout = new Checkout();
            checkout.setKeyID("rzp_live_wIY4PpTNjVURtd");
            checkout.setImage(R.drawable.razorlogo);
            JSONObject object = new JSONObject();
            try {
                object.put("name",nn);
                object.put("description"," Payment for Your order");
                object.put("theme.color","#0093DD");
                object.put("currency","INR");
                int amount = Math.round(Float.parseFloat(pri) * 100);
                object.put("amount",amount);
                object.put("prefill.contact",pp);
                object.put("prefill.email",ee);
                checkout.open(ConfirmOrderPage.this,object);

            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        if (cod.isChecked()){
            progressDialog.show();
            DocumentReference dref = FirebaseFirestore.getInstance().collection("order").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            Map<String,Object> codmap = new HashMap<>();
            codmap.put("name",nn);
            codmap.put("id",FirebaseAuth.getInstance().getCurrentUser().getUid());
            codmap.put("status","ordered");
            codmap.put("priority",priority);
            dref.set(codmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    for (i = 0; i < idList.size() ;i++){
                        DocumentReference dref = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .collection("my product").document(idList.get(i));
                        Log.d("TAG", "onCreate: "+idList.get(i));
                        dref.update("type","ordered","payment","cod","date",DateToday,"time",currentTime,"status","ordered","address",address,"priority",priority);
                    }
                    if (progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    Toast.makeText(ConfirmOrderPage.this, "Order Successfully placed..", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ConfirmOrderPage.this,MainActivity.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    if (progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    Toast.makeText(ConfirmOrderPage.this, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this,"Your final Order has been placed successfully and payment id is: "+s,Toast.LENGTH_SHORT).show();
        orderid = s;
        ordersuccess();
    }

    private void ordersuccess() {
        progressDialog.show();
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){
                    username = task.getResult().getString("name");
                    DocumentReference dref = FirebaseFirestore.getInstance().collection("order").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    Map<String,Object> codmap = new HashMap<>();
                    codmap.put("name",username);
                    codmap.put("id",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    codmap.put("status","ordered");
                    codmap.put("priority",priority);
                    dref.set(codmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            for (i = 0; i < idList.size() ;i++){
                                DocumentReference dref = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .collection("my product").document(idList.get(i));
                                Log.d("TAG", "onCreate: "+idList.get(i));
                                dref.update("type","ordered","payment","Online","date",DateToday,"time",currentTime,"status","ordered","payment_id",orderid,"address",address,"priority",priority);
                            }
                            if (progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                            Toast.makeText(ConfirmOrderPage.this, "Order Successfully placed..", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ConfirmOrderPage.this,MainActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            if (progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                            Toast.makeText(ConfirmOrderPage.this, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){
                    String na = task.getResult().getString("name");
                    String em = task.getResult().getString("email");
                    String ph = task.getResult().getString("phone");
                    String add = task.getResult().getString("address");
                    if (na != null){
                        name.setText(na);
                    }
                    if (em != null){
                        mail.setText(em);
                    }
                    if (ph != null){
                        phone.setText(ph);
                    }
                    if (add != null){
                        addr.setText(add);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(ConfirmOrderPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}