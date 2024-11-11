package com.yolanda.uasyolan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Space;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;
import com.yolanda.uasyolan.adapter.MyDressAdapter;
import com.yolanda.uasyolan.eventbus.MyUpdateCartEvent;
import com.yolanda.uasyolan.listener.ICartLoadListener;
import com.yolanda.uasyolan.listener.IDressLoadListener;
import com.yolanda.uasyolan.model.CartModel;
import com.yolanda.uasyolan.model.DressModel;
import com.yolanda.uasyolan.utils.SpaceItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements IDressLoadListener, ICartLoadListener {
    @BindView(R.id.recycler_dress)
    RecyclerView recycler_dress;
    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;
    @BindView(R.id.badge)
    NotificationBadge badge;
    @BindView(R.id.btnCart)
    ImageView btnCart;

    IDressLoadListener dressLoadListener;
    ICartLoadListener cartLoadListener;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

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
        countCartItem();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        loadDressFromFirebase();
        countCartItem();
    }

    private void loadDressFromFirebase() {
        List<DressModel> dressModels=new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference("Dress")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {
                            for (DataSnapshot dressSnapshot:snapshot.getChildren())
                            {
                                DressModel dressModel=dressSnapshot.getValue(DressModel.class);
                                dressModel.setKey(dressSnapshot.getKey());
                                dressModels.add(dressModel);
                            }
                            dressLoadListener.onDressLoadSuccess(dressModels);
                        }
                        else
                            dressLoadListener.onDressLoadFailed("Can't find Dress");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        dressLoadListener.onDressLoadFailed(error.getMessage());
                    }
                });
    }

    private  void init(){
        ButterKnife.bind(this);

        dressLoadListener = this;
        cartLoadListener = this;

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        recycler_dress.setLayoutManager(gridLayoutManager);
        recycler_dress.addItemDecoration(new SpaceItemDecoration());

        btnCart.setOnClickListener(v -> startActivity(new Intent(this,CartActivity.class)));
    }

    @Override
    public void onDressLoadSuccess(List<DressModel> dressModelList) {
        MyDressAdapter adapter= new MyDressAdapter(this,dressModelList,cartLoadListener);
        recycler_dress.setAdapter(adapter);
    }

    @Override
    public void onDressLoadFailed(String message) {
        Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onCartLoadSuccess(List<CartModel> cartModelList) {
        int cartSum = 0;
        for(CartModel cartModel : cartModelList )
            cartSum += cartModel.getQuantity();
        badge.setNumber(cartSum);
    }

    @Override
    public void onCartLoadFailed(String message) {
        Snackbar.make(mainLayout,message,Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        countCartItem();
    }

    private void countCartItem() {
        List<CartModel> cartModels = new ArrayList<>();
        FirebaseDatabase
                .getInstance().getReference("Cart")
                .child("UNIQUE_USER_ID")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot cartSnapshot : snapshot.getChildren())
                        {
                            CartModel cartModel = cartSnapshot.getValue(CartModel.class);
                            cartModel.setKey(cartSnapshot.getKey());
                            cartModels.add(cartModel);
                        }
                        cartLoadListener.onCartLoadSuccess(cartModels);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cartLoadListener.onCartLoadFailed(error.getMessage());
                    }
                });
    }
}