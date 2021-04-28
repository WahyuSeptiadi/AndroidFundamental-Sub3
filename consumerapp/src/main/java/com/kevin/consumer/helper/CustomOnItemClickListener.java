package com.kevin.consumer.helper;

import android.view.View;

public class CustomOnItemClickListener implements View.OnClickListener {
    private final OnItemClickCallback onItemClickCallback;

    public CustomOnItemClickListener(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    @Override
    public void onClick(View view) {
        onItemClickCallback.onItemClicked(view);
    }

    public interface OnItemClickCallback {
        void onItemClicked(View view);
    }
}
