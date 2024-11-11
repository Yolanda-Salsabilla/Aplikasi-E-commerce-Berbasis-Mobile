package com.yolanda.uasyolan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yolanda.uasyolan.R;
import com.yolanda.uasyolan.eventbus.MyUpdateCartEvent;
import com.yolanda.uasyolan.listener.ICartLoadListener;
import com.yolanda.uasyolan.listener.IRecyclerViewClickListener;
import com.yolanda.uasyolan.model.CartModel;
import com.yolanda.uasyolan.model.DressModel;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyDressAdapter extends RecyclerView.Adapter<MyDressAdapter.MyDressViewHolder> {

    private Context context;
    private List<DressModel> dressModelList;
    private ICartLoadListener iCartLoadListener;

    public MyDressAdapter(Context context, List<DressModel> dressModelList, ICartLoadListener iCartLoadListener) {
        this.context = context;
        this.dressModelList = dressModelList;
        this.iCartLoadListener = iCartLoadListener;
    }

    @NonNull
    @Override
    public MyDressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyDressViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_dress_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyDressViewHolder holder, int position) {
        Glide.with(context)
                .load(dressModelList.get(position).getImage())
                .into(holder.imageView);
        holder.txtprice.setText(new StringBuilder("Rp").append(dressModelList.get(position).getPrice()));
        holder.txtName.setText(new StringBuilder().append(dressModelList.get(position).getName()));

        holder.setListener((view, adapterPosition) -> {
                addToCart(dressModelList.get(position));
        });
    }

    private void  addToCart(DressModel dressModel) {
        DatabaseReference userCart = FirebaseDatabase
                .getInstance()
                .getReference("Cart")
                .child("UNIQUE_USER_ID");

        userCart.child(dressModel.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())//if user already have item ini cart
                        {
                            //Just update quantity and totalPrice
                            CartModel cartModel = snapshot.getValue(CartModel.class);
                            cartModel.setQuantity(cartModel.getQuantity()+1);
                            Map<String,Object> updateData = new HashMap<>();
                            updateData.put("quantity",cartModel.getQuantity());
                            updateData.put("totalPrice",cartModel.getQuantity()*Float.parseFloat(cartModel.getPrice()));

                            userCart.child(dressModel.getKey())
                                    .updateChildren(updateData)
                                    .addOnSuccessListener(aVoid -> {
                                        iCartLoadListener.onCartLoadFailed("Add To Cart Success");
                                    })
                                    .addOnFailureListener(e -> iCartLoadListener.onCartLoadFailed(e.getMessage()));
                        }
                        else //If item not have in cart, add new
                        {
                            CartModel cartModel= new CartModel();
                            cartModel.setName(dressModel.getName());
                            cartModel.setImage(dressModel.getImage());
                            cartModel.setKey(dressModel.getKey());
                            cartModel.setPrice(dressModel.getPrice());
                            cartModel.setQuantity(1);
                            cartModel.setTotalPrice(Float.parseFloat(dressModel.getPrice()));

                            userCart.child(dressModel.getKey())
                                    .setValue(cartModel)
                                    .addOnSuccessListener(aVoid -> {
                                        iCartLoadListener.onCartLoadFailed("Add To Cart Success");
                                    })
                                    .addOnFailureListener(e -> iCartLoadListener.onCartLoadFailed(e.getMessage()));
                        }
                        EventBus.getDefault().postSticky(new MyUpdateCartEvent());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        iCartLoadListener.onCartLoadFailed(error.getMessage());
                    }
                });
    }


    @Override
    public int getItemCount() {
        return dressModelList.size();
    }

    public class MyDressViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.txtName)
        TextView txtName;
        @BindView(R.id.txtprice)
        TextView txtprice;

        IRecyclerViewClickListener listener;

        public void setListener(IRecyclerViewClickListener listener) {
            this.listener = listener;
        }

        private Unbinder unbinder;
        public MyDressViewHolder(@NonNull View itemView ){
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onRecyclerClick(v,getAdapterPosition());
        }
    }
}
