package com.wahyu.consumerapp.adapter;

import android.view.View;

/**
 * Created by wahyu_septiadi on 03, July 2020.
 * Visit My GitHub --> https://github.com/WahyuSeptiadi
 */

class CustomOnItemClickListener implements View.OnClickListener {

    private final int position;
    private final OnItemClickCallback onItemClickCallback;
    public CustomOnItemClickListener(int position, OnItemClickCallback onItemClickCallback) {
        this.position = position;
        this.onItemClickCallback = onItemClickCallback;
    }

    @Override
    public void onClick(View view) {
        onItemClickCallback.onItemClicked(view, position);
    }

    public interface OnItemClickCallback {
        void onItemClicked(View view, int position);
    }
}
