package com.yolanda.uasyolan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yolanda.uasyolan.adapter.MyCartAdapter;
import com.yolanda.uasyolan.eventbus.MyUpdateCartEvent;
import com.yolanda.uasyolan.listener.ICartLoadListener;
import com.yolanda.uasyolan.model.CartModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartActivity extends AppCompatActivity implements ICartLoadListener {

    @BindView(R.id.recycler_cart)
    RecyclerView recyclerCart;
    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;
    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.txtTotal)
    TextView txtTotal;
    @BindView(R.id.btnNext)
    Button btnNext;
    ICartLoadListener cartLoadListener;
    @Override
    protected void onStop() {
        if (EventBus.getDefault().hasSubscriberForEvent(MyUpdateCartEvent.class))
            EventBus.getDefault().removeStickyEvent(MyUpdateCartEvent.class);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onUpdateCart (MyUpdateCartEvent event)
    {
        loadCartFromFirebase();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        init();
        loadCartFromFirebase();
    }

    private void loadCartFromFirebase() {
        List<CartModel> cartModels= new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child("UNIQUE_USER_ID")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {
                            for (DataSnapshot cartSnapshot:snapshot.getChildren())
                            {
                                CartModel cartModel=cartSnapshot.getValue(CartModel.class);
                                cartModel.setKey(cartSnapshot.getKey());
                                cartModels.add(cartModel);
                            }
                            cartLoadListener.onCartLoadSuccess(cartModels);
                        }
                        else
                            cartLoadListener.onCartLoadFailed("Cart empty");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cartLoadListener.onCartLoadFailed(error.getMessage());
                    }
                });
    }

    private void init(){
        ButterKnife.bind(this);

        cartLoadListener = this;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerCart.setLayoutManager(layoutManager);
        recyclerCart.addItemDecoration(new DividerItemDecoration(this,layoutManager.getOrientation()));

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(CartActivity.this,DetailPembelian.class);
                startActivity(next);
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    public void onCartLoadSuccess(List<CartModel> cartModelList) {
        double sum = 0;
        for (CartModel cartModel: cartModelList)
        {
            sum+=cartModel.getTotalPrice();
        }
        txtTotal.setText(new StringBuilder("Rp").append(sum));
        MyCartAdapter adapter = new MyCartAdapter(this,cartModelList);
        recyclerCart.setAdapter(adapter);

    }

    @Override
    public void onCartLoadFailed(String message) {
        Snackbar.make(mainLayout,message,Snackbar.LENGTH_LONG).show();
    }
}