package com.yolanda.uasyolan.listener;

import com.yolanda.uasyolan.model.DressModel;

import java.util.List;

public interface IDressLoadListener {
    void onDressLoadSuccess(List<DressModel> dressModelList);
    void onDressLoadFailed(String message);
}
