package com.wahyu.githubapi.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by wahyu_septiadi on 07, July 2020.
 * Visit My GitHub --> https://github.com/WahyuSeptiadi
 */

public class StackWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext());
    }
}
