package org.ganeshatech05102021.ecommerce.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.ganeshatech05102021.ecommerce.HelpPage;
import org.ganeshatech05102021.ecommerce.LoginActivity;
import org.ganeshatech05102021.ecommerce.R;
import org.ganeshatech05102021.ecommerce.aboutCompany;
import org.ganeshatech05102021.ecommerce.orderspage;
import org.ganeshatech05102021.ecommerce.userInfo;
import org.ganeshatech05102021.ecommerce.usersetting;
import org.ganeshatech05102021.ecommerce.voucherPage;
import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class profileFragment extends Fragment {

    private CircleImageView profileimg;
    private ImageButton pickimg,setting;
    private TextView name;
    private CardView personalinfo,myvoucher,orders,aboutus,help,logout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileimg = view.findViewById(R.id.userimg);
        setting = view.findViewById(R.id.settingbtn);
        name = view.findViewById(R.id.username);
        personalinfo = view.findViewById(R.id.personalinfobtn);
        myvoucher = view.findViewById(R.id.voucherbtn);
        orders = view.findViewById(R.id.ordersbtn);
        aboutus = view.findViewById(R.id.aboutbtn);
        help = view.findViewById(R.id.helpbtn);
        logout = view.findViewById(R.id.logoutbutton);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), usersetting.class));
            }
        });
        personalinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), userInfo.class));
            }
        });
        myvoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), voucherPage.class));
            }
        });
        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), orderspage.class));
            }
        });
        aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), aboutCompany.class));
            }
        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), HelpPage.class));
            }
        });

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){
                    String rname = task.getResult().getString("name");
                    String Url = task.getResult().getString("url");

                    if (Url != null){
                        Picasso.get().load(Url).into(profileimg);
                    }
                    name.setText(rname);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}