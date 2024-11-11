package com.yolanda.uasyolan.listener;

import com.yolanda.uasyolan.model.CartModel;
import com.yolanda.uasyolan.model.DressModel;

import java.util.List;

public interface ICartLoadListener {
    void onCartLoadSuccess(List<CartModel> cartModelList);
    void onCartLoadFailed(String message);
}
