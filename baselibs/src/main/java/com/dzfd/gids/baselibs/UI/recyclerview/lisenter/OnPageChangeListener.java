package com.dzfd.gids.baselibs.UI.recyclerview.lisenter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by zheng on 2019/8/24.
 */

public interface OnPageChangeListener {
    void onScrollStateChanged(RecyclerView recyclerView, int newState);

    void onScrolled(RecyclerView recyclerView, int dx, int dy);

    void onPageSelected(int position, View view);
}
