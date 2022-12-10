package org.ganeshatech05102021.ecommerce;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import org.ganeshatech05102021.ecommerce.adminactions.ManageProducts;
import org.ganeshatech05102021.ecommerce.adminactions.addCategory;
import org.ganeshatech05102021.ecommerce.adminactions.addDeals;
import org.ganeshatech05102021.ecommerce.adminactions.addProduct;
import org.ganeshatech05102021.ecommerce.adminactions.completedOrders;
import org.ganeshatech05102021.ecommerce.adminactions.receivedOrder;
import org.ganeshatech05102021.ecommerce.adminactions.returnRequest;
import org.ganeshatech05102021.ecommerce.adminactions.settingPage;

public class AdminActivity extends AppCompatActivity {
    private Button signout;
    private CardView orders,returns,addproduct,addcategory,adddeals,completed,manage,settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        signout = findViewById(R.id.logout_admin);

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(AdminActivity.this,LoginActivity.class));
                finish();
            }
        });

        orders = findViewById(R.id.orderreceived);
        returns = findViewById(R.id.returns);
        addproduct = findViewById(R.id.addproduct);
        addcategory = findViewById(R.id.addcategory);
        adddeals= findViewById(R.id.adddeals);
        completed = findViewById(R.id.addvaoucher);
        manage = findViewById(R.id.manageproductbtn);
        settings = findViewById(R.id.settings);
        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, receivedOrder.class));
            }
        });
        returns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, returnRequest.class));
            }
        });
        addproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, addProduct.class));
            }
        });
        addcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, addCategory.class));
            }
        });
        adddeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, addDeals.class));
            }
        });
        completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, completedOrders.class));
            }
        });
        manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, ManageProducts.class));
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, settingPage.class));
            }
        });
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Exit");
        builder.setMessage("Do you want Exit ? ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.show();
    }
}