package com.cashLoan.money.language;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.dzfd.gids.baselibs.utils.DensityUtils;

/**
 * Created by liruidong on 2019/4/30.
 */

public class LanguageDecoration extends RecyclerView.ItemDecoration{

    private int space;

    public LanguageDecoration() {
        space = DensityUtils.dip2px(15f);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;

    }

}
